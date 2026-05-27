package com.pragma.plazoleta.application.handler;

import com.pragma.plazoleta.application.dto.request.ObjectRequestDto;
import com.pragma.plazoleta.application.dto.response.ObjectResponseDto;

import java.util.List;

public interface IObjectHandler {

    void saveObject(ObjectRequestDto objectRequestDto);

    List<ObjectResponseDto> getAllObjects();
}