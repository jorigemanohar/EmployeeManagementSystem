package com.employee.employeemanagement.service;

import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Payroll;
import com.employee.employeemanagement.repository.EmployeeRepository;
import com.employee.employeemanagement.repository.PayrollRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(
            PayrollRepository payrollRepository,
            EmployeeRepository employeeRepository) {

        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    /*
     * Add Payroll
     */
    public Payroll addPayroll(Payroll payroll) {

        if (payroll.getEmployee() == null
                || payroll.getEmployee().getId() == null) {

            throw new RuntimeException(
                    "Employee is required"
            );
        }

        if (payroll.getMonth() == null
                || payroll.getMonth().trim().isEmpty()) {

            throw new RuntimeException(
                    "Payroll month is required"
            );
        }

        Long employeeId =
                payroll.getEmployee().getId();

        String month =
                payroll.getMonth().trim();

        /*
         * Prevent duplicate payroll for the
         * same employee and month.
         */
        boolean alreadyExists =
                payrollRepository
                        .existsByEmployeeIdAndMonth(
                                employeeId,
                                month
                        );

        if (alreadyExists) {
            throw new RuntimeException(
                    "Payroll already exists for this employee for this month"
            );
        }

        /*
         * Load employee from database.
         */
        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employee not found"
                                )
                        );

        payroll.setEmployee(employee);
        payroll.setMonth(month);

        /*
         * Get salary values safely.
         */
        double basicSalary =
                payroll.getBasicSalary() != null
                        ? payroll.getBasicSalary()
                        : 0.0;

        double allowances =
                payroll.getAllowances() != null
                        ? payroll.getAllowances()
                        : 0.0;

        double deductions =
                payroll.getDeductions() != null
                        ? payroll.getDeductions()
                        : 0.0;

        /*
         * Prevent negative salary.
         */
        double totalEarnings =
                basicSalary + allowances;

        if (deductions > totalEarnings) {
            throw new RuntimeException(
                    "Deductions cannot be greater than the total earnings"
            );
        }

        /*
         * Calculate net salary.
         */
        double netSalary =
                totalEarnings - deductions;

        payroll.setNetSalary(netSalary);

        return payrollRepository.save(payroll);
    }

    /*
     * Get all payroll records.
     */
    public List<Payroll> getAllPayrolls() {

        return payrollRepository.findAll();
    }

    /*
     * Get payroll by ID.
     */
    public Optional<Payroll> getPayrollById(
            Long id) {

        return payrollRepository.findById(id);
    }

    /*
     * Update Payroll.
     */
    public Payroll updatePayroll(
            Long id,
            Payroll payroll) {

        Payroll existingPayroll =
                payrollRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Payroll not found"
                                )
                        );

        if (payroll.getEmployee() == null
                || payroll.getEmployee().getId() == null) {

            throw new RuntimeException(
                    "Employee is required"
            );
        }

        if (payroll.getMonth() == null
                || payroll.getMonth().trim().isEmpty()) {

            throw new RuntimeException(
                    "Payroll month is required"
            );
        }

        Long employeeId =
                payroll.getEmployee().getId();

        String month =
                payroll.getMonth().trim();

        /*
         * Prevent duplicate employee/month
         * during update.
         */
        List<Payroll> employeePayrolls =
                payrollRepository
                        .findByEmployeeId(employeeId);

        boolean duplicate =
                employeePayrolls.stream()
                        .anyMatch(record ->
                                !record.getId().equals(id)
                                        &&
                                        month.equals(
                                                record.getMonth()
                                        )
                        );

        if (duplicate) {
            throw new RuntimeException(
                    "Payroll already exists for this employee for this month"
            );
        }

        /*
         * Load employee.
         */
        Employee employee =
                employeeRepository
                        .findById(employeeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employee not found"
                                )
                        );

        existingPayroll.setEmployee(employee);
        existingPayroll.setMonth(month);

        existingPayroll.setBasicSalary(
                payroll.getBasicSalary()
        );

        existingPayroll.setAllowances(
                payroll.getAllowances()
        );

        existingPayroll.setDeductions(
                payroll.getDeductions()
        );

        /*
         * Get values safely.
         */
        double basicSalary =
                payroll.getBasicSalary() != null
                        ? payroll.getBasicSalary()
                        : 0.0;

        double allowances =
                payroll.getAllowances() != null
                        ? payroll.getAllowances()
                        : 0.0;

        double deductions =
                payroll.getDeductions() != null
                        ? payroll.getDeductions()
                        : 0.0;

        /*
         * Prevent negative salary during update.
         */
        double totalEarnings =
                basicSalary + allowances;

        if (deductions > totalEarnings) {
            throw new RuntimeException(
                    "Deductions cannot be greater than the total earnings"
            );
        }

        /*
         * Recalculate net salary.
         */
        double netSalary =
                totalEarnings - deductions;

        existingPayroll.setNetSalary(
                netSalary
        );

        existingPayroll.setPaymentStatus(
                payroll.getPaymentStatus()
        );

        return payrollRepository.save(
                existingPayroll
        );
    }

    /*
     * Delete Payroll.
     */
    public void deletePayroll(Long id) {

        if (!payrollRepository.existsById(id)) {
            throw new RuntimeException(
                    "Payroll not found"
            );
        }

        payrollRepository.deleteById(id);
    }
}