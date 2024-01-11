package ru.kuzmin.webfluxsecurity.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import ru.kuzmin.webfluxsecurity.dto.UserDto;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;

//класс для маппинга ДТОЮзера в ЮзерЭнтити и обратно исмпользуем мапстракт
//за место реализации методов используется аннотация @Mapper
@Mapper(componentModel = "spring")
public interface UserMapper {

    //какойто из методов должен быть основой, а какой то ведомым, ведомым будет второй,
    // и над ним неообходимо проставить InheritInverseConfiguration
    UserDto map(UserEntity userEntity);

    @InheritInverseConfiguration
    UserEntity map (UserDto userDto);
}
