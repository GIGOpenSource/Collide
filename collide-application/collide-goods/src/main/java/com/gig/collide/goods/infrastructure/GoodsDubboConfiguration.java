package com.gig.collide.goods.infrastructure;

import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.content.ContentFacadeService;
import com.gig.collide.api.user.UserFacadeService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class GoodsDubboConfiguration {
    /**
     * 用户服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private UserFacadeService userFacadeService;

    /**
     * 内容服务引用
     */
    @DubboReference(version = "1.0.0", check = false, timeout = 3000)
    private ContentFacadeService contentFacadeService;

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
    @ConditionalOnMissingBean(name = "contentFacadeService")
    public ContentFacadeService contentFacadeService() {
        return contentFacadeService;
    }

    @Bean
    @ConditionalOnMissingBean(name = "orderFacadeService")
    public OrderFacadeService orderFacadeService() {
        return orderFacadeService;
    }
}
