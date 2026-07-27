package com.sentinel.risk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sentinel Risk Engine & Security Intelligence API")
                        .version("0.1.0-SNAPSHOT")
                        .description("Contextual Risk Scoring Engine, Entity Profiling, and Security Posture Intelligence Service"));
    }
}
