package com.pragma.plazoleta.domain.exception.user;

import com.pragma.plazoleta.domain.exception.NotFoundException;
import com.pragma.plazoleta.domain.model.User;

public class UserNotFoundException extends NotFoundException {

    private static final String ERROR_MESSAGE = "User not found.";

    public UserNotFoundException(Long userId) {
        super(ERROR_MESSAGE, User.class.getSimpleName(), userId);
    }
}
