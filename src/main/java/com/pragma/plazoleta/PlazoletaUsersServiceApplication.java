package com.pragma.plazoleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.pragma.plazoleta")
public class PlazoletaUsersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlazoletaUsersServiceApplication.class, args);
	}

}
