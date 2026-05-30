package com.pragma.plazoleta.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pragma.plazoleta.application.dto.request.user.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.dto.response.user.UserInformationResponseDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;
import com.pragma.plazoleta.application.handler.IUserHandler;
import com.pragma.plazoleta.domain.enums.Roles;
import com.pragma.plazoleta.domain.exception.user.EmailAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.RoleNotFoundException;
import com.pragma.plazoleta.domain.exception.user.UserNotOfLegalAgeException;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserRestController.class)
@Import({
        GlobalExceptionHandler.class,
        SecurityConfiguration.class,
        CustomAuthenticationFilter.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITokenValidationPort tokenValidationPort;

    @MockBean
    private IUserHandler userHandler;

    private ObjectMapper objectMapper;
    private CreateOwnerRequestDto ownerValidRequest;
    private CreateEmployeeRequestDto employeeValidRequest;

    private UsernamePasswordAuthenticationToken adminUserAuthentication;
    private UsernamePasswordAuthenticationToken ownerUserAuthentication;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ownerValidRequest = CreateOwnerRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();
        employeeValidRequest = CreateEmployeeRequestDto.builder()
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .password("PlainPassword123$")
                .build();

        var adminPrincipal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "ADMIN");
        adminUserAuthentication = new UsernamePasswordAuthenticationToken(
                adminPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        var ownerPrincipal = new AuthenticatedUser(2L, "jenner.durand@plazoleta.com", "OWNER");
        ownerUserAuthentication = new UsernamePasswordAuthenticationToken(
                ownerPrincipal,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_OWNER"))
        );
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
                        .with(authentication(adminUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerValidRequest)))
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
                        .with(authentication(adminUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerValidRequest)))
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
                        .with(authentication(adminUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerValidRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 422 when user is not of legal age")
    void shouldReturn422OnBusinessRule() throws Exception {
        when(userHandler.createOwner(any(CreateOwnerRequestDto.class)))
                .thenThrow(new UserNotOfLegalAgeException());

        mockMvc.perform(post("/api/v1/users/owner")
                        .with(authentication(adminUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerValidRequest)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Should return 400 with field errors on invalid input")
    void shouldReturn400OnValidationError() throws Exception {
        ownerValidRequest.setEmail("not-an-email");

        mockMvc.perform(post("/api/v1/users/owner")
                        .with(authentication(adminUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerValidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed JSON")
    void shouldReturn400OnMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/users/owner")
                        .with(authentication(adminUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ this is not valid json "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should return 405 when HTTP method is not supported")
    void shouldReturn405OnUnsupportedMethod() throws Exception {
        mockMvc.perform(delete("/api/v1/users/owner").with(authentication(adminUserAuthentication)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    @DisplayName("Should return 201 when employee data is valid (caller is OWNER)")
    void shouldReturn201OnCreateEmployee() throws Exception {
        var response = UserResponseDto.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .email("jenner.durand@plazoleta.com")
                .roleName("EMPLOYEE")
                .build();
        when(userHandler.createEmployee(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/employee")
                        .with(authentication(ownerUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeValidRequest)))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roleName").value("EMPLOYEE"))
                .andExpect(jsonPath("$.email").value("jenner.durand@plazoleta.com"));
    }

    @Test
    @DisplayName("Should return 400 when document number is not numeric")
    void shouldReturn400OnInvalidDocumentEmployee() throws Exception {
        employeeValidRequest.setDocumentNumber("invalid.document");

        mockMvc.perform(post("/api/v1/users/employee")
                        .with(authentication(ownerUserAuthentication))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeValidRequest)))
                .andExpect(status().isBadRequest());
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

        mockMvc.perform(get("/api/v1/users/10").with(authentication(adminUserAuthentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.roleName").value("OWNER"))
                .andExpect(jsonPath("$.phone").value("+51985768594"));
    }
}
