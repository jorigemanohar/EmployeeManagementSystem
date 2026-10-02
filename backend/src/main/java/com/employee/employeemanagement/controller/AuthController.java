package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Role;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.EmployeeRepository;
import com.employee.employeemanagement.service.JwtService;
import com.employee.employeemanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmployeeRepository employeeRepository) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.employeeRepository = employeeRepository;
    }

    // =========================================================
    // REGISTER USER
    // ADMIN ONLY
    // =========================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user,
            Authentication authentication) {

        // -----------------------------------------------------
        // Check logged-in user is ADMIN
        // -----------------------------------------------------

        boolean isAdmin =
                authentication != null
                        && authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_ADMIN")
                        );

        if (!isAdmin) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Only ADMIN users can register new users"
                    );
        }

        // -----------------------------------------------------
        // Validate username
        // -----------------------------------------------------

        if (user.getUsername() == null
                || user.getUsername().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Username is required");
        }

        // -----------------------------------------------------
        // Validate password
        // -----------------------------------------------------

        if (user.getPassword() == null
                || user.getPassword().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Password is required");
        }

        // -----------------------------------------------------
        // Validate role
        // -----------------------------------------------------

        if (user.getRole() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Role is required");
        }

        // -----------------------------------------------------
        // Check duplicate username
        // -----------------------------------------------------

        if (userService
                .findByUsername(user.getUsername())
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Username already exists");
        }

        // -----------------------------------------------------
        // Encode password
        // -----------------------------------------------------

        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );

        // -----------------------------------------------------
        // Save user
        // -----------------------------------------------------

        User savedUser =
                userService.saveUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }

    // =========================================================
    // LOGIN
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody User loginRequest) {

        User user =
                userService
                        .findByUsername(
                                loginRequest.getUsername()
                        )
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Invalid username or password"
                    );
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        loginRequest.getPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Invalid username or password"
                    );
        }

        String token =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole().name()
                );

        return ResponseEntity.ok(token);
    }

    // =========================================================
    // CREATE EMPLOYEE LOGIN ACCOUNT
    // ADMIN ONLY
    // =========================================================

    @PostMapping(
            "/create-employee-account/{employeeId}"
    )
    public ResponseEntity<?> createEmployeeAccount(
            @PathVariable Long employeeId,
            @RequestBody User accountRequest,
            Authentication authentication) {

        // -----------------------------------------------------
        // Check logged-in user is ADMIN
        // -----------------------------------------------------

        boolean isAdmin =
                authentication != null
                        && authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_ADMIN")
                        );

        if (!isAdmin) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Only ADMIN users can create employee accounts"
                    );
        }

        // -----------------------------------------------------
        // Validate username
        // -----------------------------------------------------

        if (accountRequest.getUsername() == null
                || accountRequest
                .getUsername()
                .isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Username is required"
                    );
        }

        // -----------------------------------------------------
        // Validate password
        // -----------------------------------------------------

        if (accountRequest.getPassword() == null
                || accountRequest
                .getPassword()
                .isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Password is required"
                    );
        }

        // -----------------------------------------------------
        // Check username
        // -----------------------------------------------------

        if (userService
                .findByUsername(
                        accountRequest.getUsername()
                )
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            "Username already exists"
                    );
        }

        // -----------------------------------------------------
        // Find employee
        // -----------------------------------------------------

        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElse(null);

        if (employee == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            "Employee not found"
                    );
        }

        // -----------------------------------------------------
        // Check existing employee account
        // -----------------------------------------------------

        if (userService
                .employeeHasAccount(
                        employeeId
                )) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            "This employee already has a login account"
                    );
        }

        // -----------------------------------------------------
        // Create employee user
        // -----------------------------------------------------

        User newUser = new User();

        newUser.setUsername(
                accountRequest.getUsername()
        );

        newUser.setPassword(
                passwordEncoder.encode(
                        accountRequest.getPassword()
                )
        );

        // Always EMPLOYEE for this endpoint
        newUser.setRole(
                Role.EMPLOYEE
        );

        // Link employee
        newUser.setEmployee(
                employee
        );

        User savedUser =
                userService.saveUser(
                        newUser
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }
}