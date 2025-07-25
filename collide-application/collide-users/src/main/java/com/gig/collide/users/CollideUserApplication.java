package com.gig.collide.users;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户服务启动类
 *
 * @author GIG
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.users", "com.gig.collide.cache"})
@EnableDubbo
@MapperScan("com.gig.collide.users.infrastructure.mapper")
public class CollideUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideUserApplication.class, args);
        System.out.println("========== Collide Users Service Started ==========");
        System.out.println("""
            ====================================
            🎉 Collide Users 模块启动成功！
            📝 用户服务已就绪
            🔗 端口: 9501
            ====================================
            """);
    }
}
