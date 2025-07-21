package com.gig.collide.api.collection.request;

import java.math.BigDecimal;

import com.gig.collide.api.goods.constant.GoodsEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author GIG
 * @date 2025/07/17
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CollectionModifyPriceRequest extends BaseCollectionRequest {

    /**
     * '价格'
     */
    private BigDecimal price;


    @Override
    public GoodsEvent getEventType() {
        return GoodsEvent.MODIFY_PRICE;
    }
}
