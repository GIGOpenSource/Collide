package com.gig.collide.like.infrastructure;

import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.comment.CommentFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 点赞模块Dubbo配置类 - 缓存增强版
 * 对齐order模块设计风格，提供跨模块服务引用配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Configuration
public class LikeDubboConfiguration {

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
     * 评论服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private CommentFacadeService commentFacadeService;

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
    @ConditionalOnMissingBean(name = "commentFacadeService")
    public CommentFacadeService commentFacadeService() {
        return commentFacadeService;
    }
}
