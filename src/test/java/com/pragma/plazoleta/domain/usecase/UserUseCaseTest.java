package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.enums.Roles;
import com.pragma.plazoleta.domain.exception.user.DocumentNumberAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.EmailAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.RoleNotFoundException;
import com.pragma.plazoleta.domain.exception.user.UserNotOfLegalAgeException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IPasswordEncoderPort;
import com.pragma.plazoleta.domain.spi.IRolePersistencePort;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private IUserPersistencePort userPersistencePort;

    @Mock
    private IRolePersistencePort rolePersistencePort;

    @Mock
    private IPasswordEncoderPort passwordEncoderPort;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validOwner;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        ownerRole = Role.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();

        validOwner = User.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("7685968")
                .phone("+519839485495")
                .birthDate(LocalDate.now().minusYears(30))
                .email("jenner.durand@plazoleta.com")
                .password("ExamplePassword123")
                .build();
    }

    @Test
    @DisplayName("Should create an owner successfully, assigning role and encrypting password")
    void shouldCreateOwnerSuccessfullyWhenAllDataIsValid() {
        var rawPassword = validOwner.getPassword();
        var encodedPassword = "$2a$10$hashedPassword";

        when(userPersistencePort
                .existsByEmail(validOwner.getEmail()))
                .thenReturn(false);
        when(userPersistencePort
                .existsByDocumentNumber(validOwner.getDocumentNumber()))
                .thenReturn(false);
        when(rolePersistencePort
                .findByName(Roles.OWNER.getName()))
                .thenReturn(ownerRole);
        when(passwordEncoderPort
                .encode(rawPassword))
                .thenReturn(encodedPassword);
        when(userPersistencePort
                .save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = userUseCase.createOwner(validOwner);

        verify(passwordEncoderPort).encode(rawPassword);
        verify(userPersistencePort).save(any(User.class));

        assertThat(result).isNotNull();

        assertThat(result.getEmail()).isEqualTo(validOwner.getEmail());
        assertThat(result.getPassword()).isEqualTo(encodedPassword);

        assertThat(result.getRole()).isNotNull();
        assertThat(result.getRole().getName()).isEqualTo(Roles.OWNER.getName());
        assertThat(result.getRole()).isEqualTo(ownerRole);
    }

    @Test
    @DisplayName("Should throw UserNotOfLegalAgeException when user is younger than 18")
    void shouldThrowExceptionWhenUserIsNotOfLegalAge() {
        validOwner.setBirthDate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> userUseCase.createOwner(validOwner))
                .isInstanceOf(UserNotOfLegalAgeException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already registered")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userPersistencePort
                .existsByEmail(validOwner.getEmail()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createOwner(validOwner))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DocumentNumberAlreadyExistsException when document is duplicated")
    void shouldThrowExceptionWhenDocumentNumberAlreadyExists() {
        when(userPersistencePort
                .existsByEmail(validOwner.getEmail()))
                .thenReturn(false);
        when(userPersistencePort
                .existsByDocumentNumber(validOwner.getDocumentNumber()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createOwner(validOwner))
                .isInstanceOf(DocumentNumberAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when OWNER role is not configured")
    void shouldThrowExceptionWhenOwnerRoleDoesNotExist() {
        when(userPersistencePort
                .existsByEmail(validOwner.getEmail()))
                .thenReturn(false);
        when(userPersistencePort
                .existsByDocumentNumber(validOwner.getDocumentNumber()))
                .thenReturn(false);
        when(rolePersistencePort
                .findByName(Roles.OWNER.getName()))
                .thenReturn(null);

        assertThatThrownBy(() -> userUseCase.createOwner(validOwner))
                .isInstanceOf(RoleNotFoundException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }
}