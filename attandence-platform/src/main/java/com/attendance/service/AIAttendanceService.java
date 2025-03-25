package com.attendance.service;

import com.attendance.model.Attendance;
import com.attendance.model.AttendanceType;
import com.attendance.model.Employee;
import com.attendance.repository.AttendanceRepository;
import com.attendance.repository.EmployeeRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAttendanceService {
        private final AttendanceRepository attendanceRepository;
        private final EmployeeRepository employeeRepository;
        private final OpenAiService openAiService;

        @Value("${openai.model}")
        private String model;

        /**
         * Generates a daily attendance summary for the specified date.
         * 
         * @param date The date for which to generate the summary
         * @return A natural language summary of attendance for the day
         * @throws RuntimeException if there's an issue accessing data or communicating
         *                          with OpenAI
         */
        public String generateDailySummary(LocalDateTime date) {
                try {
                        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
                        LocalDateTime endOfDay = date.toLocalDate().plusDays(1).atStartOfDay();

                        // Get all employees
                        List<Employee> employees;
                        try {
                                employees = employeeRepository.findAll();
                                if (employees.isEmpty()) {
                                        log.warn("No employees found in the database");
                                        return "No attendance data is available as there are no employees in the system.";
                                }
                        } catch (DataAccessException e) {
                                log.error("Failed to retrieve employees from the database", e);
                                throw new RuntimeException("Unable to access employee data", e);
                        }

                        // Map to store attendance data per employee
                        Map<Employee, List<Attendance>> attendanceByEmployee = new HashMap<>();

                        // For each employee, get their attendance records for the specified day
                        try {
                                for (Employee employee : employees) {
                                        List<Attendance> records = attendanceRepository.findByEmployeeAndCheckInBetween(
                                                        employee, startOfDay, endOfDay);
                                        attendanceByEmployee.put(employee, records);
                                }
                        } catch (DataAccessException e) {
                                log.error("Failed to retrieve attendance records from the database", e);
                                throw new RuntimeException("Unable to access attendance data", e);
                        }

                        // Build the prompt
                        StringBuilder prompt = new StringBuilder();
                        prompt.append("Generate a summary of attendance for ")
                                        .append(date.format(DateTimeFormatter.ISO_LOCAL_DATE))
                                        .append(":\n\n");

                        for (Map.Entry<Employee, List<Attendance>> entry : attendanceByEmployee.entrySet()) {
                                Employee employee = entry.getKey();
                                List<Attendance> records = entry.getValue();

                                prompt.append(employee.getFirstName()).append(" ").append(employee.getLastName())
                                                .append(" (").append(employee.getDepartment()).append("): ");

                                if (records.isEmpty()) {
                                        prompt.append("No attendance records\n");
                                } else {
                                        prompt.append(records.stream()
                                                        .map(a -> a.getType().toString() + " at "
                                                                        + a.getCheckIn().format(
                                                                                        DateTimeFormatter.ISO_LOCAL_TIME))
                                                        .collect(Collectors.joining(", ")))
                                                        .append("\n");
                                }
                        }

                        try {
                                ChatCompletionRequest request = ChatCompletionRequest.builder()
                                                .model(model)
                                                .messages(List.of(
                                                                new com.theokanning.openai.completion.chat.ChatMessage(
                                                                                "user",
                                                                                prompt.toString())))
                                                .build();

                                return openAiService.createChatCompletion(request)
                                                .getChoices().get(0).getMessage().getContent();
                        } catch (Exception e) {
                                log.error("Error calling OpenAI API", e);
                                // Fallback response in case of OpenAI API failure
                                return "Unable to generate AI summary. Raw attendance data: " +
                                                attendanceByEmployee.entrySet().stream()
                                                                .map(entry -> {
                                                                        Employee emp = entry.getKey();
                                                                        List<Attendance> records = entry.getValue();
                                                                        return emp.getFirstName() + " "
                                                                                        + emp.getLastName() + ": " +
                                                                                        (records.isEmpty()
                                                                                                        ? "No records"
                                                                                                        : records.size() + " records");
                                                                })
                                                                .collect(Collectors.joining("; "));
                        }
                } catch (Exception e) {
                        log.error("Unexpected error generating attendance summary", e);
                        throw new RuntimeException("Failed to generate attendance summary: " + e.getMessage(), e);
                }
        }

        /**
         * Answers a question about attendance data using AI.
         * 
         * @param question The question to answer
         * @return An AI-generated answer based on the attendance data
         * @throws RuntimeException if there's an issue accessing data or communicating
         *                          with OpenAI
         */
        public String answerAttendanceQuestion(String question) {
                try {
                        if (question == null || question.trim().isEmpty()) {
                                return "Please provide a valid question about attendance.";
                        }

                        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0)
                                        .withSecond(0);
                        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);

                        // Get all employees
                        List<Employee> employees;
                        try {
                                employees = employeeRepository.findAll();
                                if (employees.isEmpty()) {
                                        log.warn("No employees found in the database");
                                        return "No attendance data is available as there are no employees in the system.";
                                }
                        } catch (DataAccessException e) {
                                log.error("Failed to retrieve employees from the database", e);
                                throw new RuntimeException("Unable to access employee data", e);
                        }

                        // Map to store attendance data per employee
                        Map<Employee, Map<AttendanceType, Long>> attendanceStats = new HashMap<>();

                        // For each employee, get their attendance records for the month and count by
                        // type
                        try {
                                for (Employee employee : employees) {
                                        Map<AttendanceType, Long> typeCounts = new HashMap<>();

                                        // Initialize counts for all attendance types to ensure we have data for each
                                        // type
                                        for (AttendanceType type : AttendanceType.values()) {
                                                long count = attendanceRepository
                                                                .countByEmployeeAndTypeAndCheckInBetween(
                                                                                employee, type, startOfMonth,
                                                                                endOfMonth);
                                                typeCounts.put(type, count);
                                        }

                                        attendanceStats.put(employee, typeCounts);
                                }
                        } catch (DataAccessException e) {
                                log.error("Failed to retrieve attendance statistics from the database", e);
                                throw new RuntimeException("Unable to access attendance data", e);
                        }

                        // Build the prompt
                        StringBuilder prompt = new StringBuilder();
                        prompt.append("Based on the following attendance data, please answer this question: ")
                                        .append(question)
                                        .append("\n\n");
                        prompt.append("Monthly Attendance Data:\n");

                        for (Map.Entry<Employee, Map<AttendanceType, Long>> entry : attendanceStats.entrySet()) {
                                Employee employee = entry.getKey();
                                Map<AttendanceType, Long> typeCounts = entry.getValue();

                                prompt.append(employee.getFirstName()).append(" ").append(employee.getLastName())
                                                .append(" (").append(employee.getDepartment()).append("): ");

                                prompt.append(typeCounts.entrySet().stream()
                                                .map(e -> e.getKey() + ": " + e.getValue())
                                                .collect(Collectors.joining(", ")))
                                                .append("\n");
                        }

                        try {
                                ChatCompletionRequest request = ChatCompletionRequest.builder()
                                                .model(model)
                                                .messages(List.of(
                                                                new com.theokanning.openai.completion.chat.ChatMessage(
                                                                                "user",
                                                                                prompt.toString())))
                                                .build();

                                return openAiService.createChatCompletion(request)
                                                .getChoices().get(0).getMessage().getContent();
                        } catch (Exception e) {
                                log.error("Error calling OpenAI API", e);
                                // Fallback response in case of OpenAI API failure
                                StringBuilder fallback = new StringBuilder(
                                                "Unable to generate AI response. Raw data summary:");

                                // Find employee with most absences
                                Employee mostAbsent = null;
                                long maxAbsences = -1;

                                // Find employee with most WFH
                                Employee mostWfh = null;
                                long maxWfh = -1;

                                for (Map.Entry<Employee, Map<AttendanceType, Long>> entry : attendanceStats
                                                .entrySet()) {
                                        Employee emp = entry.getKey();
                                        Map<AttendanceType, Long> counts = entry.getValue();

                                        long absences = counts.getOrDefault(AttendanceType.ABSENT, 0L);
                                        if (absences > maxAbsences) {
                                                maxAbsences = absences;
                                                mostAbsent = emp;
                                        }

                                        long wfh = counts.getOrDefault(AttendanceType.WORK_FROM_HOME, 0L);
                                        if (wfh > maxWfh) {
                                                maxWfh = wfh;
                                                mostWfh = emp;
                                        }
                                }

                                fallback.append("\nMost absences: ");
                                fallback.append(mostAbsent != null
                                                ? mostAbsent.getFirstName() + " " + mostAbsent.getLastName() + " ("
                                                                + maxAbsences + " times)"
                                                : "No absences recorded");

                                fallback.append("\nMost WFH: ");
                                fallback.append(mostWfh != null
                                                ? mostWfh.getFirstName() + " " + mostWfh.getLastName() + " (" + maxWfh
                                                                + " times)"
                                                : "No WFH recorded");

                                return fallback.toString();
                        }
                } catch (Exception e) {
                        log.error("Unexpected error answering attendance question", e);
                        throw new RuntimeException("Failed to answer attendance question: " + e.getMessage(), e);
                }
        }
}