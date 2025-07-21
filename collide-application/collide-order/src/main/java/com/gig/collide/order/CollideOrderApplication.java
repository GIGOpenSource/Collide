package com.gig.collide.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hollis
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.order")
@EnableDubbo
public class CollideOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideOrderApplication.class, args);
    }

}
