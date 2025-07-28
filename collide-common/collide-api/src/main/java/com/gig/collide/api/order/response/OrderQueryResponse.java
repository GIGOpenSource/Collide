package com.gig.collide.api.order.response;

import com.gig.collide.api.order.response.data.OrderInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 订单查询响应
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
public class OrderQueryResponse extends BaseResponse {

    /**
     * 订单信息列表
     */
    private List<OrderInfo> orderList;

    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 查询时间戳
     */
    private Long queryTimestamp;
} 