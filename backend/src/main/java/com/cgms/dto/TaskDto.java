package com.cgms.dto;

import com.cgms.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;
    private Long counsellorId;
    private String counsellorName;
    private Long studentId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private LocalDateTime assignedDate;
    private TaskStatus status;
    private SubmissionDto submission;
}
