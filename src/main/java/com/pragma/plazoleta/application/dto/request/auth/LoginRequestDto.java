package com.pragma.plazoleta.application.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Schema(description = "Registered user email", example = "jenner.durand@plazoleta.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Schema(
            description = "User password",
            example = "PlainPassword123$"
    )
    private String password;
}
