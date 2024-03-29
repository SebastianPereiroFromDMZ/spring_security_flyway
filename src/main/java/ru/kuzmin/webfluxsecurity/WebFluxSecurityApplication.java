package ru.kuzmin.webfluxsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebFluxSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebFluxSecurityApplication.class, args);
	}

}

//ВАЖНО ПО ТЕМЕ ПРОЭКТА : здесь 2 потока (flow)
//1: Генерация токена, тоесть человек дал юзернэйм и пароль приложение проверяет эти данные и если все хорошо генерим токен в классе SecurityService
//2: Валидация токена этого токена

//Здесь в этом приложении: какой у нас здесь путь запроса:
//1: когда пытаемся зарегистрироваться (вызываем метотод register в контроллере) мы отдаем данные по пользователю, путь открыт спокойно заходим

//2: при попытке логина (вызываем метотод login в контроллере) пытаемся получить аутонтификацию (класс SecurityService метод authenticated),
//аутонтификация происходит на основании юзернэйма и пароля, проверяем что пользователь есть в БД что он инайбл что пароли совпадают,
//если все хорошо вызываем метод генерации токена(generateToken)который сделан с помощью секретного ключа,
//устанавливаем время истечения токена(эксперейшн), подписываем токен с помощью алгоритма и подписи и потом этот токен уходит пользователю

//3:Когда мы уже пользуемся приложением (зарегистрированны и аутентифицированны) а именно мы при запросе (пользователь) отправляем токен,
//токен ловится фильром (bearerAuthenticationFilter в классе WebSecurityConfig) в этом фильтре используем BearerTokenServerAuthenticationConverter
//который из нашего запроса вытаскивает хэдер ауторизэйшн, тримирит его (тоесть убирает начало "Bearer "),
//получает токен, токен уходит на проверку (проверка заключается в корректности подписи и эксперейшн дате)
//далее после конвертации создаем обьект аутонтификации закидываем его в моно и после этого он уходит наружу, таким образом получаем доступ к нашей системе
