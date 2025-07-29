package com.gig.collide;

// =================== Facade服务接口导入 ===================
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.comment.CommentFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.favorite.FavoriteFacadeService;
import com.gig.collide.api.follow.FollowFacadeService;
import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.like.LikeFacadeService;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.payment.PaymentFacadeService;
import com.gig.collide.api.search.SearchFacadeService;
import com.gig.collide.api.social.SocialFacadeService;
import com.gig.collide.api.tag.TagFacadeService;

// =================== Spring框架注解导入 ===================
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Collide应用聚合启动类Dubbo服务配置
 * 
 * <p>功能说明：</p>
 * <ul>
 *   <li>作为聚合启动类，需要注册所有模块的Dubbo Facade服务</li>
 *   <li>使用@DubboReference注解引用远程服务提供者</li>
 *   <li>为每个服务创建Bean供应用内部调用</li>
 *   <li>支持全球化应用，统一UTC时间戳处理</li>
 * </ul>
 *
 * <p>模块服务清单：</p>
 * <ul>
 *   <li>用户服务 - 用户管理、认证授权</li>
 *   <li>分类服务 - 内容分类、层级管理</li>
 *   <li>评论服务 - 评论回复、审核管理</li>
 *   <li>内容服务 - 内容创作、章节管理</li>
 *   <li>收藏服务 - 收藏管理、推荐系统</li>
 *   <li>关注服务 - 用户关注、粉丝管理</li>
 *   <li>商品服务 - 商品管理、库存控制</li>
 *   <li>点赞服务 - 点赞统计、热度计算</li>
 *   <li>订单服务 - 订单处理、状态管理</li>
 *   <li>支付服务 - 支付处理、回调管理</li>
 *   <li>搜索服务 - 搜索引擎、热搜管理</li>
 *   <li>社交服务 - 动态发布、社交互动</li>
 *   <li>标签服务 - 标签管理、兴趣推荐</li>
 * </ul>
 * 
 * @author Collide Team
 * @version 2.0.0
 * @since 2024-01-01
 */
@Configuration
public class AppDubboConfiguration {

    // =================== Dubbo服务引用声明 ===================
    
    /**
     * 用户管理服务引用 - 负责用户注册、登录、个人信息管理
     * 端口：9601
     */
    @DubboReference(version = "2.0.0")
    private UserFacadeService userFacadeService;
    
    /**
     * 分类管理服务引用 - 负责内容分类、层级结构管理
     * 端口：9602
     */
    @DubboReference(version = "2.0.0")
    private CategoryFacadeService categoryFacadeService;
    
    /**
     * 评论管理服务引用 - 负责评论发布、回复、审核管理
     * 端口：9603
     */
    @DubboReference(version = "2.0.0")
    private CommentFacadeService commentFacadeService;
    
    /**
     * 内容管理服务引用 - 负责小说、漫画、视频等内容创作管理
     * 端口：9604
     */
    @DubboReference(version = "2.0.0")
    private ContentFacadeService contentFacadeService;
    
    /**
     * 收藏管理服务引用 - 负责用户收藏、收藏夹管理、推荐算法
     * 端口：9605
     */
    @DubboReference(version = "2.0.0")
    private FavoriteFacadeService favoriteFacadeService;
    
    /**
     * 关注管理服务引用 - 负责用户关注、粉丝关系、互相关注检测
     * 端口：9606
     */
    @DubboReference(version = "2.0.0")
    private FollowFacadeService followFacadeService;
    
    /**
     * 商品管理服务引用 - 负责商品上架、库存管理、价格控制
     * 端口：9607
     */
    @DubboReference(version = "2.0.0")
    private GoodsFacadeService goodsFacadeService;
    
    /**
     * 点赞管理服务引用 - 负责点赞统计、热度计算、防重复处理
     * 端口：9608
     */
    @DubboReference(version = "2.0.0")
    private LikeFacadeService likeFacadeService;
    
    /**
     * 订单管理服务引用 - 负责订单创建、支付、取消、状态同步
     * 端口：9609
     */
    @DubboReference(version = "2.0.0")
    private OrderFacadeService orderFacadeService;
    
