package com.gig.collide.search.infrastructure;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class SearchDubboConfiguration {
    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0")
    private ContentFacadeService contentFacadeService;

    @DubboReference(version = "1.0.0")
    private TagFacadeService tagFacadeService;

    @DubboReference(version = "1.0.0")
    private CategoryFacadeService categoryFacadeService;


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
    @ConditionalOnMissingBean(name = "tagFacadeService")
    public TagFacadeService tagFacadeService() {
        return tagFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "categoryFacadeService")
    public CategoryFacadeService categoryFacadeService() {
        return categoryFacadeService;
    }
}
