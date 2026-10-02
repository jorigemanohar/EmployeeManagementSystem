package com.employee.employeemanagement.service;

import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    // =========================================================
    // SAVE USER
    // =========================================================

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // =========================================================
    // FIND USER BY USERNAME
    // =========================================================

    public Optional<User> findByUsername(
            String username) {

        return userRepository
                .findByUsername(username);
    }

    // =========================================================
    // GET USER BY USERNAME
    // =========================================================

    public User getUserByUsername(
            String username) {

        return userRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );
    }

    // =========================================================
    // FIND USER BY EMPLOYEE ID
    // =========================================================

    public Optional<User> findByEmployeeId(
            Long employeeId) {

        return userRepository
                .findByEmployeeId(employeeId);
    }

    // =========================================================
    // CHECK WHETHER EMPLOYEE HAS AN ACCOUNT
    // =========================================================

    public boolean employeeHasAccount(
            Long employeeId) {

        return userRepository
                .findByEmployeeId(employeeId)
                .isPresent();
    }
}