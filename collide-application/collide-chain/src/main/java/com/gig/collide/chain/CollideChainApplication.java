package com.gig.collide.chain;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author GIG
 */
@SpringBootApplication(scanBasePackages = "com.gig.collide.chain")
@EnableDubbo
public class CollideChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideChainApplication.class, args);
    }

}
