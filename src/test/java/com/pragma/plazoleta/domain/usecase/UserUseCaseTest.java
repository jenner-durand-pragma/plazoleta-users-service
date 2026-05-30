package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.enums.Roles;
import com.pragma.plazoleta.domain.exception.user.DocumentNumberAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.EmailAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.RoleNotFoundException;
import com.pragma.plazoleta.domain.exception.user.UserNotFoundException;
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

    private User validEmployee;
    private Role employeeRole;

    private User validClient;
    private Role clientRole;

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

        employeeRole = Role.builder()
                .id(3L)
                .name("EMPLOYEE")
                .description("Restaurant employee")
                .build();

        validEmployee = User.builder()
                .name("John")
                .lastName("Cook")
                .documentNumber("88888888")
                .phone("+51988888888")
                .birthDate(LocalDate.of(1995, 5, 10))
                .email("john.cook@plazoleta.com")
                .password("EmployeePass123$")
                .build();

        clientRole = Role.builder()
                .id(4L)
                .name("CLIENT")
                .description("Restaurant client")
                .build();

        validClient = User.builder()
                .name("Jane")
                .lastName("Doe")
                .documentNumber("12345678")
                .phone("+51911111111")
                .birthDate(LocalDate.of(2000, 1, 1))
                .email("jane.doe@plazoleta.com")
                .password("ClientPass123$")
                .build();
    }

    @Test
    @DisplayName("Should create an owner successfully, assigning role and encrypting password")
    void shouldCreateOwnerSuccessfullyWhenAllDataIsValidInCreateOwner() {
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
    @DisplayName("Should throw UserNotOfLegalAgeException when user is younger than 18 in create owner")
    void shouldThrowExceptionWhenUserIsNotOfLegalAgeInCreateOwner() {
        validOwner.setBirthDate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> userUseCase.createOwner(validOwner))
                .isInstanceOf(UserNotOfLegalAgeException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already registered in create owner")
    void shouldThrowExceptionWhenEmailAlreadyExistsInCreateOwner() {
        when(userPersistencePort
                .existsByEmail(validOwner.getEmail()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createOwner(validOwner))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DocumentNumberAlreadyExistsException when document is duplicated in create owner")
    void shouldThrowExceptionWhenDocumentNumberAlreadyExistsInCreateOwner() {
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
    @DisplayName("Should throw RoleNotFoundException when OWNER role is not configured in create owner")
    void shouldThrowExceptionWhenOwnerRoleDoesNotExistInCreateOwner() {
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

    @Test
    @DisplayName("Should return the user when it exists in get user by Id")
    void shouldReturnUserWhenExistsInGetUserById() {
        var existing = User.builder()
                .id(5L)
                .email("jenner.durand@plazoleta.com")
                .role(ownerRole)
                .build();
        when(userPersistencePort.findById(5L)).thenReturn(existing);

        var result = userUseCase.getUserById(5L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getRole().getName()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist in get user by Id")
    void shouldThrowWhenUserNotFoundInGetUserById() {
        when(userPersistencePort.findById(99L)).thenReturn(null);

        assertThatThrownBy(() -> userUseCase.getUserById(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("Should create an employee successfully, assigning role and encrypting password")
    void shouldCreateEmployeeSuccessfullyWhenAllDataIsValidInCreateEmployee() {
        var rawPassword = validEmployee.getPassword();
        var encodedPassword = "$2a$10$hashedEmployeePassword";

        when(userPersistencePort.existsByEmail(validEmployee.getEmail())).thenReturn(false);
        when(userPersistencePort.existsByDocumentNumber(validEmployee.getDocumentNumber())).thenReturn(false);
        when(rolePersistencePort.findByName(Roles.EMPLOYEE.getName())).thenReturn(employeeRole);
        when(passwordEncoderPort.encode(rawPassword)).thenReturn(encodedPassword);
        when(userPersistencePort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = userUseCase.createEmployee(validEmployee);

        verify(passwordEncoderPort).encode(rawPassword);
        verify(userPersistencePort).save(any(User.class));

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(validEmployee.getEmail());
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getRole()).isNotNull();
        assertThat(result.getRole().getName()).isEqualTo(Roles.EMPLOYEE.getName());
    }

    @Test
    @DisplayName("Should throw UserNotOfLegalAgeException when employee is under 18 in create employee")
    void shouldThrowExceptionWhenUserIsNotOfLegalAgeInCreateEmployee() {
        validEmployee.setBirthDate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> userUseCase.createEmployee(validEmployee))
                .isInstanceOf(UserNotOfLegalAgeException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is taken in create employee")
    void shouldThrowExceptionWhenEmailAlreadyExistsInCreateEmployee() {
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createEmployee(validEmployee))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DocumentNumberAlreadyExistsException when document is taken in create employee")
    void shouldThrowExceptionWhenDocumentNumberAlreadyExistsInCreateEmployee() {
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(false);
        when(userPersistencePort.existsByDocumentNumber(validEmployee.getDocumentNumber()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createEmployee(validEmployee))
                .isInstanceOf(DocumentNumberAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when EMPLOYEE role is not configured in create employee")
    void shouldThrowExceptionWhenEmployeeRoleDoesNotExistInCreateEmployee() {
        when(userPersistencePort.existsByEmail(validEmployee.getEmail()))
                .thenReturn(false);
        when(userPersistencePort.existsByDocumentNumber(validEmployee.getDocumentNumber()))
                .thenReturn(false);
        when(rolePersistencePort.findByName(Roles.EMPLOYEE.getName()))
                .thenReturn(null);

        assertThatThrownBy(() -> userUseCase.createEmployee(validEmployee))
                .isInstanceOf(RoleNotFoundException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create a client successfully, assigning role and encrypting password")
    void shouldCreateClientSuccessfullyWhenAllDataIsValidInCreateClient() {
        var rawPassword = validClient.getPassword();
        var encodedPassword = "$2a$10$hashedClientPassword";

        when(userPersistencePort.existsByEmail(validClient.getEmail()))
                .thenReturn(false);
        when(userPersistencePort.existsByDocumentNumber(validClient.getDocumentNumber()))
                .thenReturn(false);
        when(rolePersistencePort.findByName(Roles.CLIENT.getName()))
                .thenReturn(clientRole);
        when(passwordEncoderPort.encode(rawPassword))
                .thenReturn(encodedPassword);
        when(userPersistencePort.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var result = userUseCase.createClient(validClient);

        verify(passwordEncoderPort).encode(rawPassword);
        verify(userPersistencePort).save(any(User.class));

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(validClient.getEmail());
        assertThat(result.getPassword()).isEqualTo(encodedPassword);
        assertThat(result.getRole()).isNotNull();
        assertThat(result.getRole().getName()).isEqualTo(Roles.CLIENT.getName());
        assertThat(result.getRole()).isEqualTo(clientRole);
    }

    @Test
    @DisplayName("Should throw UserNotOfLegalAgeException when client is under 18 in create client")
    void shouldThrowExceptionWhenUserIsNotOfLegalAgeInCreateClient() {
        validClient.setBirthDate(LocalDate.now().minusYears(17));

        assertThatThrownBy(() -> userUseCase.createClient(validClient))
                .isInstanceOf(UserNotOfLegalAgeException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is taken in create client")
    void shouldThrowExceptionWhenEmailAlreadyExistsInCreateClient() {
        when(userPersistencePort.existsByEmail(validClient.getEmail()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createClient(validClient))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw DocumentNumberAlreadyExistsException when document is taken in create client")
    void shouldThrowExceptionWhenDocumentNumberAlreadyExistsInCreateClient() {
        when(userPersistencePort.existsByEmail(validClient.getEmail()))
                .thenReturn(false);
        when(userPersistencePort.existsByDocumentNumber(validClient.getDocumentNumber()))
                .thenReturn(true);

        assertThatThrownBy(() -> userUseCase.createClient(validClient))
                .isInstanceOf(DocumentNumberAlreadyExistsException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw RoleNotFoundException when CLIENT role is not configured in create client")
    void shouldThrowExceptionWhenClientRoleDoesNotExistInCreateClient() {
        when(userPersistencePort.existsByEmail(validClient.getEmail()))
                .thenReturn(false);
        when(userPersistencePort.existsByDocumentNumber(validClient.getDocumentNumber()))
                .thenReturn(false);
        when(rolePersistencePort.findByName(Roles.CLIENT.getName()))
                .thenReturn(null);

        assertThatThrownBy(() -> userUseCase.createClient(validClient))
                .isInstanceOf(RoleNotFoundException.class);

        verify(userPersistencePort, never()).save(any(User.class));
    }
}
