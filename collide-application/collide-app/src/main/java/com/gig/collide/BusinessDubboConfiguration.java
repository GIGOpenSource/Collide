package com.gig.collide;


import com.gig.collide.api.favorite.service.FavoriteFacadeService;
import com.gig.collide.api.comment.service.CommentFacadeService;
import com.gig.collide.api.content.service.ContentFacadeService;
import com.gig.collide.api.follow.service.FollowFacadeService;
import com.gig.collide.api.like.service.LikeFacadeService;
import com.gig.collide.api.auth.service.AuthFacadeService;
import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.search.service.SearchFacadeService;
import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.api.order.service.OrderFacadeService;
import com.gig.collide.api.payment.service.PaymentFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Collide 业务模块 Dubbo 配置
 *
 * 集中管理所有业务模块的 Dubbo 服务引用，包括：
 * - UserFacadeService: 用户服务
 * - FollowFacadeService: 关注服务
 * - ContentFacadeService: 内容服务
 * - GoodsFacadeService: 商品服务
 * - OrderFacadeService: 订单服务
 * - PaymentFacadeService: 支付服务
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

    @DubboReference(version = "1.0.0")
    private CommentFacadeService commentFacadeService;

    @DubboReference(version = "1.0.0")
    private FavoriteFacadeService favoriteFacadeService;

    @DubboReference(version = "1.0.0")
    private SocialFacadeService socialFacadeService;

    @DubboReference(version = "1.0.0")
    private LikeFacadeService likeFacadeService;

    @DubboReference(version = "1.0.0")
    private AuthFacadeService authFacadeService;

    @DubboReference(version = "1.0.0")
    private CategoryFacadeService categoryFacadeService;

    @DubboReference(version = "1.0.0")
    private TagFacadeService tagFacadeService;

    @DubboReference(version = "1.0.0")
    private SearchFacadeService searchFacadeService;

    @DubboReference(version = "1.0.0")
    private GoodsFacadeService goodsFacadeService;

    @DubboReference(version = "1.0.0")
    private OrderFacadeService orderFacadeService;

    @DubboReference(version = "1.0.0")
    private PaymentFacadeService paymentFacadeService;

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

    @Bean
    @ConditionalOnMissingBean(name = "commentFacadeService")
    public CommentFacadeService commentFacadeService() {
        return commentFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "favoriteFacadeService")
    public FavoriteFacadeService favoriteFacadeService() {
        return favoriteFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "socialFacadeService")
    public SocialFacadeService socialFacadeService() {
        return socialFacadeService;
    }

    /**
     * 点赞服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "likeFacadeService")
    public LikeFacadeService likeFacadeService() {
        return likeFacadeService;
    }

    /**
     * 认证服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "authFacadeService")
    public AuthFacadeService authFacadeService() {
        return authFacadeService;
    }

    /**
     * 分类服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "categoryFacadeService")
    public CategoryFacadeService categoryFacadeService() {
        return categoryFacadeService;
    }

    /**
     * 标签服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "tagFacadeService")
    public TagFacadeService tagFacadeService() {
        return tagFacadeService;
    }

    /**
     * 搜索服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "searchFacadeService")
    public SearchFacadeService searchFacadeService() {
        return searchFacadeService;
    }

    /**
     * 商品服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }

    /**
     * 订单服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "orderFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }

    /**
     * 支付服务 Bean 配置
     */
    @Bean
    @ConditionalOnMissingBean(name = "paymentFacadeService")
    public PaymentFacadeService paymentFacadeService() {
        return paymentFacadeService;
    }
}