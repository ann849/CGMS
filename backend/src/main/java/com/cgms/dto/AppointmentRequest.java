package com.cgms.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @NotNull(message = "Counsellor ID is required")
    private Long counsellorId;

    @NotNull(message = "Availability slot ID is required")
    private Long availabilitySlotId;
}
