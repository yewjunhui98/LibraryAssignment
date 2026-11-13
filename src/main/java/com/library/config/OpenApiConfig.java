package com.library.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libraryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Library API")
                .version("1.0.0")
                .description("""
                        Register borrowers and book copies, list borrowers, ISBNs, and copies, \
                        and borrow or return a copy on behalf of a borrower. \
                        Interactive documentation is served by Swagger UI."""));
    }
}
