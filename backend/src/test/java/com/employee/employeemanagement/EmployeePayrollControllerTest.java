package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.EmployeePayrollController;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Payroll;
import com.employee.employeemanagement.entity.PaymentStatus;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.PayrollRepository;
import com.employee.employeemanagement.service.PayrollPdfService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmployeePayrollControllerTest {

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private UserService userService;

    @Mock
    private PayrollPdfService payrollPdfService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmployeePayrollController employeePayrollController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(employeePayrollController)
                .build();
    }

    @Test
    void getMyPayroll_shouldReturnPayrollForLoggedInEmployee()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setEmployeeCode("EMP001");

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        Payroll payroll = new Payroll();
        payroll.setId(1L);
        payroll.setEmployee(employee);
        payroll.setMonth("2026-10");
        payroll.setBasicSalary(30000.0);
        payroll.setAllowances(5000.0);
        payroll.setDeductions(2000.0);
        payroll.setNetSalary(33000.0);
        payroll.setPaymentStatus(PaymentStatus.PAID);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(payrollRepository.findByEmployeeId(10L))
                .thenReturn(List.of(payroll));

        mockMvc.perform(
                        get("/api/employee/payroll")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].month").value("2026-10"))
                .andExpect(jsonPath("$[0].basicSalary").value(30000.0))
                .andExpect(jsonPath("$[0].allowances").value(5000.0))
                .andExpect(jsonPath("$[0].deductions").value(2000.0))
                .andExpect(jsonPath("$[0].netSalary").value(33000.0))
                .andExpect(jsonPath("$[0].paymentStatus").value("PAID"));

        verify(userService)
                .getUserByUsername("manohar");

        verify(payrollRepository)
                .findByEmployeeId(10L);
    }

    @Test
    void getMyPayroll_shouldReturnEmptyListWhenNoPayrollExists()
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

        when(payrollRepository.findByEmployeeId(10L))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/api/employee/payroll")
                                .principal(authentication)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userService)
                .getUserByUsername("manohar");

        verify(payrollRepository)
                .findByEmployeeId(10L);
    }

    @Test
    void getMyPayroll_shouldThrowExceptionWhenEmployeeIsNotLinked() {

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
                        () -> employeePayrollController
                                .getMyPayroll(authentication)
                );

        assertEquals(
                "No employee is linked to this user",
                exception.getMessage()
        );

        verify(userService)
                .getUserByUsername("manohar");

        verifyNoInteractions(payrollRepository);
    }

    @Test
    void downloadMySalarySlip_shouldReturnPdfForOwnedPayroll()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setEmployeeCode("EMP001");

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        Payroll payroll = new Payroll();
        payroll.setId(1L);
        payroll.setEmployee(employee);
        payroll.setMonth("2026-10");
        payroll.setBasicSalary(30000.0);
        payroll.setAllowances(5000.0);
        payroll.setDeductions(2000.0);
        payroll.setNetSalary(33000.0);
        payroll.setPaymentStatus(PaymentStatus.PAID);

        byte[] pdfBytes = {
                37, 80, 68, 70, 45, 49, 46, 52
        };

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(payrollRepository.findByEmployeeId(10L))
                .thenReturn(List.of(payroll));

        when(payrollPdfService.generateSalarySlip(payroll))
                .thenReturn(pdfBytes);

        mockMvc.perform(
                        get("/api/employee/payroll/1/pdf")
                                .principal(authentication)
                )
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

        verify(userService)
                .getUserByUsername("manohar");

        verify(payrollRepository)
                .findByEmployeeId(10L);

        verify(payrollPdfService)
                .generateSalarySlip(payroll);
    }

    @Test
    void downloadMySalarySlip_shouldReturnBadRequestWhenEmployeeIsNotLinked()
            throws Exception {

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(null);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        mockMvc.perform(
                        get("/api/employee/payroll/1/pdf")
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verify(userService)
                .getUserByUsername("manohar");

        verifyNoInteractions(
                payrollRepository,
                payrollPdfService
        );
    }

    @Test
    void downloadMySalarySlip_shouldReturnForbiddenWhenPayrollDoesNotBelongToEmployee()
            throws Exception {

        Employee employee = new Employee();
        employee.setId(10L);
        employee.setEmployeeCode("EMP001");

        User user = new User();
        user.setUsername("manohar");
        user.setEmployee(employee);

        Payroll anotherPayroll = new Payroll();
        anotherPayroll.setId(2L);
        anotherPayroll.setEmployee(employee);
        anotherPayroll.setMonth("2026-09");
        anotherPayroll.setBasicSalary(30000.0);
        anotherPayroll.setPaymentStatus(PaymentStatus.PENDING);

        when(authentication.getName())
                .thenReturn("manohar");

        when(userService.getUserByUsername("manohar"))
                .thenReturn(user);

        when(payrollRepository.findByEmployeeId(10L))
                .thenReturn(List.of(anotherPayroll));

        mockMvc.perform(
                        get("/api/employee/payroll/1/pdf")
                                .principal(authentication)
                )
                .andExpect(status().isForbidden());

        verify(userService)
                .getUserByUsername("manohar");

        verify(payrollRepository)
                .findByEmployeeId(10L);

        verifyNoInteractions(payrollPdfService);
    }
}