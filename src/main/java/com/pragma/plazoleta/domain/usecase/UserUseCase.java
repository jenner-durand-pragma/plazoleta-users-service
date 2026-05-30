package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.api.IUserServicePort;
import com.pragma.plazoleta.domain.enums.Roles;
import com.pragma.plazoleta.domain.exception.user.DocumentNumberAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.EmailAlreadyExistsException;
import com.pragma.plazoleta.domain.exception.user.RoleNotFoundException;
import com.pragma.plazoleta.domain.exception.user.UserNotFoundException;
import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.IPasswordEncoderPort;
import com.pragma.plazoleta.domain.spi.IRolePersistencePort;
import com.pragma.plazoleta.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    @Override
    public User createOwner(User user) {
        return createUserWithRole(user, Roles.OWNER.getName());
    }

    @Override
    public User createEmployee(User user) {
        return createUserWithRole(user, Roles.EMPLOYEE.getName());
    }

    @Override
    public User createClient(User user) {
        return createUserWithRole(user, Roles.CLIENT.getName());
    }

    @Override
    public User getUserById(Long id) {
        var user = userPersistencePort.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }

        return user;
    }

    private User createUserWithRole(User user, String roleName) {
        user.validateLegalAge();

        validateEmailUniqueness(user.getEmail());
        validateDocumentNumberUniqueness(user.getDocumentNumber());

        var role = resolveRoleByName(roleName);
        user.setRole(role);

        user.setPassword(passwordEncoderPort.encode(user.getPassword()));

        return userPersistencePort.save(user);
    }

    private void validateEmailUniqueness(String email) {
        var existsByEmail = userPersistencePort.existsByEmail(email);

        if (Boolean.TRUE.equals(existsByEmail)) {
            throw new EmailAlreadyExistsException();
        }
    }

    private void validateDocumentNumberUniqueness(String documentNumber) {
        var existsByDocumentNumber = userPersistencePort.existsByDocumentNumber(documentNumber);

        if (Boolean.TRUE.equals(existsByDocumentNumber)) {
            throw new DocumentNumberAlreadyExistsException();
        }
    }

    private Role resolveRoleByName(String roleName) {
        var role = rolePersistencePort.findByName(roleName);

        if (role == null) {
            throw new RoleNotFoundException(Roles.OWNER.getId());
        }

        return role;
    }
}
