package com.employee.employeemanagement;

import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.DepartmentRepository;
import com.employee.employeemanagement.repository.EmployeeRepository;
import com.employee.employeemanagement.repository.UserRepository;
import com.employee.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void addEmployee_shouldSaveEmployee() {
        Employee employee = new Employee();

        employee.setEmployeeCode("EMP001");
        employee.setFirstName("Manohar");
        employee.setLastName("Jorige");
        employee.setEmail("manohar@example.com");
        employee.setSalary(35000.0);

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        Employee result = employeeService.addEmployee(employee);

        assertNotNull(result);
        assertEquals("EMP001", result.getEmployeeCode());
        assertEquals("Manohar", result.getFirstName());
        assertFalse(result.isAccountCreated());

        verify(employeeRepository).save(employee);
    }

    @Test
    void getEmployeeById_shouldReturnEmployeeWithoutAccount() {
        Employee employee = new Employee();

        employee.setId(1L);
        employee.setEmployeeCode("EMP001");

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(userRepository.findByEmployeeId(1L))
                .thenReturn(Optional.empty());

        Optional<Employee> result =
                employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertEquals("EMP001", result.get().getEmployeeCode());
        assertFalse(result.get().isAccountCreated());

        verify(employeeRepository).findById(1L);
        verify(userRepository).findByEmployeeId(1L);
    }

    @Test
    void getEmployeeById_shouldMarkAccountCreatedWhenUserExists() {
        Employee employee = new Employee();

        employee.setId(1L);
        employee.setEmployeeCode("EMP001");

        User user = mock(User.class);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(userRepository.findByEmployeeId(1L))
                .thenReturn(Optional.of(user));

        Optional<Employee> result =
                employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent());
        assertTrue(result.get().isAccountCreated());
    }

    @Test
    void getEmployeeById_shouldReturnEmptyWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Employee> result =
                employeeService.getEmployeeById(99L);

        assertTrue(result.isEmpty());

        verify(employeeRepository).findById(99L);
        verify(userRepository, never())
                .findByEmployeeId(anyLong());
    }

    @Test
    void updateEmployee_shouldUpdateEmployee() {
        Employee existingEmployee = new Employee();

        existingEmployee.setId(1L);
        existingEmployee.setEmployeeCode("EMP001");

        Employee updatedData = new Employee();

        updatedData.setEmployeeCode("EMP002");
        updatedData.setFirstName("Updated");
        updatedData.setLastName("Employee");
        updatedData.setEmail("updated@example.com");
        updatedData.setPhone("9876543210");
        updatedData.setDesignation("Senior Developer");
        updatedData.setSalary(50000.0);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(existingEmployee));

        when(employeeRepository.save(existingEmployee))
                .thenReturn(existingEmployee);

        when(userRepository.findByEmployeeId(1L))
                .thenReturn(Optional.empty());

        Employee result =
                employeeService.updateEmployee(
                        1L,
                        updatedData
                );

        assertNotNull(result);
        assertEquals("EMP002", result.getEmployeeCode());
        assertEquals("Updated", result.getFirstName());
        assertEquals(
                "Senior Developer",
                result.getDesignation()
        );
        assertEquals(50000.0, result.getSalary());
        assertFalse(result.isAccountCreated());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(existingEmployee);
        verify(userRepository).findByEmployeeId(1L);
    }

    @Test
    void deleteEmployee_shouldDeleteById() {
        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }
}