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
import java.util.stream.Collectors;

@Service
public class CounsellorService {

    private final CounsellorProfileRepository counsellorProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final AppointmentRepository appointmentRepository;
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;

    public CounsellorService(CounsellorProfileRepository counsellorProfileRepository,
                             StudentProfileRepository studentProfileRepository,
                             UserRepository userRepository,
                             AvailabilityRepository availabilityRepository,
                             AppointmentRepository appointmentRepository,
                             TaskRepository taskRepository,
                             SubmissionRepository submissionRepository) {
        this.counsellorProfileRepository = counsellorProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.appointmentRepository = appointmentRepository;
        this.taskRepository = taskRepository;
        this.submissionRepository = submissionRepository;
    }

    public List<CounsellorProfile> getAllCounsellors() {
        return counsellorProfileRepository.findAll();
    }

    // 1. GET & PUT own profile
    public CounsellorDto getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        CounsellorProfile profile = counsellorProfileRepository.findByUser(user).orElse(null);

        return CounsellorDto.builder()
                .id(user.getId())
                .profileId(profile != null ? profile.getId() : null)
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .qualification(profile != null ? profile.getQualification() : "")
                .specialization(profile != null ? profile.getSpecialization() : "")
                .experience(profile != null ? profile.getExperience() : 0)
                .bio(profile != null ? profile.getBio() : "")
                .profilePhoto(profile != null ? profile.getProfilePhoto() : null)
                .build();
    }

    @Transactional
    public CounsellorDto updateProfile(String email, CounsellorDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        userRepository.save(user);

        CounsellorProfile profile = counsellorProfileRepository.findByUser(user)
                .orElseGet(() -> CounsellorProfile.builder().user(user).build());

        if (dto.getQualification() != null) profile.setQualification(dto.getQualification());
        if (dto.getSpecialization() != null) profile.setSpecialization(dto.getSpecialization());
        if (dto.getExperience() != null) profile.setExperience(dto.getExperience());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getProfilePhoto() != null) profile.setProfilePhoto(dto.getProfilePhoto());

        counsellorProfileRepository.save(profile);

        return getProfileByEmail(email);
    }

    // 2. Add availability slot
    @Transactional
    public Availability addAvailability(String counsellorEmail, AvailabilityRequest request) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        String dayOfWeek = request.getDayOfWeek();
        if (dayOfWeek == null && request.getDate() != null) {
            dayOfWeek = request.getDate().getDayOfWeek().name();
        }

        Availability availability = Availability.builder()
                .counsellor(counsellor)
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .dayOfWeek(dayOfWeek)
                .isBooked(false)
                .build();

        return availabilityRepository.save(availability);
    }

    // 3. View own slots
    public List<Availability> getCounsellorAvailabilities(String counsellorEmail) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));
        return availabilityRepository.findByCounsellorId(counsellor.getId());
    }

    // 4. Delete an unbooked slot
    @Transactional
    public void deleteAvailabilitySlot(String counsellorEmail, Long slotId) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        Availability slot = availabilityRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability", "id", slotId));

        if (!slot.getCounsellor().getId().equals(counsellor.getId())) {
            throw new BadRequestException("Slot does not belong to you.");
        }

        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new BadRequestException("Cannot delete an availability slot that has already been booked by a student.");
        }

        availabilityRepository.delete(slot);
    }

    // 5. View appointments booked with this counsellor
    public List<AppointmentDto> getCounsellorAppointments(String counsellorEmail) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        List<Appointment> list = appointmentRepository.findByCounsellorId(counsellor.getId());
        return list.stream().map(this::mapToAppointmentDto).collect(Collectors.toList());
    }

    // 6. Update appointment status
    @Transactional
    public AppointmentDto updateAppointmentStatus(String counsellorEmail, Long appointmentId, AppointmentStatus newStatus) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", appointmentId));

        if (!appointment.getCounsellor().getId().equals(counsellor.getId())) {
            throw new BadRequestException("Appointment is not assigned to you.");
        }

        appointment.setStatus(newStatus);
        Appointment saved = appointmentRepository.save(appointment);
        return mapToAppointmentDto(saved);
    }

    // 7. Assign a task to a specific student
    @Transactional
    public TaskDto assignTask(String counsellorEmail, TaskAssignmentRequest request) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student User", "id", request.getStudentId()));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("Selected user is not a Student.");
        }

        Task task = Task.builder()
                .counsellor(counsellor)
                .student(student)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .status(TaskStatus.ASSIGNED)
                .build();

        Task saved = taskRepository.save(task);
        return mapToTaskDto(saved);
    }

    // 8. View tasks assigned by this counsellor
    public List<TaskDto> getAssignedTasks(String counsellorEmail) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        List<Task> tasks = taskRepository.findByCounsellorId(counsellor.getId());
        return tasks.stream().map(this::mapToTaskDto).collect(Collectors.toList());
    }

    // 9. View student submissions
    public List<SubmissionDto> getStudentSubmissions(String counsellorEmail) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        List<Task> tasks = taskRepository.findByCounsellorId(counsellor.getId());
        List<Long> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

        List<Submission> submissions = submissionRepository.findAll().stream()
                .filter(s -> taskIds.contains(s.getTask().getId()))
                .collect(Collectors.toList());

        return submissions.stream().map(this::mapToSubmissionDto).collect(Collectors.toList());
    }

    // 10. Evaluate submission
    @Transactional
    public SubmissionDto evaluateSubmission(String counsellorEmail, Long submissionId, EvaluationRequest request) {
        User counsellor = userRepository.findByEmail(counsellorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", counsellorEmail));

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission", "id", submissionId));

        Task task = submission.getTask();
        if (!task.getCounsellor().getId().equals(counsellor.getId())) {
            throw new BadRequestException("Submission belongs to a task assigned by another counsellor.");
        }

        submission.setMarksOrGrade(request.getMarksOrGrade());
        submission.setRemarks(request.getRemarks());
        submission.setEvaluatedAt(LocalDateTime.now());

        Submission saved = submissionRepository.save(submission);

        task.setStatus(TaskStatus.EVALUATED);
        taskRepository.save(task);

        return mapToSubmissionDto(saved);
    }

    // 11. List all students for dropdown selection
    public List<StudentDto> getAllStudents() {
        List<User> students = userRepository.findByRole(Role.STUDENT);
        return students.stream().map(u -> {
            StudentProfile profile = studentProfileRepository.findByUser(u).orElse(null);
            return StudentDto.builder()
                    .id(u.getId())
                    .profileId(profile != null ? profile.getId() : null)
                    .name(u.getName())
                    .email(u.getEmail())
                    .phone(u.getPhone())
                    .education(profile != null ? profile.getEducation() : "")
                    .interests(profile != null ? profile.getInterests() : "")
                    .build();
        }).collect(Collectors.toList());
    }

    // Helper Mappers
    private AppointmentDto mapToAppointmentDto(Appointment appt) {
        Availability slot = appt.getAvailabilitySlot();
        return AppointmentDto.builder()
                .id(appt.getId())
                .counsellorId(appt.getCounsellor().getId())
                .counsellorName(appt.getCounsellor().getName())
                .counsellorSpecialization(
                        counsellorProfileRepository.findByUser(appt.getCounsellor())
                                .map(CounsellorProfile::getSpecialization)
                                .orElse("Career Guidance")
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
