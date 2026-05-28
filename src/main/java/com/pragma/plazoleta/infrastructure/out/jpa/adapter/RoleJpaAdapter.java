package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.spi.IRolePersistencePort;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRoleEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleJpaAdapter implements IRolePersistencePort {
    private final IRoleRepository roleRepository;
    private final IRoleEntityMapper roleEntityMapper;

    @Override
    public Role findByName(String name) {
        return null;
    }
}
