package com.gig.collide.api.order.request;

import com.gig.collide.api.order.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单查询请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class OrderQueryRequest extends BaseRequest {

    /**
     * 查询条件
     */
    private OrderQueryCondition orderQueryCondition;

    /**
     * 分页页码
     */
    private Integer pageNo = 1;

    /**
     * 分页大小
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField = "createTime";

    /**
     * 排序方向: ASC-升序, DESC-降序
     */
    private String sortOrder = "DESC";

    /**
     * 根据订单号查询
     */
    public OrderQueryRequest(String orderNo) {
        OrderNoQueryCondition condition = new OrderNoQueryCondition();
        condition.setOrderNo(orderNo);
        this.orderQueryCondition = condition;
    }

    /**
     * 根据用户ID查询
     */
    public OrderQueryRequest(Long userId) {
        OrderUserIdQueryCondition condition = new OrderUserIdQueryCondition();
        condition.setUserId(userId);
        this.orderQueryCondition = condition;
    }

    /**
     * 根据订单状态查询
     */
    public static OrderQueryRequest byStatus(String status) {
        OrderQueryRequest request = new OrderQueryRequest();
        OrderStatusQueryCondition condition = new OrderStatusQueryCondition();
        condition.setStatus(status);
        request.setOrderQueryCondition(condition);
        return request;
    }

    /**
     * 根据订单类型查询
     */
    public static OrderQueryRequest byType(String orderType) {
        OrderQueryRequest request = new OrderQueryRequest();
        OrderTypeQueryCondition condition = new OrderTypeQueryCondition();
        condition.setOrderType(orderType);
        request.setOrderQueryCondition(condition);
        return request;
    }

    /**
     * 根据时间范围查询
     */
    public static OrderQueryRequest byTimeRange(Long startTime, Long endTime) {
        OrderQueryRequest request = new OrderQueryRequest();
        OrderTimeRangeQueryCondition condition = new OrderTimeRangeQueryCondition();
        condition.setStartTime(startTime);
        condition.setEndTime(endTime);
        request.setOrderQueryCondition(condition);
        return request;
    }

    /**
     * 根据商品ID查询
     */
    public static OrderQueryRequest byGoodsId(Long goodsId) {
        OrderQueryRequest request = new OrderQueryRequest();
        OrderGoodsIdQueryCondition condition = new OrderGoodsIdQueryCondition();
        condition.setGoodsId(goodsId);
        request.setOrderQueryCondition(condition);
        return request;
    }

    /**
     * 根据内容ID查询
     */
    public static OrderQueryRequest byContentId(Long contentId) {
        OrderQueryRequest request = new OrderQueryRequest();
        OrderContentIdQueryCondition condition = new OrderContentIdQueryCondition();
        condition.setContentId(contentId);
        request.setOrderQueryCondition(condition);
        return request;
    }
} 