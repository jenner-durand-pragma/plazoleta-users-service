package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IAuthServicePort;
import com.pragma.plazoleta.domain.exception.auth.InvalidCredentialsException;
import com.pragma.plazoleta.domain.model.User;
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
        var user = resolveUserByEmail(email);
        validatePasswordMatch(rawPassword, user.getPassword());

        return tokenServicePort.generateToken(user);
    }

    private User resolveUserByEmail(String email) {
        var user = userPersistencePort.findByEmail(email);
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    private void validatePasswordMatch(String rawPassword, String encryptedPassword) {
        var isCorrectPassword = passwordEncoderPort.matches(rawPassword, encryptedPassword);

        if (Boolean.FALSE.equals(isCorrectPassword)) {
            throw new InvalidCredentialsException();
        }
    }
}
