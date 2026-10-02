package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Leave;
import com.employee.employeemanagement.entity.LeaveStatus;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.LeaveRepository;
import com.employee.employeemanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/leaves")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeLeaveController {

    private final LeaveRepository leaveRepository;
    private final UserService userService;

    public EmployeeLeaveController(
            LeaveRepository leaveRepository,
            UserService userService) {
        this.leaveRepository = leaveRepository;
        this.userService = userService;
    }

    @GetMapping
    public List<Leave> getMyLeaves(
            Authentication authentication) {

        String username = authentication.getName();

        User user =
                userService.getUserByUsername(username);

        if (user.getEmployee() == null) {
            throw new RuntimeException(
                    "No employee is linked to this user"
            );
        }

        Long employeeId =
                user.getEmployee().getId();

        return leaveRepository.findByEmployeeId(
                employeeId
        );
    }

    @PostMapping
    public Leave submitLeave(
            Authentication authentication,
            @Valid @RequestBody Leave leave) {

        String username = authentication.getName();

        User user =
                userService.getUserByUsername(username);

        if (user.getEmployee() == null) {
            throw new RuntimeException(
                    "No employee is linked to this user"
            );
        }

        /*
         * The employee is taken from the
         * authenticated account.
         */
        leave.setEmployee(user.getEmployee());

        /*
         * Every new employee leave request
         * starts as PENDING.
         */
        leave.setStatus(LeaveStatus.PENDING);

        return leaveRepository.save(leave);
    }
}