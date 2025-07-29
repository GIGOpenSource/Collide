package com.gig.collide;


import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.payment.PaymentFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppDubboConfiguration {

    @DubboReference(version = "2.0.0")
    private UserFacadeService userFacadeService;

    @DubboReference(version = "2.0.0")
    private OrderFacadeService orderFacadeService;

    @DubboReference(version = "2.0.0")
    private PaymentFacadeService paymentFacadeService;


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

    @Bean
    @ConditionalOnMissingBean(name = "paymentFacadeService")
    public PaymentFacadeService paymentFacadeService() {
        return paymentFacadeService;
    }
}