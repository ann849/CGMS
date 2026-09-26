package com.cgms.service;

import com.cgms.dto.AuthResponse;
import com.cgms.dto.LoginRequest;
import com.cgms.dto.RegisterRequest;
import com.cgms.exception.BadRequestException;
import com.cgms.model.*;
import com.cgms.repository.*;
import com.cgms.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CounsellorProfileRepository counsellorProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       StudentProfileRepository studentProfileRepository,
                       CounsellorProfileRepository counsellorProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.counsellorProfileRepository = counsellorProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Role targetRole = request.getRole() != null ? request.getRole() : Role.STUDENT;

        // Role-based registration security rule:
        // Students can self-register. Counsellor & Admin accounts can ONLY be created by an Admin.
        if (targetRole == Role.COUNSELLOR || targetRole == Role.ADMIN) {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = currentAuth != null && currentAuth.isAuthenticated() &&
                    currentAuth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new BadRequestException("Access denied: Only Administrators can create " + targetRole + " accounts.");
            }
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered!");
        }

        User user = User.builder()
                .name(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(targetRole)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        if (targetRole == Role.STUDENT) {
            StudentProfile studentProfile = StudentProfile.builder()
                    .user(savedUser)
                    .education(request.getEducationLevel())
                    .interests(request.getPreferredField())
                    .build();
            studentProfileRepository.save(studentProfile);
        } else if (targetRole == Role.COUNSELLOR) {
            CounsellorProfile counsellorProfile = CounsellorProfile.builder()
                    .user(savedUser)
                    .qualification(request.getQualification())
                    .specialization(request.getSpecialization())
                    .build();
            counsellorProfileRepository.save(counsellorProfile);
        }

        // Authenticate & generate JWT
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(jwt)
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getName())
                .role(savedUser.getRole())
                .message("Registration successful!")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!user.getIsActive()) {
            throw new BadRequestException("Account is inactive. Please contact support.");
        }

        return AuthResponse.builder()
                .accessToken(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getName())
                .role(user.getRole())
                .message("Login successful!")
                .build();
    }
}
