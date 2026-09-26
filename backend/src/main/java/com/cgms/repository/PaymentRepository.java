package com.cgms.repository;

import com.cgms.model.Payment;
import com.cgms.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAppointmentId(Long appointmentId);

    List<Payment> findByStudentId(Long studentId);

    List<Payment> findByStudentIdAndStatus(Long studentId, PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);
}
