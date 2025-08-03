package com.gig.collide.api.task;

import com.gig.collide.api.task.request.UserCheckinRequest;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.task.response.UserCheckinResponse;
import com.gig.collide.api.task.response.UserTaskResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户任务门面服务接口 - 签到专用版
 * 专注于用户签到功能
 * 
 * @author GIG Team
 * @version 1.0.0 (签到专用版)
 * @since 2024-01-16
 */
public interface UserTaskFacadeService {

    // =================== 签到核心功能 ===================

    /**
     * 用户签到
     * 用户点击签到按钮，完成每日签到并获得金币奖励
     */
    Result<UserCheckinResponse> checkin(UserCheckinRequest request);

    /**
     * 查询用户今日签到状态
     * 检查用户今天是否已经签到
     */
    Result<Boolean> getTodayCheckinStatus(Long userId);

    /**
     * 查询用户连续签到天数
     * 获取用户当前的连续签到天数
     */
    Result<Integer> getContinuousCheckinDays(Long userId);

    // =================== 签到历史查询 ===================

    /**
     * 分页查询用户签到记录
     * 查看用户的签到历史记录
     */
    Result<PageResponse<UserTaskResponse>> getUserCheckinHistory(UserTaskQueryRequest request);

    /**
     * 查询用户指定日期的签到记录
     * 检查用户某一天的签到情况
     */
    Result<UserTaskResponse> getUserCheckinRecord(Long userId, LocalDate date);

    /**
     * 查询用户本月签到记录
     * 获取用户当月的所有签到记录
     */
    Result<List<UserTaskResponse>> getMonthlyCheckinRecords(Long userId, LocalDate month);

    // =================== 签到统计信息 ===================

    /**
     * 查询用户签到统计
     * 获取用户的签到次数、连续签到等统计信息
     */
    Result<UserCheckinResponse> getUserCheckinStats(Long userId);
}