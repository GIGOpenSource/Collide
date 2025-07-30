package com.gig.collide.order.infrastructure;

import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.payment.PaymentFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.goods.GoodsFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单模块Dubbo配置类 - 缓存增强版
 * 对齐payment模块设计风格，提供跨模块服务引用配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Configuration
public class OrderDubboConfiguration {

    /**
     * 用户服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private UserFacadeService userFacadeService;

    /**
     * 支付服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private PaymentFacadeService paymentFacadeService;

    /**
     * 商品服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private GoodsFacadeService goodsFacadeService;

    @Bean
    @ConditionalOnMissingBean(name = "userFacadeService")
    public UserFacadeService userFacadeService() {
        return userFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "paymentFacadeService")
    public PaymentFacadeService paymentFacadeService() {
        return paymentFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "goodsFacadeService")
    public GoodsFacadeService goodsFacadeService() {
        return goodsFacadeService;
    }
}