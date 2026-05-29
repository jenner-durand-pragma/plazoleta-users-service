package com.pragma.plazoleta.infrastructure.out.jpa.repository;

import com.pragma.plazoleta.infrastructure.out.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    Boolean existsByEmail(String email);
    Boolean existsByDocumentNumber(String documentNumber);

    @EntityGraph(attributePaths = {"role"})
    Optional<UserEntity> findByEmail(String email);
}
