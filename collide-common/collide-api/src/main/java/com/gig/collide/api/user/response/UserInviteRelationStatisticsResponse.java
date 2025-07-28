package com.gig.collide.api.user.response;

import com.gig.collide.api.user.response.data.UserInviteStatisticsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户邀请关系统计响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class UserInviteRelationStatisticsResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 统计信息
     */
    private UserInviteStatisticsInfo statisticsInfo;

    /**
     * 统计维度
     */
    private String statisticsDimension;

    /**
     * 统计生成时间
     */
    private String generateTime;

    /**
     * 缓存有效期（秒）
     */
    private Integer cacheExpire;

    /**
     * 操作提示信息
     */
    private String message;
} 