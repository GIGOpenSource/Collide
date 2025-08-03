package com.gig.collide.task.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.task.domain.entity.UserCheckinRecord;
import com.gig.collide.api.task.request.UserTaskQueryRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户签到领域服务接口 - 对应 t_user_checkin_record 表
 * 负责用户签到行为和奖励记录管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public interface UserCheckinService {

    // =================== 签到核心功能 ===================

    /**
     * 用户签到
     * 执行签到逻辑，计算奖励，发放金币
     */
    UserCheckinRecord checkin(Long userId, Long taskTemplateId, String checkinIp);

    /**
     * 检查用户今日是否已签到
     */
    boolean isTodayChecked(Long userId);

    /**
     * 获取用户当前连续签到天数
     */
    Integer getContinuousCheckinDays(Long userId);

    // =================== 签到记录查询 ===================

    /**
     * 根据用户ID和日期查询签到记录
     */
    UserCheckinRecord getCheckinRecord(Long userId, LocalDate date);

    /**
     * 分页查询用户签到记录
     */
    PageResponse<UserCheckinRecord> queryUserCheckinRecords(UserTaskQueryRequest request);

    /**
     * 查询用户指定月份的签到记录
     */
    List<UserCheckinRecord> getMonthlyCheckinRecords(Long userId, LocalDate month);

    /**
     * 获取用户最近的签到记录
     */
    UserCheckinRecord getLatestCheckinRecord(Long userId);

    // =================== 签到统计功能 ===================

    /**
     * 统计用户总签到次数
     */
    Integer getUserTotalCheckins(Long userId);

    /**
     * 统计用户本月签到次数
     */
    Integer getUserMonthlyCheckins(Long userId, LocalDate month);

    /**
     * 查询用户历史最长连续签到天数
     */
    Integer getUserMaxContinuousCheckinDays(Long userId);

    /**
     * 统计用户累计获得金币
     */
    Long getUserTotalRewardCoins(Long userId);

    /**
     * 构建用户签到统计信息
     * 包含总次数、月度次数、最长连续天数、累计金币等
     */
    UserCheckinRecord buildUserCheckinStats(Long userId);

    // =================== 管理员功能 ===================

    /**
     * 查询指定日期的所有签到记录
     */
    PageResponse<UserCheckinRecord> getCheckinRecordsByDate(LocalDate date, Integer page, Integer size);

    /**
     * 统计指定日期的签到用户数
     */
    Long countCheckinUsersByDate(LocalDate date);

    /**
     * 批量查询用户签到记录
     */
    List<UserCheckinRecord> batchGetCheckinRecords(List<Long> userIds, LocalDate date);

    // =================== 业务验证功能 ===================

    /**
     * 验证用户是否可以签到
     * 检查用户状态、签到限制等
     */
    boolean canCheckin(Long userId);

    /**
     * 计算签到奖励金币
     * 基于连续签到天数计算实际奖励
     */
    Integer calculateCheckinReward(Long userId, Integer baseReward);
}