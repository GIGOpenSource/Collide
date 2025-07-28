package com.gig.collide.api.goods.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;

/**
 * 按商品名称查询条件
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class GoodsNameQueryCondition extends GoodsQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
    private String name;

    /**
     * 是否精确匹配
     */
    private Boolean exactMatch = false;

    public GoodsNameQueryCondition(String name) {
        this.name = name;
    }

    public GoodsNameQueryCondition(String name, Boolean exactMatch) {
        this.name = name;
        this.exactMatch = exactMatch;
    }
} 