package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Department;
import com.employee.employeemanagement.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:3000")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService) {

        this.departmentService =
                departmentService;
    }

    // =========================================================
    // ADD DEPARTMENT
    // =========================================================

    @PostMapping
    public Department addDepartment(
            @Valid @RequestBody Department department) {

        return departmentService.addDepartment(
                department
        );
    }

    // =========================================================
    // GET ALL DEPARTMENTS
    // =========================================================

    @GetMapping
    public List<Department> getAllDepartments() {

        return departmentService
                .getAllDepartments();
    }

    // =========================================================
    // GET DEPARTMENT BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(
            @PathVariable Long id) {

        return departmentService
                .getDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

    // =========================================================
    // UPDATE DEPARTMENT
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody Department department) {

        try {

            Department updatedDepartment =
                    departmentService.updateDepartment(
                            id,
                            department
                    );

            return ResponseEntity.ok(
                    updatedDepartment
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    // =========================================================
    // DELETE DEPARTMENT
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable Long id) {

        try {

            departmentService.deleteDepartment(
                    id
            );

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}