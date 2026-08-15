package edu.team.carshopbackend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {

    /**
     * Provides OpenAPI metadata used by Swagger UI.
     *
     * @return OpenAPI info object
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("CarShop SprintBoard API")
                        .description("API documentation for the CarShopSprintBoard team project")
                        .version("1.0.0"));
    }
}
