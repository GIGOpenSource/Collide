package com.gig.collide.users;

import com.gig.collide.file.config.OssConfiguration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author GIGOpenTeam
 */
@SpringBootApplication
@EnableDubbo
@Import(OssConfiguration.class)
public class CollideUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollideUserApplication.class, args);
    }
}
