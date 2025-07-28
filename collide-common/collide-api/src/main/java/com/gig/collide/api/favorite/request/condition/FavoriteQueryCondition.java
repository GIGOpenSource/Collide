package com.gig.collide.api.favorite.request.condition;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 收藏查询条件基础接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Schema(description = "收藏查询条件")
public interface FavoriteQueryCondition extends Serializable {

    /**
     * 获取条件类型
     * 
     * @return 条件类型描述
     */
    String getConditionType();

    /**
     * 验证条件参数是否有效
     * 
     * @return true-有效，false-无效
     */
    default boolean isValid() {
        return true;
    }
} 