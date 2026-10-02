package com.employee.employeemanagement.service;

import com.employee.employeemanagement.entity.Attendance;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.repository.AttendanceRepository;
import com.employee.employeemanagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(
            AttendanceRepository attendanceRepository,
            EmployeeRepository employeeRepository) {

        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    /*
     * Add Attendance
     */
    public Attendance addAttendance(Attendance attendance) {

        if (attendance.getEmployee() == null
                || attendance.getEmployee().getId() == null) {

            throw new RuntimeException(
                    "Employee is required"
            );
        }

        Long employeeId =
                attendance.getEmployee().getId();

        LocalDate attendanceDate =
                attendance.getAttendanceDate();

        if (attendanceDate == null) {
            throw new RuntimeException(
                    "Attendance date is required"
            );
        }

        /*
         * Check whether attendance already exists
         * for this employee on this date.
         */
        boolean alreadyExists =
                attendanceRepository
                        .existsByEmployeeIdAndAttendanceDate(
                                employeeId,
                                attendanceDate
                        );

        if (alreadyExists) {
            throw new RuntimeException(
                    "Attendance already exists for this employee on this date"
            );
        }

        /*
         * Load the actual employee from database.
         */
        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employee not found"
                                )
                        );

        attendance.setEmployee(employee);

        return attendanceRepository.save(
                attendance
        );
    }

    /*
     * Get all attendance records
     */
    public List<Attendance> getAllAttendance() {

        return attendanceRepository.findAll();
    }

    /*
     * Get attendance by ID
     */
    public Optional<Attendance> getAttendanceById(
            Long id) {

        return attendanceRepository.findById(id);
    }

    /*
     * Update Attendance
     */
    public Attendance updateAttendance(
            Long id,
            Attendance attendance) {

        Attendance existingAttendance =
                attendanceRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Attendance not found"
                                )
                        );

        if (attendance.getEmployee() == null
                || attendance.getEmployee().getId() == null) {

            throw new RuntimeException(
                    "Employee is required"
            );
        }

        Long employeeId =
                attendance.getEmployee().getId();

        LocalDate attendanceDate =
                attendance.getAttendanceDate();

        if (attendanceDate == null) {
            throw new RuntimeException(
                    "Attendance date is required"
            );
        }

        /*
         * Check for another attendance record
         * belonging to the same employee and date.
         *
         * The current record itself is ignored.
         */
        List<Attendance> employeeAttendance =
                attendanceRepository
                        .findByEmployeeId(employeeId);

        boolean duplicate =
                employeeAttendance.stream()
                        .anyMatch(record ->
                                !record.getId().equals(id)
                                        &&
                                        attendanceDate.equals(
                                                record.getAttendanceDate()
                                        )
                        );

        if (duplicate) {
            throw new RuntimeException(
                    "Attendance already exists for this employee on this date"
            );
        }

        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employee not found"
                                )
                        );

        existingAttendance.setEmployee(
                employee
        );

        existingAttendance.setAttendanceDate(
                attendanceDate
        );

        existingAttendance.setStatus(
                attendance.getStatus()
        );

        return attendanceRepository.save(
                existingAttendance
        );
    }

    /*
     * Delete Attendance
     */
    public void deleteAttendance(Long id) {

        if (!attendanceRepository.existsById(id)) {
            throw new RuntimeException(
                    "Attendance not found"
            );
        }

        attendanceRepository.deleteById(id);
    }
}