    /**
     * 支付管理服务引用 - 负责支付处理、回调、风控、多渠道支持
     * 端口：9610
     */
    @DubboReference(version = "2.0.0")
    private PaymentFacadeService paymentFacadeService;
    
    /**
     * 搜索管理服务引用 - 负责全文搜索、搜索历史、热搜推荐
     * 端口：9611
     */
    @DubboReference(version = "2.0.0")
    private SearchFacadeService searchFacadeService;
    
    /**
     * 社交管理服务引用 - 负责动态发布、社交互动、内容推荐
     * 端口：9612
     */
    @DubboReference(version = "2.0.0")
    private SocialFacadeService socialFacadeService;
    
    /**
     * 标签管理服务引用 - 负责标签创建、兴趣管理、智能推荐
     * 端口：9613
     */
    @DubboReference(version = "2.0.0")
    private TagFacadeService tagFacadeService;

    // =================== Spring Bean注册方法 ===================
    
    /**
     * 注册用户管理服务Bean
     * 提供用户注册、登录、个人信息管理功能
     * 
     * @return UserFacadeService 用户服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }
    
    /**
     * 注册分类管理服务Bean
     * 提供内容分类、层级结构管理功能
     * 
     * @return CategoryFacadeService 分类服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "categoryFacadeService")
    public CategoryFacadeService categoryFacadeService() {
        return categoryFacadeService;
    }
    
    /**
     * 注册评论管理服务Bean
     * 提供评论发布、回复、审核管理功能
     * 
     * @return CommentFacadeService 评论服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "commentFacadeService")
    public CommentFacadeService commentFacadeService() {
        return commentFacadeService;
    }
    
    /**
     * 注册内容管理服务Bean
     * 提供小说、漫画、视频等内容创作管理功能
     * 
     * @return ContentFacadeService 内容服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "contentFacadeService")
    public ContentFacadeService contentFacadeService() {
        return contentFacadeService;
    }
    
    /**
     * 注册收藏管理服务Bean
     * 提供用户收藏、收藏夹管理、推荐算法功能
     * 
     * @return FavoriteFacadeService 收藏服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "favoriteFacadeService")
    public FavoriteFacadeService favoriteFacadeService() {
        return favoriteFacadeService;
    }
    
    /**
     * 注册关注管理服务Bean
     * 提供用户关注、粉丝关系、互相关注检测功能
     * 
     * @return FollowFacadeService 关注服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "followFacadeService")
    public FollowFacadeService followFacadeService() {
        return followFacadeService;
    }
    
    /**
     * 注册商品管理服务Bean
     * 提供商品上架、库存管理、价格控制功能
     * 
     * @return GoodsFacadeService 商品服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }
    
    /**
     * 注册点赞管理服务Bean
     * 提供点赞统计、热度计算、防重复处理功能
     * 
     * @return LikeFacadeService 点赞服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "likeFacadeService")
    public LikeFacadeService likeFacadeService() {
        return likeFacadeService;
    }
    
    /**
     * 注册订单管理服务Bean
     * 提供订单创建、支付、取消、状态同步功能
     * 
     * @return OrderFacadeService 订单服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "orderFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }
    
    /**
     * 注册支付管理服务Bean
     * 提供支付处理、回调、风控、多渠道支持功能
     * 
     * @return PaymentFacadeService 支付服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "paymentFacadeService")
    public PaymentFacadeService paymentFacadeService() {
        return paymentFacadeService;
    }
    
    /**
     * 注册搜索管理服务Bean
     * 提供全文搜索、搜索历史、热搜推荐功能
     * 
     * @return SearchFacadeService 搜索服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "searchFacadeService")
    public SearchFacadeService searchFacadeService() {
        return searchFacadeService;
    }
    
    /**
     * 注册社交管理服务Bean
     * 提供动态发布、社交互动、内容推荐功能
     * 
     * @return SocialFacadeService 社交服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "socialFacadeService")
    public SocialFacadeService socialFacadeService() {
        return socialFacadeService;
    }
    
    /**
     * 注册标签管理服务Bean
     * 提供标签创建、兴趣管理、智能推荐功能
     * 
     * @return TagFacadeService 标签服务实例
     */
    @Bean
    @ConditionalOnMissingBean(name = "tagFacadeService")
    public TagFacadeService tagFacadeService() {
        return tagFacadeService;
    }
}