package com.pragma.plazoleta.infrastructure.configuration.security.token;

import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.TokenPayload;

public interface ITokenValidationPort {

    TokenPayload validate(String token);

}
