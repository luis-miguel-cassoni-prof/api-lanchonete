package com.br.lanchonete.api_lanchonete.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration 
public class SwaggerConfiguration {

    @Bean 
    public OpenAPI customOpenAPI() {
        return new OpenAPI();
    }
}
