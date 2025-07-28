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
 * 用户内容权限列表响应
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
public class OrderUserContentResponse extends BaseResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 有效内容权限列表
     */
    private List<OrderContentAssociationInfo> validContents;

    /**
     * 即将过期内容权限列表
     */
    private List<OrderContentAssociationInfo> expiringContents;

    /**
     * 永久权限数量
     */
    private Integer permanentCount;

    /**
     * 临时权限数量
     */
    private Integer temporaryCount;

    /**
     * 订阅权限数量
     */
    private Integer subscriptionCount;

    /**
     * 查询时间戳
     */
    private Long queryTimestamp;
} 