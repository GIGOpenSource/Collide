package com.gig.collide.content.infrastructure;

import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.favorite.FavoriteFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0")
    private TagFacadeService tagFacadeService;

    @DubboReference(version = "1.0.0")
    private CategoryFacadeService categoryFacadeService;

    @DubboReference(version = "1.0.0")
    private LikeFacadeService likeFacadeService;

    @DubboReference(version = "1.0.0")
    private FavoriteFacadeService favoriteFacadeService;


    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "tagFacadeService")
    public TagFacadeService tagFacadeService() {
        return tagFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "categoryFacadeService")
    public CategoryFacadeService categoryFacadeService() {
        return categoryFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "likeFacadeService")
    public LikeFacadeService likeFacadeService() {
        return likeFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "favoriteFacadeService")
    public FavoriteFacadeService favoriteFacadeService() {
        return favoriteFacadeService;
    }
}
