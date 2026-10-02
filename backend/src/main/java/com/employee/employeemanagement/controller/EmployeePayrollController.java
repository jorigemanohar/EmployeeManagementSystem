package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Payroll;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.PayrollRepository;
import com.employee.employeemanagement.service.PayrollPdfService;
import com.employee.employeemanagement.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/payroll")
@CrossOrigin(origins = "http://localhost:3000")
public class EmployeePayrollController {

    private final PayrollRepository payrollRepository;
    private final UserService userService;
    private final PayrollPdfService payrollPdfService;

    public EmployeePayrollController(
            PayrollRepository payrollRepository,
            UserService userService,
            PayrollPdfService payrollPdfService) {

        this.payrollRepository = payrollRepository;
        this.userService = userService;
        this.payrollPdfService = payrollPdfService;
    }

    @GetMapping
    public List<Payroll> getMyPayroll(
            Authentication authentication) {

        User user =
                userService.getUserByUsername(
                        authentication.getName()
                );

        if (user.getEmployee() == null) {
            throw new RuntimeException(
                    "No employee is linked to this user"
            );
        }

        Long employeeId =
                user.getEmployee().getId();

        return payrollRepository.findByEmployeeId(employeeId);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadMySalarySlip(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                userService.getUserByUsername(
                        authentication.getName()
                );

        if (user.getEmployee() == null) {
            return ResponseEntity.badRequest().build();
        }

        Long employeeId =
                user.getEmployee().getId();

        List<Payroll> myPayrolls =
                payrollRepository.findByEmployeeId(
                        employeeId
                );

        Payroll payroll = myPayrolls.stream()
                .filter(record -> record.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (payroll == null) {
            return ResponseEntity.status(403).build();
        }

        byte[] pdf =
                payrollPdfService.generateSalarySlip(
                        payroll
                );

        String fileName =
                "salary-slip-"
                        + payroll.getEmployee().getEmployeeCode()
                        + "-"
                        + payroll.getMonth()
                        + ".pdf";

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(pdf);
    }
}