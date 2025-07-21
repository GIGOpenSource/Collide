package com.gig.collide.order.domain.validator;

import com.gig.collide.order.validator.GoodsBookValidator;
import com.gig.collide.order.validator.GoodsValidator;
import com.gig.collide.order.validator.OrderCreateValidator;
import com.gig.collide.order.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 订单创建校验器配置
 *
 * @author hollis
 */
@Configuration
public class OrderCreateValidatorConfig {

    @Autowired
    private GoodsValidator goodsValidator;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private GoodsBookValidator goodsBookValidator;

    @Bean
    public OrderCreateValidator orderValidatorChain() {
        userValidator.setNext(goodsValidator);
        goodsValidator.setNext(goodsBookValidator);
        return userValidator;
    }
}
