package com.pragma.plazoleta.domain.api;

import com.pragma.plazoleta.domain.model.User;

public interface IUserServicePort {

    User createOwner(User user);
    User createEmployee(User user);
    User getUserById(Long id);

}
