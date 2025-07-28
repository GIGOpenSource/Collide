package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 续期内容访问权限响应
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
public class OrderContentRenewResponse extends BaseResponse {

    /**
     * 订单内容关联ID
     */
    private Long associationId;

    /**
     * 续期前结束时间
     */
    private Long beforeEndTime;

    /**
     * 续期后结束时间
     */
    private Long afterEndTime;

    /**
     * 续期天数
     */
    private Integer renewDays;

    /**
     * 续期时间戳
     */
    private Long renewTimestamp;
} 