package ru.kuzmin.webfluxsecurity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.exception.AuthException;
import ru.kuzmin.webfluxsecurity.exception.UnauthorizedException;

import java.util.Base64;
import java.util.Date;

//класс обработки токена и возвращения результата проверки
public class JwtHandler {

    private final String secret;

    //передаем снаружи секрет в конструктор
    public JwtHandler(String secret) {
        this.secret = secret;
    }

    //единственный публичный метод этого хэндлера это какая то проверка
    //необходимо проверить ДВА МОМЕНТА:
    //1: подпись
    //2: дату эксперации (expiration-истечение)
    public Mono<VerificationResult> check(String accessToken){
        //для того что бы получить дату эксперации необходимо достать клеймсы и вытащить от туда дату поэтому создаем приватный метод
        return Mono.just(verify(accessToken))
                .onErrorResume(e -> Mono.error(new UnauthorizedException(e.getMessage())));
    }

    //мотод который возвращает результат верификации, в нем мы проверяем подпись методом getClaimsFromToken и дату истечения токена
    private VerificationResult verify(String token){
        Claims claims = getClaimsFromToken(token);
        //вытаскиваем из клеймсов дату эксперации(истечения)
        final Date expirationDate = claims.getExpiration();

        //если токен истек(эксперация прошло) кидаем исключение
        if (expirationDate.before(new Date())){
            //прокидываем RuntimeException и прокидываем на верх где его он перехватывается в методе check блоком onErrorResume
            throw new RuntimeException("Token expired");
        }

        //если все хорошо
        return new VerificationResult(claims, token);
    }

    private Claims getClaimsFromToken(String token){
        //берем класс Jwts просим у него переводчик
        return Jwts.parser()
                //даем ключь с помощью которого подписывается токен, используя наш подход
                .setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
                //парсим клеймсы
                .parseClaimsJws(token)
                //получаем тело
                .getBody();
    }

    //для результата проверки вместо обычного булеана или void (войды) проще берут какой то результат тоесть например какойто токен
    //и еще клеймсы накинуть что бы получить доступ к этим клеймсам
    //исходя их вышеописанного необходимо создать встроенный класс:
    public static class VerificationResult{
        public Claims claims; //Claims - утверждения
        public String token;

        public VerificationResult(Claims claims, String token) {
            this.claims = claims;
            this.token = token;
        }
    }
}
