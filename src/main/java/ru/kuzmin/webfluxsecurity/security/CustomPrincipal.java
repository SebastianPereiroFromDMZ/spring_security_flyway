package ru.kuzmin.webfluxsecurity.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

//делаем своего принципала потому что стандартный принципал спринг секюрити имеет только юзернэйм, а у нас есть  (Id)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPrincipal implements Principal {

    private Long id;
    private String name;

}
