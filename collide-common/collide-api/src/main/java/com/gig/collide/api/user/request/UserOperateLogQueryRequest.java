package com.gig.collide.api.user.request;

import com.gig.collide.api.user.request.condition.*;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户操作日志查询请求
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
public class UserOperateLogQueryRequest extends BaseRequest {

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
     * 根据用户ID查询操作日志
     */
    public UserOperateLogQueryRequest(Long userId) {
        UserOperateLogUserIdQueryCondition condition = new UserOperateLogUserIdQueryCondition();
        condition.setUserId(userId);
        this.userQueryCondition = condition;
    }

    /**
     * 根据操作类型查询
     */
    public static UserOperateLogQueryRequest byOperateType(String operateType) {
        UserOperateLogQueryRequest request = new UserOperateLogQueryRequest();
        UserOperateLogOperateTypeQueryCondition condition = new UserOperateLogOperateTypeQueryCondition();
        condition.setOperateType(UserOperateLogOperateTypeQueryCondition.OperateType.valueOf(operateType));
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据IP地址查询
     */
    public static UserOperateLogQueryRequest byIpAddress(String ipAddress) {
        UserOperateLogQueryRequest request = new UserOperateLogQueryRequest();
        UserOperateLogIpAddressQueryCondition condition = new UserOperateLogIpAddressQueryCondition();
        condition.setIpAddress(ipAddress);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 根据时间范围查询
     */
    public static UserOperateLogQueryRequest byTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        UserOperateLogQueryRequest request = new UserOperateLogQueryRequest();
        UserOperateLogTimeRangeQueryCondition condition = new UserOperateLogTimeRangeQueryCondition();
        condition.setStartTime(startTime);
        condition.setEndTime(endTime);
        request.setUserQueryCondition(condition);
        return request;
    }

    /**
     * 查询最近N天的日志
     */
    public static UserOperateLogQueryRequest byRecentDays(Integer days) {
        UserOperateLogQueryRequest request = new UserOperateLogQueryRequest();
        UserOperateLogTimeRangeQueryCondition condition = new UserOperateLogTimeRangeQueryCondition();
        condition.setRecentDays(days);
        request.setUserQueryCondition(condition);
        return request;
    }
} 