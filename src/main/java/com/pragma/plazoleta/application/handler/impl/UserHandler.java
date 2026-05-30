package com.pragma.plazoleta.application.handler.impl;

import com.pragma.plazoleta.application.dto.request.user.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.dto.response.user.UserInformationResponseDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;
import com.pragma.plazoleta.application.handler.IUserHandler;
import com.pragma.plazoleta.application.mapper.IUserRequestMapper;
import com.pragma.plazoleta.application.mapper.IUserResponseMapper;
import com.pragma.plazoleta.domain.api.IUserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserHandler implements IUserHandler {

    private final IUserServicePort userServicePort;
    private final IUserRequestMapper userRequestMapper;
    private final IUserResponseMapper userResponseMapper;

    @Override
    @Transactional
    public UserResponseDto createOwner(CreateOwnerRequestDto request) {
        var userToCreate = userRequestMapper.toUser(request);
        var userCreated = userServicePort.createOwner(userToCreate);

        return userResponseMapper.toResponse(userCreated);
    }

    @Override
    public UserResponseDto createEmployee(CreateEmployeeRequestDto request) {
        var userToCreate = userRequestMapper.toUser(request);
        var userCreated = userServicePort.createEmployee(userToCreate);

        return userResponseMapper.toResponse(userCreated);
    }

    @Override
    public UserInformationResponseDto getUserById(Long id) {
        var user = userServicePort.getUserById(id);

        return userResponseMapper.toInformationResponse(user);
    }
}
