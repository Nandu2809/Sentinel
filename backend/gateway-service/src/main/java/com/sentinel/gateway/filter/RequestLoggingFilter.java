package com.sentinel.gateway.filter;

import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    public static final String START_TIME_ATTR = "sentinel.start.time";
    public static final String USER_ID_ATTR = "sentinel.user.id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME_ATTR, startTime);

        ServerHttpRequest request = exchange.getRequest();
        String correlationId = exchange.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTR);
        String clientIp = getClientIp(request);

        log.info("incoming_request correlation_id={} method={} uri={} client_ip={}",
                correlationId, request.getMethod(), request.getURI().getPath(), clientIp);

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long start = exchange.getAttribute(START_TIME_ATTR);
            long duration = start != null ? System.currentTimeMillis() - start : -1;
            HttpStatusCode status = exchange.getResponse().getStatusCode();
            String userId = exchange.getAttribute(USER_ID_ATTR);

            log.info("completed_request correlation_id={} method={} uri={} status={} latency_ms={} user_id={}",
                    correlationId, request.getMethod(), request.getURI().getPath(),
                    status != null ? status.value() : 500, duration, userId != null ? userId : "anonymous");
        }));
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        return remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "unknown";
    }

    @Override
    public int getOrder() {
        return -50;
    }
}
