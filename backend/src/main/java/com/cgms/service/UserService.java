package com.cgms.service;

import com.cgms.dto.UserDto;
import com.cgms.exception.ResourceNotFoundException;
import com.cgms.model.*;
import com.cgms.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CounsellorProfileRepository counsellorProfileRepository;

    public UserService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       CounsellorProfileRepository counsellorProfileRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.counsellorProfileRepository = counsellorProfileRepository;
    }

    public UserDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Object profile = null;
        if (user.getRole() == Role.STUDENT) {
            profile = studentProfileRepository.findByUser(user).orElse(null);
        } else if (user.getRole() == Role.COUNSELLOR) {
            profile = counsellorProfileRepository.findByUser(user).orElse(null);
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getName())
                .phone(user.getPhone())
                .role(user.getRole())
                .details(profile)
                .build();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
