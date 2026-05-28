package com.pragma.plazoleta.infrastructure.out.security.passwordencoder;

import com.pragma.plazoleta.domain.spi.IPasswordEncoderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class BCryptPasswordEncoderAdapter implements IPasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}