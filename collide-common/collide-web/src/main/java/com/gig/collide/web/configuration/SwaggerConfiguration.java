package com.gig.collide.web.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger API文档配置
 * 
 * @author GIG Team
 */
@Configuration
public class SwaggerConfiguration {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:collide}")
    private String applicationName;

    /**
     * 全局API配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .components(securityComponents())
                .addSecurityItem(securityRequirement());
    }

    /**
     * API信息
     */
    private Info apiInfo() {
        return new Info()
                .title("Collide API 文档")
                .description("Collide 微服务架构 - 在线API文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("GIG Team")
                        .email("support@gig.com")
                        .url("https://github.com/GIG-Team/Collide"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    /**
     * 服务器列表
     */
    private List<Server> serverList() {
        List<Server> servers = new ArrayList<>();
        
        // 本地开发环境
        servers.add(new Server()
                .url("http://localhost:" + serverPort)
                .description("本地开发环境"));
        
        // 网关环境
        servers.add(new Server()
                .url("http://localhost:8080")
                .description("API网关"));
        
        return servers;
    }

    /**
     * 安全组件配置
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("Bearer Token", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("请在请求头中添加 Authorization: Bearer {token}"));
    }

    /**
     * 安全要求配置
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("Bearer Token");
    }

    /**
     * 用户服务API分组
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("01-用户服务")
                .pathsToMatch("/api/v1/users/**", "/api/v1/files/**")
                .build();
    }

    /**
     * 认证服务API分组
     */
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("02-认证服务")
                .pathsToMatch("/api/v1/auth/**")
                .build();
    }

    /**
     * 内容服务API分组
     */
    @Bean
    public GroupedOpenApi contentApi() {
        return GroupedOpenApi.builder()
                .group("03-内容服务")
                .pathsToMatch("/api/v1/content/**")
                .build();
    }

    /**
     * 社交服务API分组
     */
    @Bean
    public GroupedOpenApi socialApi() {
        return GroupedOpenApi.builder()
                .group("04-社交服务")
                .pathsToMatch("/api/v1/social/**", "/api/v1/like/**", "/api/v1/favorite/**", "/api/v1/follow/**", "/api/v1/comment/**")
                .build();
    }

    /**
     * 业务聚合API分组
     */
    @Bean
    public GroupedOpenApi businessApi() {
        return GroupedOpenApi.builder()
                .group("05-业务聚合")
                .pathsToMatch("/api/v1/business/**", "/api/v1/test/**")
                .build();
    }

    /**
     * 系统监控API分组
     */
    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
                .group("99-系统监控")
                .pathsToMatch("/actuator/**")
                .build();
    }
} 