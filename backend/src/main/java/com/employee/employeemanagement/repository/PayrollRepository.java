package com.employee.employeemanagement.repository;

import com.employee.employeemanagement.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayrollRepository
        extends JpaRepository<Payroll, Long> {

    List<Payroll> findByEmployeeId(Long employeeId);

    boolean existsByEmployeeIdAndMonth(
            Long employeeId,
            String month
    );
}