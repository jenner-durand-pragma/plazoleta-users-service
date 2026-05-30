package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.plazoleta.application.dto.request.auth.LoginRequestDto;
import com.pragma.plazoleta.application.dto.response.auth.LoginResponseDto;
import com.pragma.plazoleta.application.handler.IAuthHandler;
import com.pragma.plazoleta.domain.exception.auth.InvalidCredentialsException;
import com.pragma.plazoleta.infrastructure.configuration.SecurityConfiguration;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAccessDeniedHandler;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAuthenticationEntryPoint;
import com.pragma.plazoleta.infrastructure.configuration.security.CustomAuthenticationFilter;
import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.AuthenticatedUser;
import com.pragma.plazoleta.infrastructure.exceptionhandler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IAuthHandler authHandler;

    private ObjectMapper objectMapper;
    private LoginRequestDto validRequest;
    private UsernamePasswordAuthenticationToken userAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        validRequest = LoginRequestDto.builder()
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();

        var principal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "ADMIN");
        userAuthentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    @Test
    @DisplayName("Should return 200 with token when credentials are valid")
    void shouldReturn200WithToken() throws Exception {
        var response = LoginResponseDto.builder()
                .token("jwt.token.example")
                .tokenType("Bearer")
                .build();
        when(authHandler.login(any(LoginRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.example"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void shouldReturn401() throws Exception {
        when(authHandler.login(any(LoginRequestDto.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return 400 when email format is invalid")
    void shouldReturn400OnBadEmail() throws Exception {
        validRequest.setEmail("invalid.email");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(authentication(userAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }
}
