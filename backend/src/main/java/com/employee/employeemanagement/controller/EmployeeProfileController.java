package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeeProfileController {

    private final UserService userService;

    public EmployeeProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public Employee getMyProfile(
            Authentication authentication) {

        // Get the username from the logged-in JWT user
        String username = authentication.getName();

        // Find the corresponding User record
        User user = userService.getUserByUsername(username);

        // Make sure the user is linked to an employee
        if (user.getEmployee() == null) {
            throw new RuntimeException(
                    "No employee is linked to this user"
            );
        }

        // Get the employee linked to this account
        Employee employee = user.getEmployee();

        /*
         * This field is @Transient, so it is not stored
         * in the employees table.
         *
         * Since this request is being accessed through
         * a valid user account, the account exists.
         */
        employee.setAccountCreated(true);

        return employee;
    }
}