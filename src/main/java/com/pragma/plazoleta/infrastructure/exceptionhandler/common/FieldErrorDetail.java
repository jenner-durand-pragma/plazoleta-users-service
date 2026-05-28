package com.pragma.plazoleta.infrastructure.exceptionhandler.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDetail {

    private String field;
    private String message;

}
