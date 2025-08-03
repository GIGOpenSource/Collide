package com.gig.collide.favorite.infrastructure;

import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.comment.CommentFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 收藏模块Dubbo服务配置 - 缓存增强版
 * 对齐follow模块设计风格，提供必要的外部服务依赖
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Configuration
public class FavoriteDubboConfiguration {

    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private ContentFacadeService contentFacadeService;

    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private GoodsFacadeService goodsFacadeService;

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
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "commentFacadeService")
    public CommentFacadeService commentFacadeService() {
        return commentFacadeService;
    }
}
