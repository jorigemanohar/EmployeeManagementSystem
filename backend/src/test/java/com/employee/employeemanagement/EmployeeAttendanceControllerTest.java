package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.EmployeeAttendanceController;
import com.employee.employeemanagement.entity.Attendance;
import com.employee.employeemanagement.entity.AttendanceStatus;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.AttendanceRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeAttendanceControllerTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeAttendanceController employeeAttendanceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeAttendanceController)
                .build();
    }

    @Test
    void getMyAttendance_shouldReturnAttendanceForLoggedInEmployee()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        Attendance attendance = new Attendance();
        attendance.setId(1L);
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(
                LocalDate.of(2026, 10, 4)
        );
        attendance.setStatus(AttendanceStatus.PRESENT);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(attendanceRepository.findByEmployeeId(10L))
                .thenReturn(List.of(attendance));

        mockMvc.perform(
                        get("/api/employee/attendance")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(
                        jsonPath("$[0].attendanceDate")
                                .value("2026-10-04")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("PRESENT")
                );

        verify(userService)
                .getUserByUsername("manohar");

        verify(attendanceRepository)
                .findByEmployeeId(10L);
    }

    @Test
    void getMyAttendance_shouldReturnEmptyListWhenNoAttendanceExists()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(attendanceRepository.findByEmployeeId(10L))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/employee/attendance")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(attendanceRepository)
                .findByEmployeeId(10L);
    }

    @Test
    void getMyAttendance_shouldThrowExceptionWhenEmployeeIsNotLinked() {

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(null);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> employeeAttendanceController
                                .getMyAttendance(authentication)
                );

        assertEquals(
                "No employee is linked to this user",
                exception.getMessage()
        );

        verify(userService)
                .getUserByUsername("manohar");

        verifyNoInteractions(attendanceRepository);
    }

    @Test
    void getMyAttendance_shouldUseCorrectEmployeeId()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(25L);

        User user = new User();
        user.setUsername("employee25");
        user.setEmployee(employee);

        when(authentication.getName())
                .thenReturn("employee25");

        when(userService.getUserByUsername("employee25"))
                .thenReturn(user);

        when(attendanceRepository.findByEmployeeId(25L))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/employee/attendance")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService)
                .getUserByUsername("employee25");

        verify(attendanceRepository)
                .findByEmployeeId(25L);
    }
}
