package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IAuthServicePort;
import com.pragma.plazoleta.domain.spi.IPasswordEncoderPort;
import com.pragma.plazoleta.domain.spi.ITokenServicePort;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;
    private final ITokenServicePort tokenServicePort;

    @Override
    public String login(String email, String rawPassword) {
        return "";
    }
}
