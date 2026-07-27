package com.sentinel.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentinel.bootstrap")
public record BootstrapProperties(
        String adminEmail,
        String adminUsername,
        String adminPassword
) {
}

