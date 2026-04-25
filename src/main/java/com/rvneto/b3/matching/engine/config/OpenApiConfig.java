package com.rvneto.b3.matching.engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("B3 Matching Engine API")
                        .version("1.0.0")
                        .description("Simulates the B3 stock exchange matching engine. " +
                                "Receives orders via RabbitMQ, validates against real market prices from Redis, " +
                                "and returns execution results to the broker.")
                        .contact(new Contact()
                                .name("Roberto de Vargas Neto")
                                .url("https://www.linkedin.com/in/roberto-de-vargas/")));
    }
}
