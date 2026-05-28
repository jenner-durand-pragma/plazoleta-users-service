package com.pragma.plazoleta.infrastructure.out.jpa.mapper;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.infrastructure.out.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {IRoleEntityMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface IUserEntityMapper {

    UserEntity toEntity(User user);

    User toModel(UserEntity entity);

}
