package ru.kuzmin.webfluxsecurity.exception;

public class ApiException extends RuntimeException{

    //дополнительное поле для того чтобы было легче работать с кодами, вести документацию, давать фронту вообщем это довольно удобно
    protected String errorCode;

    //перебиваем конструктор
    public ApiException(String message, String errorCode){
        //применяем конструктор класса родителя и передаем туда сообщение
        super(message);
        //и вводим наше поле
        this.errorCode = errorCode;
    }
}
