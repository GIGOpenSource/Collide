package com.gig.collide.task.facade;

import com.gig.collide.api.task.UserTaskFacadeService;
import com.gig.collide.api.task.request.UserCheckinRequest;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.task.response.UserCheckinResponse;
import com.gig.collide.api.task.response.UserTaskResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.task.domain.entity.UserCheckinRecord;
import com.gig.collide.task.domain.service.UserCheckinService;
import com.gig.collide.task.infrastructure.cache.TaskCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户任务门面服务实现 - 签到专用版
 * Dubbo独立微服务提供者 - 负责用户签到行为和奖励记录管理
 * 
 * @author GIG Team
 * @version 1.0.0 (签到专用版)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserTaskFacadeService.class)
@RequiredArgsConstructor
public class UserTaskFacadeServiceImpl implements UserTaskFacadeService {

    private final UserCheckinService userCheckinService;

    // =================== 签到核心功能 ===================

    @Override
    @CacheInvalidate(name = TaskCacheConstant.USER_TODAY_CHECKIN_CACHE)
    @CacheInvalidate(name = TaskCacheConstant.USER_CONTINUOUS_DAYS_CACHE)
    @CacheInvalidate(name = TaskCacheConstant.USER_CHECKIN_STATS_CACHE)
    public Result<UserCheckinResponse> checkin(UserCheckinRequest request) {
        try {
            log.info("RPC用户签到请求: userId={}, taskTemplateId={}", 
                    request.getUserId(), request.getTaskTemplateId());
            
            // 参数验证
            if (request.getUserId() == null || request.getUserId() <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            if (request.getTaskTemplateId() == null || request.getTaskTemplateId() <= 0) {
                request.setTaskTemplateId(1L); // 默认每日签到任务
            }
            
            // 验证用户签到权限
            if (!userCheckinService.canCheckin(request.getUserId())) {
                return Result.error("CHECKIN_NOT_ALLOWED", "今日已签到或用户状态异常");
            }
            
            // 执行签到
            UserCheckinRecord checkinRecord = userCheckinService.checkin(
                    request.getUserId(), 
                    request.getTaskTemplateId(), 
                    request.getCheckinIp()
            );
            
            // 构建签到响应
            UserCheckinResponse response = buildCheckinResponse(checkinRecord);
            
            log.info("用户签到成功: userId={}, rewardCoins={}, continuousDays={}", 
                    request.getUserId(), response.getRewardCoins(), response.getContinuousDays());
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("用户签到失败: userId={}", request.getUserId(), e);
            return Result.error("CHECKIN_ERROR", "签到失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TaskCacheConstant.USER_TODAY_CHECKIN_CACHE,
            key = TaskCacheConstant.USER_TODAY_CHECKIN_KEY,
            expire = TaskCacheConstant.USER_CHECKIN_STATUS_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Boolean> getTodayCheckinStatus(Long userId) {
        try {
            log.debug("RPC查询用户今日签到状态: userId={}", userId);
            
            if (userId == null || userId <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            boolean checkedToday = userCheckinService.isTodayChecked(userId);
            log.debug("用户今日签到状态: userId={}, checked={}", userId, checkedToday);
            
            return Result.success(checkedToday);
        } catch (Exception e) {
            log.error("查询用户今日签到状态失败: userId={}", userId, e);
            return Result.error("GET_CHECKIN_STATUS_ERROR", "查询签到状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = TaskCacheConstant.USER_CONTINUOUS_DAYS_CACHE,
            key = TaskCacheConstant.USER_CONTINUOUS_DAYS_KEY,
            expire = TaskCacheConstant.USER_CHECKIN_STATUS_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<Integer> getContinuousCheckinDays(Long userId) {
        try {
            log.debug("RPC查询用户连续签到天数: userId={}", userId);
            
            if (userId == null || userId <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            Integer continuousDays = userCheckinService.getContinuousCheckinDays(userId);
            log.debug("用户连续签到天数: userId={}, days={}", userId, continuousDays);
            
            return Result.success(continuousDays);
        } catch (Exception e) {
            log.error("查询用户连续签到天数失败: userId={}", userId, e);
            return Result.error("GET_CONTINUOUS_DAYS_ERROR", "查询连续签到天数失败: " + e.getMessage());
        }
    }

    // =================== 签到历史查询 ===================

    @Override
    @Cached(name = TaskCacheConstant.USER_CHECKIN_RECORDS_CACHE,
            expire = TaskCacheConstant.USER_CHECKIN_RECORDS_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.LOCAL)
    public Result<PageResponse<UserTaskResponse>> getUserCheckinHistory(UserTaskQueryRequest request) {
        try {
            log.info("RPC分页查询用户签到记录: userId={}, page={}, size={}", 
                    request.getUserId(), request.getCurrentPage(), request.getPageSize());
            
            if (request.getUserId() == null || request.getUserId() <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            PageResponse<UserCheckinRecord> recordPage = userCheckinService.queryUserCheckinRecords(request);
            
            // 转换为响应DTO
            List<UserTaskResponse> responses = recordPage.getDatas().stream()
                    .map(this::convertToTaskResponse)
                    .collect(Collectors.toList());
            
            // 构建分页响应
            PageResponse<UserTaskResponse> responsePage = new PageResponse<>();
            responsePage.setDatas(responses);
            responsePage.setCurrentPage(recordPage.getCurrentPage());
            responsePage.setPageSize(recordPage.getPageSize());
            responsePage.setTotal(recordPage.getTotal());
            
            log.info("用户签到记录查询成功: userId={}, count={}, total={}", 
                    request.getUserId(), responses.size(), recordPage.getTotal());
            
            return Result.success(responsePage);
        } catch (Exception e) {
            log.error("查询用户签到记录失败: userId={}", request.getUserId(), e);
            return Result.error("GET_CHECKIN_HISTORY_ERROR", "查询签到历史失败: " + e.getMessage());
        }
    }

    @Override
    public Result<UserTaskResponse> getUserCheckinRecord(Long userId, LocalDate date) {
        try {
            log.debug("RPC查询用户指定日期签到记录: userId={}, date={}", userId, date);
            
            if (userId == null || userId <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            if (date == null) {
                return Result.error("INVALID_DATE", "日期无效");
            }
            
            UserCheckinRecord record = userCheckinService.getCheckinRecord(userId, date);
            if (record == null) {
                return Result.error("RECORD_NOT_FOUND", "该日期无签到记录");
            }
            
            UserTaskResponse response = convertToTaskResponse(record);
            log.debug("用户指定日期签到记录查询成功: userId={}, date={}", userId, date);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户指定日期签到记录失败: userId={}, date={}", userId, date, e);
            return Result.error("GET_CHECKIN_RECORD_ERROR", "查询签到记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserTaskResponse>> getMonthlyCheckinRecords(Long userId, LocalDate month) {
        try {
            log.info("RPC查询用户月度签到记录: userId={}, month={}", userId, month);
            
            if (userId == null || userId <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            if (month == null) {
                month = LocalDate.now().withDayOfMonth(1); // 默认当前月份
            }
            
            List<UserCheckinRecord> records = userCheckinService.getMonthlyCheckinRecords(userId, month);
            List<UserTaskResponse> responses = records.stream()
                    .map(this::convertToTaskResponse)
                    .collect(Collectors.toList());
            
            log.info("用户月度签到记录查询成功: userId={}, month={}, count={}", 
                    userId, month, responses.size());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户月度签到记录失败: userId={}, month={}", userId, month, e);
            return Result.error("GET_MONTHLY_RECORDS_ERROR", "查询月度签到记录失败: " + e.getMessage());
        }
    }

    // =================== 签到统计信息 ===================

    @Override
    @Cached(name = TaskCacheConstant.USER_CHECKIN_STATS_CACHE,
            key = TaskCacheConstant.USER_CHECKIN_STATS_KEY,
            expire = TaskCacheConstant.USER_CHECKIN_STATS_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserCheckinResponse> getUserCheckinStats(Long userId) {
        try {
            log.info("RPC查询用户签到统计信息: userId={}", userId);
            
            if (userId == null || userId <= 0) {
                return Result.error("INVALID_USER_ID", "用户ID无效");
            }
            
            // 构建签到统计信息
            UserCheckinResponse stats = new UserCheckinResponse();
            stats.setUserId(userId);
            
            // 获取各项统计数据
            LocalDate now = LocalDate.now();
            stats.setTotalCheckinCount(userCheckinService.getUserTotalCheckins(userId));
            stats.setMonthlyCheckinCount(userCheckinService.getUserMonthlyCheckins(userId, now));
            stats.setMaxContinuousDays(userCheckinService.getUserMaxContinuousCheckinDays(userId));
            stats.setTotalRewardCoins(userCheckinService.getUserTotalRewardCoins(userId));
            stats.setContinuousDays(userCheckinService.getContinuousCheckinDays(userId));
            
            log.info("用户签到统计信息查询成功: userId={}, totalCount={}, continuousDays={}", 
                    userId, stats.getTotalCheckinCount(), stats.getContinuousDays());
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("查询用户签到统计信息失败: userId={}", userId, e);
            return Result.error("GET_CHECKIN_STATS_ERROR", "查询签到统计失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 构建签到响应
     */
    private UserCheckinResponse buildCheckinResponse(UserCheckinRecord record) {
        UserCheckinResponse response = new UserCheckinResponse();
        
        // 基础信息
        response.setUserId(record.getUserId());
        response.setCheckinDate(record.getCheckinDate());
        response.setRewardCoins(record.getRewardCoins());
        response.setContinuousDays(record.getContinuousDays());
        response.setIsBonus(record.hasBonus());
        response.setCheckinTime(record.getCheckinTime());
        
        // 构建成功消息
        response.buildSuccessMessage();
        
        // 获取统计信息
        try {
            LocalDate now = LocalDate.now();
            response.setTotalCheckinCount(userCheckinService.getUserTotalCheckins(record.getUserId()));
            response.setMonthlyCheckinCount(userCheckinService.getUserMonthlyCheckins(record.getUserId(), now));
            response.setMaxContinuousDays(userCheckinService.getUserMaxContinuousCheckinDays(record.getUserId()));
            response.setTotalRewardCoins(userCheckinService.getUserTotalRewardCoins(record.getUserId()));
        } catch (Exception e) {
            log.warn("获取用户签到统计信息失败: userId={}", record.getUserId(), e);
        }
        
        return response;
    }

    /**
     * 转换为任务响应DTO
     */
    private UserTaskResponse convertToTaskResponse(UserCheckinRecord record) {
        if (record == null) {
            return null;
        }
        
        UserTaskResponse response = new UserTaskResponse();
        BeanUtils.copyProperties(record, response);
        
        // 设置任务名称
        response.setTaskName("每日签到");
        
        return response;
    }
}