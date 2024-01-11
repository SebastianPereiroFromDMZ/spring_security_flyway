package ru.kuzmin.webfluxsecurity.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
//создаем свой инкодер
//он естественно будет @Component потому что нужен бин

@Component
public class PBFDK2Encoder implements PasswordEncoder {
    //понадобится 3 параметра из вне (из application.yaml)
    //1:Секрет. На основании которого будем кодировать
    @Value("${jwt.password.encoder.secret}")
    private String secret;
    //2:Количесво итераций эндкодинга
    @Value("${jwt.password.encoder.iteration}")
    private Integer iteration;
    //3:Длина ключа.
    @Value("${jwt.password.encoder.keylength}")
    private Integer keyLength;


    //генерим пароль с использованием ниже предоставленного алгоритма "PBKDF2WithHmacSHA512",
    // или другими словами какую сущьность(подход) мы будет использовать для генерации секрета а используем PBKDF2WithHmacSHA512
    private static final String SECRET_KEY_INSTANCE = "PBKDF2WithHmacSHA512";

    //от интерфейса PasswordEncoder имплементируем два метода:

    //1: будет кодировать открытый пароль CharSequence в защищенную запись
    @Override
    public String encode(CharSequence rawPassword) {
        //на основании CharSequence получаем массив байтов, а потом с помощью PBEKeySpec кодируем в строку c помощью алгоритма (PBKDF2WithHmacSHA512),
        // секрета, итераций, длины ключа
        try {

            byte[] result = SecretKeyFactory.getInstance(SECRET_KEY_INSTANCE)
                    //генерируем секрт с помощью спецификации PBEKeySpec
                    .generateSecret(new PBEKeySpec(rawPassword.toString().toCharArray(),
                            secret.getBytes(), iteration, keyLength))
                    .getEncoded();
            return Base64.getEncoder()
                    .encodeToString(result);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    //2:принимает сырой пароль CharSequence и закодированную запись String encodedPassword и принять решение
    // являются эти две записи CharSequence rawPassword, String encodedPassword одним и темже паролем
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
