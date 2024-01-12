package ru.kuzmin.webfluxsecurity.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//класс где мы настраиваем токен который отдаем аутентифицированному юзеру
//отдаем токе и еще дополнительные данные которые включают в себе ID юзера и дату создания токена а также дату истечения токена

//этот класс надстройка над данными по токену

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenDetails {
    //все то что мы возвращаем эзеру

    private Long userId;//юзер Id
    private String token;//сам токен
    private Date issuedAt; //дата выдачи токена
    private Date expiresAt; //дата истечения срока действия
}
