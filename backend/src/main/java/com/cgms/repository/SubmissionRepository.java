package com.cgms.repository;

import com.cgms.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByTaskId(Long taskId);

    List<Submission> findByStudentId(Long studentId);

    Optional<Submission> findByTaskIdAndStudentId(Long taskId, Long studentId);
}
