package com.pragma.plazoleta.application.dto.request.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOwnerRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Document number is required")
    @Pattern(regexp = "^\\D+$", message = "Document number must be numeric only")
    private String documentNumber;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^\\+?\\D{1,13}$",
            message = "Phone must be numeric, max 13 characters, optional leading '+'"
    )
    private String phone;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}
