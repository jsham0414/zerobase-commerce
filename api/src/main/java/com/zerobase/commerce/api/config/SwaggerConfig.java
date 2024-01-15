package com.zerobase.commerce.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("0.0.1")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Swagger").description("Commerce API").version("0.0.1"));
    }
}
