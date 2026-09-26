package com.cgms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {

    @NotBlank(message = "Marks or Grade is required")
    private String marksOrGrade;

    private String remarks;
}
