package com.pragma.plazoleta.domain.model;

import com.pragma.plazoleta.domain.exception.user.UserNotOfLegalAgeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;
    private String name;
    private String lastName;
    private String documentNumber;
    private String phone;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Role role;

    private static final Integer MINIMUM_AGE = 18;

    public void validateLegalAge() {
        if (this.birthDate == null) {
            return;
        }

        var age = Period.between(this.birthDate, LocalDate.now()).getYears();

        if (age < MINIMUM_AGE) {
            throw new UserNotOfLegalAgeException();
        }
    }
}