package com.cgms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubmissionRequest {
    private String submissionText;
    private String submissionFile;
}
