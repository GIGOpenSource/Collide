package com.gig.collide.task.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.task.UserTaskFacadeService;
import com.gig.collide.api.task.request.UserCheckinRequest;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.task.response.UserCheckinResponse;
import com.gig.collide.api.task.response.UserTaskResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户任务控制器 - 签到专用版
 * 提供用户签到功能的HTTP接口
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户任务管理", description = "用户签到功能接口")
public class UserTaskController {

    @DubboReference
    private UserTaskFacadeService userTaskFacadeService;

    // =================== 签到核心功能 ===================

    /**
     * 用户签到
     */
    @PostMapping("/checkin")
    @SaCheckLogin
    @Operation(summary = "用户签到", description = "用户每日签到获取金币奖励")
    public Result<UserCheckinResponse> checkin(HttpServletRequest request) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.info("REST用户签到: userId={}", userId);
            
            // 构建签到请求
            UserCheckinRequest checkinRequest = new UserCheckinRequest();
            checkinRequest.setUserId(userId);
            checkinRequest.setTaskTemplateId(1L); // 默认每日签到任务
            checkinRequest.setCheckinIp(getClientIpAddress(request));
            
            return userTaskFacadeService.checkin(checkinRequest);
        } catch (Exception e) {
            log.error("用户签到异常", e);
            return Result.error("CHECKIN_ERROR", "签到失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户今日签到状态
     */
    @GetMapping("/checkin/today")
    @SaCheckLogin
    @Operation(summary = "查询今日签到状态", description = "检查用户今天是否已经签到")
    public Result<Boolean> getTodayCheckinStatus() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("REST查询今日签到状态: userId={}", userId);
            
            return userTaskFacadeService.getTodayCheckinStatus(userId);
        } catch (Exception e) {
            log.error("查询今日签到状态异常", e);
            return Result.error("GET_TODAY_STATUS_ERROR", "查询今日签到状态失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户连续签到天数
     */
    @GetMapping("/checkin/continuous")
    @SaCheckLogin
    @Operation(summary = "查询连续签到天数", description = "获取用户当前连续签到天数")
    public Result<Integer> getContinuousCheckinDays() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("REST查询连续签到天数: userId={}", userId);
            
            return userTaskFacadeService.getContinuousCheckinDays(userId);
        } catch (Exception e) {
            log.error("查询连续签到天数异常", e);
            return Result.error("GET_CONTINUOUS_DAYS_ERROR", "查询连续签到天数失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户签到统计信息
     */
    @GetMapping("/checkin/stats")
    @SaCheckLogin
    @Operation(summary = "查询签到统计", description = "获取用户签到次数、连续天数、累计金币等统计信息")
    public Result<UserCheckinResponse> getUserCheckinStats() {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.info("REST查询用户签到统计: userId={}", userId);
            
            return userTaskFacadeService.getUserCheckinStats(userId);
        } catch (Exception e) {
            log.error("查询用户签到统计异常", e);
            return Result.error("GET_CHECKIN_STATS_ERROR", "查询签到统计失败: " + e.getMessage());
        }
    }

    // =================== 签到历史查询 ===================

    /**
     * 分页查询用户签到记录
     */
    @PostMapping("/checkin/history")
    @SaCheckLogin
    @Operation(summary = "分页查询签到记录", description = "分页查询用户的签到历史记录")
    public Result<PageResponse<UserTaskResponse>> getUserCheckinHistory(@Valid @RequestBody UserTaskQueryRequest request) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            request.setUserId(userId); // 强制设置为当前用户
            
            log.info("REST分页查询签到记录: userId={}, page={}, size={}", 
                    userId, request.getCurrentPage(), request.getPageSize());
            
            return userTaskFacadeService.getUserCheckinHistory(request);
        } catch (Exception e) {
            log.error("分页查询签到记录异常", e);
            return Result.error("GET_CHECKIN_HISTORY_ERROR", "查询签到记录失败: " + e.getMessage());
        }
    }

    /**
     * 查询指定日期的签到记录
     */
    @GetMapping("/checkin/record")
    @SaCheckLogin
    @Operation(summary = "查询指定日期签到记录", description = "查询用户在指定日期的签到记录")
    public Result<UserTaskResponse> getUserCheckinRecord(
            @RequestParam("date") 
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @Parameter(description = "查询日期", example = "2024-01-16")
            LocalDate date) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            log.debug("REST查询指定日期签到记录: userId={}, date={}", userId, date);
            
            return userTaskFacadeService.getUserCheckinRecord(userId, date);
        } catch (Exception e) {
            log.error("查询指定日期签到记录异常: date={}", date, e);
            return Result.error("GET_CHECKIN_RECORD_ERROR", "查询签到记录失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户月度签到记录
     */
    @GetMapping("/checkin/monthly")
    @SaCheckLogin
    @Operation(summary = "查询月度签到记录", description = "查询用户指定月份的所有签到记录")
    public Result<List<UserTaskResponse>> getMonthlyCheckinRecords(
            @RequestParam(value = "month", required = false)
            @DateTimeFormat(pattern = "yyyy-MM")
            @Parameter(description = "查询月份", example = "2024-01")
            LocalDate month) {
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            
            if (month == null) {
                month = LocalDate.now().withDayOfMonth(1); // 默认当前月份
            }
            
            log.info("REST查询月度签到记录: userId={}, month={}", userId, month);
            
            return userTaskFacadeService.getMonthlyCheckinRecords(userId, month);
        } catch (Exception e) {
            log.error("查询月度签到记录异常: month={}", month, e);
            return Result.error("GET_MONTHLY_RECORDS_ERROR", "查询月度签到记录失败: " + e.getMessage());
        }
    }

    // =================== 公共查询接口（无需登录） ===================

    /**
     * 根据用户ID查询签到统计（公开接口）
     */
    @GetMapping("/{userId}/stats")
    @Operation(summary = "查询用户签到统计", description = "根据用户ID查询签到统计信息（公开接口）")
    public Result<UserCheckinResponse> getUserCheckinStatsByUserId(
            @PathVariable("userId") @NotNull @Min(1) Long userId) {
        try {
            log.info("REST查询用户签到统计（公开接口）: userId={}", userId);
            
            return userTaskFacadeService.getUserCheckinStats(userId);
        } catch (Exception e) {
            log.error("查询用户签到统计异常: userId={}", userId, e);
            return Result.error("GET_USER_STATS_ERROR", "查询用户签到统计失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户指定日期签到状态（公开接口）
     */
    @GetMapping("/{userId}/checkin/status")
    @Operation(summary = "查询用户签到状态", description = "查询指定用户在指定日期的签到状态")
    public Result<UserTaskResponse> getUserCheckinStatus(
            @PathVariable("userId") @NotNull @Min(1) Long userId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @Parameter(description = "查询日期", example = "2024-01-16")
            LocalDate date) {
        try {
            if (date == null) {
                date = LocalDate.now(); // 默认今天
            }
            
            log.debug("REST查询用户签到状态: userId={}, date={}", userId, date);
            
            return userTaskFacadeService.getUserCheckinRecord(userId, date);
        } catch (Exception e) {
            log.error("查询用户签到状态异常: userId={}, date={}", userId, date, e);
            return Result.error("GET_USER_STATUS_ERROR", "查询用户签到状态失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // 处理多个IP的情况，取第一个IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
}