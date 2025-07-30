package com.gig.collide.follow.infrastructure;

import com.gig.collide.api.user.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FollowDubboConfiguration {
    /**
     * 用户服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private UserFacadeService userFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }
}
