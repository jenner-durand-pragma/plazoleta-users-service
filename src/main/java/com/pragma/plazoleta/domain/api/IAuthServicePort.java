package com.pragma.plazoleta.domain.api;

public interface IAuthServicePort {

    String login(String email, String rawPassword);

}
