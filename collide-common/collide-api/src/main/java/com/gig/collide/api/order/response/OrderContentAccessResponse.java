package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 内容访问权限响应
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
public class OrderContentAccessResponse extends BaseResponse {

    /**
     * 是否有访问权限
     */
    private Boolean hasAccess;

    /**
     * 访问权限类型: PERMANENT-永久访问, TEMPORARY-临时访问, SUBSCRIPTION_BASED-基于订阅
     */
    private String accessType;

    /**
     * 权限状态: ACTIVE-激活状态, EXPIRED-已过期, REVOKED-已撤销
     */
    private String accessStatus;

    /**
     * 权限开始时间
     */
    private Long accessStartTime;

    /**
     * 权限结束时间（null表示永久）
     */
    private Long accessEndTime;

    /**
     * 剩余天数（临时权限）
     */
    private Integer remainingDays;

    /**
     * 权限来源订单号
     */
    private String sourceOrderNo;

    /**
     * 检查时间戳
     */
    private Long checkTimestamp;
} 