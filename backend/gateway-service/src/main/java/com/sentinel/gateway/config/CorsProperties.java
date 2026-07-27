package com.sentinel.gateway.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentinel.cors")
public record CorsProperties(
        List<String> allowedOrigins
) {
}
