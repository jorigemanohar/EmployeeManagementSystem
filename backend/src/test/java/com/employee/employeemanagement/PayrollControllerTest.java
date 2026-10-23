package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.PayrollController;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Payroll;
import com.employee.employeemanagement.entity.PaymentStatus;
import com.employee.employeemanagement.service.PayrollPdfService;
import com.employee.employeemanagement.service.PayrollService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PayrollControllerTest {

    @Mock
    private PayrollService payrollService;

    @Mock
    private PayrollPdfService payrollPdfService;

    @InjectMocks
    private PayrollController payrollController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(payrollController)
                .build();
    }

    private Payroll createPayroll(Long id) {
        Employee employee = new Employee();
        employee.setId(10L);
        employee.setEmployeeCode("EMP001");

        Payroll payroll = new Payroll();
        payroll.setId(id);
        payroll.setEmployee(employee);
        payroll.setMonth("2026-10");
        payroll.setBasicSalary(30000.0);
        payroll.setAllowances(5000.0);
        payroll.setDeductions(2000.0);
        payroll.setNetSalary(33000.0);
        payroll.setPaymentStatus(PaymentStatus.PAID);

        return payroll;
    }

    private String validPayrollJson() {
        return """
                {
                    "month": "2026-10",
                    "basicSalary": 30000.0,
                    "allowances": 5000.0,
                    "deductions": 2000.0,
                    "netSalary": 33000.0,
                    "paymentStatus": "PAID"
                }
                """;
    }

    @Test
    void addPayroll_shouldReturnSavedPayroll() throws Exception {

        Payroll savedPayroll = createPayroll(1L);

        when(payrollService.addPayroll(any(Payroll.class)))
                .thenReturn(savedPayroll);

        mockMvc.perform(
                        post("/api/payroll")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayrollJson())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.month").value("2026-10"))
                .andExpect(jsonPath("$.basicSalary").value(30000.0))
                .andExpect(jsonPath("$.netSalary").value(33000.0))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        verify(payrollService)
                .addPayroll(any(Payroll.class));
    }

    @Test
    void addPayroll_shouldReturnBadRequestWhenServiceThrowsException()
            throws Exception {

        when(payrollService.addPayroll(any(Payroll.class)))
                .thenThrow(new RuntimeException("Employee not found"));

        mockMvc.perform(
                        post("/api/payroll")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayrollJson())
                )
                .andExpect(status().isBadRequest());

        verify(payrollService)
                .addPayroll(any(Payroll.class));

        verifyNoInteractions(payrollPdfService);
    }

    @Test
    void addPayroll_shouldReturnBadRequestWhenRequiredFieldsAreMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/payroll")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "month": null,
                                            "basicSalary": null,
                                            "paymentStatus": null
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(payrollService, payrollPdfService);
    }

    @Test
    void getAllPayrolls_shouldReturnAllPayrollRecords()
            throws Exception {

        List<Payroll> payrolls = List.of(
                createPayroll(1L),
                createPayroll(2L)
        );

        when(payrollService.getAllPayrolls())
                .thenReturn(payrolls);

        mockMvc.perform(get("/api/payroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        verify(payrollService).getAllPayrolls();
    }

    @Test
    void getAllPayrolls_shouldReturnEmptyListWhenNoRecordsExist()
            throws Exception {

        when(payrollService.getAllPayrolls())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/payroll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(payrollService).getAllPayrolls();
    }

    @Test
    void getPayrollById_shouldReturnPayrollWhenFound()
            throws Exception {

        Payroll payroll = createPayroll(1L);

        when(payrollService.getPayrollById(1L))
                .thenReturn(Optional.of(payroll));

        mockMvc.perform(get("/api/payroll/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.month").value("2026-10"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        verify(payrollService).getPayrollById(1L);
    }

    @Test
    void getPayrollById_shouldReturnNotFoundWhenMissing()
            throws Exception {

        when(payrollService.getPayrollById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/payroll/99"))
                .andExpect(status().isNotFound());

        verify(payrollService).getPayrollById(99L);

        verifyNoInteractions(payrollPdfService);
    }

    @Test
    void updatePayroll_shouldReturnUpdatedPayroll()
            throws Exception {

        Payroll updatedPayroll = createPayroll(1L);
        updatedPayroll.setBasicSalary(35000.0);
        updatedPayroll.setNetSalary(38000.0);

        when(payrollService.updatePayroll(
                org.mockito.ArgumentMatchers.eq(1L),
                any(Payroll.class)
        )).thenReturn(updatedPayroll);

        mockMvc.perform(
                        put("/api/payroll/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayrollJson())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.basicSalary").value(35000.0))
                .andExpect(jsonPath("$.netSalary").value(38000.0));

        verify(payrollService)
                .updatePayroll(
                        org.mockito.ArgumentMatchers.eq(1L),
                        any(Payroll.class)
                );
    }

    @Test
    void updatePayroll_shouldReturnNotFoundWhenPayrollDoesNotExist()
            throws Exception {

        when(payrollService.updatePayroll(
                org.mockito.ArgumentMatchers.eq(99L),
                any(Payroll.class)
        )).thenThrow(new RuntimeException("Payroll not found"));

        mockMvc.perform(
                        put("/api/payroll/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayrollJson())
                )
                .andExpect(status().isNotFound());

        verify(payrollService)
                .updatePayroll(
                        org.mockito.ArgumentMatchers.eq(99L),
                        any(Payroll.class)
                );
    }

    @Test
    void updatePayroll_shouldReturnBadRequestWhenServiceRejectsUpdate()
            throws Exception {

        when(payrollService.updatePayroll(
                org.mockito.ArgumentMatchers.eq(1L),
                any(Payroll.class)
        )).thenThrow(new RuntimeException("Invalid payroll data"));

        mockMvc.perform(
                        put("/api/payroll/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validPayrollJson())
                )
                .andExpect(status().isBadRequest());

        verify(payrollService)
                .updatePayroll(
                        org.mockito.ArgumentMatchers.eq(1L),
                        any(Payroll.class)
                );
    }

    @Test
    void deletePayroll_shouldReturnNoContentWhenSuccessful()
            throws Exception {

        mockMvc.perform(delete("/api/payroll/1"))
                .andExpect(status().isNoContent());

        verify(payrollService).deletePayroll(1L);
    }

    @Test
    void deletePayroll_shouldReturnNotFoundWhenPayrollDoesNotExist()
            throws Exception {

        doThrow(new RuntimeException("Payroll not found"))
                .when(payrollService)
                .deletePayroll(99L);

        mockMvc.perform(delete("/api/payroll/99"))
                .andExpect(status().isNotFound());

        verify(payrollService).deletePayroll(99L);
    }

    @Test
    void deletePayroll_shouldReturnBadRequestWhenDeletionFails()
            throws Exception {

        doThrow(new RuntimeException("Cannot delete payroll"))
                .when(payrollService)
                .deletePayroll(1L);

        mockMvc.perform(delete("/api/payroll/1"))
                .andExpect(status().isBadRequest());

        verify(payrollService).deletePayroll(1L);
    }

    @Test
    void generateSalarySlip_shouldReturnPdfWhenPayrollExists()
            throws Exception {

        Payroll payroll = createPayroll(1L);

        byte[] pdfBytes = {
                37, 80, 68, 70, 45, 49, 46, 52
        };

        when(payrollService.getPayrollById(1L))
                .thenReturn(Optional.of(payroll));

        when(payrollPdfService.generateSalarySlip(payroll))
                .thenReturn(pdfBytes);

        mockMvc.perform(get("/api/payroll/1/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfBytes))
                .andExpect(
                        header().longValue(
                                "Content-Length",
                                pdfBytes.length
                        )
                )
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                "attachment; filename=\"salary-slip-EMP001-2026-10.pdf\""
                        )
                );

        verify(payrollService).getPayrollById(1L);
        verify(payrollPdfService).generateSalarySlip(payroll);
    }

    @Test
    void generateSalarySlip_shouldReturnNotFoundWhenPayrollIsMissing()
            throws Exception {

        when(payrollService.getPayrollById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/payroll/99/pdf"))
                .andExpect(status().isNotFound());

        verify(payrollService).getPayrollById(99L);

        verifyNoInteractions(payrollPdfService);
    }
}