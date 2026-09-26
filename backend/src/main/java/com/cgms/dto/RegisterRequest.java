package com.cgms.dto;

import com.cgms.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String phone;

    @NotNull(message = "Role is required (STUDENT, COUNSELLOR, ADMIN)")
    private Role role;

    // Optional role-specific initial fields
    private String qualification; // For Counsellor
    private String specialization; // For Counsellor
    private String educationLevel; // For Student
    private String preferredField; // For Student
    private String department;     // For Admin
}
