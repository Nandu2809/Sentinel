package com.sentinel.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class GatewayFilterIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private String validJwtToken;

    @BeforeEach
    void setUp() {
        String secret = "dGhpcy1pcy1hLXNlY3VyZS10ZXN0LWp3dC1zZWNyZXQtc3RyaW5nLXRvLXBhc3MtdmFsaWRhdGlvbg==";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        validJwtToken = Jwts.builder()
                .subject("security_admin")
                .claim("userId", "999e4567-e89b-12d3-a456-426614174999")
                .claim("roles", List.of("ADMIN"))
                .claim("permissions", List.of("USER_READ"))
                .issuer("sentinel-auth-service")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(key)
                .compact();
    }

    @Test
    @DisplayName("Protected endpoint without Authorization header returns HTTP 401 Unauthorized")
    void protectedEndpoint_MissingToken_Returns401() {
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().exists("X-Correlation-ID")
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.status").isEqualTo(401)
                .jsonPath("$.error").isEqualTo("Unauthorized");
    }

    @Test
    @DisplayName("Correlation ID header is automatically generated and returned in HTTP responses")
    void correlationId_GeneratedAndReturned() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Correlation-ID");
    }
}
