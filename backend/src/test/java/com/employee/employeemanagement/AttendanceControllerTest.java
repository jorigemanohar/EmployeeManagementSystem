package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.AttendanceController;
import com.employee.employeemanagement.entity.Attendance;
import com.employee.employeemanagement.entity.AttendanceStatus;
import com.employee.employeemanagement.service.AttendanceService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(attendanceController)
                .build();
    }

    @Test
    void addAttendance_shouldReturnSavedAttendance()
            throws Exception {

        Attendance attendance = new Attendance();

        attendance.setId(1L);
        attendance.setAttendanceDate(
                LocalDate.of(2026, 10, 4)
        );
        attendance.setStatus(AttendanceStatus.PRESENT);

        when(attendanceService.addAttendance(any(Attendance.class)))
                .thenReturn(attendance);

        mockMvc.perform(
                        post("/api/attendance")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "attendanceDate": "2026-10-04",
                                            "status": "PRESENT"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.attendanceDate")
                        .value("2026-10-04"))
                .andExpect(jsonPath("$.status")
                        .value("PRESENT"));

        verify(attendanceService)
                .addAttendance(any(Attendance.class));
    }

    @Test
    void addAttendance_shouldReturnBadRequestWhenServiceFails()
            throws Exception {

        when(attendanceService.addAttendance(any(Attendance.class)))
                .thenThrow(
                        new RuntimeException(
                                "Attendance already exists"
                        )
                );

        mockMvc.perform(
                        post("/api/attendance")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "attendanceDate": "2026-10-04",
                                            "status": "PRESENT"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        result -> result.getResponse()
                                .getContentAsString()
                                .equals("Attendance already exists")
                );

        verify(attendanceService)
                .addAttendance(any(Attendance.class));
    }

    @Test
    void getAllAttendance_shouldReturnAttendanceList()
            throws Exception {

        Attendance attendance = new Attendance();

        attendance.setId(1L);
        attendance.setAttendanceDate(
                LocalDate.of(2026, 10, 4)
        );
        attendance.setStatus(AttendanceStatus.PRESENT);

        when(attendanceService.getAllAttendance())
                .thenReturn(List.of(attendance));

        mockMvc.perform(
                        get("/api/attendance")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status")
                        .value("PRESENT"));

        verify(attendanceService)
                .getAllAttendance();
    }

    @Test
    void getAttendanceById_shouldReturnAttendanceWhenFound()
            throws Exception {

        Attendance attendance = new Attendance();

        attendance.setId(1L);
        attendance.setAttendanceDate(
                LocalDate.of(2026, 10, 4)
        );
        attendance.setStatus(AttendanceStatus.ABSENT);

        when(attendanceService.getAttendanceById(1L))
                .thenReturn(Optional.of(attendance));

        mockMvc.perform(
                        get("/api/attendance/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status")
                        .value("ABSENT"));

        verify(attendanceService)
                .getAttendanceById(1L);
    }

    @Test
    void getAttendanceById_shouldReturnNotFoundWhenMissing()
            throws Exception {

        when(attendanceService.getAttendanceById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        get("/api/attendance/99")
                )
                .andExpect(status().isNotFound());

        verify(attendanceService)
                .getAttendanceById(99L);
    }

    @Test
    void updateAttendance_shouldReturnUpdatedAttendance()
            throws Exception {

        Attendance attendance = new Attendance();

        attendance.setId(1L);
        attendance.setAttendanceDate(
                LocalDate.of(2026, 10, 4)
        );
        attendance.setStatus(AttendanceStatus.LEAVE);

        when(attendanceService.updateAttendance(
                eq(1L),
                any(Attendance.class)
        )).thenReturn(attendance);

        mockMvc.perform(
                        put("/api/attendance/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "attendanceDate": "2026-10-04",
                                            "status": "LEAVE"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status")
                        .value("LEAVE"));

        verify(attendanceService)
                .updateAttendance(
                        eq(1L),
                        any(Attendance.class)
                );
    }

    @Test
    void updateAttendance_shouldReturnNotFoundForMissingAttendance()
            throws Exception {

        when(attendanceService.updateAttendance(
                eq(99L),
                any(Attendance.class)
        )).thenThrow(
                new RuntimeException("Attendance not found")
        );

        mockMvc.perform(
                        put("/api/attendance/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "attendanceDate": "2026-10-04",
                                            "status": "PRESENT"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        verify(attendanceService)
                .updateAttendance(
                        eq(99L),
                        any(Attendance.class)
                );
    }

    @Test
    void updateAttendance_shouldReturnNotFoundForMissingEmployee()
            throws Exception {

        when(attendanceService.updateAttendance(
                eq(1L),
                any(Attendance.class)
        )).thenThrow(
                new RuntimeException("Employee not found")
        );

        mockMvc.perform(
                        put("/api/attendance/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "attendanceDate": "2026-10-04",
                                            "status": "PRESENT"
                                        }
                                        """)
                )
                .andExpect(status().isNotFound());

        verify(attendanceService)
                .updateAttendance(
                        eq(1L),
                        any(Attendance.class)
                );
    }

    @Test
    void updateAttendance_shouldReturnBadRequestForOtherError()
            throws Exception {

        when(attendanceService.updateAttendance(
                eq(1L),
                any(Attendance.class)
        )).thenThrow(
                new RuntimeException("Invalid attendance data")
        );

        mockMvc.perform(
                        put("/api/attendance/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "attendanceDate": "2026-10-04",
                                            "status": "PRESENT"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(attendanceService)
                .updateAttendance(
                        eq(1L),
                        any(Attendance.class)
                );
    }

    @Test
    void deleteAttendance_shouldReturnNoContent()
            throws Exception {

        doNothing()
                .when(attendanceService)
                .deleteAttendance(1L);

        mockMvc.perform(
                        delete("/api/attendance/1")
                )
                .andExpect(status().isNoContent());

        verify(attendanceService)
                .deleteAttendance(1L);
    }

    @Test
    void deleteAttendance_shouldReturnNotFoundWhenMissing()
            throws Exception {

        doThrow(
                new RuntimeException("Attendance not found")
        )
                .when(attendanceService)
                .deleteAttendance(99L);

        mockMvc.perform(
                        delete("/api/attendance/99")
                )
                .andExpect(status().isNotFound());

        verify(attendanceService)
                .deleteAttendance(99L);
    }

    @Test
    void deleteAttendance_shouldReturnBadRequestForOtherError()
            throws Exception {

        doThrow(
                new RuntimeException("Cannot delete attendance")
        )
                .when(attendanceService)
                .deleteAttendance(1L);

        mockMvc.perform(
                        delete("/api/attendance/1")
                )
                .andExpect(status().isBadRequest());

        verify(attendanceService)
                .deleteAttendance(1L);
    }
}