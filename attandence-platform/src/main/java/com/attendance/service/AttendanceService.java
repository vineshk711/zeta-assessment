package com.attendance.service;

import com.attendance.model.Attendance;
import com.attendance.model.AttendanceType;
import com.attendance.model.Employee;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void recordCheckIn(Long employeeId, AttendanceType type, String notes) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setCheckIn(LocalDateTime.now());
        attendance.setType(type == null ? AttendanceType.PRESENT : type);
        attendance.setNotes(notes);

        attendanceRepository.save(attendance);
    }

    @Transactional
    public void recordCheckOut(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        attendance.setCheckOut(LocalDateTime.now());
        attendanceRepository.save(attendance);
    }

    @Transactional
    public void updateAttendance(Long attendanceId, AttendanceType type, String notes) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));

        if (type != null) {
            attendance.setType(type);
        }

        if (notes != null) {
            attendance.setNotes(notes);
        }

        attendanceRepository.save(attendance);
    }
    public List<Attendance> getEmployeeAttendance(Long employeeId, LocalDateTime start, LocalDateTime end) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return attendanceRepository.findByEmployeeAndCheckInBetween(employee, start, end);
    }

    public long getWorkFromHomeCount(Long employeeId, LocalDateTime start, LocalDateTime end) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return attendanceRepository.countByEmployeeAndTypeAndCheckInBetween(
                employee, AttendanceType.WORK_FROM_HOME, start, end);
    }
}