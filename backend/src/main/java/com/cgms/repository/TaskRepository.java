package com.cgms.repository;

import com.cgms.model.Task;
import com.cgms.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCounsellorId(Long counsellorId);

    List<Task> findByStudentId(Long studentId);

    List<Task> findByStudentIdAndStatus(Long studentId, TaskStatus status);

    List<Task> findByCounsellorIdAndStudentId(Long counsellorId, Long studentId);
}
