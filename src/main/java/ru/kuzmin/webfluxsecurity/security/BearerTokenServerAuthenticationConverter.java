package ru.kuzmin.webfluxsecurity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

//ЧТО МЫ В ИТОГЕ СДЕЛАЛИ В ЭТОМ КЛАССЕ:
//1: Получили запрос ServerWebExchange
//2: Из запроса вытащили заголовок методом extractHeader в которос есть ТОКЕН с перфиксом "Bearer "
//3: Убрали префикс функцией getBearerValue остался только токен (метод convert)
//4: Этот токен прогоняем через метод jwtHandler::check (метод convert) получаем VerificationResult
//5: VerificationResult натягиваем на метод create класса UserAuthenticationBearer
//и в итоге после всего этого получаем Authentication


//класс конвертер который будет конвертировать веб запрос который приходит из вне в аутентификацияюи
//и потом этот класс необходимо встроить в цепочку фильтров

//!!!!ВСЯ ЛОГИКА СПРИНГ СЕКЮРИТИ СВОДИТСЯ К: приходит запрос поподает на фильтрЧейн и далее он прогоняется,
//и в рамках этого фильтра мы понимаем рабочий этот токен или нет
@RequiredArgsConstructor
public class BearerTokenServerAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtHandler jwtHandler;

    //предпологаем что токен ServerWebExchange запроса будет храниться в заголовке BearerToken поэтому создаем поле:
    private static final String BEARER_PREFIX = "Bearer ";
    //теперь необходимо вытащить из запроса который будет этот токен
    //из authValue вытаскиваем строку (.substring) которая начинается после BEARER_PREFIX ТОЕСТЬ:
    //получили строку токен которая начинается с Bearer и просто отсекли его
    private static final Function<String, Mono<String>> getBearerValue = authValue -> Mono
            .justOrEmpty(authValue.substring(BEARER_PREFIX.length()));

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        //вытаскиваем нужный хэдер через прописанный ниже метод
        return extractHeader(exchange)
                //прменяем функцию getBearerValue что бы тримернуть ее
                .flatMap(getBearerValue)
                //прогоняем точ то у нас получилось через метод check
                .flatMap(jwtHandler::check)
                //выше нам вернулся VerificationResult и натягиваем его на метод креате
                .flatMap(UserAuthenticationBearer::create);
    }

    private Mono<String> extractHeader(ServerWebExchange exchange) { //extract-извлекать, xchange - обмен
        //из ServerWebExchange exchange вытаскиваем Request
        return Mono.justOrEmpty(exchange.getRequest()
                //вытаскиваем заголовки
                .getHeaders()
                //вытаскиваем первый заголовок с необходимым нам названием
                .getFirst(HttpHeaders.AUTHORIZATION));
    }
}
