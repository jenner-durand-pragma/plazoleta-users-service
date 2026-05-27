package com.pragma.plazoleta.domain.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends DomainException {
    private final String resource;
    private final Object resourceId;

    protected NotFoundException(String message, String resource, Object resourceId) {
        super(message);
        this.resource = resource;
        this.resourceId = resourceId;
    }
}
