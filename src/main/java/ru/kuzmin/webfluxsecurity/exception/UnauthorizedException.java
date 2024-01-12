package ru.kuzmin.webfluxsecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Unauthorized-Неавторизованный

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends ApiException {


    public UnauthorizedException(String message) {
        super(message, "KUZMIN_UNAUTHORIZED");
    }
}
