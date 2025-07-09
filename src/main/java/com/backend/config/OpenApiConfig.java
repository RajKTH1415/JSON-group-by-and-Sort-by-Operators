package com.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeDatasetOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("JSON group by and Sort By Operators")
                        .description("API-Documentation for JSON group by and Sort by Operators")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Rajkumar prasad")
                                .email("rajkumarprasadkth@gmail.com")
                                .url("https://github.com/RajKTH1415"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/RajKTH1415/JSON-group-by-and-Sort-by-Operators"));
    }
}
