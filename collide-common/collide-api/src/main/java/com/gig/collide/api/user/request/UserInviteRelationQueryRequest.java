package com.gig.collide.api.user.request;

import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户邀请关系查询请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserInviteRelationQueryRequest extends BaseRequest {

    /**
     * 查询条件
     */
    private UserQueryCondition userQueryCondition;

    /**
     * 分页参数
     */
    private Integer pageNum = 1;
    private Integer pageSize = 20;

    // ===================== 便捷构造器 =====================

    /**
     * 根据被邀请用户ID查询
     */
    public UserInviteRelationQueryRequest(Long userId) {
        UserInviteRelationUserIdQueryCondition condition = new UserInviteRelationUserIdQueryCondition();
        condition.setUserId(userId);
        this.userQueryCondition = condition;
    }

    /**
     * 根据邀请人ID查询被邀请用户列表
     */
    public static UserInviteRelationQueryRequest byInviterId(Long inviterId) {
        UserInviteRelationQueryRequest request = new UserInviteRelationQueryRequest();
        UserInviteRelationInviterIdQueryCondition condition = new UserInviteRelationInviterIdQueryCondition();
        condition.setInviterId(inviterId);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据邀请码查询
     */
    public static UserInviteRelationQueryRequest byInviteCode(String inviteCode) {
        UserInviteRelationQueryRequest request = new UserInviteRelationQueryRequest();
        UserInviteRelationInviteCodeQueryCondition condition = new UserInviteRelationInviteCodeQueryCondition();
        condition.setInviteCode(inviteCode);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据邀请层级查询
     */
    public static UserInviteRelationQueryRequest byInviteLevel(Integer inviteLevel) {
        UserInviteRelationQueryRequest request = new UserInviteRelationQueryRequest();
        UserInviteRelationInviteLevelQueryCondition condition = new UserInviteRelationInviteLevelQueryCondition();
        condition.setInviteLevel(inviteLevel);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据时间范围查询
     */
    public static UserInviteRelationQueryRequest byTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        UserInviteRelationQueryRequest request = new UserInviteRelationQueryRequest();
        UserInviteRelationTimeRangeQueryCondition condition = new UserInviteRelationTimeRangeQueryCondition();
        condition.setStartTime(startTime);
        condition.setEndTime(endTime);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 查询最近N天的邀请关系
     */
    public static UserInviteRelationQueryRequest byRecentDays(Integer days) {
        UserInviteRelationQueryRequest request = new UserInviteRelationQueryRequest();
        UserInviteRelationTimeRangeQueryCondition condition = new UserInviteRelationTimeRangeQueryCondition();
        condition.setRecentDays(days);
        request.setUserQueryCondition(condition);
        return request;
    }
} 