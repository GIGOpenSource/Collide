package com.gig.collide.users;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author GIGOpenTeam
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.users")
@EnableDubbo
public class CollideUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideUserApplication.class, args);
    }

}
