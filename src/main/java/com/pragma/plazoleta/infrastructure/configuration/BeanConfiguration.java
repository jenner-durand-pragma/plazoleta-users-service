package com.pragma.plazoleta.infrastructure.configuration;

import com.pragma.plazoleta.domain.api.IObjectServicePort;
import com.pragma.plazoleta.domain.spi.IObjectPersistencePort;
import com.pragma.plazoleta.domain.usecase.ObjectUseCase;
import com.pragma.plazoleta.infrastructure.out.jpa.adapter.ObjectJpaAdapter;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IObjectEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IObjectRepository objectRepository;
    private final IObjectEntityMapper objectEntityMapper;

    @Bean
    public IObjectPersistencePort objectPersistencePort() {
        return new ObjectJpaAdapter(objectRepository, objectEntityMapper);
    }

    @Bean
    public IObjectServicePort objectServicePort() {
        return new ObjectUseCase(objectPersistencePort());
    }
}