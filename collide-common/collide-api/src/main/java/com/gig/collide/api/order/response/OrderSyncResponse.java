package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 订单同步响应
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
public class OrderSyncResponse extends BaseResponse {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 同步状态: SUCCESS-同步成功, FAILED-同步失败, NO_CHANGE-无变化
     */
    private String syncStatus;

    /**
     * 同步前状态
     */
    private String beforeStatus;

    /**
     * 同步后状态
     */
    private String afterStatus;

    /**
     * 同步详细信息
     */
    private Map<String, String> syncDetails;

    /**
     * 同步时间戳
     */
    private Long syncTimestamp;
} 