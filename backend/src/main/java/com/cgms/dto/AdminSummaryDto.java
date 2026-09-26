package com.cgms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSummaryDto {
    private long totalStudents;
    private long totalCounsellors;
    private long totalAppointments;
    private BigDecimal totalRevenue;
    private Map<String, Long> appointmentsByStatus;
}
