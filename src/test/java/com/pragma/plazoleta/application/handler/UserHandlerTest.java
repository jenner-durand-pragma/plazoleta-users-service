package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.user.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.handler.impl.UserHandler;
import com.pragma.plazoleta.application.mapper.IUserRequestMapper;
import com.pragma.plazoleta.application.mapper.IUserResponseMapper;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserHandlerTest {

    @Mock
    private IUserServicePort userServicePort;

    @Spy
    private IUserRequestMapper userRequestMapper = Mappers.getMapper(IUserRequestMapper.class);

    @Spy
    private IUserResponseMapper userResponseMapper = Mappers.getMapper(IUserResponseMapper.class);

    @InjectMocks
    private UserHandler userHandler;

    private CreateOwnerRequestDto ownerRequestDto;
    private CreateEmployeeRequestDto employeeRequestDto;
    private User ownerSavedUser;
    private User employeeSavedUser;

    @BeforeEach
    void setUp() {
        ownerRequestDto = CreateOwnerRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();
        employeeRequestDto = CreateEmployeeRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();

        var ownerRole = Role.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();

        var employeeRole = Role.builder()
                .id(3L)
                .name("EMPLOYEE")
                .description("Restaurant employee")
                .build();

        ownerSavedUser = User.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("$2a$10$hashedPassword")
                .role(ownerRole)
                .build();

        employeeSavedUser = User.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("$2a$10$hashedPassword")
                .role(employeeRole)
                .build();
    }

    @Test
    @DisplayName("Should create an owner")
    void shouldCreateOwner() {
        when(userServicePort.createOwner(any(User.class))).thenReturn(ownerSavedUser);

        var result = userHandler.createOwner(ownerRequestDto);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userServicePort).createOwner(userCaptor.capture());
        var passedUser = userCaptor.getValue();

        assertThat(passedUser.getEmail()).isEqualTo(ownerRequestDto.getEmail());
        assertThat(passedUser.getDocumentNumber()).isEqualTo(ownerRequestDto.getDocumentNumber());

        verify(userRequestMapper).toUser(ownerRequestDto);
        verify(userResponseMapper).toResponse(ownerSavedUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ownerSavedUser.getId());
        assertThat(result.getRoleName()).isEqualTo(ownerSavedUser.getRole().getName());
        assertThat(result.getEmail()).isEqualTo(ownerSavedUser.getEmail());
    }

    @Test
    @DisplayName("Should return user by id")
    void shouldReturnUserById() {
        when(userServicePort.getUserById(any(Long.class))).thenReturn(ownerSavedUser);

        var result = userHandler.getUserById(ownerSavedUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(ownerSavedUser.getEmail());
        assertThat(result.getRoleName()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("Should create an employee")
    void shouldCreateEmployee() {
        when(userServicePort.createEmployee(any(User.class))).thenReturn(employeeSavedUser);

        var result = userHandler.createEmployee(employeeRequestDto);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userServicePort).createEmployee(userCaptor.capture());
        var passedUser = userCaptor.getValue();

        assertThat(passedUser.getEmail()).isEqualTo(ownerRequestDto.getEmail());
        assertThat(passedUser.getDocumentNumber()).isEqualTo(ownerRequestDto.getDocumentNumber());

        verify(userRequestMapper).toUser(employeeRequestDto);
        verify(userResponseMapper).toResponse(employeeSavedUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employeeSavedUser.getId());
        assertThat(result.getRoleName()).isEqualTo(employeeSavedUser.getRole().getName());
        assertThat(result.getEmail()).isEqualTo(employeeSavedUser.getEmail());
    }
}
