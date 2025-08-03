package com.gig.collide.social;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Social模块启动类
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@SpringBootApplication
@EnableDubbo
@MapperScan("com.gig.collide.social.infrastructure.mapper")
@EnableTransactionManagement
@EnableMethodCache(basePackages = "com.gig.collide.social")
@EnableCreateCacheAnnotation
@EnableScheduling
@EnableAsync
public class CollideSocialApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideSocialApplication.class, args);
        System.out.println("""
                
                 ██████╗ ██████╗ ██╗     ██╗     ██╗██████╗ ███████╗    ███████╗ ██████╗  ██████╗██╗ █████╗ ██╗     
                ██╔════╝██╔═══██╗██║     ██║     ██║██╔══██╗██╔════╝    ██╔════╝██╔═══██╗██╔════╝██║██╔══██╗██║     
                ██║     ██║   ██║██║     ██║     ██║██║  ██║█████╗      ███████╗██║   ██║██║     ██║███████║██║     
                ██║     ██║   ██║██║     ██║     ██║██║  ██║██╔══╝      ╚════██║██║   ██║██║     ██║██╔══██║██║     
                ╚██████╗╚██████╔╝███████╗███████╗██║██████╔╝███████╗    ███████║╚██████╔╝╚██████╗██║██║  ██║███████╗
                 ╚═════╝ ╚═════╝ ╚══════╝╚══════╝╚═╝╚═════╝ ╚══════╝    ╚══════╝ ╚═════╝  ╚═════╝╚═╝╚═╝  ╚═╝╚══════╝
                                                                                                                     
                🎬 Collide Social 社交视频平台模块启动成功！
                📱 Instagram级别的社交体验
                💰 付费内容 + 智能推荐 + 三级分类
                🚀 企业级社交视频平台解决方案
                
                """);
    }
}