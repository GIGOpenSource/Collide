package com.gig.collide;

import com.gig.collide.api.content.service.ContentFacadeService;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.api.user.service.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Collide 业务模块 Dubbo 配置
 * 参考 nft-turbo-app 的 BusinessDubboConfiguration 设计
 * 
 * 集中管理所有业务模块的 Dubbo 服务引用，包括：
 * - UserFacadeService: 用户服务
 * - FollowFacadeService: 关注服务
 * - ContentFacadeService: 内容服务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Configuration
public class BusinessDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0")
    private FollowFacadeService followFacadeService;

    @DubboReference(version = "1.0.0")
    private ContentFacadeService contentFacadeService;

    /**
     * 用户服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    /**
     * 关注服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "followFacadeService")
    public FollowFacadeService followFacadeService() {
        return followFacadeService;
    }

    /**
     * 内容服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "contentFacadeService")
    public ContentFacadeService contentFacadeService() {
        return contentFacadeService;
    }
} 