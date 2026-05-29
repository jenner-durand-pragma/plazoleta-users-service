package com.pragma.plazoleta.domain.spi;

public interface IPasswordEncoderPort {

    String encode(String rawPassword);
    Boolean matches(String rawPassword, String encryptedPassword);

}
