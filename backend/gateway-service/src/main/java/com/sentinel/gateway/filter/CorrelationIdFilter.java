package com.sentinel.gateway.filter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements WebFilter, GlobalFilter, Ordered {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_ATTR = "sentinel.correlation.id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return processFilter(exchange, exchange1 -> chain.filter(exchange1));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return processFilter(exchange, exchange1 -> chain.filter(exchange1));
    }

    private Mono<Void> processFilter(ServerWebExchange exchange, FilterDelegate next) {
        if (exchange.getAttributes().containsKey(CORRELATION_ID_ATTR)) {
            return next.execute(exchange);
        }

        String correlationId = exchange.getRequest().getHeaders().getFirst(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        final String finalCorrelationId = correlationId;
        exchange.getAttributes().put(CORRELATION_ID_ATTR, finalCorrelationId);

        exchange.getResponse().beforeCommit(() -> {
            if (!exchange.getResponse().getHeaders().containsKey(CORRELATION_ID_HEADER)) {
                exchange.getResponse().getHeaders().set(CORRELATION_ID_HEADER, finalCorrelationId);
            }
            return Mono.empty();
        });

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(CORRELATION_ID_HEADER, finalCorrelationId)
                .build();

        return next.execute(exchange.mutate().request(mutatedRequest).build());
    }

    @FunctionalInterface
    private interface FilterDelegate {
        Mono<Void> execute(ServerWebExchange exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
