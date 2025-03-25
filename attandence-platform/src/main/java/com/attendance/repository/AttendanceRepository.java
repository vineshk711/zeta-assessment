package com.attendance.repository;

import com.attendance.model.Attendance;
import com.attendance.model.AttendanceType;
import com.attendance.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeAndCheckInBetween(Employee employee, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee = ?1 AND a.type = ?2 AND a.checkIn BETWEEN ?3 AND ?4")
    long countByEmployeeAndTypeAndCheckInBetween(Employee employee, AttendanceType type, LocalDateTime start,
            LocalDateTime end);

    List<Attendance> findByEmployeeAndTypeAndCheckInBetween(Employee employee, AttendanceType type, LocalDateTime start,
            LocalDateTime end);
}