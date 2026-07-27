package com.sentinel.report.config;

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
                        .title("Sentinel Security Analytics & Reporting API")
                        .version("0.1.0-SNAPSHOT")
                        .description("Security Dashboard Aggregation, Executive Reports, and Analytics API Service"));
    }
}
