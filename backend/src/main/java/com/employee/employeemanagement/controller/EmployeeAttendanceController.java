package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Attendance;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.AttendanceRepository;
import com.employee.employeemanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/attendance")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeAttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final UserService userService;

    public EmployeeAttendanceController(
            AttendanceRepository attendanceRepository,
            UserService userService) {

        this.attendanceRepository = attendanceRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<Attendance> getMyAttendance(
            Authentication authentication) {

        String username = authentication.getName();

        User user = userService.getUserByUsername(username);

        if (user.getEmployee() == null) {
            throw new RuntimeException(
                    "No employee is linked to this user"
            );
        }

        Long employeeId = user.getEmployee().getId();

        return attendanceRepository.findByEmployeeId(employeeId);
    }
}
