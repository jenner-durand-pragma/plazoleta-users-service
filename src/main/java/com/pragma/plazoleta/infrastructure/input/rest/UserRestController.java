package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;
import com.pragma.plazoleta.application.handler.IUserHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserHandler userHandler;

    @Operation(summary = "Create a restaurant owner account",
            description = "Allows the administrator to register a new owner. " +
                    "The user is assigned the OWNER role and the password is encrypted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Owner created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email or document number already exists")
    })
    @PostMapping("/owner")
    public ResponseEntity<UserResponseDto> createOwner(
            @Valid @RequestBody CreateOwnerRequestDto request
    ) {
        var createdOwner = userHandler.createOwner(request);

        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }
}
