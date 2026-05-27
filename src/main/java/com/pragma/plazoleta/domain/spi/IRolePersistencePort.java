package com.pragma.plazoleta.domain.spi;

import com.pragma.plazoleta.domain.model.Role;

public interface IRolePersistencePort {

    Role findByName(String name);

}
