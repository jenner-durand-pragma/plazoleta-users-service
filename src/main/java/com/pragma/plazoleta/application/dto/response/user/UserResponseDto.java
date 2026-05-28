package com.pragma.plazoleta.application.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

    @Schema(description = "Unique internal identifier", example = "10")
    private Long id;

    @Schema(description = "Owner's first name", example = "Jenner")
    private String name;

    @Schema(description = "Owner's last name", example = "Durand")
    private String lastName;

    @Schema(description = "Identity Document number", example = "76859685")
    private String documentNumber;

    @Schema(description = "Phone number", example = "+51985768594")
    private String phone;

    @Schema(description = "Owner's date of birth", example = "2002-09-21")
    private LocalDate birthDate;

    @Schema(description = "Email address", example = "jenner.durand@plazoleta.com")
    private String email;

    @Schema(description = "Assigned role", example = "OWNER")
    private String roleName;

}
