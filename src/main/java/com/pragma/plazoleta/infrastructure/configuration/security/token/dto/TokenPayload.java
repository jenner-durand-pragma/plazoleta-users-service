package com.pragma.plazoleta.infrastructure.configuration.security.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenPayload {

    private final Long userId;
    private final String email;
    private final String role;

}
