package com.pragma.plazoleta.domain.exception.user;

import com.pragma.plazoleta.domain.exception.ConflictException;

public class DocumentNumberAlreadyExistsException extends ConflictException {
    private static final String ERROR_MESSAGE = "A user with this document number already exists.";
    private static final String ERROR_FIELD = "document_number";

    public DocumentNumberAlreadyExistsException() {
        super(ERROR_MESSAGE, ERROR_FIELD);
    }
}
