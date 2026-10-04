package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.EmployeeLeaveController;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Leave;
import com.employee.employeemanagement.entity.LeaveStatus;
import com.employee.employeemanagement.entity.LeaveType;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.LeaveRepository;
import com.employee.employeemanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeLeaveControllerTest {

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeeLeaveController employeeLeaveController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeLeaveController)
                .build();
    }

    @Test
    void getMyLeaves_shouldReturnLeavesForLoggedInEmployee()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        Leave leave = new Leave();
        leave.setId(1L);
        leave.setEmployee(employee);
        leave.setLeaveType(LeaveType.CASUAL);
        leave.setStartDate(
                LocalDate.of(2026, 10, 5)
        );
        leave.setEndDate(
                LocalDate.of(2026, 10, 7)
        );
        leave.setReason("Personal work");
        leave.setStatus(LeaveStatus.PENDING);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(leaveRepository.findByEmployeeId(10L))
                .thenReturn(List.of(leave));

        mockMvc.perform(
                        get("/api/employee/leaves")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].leaveType")
                                .value("CASUAL")
                )
                .andExpect(
                        jsonPath("$[0].startDate")
                                .value("2026-10-05")
                )
                .andExpect(
                        jsonPath("$[0].endDate")
                                .value("2026-10-07")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("PENDING")
                );

        verify(userService)
                .getUserByUsername("manohar");

        verify(leaveRepository)
                .findByEmployeeId(10L);
    }

    @Test
    void getMyLeaves_shouldReturnEmptyListWhenNoLeavesExist()
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

        when(leaveRepository.findByEmployeeId(10L))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/employee/leaves")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(0)
                );

        verify(userService)
                .getUserByUsername("manohar");

        verify(leaveRepository)
                .findByEmployeeId(10L);
    }

    @Test
    void getMyLeaves_shouldThrowExceptionWhenEmployeeIsNotLinked() {

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
                        () -> employeeLeaveController
                                .getMyLeaves(authentication)
                );

        assertEquals(
                "No employee is linked to this user",
                exception.getMessage()
        );

        verify(userService)
                .getUserByUsername("manohar");

        verifyNoInteractions(leaveRepository);
    }

    @Test
    void submitLeave_shouldSaveLeaveWithEmployeeAndPendingStatus()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        Leave savedLeave = new Leave();
        savedLeave.setId(1L);
        savedLeave.setEmployee(employee);
        savedLeave.setLeaveType(LeaveType.SICK);
        savedLeave.setStartDate(
                LocalDate.of(2026, 10, 10)
        );
        savedLeave.setEndDate(
                LocalDate.of(2026, 10, 12)
        );
        savedLeave.setReason("Medical leave");
        savedLeave.setStatus(LeaveStatus.PENDING);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(leaveRepository.save(any(Leave.class)))
                .thenReturn(savedLeave);

        mockMvc.perform(
                        post("/api/employee/leaves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "leaveType": "SICK",
                                            "startDate": "2026-10-10",
                                            "endDate": "2026-10-12",
                                            "reason": "Medical leave"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.leaveType")
                                .value("SICK")
                )
                .andExpect(
                        jsonPath("$.startDate")
                                .value("2026-10-10")
                )
                .andExpect(
                        jsonPath("$.endDate")
                                .value("2026-10-12")
                )
                .andExpect(
                        jsonPath("$.reason")
                                .value("Medical leave")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("PENDING")
                );

        verify(userService)
                .getUserByUsername("manohar");

        verify(leaveRepository)
                .save(any(Leave.class));
    }

    @Test
    void submitLeave_shouldThrowExceptionWhenEmployeeIsNotLinked() {

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(null);

        Leave leave = new Leave();

        leave.setLeaveType(LeaveType.CASUAL);
        leave.setStartDate(
                LocalDate.of(2026, 10, 5)
        );
        leave.setEndDate(
                LocalDate.of(2026, 10, 6)
        );

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> employeeLeaveController
                                .submitLeave(
                                        authentication,
                                        leave
                                )
                );

        assertEquals(
                "No employee is linked to this user",
                exception.getMessage()
        );

        verify(userService)
                .getUserByUsername("manohar");

        verifyNoInteractions(leaveRepository);
    }

    @Test
    void submitLeave_shouldReturnBadRequestWhenDateRangeIsInvalid()
            throws Exception {

        mockMvc.perform(
                        post("/api/employee/leaves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "leaveType": "CASUAL",
                                            "startDate": "2026-10-10",
                                            "endDate": "2026-10-05",
                                            "reason": "Invalid date range"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(
                authentication,
                userService,
                leaveRepository
        );
    }
}