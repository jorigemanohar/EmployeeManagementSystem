package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.EmployeeController;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .build();
    }

    @Test
    void addEmployee_shouldReturnCreatedEmployee() throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("Manohar");
        employee.setLastName("Jorige");
        employee.setEmail("manohar@example.com");
        employee.setSalary(35000.0);

        when(employeeService.addEmployee(any(Employee.class)))
                .thenReturn(employee);

        mockMvc.perform(
                        post("/api/employees")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "employeeCode": "EMP001",
                                            "firstName": "Manohar",
                                            "lastName": "Jorige",
                                            "email": "manohar@example.com",
                                            "salary": 35000.0
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeCode").value("EMP001"))
                .andExpect(jsonPath("$.firstName").value("Manohar"));

        verify(employeeService)
                .addEmployee(any(Employee.class));
    }

    @Test
    void getAllEmployees_shouldReturnEmployeeList() throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("Manohar");

        when(employeeService.getAllEmployees())
                .thenReturn(List.of(employee));

        mockMvc.perform(
                        get("/api/employees")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].employeeCode")
                        .value("EMP001"))
                .andExpect(jsonPath("$[0].firstName")
                        .value("Manohar"));

        verify(employeeService)
                .getAllEmployees();
    }

    @Test
    void getEmployeeById_shouldReturnEmployeeWhenFound()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("Manohar");

        when(employeeService.getEmployeeById(1L))
                .thenReturn(Optional.of(employee));

        mockMvc.perform(
                        get("/api/employees/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employeeCode")
                        .value("EMP001"));

        verify(employeeService)
                .getEmployeeById(1L);
    }

    @Test
    void getEmployeeById_shouldReturnNotFoundWhenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.getEmployeeById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/employees/99")
                )
                .andExpect(status().isNotFound());

        verify(employeeService)
                .getEmployeeById(99L);
    }

    @Test
    void updateEmployee_shouldReturnUpdatedEmployee()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP002");
        employee.setFirstName("Updated");
        employee.setLastName("Employee");
        employee.setEmail("updated@example.com");
        employee.setSalary(50000.0);

        when(employeeService.updateEmployee(
                eq(1L),
                any(Employee.class)
        )).thenReturn(employee);

        mockMvc.perform(
                        put("/api/employees/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "employeeCode": "EMP002",
                                            "firstName": "Updated",
                                            "lastName": "Employee",
                                            "email": "updated@example.com",
                                            "salary": 50000.0
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeCode")
                        .value("EMP002"))
                .andExpect(jsonPath("$.firstName")
                        .value("Updated"))
                .andExpect(jsonPath("$.salary")
                        .value(50000.0));

        verify(employeeService)
                .updateEmployee(eq(1L), any(Employee.class));
    }

    @Test
    void deleteEmployee_shouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(employeeService)
                .deleteEmployee(1L);

        mockMvc.perform(
                        delete("/api/employees/1")
                )
                .andExpect(status().isNoContent());

        verify(employeeService)
                .deleteEmployee(1L);
    }
}