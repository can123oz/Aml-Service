package com.aml_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${springdoc.swagger-ui.enabled}")
    private boolean swaggerEnabled;

    @Bean
    @ConditionalOnProperty(name = "SWAGGER_ENABLED", havingValue = "true", matchIfMissing = true)
    public OpenAPI payoutProcessingServiceApiDocumentation() {
        if (!swaggerEnabled) {
            return null;
        }

        return new OpenAPI()
                .info(new Info().title("AML Service")
                        .description("AML Service")
                        .version("1.0"));
    }
}