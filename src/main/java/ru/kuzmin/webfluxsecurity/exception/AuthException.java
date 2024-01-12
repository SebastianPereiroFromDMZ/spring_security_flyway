package ru.kuzmin.webfluxsecurity.exception;

//кастомный эксепшн, его будем прокидывать наружу, и надо добавить еще 1 класс ApiException
public class AuthException extends ApiException {

    public AuthException(String message, String errorCode) {
        super(message, errorCode);
    }
}
