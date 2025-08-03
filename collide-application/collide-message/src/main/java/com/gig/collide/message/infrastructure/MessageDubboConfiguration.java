package com.gig.collide.message.infrastructure;

import com.gig.collide.api.user.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 消息模块 Dubbo 服务配置类
 * 用于配置 collide-message 模块引用的 Dubbo 服务
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Configuration
public class MessageDubboConfiguration {

    /**
     * 引用用户门面服务
     * 用于消息发送时验证发送者和接收者是否存在
     * version 必须与服务提供者一致
     * timeout 设置为 10 秒，重试 2 次
     */
    @DubboReference(version = "1.0.0", timeout = 10000, retries = 2)
    @Lazy // 懒加载，避免启动时循环依赖或不必要的初始化
    private UserFacadeService userFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }
}