package com.cgms.controller;

import com.cgms.dto.*;
import com.cgms.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 1. GET /api/admin/students — list all students
    @GetMapping("/students")
    public ResponseEntity<ApiResponse<List<StudentDto>>> getAllStudents() {
        List<StudentDto> students = adminService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.success("All students retrieved successfully", students));
    }

    // 2. PUT /api/admin/students/{id}/status — activate/deactivate a student account
    @PutMapping("/students/{id}/status")
    public ResponseEntity<ApiResponse<StudentDto>> updateStudentStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest statusRequest) {
        StudentDto updated = adminService.updateStudentStatus(id, statusRequest.getIsActive());
        return ResponseEntity.ok(ApiResponse.success("Student active status updated to " + statusRequest.getIsActive(), updated));
    }

    // 3. POST /api/admin/counsellors — create a new counsellor account
    @PostMapping("/counsellors")
    public ResponseEntity<ApiResponse<CounsellorDto>> createCounsellor(
            @Valid @RequestBody CreateCounsellorRequest request) {
        CounsellorDto counsellor = adminService.createCounsellor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Counsellor account created successfully", counsellor));
    }

    // 4. GET /api/admin/counsellors — list all counsellors
    @GetMapping("/counsellors")
    public ResponseEntity<ApiResponse<List<CounsellorDto>>> getAllCounsellors() {
        List<CounsellorDto> counsellors = adminService.getAllCounsellors();
        return ResponseEntity.ok(ApiResponse.success("All counsellors retrieved successfully", counsellors));
    }

    // 5. PUT /api/admin/counsellors/{id}/status — activate/deactivate a counsellor
    @PutMapping("/counsellors/{id}/status")
    public ResponseEntity<ApiResponse<CounsellorDto>> updateCounsellorStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest statusRequest) {
        CounsellorDto updated = adminService.updateCounsellorStatus(id, statusRequest.getIsActive());
        return ResponseEntity.ok(ApiResponse.success("Counsellor active status updated to " + statusRequest.getIsActive(), updated));
    }

    // 6. GET /api/admin/payments — view all payment transactions with filters
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<List<PaymentDto>>> getAllPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PaymentDto> payments = adminService.getAllPayments(status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Payment transactions retrieved", payments));
    }

    // 7. GET /api/admin/reports/summary — dashboard stats
    @GetMapping("/reports/summary")
    public ResponseEntity<ApiResponse<AdminSummaryDto>> getSummaryReport() {
        AdminSummaryDto summary = adminService.getSummaryReport();
        return ResponseEntity.ok(ApiResponse.success("Summary report metrics retrieved", summary));
    }

    // 8. GET /api/admin/reports/export — export CSV report
    @GetMapping(value = "/reports/export", produces = "text/csv")
    public ResponseEntity<String> exportReport() {
        String csvData = adminService.exportAppointmentsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cgms_appointments_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }
}
