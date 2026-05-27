package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.User;

public interface IUserPersistencePort {

    User save(User user);

    boolean existsByEmail(String email);
    boolean existsByDocumentNumber(String documentNumber);

}
