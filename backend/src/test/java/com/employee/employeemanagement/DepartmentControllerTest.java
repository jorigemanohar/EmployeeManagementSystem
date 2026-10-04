package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.DepartmentController;
import com.employee.employeemanagement.entity.Department;
import com.employee.employeemanagement.service.DepartmentService;
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
class DepartmentControllerTest {

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(departmentController)
                .build();
    }

    @Test
    void addDepartment_shouldReturnCreatedDepartment()
            throws Exception {

        Department department = new Department();

        department.setId(1L);
        department.setDepartmentName("Engineering");
        department.setDescription("Software Engineering Department");

        when(departmentService.addDepartment(any(Department.class)))
                .thenReturn(department);

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "departmentName": "Engineering",
                                            "description": "Software Engineering Department"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.departmentName")
                        .value("Engineering"))
                .andExpect(jsonPath("$.description")
                        .value("Software Engineering Department"));

        verify(departmentService)
                .addDepartment(any(Department.class));
    }

    @Test
    void getAllDepartments_shouldReturnDepartmentList()
            throws Exception {

        Department department = new Department();

        department.setId(1L);
        department.setDepartmentName("Engineering");
        department.setDescription("Software Engineering Department");

        when(departmentService.getAllDepartments())
                .thenReturn(List.of(department));

        mockMvc.perform(
                        get("/api/departments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].departmentName")
                        .value("Engineering"));

        verify(departmentService)
                .getAllDepartments();
    }

    @Test
    void getDepartmentById_shouldReturnDepartmentWhenFound()
            throws Exception {

        Department department = new Department();

        department.setId(1L);
        department.setDepartmentName("Engineering");
        department.setDescription("Software Engineering Department");

        when(departmentService.getDepartmentById(1L))
                .thenReturn(Optional.of(department));

        mockMvc.perform(
                        get("/api/departments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.departmentName")
                        .value("Engineering"));

        verify(departmentService)
                .getDepartmentById(1L);
    }

    @Test
    void getDepartmentById_shouldReturnNotFoundWhenDepartmentDoesNotExist()
            throws Exception {

        when(departmentService.getDepartmentById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/departments/99")
                )
                .andExpect(status().isNotFound());

        verify(departmentService)
                .getDepartmentById(99L);
    }

    @Test
    void updateDepartment_shouldReturnUpdatedDepartment()
            throws Exception {

        Department department = new Department();

        department.setId(1L);
        department.setDepartmentName("Human Resources");
        department.setDescription("HR Department");

        when(departmentService.updateDepartment(
                eq(1L),
                any(Department.class)
        )).thenReturn(department);

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "departmentName": "Human Resources",
                                            "description": "HR Department"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.departmentName")
                        .value("Human Resources"))
                .andExpect(jsonPath("$.description")
                        .value("HR Department"));

        verify(departmentService)
                .updateDepartment(
                        eq(1L),
                        any(Department.class)
                );
    }

    @Test
    void updateDepartment_shouldReturnNotFoundWhenDepartmentDoesNotExist()
            throws Exception {

        when(departmentService.updateDepartment(
                eq(99L),
                any(Department.class)
        )).thenThrow(
                new RuntimeException("Department not found")
        );

        mockMvc.perform(
                        put("/api/departments/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "departmentName": "Finance",
                                            "description": "Finance Department"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        verify(departmentService)
                .updateDepartment(
                        eq(99L),
                        any(Department.class)
                );
    }

    @Test
    void deleteDepartment_shouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(departmentService)
                .deleteDepartment(1L);

        mockMvc.perform(
                        delete("/api/departments/1")
                )
                .andExpect(status().isNoContent());

        verify(departmentService)
                .deleteDepartment(1L);
    }

    @Test
    void deleteDepartment_shouldReturnNotFoundWhenDepartmentDoesNotExist()
            throws Exception {

        doThrow(
                new RuntimeException("Department not found")
        )
                .when(departmentService)
                .deleteDepartment(99L);

        mockMvc.perform(
                        delete("/api/departments/99")
                )
                .andExpect(status().isNotFound());

        verify(departmentService)
                .deleteDepartment(99L);
    }
}