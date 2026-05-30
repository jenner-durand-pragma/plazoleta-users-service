package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.api.IAuthServicePort;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.spi.IPasswordEncoderPort;
import com.pragma.plazoleta.domain.spi.IRolePersistencePort;
import com.pragma.plazoleta.domain.spi.ITokenServicePort;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import com.pragma.plazoleta.domain.usecase.AuthUseCase;
import com.pragma.plazoleta.domain.usecase.UserUseCase;
import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.RoleJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.UserJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRoleRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IUserRepository;
import com.pragma.plazoleta.infrastructure.out.security.jwt.JwtAdapter;
import com.pragma.plazoleta.infrastructure.out.security.jwt.configuration.JwtProperties;
import com.pragma.plazoleta.infrastructure.out.security.passwordencoder.BCryptPasswordEncoderAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;
    private final IRoleRepository roleRepository;
    private final IRoleEntityMapper roleEntityMapper;

    private final JwtProperties jwtProperties;

    @Bean
    public IUserPersistencePort userPersistencePort() {
        return new UserJpaAdapter(userRepository, userEntityMapper);
    }

    @Bean
    public IRolePersistencePort rolePersistencePort() {
        return new RoleJpaAdapter(roleRepository, roleEntityMapper);
    }

    @Bean
    public IPasswordEncoderPort passwordEncoderPort(PasswordEncoder passwordEncoder) {
        return new BCryptPasswordEncoderAdapter(passwordEncoder);
    }

    @Bean
    public JwtAdapter jwtAdapter() {
        return new JwtAdapter(jwtProperties);
    }

    @Bean
    public ITokenServicePort tokenServicePort(JwtAdapter jwtAdapter) {
        return jwtAdapter;
    }

    @Bean
    public ITokenValidationPort tokenValidationPort(JwtAdapter jwtAdapter) {
        return jwtAdapter;
    }

    @Bean
    public IAuthServicePort authServicePort(
            ITokenServicePort tokenServicePort,
            IPasswordEncoderPort passwordEncoderPort
    ) {
        return new AuthUseCase(
                userPersistencePort(),
                passwordEncoderPort,
                tokenServicePort
        );
    }

    @Bean
    public IUserServicePort userServicePort(
            IPasswordEncoderPort passwordEncoderPort
    ) {
        return new UserUseCase(
                userPersistencePort(),
                rolePersistencePort(),
                passwordEncoderPort
        );
    }
}
