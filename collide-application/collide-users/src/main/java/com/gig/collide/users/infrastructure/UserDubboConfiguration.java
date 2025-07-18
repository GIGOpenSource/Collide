package com.gig.collide.users.infrastructure;

import com.gig.collide.api.chain.service.ChainFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class UserDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private ChainFacadeService chainFacadeService;


    @Bean
    @ConditionalOnMissingBean(name = "chainFacadeService")
    public ChainFacadeService chainFacadeService() {
        return chainFacadeService;
    }
}
