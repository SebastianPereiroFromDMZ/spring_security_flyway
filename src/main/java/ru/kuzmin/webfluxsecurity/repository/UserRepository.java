package ru.kuzmin.webfluxsecurity.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;

//R2dbcRepository аналог JpaRepository только реактивный
public interface UserRepository extends R2dbcRepository<UserEntity, Long> {

    //Mono это реактивная надстройка над возвращаемым типом, обещание результата
    Mono<UserEntity> findByUsername (String username);
}
