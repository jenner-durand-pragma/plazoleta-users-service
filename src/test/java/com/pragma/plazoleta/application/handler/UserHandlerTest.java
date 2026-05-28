package com.pragma.plazoleta.application.handler;

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

    private CreateOwnerRequestDto requestDto;
    private User savedUser;

    @BeforeEach
    void setUp() {
        requestDto = CreateOwnerRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();

        var ownerRole = Role.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();

        savedUser = User.builder()
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
    }

    @Test
    @DisplayName("Should create an owner")
    void shouldCreateOwner() {
        when(userServicePort.createOwner(any(User.class))).thenReturn(savedUser);

        var result = userHandler.createOwner(requestDto);

        var userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userServicePort).createOwner(userCaptor.capture());
        var passedUser = userCaptor.getValue();

        assertThat(passedUser.getEmail()).isEqualTo(requestDto.getEmail());
        assertThat(passedUser.getDocumentNumber()).isEqualTo(requestDto.getDocumentNumber());

        verify(userRequestMapper).toUser(requestDto);
        verify(userResponseMapper).toResponse(savedUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedUser.getId());
        assertThat(result.getRoleName()).isEqualTo(savedUser.getRole().getName());
        assertThat(result.getEmail()).isEqualTo(savedUser.getEmail());
    }

    @Test
    @DisplayName("Should return user by id")
    void shouldReturnUserById() {
        when(userServicePort.getUserById(any(Long.class))).thenReturn(savedUser);

        var result = userHandler.getUserById(savedUser.getId());

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(savedUser.getEmail());
        assertThat(result.getRoleName()).isEqualTo("OWNER");
    }
}
