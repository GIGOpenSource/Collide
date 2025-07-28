package com.gig.collide.api.user.service;

import com.gig.collide.api.user.request.*;
import com.gig.collide.api.user.response.*;
import com.gig.collide.api.user.response.data.UserOperateLogInfo;
import com.gig.collide.api.user.response.data.UserOperateLogStatisticsInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 用户操作日志门面服务接口
 * 提供用户操作日志管理功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface UserOperateLogFacadeService {

    /**
     * 创建用户操作日志
     * 
     * @param createRequest 创建请求
     * @return 创建响应
     */
    UserOperateLogCreateResponse createLog(UserOperateLogCreateRequest createRequest);

    /**
     * 查询用户操作日志
     * 
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    UserOperateLogQueryResponse<UserOperateLogInfo> queryLogs(UserOperateLogQueryRequest queryRequest);

    /**
     * 分页查询用户操作日志
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<UserOperateLogInfo> pageQueryLogs(UserOperateLogQueryRequest queryRequest);

    /**
     * 获取用户操作日志统计信息
     * 
     * @param statisticsRequest 统计请求
     * @return 统计响应
     */
    UserOperateLogStatisticsResponse getStatistics(UserOperateLogStatisticsRequest statisticsRequest);

    /**
     * 获取用户操作历史（最近N条）
     * 
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 操作历史列表
     */
    UserOperateLogQueryResponse<UserOperateLogInfo[]> getUserRecentLogs(Long userId, Integer limit);

    /**
     * 获取系统操作日志统计（今日）
     * 
     * @return 今日统计
     */
    UserOperateLogStatisticsResponse getTodayStatistics();

    /**
     * 获取系统操作日志统计（本周）
     * 
     * @return 本周统计
     */
    UserOperateLogStatisticsResponse getThisWeekStatistics();

    /**
     * 获取系统操作日志统计（本月）
     * 
     * @return 本月统计
     */
    UserOperateLogStatisticsResponse getThisMonthStatistics();

    /**
     * 根据IP地址查询操作日志
     * 
     * @param ipAddress IP地址
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页响应
     */
    PageResponse<UserOperateLogInfo> queryLogsByIp(String ipAddress, String startTime, String endTime, 
                                                  Integer pageNum, Integer pageSize);

    /**
     * 根据操作类型查询操作日志
     * 
     * @param operateType 操作类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 分页响应
     */
    PageResponse<UserOperateLogInfo> queryLogsByType(String operateType, String startTime, String endTime,
                                                    Integer pageNum, Integer pageSize);

    /**
     * 异步创建操作日志（无返回值，用于高频操作）
     * 
     * @param createRequest 创建请求
     */
    void createLogAsync(UserOperateLogCreateRequest createRequest);

    /**
     * 批量删除过期日志
     * 
     * @param days 保留天数
     * @param operatorId 操作员ID
     * @return 删除条数
     */
    Integer cleanExpiredLogs(Integer days, Long operatorId);
} 