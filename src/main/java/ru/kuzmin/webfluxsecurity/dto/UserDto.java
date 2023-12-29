package ru.kuzmin.webfluxsecurity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import ru.kuzmin.webfluxsecurity.entity.UserRole;

import java.time.LocalDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {

    private Long id;
    private String username;
    //так же мы не хотим отдавать пароль наверх(так как мы возвращаем этот ДТО клиенту) и чтобы не создавать еще 1 ДТО делаем:
    //это означает что мы берем пароль только для создания юзерЭнтити тоестьтолько считываем, больше мы с паролем не работаем
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private UserRole role;
    //нам надо из кэмэлкейса перевести в снейккейс именования полей,так нужно по АПИ контракту
    //но так как у нас полей много с кэмалкейсон проще будет сделать общуу аннотацию над классом
    //@JsonProperty("first_name")
    private String firstName;
    private String lastName;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
