package com.cgms.service;

import com.cgms.dto.*;
import com.cgms.exception.BadRequestException;
import com.cgms.exception.ResourceNotFoundException;
import com.cgms.model.*;
import com.cgms.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CounsellorProfileRepository counsellorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(UserRepository userRepository,
                        StudentProfileRepository studentProfileRepository,
                        CounsellorProfileRepository counsellorProfileRepository,
                        AppointmentRepository appointmentRepository,
                        PaymentRepository paymentRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.counsellorProfileRepository = counsellorProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 1. GET /api/admin/students — list all students
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
                    .isActive(u.getIsActive())
                    .build();
        }).collect(Collectors.toList());
    }

    // 2. PUT /api/admin/students/{id}/status — activate/deactivate student
    @Transactional
    public StudentDto updateStudentStatus(Long id, Boolean isActive) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student User", "id", id));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("User with ID " + id + " is not a Student.");
        }

        student.setIsActive(isActive);
        userRepository.save(student);

        StudentProfile profile = studentProfileRepository.findByUser(student).orElse(null);
        return StudentDto.builder()
                .id(student.getId())
                .profileId(profile != null ? profile.getId() : null)
                .name(student.getName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .education(profile != null ? profile.getEducation() : "")
                .interests(profile != null ? profile.getInterests() : "")
                .isActive(student.getIsActive())
                .build();
    }

    // 3. POST /api/admin/counsellors — create new counsellor account
    @Transactional
    public CounsellorDto createCounsellor(CreateCounsellorRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered in system!");
        }

        User counsellor = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.COUNSELLOR)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(counsellor);

        CounsellorProfile profile = CounsellorProfile.builder()
                .user(savedUser)
                .specialization(request.getSpecialization())
                .qualification(request.getQualification())
                .experience(request.getExperience() != null ? request.getExperience() : 0)
                .bio(request.getBio())
                .build();

        counsellorProfileRepository.save(profile);

        return CounsellorDto.builder()
                .id(savedUser.getId())
                .profileId(profile.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .qualification(profile.getQualification())
                .specialization(profile.getSpecialization())
                .experience(profile.getExperience())
                .bio(profile.getBio())
                .isActive(savedUser.getIsActive())
                .build();
    }

    // 4. GET /api/admin/counsellors — list all counsellors
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
                    .qualification(profile != null ? profile.getQualification() : "")
                    .specialization(profile != null ? profile.getSpecialization() : "")
                    .experience(profile != null ? profile.getExperience() : 0)
                    .bio(profile != null ? profile.getBio() : "")
                    .profilePhoto(profile != null ? profile.getProfilePhoto() : null)
                    .isActive(u.getIsActive())
                    .build();
        }).collect(Collectors.toList());
    }

    // 5. PUT /api/admin/counsellors/{id}/status — activate/deactivate counsellor
    @Transactional
    public CounsellorDto updateCounsellorStatus(Long id, Boolean isActive) {
        User counsellor = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counsellor User", "id", id));

        if (counsellor.getRole() != Role.COUNSELLOR) {
            throw new BadRequestException("User with ID " + id + " is not a Counsellor.");
        }

        counsellor.setIsActive(isActive);
        userRepository.save(counsellor);

        CounsellorProfile profile = counsellorProfileRepository.findByUser(counsellor).orElse(null);
        return CounsellorDto.builder()
                .id(counsellor.getId())
                .profileId(profile != null ? profile.getId() : null)
                .name(counsellor.getName())
                .email(counsellor.getEmail())
                .phone(counsellor.getPhone())
                .qualification(profile != null ? profile.getQualification() : "")
                .specialization(profile != null ? profile.getSpecialization() : "")
                .experience(profile != null ? profile.getExperience() : 0)
                .bio(profile != null ? profile.getBio() : "")
                .isActive(counsellor.getIsActive())
                .build();
    }

    // 6. GET /api/admin/payments — view payments with filters
    public List<PaymentDto> getAllPayments(String statusStr, LocalDate startDate, LocalDate endDate) {
        List<Payment> payments = paymentRepository.findAll();

        if (statusStr != null && !statusStr.isBlank()) {
            try {
                PaymentStatus status = PaymentStatus.valueOf(statusStr.toUpperCase());
                payments = payments.stream().filter(p -> p.getStatus() == status).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }

        if (startDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            payments = payments.stream()
                    .filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().isBefore(startDateTime))
                    .collect(Collectors.toList());
        }

        if (endDate != null) {
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            payments = payments.stream()
                    .filter(p -> p.getPaymentDate() != null && !p.getPaymentDate().isAfter(endDateTime))
                    .collect(Collectors.toList());
        }

        return payments.stream().map(p -> PaymentDto.builder()
                .id(p.getId())
                .appointmentId(p.getAppointment() != null ? p.getAppointment().getId() : null)
                .studentId(p.getStudent().getId())
                .studentName(p.getStudent().getName())
                .amount(p.getAmount())
                .status(p.getStatus())
                .transactionId(p.getTransactionId())
                .paymentMethod(p.getPaymentMethod())
                .paymentDate(p.getPaymentDate())
                .build()
        ).collect(Collectors.toList());
    }

    // 7. GET /api/admin/reports/summary — summary metrics
    public AdminSummaryDto getSummaryReport() {
        long totalStudents = userRepository.findByRole(Role.STUDENT).size();
        long totalCounsellors = userRepository.findByRole(Role.COUNSELLOR).size();
        long totalAppointments = appointmentRepository.count();

        List<Payment> successfulPayments = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = successfulPayments.stream()
                .map(Payment::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Appointment> appointments = appointmentRepository.findAll();
        Map<String, Long> statusCounts = new HashMap<>();
        for (AppointmentStatus s : AppointmentStatus.values()) {
            statusCounts.put(s.name(), 0L);
        }
        for (Appointment a : appointments) {
            if (a.getStatus() != null) {
                statusCounts.put(a.getStatus().name(), statusCounts.getOrDefault(a.getStatus().name(), 0L) + 1);
            }
        }

        return AdminSummaryDto.builder()
                .totalStudents(totalStudents)
                .totalCounsellors(totalCounsellors)
                .totalAppointments(totalAppointments)
                .totalRevenue(totalRevenue)
                .appointmentsByStatus(statusCounts)
                .build();
    }

    // 8. GET /api/admin/reports/export — export CSV string
    public String exportAppointmentsCsv() {
        List<Appointment> appointments = appointmentRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("Appointment_ID,Student_Name,Student_Email,Counsellor_Name,Date_Time,Status,Created_At\n");

        for (Appointment a : appointments) {
            csv.append(a.getId()).append(",")
                    .append(escapeCsv(a.getStudent() != null ? a.getStudent().getName() : "")).append(",")
                    .append(escapeCsv(a.getStudent() != null ? a.getStudent().getEmail() : "")).append(",")
                    .append(escapeCsv(a.getCounsellor() != null ? a.getCounsellor().getName() : "")).append(",")
                    .append(a.getAppointmentDate() != null ? a.getAppointmentDate().toString() : "").append(",")
                    .append(a.getStatus() != null ? a.getStatus().name() : "").append(",")
                    .append(a.getCreatedAt() != null ? a.getCreatedAt().toString() : "").append("\n");
        }

        return csv.toString();
    }

    private String escapeCsv(String data) {
        if (data == null) return "";
        String escaped = data.replaceAll("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
