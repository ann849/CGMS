package com.cgms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignmentRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private LocalDate dueDate;
}
