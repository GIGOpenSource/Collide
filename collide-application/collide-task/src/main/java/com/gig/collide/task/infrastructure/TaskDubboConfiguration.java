package com.gig.collide.task.infrastructure;

import com.gig.collide.api.user.UserFacadeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Task 模块 Dubbo 服务配置
 * 配置对外部服务的 Dubbo 引用
 * 
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@Configuration
public class TaskDubboConfiguration {

    /**
     * 用户门面服务 Dubbo 引用
     * 用于任务奖励发放时调用用户钱包服务
     */
    @DubboReference(version = "1.0.0", timeout = 10000, retries = 2)
    private UserFacadeService userFacadeService;

    /**
     * 提供用户门面服务 Bean
     * 供其他组件注入使用
     */
    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        log.info("配置 UserFacadeService Dubbo 引用，版本: 1.0.0");
        return userFacadeService;
    }
}