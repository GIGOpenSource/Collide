package com.gig.collide.api.like.request.condition;

import java.io.Serializable;

/**
 * 点赞查询条件接口
 * 所有具体的查询条件类都需要实现此接口
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface LikeQueryCondition extends Serializable {

    /**
     * 获取查询条件类型名称
     * 用于日志记录和调试
     * 
     * @return 条件类型名称
     */
    default String getConditionType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 验证查询条件是否有效
     * 
     * @return true 如果条件有效，false 否则
     */
    default boolean isValid() {
        return true;
    }
} 