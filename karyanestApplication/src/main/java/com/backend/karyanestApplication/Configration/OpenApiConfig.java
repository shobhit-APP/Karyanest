package com.backend.karyanestApplication.Configration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Documentation")
                        .version("1.0")
                        .description("API documentation for your application")
                )
                .servers(List.of(new Server().url("http://localhost:8080").description("local"),
                        new Server().url("http://localhost:8080").description("live")));
    }
}
