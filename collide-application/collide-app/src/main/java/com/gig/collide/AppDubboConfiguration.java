package com.gig.collide;

// =================== 只导入真正需要远程调用的服务接口 ===================
import com.gig.collide.api.user.UserFacadeService;
// 注意：其他服务都是本地模块，不需要远程调用

// =================== Spring框架注解导入 ===================
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Collide应用聚合启动类Dubbo服务配置
 * 
 * <p>架构说明：</p>
 * <ul>
 *   <li>采用NFT-Turbo混合架构模式：聚合单体 + 独立微服务</li>
 *   <li>聚合模块：通过Maven依赖直接引入，本地调用</li>
 *   <li>独立微服务：通过@DubboReference远程调用</li>
 * </ul>
 *
 * <p>聚合本地模块：</p>
 * <ul>
 *   <li>✅ 分类服务 - 内容分类、层级管理（本地）</li>
 *   <li>✅ 评论服务 - 评论回复、审核管理（本地）</li>
 *   <li>✅ 内容服务 - 内容创作、章节管理（本地）</li>
 *   <li>✅ 收藏服务 - 收藏管理、推荐系统（本地）</li>
 *   <li>✅ 关注服务 - 用户关注、粉丝管理（本地）</li>
 *   <li>✅ 商品服务 - 商品管理、库存控制（本地）</li>
 *   <li>✅ 点赞服务 - 点赞统计、热度计算（本地）</li>
 *   <li>✅ 订单服务 - 订单处理、状态管理（本地）</li>
 *   <li>✅ 支付服务 - 支付处理、回调管理（本地）</li>
 *   <li>✅ 搜索服务 - 搜索引擎、热搜管理（本地）</li>
 *   <li>✅ 社交服务 - 动态发布、社交互动（本地）</li>
 *   <li>✅ 标签服务 - 标签管理、兴趣推荐（本地）</li>
 * </ul>
 *
 * <p>独立微服务：</p>
 * <ul>
 *   <li>🔗 用户服务 - 用户管理、认证授权（远程调用）</li>
 * </ul>
 * 
 * @author Collide Team
 * @version 2.0.0 (混合架构版本)
 * @since 2024-01-01
 */
@Configuration
public class AppDubboConfiguration {

    // =================== 独立微服务远程引用 ===================
    
    /**
     * 用户管理服务引用 - 独立微服务，远程调用
     * 用户服务作为核心服务独立部署，支持多个业务模块调用
     */
    @DubboReference(version = "2.0.0")
    private UserFacadeService userFacadeService;

    // =================== Spring Bean注册方法 ===================
    
    /**
     * 注册用户管理服务Bean
     * 提供用户注册、登录、个人信息管理功能
     * 
     * @return UserFacadeService 用户服务实例（远程调用）
     */
    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }
    
    // 注意：其他服务（category, comment, content, favorite, follow, goods, like, order, payment, search, social, tag）
    // 都是通过Maven依赖引入的本地模块，Spring会自动发现并注册它们的@Service Bean
    // 不需要在这里通过@DubboReference进行远程调用配置
}