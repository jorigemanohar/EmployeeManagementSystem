package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.LeaveController;
import com.employee.employeemanagement.entity.Leave;
import com.employee.employeemanagement.entity.LeaveStatus;
import com.employee.employeemanagement.entity.LeaveType;
import com.employee.employeemanagement.service.LeaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LeaveControllerTest {

    @Mock
    private LeaveService leaveService;

    @InjectMocks
    private LeaveController leaveController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(leaveController)
                .build();
    }

    @Test
    void addLeave_shouldReturnSavedLeave() throws Exception {

        Leave leave = new Leave();

        leave.setId(1L);
        leave.setLeaveType(LeaveType.CASUAL);
        leave.setStartDate(LocalDate.of(2026, 10, 5));
        leave.setEndDate(LocalDate.of(2026, 10, 7));
        leave.setReason("Personal work");
        leave.setStatus(LeaveStatus.PENDING);

        when(leaveService.addLeave(any(Leave.class)))
                .thenReturn(leave);

        mockMvc.perform(
                        post("/api/leaves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "leaveType": "CASUAL",
                                            "startDate": "2026-10-05",
                                            "endDate": "2026-10-07",
                                            "reason": "Personal work",
                                            "status": "PENDING"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leaveType").value("CASUAL"))
                .andExpect(jsonPath("$.startDate").value("2026-10-05"))
                .andExpect(jsonPath("$.endDate").value("2026-10-07"))
                .andExpect(jsonPath("$.reason").value("Personal work"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(leaveService)
                .addLeave(any(Leave.class));
    }

    @Test
    void getAllLeaves_shouldReturnLeaveList() throws Exception {

        Leave leave = new Leave();

        leave.setId(1L);
        leave.setLeaveType(LeaveType.SICK);
        leave.setStartDate(LocalDate.of(2026, 10, 10));
        leave.setEndDate(LocalDate.of(2026, 10, 11));
        leave.setReason("Not feeling well");
        leave.setStatus(LeaveStatus.PENDING);

        when(leaveService.getAllLeaves())
                .thenReturn(List.of(leave));

        mockMvc.perform(
                        get("/api/leaves")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].leaveType").value("SICK"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(leaveService)
                .getAllLeaves();
    }

    @Test
    void getLeaveById_shouldReturnLeaveWhenFound()
            throws Exception {

        Leave leave = new Leave();

        leave.setId(1L);
        leave.setLeaveType(LeaveType.ANNUAL);
        leave.setStartDate(LocalDate.of(2026, 11, 1));
        leave.setEndDate(LocalDate.of(2026, 11, 5));
        leave.setReason("Vacation");
        leave.setStatus(LeaveStatus.APPROVED);

        when(leaveService.getLeaveById(1L))
                .thenReturn(Optional.of(leave));

        mockMvc.perform(
                        get("/api/leaves/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leaveType").value("ANNUAL"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(leaveService)
                .getLeaveById(1L);
    }

    @Test
    void getLeaveById_shouldReturnNotFoundWhenLeaveDoesNotExist()
            throws Exception {

        when(leaveService.getLeaveById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/leaves/99")
                )
                .andExpect(status().isNotFound());

        verify(leaveService)
                .getLeaveById(99L);
    }

    @Test
    void updateLeave_shouldReturnUpdatedLeave()
            throws Exception {

        Leave leave = new Leave();

        leave.setId(1L);
        leave.setLeaveType(LeaveType.SICK);
        leave.setStartDate(LocalDate.of(2026, 10, 15));
        leave.setEndDate(LocalDate.of(2026, 10, 17));
        leave.setReason("Medical leave");
        leave.setStatus(LeaveStatus.APPROVED);

        when(leaveService.updateLeave(
                org.mockito.ArgumentMatchers.eq(1L),
                any(Leave.class)
        )).thenReturn(leave);

        mockMvc.perform(
                        put("/api/leaves/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "leaveType": "SICK",
                                            "startDate": "2026-10-15",
                                            "endDate": "2026-10-17",
                                            "reason": "Medical leave",
                                            "status": "APPROVED"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.leaveType").value("SICK"))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(leaveService)
                .updateLeave(
                        org.mockito.ArgumentMatchers.eq(1L),
                        any(Leave.class)
                );
    }

    @Test
    void deleteLeave_shouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(leaveService)
                .deleteLeave(1L);

        mockMvc.perform(
                        delete("/api/leaves/1")
                )
                .andExpect(status().isNoContent());

        verify(leaveService)
                .deleteLeave(1L);
    }
}