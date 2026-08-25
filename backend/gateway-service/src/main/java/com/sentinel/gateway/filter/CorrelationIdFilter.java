package com.sentinel.gateway.filter;

import java.util.UUID;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorrelationIdFilter implements GlobalFilter, Ordered {
    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_ATTR = "sentinel.correlation.id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (exchange.getAttributes().containsKey(CORRELATION_ID_ATTR)) {
            return chain.filter(exchange);
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

        HttpHeaders mutableHeaders = new HttpHeaders();
        mutableHeaders.putAll(exchange.getRequest().getHeaders());
        mutableHeaders.set(CORRELATION_ID_HEADER, finalCorrelationId);

        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                return mutableHeaders;
            }
        };

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
