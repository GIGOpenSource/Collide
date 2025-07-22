package com.gig.collide;

import com.gig.collide.api.box.service.BlindBoxManageFacadeService;
import com.gig.collide.api.box.service.BlindBoxReadFacadeService;
import com.gig.collide.api.collection.service.CollectionReadFacadeService;
import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.chain.service.ChainFacadeService;
import com.gig.collide.api.artist.service.ArtistFacadeService;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.api.user.service.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo配置
 *
 * @author GIG
 */
@Configuration
public class StartDubboConfiguration {

    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "1.0.0")
    private FollowFacadeService followFacadeService;

    @DubboReference(version = "1.0.0")
    private ArtistFacadeService artistFacadeService;

    @DubboReference(version = "1.0.0")
    private ChainFacadeService chainFacadeService;

    @DubboReference(version = "1.0.0")
    private OrderFacadeService orderFacadeService;

    @DubboReference(version = "1.0.0")
    private CollectionReadFacadeService collectionReadFacadeService;

    @DubboReference(version = "1.0.0")
    private GoodsFacadeService goodsFacadeService;

    @DubboReference(version = "1.0.0")
    private BlindBoxManageFacadeService blindBoxManageFacadeService;

    @DubboReference(version = "1.0.0")
    private BlindBoxReadFacadeService blindBoxReadFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "followFacadeService")
    public FollowFacadeService followFacadeService() {
        return followFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "artistFacadeService")
    public ArtistFacadeService artistFacadeService() {
        return artistFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "chainFacadeService")
    public ChainFacadeService chainFacadeService() {
        return chainFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "chainFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "collectionReadFacadeService")
    public CollectionReadFacadeService collectionReadFacadeService() {
        return collectionReadFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "blindBoxManageFacadeService")
    public BlindBoxManageFacadeService blindBoxManageFacadeService() {
        return blindBoxManageFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "blindBoxReadFacadeService")
    public BlindBoxReadFacadeService blindBoxReadFacadeService() {
        return blindBoxReadFacadeService;
    }

}
