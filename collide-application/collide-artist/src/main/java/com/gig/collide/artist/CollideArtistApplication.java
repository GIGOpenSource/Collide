package com.gig.collide.artist;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author GIGOpenTeam
 */
@SpringBootApplication
@EnableDubbo
public class CollideArtistApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideArtistApplication.class, args);
    }
}
