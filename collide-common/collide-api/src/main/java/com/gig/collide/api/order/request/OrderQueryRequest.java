package com.gig.collide.api.order.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 订单查询请求 - 简洁版
 * 基于order-simple.sql的字段，支持常用查询条件
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderQueryRequest {

    /**
     * 用户ID - 查询某用户的订单
     */
    private Long userId;

    /**
     * 订单号 - 精确查询
     */
    private String orderNo;

    /**
     * 商品ID - 查询某商品的订单
     */
    private Long goodsId;

    /**
     * 订单状态：pending、paid、shipped、completed、cancelled
     */
    private String status;

    /**
     * 支付状态：unpaid、paid、refunded
     */
    private String payStatus;

    /**
     * 支付方式：alipay、wechat、balance
     */
    private String payMethod;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 支付时间开始
     */
    private LocalDateTime payTimeStart;

    /**
     * 支付时间结束
     */
    private LocalDateTime payTimeEnd;

    // =================== 分页参数 ===================
    
    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段：create_time、update_time、pay_time
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";
} 