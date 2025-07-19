package com.gig.collide;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.gig.collide")
@EnableDubbo(scanBasePackages = {"com.gig.collide"})
public class CollideStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideStartApplication.class, args);
    }

}
