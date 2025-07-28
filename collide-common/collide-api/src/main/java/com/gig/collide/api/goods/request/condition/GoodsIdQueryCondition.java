package com.gig.collide.api.goods.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;

/**
 * 按商品ID查询条件
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class GoodsIdQueryCondition extends GoodsQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    public GoodsIdQueryCondition(Long goodsId) {
        this.goodsId = goodsId;
    }
} 