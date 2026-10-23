package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.EmployeeProfileController;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeProfileController employeeProfileController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeProfileController)
                .build();
    }

    @Test
    void getMyProfile_shouldReturnLinkedEmployeeProfile()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(15L);
        employee.setEmployeeCode("EMP015");
        employee.setAccountCreated(false);

        User user = new User();
        user.setUsername("employee15");
        user.setEmployee(employee);

        when(authentication.getName())
                .thenReturn("employee15");

        when(userService.getUserByUsername("employee15"))
                .thenReturn(user);

        mockMvc.perform(
                        get("/api/employee/profile")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.employeeCode")
                        .value("EMP015"))
                .andExpect(jsonPath("$.accountCreated")
                        .value(true));

        verify(userService)
                .getUserByUsername("employee15");

        assertTrue(employee.isAccountCreated());
    }

    @Test
    void getMyProfile_shouldThrowExceptionWhenEmployeeIsNotLinked() {

        User user = new User();
        user.setUsername("employee15");
        user.setEmployee(null);

        when(authentication.getName())
                .thenReturn("employee15");

        when(userService.getUserByUsername("employee15"))
                .thenReturn(user);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> employeeProfileController
                                .getMyProfile(authentication)
                );

        assertEquals(
                "No employee is linked to this user",
                exception.getMessage()
        );

        verify(userService)
                .getUserByUsername("employee15");
    }

    @Test
    void getMyProfile_shouldSetAccountCreatedToTrue() {

        Employee employee = new Employee();
        employee.setId(20L);
        employee.setEmployeeCode("EMP020");
        employee.setAccountCreated(false);

        User user = new User();
        user.setUsername("employee20");
        user.setEmployee(employee);

        when(authentication.getName())
                .thenReturn("employee20");

        when(userService.getUserByUsername("employee20"))
                .thenReturn(user);

        assertFalse(employee.isAccountCreated());

        Employee result =
                employeeProfileController.getMyProfile(authentication);

        assertEquals(20L, result.getId());
        assertEquals("EMP020", result.getEmployeeCode());
        assertTrue(result.isAccountCreated());
    }
}