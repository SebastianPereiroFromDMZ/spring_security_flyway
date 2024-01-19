package ru.kuzmin.webfluxsecurity.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kuzmin.webfluxsecurity.mapper.UserMapper;
import ru.kuzmin.webfluxsecurity.repository.UserRepository;
import ru.kuzmin.webfluxsecurity.security.SecurityService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
}
