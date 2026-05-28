package com.pragma.plazoleta.domain.exception.user;

import com.pragma.plazoleta.domain.exception.BusinessRuleException;

public class UserNotOfLegalAgeException extends BusinessRuleException {

    private static final String ERROR_MESSAGE = "User must be of legal age (at least 18 years old).";

    public UserNotOfLegalAgeException() {
        super(ERROR_MESSAGE);
    }
}
