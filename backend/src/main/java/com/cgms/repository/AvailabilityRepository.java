package com.cgms.repository;

import com.cgms.model.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByCounsellorId(Long counsellorId);

    List<Availability> findByCounsellorIdAndIsBooked(Long counsellorId, Boolean isBooked);

    List<Availability> findByCounsellorIdAndDate(Long counsellorId, LocalDate date);

    List<Availability> findByDayOfWeekAndIsBookedFalse(String dayOfWeek);
}
