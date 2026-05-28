package com.pragma.plazoleta.application.mapper;

import com.pragma.plazoleta.application.dto.response.user.UserInformationResponseDto;
import com.pragma.plazoleta.application.dto.response.user.UserResponseDto;
import com.pragma.plazoleta.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IUserResponseMapper {

    @Mapping(source = "role.name", target = "roleName")
    UserResponseDto toResponse(User user);

    @Mapping(source = "role.name", target = "roleName")
    UserInformationResponseDto toInformationResponse(User user);
}
