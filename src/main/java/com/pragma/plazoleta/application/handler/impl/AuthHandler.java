package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.auth.LoginRequestDto;
import com.pragma.plazoleta.application.dto.response.auth.LoginResponseDto;
import com.pragma.plazoleta.application.handler.IAuthHandler;
import com.pragma.plazoleta.domain.api.IAuthServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHandler implements IAuthHandler {

    private static final String BEARER = "Bearer";

    private final IAuthServicePort authServicePort;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        return null;
    }
}
