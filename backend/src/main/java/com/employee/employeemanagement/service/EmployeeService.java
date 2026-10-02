package com.employee.employeemanagement.service;

import com.employee.employeemanagement.entity.Department;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.repository.DepartmentRepository;
import com.employee.employeemanagement.repository.EmployeeRepository;
import com.employee.employeemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    /*
     * Add Employee
     */
    public Employee addEmployee(Employee employee) {

        if (employee.getDepartment() != null
                && employee.getDepartment().getId() != null) {

            Department department =
                    departmentRepository.findById(
                            employee.getDepartment().getId()
                    ).orElseThrow(
                            () -> new RuntimeException(
                                    "Department not found"
                            )
                    );

            employee.setDepartment(department);
        }

        Employee savedEmployee =
                employeeRepository.save(employee);

        /*
         * Newly created employees do not have
         * a login account yet.
         */
        savedEmployee.setAccountCreated(false);

        return savedEmployee;
    }

    /*
     * Get All Employees
     */
    public List<Employee> getAllEmployees() {

        List<Employee> employees =
                employeeRepository.findAll();

        /*
         * Check every employee against the users table.
         */
        for (Employee employee : employees) {

            boolean accountExists =
                    userRepository
                            .findByEmployeeId(employee.getId())
                            .isPresent();

            employee.setAccountCreated(accountExists);
        }

        return employees;
    }

    /*
     * Get Employee By ID
     */
    public Optional<Employee> getEmployeeById(Long id) {

        Optional<Employee> employee =
                employeeRepository.findById(id);

        employee.ifPresent(existingEmployee -> {

            boolean accountExists =
                    userRepository
                            .findByEmployeeId(
                                    existingEmployee.getId()
                            )
                            .isPresent();

            existingEmployee.setAccountCreated(
                    accountExists
            );
        });

        return employee;
    }

    /*
     * Update Employee
     */
    public Employee updateEmployee(
            Long id,
            Employee employee) {

        Employee existingEmployee =
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employee not found"
                                )
                        );

        existingEmployee.setEmployeeCode(
                employee.getEmployeeCode()
        );

        existingEmployee.setFirstName(
                employee.getFirstName()
        );

        existingEmployee.setLastName(
                employee.getLastName()
        );

        existingEmployee.setEmail(
                employee.getEmail()
        );

        existingEmployee.setPhone(
                employee.getPhone()
        );

        existingEmployee.setDesignation(
                employee.getDesignation()
        );

        /*
         * Update department
         */
        if (employee.getDepartment() != null
                && employee.getDepartment().getId() != null) {

            Department department =
                    departmentRepository.findById(
                            employee.getDepartment().getId()
                    ).orElseThrow(
                            () -> new RuntimeException(
                                    "Department not found"
                            )
                    );

            existingEmployee.setDepartment(
                    department
            );

        } else {

            existingEmployee.setDepartment(null);
        }

        existingEmployee.setSalary(
                employee.getSalary()
        );

        existingEmployee.setJoiningDate(
                employee.getJoiningDate()
        );

        Employee updatedEmployee =
                employeeRepository.save(
                        existingEmployee
                );

        /*
         * Recalculate account status
         */
        boolean accountExists =
                userRepository
                        .findByEmployeeId(
                                updatedEmployee.getId()
                        )
                        .isPresent();

        updatedEmployee.setAccountCreated(
                accountExists
        );

        return updatedEmployee;
    }

    /*
     * Delete Employee
     */
    public void deleteEmployee(Long id) {

        employeeRepository.deleteById(id);
    }
}