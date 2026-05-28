package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.dto.response.user.UserInformationResponseDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;
import com.pragma.plazoleta.application.handler.IUserHandler;
import com.pragma.plazoleta.domain.enums.Roles;
import com.pragma.plazoleta.domain.exception.user.EmailAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.RoleNotFoundException;
import com.pragma.plazoleta.domain.exception.user.UserNotOfLegalAgeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = UserRestController.class,
        excludeAutoConfiguration = {
                org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
        }
)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserHandler userHandler;

    private ObjectMapper objectMapper;

    private CreateOwnerRequestDto validRequest;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validRequest = CreateOwnerRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();
    }

    @Test
    @DisplayName("Should return 201 Created when owner data is valid")
    void shouldReturn201WhenOwnerIsCreated() throws Exception {
        var response = UserResponseDto.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();
        when(userHandler.createOwner(any(CreateOwnerRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roleName").value("OWNER"))
                .andExpect(jsonPath("$.email").value("jenner.durand@plazoleta.com"));

        verify(userHandler).createOwner(any(CreateOwnerRequestDto.class));
    }

    @Test
    @DisplayName("Should return 409 with field detail when email already exists")
    void shouldReturn409OnEmailConflict() throws Exception {
        when(userHandler.createOwner(any(CreateOwnerRequestDto.class)))
                .thenThrow(new EmailAlreadyExistsException());

        mockMvc.perform(post("/api/v1/users/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("email"));
    }

    @Test
    @DisplayName("Should return 404 when role is not found")
    void shouldReturn404OnRoleNotFound() throws Exception {
        when(userHandler.createOwner(any(CreateOwnerRequestDto.class)))
                .thenThrow(new RoleNotFoundException(Roles.OWNER.getId()));

        mockMvc.perform(post("/api/v1/users/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 422 when user is not of legal age")
    void shouldReturn422OnBusinessRule() throws Exception {
        when(userHandler.createOwner(any(CreateOwnerRequestDto.class)))
                .thenThrow(new UserNotOfLegalAgeException());

        mockMvc.perform(post("/api/v1/users/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Should return 400 with field errors on invalid input")
    void shouldReturn400OnValidationError() throws Exception {
        validRequest.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/users/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed JSON")
    void shouldReturn400OnMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/users/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not valid json "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 405 when HTTP method is not supported")
    void shouldReturn405OnUnsupportedMethod() throws Exception {
        mockMvc.perform(get("/api/v1/users/owner"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    @DisplayName("Should return 200 with user info when user exists")
    void shouldReturn200WithUserInfo() throws Exception {
        var userInformation = UserInformationResponseDto.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("OWNER")
                .build();

        when(userHandler.getUserById(10L)).thenReturn(userInformation);

        mockMvc.perform(get("/api/v1/users/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roleName").value("OWNER"))
                .andExpect(jsonPath("$.phone").value("+51985768594"));
    }
}
