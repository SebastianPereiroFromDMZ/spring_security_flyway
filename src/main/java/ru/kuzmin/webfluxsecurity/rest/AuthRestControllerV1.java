package ru.kuzmin.webfluxsecurity.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.dto.AuthRequestDto;
import ru.kuzmin.webfluxsecurity.dto.AuthResponseDto;
import ru.kuzmin.webfluxsecurity.dto.UserDto;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;
import ru.kuzmin.webfluxsecurity.mapper.UserMapper;
import ru.kuzmin.webfluxsecurity.security.CustomPrincipal;
import ru.kuzmin.webfluxsecurity.security.SecurityService;
import ru.kuzmin.webfluxsecurity.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final SecurityService securityService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public Mono<UserDto> register(@RequestBody UserDto dto) {
        //мапим ДТО на ЭНТИТИ
        UserEntity entity = userMapper.map(dto);
        return userService.registerUser(entity)
                //результат который получим намапим на дто (другими словами мы мапим из энтити дто и дто отдаем наверх)
                .map(userMapper::map);
        //и по итогу вернем дто
    }

    @PostMapping("/login")
    public Mono<AuthResponseDto> login(@RequestBody AuthRequestDto dto) {
        //вернем:
        //аутентифицируем пользователя на основании его узернама и пароля
        return securityService.authenticated(dto.getUsername(), dto.getPassword())
                //полученный ответ (токен детайлс) мапим AuthResponseDto
                .flatMap(tokenDetails -> Mono.just(
                        AuthResponseDto.builder()
                                .userId(tokenDetails.getUserId())
                                .token(tokenDetails.getToken())
                                .issuedAt(tokenDetails.getIssuedAt())
                                .expiresAt(tokenDetails.getExpiresAt())
                                .build()
                ));
    }

    @GetMapping("/info")
    public Mono<UserDto> getUserInfo(Authentication authentication) {
        CustomPrincipal customPrincipal = (CustomPrincipal) authentication.getPrincipal();
        return userService.getUserById(customPrincipal.getId())
                .map(userMapper::map);
    }


}
