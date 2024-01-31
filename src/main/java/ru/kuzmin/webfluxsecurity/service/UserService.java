package ru.kuzmin.webfluxsecurity.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;
import ru.kuzmin.webfluxsecurity.entity.UserRole;
import ru.kuzmin.webfluxsecurity.repository.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<UserEntity> registerUser(UserEntity user){
        return userRepository.save(
                user.toBuilder()
                        .password(passwordEncoder.encode(user.getPassword()))
                        .role(UserRole.USER)
                        .enabled(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        ).doOnSuccess(u -> {
            log.info("In registerUser - user: {} created", u);
        });
    }

    public Mono<UserEntity> getUserById(Long id){
        return userRepository.findById(id);
    }

    public Mono<UserEntity> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
