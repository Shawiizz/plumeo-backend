package fr.shawiizz.plumeo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plumeo API")
                        .description("Plumeo Backend API Documentation")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Shawiiz_z")
                                .email("contact@plumeo.fr")
                                .url("https://github.com/Shawiizz/plumeo-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server"),
                        new Server()
                                .url("https://api.plumeo.fr")
                                .description("Production server")
                ));
    }
}