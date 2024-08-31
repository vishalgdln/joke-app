package com.example.joke_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Jokes API",
                version = "0.0.1",
                description = "Provide jokes on demand"
        ),
        servers = @Server(
                url = "/",
                description = "Default Server",
                variables = {
                        @ServerVariable(
                                name = "basePath",
                                description = "Base path for the API",
                                defaultValue = "/"
                        )
                }
        )
)
public class SwaggerConfig {
}