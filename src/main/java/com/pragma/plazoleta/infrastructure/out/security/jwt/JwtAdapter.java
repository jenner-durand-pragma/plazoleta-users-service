package com.pragma.plazoleta.infrastructure.out.security.jwt;

import com.pragma.plazoleta.domain.model.User;
import com.pragma.plazoleta.domain.spi.ITokenServicePort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.ITokenValidationPort;
import com.pragma.plazoleta.infrastructure.configuration.security.token.exception.InvalidTokenException;
import com.pragma.plazoleta.infrastructure.configuration.security.token.dto.TokenPayload;
import com.pragma.plazoleta.infrastructure.out.security.jwt.configuration.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAdapter implements ITokenServicePort, ITokenValidationPort {

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    private final JwtProperties jwtProperties;

    @Override
    public String generateToken(User user) {
        var now = new Date();
        var expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_ROLE, user.getRole().getName())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public TokenPayload validate(String token) {
        try {
            var claims = parseClaims(token);
            return new TokenPayload(
                    claims.get(CLAIM_USER_ID, Long.class),
                    claims.getSubject(),
                    claims.get(CLAIM_ROLE, String.class)
            );
        } catch (JwtException | IllegalArgumentException ex) {
            throw new InvalidTokenException("Invalid or expired token", ex);
        }
    }

    private SecretKey signingKey() {
        var decoded = Base64.getDecoder().decode(jwtProperties.getSecret());

        return Keys.hmacShaKeyFor(decoded);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
