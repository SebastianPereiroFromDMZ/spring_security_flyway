package ru.kuzmin.webfluxsecurity.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.core.publisher.Mono;

import java.util.List;

//необходимо наш класс VerificationResult который вложен в класс JwtHandler трансформировать в аутентификацию
public class UserAuthenticationBearer { //UserAuthenticationBearer - Носитель аутентификации пользователя, Bearer носитель
    //здесь будет 1 статический метод который будет принимать VerificationResult а возвращать Authentication завернутое в Mono

    public static Mono<Authentication> create(JwtHandler.VerificationResult verificationResult){
        //вытягиваем тело (клеймсы)
        Claims claims = verificationResult.claims;
        //вытаскиваем сабджект
        String subject = claims.getSubject();

        //вытаскиваем роль
        String role = claims.get("role", String.class);
        //и имя юзера
        String username = claims.get("username", String.class);
        //необходимо транслировать роль в БД в SimpleGrantedAuthority
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        Long principalId = Long.parseLong(subject);
        CustomPrincipal principal = new CustomPrincipal(principalId, username);

        //возвращаем аутентификацию
        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(principal, null, authorities));

    }
}
