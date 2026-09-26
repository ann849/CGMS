package com.cgms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDto {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long counsellorId;
    private String counsellorName;
    private String submissionFile;
    private String submissionText;
    private LocalDateTime submittedAt;
    private String remarks;
    private String marksOrGrade;
    private LocalDateTime evaluatedAt;
}
