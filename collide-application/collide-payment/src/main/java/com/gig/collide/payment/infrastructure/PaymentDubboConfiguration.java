package com.gig.collide.payment.infrastructure;

import com.gig.collide.api.category.CategoryFacadeService;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.tag.TagFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付模块Dubbo配置类 - 缓存增强版
 * 对齐search模块设计风格，提供跨模块服务引用配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Configuration
public class PaymentDubboConfiguration {

    /**
     * 用户服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private UserFacadeService userFacadeService;

    /**
     * 订单服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private OrderFacadeService orderFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "orderFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }
}