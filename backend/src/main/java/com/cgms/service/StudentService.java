package com.cgms.service;

import com.cgms.dto.*;
import com.cgms.exception.BadRequestException;
import com.cgms.exception.ResourceNotFoundException;
import com.cgms.model.*;
import com.cgms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final CounsellorProfileRepository counsellorProfileRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;

    public StudentService(StudentProfileRepository studentProfileRepository,
                          CounsellorProfileRepository counsellorProfileRepository,
                          UserRepository userRepository,
                          AvailabilityRepository availabilityRepository,
                          AppointmentRepository appointmentRepository,
                          PaymentRepository paymentRepository,
                          TaskRepository taskRepository,
                          SubmissionRepository submissionRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.counsellorProfileRepository = counsellorProfileRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.taskRepository = taskRepository;
        this.submissionRepository = submissionRepository;
    }

    public StudentProfile getStudentProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return studentProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "user_id", userId));
    }

    public StudentProfile updateStudentProfile(Long userId, StudentProfile updatedProfile) {
        StudentProfile existing = getStudentProfileByUserId(userId);
        if (updatedProfile.getEducation() != null) existing.setEducation(updatedProfile.getEducation());
        if (updatedProfile.getInterests() != null) existing.setInterests(updatedProfile.getInterests());
        if (updatedProfile.getDob() != null) existing.setDob(updatedProfile.getDob());
        return studentProfileRepository.save(existing);
    }

    // 1. List all counsellors with profile info
    public List<CounsellorDto> getAllCounsellors() {
        List<User> counsellors = userRepository.findByRole(Role.COUNSELLOR);
        return counsellors.stream().map(u -> {
            CounsellorProfile profile = counsellorProfileRepository.findByUser(u).orElse(null);
            return CounsellorDto.builder()
                    .id(u.getId())
                    .profileId(profile != null ? profile.getId() : null)
                    .name(u.getName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .qualification(profile != null ? profile.getQualification() : "N/A")
                    .specialization(profile != null ? profile.getSpecialization() : "General Guidance")
                    .experience(profile != null ? profile.getExperience() : 0)
                    .bio(profile != null ? profile.getBio() : "")
                    .profilePhoto(profile != null ? profile.getProfilePhoto() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    // 2. View available slots for a counsellor
    public List<Availability> getCounsellorAvailability(Long counsellorId) {
        if (!userRepository.existsById(counsellorId)) {
            throw new ResourceNotFoundException("Counsellor", "id", counsellorId);
        }
        return availabilityRepository.findByCounsellorIdAndIsBooked(counsellorId, false);
    }

    // 3. Book an appointment for a specific slot
    @Transactional
    public AppointmentDto bookAppointment(String studentEmail, AppointmentRequest request) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", studentEmail));

        Availability slot = availabilityRepository.findById(request.getAvailabilitySlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Availability", "id", request.getAvailabilitySlotId()));

        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new BadRequestException("The selected availability slot is already booked.");
        }

        User counsellor = slot.getCounsellor();
        if (!counsellor.getId().equals(request.getCounsellorId())) {
            throw new BadRequestException("Selected slot does not belong to the specified counsellor.");
        }

        // Mark slot as booked
        slot.setIsBooked(true);
        availabilityRepository.save(slot);

        LocalDateTime apptDateTime = slot.getDate() != null && slot.getStartTime() != null
                ? LocalDateTime.of(slot.getDate(), slot.getStartTime())
                : LocalDateTime.now();

        Appointment appointment = Appointment.builder()
                .student(student)
                .counsellor(counsellor)
                .availabilitySlot(slot)
                .appointmentDate(apptDateTime)
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        return mapToAppointmentDto(saved);
    }

    // 4. View student's own appointment history
    public List<AppointmentDto> getStudentAppointments(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", studentEmail));
        List<Appointment> list = appointmentRepository.findByStudentId(student.getId());
        return list.stream().map(this::mapToAppointmentDto).collect(Collectors.toList());
    }

    // 5. Make payment for an appointment
    @Transactional
    public PaymentDto processPayment(String studentEmail, PaymentRequest request) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", studentEmail));

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.getAppointmentId()));

        if (!appointment.getStudent().getId().equals(student.getId())) {
            throw new BadRequestException("Appointment does not belong to the current user.");
        }

        if (appointment.getStatus() == AppointmentStatus.CONFIRMED) {
            throw new BadRequestException("Appointment is already confirmed and paid for.");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .appointment(appointment)
                .student(student)
                .amount(request.getAmount())
                .status(PaymentStatus.SUCCESS)
                .transactionId(transactionId)
                .paymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "ONLINE")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update appointment status to CONFIRMED
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

        return PaymentDto.builder()
                .id(savedPayment.getId())
                .appointmentId(appointment.getId())
                .studentId(student.getId())
                .studentName(student.getName())
                .amount(savedPayment.getAmount())
                .status(savedPayment.getStatus())
                .transactionId(savedPayment.getTransactionId())
                .paymentMethod(savedPayment.getPaymentMethod())
                .paymentDate(savedPayment.getPaymentDate())
                .build();
    }

    // 6. View tasks assigned by counsellor
    public List<TaskDto> getStudentTasks(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", studentEmail));
        List<Task> tasks = taskRepository.findByStudentId(student.getId());
        return tasks.stream().map(this::mapToTaskDto).collect(Collectors.toList());
    }

    // 7. Submit a task
    @Transactional
    public SubmissionDto submitTask(String studentEmail, Long taskId, TaskSubmissionRequest request) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", studentEmail));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        if (!task.getStudent().getId().equals(student.getId())) {
            throw new BadRequestException("Task is not assigned to you.");
        }

        Submission submission = submissionRepository.findByTaskId(taskId).orElse(
                Submission.builder()
                        .task(task)
                        .student(student)
                        .build()
        );

        submission.setSubmissionText(request.getSubmissionText());
        submission.setSubmissionFile(request.getSubmissionFile());
        submission.setSubmittedAt(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);

        task.setStatus(TaskStatus.SUBMITTED);
        taskRepository.save(task);

        return mapToSubmissionDto(savedSubmission);
    }

    // 8. View evaluated submissions with marks/remarks
    public List<SubmissionDto> getStudentResults(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", studentEmail));

        List<Submission> submissions = submissionRepository.findByStudentId(student.getId());
        return submissions.stream()
                .filter(s -> s.getTask().getStatus() == TaskStatus.EVALUATED || s.getMarksOrGrade() != null)
                .map(this::mapToSubmissionDto)
                .collect(Collectors.toList());
    }

    // Helper Mapping Methods
    private AppointmentDto mapToAppointmentDto(Appointment appt) {
        Availability slot = appt.getAvailabilitySlot();
        return AppointmentDto.builder()
                .id(appt.getId())
                .counsellorId(appt.getCounsellor().getId())
                .counsellorName(appt.getCounsellor().getName())
                .counsellorSpecialization(
                        counsellorProfileRepository.findByUser(appt.getCounsellor())
                                .map(CounsellorProfile::getSpecialization)
                                .orElse("Career Counsellor")
                )
                .studentId(appt.getStudent().getId())
                .studentName(appt.getStudent().getName())
                .availabilitySlotId(slot != null ? slot.getId() : null)
                .slotDate(slot != null ? slot.getDate() : null)
                .startTime(slot != null ? slot.getStartTime() : null)
                .endTime(slot != null ? slot.getEndTime() : null)
                .appointmentDate(appt.getAppointmentDate())
                .status(appt.getStatus())
                .createdAt(appt.getCreatedAt())
                .build();
    }

    private TaskDto mapToTaskDto(Task task) {
        Submission sub = submissionRepository.findByTaskId(task.getId()).orElse(null);
        return TaskDto.builder()
                .id(task.getId())
                .counsellorId(task.getCounsellor().getId())
                .counsellorName(task.getCounsellor().getName())
                .studentId(task.getStudent().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .assignedDate(task.getAssignedDate())
                .status(task.getStatus())
                .submission(sub != null ? mapToSubmissionDto(sub) : null)
                .build();
    }

    private SubmissionDto mapToSubmissionDto(Submission sub) {
        return SubmissionDto.builder()
                .id(sub.getId())
                .taskId(sub.getTask().getId())
                .taskTitle(sub.getTask().getTitle())
                .counsellorId(sub.getTask().getCounsellor().getId())
                .counsellorName(sub.getTask().getCounsellor().getName())
                .submissionFile(sub.getSubmissionFile())
                .submissionText(sub.getSubmissionText())
                .submittedAt(sub.getSubmittedAt())
                .remarks(sub.getRemarks())
                .marksOrGrade(sub.getMarksOrGrade())
                .evaluatedAt(sub.getEvaluatedAt())
                .build();
    }
}
