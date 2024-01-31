package ru.kuzmin.webfluxsecurity.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;
import ru.kuzmin.webfluxsecurity.exception.UnauthorizedException;
import ru.kuzmin.webfluxsecurity.repository.UserRepository;
import ru.kuzmin.webfluxsecurity.service.UserService;

//настройка того что как получать данные, как проверять, как отдавать
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;
    //необходимо получить самого пользователя, принимаем Authentication и проверяем что пользователь валидный, для этого подтягиваем UserRepository
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        //получаем нашего принципала
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        //ищем нашего пользователя в БД
        return userService.getUserById(principal.getId())
                //смотрим что бы он был включон enabled
                .filter(UserEntity::isEnabled)
                //если не активен
                .switchIfEmpty(Mono.error(new UnauthorizedException("User disabled")))
                //мапин на аутентфикацию
                .map(user -> authentication);
    }


}
