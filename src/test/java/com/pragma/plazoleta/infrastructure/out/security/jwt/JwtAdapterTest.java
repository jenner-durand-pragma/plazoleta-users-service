package com.pragma.plazoleta.infrastructure.out.security.jwt;

import com.pragma.plazoleta.domain.model.Role;
import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.infrastructure.configuration.security.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAdapterTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtAdapter jwtAdapter;

    private User validUser;

    private static final String BASE64_SECRET = "bXktc3VwZXItc2VjcmV0LWtleS10aGF0LWlzLWF0LWxlYXN0LTMyLWJ5dGVzLWxvbmc=";

    @BeforeEach
    void setUp() {
        var ownerRole = Role.builder()
                .id(2L)
                .name("OWNER")
                .description("Restaurant owner")
                .build();

        validUser = User.builder()
                .id(10L)
                .name("Jenner")
                .lastName("Durand")
                .documentNumber("76859685")
                .phone("+51985768594")
                .birthDate(LocalDate.of(2002, 9, 21))
                .email("jenner.durand@plazoleta.com")
                .password("$2a$10$hashedPassword")
                .role(ownerRole)
                .build();
    }

    @Test
    @DisplayName("Should generate a valid JWT token with correct claims")
    void shouldGenerateValidToken() {
        when(jwtProperties.getSecret()).thenReturn(BASE64_SECRET);
        when(jwtProperties.getExpirationMs()).thenReturn(1000L * 60L * 60L);

        var token = jwtAdapter.generateToken(validUser);

        assertThat(token).isNotBlank();

        var decodedKey = Base64.getDecoder().decode(BASE64_SECRET);
        var secretKey = Keys.hmacShaKeyFor(decodedKey);

        var claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("jenner.durand@plazoleta.com");
        assertThat(claims.get("userId", Long.class)).isEqualTo(10L);
        assertThat(claims.get("role", String.class)).isEqualTo("OWNER");

        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }
}
