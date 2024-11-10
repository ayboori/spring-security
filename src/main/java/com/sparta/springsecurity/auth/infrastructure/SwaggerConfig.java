package com.sparta.springsecurity.auth.infrastructure;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;


@OpenAPIDefinition(
        info = @Info(title = "Spring Security + JWT API",
                description = "Spring Security와 JWT를 사용한 API 문서화",
                version = "v1"))
@Configuration
public class SwaggerConfig {

    // 서버 URL 설정 (배포 환경에 맞게 수정)
    private final Server[] SERVERS = {
            new Server().url("http://localhost:8080").description("Local Development Server")
    };

    @Bean
    public GroupedOpenApi publicAPI() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/**") // 모든 API 경로를 포함
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .components(new Components());
    }
}