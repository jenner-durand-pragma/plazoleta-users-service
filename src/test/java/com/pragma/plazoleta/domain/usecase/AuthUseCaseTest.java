package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.exception.auth.InvalidCredentialsException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IPasswordEncoderPort;
import com.pragma.plazoleta.domain.spi.ITokenServicePort;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @Mock
    private ITokenServicePort tokenServicePort;

    @InjectMocks
    private AuthUseCase authUseCase;

    private User existingUser;
    private static final String EMAIL = "jenner.durand@plazoleta.com";
    private static final String RAW_PASSWORD = "PlainPassword123$";
    private static final String HASHED_PASSWORD = "$2a$10$hashedPassword";

    @BeforeEach
    void setUp() {
        var ownerRole = Role.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();

        existingUser = User.builder()
                .id(5L)
                .email(EMAIL)
                .password(HASHED_PASSWORD)
                .role(ownerRole)
                .build();
    }

    @Test
    @DisplayName("Should return a token when credentials are valid")
    void shouldReturnTokenWhenCredentialsAreValid() {
        when(userPersistencePort.findByEmail(EMAIL))
                .thenReturn(existingUser);
        when(passwordEncoderPort.matches(RAW_PASSWORD, HASHED_PASSWORD))
                .thenReturn(true);
        when(tokenServicePort.generateToken(existingUser))
                .thenReturn("jwt.token.example");

        var token = authUseCase.login(EMAIL, RAW_PASSWORD);

        assertThat(token).isEqualTo("jwt.token.example");
        verify(tokenServicePort).generateToken(existingUser);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when email does not exist")
    void shouldThrowWhenEmailDoesNotExist() {
        when(userPersistencePort.findByEmail(EMAIL)).thenReturn(null);

        assertThatThrownBy(() -> authUseCase.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(passwordEncoderPort, never()).matches(RAW_PASSWORD, HASHED_PASSWORD);
        verify(tokenServicePort, never()).generateToken(existingUser);
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password does not match")
    void shouldThrowWhenPasswordDoesNotMatch() {
        when(userPersistencePort.findByEmail(EMAIL))
                .thenReturn(existingUser);
        when(passwordEncoderPort.matches(RAW_PASSWORD, HASHED_PASSWORD))
                .thenReturn(false);

        assertThatThrownBy(() -> authUseCase.login(EMAIL, RAW_PASSWORD))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenServicePort, never()).generateToken(existingUser);
    }
}
