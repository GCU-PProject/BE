package com.glow.Glaw.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		io.swagger.v3.oas.models.servers.Server server = new io.swagger.v3.oas.models.servers.Server();
		server.setUrl("https://api.glaw.site");

		Info info = new Info()
			.title("GLAW API Documentation")
			.description("GLAW API 명세서")
			.version("1.0.0");

		SecurityScheme bearerScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		SecurityRequirement securityRequirement =
			new SecurityRequirement().addList("BearerAuth");

		return new OpenAPI()
			.info(info)
			.servers(List.of(server))
			.components(new Components().addSecuritySchemes("BearerAuth", bearerScheme))
			.security(List.of(securityRequirement));
	}
}
