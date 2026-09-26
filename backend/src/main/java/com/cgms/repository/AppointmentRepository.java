package com.cgms.repository;

import com.cgms.model.Appointment;
import com.cgms.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByStudentId(Long studentId);

    List<Appointment> findByCounsellorId(Long counsellorId);

    List<Appointment> findByStudentIdAndStatus(Long studentId, AppointmentStatus status);

    List<Appointment> findByCounsellorIdAndStatus(Long counsellorId, AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
}
