package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.auth.LoginRequestDto;
import com.pragma.plazoleta.application.dto.response.auth.LoginResponseDto;

public interface IAuthHandler {

    LoginResponseDto login(LoginRequestDto request);

}
