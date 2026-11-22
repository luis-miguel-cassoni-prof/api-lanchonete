package com.br.lanchonete.api_lanchonete.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration 
public class SwaggerConfiguration {

    @Bean 
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
            .title("Lanchonete Simples API")
            .version("1.0.0")
            .description("Documentação da API. Autentique-se pela sessão e informe o token CSRF nos cabeçalhos.")
        )
        .components(new Components().addSecuritySchemes("csrfToken",
            new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("X-CSRF-TOKEN")))
        .addSecurityItem(new SecurityRequirement().addList("csrfToken"));
    }
}
