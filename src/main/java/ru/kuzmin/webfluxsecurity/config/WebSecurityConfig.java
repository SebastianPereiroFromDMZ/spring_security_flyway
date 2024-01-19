package ru.kuzmin.webfluxsecurity.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.security.AuthenticationManager;
import ru.kuzmin.webfluxsecurity.security.BearerTokenServerAuthenticationConverter;
import ru.kuzmin.webfluxsecurity.security.JwtHandler;

@Slf4j
@Configuration
@EnableReactiveMethodSecurity

//ЧТО МЫ СДЕЛАЛИ В ЭТОМ КЛАССЕ:
//1: Сконфигурирововали веб секюрити в котором мы используем наш сконфигурированный AuthenticationManager
//2: Айтентификация идет с помощью bearerAuthenticationFilter этот фильтор внутри под капотом просто транслирует запрос
//в аутонтификацию вытягивая из него токен который получим
//3: И если все хорошо идем дальше
public class WebSecurityConfig {

    @Value("${jwt.secret}")
    private String secret;

    private final String[] publicRoutes = {"/api/v1/auth/register", "/api/v1/auth/login"}; //Routes - Маршруты

    //создаем бин SecurityWebFilterChain
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthenticationManager authenticationManager){
        return http
                .csrf().disable()
                //любой запрос должен быть аутофицирован
                .authorizeExchange()
                //здесь мы говорим что все могут гулять на эндпоинты OPTIONS
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                //разрешаем всем гулять на публичные руты
                .pathMatchers(publicRoutes).permitAll()
                //остальные запросы должны быть аутонфицированны
                .anyExchange().authenticated()
                //необходимо обработать ошибки которые могут возникнуть
                .and()
                .exceptionHandling()
                //если ошибка будет на нем (при попытке получить аутонтификацию)
                .authenticationEntryPoint((swe, e) -> {
                    log.error("In SecurityWebFilterChain - unauthorized error: {}", e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED));
                })
                //если доступ не разрешен
                .accessDeniedHandler((swe, e) -> {
                    log.error("In SecurityWebFilterChain - access denied error: {}", e.getMessage());
                    return Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN));
                })
                .and()
                //добавляем фильтр который сконфигурировали ниже, передаем authenticationManager
                //и говорим место в котором он находится(SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(bearerAuthenticationFilter(authenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }

    //реализуем аутентикатед веб фильтр который будет под капотом использовать
    //BearerTokenServerAuthenticationConverter(который будет запрос транслировать в аутонтификацию)
    private AuthenticationWebFilter bearerAuthenticationFilter(AuthenticationManager authenticationManager){
        //создаем новый фильтр
        AuthenticationWebFilter bearerAuthenticationFilter = new AuthenticationWebFilter(authenticationManager);
        //созданный фильтр принимает в себя конвертер(тот который мы создали: BearerTokenServerAuthenticationConverter),
        //а конвертеру нужен обработчик JWT(JwtHandler), а ему необходим секрет
        bearerAuthenticationFilter.setServerAuthenticationConverter(new BearerTokenServerAuthenticationConverter(new JwtHandler(secret)));
        //этот фильтр должен применятся для всех входящих запросов, любой запрос идет сюда
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"));

        //и этот фильтр возвращаем
        return bearerAuthenticationFilter;
    }
}
