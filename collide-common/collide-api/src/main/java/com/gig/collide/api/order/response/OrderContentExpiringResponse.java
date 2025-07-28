package com.gig.collide.api.order.response;

import com.gig.collide.api.order.response.data.OrderContentAssociationInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 即将过期权限响应
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
public class OrderContentExpiringResponse extends BaseResponse {

    /**
     * 即将过期的内容权限列表
     */
    private List<OrderContentAssociationInfo> expiringContents;

    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 提前预警天数
     */
    private Integer alertDays;

    /**
     * 查询时间戳
     */
    private Long queryTimestamp;
} 