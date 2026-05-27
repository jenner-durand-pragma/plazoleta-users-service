package com.pragma.plazoleta.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    private Long id;
    private String name;
    private String description;

}
