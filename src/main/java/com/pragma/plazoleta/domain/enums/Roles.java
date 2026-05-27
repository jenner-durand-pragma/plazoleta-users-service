package com.pragma.plazoleta.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public enum Roles {

    ADMIN(1L, "ADMIN", "Platform administrator"),
    OWNER(2L, "OWNER", "Restaurant owner"),
    EMPLOYEE(3L, "EMPLOYEE", "Restaurant employee"),
    CLIENT(4L, "CLIENT", "Food court client");

    private final Long id;
    private final String name;
    private final String description;

}
