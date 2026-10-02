package com.employee.employeemanagement.controller;

import com.employee.employeemanagement.entity.Payroll;
import com.employee.employeemanagement.service.PayrollPdfService;
import com.employee.employeemanagement.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@CrossOrigin(origins = "http://localhost:3000")
public class PayrollController {

    private final PayrollService payrollService;
    private final PayrollPdfService payrollPdfService;

    public PayrollController(
            PayrollService payrollService,
            PayrollPdfService payrollPdfService) {

        this.payrollService = payrollService;
        this.payrollPdfService = payrollPdfService;
    }

    /*
     * Add Payroll
     */
    @PostMapping
    public ResponseEntity<?> addPayroll(
            @Valid @RequestBody Payroll payroll) {

        try {
            Payroll savedPayroll =
                    payrollService.addPayroll(payroll);

            return ResponseEntity.ok(savedPayroll);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /*
     * Get all Payroll records
     */
    @GetMapping
    public ResponseEntity<List<Payroll>> getAllPayrolls() {

        return ResponseEntity.ok(
                payrollService.getAllPayrolls()
        );
    }

    /*
     * Get Payroll by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getPayrollById(
            @PathVariable Long id) {

        return payrollService
                .getPayrollById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }

    /*
     * Update Payroll
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePayroll(
            @PathVariable Long id,
            @Valid @RequestBody Payroll payroll) {

        try {
            Payroll updatedPayroll =
                    payrollService.updatePayroll(
                            id,
                            payroll
                    );

            return ResponseEntity.ok(
                    updatedPayroll
            );

        } catch (RuntimeException e) {

            if ("Payroll not found"
                    .equals(e.getMessage())) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /*
     * Delete Payroll
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayroll(
            @PathVariable Long id) {

        try {
            payrollService.deletePayroll(id);

            return ResponseEntity
                    .noContent()
                    .build();

        } catch (RuntimeException e) {

            if ("Payroll not found"
                    .equals(e.getMessage())) {

                return ResponseEntity
                        .notFound()
                        .build();
            }

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    /*
     * Generate Salary Slip PDF
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateSalarySlip(
            @PathVariable Long id) {

        Payroll payroll =
                payrollService
                        .getPayrollById(id)
                        .orElse(null);

        if (payroll == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        byte[] pdf =
                payrollPdfService
                        .generateSalarySlip(payroll);

        String fileName =
                "salary-slip-"
                        + payroll.getEmployee()
                        .getEmployeeCode()
                        + "-"
                        + payroll.getMonth()
                        + ".pdf";

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""
                                + fileName
                                + "\""
                )
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .contentLength(pdf.length)
                .body(pdf);
    }
}