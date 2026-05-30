package com.pragma.plazoleta.infrastructure.out.jpa.adapter;

import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.RoleEntity;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IRoleEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IUserEntityMapper;
import com.pragma.plazoleta.infrastructure.out.jpa.mapper.IUserEntityMapperImpl;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IRoleRepository;
import com.pragma.plazoleta.infrastructure.out.jpa.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({IUserEntityMapperImpl.class, IRoleEntityMapperImpl.class})
class UserJpaAdapterTest {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IUserEntityMapper userEntityMapper;

    private UserJpaAdapter userJpaAdapter;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        userJpaAdapter = new UserJpaAdapter(userRepository, userEntityMapper);

        var roleEntity = RoleEntity.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();
        roleRepository.save(roleEntity);

        ownerRole = Role.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("$2a$10$hashedPassword")
                .role(ownerRole)
                .build();
    }

    @Test
    @DisplayName("Should save a user and return it with a generated id")
    void shouldSaveUserAndReturnWithId() {
        var saved = userJpaAdapter.save(buildUser());

        assertThat(saved).isNotNull();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("jenner.durand@plazoleta.com");
        assertThat(saved.getRole().getName()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("Should return true when email already exists")
    void shouldReturnTrueWhenEmailExists() {
        userJpaAdapter.save(buildUser());

        assertThat(userJpaAdapter.existsByEmail("jenner.durand@plazoleta.com")).isTrue();
        assertThat(userJpaAdapter.existsByEmail("admin@plazoleta.com")).isFalse();
    }

    @Test
    @DisplayName("Should return true when document number already exists")
    void shouldReturnTrueWhenDocumentNumberExists() {
        userJpaAdapter.save(buildUser());

        assertThat(userJpaAdapter.existsByDocumentNumber("76859685")).isTrue();
        assertThat(userJpaAdapter.existsByDocumentNumber("00000000")).isFalse();
    }

    @Test
    @DisplayName("Should return user when id exists")
    void shouldReturnUserWhenIdExists() {
        var savedUser = userJpaAdapter.save(buildUser());

        var userFounded = userJpaAdapter.findById(savedUser.getId());

        assertThat(userFounded).isNotNull();
        assertThat(userFounded.getEmail()).isEqualTo("jenner.durand@plazoleta.com");
        assertThat(userFounded.getRole().getName()).isEqualTo("OWNER");
    }

    @Test
    @DisplayName("Should return user when email exists")
    void shouldReturnUserWhenEmailExists() {
        var savedUser = userJpaAdapter.save(buildUser());

        var userFounded = userJpaAdapter.findByEmail(savedUser.getEmail());

        assertThat(userFounded).isNotNull();
        assertThat(userFounded.getDocumentNumber()).isEqualTo("76859685");
        assertThat(userFounded.getRole().getName()).isEqualTo("OWNER");
    }
}
