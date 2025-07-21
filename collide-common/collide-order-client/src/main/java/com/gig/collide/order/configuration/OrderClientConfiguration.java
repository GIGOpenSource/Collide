package com.gig.collide.order.configuration;

import com.gig.collide.api.goods.service.GoodsFacadeService;
import com.gig.collide.api.inventory.service.InventoryFacadeService;
import com.gig.collide.api.user.service.UserFacadeService;
import com.gig.collide.order.sharding.id.WorkerIdHolder;
import com.gig.collide.order.validator.GoodsBookValidator;
import com.gig.collide.order.validator.GoodsValidator;
import com.gig.collide.order.validator.StockValidator;
import com.gig.collide.order.validator.UserValidator;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GIG
 */
@Configuration
public class OrderClientConfiguration {

    @Bean
    public WorkerIdHolder workerIdHolder(RedissonClient redisson) {
        return new WorkerIdHolder(redisson);
    }

    @Bean
    public GoodsValidator goodsValidator(GoodsFacadeService goodsFacadeService) {
        return new GoodsValidator(goodsFacadeService);
    }

    @Bean
    public StockValidator stockValidator(InventoryFacadeService inventoryFacadeService) {
        return new StockValidator(inventoryFacadeService);
    }

    @Bean
    public UserValidator userValidator(UserFacadeService userFacadeService) {
        return new UserValidator(userFacadeService);
    }

    @Bean
    public GoodsBookValidator goodsBookValidator(GoodsFacadeService goodsFacadeService) {
        return new GoodsBookValidator(goodsFacadeService);
    }
}
