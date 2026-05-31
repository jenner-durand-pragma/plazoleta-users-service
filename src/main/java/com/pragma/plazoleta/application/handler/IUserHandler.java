package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.user.CreateClientRequestDto;
import com.pragma.plazoleta.application.dto.request.user.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.dto.response.user.UserInformationResponseDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;

public interface IUserHandler {

    UserResponseDto createOwner(CreateOwnerRequestDto request);
    UserResponseDto createEmployee(CreateEmployeeRequestDto request);
    UserResponseDto createClient(CreateClientRequestDto request);

    UserInformationResponseDto getUserById(Long id);
}
