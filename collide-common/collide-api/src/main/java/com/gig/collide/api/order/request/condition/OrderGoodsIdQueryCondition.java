package com.gig.collide.api.order.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 按商品ID查询条件
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderGoodsIdQueryCondition implements OrderQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    private Long goodsId;

    @Override
    public String getConditionType() {
        return "GOODS_ID";
    }
} 