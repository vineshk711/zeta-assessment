package com.attendance.controller;

import com.attendance.service.AIAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ai/attendance")
@RequiredArgsConstructor
public class AIAttendanceController {
    private final AIAttendanceService aiAttendanceService;

    @GetMapping("/summary")
    public ResponseEntity<String> getDailySummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(aiAttendanceService.generateDailySummary(date));
    }

    @GetMapping("/question")
    public ResponseEntity<String> answerQuestion(@RequestParam String question) {
        return ResponseEntity.ok(aiAttendanceService.answerAttendanceQuestion(question));
    }
}