package com.pragma.plazoleta.domain.exception.user;

import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.Role;

public class RoleNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "Role not found.";

    public RoleNotFoundException(Long roleId) {
        super(ERROR_MESSAGE, Role.class.getSimpleName(), roleId);
    }
}
