package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository userRepository;
    private final IUserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public Boolean existsByEmail(String email) {
        return null;
    }

    @Override
    public Boolean existsByDocumentNumber(String documentNumber) {
        return null;
    }
}
