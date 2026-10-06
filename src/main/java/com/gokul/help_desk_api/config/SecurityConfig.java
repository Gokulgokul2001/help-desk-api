package com.gokul.help_desk_api.config;

import com.gokul.help_desk_api.security.JwtAccessDeniedHandler;
import com.gokul.help_desk_api.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.gokul.help_desk_api.security.JwtAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // Disable CSRF because this is a stateless REST API
                .csrf(csrf -> csrf.disable())

                // Handle authentication errors
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // JWT-based authentication
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // Authentication APIs
                        // ==============================
                        .requestMatchers("/api/auth/**", "/error")
                        .permitAll()


                        // ==============================
                        // Category APIs
                        // ==============================

                        // Get all categories
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Get category by ID
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories/*"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Create category
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories"
                        )
                        .hasRole("ADMIN")

                        // Update category
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/*"
                        )
                        .hasRole("ADMIN")

                        // Deactivate category
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/categories/*/deactivate"
                        )
                        .hasRole("ADMIN")


                        // ==============================
                        // Ticket APIs
                        // ==============================

                        // Create ticket
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/tickets"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Ticket attachment upload
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/tickets/*/attachments"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Get ticket attachments
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/tickets/*/attachments"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Ticket attachment deletion
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/tickets/*/attachments/*"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Ticket assignment
                        .requestMatchers(
                                "/api/tickets/*/assign"
                        )
                        .hasAnyRole(
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Ticket status update
                        .requestMatchers(
                                "/api/tickets/*/status"
                        )
                        .hasAnyRole(
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Ticket update
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/tickets/*"
                        )
                        .hasAnyRole(
                                "IT_SUPPORT",
                                "ADMIN"
                        )

                        // Ticket deletion
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/tickets/*"
                        )
                        .hasRole("ADMIN")

                        // Ticket viewing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/tickets/**"
                        )
                        .hasAnyRole(
                                "EMPLOYEE",
                                "IT_SUPPORT",
                                "ADMIN"
                        )


                        // ==============================
                        // Everything else
                        // ==============================
                        .anyRequest()
                        .authenticated()
                )

                // Add JWT filter before Spring's
                // UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}