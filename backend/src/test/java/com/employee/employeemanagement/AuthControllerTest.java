package com.employee.employeemanagement;

import com.employee.employeemanagement.controller.AuthController;
import com.employee.employeemanagement.entity.Employee;
import com.employee.employeemanagement.entity.Role;
import com.employee.employeemanagement.entity.User;
import com.employee.employeemanagement.repository.EmployeeRepository;
import com.employee.employeemanagement.service.JwtService;
import com.employee.employeemanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    private void mockAdminAuthentication() {

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN")
                )
        ).when(authentication).getAuthorities();
    }

    @Test
    void register_shouldCreateUserForAdmin() throws Exception {

        mockAdminAuthentication();

        User savedUser = new User();

        savedUser.setId(1L);
        savedUser.setUsername("newadmin");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.ADMIN);

        when(userService.findByUsername("newadmin"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userService.saveUser(any(User.class)))
                .thenReturn(savedUser);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "newadmin",
                                            "password": "password123",
                                            "role": "ADMIN"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isCreated());

        verify(userService)
                .saveUser(any(User.class));

        verify(passwordEncoder)
                .encode("password123");
    }

    @Test
    void register_shouldReturnForbiddenForNonAdmin()
            throws Exception {

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_EMPLOYEE")
                )
        ).when(authentication).getAuthorities();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "password123",
                                            "role": "EMPLOYEE"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void register_shouldReturnForbiddenWithoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "password123",
                                            "role": "EMPLOYEE"
                                        }
                                        """)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
        verifyNoInteractions(employeeRepository);
    }

    @Test
    void register_shouldReturnBadRequestWhenUsernameMissing()
            throws Exception {

        mockAdminAuthentication();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "",
                                            "password": "password123",
                                            "role": "EMPLOYEE"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldReturnBadRequestWhenPasswordMissing()
            throws Exception {

        mockAdminAuthentication();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "",
                                            "role": "EMPLOYEE"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldReturnBadRequestWhenRoleMissing()
            throws Exception {

        mockAdminAuthentication();

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void register_shouldReturnBadRequestWhenUsernameAlreadyExists()
            throws Exception {

        mockAdminAuthentication();

        User existingUser = new User();
        existingUser.setUsername("existing");

        when(userService.findByUsername("existing"))
                .thenReturn(Optional.of(existingUser));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "existing",
                                            "password": "password123",
                                            "role": "EMPLOYEE"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verify(userService)
                .findByUsername("existing");

        verify(userService, never())
                .saveUser(any(User.class));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_shouldReturnJwtTokenForValidCredentials()
            throws Exception {

        User user = new User();

        user.setId(1L);
        user.setUsername("manohar");
        user.setPassword("encoded-password");
        user.setRole(Role.EMPLOYEE);

        when(userService.findByUsername("manohar"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "password123",
                "encoded-password"
        ))
                .thenReturn(true);

        when(jwtService.generateToken(
                "manohar",
                "EMPLOYEE"
        ))
                .thenReturn("jwt-token");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "manohar",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(result -> {

                    String response =
                            result.getResponse()
                                    .getContentAsString();

                    if (!"jwt-token".equals(response)) {
                        throw new AssertionError(
                                "Unexpected token: " + response
                        );
                    }
                });

        verify(passwordEncoder)
                .matches(
                        "password123",
                        "encoded-password"
                );

        verify(jwtService)
                .generateToken(
                        "manohar",
                        "EMPLOYEE"
                );
    }

    @Test
    void login_shouldReturnUnauthorizedWhenUserDoesNotExist()
            throws Exception {

        when(userService.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "unknown",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());

        verify(userService)
                .findByUsername("unknown");

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    @Test
    void login_shouldReturnUnauthorizedWhenPasswordIsWrong()
            throws Exception {

        User user = new User();

        user.setUsername("manohar");
        user.setPassword("encoded-password");
        user.setRole(Role.EMPLOYEE);

        when(userService.findByUsername("manohar"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrong-password",
                "encoded-password"
        ))
                .thenReturn(false);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "manohar",
                                            "password": "wrong-password"
                                        }
                                        """)
                )
                .andExpect(status().isUnauthorized());

        verify(passwordEncoder)
                .matches(
                        "wrong-password",
                        "encoded-password"
                );

        verifyNoInteractions(jwtService);
    }

    @Test
    void createEmployeeAccount_shouldCreateEmployeeAccountForAdmin()
            throws Exception {

        mockAdminAuthentication();

        Employee employee = new Employee();

        User savedUser = new User();

        savedUser.setId(2L);
        savedUser.setUsername("employee001");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.EMPLOYEE);
        savedUser.setEmployee(employee);

        when(userService.findByUsername("employee001"))
                .thenReturn(Optional.empty());

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(userService.employeeHasAccount(1L))
                .thenReturn(false);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userService.saveUser(any(User.class)))
                .thenReturn(savedUser);

        mockMvc.perform(
                        post("/api/auth/create-employee-account/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "employee001",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isCreated());

        verify(userService)
                .saveUser(any(User.class));

        verify(passwordEncoder)
                .encode("password123");
    }

    @Test
    void createEmployeeAccount_shouldReturnForbiddenForNonAdmin()
            throws Exception {

        doReturn(
                List.of(
                        new SimpleGrantedAuthority("ROLE_EMPLOYEE")
                )
        ).when(authentication).getAuthorities();

        mockMvc.perform(
                        post("/api/auth/create-employee-account/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "employee001",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
        verifyNoInteractions(employeeRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createEmployeeAccount_shouldReturnBadRequestWhenUsernameMissing()
            throws Exception {

        mockAdminAuthentication();

        mockMvc.perform(
                        post("/api/auth/create-employee-account/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void createEmployeeAccount_shouldReturnBadRequestWhenPasswordMissing()
            throws Exception {

        mockAdminAuthentication();

        mockMvc.perform(
                        post("/api/auth/create-employee-account/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "employee001",
                                            "password": ""
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void createEmployeeAccount_shouldReturnBadRequestWhenUsernameExists()
            throws Exception {

        mockAdminAuthentication();

        User existingUser = new User();
        existingUser.setUsername("employee001");

        when(userService.findByUsername("employee001"))
                .thenReturn(Optional.of(existingUser));

        mockMvc.perform(
                        post("/api/auth/create-employee-account/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "employee001",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verify(userService)
                .findByUsername("employee001");

        verify(userService, never())
                .saveUser(any(User.class));

        verifyNoInteractions(employeeRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createEmployeeAccount_shouldReturnNotFoundWhenEmployeeMissing()
            throws Exception {

        mockAdminAuthentication();

        when(userService.findByUsername("employee001"))
                .thenReturn(Optional.empty());

        when(employeeRepository.findById(99L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/auth/create-employee-account/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "employee001",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isNotFound());

        verify(employeeRepository)
                .findById(99L);

        verify(userService, never())
                .saveUser(any(User.class));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createEmployeeAccount_shouldReturnBadRequestWhenEmployeeAlreadyHasAccount()
            throws Exception {

        mockAdminAuthentication();

        Employee employee = new Employee();

        when(userService.findByUsername("employee001"))
                .thenReturn(Optional.empty());

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        when(userService.employeeHasAccount(1L))
                .thenReturn(true);

        mockMvc.perform(
                        post("/api/auth/create-employee-account/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "employee001",
                                            "password": "password123"
                                        }
                                        """)
                                .principal(authentication)
                )
                .andExpect(status().isBadRequest());

        verify(userService)
                .employeeHasAccount(1L);

        verify(userService, never())
                .saveUser(any(User.class));

        verifyNoInteractions(passwordEncoder);
    }
}