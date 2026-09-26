package com.cgms.controller;

import com.cgms.dto.*;
import com.cgms.model.Availability;
import com.cgms.model.CounsellorProfile;
import com.cgms.service.CounsellorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/counsellor")
@PreAuthorize("hasRole('COUNSELLOR') or hasRole('ADMIN')")
public class CounsellorController {

    private final CounsellorService counsellorService;

    public CounsellorController(CounsellorService counsellorService) {
        this.counsellorService = counsellorService;
    }

    // Public endpoint for listing counsellors
    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<CounsellorProfile>>> getAllCounsellors() {
        List<CounsellorProfile> counsellors = counsellorService.getAllCounsellors();
        return ResponseEntity.ok(ApiResponse.success("Counsellors list retrieved", counsellors));
    }

    // 1. GET & PUT /api/counsellor/profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CounsellorDto>> getCounsellorProfile(Authentication authentication) {
        CounsellorDto profile = counsellorService.getProfileByEmail(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Counsellor profile retrieved successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CounsellorDto>> updateCounsellorProfile(
            Authentication authentication,
            @RequestBody CounsellorDto counsellorDto) {
        CounsellorDto updated = counsellorService.updateProfile(authentication.getName(), counsellorDto);
        return ResponseEntity.ok(ApiResponse.success("Counsellor profile updated successfully", updated));
    }

    // 2. POST /api/counsellor/availability — add available time slots
    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<Availability>> addAvailability(
            Authentication authentication,
            @Valid @RequestBody AvailabilityRequest request) {
        Availability slot = counsellorService.addAvailability(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Availability slot added successfully", slot));
    }

    // 3. GET /api/counsellor/availability — view own slots
    @GetMapping("/availability")
    public ResponseEntity<ApiResponse<List<Availability>>> getCounsellorAvailabilities(Authentication authentication) {
        List<Availability> slots = counsellorService.getCounsellorAvailabilities(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Own availability slots retrieved", slots));
    }

    // 4. DELETE /api/counsellor/availability/{id} — remove an unbooked slot
    @DeleteMapping("/availability/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAvailabilitySlot(
            Authentication authentication,
            @PathVariable Long id) {
        counsellorService.deleteAvailabilitySlot(authentication.getName(), id);
        return ResponseEntity.ok(ApiResponse.success("Availability slot deleted successfully"));
    }

    // 5. GET /api/counsellor/appointments — view appointments booked with this counsellor
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<List<AppointmentDto>>> getCounsellorAppointments(Authentication authentication) {
        List<AppointmentDto> appointments = counsellorService.getCounsellorAppointments(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Booked appointments retrieved", appointments));
    }

    // 6. PUT /api/counsellor/appointments/{id}/status — update status
    @PutMapping("/appointments/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentDto>> updateAppointmentStatus(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateRequest statusRequest) {
        AppointmentDto updated = counsellorService.updateAppointmentStatus(authentication.getName(), id, statusRequest.getStatus());
        return ResponseEntity.ok(ApiResponse.success("Appointment status updated to " + statusRequest.getStatus(), updated));
    }

    // 7. POST /api/counsellor/tasks — assign a task to a specific student
    @PostMapping("/tasks")
    public ResponseEntity<ApiResponse<TaskDto>> assignTask(
            Authentication authentication,
            @Valid @RequestBody TaskAssignmentRequest taskRequest) {
        TaskDto task = counsellorService.assignTask(authentication.getName(), taskRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task assigned successfully", task));
    }

    // 8. GET /api/counsellor/tasks — view tasks assigned
    @GetMapping("/tasks")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getAssignedTasks(Authentication authentication) {
        List<TaskDto> tasks = counsellorService.getAssignedTasks(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Assigned tasks retrieved", tasks));
    }

    // 9. GET /api/counsellor/submissions — view student submissions pending evaluation
    @GetMapping("/submissions")
    public ResponseEntity<ApiResponse<List<SubmissionDto>>> getStudentSubmissions(Authentication authentication) {
        List<SubmissionDto> submissions = counsellorService.getStudentSubmissions(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Student submissions retrieved", submissions));
    }

    // 10. PUT /api/counsellor/submissions/{id}/evaluate — add marks/grade and remarks
    @PutMapping("/submissions/{id}/evaluate")
    public ResponseEntity<ApiResponse<SubmissionDto>> evaluateSubmission(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody EvaluationRequest evaluationRequest) {
        SubmissionDto evaluated = counsellorService.evaluateSubmission(authentication.getName(), id, evaluationRequest);
        return ResponseEntity.ok(ApiResponse.success("Submission evaluated successfully", evaluated));
    }

    // 11. GET /api/counsellor/students — list all registered students for dropdown
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents() {
        List<StudentDto> students = counsellorService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success("Student list retrieved", students));
    }
}
