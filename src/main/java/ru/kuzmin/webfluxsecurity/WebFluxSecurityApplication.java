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
