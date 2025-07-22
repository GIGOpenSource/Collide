package com.gig.collide.goods;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author hollis
 */
@SpringBootApplication(scanBasePackages = {"com.gig.collide.goods","com.gig.collide.collection","com.gig.collide.box"})
@EnableDubbo(scanBasePackages = {"com.gig.collide.goods","com.gig.collide.collection","com.gig.collide.box"})
public class CollideGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideGoodsApplication.class, args);
    }

}
