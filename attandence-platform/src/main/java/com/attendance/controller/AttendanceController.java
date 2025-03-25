package com.attendance.controller;

import com.attendance.model.Attendance;
import com.attendance.model.AttendanceType;
import com.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<Void> checkIn(
            @PathVariable Long employeeId,
            @RequestParam(value = "attendanceType", required = false) AttendanceType attendanceType,
            @RequestParam(value = "note", required = false) String note) {
        attendanceService.recordCheckIn(employeeId, attendanceType, note);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/check-out/{attendanceId}")
    public ResponseEntity<Void> checkOut(@PathVariable Long attendanceId) {
        attendanceService.recordCheckOut(attendanceId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Attendance>> getEmployeeAttendance(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(attendanceService.getEmployeeAttendance(employeeId, start, end));
    }

    @GetMapping("/work-from-home/count/{employeeId}")
    public ResponseEntity<Long> getWorkFromHomeCount(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(attendanceService.getWorkFromHomeCount(employeeId, start, end));
    }

    @PutMapping("/update/{attendanceId}")
    public ResponseEntity<Void> updateAttendance(
            @PathVariable Long attendanceId,
            @RequestParam(required = false) AttendanceType attendanceType,
            @RequestParam(required = false) String notes) {
        attendanceService.updateAttendance(attendanceId, attendanceType, notes);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}