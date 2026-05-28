package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;
import com.pragma.plazoleta.application.handler.IUserHandler;
import com.pragma.plazoleta.infrastructure.exceptionhandler.common.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Users", description = "User management operations")
public class UserRestController {

    private final IUserHandler userHandler;

    @Operation(summary = "Create a owner account",
            description = "Allows to register a new owner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Owner created successfully"),

            @ApiResponse(responseCode = "400", description = "Invalid input data (validation failed)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(responseCode = "404", description = "OWNER role is not configured",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(responseCode = "409", description = "Email or document number already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(responseCode = "422", description = "User is not of legal age",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/owner")
    public ResponseEntity<UserResponseDto> createOwner(
            @Valid @RequestBody CreateOwnerRequestDto request
    ) {
        var createdOwner = userHandler.createOwner(request);

        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }
}
