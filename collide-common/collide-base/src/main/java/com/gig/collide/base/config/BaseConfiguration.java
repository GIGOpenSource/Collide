package com.gig.collide.base.config;

import com.gig.collide.base.utils.SpringContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用模块的配置类
 *
 * @author GIGOpenSource
 */
@Configuration
public class BaseConfiguration {

    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}