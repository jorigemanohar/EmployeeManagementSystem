package com.employee.employeemanagement.config;

import com.employee.employeemanagement.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println();
        System.out.println("========== JWT FILTER ==========");
        System.out.println(
                "REQUEST: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "AUTH HEADER PRESENT: "
                        + (authHeader != null)
        );

        /*
         * No Authorization header.
         * Allow the request to continue.
         *
         * This is important for:
         * POST /api/auth/login
         */
        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "TOKEN RECEIVED: NO"
            );

            System.out.println(
                    "REQUEST CONTINUES WITHOUT JWT"
            );

            System.out.println(
                    "================================"
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authHeader.substring(7);

        System.out.println(
                "TOKEN RECEIVED: YES"
        );

        try {

            String username =
                    jwtService.extractUsername(token);

            System.out.println(
                    "JWT USERNAME: "
                            + username
            );

            if (username != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username
                                );

                System.out.println(
                        "USER FOUND: "
                                + userDetails.getUsername()
                );

                System.out.println(
                        "AUTHORITIES: "
                                + userDetails.getAuthorities()
                );

                boolean valid =
                        jwtService.isTokenValid(token);

                System.out.println(
                        "JWT VALID: "
                                + valid
                );

                if (valid) {

                    UsernamePasswordAuthenticationToken
                            authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(
                                    authentication
                            );

                    System.out.println(
                            "AUTHENTICATION SET SUCCESSFULLY"
                    );

                    System.out.println(
                            "CURRENT AUTH: "
                                    + SecurityContextHolder
                                    .getContext()
                                    .getAuthentication()
                    );

                } else {

                    System.out.println(
                            "JWT IS INVALID"
                    );
                }

            }

        } catch (Exception exception) {

            System.out.println(
                    "JWT ERROR: "
                            + exception.getMessage()
            );
        }

        System.out.println(
                "================================"
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}