package ru.kuzmin.webfluxsecurity.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.kuzmin.webfluxsecurity.entity.UserEntity;
import ru.kuzmin.webfluxsecurity.exception.AuthException;
import ru.kuzmin.webfluxsecurity.repository.UserRepository;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    //что необходимо для того чтобы сгенерировать токен? Токен состоит из 3 частей : заголовок, тело(инфо часть), подпись.
    // Для генерации подписи необходимо знать алгоритм с помощью которого мы это делаем и секретный ключь с помощью которого будем подписывать токен,
    // так же для токена необходимо expiration тоесть время жизни токена после которого он будет невалиден
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Integer expirationsInSeconds; //expiration-истечение
    @Value("${jwt.issuer}")
    private String issuer; //issuer-издатель

    //свмый первый из 3 перегруженных методов который вызывается из метода authenticated а дальже ниже они вызываются по цепочке
    private TokenDetails generateToken(UserEntity user) {
        //генерим клеймсы (тело)
        Map<String, Object> claims = new HashMap<>() {{
            put("role", user.getRole());
            put("username", user.getUsername());
        }};
        return generateToken(claims, user.getId().toString());
    }

    //снова метод генерации токена по такомуже названию метода, тоесть мы его перегружаем
    private TokenDetails generateToken(Map<String, Object> claims, String subject) {
        Long expirationTimeInMillis = expirationsInSeconds * 1000L;
        //получаем дату истечения токена которая равна текущая дата получаем время сейчас и умножаем на дату жизни токена expirationTimeInMillis
        Date expirationDate = new Date(new Date().getTime() * expirationTimeInMillis);

        //передаем параметры в следущий ниже метод с такимже названием(перегруженный)
        return generateToken(expirationDate, claims, subject);

    }

    //приватный метод для генерации токена
    private TokenDetails generateToken(Date expirationDate, Map<String, Object> claims, String subject) {
        //генерим дату создания, в нашем случае текущая дата, в данный момент
        Date createdDate = new Date();
        //генерим токен
        String token = Jwts.builder()
                //в токене будут следующие данные:
                //клеймсы
                .setClaims(claims)
                //issuer это я издатель этого токена
                .setIssuer(issuer)
                //subject это будет тот кому мы отдадим этот токен, каким то образом мы прокинем сюда юзер id
                .setSubject(subject)
                //дата создания или дата когда был выдан токен
                .setIssuedAt(createdDate)
                //нужен Id самого токена, пускай это будет какаято рандомная строка
                .setId(UUID.randomUUID().toString())
                //и необходима эксперация expiration тоесть дата истечения
                .setExpiration(expirationDate)
                //и самое главное нам этот токен необходимо подписать
                //в подписе токена выбираем алгоритм SignatureAlgorithm.ES256 и секрет который мы закодируем через Base64
                .signWith(SignatureAlgorithm.ES256, Base64.getEncoder().encodeToString(secret.getBytes()))
                .compact();
        //возвращаем TokenDetails
        return TokenDetails.builder()
                //закидываем токен который мы выше генерим
                .token(token)
                //закинем дату года мы его сгенерим
                .issuedAt(createdDate)
                //закидываем дату истечения
                .expiresAt(expirationDate)
                .build();
    }

    //метод аутентификации: получаем имя и пароль юзера, сверяем эти данные с БД если пользователь валиден даем TokenDetails, если нет кидаем ошибку
    public Mono<TokenDetails> authenticated(String username, String password) {
        //ищем юзера в БД
        return userRepository.findByUsername(username)
                //если юзер найден в БД:
                .flatMap(user -> {
                    //если пользователь не включон
                    if (!user.isEnabled()) {
                        return Mono.error(new AuthException("Account disabled" , "KUZMIN_USER_ACCOUNT_DISABLED"));
                    }
                    //если пароли не совпадают
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new AuthException("Invalid password" , "KUZMIN_INVALID_PASSWORD"));
                    }
                    //ВАЖНАЯ ЗАПИСЬ!!!! если все хорошо возвращаем TokenDetails в котором будут
                    //САМ ТОКЕН, ДАТА ВЫДАЧИ ТОКЕНА И ДАТА ИСТЕЧЕНИЯ СРОКА ДЕЙСТВИЯ ТОКЕНА !!!НО НЕ БУДЕТ Id ЮЗЕРА
                    //(Id юзера есть в самом токене но не удобно его оттуда вытаскивать) ПОЭТОМУ:
                    //в методе generateToken даем юзера которого нам достал репозиторий из БД (UserEntity) ОН НАМ ВОЗВРАЩАЕТ TokenDetails
                    // НО БЕЗ ID !!! ПОЭТОМУ МЫ ДОКИДЫВАЕМ ID ЗДЕСЬ!!!И возвращаем полностью заполненный TokenDetails
                    return Mono.just(generateToken(user).toBuilder()
                            .userId(user.getId())
                            .build());

                })
                //если его вдруг нет кинь ошибку
                .switchIfEmpty(Mono.error(new AuthException("Invalid username" , "KUZMIN_INVALID_USERNAME")));
    }
}
