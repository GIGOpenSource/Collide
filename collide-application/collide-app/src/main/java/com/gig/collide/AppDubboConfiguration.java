package com.gig.collide;

// =================== 只导入需要远程调用的用户服务接口 ===================
import com.gig.collide.api.user.UserFacadeService;

// =================== Spring框架注解导入 ===================
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Collide应用Dubbo服务配置 - 用户服务远程调用测试
 * 
 * <p>架构说明：</p>
 * <ul>
 *   <li>用户服务：独立Dubbo微服务，远程调用</li>
 *   <li>其他服务：本地聚合，通过Maven依赖本地调用</li>
 * </ul>
 * 
 * @author Collide Team
 * @version 1.0.0 (用户服务远程调用测试版)
 * @since 2024-01-01
 */
@Configuration
public class AppDubboConfiguration {

    // =================== 用户服务@DubboReference定义 ===================
    
    @DubboReference(version = "1.0.0")
    private UserFacadeService userFacadeServiceRef;

    // =================== 优雅降级Bean注册 ===================

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeServiceRef;
    }

    // 注意：其他服务（order, payment, category, comment, content, favorite, follow, goods, like, search, social, tag）
    // 都是通过Maven依赖引入的本地模块，Spring会自动发现并注册它们的@Service Bean
    // 无需在这里配置@DubboReference
}