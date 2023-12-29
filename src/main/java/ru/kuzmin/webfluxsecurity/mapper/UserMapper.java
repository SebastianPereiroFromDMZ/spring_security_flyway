package ru.kuzmin.webfluxsecurity.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.kuzmin.webfluxsecurity.dto.UserDto;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;

//класс для маппинга ДТОЮзера в ЮзерЭнтити и обратно исмпользуем мапстракт
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto map(UserEntity userEntity);

    @InheritInverseConfiguration
    UserEntity map (UserDto userDto);
}
