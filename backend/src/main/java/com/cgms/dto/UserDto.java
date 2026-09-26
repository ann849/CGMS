package com.cgms.dto;

import com.cgms.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private Object details; // Holds StudentDetail, CounsellorDetail, or AdminDetail
}
