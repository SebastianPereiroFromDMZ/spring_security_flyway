package ru.kuzmin.webfluxsecurity.exception;

import lombok.Getter;

public class ApiException extends RuntimeException{

    //дополнительное поле для того чтобы было легче работать с кодами, вести документацию, давать фронту вообщем это довольно удобно
   @Getter
    protected String errorCode;

    //перебиваем конструктор
    public ApiException(String message, String errorCode){
        //применяем конструктор класса родителя и передаем туда сообщение
        super(message);
        //и вводим наше поле
        this.errorCode = errorCode;
    }
}
