package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.User;

public interface IUserPersistencePort {

    User save(User user);

    Boolean existsByEmail(String email);
    Boolean existsByDocumentNumber(String documentNumber);

}
