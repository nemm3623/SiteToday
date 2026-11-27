package com.example.sitetoday.common.config;

import com.example.sitetoday.common.exception.CommonErrorCode;
import com.example.sitetoday.common.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    SecurityFilterChain security(HttpSecurity http, ObjectMapper mapper) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                        .requestMatchers("/api/offices", "/api/offices/**").hasRole("EMPLOYER")
                        .anyRequest().authenticated())
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((req, res, ex) -> writeError(mapper, res,
                                CommonErrorCode.AUTHENTICATION_REQUIRED))
                        .accessDeniedHandler((req, res, ex) -> writeError(mapper, res,
                                CommonErrorCode.ACCESS_DENIED)))
                .formLogin(login -> login.loginProcessingUrl("/api/auth/login").usernameParameter("email")
                        .successHandler((req, res, auth) -> res.setStatus(204))
                        .failureHandler((req, res, ex) -> writeError(mapper, res,
                                CommonErrorCode.INVALID_CREDENTIALS)).permitAll())
                .build();
    }

    private void writeError(ObjectMapper mapper, HttpServletResponse response, CommonErrorCode error) throws IOException {
        response.setStatus(ErrorResponse.statusFor(error).value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getOutputStream(), ErrorResponse.from(error));
    }
}
