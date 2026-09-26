package com.cgms.controller;

import com.cgms.dto.*;
import com.cgms.model.Availability;
import com.cgms.model.StudentProfile;
import com.cgms.service.StudentService;
import com.cgms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
public class StudentController {

    private final StudentService studentService;
    private final UserService userService;

    public StudentController(StudentService studentService, UserService userService) {
        this.studentService = studentService;
        this.userService = userService;
    }

    // 1. GET /api/student/counsellors — list all counsellors with profile info
    @GetMapping("/counsellors")
    public ResponseEntity<ApiResponse<List<CounsellorDto>>> getAllCounsellors() {
        List<CounsellorDto> counsellors = studentService.getAllCounsellors();
        return ResponseEntity.ok(ApiResponse.success("Counsellors retrieved successfully", counsellors));
    }

    // 2. GET /api/student/counsellors/{id}/availability — view available slots for a counsellor
    @GetMapping("/counsellors/{id}/availability")
    public ResponseEntity<ApiResponse<List<Availability>>> getCounsellorAvailability(@PathVariable Long id) {
        List<Availability> availability = studentService.getCounsellorAvailability(id);
        return ResponseEntity.ok(ApiResponse.success("Counsellor available slots retrieved", availability));
    }

    // 3. POST /api/student/appointments — book an appointment for a specific slot
    @PostMapping("/appointments")
    public ResponseEntity<ApiResponse<AppointmentDto>> bookAppointment(
            Authentication authentication,
            @Valid @RequestBody AppointmentRequest appointmentRequest) {
        AppointmentDto appointment = studentService.bookAppointment(authentication.getName(), appointmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked successfully", appointment));
    }

    // 4. GET /api/student/appointments — view own appointment history with status
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getStudentAppointments(Authentication authentication) {
        List<AppointmentDto> appointments = studentService.getStudentAppointments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Appointments history retrieved", appointments));
    }

    // 5. POST /api/student/payments — make payment for an appointment
    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<PaymentDto>> processPayment(
            Authentication authentication,
            @Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentDto payment = studentService.processPayment(authentication.getName(), paymentRequest);
        return ResponseEntity.ok(ApiResponse.success("Payment completed successfully", payment));
    }

    // 6. GET /api/student/tasks — view tasks assigned by counsellor
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getStudentTasks(Authentication authentication) {
        List<TaskDto> tasks = studentService.getStudentTasks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student tasks retrieved", tasks));
    }

    // 7. POST /api/student/tasks/{id}/submit — submit a task
    @PostMapping("/tasks/{id}/submit")
    public ResponseEntity<ApiResponse<SubmissionDto>> submitTask(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody TaskSubmissionRequest submissionRequest) {
        SubmissionDto submission = studentService.submitTask(authentication.getName(), id, submissionRequest);
        return ResponseEntity.ok(ApiResponse.success("Task submitted successfully", submission));
    }

    // 8. GET /api/student/results — view evaluated submissions with marks/remarks
    @GetMapping("/results")
    public ResponseEntity<ApiResponse<List<SubmissionDto>>> getStudentResults(Authentication authentication) {
        List<SubmissionDto> results = studentService.getStudentResults(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Evaluated results retrieved", results));
    }

    // Profile Endpoints
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getStudentProfile(Authentication authentication) {
        UserDto profile = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student profile retrieved successfully", profile));
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<ApiResponse<StudentProfile>> updateStudentProfile(
            @PathVariable Long userId,
            @RequestBody StudentProfile studentProfile) {
        StudentProfile updated = studentService.updateStudentProfile(userId, studentProfile);
        return ResponseEntity.ok(ApiResponse.success("Student profile updated successfully", updated));
    }
}
