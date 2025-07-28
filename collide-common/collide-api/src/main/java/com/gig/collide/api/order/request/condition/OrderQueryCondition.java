package com.gig.collide.api.order.request.condition;

import java.io.Serializable;

/**
 * 订单查询条件接口
 * 定义订单查询条件的基础规范
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface OrderQueryCondition extends Serializable {

    /**
     * 获取条件类型
     * 
     * @return 条件类型
     */
    String getConditionType();
} 