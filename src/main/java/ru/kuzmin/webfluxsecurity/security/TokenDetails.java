package ru.kuzmin.webfluxsecurity.security;

import lombok.Data;

import java.util.Date;

public class TokenDetails {

    private Long userId;
    private String token;
    private Date issuedAt; //дата выдачи токена
    private Date expiresAt; //дата истечения срока действия
}
