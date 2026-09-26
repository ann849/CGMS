package com.cgms.config;

import com.cgms.model.*;
import com.cgms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CounsellorProfileRepository counsellorProfileRepository;
    private final AvailabilityRepository availabilityRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           CounsellorProfileRepository counsellorProfileRepository,
                           AvailabilityRepository availabilityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.counsellorProfileRepository = counsellorProfileRepository;
        this.availabilityRepository = availabilityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Admin
        if (!userRepository.existsByEmail("admin@cgms.com")) {
            User admin = User.builder()
                    .name("System Super Admin")
                    .email("admin@cgms.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .phone("+1-800-555-0100")
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Seeded Admin: admin@cgms.com / admin123");
        }

        // 2. Seed 3 Counsellors
        if (!userRepository.existsByEmail("sarah.connor@cgms.com")) {
            User c1 = userRepository.save(User.builder()
                    .name("Dr. Sarah Connor")
                    .email("sarah.connor@cgms.com")
                    .password(passwordEncoder.encode("counsellor123"))
                    .role(Role.COUNSELLOR)
                    .phone("+1-800-555-0101")
                    .isActive(true)
                    .build());

            counsellorProfileRepository.save(CounsellorProfile.builder()
                    .user(c1)
                    .qualification("Ph.D in Computer Science & AI")
                    .specialization("STEM, Software Engineering & AI Careers")
                    .experience(10)
                    .bio("Specialist in guiding students toward software engineering, data science, and technology leadership tracks.")
                    .profilePhoto("https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150")
                    .build());

            seedAvailabilitySlots(c1);
        }

        if (!userRepository.existsByEmail("alan.grant@cgms.com")) {
            User c2 = userRepository.save(User.builder()
                    .name("Dr. Alan Grant")
                    .email("alan.grant@cgms.com")
                    .password(passwordEncoder.encode("counsellor123"))
                    .role(Role.COUNSELLOR)
                    .phone("+1-800-555-0102")
                    .isActive(true)
                    .build());

            counsellorProfileRepository.save(CounsellorProfile.builder()
                    .user(c2)
                    .qualification("M.Sc in Biotechnology & Pre-Med")
                    .specialization("Healthcare, Medicine & Bio Sciences")
                    .experience(12)
                    .bio("Dedicated to assisting students applying to medical colleges, biomedical research, and health technology.")
                    .profilePhoto("https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=150")
                    .build());

            seedAvailabilitySlots(c2);
        }

        if (!userRepository.existsByEmail("ellie.sattler@cgms.com")) {
            User c3 = userRepository.save(User.builder()
                    .name("Dr. Ellie Sattler")
                    .email("ellie.sattler@cgms.com")
                    .password(passwordEncoder.encode("counsellor123"))
                    .role(Role.COUNSELLOR)
                    .phone("+1-800-555-0103")
                    .isActive(true)
                    .build());

            counsellorProfileRepository.save(CounsellorProfile.builder()
                    .user(c3)
                    .qualification("MBA in Business & Finance")
                    .specialization("Business Administration, Finance & Marketing")
                    .experience(8)
                    .bio("Expert mentor for business management, international university admissions, and entrepreneurship.")
                    .profilePhoto("https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150")
                    .build());

            seedAvailabilitySlots(c3);
        }

        // 3. Seed 3 Students
        if (!userRepository.existsByEmail("john.doe@student.com")) {
            User s1 = userRepository.save(User.builder()
                    .name("John Doe")
                    .email("john.doe@student.com")
                    .password(passwordEncoder.encode("student123"))
                    .role(Role.STUDENT)
                    .phone("+1-800-555-0201")
                    .isActive(true)
                    .build());

            studentProfileRepository.save(StudentProfile.builder()
                    .user(s1)
                    .education("Undergraduate 3rd Year")
                    .interests("Artificial Intelligence, Web Development, Cloud Computing")
                    .dob(LocalDate.of(2003, 5, 14))
                    .build());
        }

        if (!userRepository.existsByEmail("jane.smith@student.com")) {
            User s2 = userRepository.save(User.builder()
                    .name("Jane Smith")
                    .email("jane.smith@student.com")
                    .password(passwordEncoder.encode("student123"))
                    .role(Role.STUDENT)
                    .phone("+1-800-555-0202")
                    .isActive(true)
                    .build());

            studentProfileRepository.save(StudentProfile.builder()
                    .user(s2)
                    .education("High School Senior")
                    .interests("Biomedical Engineering, Genetics, Pre-Med")
                    .dob(LocalDate.of(2005, 9, 21))
                    .build());
        }

        if (!userRepository.existsByEmail("alex.rivera@student.com")) {
            User s3 = userRepository.save(User.builder()
                    .name("Alex Rivera")
                    .email("alex.rivera@student.com")
                    .password(passwordEncoder.encode("student123"))
                    .role(Role.STUDENT)
                    .phone("+1-800-555-0203")
                    .isActive(true)
                    .build());

            studentProfileRepository.save(StudentProfile.builder()
                    .user(s3)
                    .education("Postgraduate Student")
                    .interests("Corporate Strategy, Financial Analysis, Tech Startups")
                    .dob(LocalDate.of(2001, 11, 3))
                    .build());
        }

        System.out.println(">>> CGMS Demo Data Seeding Completed Successfully! <<<");
    }

    private void seedAvailabilitySlots(User counsellor) {
        LocalDate today = LocalDate.now();

        // Slot 1: Tomorrow 09:00 - 10:00
        availabilityRepository.save(Availability.builder()
                .counsellor(counsellor)
                .date(today.plusDays(1))
                .dayOfWeek(today.plusDays(1).getDayOfWeek().name())
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .isBooked(false)
                .build());

        // Slot 2: Tomorrow 11:00 - 12:00
        availabilityRepository.save(Availability.builder()
                .counsellor(counsellor)
                .date(today.plusDays(1))
                .dayOfWeek(today.plusDays(1).getDayOfWeek().name())
                .startTime(LocalTime.of(11, 0))
                .endTime(LocalTime.of(12, 0))
                .isBooked(false)
                .build());

        // Slot 3: Day after tomorrow 14:00 - 15:00
        availabilityRepository.save(Availability.builder()
                .counsellor(counsellor)
                .date(today.plusDays(2))
                .dayOfWeek(today.plusDays(2).getDayOfWeek().name())
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .isBooked(false)
                .build());
    }
}
