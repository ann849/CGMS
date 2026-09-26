package com.cgms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounsellorDto {
    private Long id;              // User ID
    private Long profileId;       // CounsellorProfile ID
    private String name;
    private String email;
    private String phone;
    private String qualification;
    private String specialization;
    private Integer experience;
    private String bio;
    private String profilePhoto;
    private Boolean isActive;
}
