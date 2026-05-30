package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.auth.LoginRequestDto;
import com.pragma.plazoleta.application.handler.impl.AuthHandler;
import com.pragma.plazoleta.domain.api.IAuthServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

    @Mock
    private IAuthServicePort authServicePort;

    @InjectMocks
    private AuthHandler authHandler;

    private LoginRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = LoginRequestDto.builder()
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        var exampleToken = "jwt.token.example";

        when(authServicePort.login(any(String.class), any(String.class)))
                .thenReturn(exampleToken);

        var result = authHandler.login(requestDto);

        assertThat(result.getToken()).isEqualTo(exampleToken);
        assertThat(result.getTokenType()).isEqualTo("Bearer");
    }
}
