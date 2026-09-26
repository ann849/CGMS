package com.cgms.repository;

import com.cgms.model.CounsellorProfile;
import com.cgms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CounsellorProfileRepository extends JpaRepository<CounsellorProfile, Long> {

    Optional<CounsellorProfile> findByUser(User user);

    Optional<CounsellorProfile> findByUserId(Long userId);

    List<CounsellorProfile> findBySpecializationContainingIgnoreCase(String specialization);
}
