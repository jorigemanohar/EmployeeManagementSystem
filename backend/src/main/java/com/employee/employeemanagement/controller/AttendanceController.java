package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Attendance;
import com.employee.employeemanagement.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:3000")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(
            AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /*
     * Add Attendance
     */
    @PostMapping
    public ResponseEntity<?> addAttendance(
            @Valid @RequestBody Attendance attendance) {

        try {
            Attendance savedAttendance =
                    attendanceService.addAttendance(
                            attendance
                    );

            return ResponseEntity.ok(
                    savedAttendance
            );

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    /*
     * Get all Attendance
     */
    @GetMapping
    public ResponseEntity<List<Attendance>> getAllAttendance() {

        return ResponseEntity.ok(
                attendanceService.getAllAttendance()
        );
    }

    /*
     * Get Attendance by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getAttendanceById(
            @PathVariable Long id) {

        return attendanceService
                .getAttendanceById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    /*
     * Update Attendance
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody Attendance attendance) {

        try {
            Attendance updatedAttendance =
                    attendanceService.updateAttendance(
                            id,
                            attendance
                    );

            return ResponseEntity.ok(
                    updatedAttendance
            );

        } catch (RuntimeException e) {

            if ("Attendance not found"
                    .equals(e.getMessage())
                    || "Employee not found"
                    .equals(e.getMessage())) {

                return ResponseEntity.notFound()
                        .build();
            }

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    /*
     * Delete Attendance
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttendance(
            @PathVariable Long id) {

        try {
            attendanceService.deleteAttendance(id);

            return ResponseEntity.noContent()
                    .build();

        } catch (RuntimeException e) {

            if ("Attendance not found"
                    .equals(e.getMessage())) {

                return ResponseEntity.notFound()
                        .build();
            }

            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }
}