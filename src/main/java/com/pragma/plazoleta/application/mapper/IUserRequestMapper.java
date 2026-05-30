package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.request.user.CreateEmployeeRequestDto;
import com.pragma.plazoleta.application.dto.request.user.CreateOwnerRequestDto;
import com.pragma.plazoleta.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IUserRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(CreateOwnerRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(CreateEmployeeRequestDto dto);
}
