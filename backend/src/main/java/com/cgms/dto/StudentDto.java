package com.cgms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long id;
    private Long profileId;
    private String name;
    private String email;
    private String phone;
    private String education;
    private String interests;
    private Boolean isActive;
}
