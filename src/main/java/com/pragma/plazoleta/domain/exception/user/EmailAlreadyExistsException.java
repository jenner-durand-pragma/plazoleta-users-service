package com.pragma.plazoleta.domain.exception.user;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class EmailAlreadyExistsException extends ConflictException {

    private static final String ERROR_MESSAGE = "A user with this email already exists.";
    private static final String ERROR_FIELD = "email";

    public EmailAlreadyExistsException() {
        super(ERROR_MESSAGE, ERROR_FIELD);
    }
}
