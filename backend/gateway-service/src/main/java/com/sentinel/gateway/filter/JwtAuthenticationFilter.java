package com.sentinel.gateway.filter;

import com.sentinel.gateway.security.JwtValidator;
import com.sentinel.gateway.security.JwtValidator.JwtValidationException;
import com.sentinel.gateway.security.JwtValidator.ValidatedClaims;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtValidator jwtValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final List<String> publicPathPatterns = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/email-verification",
            "/actuator/**",
            "/v3/api-docs/**",
            "/swagger-ui/**"
    );

    public JwtAuthenticationFilter(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("missing_auth_header path={} ip={}", path, request.getRemoteAddress());
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization token is missing or malformed"));
        }

        String token = authHeader.substring(7).trim();
        try {
            ValidatedClaims claims = jwtValidator.validateToken(token);
            exchange.getAttributes().put(RequestLoggingFilter.USER_ID_ATTR, claims.userId());

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", claims.userId())
                    .header("X-User-Name", claims.username())
                    .header("X-User-Roles", String.join(",", claims.roles()))
                    .header("X-User-Permissions", String.join(",", claims.permissions()))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (JwtValidationException e) {
            log.warn("invalid_jwt_token path={} error={}", path, e.getMessage());
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage()));
        }
    }

    private boolean isPublicPath(String path) {
        return publicPathPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
