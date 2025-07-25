package com.gig.collide.business.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 处理静态资源访问和路由配置
 * 
 * @author GIG Team
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 静态资源配置
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI 静态资源
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/")
                .resourceChain(false);
        
        // 自定义静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // Favicon 处理
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }
} 