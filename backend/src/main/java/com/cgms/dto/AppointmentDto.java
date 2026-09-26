package com.cgms.dto;

import com.cgms.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDto {
    private Long id;
    private Long counsellorId;
    private String counsellorName;
    private String counsellorSpecialization;
    private Long studentId;
    private String studentName;
    private Long availabilitySlotId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime appointmentDate;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
}
