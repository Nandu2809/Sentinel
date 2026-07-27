package com.sentinel.gateway.security;

import com.sentinel.gateway.config.JwtProperties;
import com.sentinel.gateway.security.JwtValidator.JwtValidationException;
import com.sentinel.gateway.security.JwtValidator.ValidatedClaims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtValidatorTest {
    private static final String SECRET_STRING = "dGhpcy1pcy1hLXNlY3VyZS10ZXN0LWp3dC1zZWNyZXQtc3RyaW5nLXRvLXBhc3MtdmFsaWRhdGlvbg==";
    private static final String ISSUER = "sentinel-auth-service";

    private JwtValidator jwtValidator;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(SECRET_STRING, ISSUER);
        jwtValidator = new JwtValidator(properties);
        secretKey = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Should successfully validate a legitimate JWT access token")
    void validateToken_Success() throws Exception {
        String token = Jwts.builder()
                .subject("john_doe")
                .claim("userId", "123e4567-e89b-12d3-a456-426614174000")
                .claim("roles", List.of("ADMIN"))
                .claim("permissions", List.of("USER_READ", "USER_WRITE"))
                .issuer(ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(secretKey)
                .compact();

        ValidatedClaims claims = jwtValidator.validateToken(token);

        assertNotNull(claims);
        assertEquals("123e4567-e89b-12d3-a456-426614174000", claims.userId());
        assertEquals("john_doe", claims.username());
        assertTrue(claims.roles().contains("ADMIN"));
        assertTrue(claims.permissions().contains("USER_READ"));
    }

    @Test
    @DisplayName("Should throw exception when JWT access token is expired")
    void validateToken_Expired() {
        String expiredToken = Jwts.builder()
                .subject("john_doe")
                .issuer(ISSUER)
                .issuedAt(new Date(System.currentTimeMillis() - 1200000))
                .expiration(new Date(System.currentTimeMillis() - 600000))
                .signWith(secretKey)
                .compact();

        JwtValidationException exception = assertThrows(JwtValidationException.class,
                () -> jwtValidator.validateToken(expiredToken));
        assertTrue(exception.getMessage().contains("expired"));
    }

    @Test
    @DisplayName("Should throw exception when JWT signature is tampered or malformed")
    void validateToken_InvalidSignature() {
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0In0.invalid_signature";

        assertThrows(JwtValidationException.class, () -> jwtValidator.validateToken(malformedToken));
    }
}
