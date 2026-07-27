package com.sentinel.gateway.security;

import com.sentinel.gateway.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {
    private final SecretKey secretKey;
    private final String expectedIssuer;

    public JwtValidator(JwtProperties jwtProperties) {
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expectedIssuer = jwtProperties.issuer();
    }

    public ValidatedClaims validateToken(String token) throws JwtValidationException {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (expectedIssuer != null && !expectedIssuer.isBlank() && !expectedIssuer.equals(claims.getIssuer())) {
                throw new JwtValidationException("JWT issuer mismatch");
            }

            String userId = claims.get("userId", String.class);
            String username = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);
            if (roles == null) {
                roles = Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<String> permissions = claims.get("permissions", List.class);
            if (permissions == null) {
                permissions = Collections.emptyList();
            }

            return new ValidatedClaims(userId != null ? userId : username, username, roles, permissions);
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("JWT token has expired", e);
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtValidationException("Invalid JWT token signature or structure", e);
        }
    }

    public record ValidatedClaims(
            String userId,
            String username,
            List<String> roles,
            List<String> permissions
    ) {
    }

    public static class JwtValidationException extends Exception {
        public JwtValidationException(String message) {
            super(message);
        }

        public JwtValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
