package com.gig.collide.follow.infrastructure;

import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.like.LikeFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 关注模块Dubbo配置类 - 缓存增强版
 * 对齐goods模块设计风格，提供跨模块服务引用配置
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Configuration
public class FollowDubboConfiguration {

    /**
     * 用户服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private UserFacadeService userFacadeService;

    /**
     * 内容服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private ContentFacadeService contentFacadeService;

    /**
     * 点赞服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private LikeFacadeService likeFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "contentFacadeService")
    public ContentFacadeService contentFacadeService() {
        return contentFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "likeFacadeService")
    public LikeFacadeService likeFacadeService() {
        return likeFacadeService;
    }
}
