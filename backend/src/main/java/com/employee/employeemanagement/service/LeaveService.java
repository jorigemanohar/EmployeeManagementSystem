package com.employee.employeemanagement.service;

import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Leave;
import com.employee.employeemanagement.repository.EmployeeRepository;
import com.employee.employeemanagement.repository.LeaveRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveService(
            LeaveRepository leaveRepository,
            EmployeeRepository employeeRepository) {

        this.leaveRepository = leaveRepository;
        this.employeeRepository = employeeRepository;
    }

    public Leave addLeave(Leave leave) {

        if (leave.getEmployee() != null
                && leave.getEmployee().getId() != null) {

            Employee employee = employeeRepository
                    .findById(leave.getEmployee().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Employee not found"));

            leave.setEmployee(employee);
        }

        return leaveRepository.save(leave);
    }

    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    public Optional<Leave> getLeaveById(Long id) {
        return leaveRepository.findById(id);
    }

    public Leave updateLeave(Long id, Leave leave) {

        Leave existingLeave = leaveRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Leave not found"));

        if (leave.getEmployee() != null
                && leave.getEmployee().getId() != null) {

            Employee employee = employeeRepository
                    .findById(leave.getEmployee().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Employee not found"));

            existingLeave.setEmployee(employee);
        }

        existingLeave.setLeaveType(leave.getLeaveType());
        existingLeave.setStartDate(leave.getStartDate());
        existingLeave.setEndDate(leave.getEndDate());
        existingLeave.setReason(leave.getReason());
        existingLeave.setStatus(leave.getStatus());

        return leaveRepository.save(existingLeave);
    }

    public void deleteLeave(Long id) {
        leaveRepository.deleteById(id);
    }
}