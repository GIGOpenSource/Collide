package com.gig.collide.social.infrastructure;

import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.comment.CommentFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocialDubboConfiguration {
    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0")
    private CommentFacadeService commentFacadeService;

    @DubboReference(version = "1.0.0")
    private LikeFacadeService likeFacadeService;


    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "commentFacadeService")
    public CommentFacadeService commentFacadeService() {
        return commentFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "likeFacadeService")
    public LikeFacadeService likeFacadeService() {
        return likeFacadeService;
    }
}
