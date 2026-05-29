package com.pragma.plazoleta.infrastructure.input.rest;

import com.pragma.plazoleta.application.dto.request.auth.LoginRequestDto;
import com.pragma.plazoleta.application.dto.response.auth.LoginResponseDto;
import com.pragma.plazoleta.application.handler.IAuthHandler;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and token issuance")
public class AuthRestController {

    private final IAuthHandler authHandler;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return null;
    }
}
