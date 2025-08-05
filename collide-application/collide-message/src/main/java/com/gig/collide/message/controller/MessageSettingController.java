package com.gig.collide.message.controller;

import com.gig.collide.api.message.MessageSettingFacadeService;
import com.gig.collide.api.message.request.MessageSettingCreateRequest;
import com.gig.collide.api.message.request.MessageSettingUpdateRequest;
import com.gig.collide.api.message.response.MessageSettingResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息设置REST控制器 - 简洁版
 * 基于message-simple.sql的t_message_setting表设计
 * 管理用户的消息偏好设置和权限控制
 * 
 * 主要功能：
 * - 设置管理：创建/更新用户设置、查询设置、初始化默认设置
 * - 单项设置：陌生人消息、已读回执、消息通知单独控制
 * - 权限验证：发送权限检查、各种设置状态查询
 * - 设置模板：重置默认、复制设置、批量初始化
 * - 设置分析：统计信息、设置历史记录
 * - 系统功能：设置同步、健康检查
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/message-settings")
@RequiredArgsConstructor
@Tag(name = "消息设置管理", description = "消息设置相关的API接口")
@Validated
public class MessageSettingController {

    private final MessageSettingFacadeService messageSettingFacadeService;

    // =================== 设置管理 ===================

    /**
     * 创建或更新用户消息设置
     * 
     * @param request 设置创建请求
     * @return 保存的设置
     */
    @PostMapping
    @Operation(summary = "创建或更新消息设置", description = "如果设置不存在则创建默认设置，存在则更新")
    public Result<MessageSettingResponse> createOrUpdateSetting(@RequestBody @Validated MessageSettingCreateRequest request) {
        log.info("REST请求 - 创建或更新消息设置: userId={}", request.getUserId());
        return messageSettingFacadeService.createOrUpdateSetting(request);
    }

    /**
     * 根据用户ID获取消息设置
     * 
     * @param userId 用户ID
     * @return 消息设置
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户消息设置", description = "如果不存在则返回默认设置")
    public Result<MessageSettingResponse> getUserSetting(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 获取用户消息设置: userId={}", userId);
        return messageSettingFacadeService.getUserSetting(userId);
    }

    /**
     * 更新用户消息设置
     * 
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的设置
     */
    @PutMapping("/user/{userId}")
    @Operation(summary = "更新用户消息设置", description = "更新用户的消息偏好设置")
    public Result<MessageSettingResponse> updateUserSetting(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestBody @Validated MessageSettingUpdateRequest request) {
        log.info("REST请求 - 更新用户消息设置: userId={}", userId);
        request.setUserId(userId);
        return messageSettingFacadeService.updateUserSetting(request);
    }

    /**
     * 初始化用户默认设置
     * 
     * @param userId 用户ID
     * @return 创建的默认设置
     */
    @PostMapping("/user/{userId}/init")
    @Operation(summary = "初始化用户默认设置", description = "为新用户创建默认的消息设置")
    public Result<MessageSettingResponse> initDefaultSetting(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 初始化用户默认设置: userId={}", userId);
        return messageSettingFacadeService.initDefaultSetting(userId);
    }

    // =================== 单项设置更新 ===================

    /**
     * 更新陌生人消息设置
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/stranger-message")
    @Operation(summary = "更新陌生人消息设置", description = "控制是否允许陌生人发送消息")
    public Result<Void> updateStrangerMessageSetting(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "是否允许陌生人发消息") Boolean allowStrangerMsg) {
        log.info("REST请求 - 更新陌生人消息设置: userId={}, allowStrangerMsg={}", userId, allowStrangerMsg);
        return messageSettingFacadeService.updateStrangerMessageSetting(userId, allowStrangerMsg);
    }

    /**
     * 更新已读回执设置
     * 
     * @param userId 用户ID
     * @param autoReadReceipt 是否自动发送已读回执
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/read-receipt")
    @Operation(summary = "更新已读回执设置", description = "控制是否自动发送已读回执")
    public Result<Void> updateReadReceiptSetting(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "是否自动发送已读回执") Boolean autoReadReceipt) {
        log.info("REST请求 - 更新已读回执设置: userId={}, autoReadReceipt={}", userId, autoReadReceipt);
        return messageSettingFacadeService.updateReadReceiptSetting(userId, autoReadReceipt);
    }

    /**
     * 更新消息通知设置
     * 
     * @param userId 用户ID
     * @param messageNotification 是否开启消息通知
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/notification")
    @Operation(summary = "更新消息通知设置", description = "控制是否开启消息通知")
    public Result<Void> updateNotificationSetting(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "是否开启消息通知") Boolean messageNotification) {
        log.info("REST请求 - 更新消息通知设置: userId={}, messageNotification={}", userId, messageNotification);
        return messageSettingFacadeService.updateNotificationSetting(userId, messageNotification);
    }

    /**
     * 批量更新用户设置
     * 
     * @param userId 用户ID
     * @param allowStrangerMsg 是否允许陌生人发消息
     * @param autoReadReceipt 是否自动发送已读回执
     * @param messageNotification 是否开启消息通知
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/batch")
    @Operation(summary = "批量更新用户设置", description = "同时更新多个设置项")
    public Result<Void> updateBatchSettings(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(required = false) @Parameter(description = "是否允许陌生人发消息") Boolean allowStrangerMsg,
            @RequestParam(required = false) @Parameter(description = "是否自动发送已读回执") Boolean autoReadReceipt,
            @RequestParam(required = false) @Parameter(description = "是否开启消息通知") Boolean messageNotification) {
        log.info("REST请求 - 批量更新用户设置: userId={}, allowStrangerMsg={}, autoReadReceipt={}, messageNotification={}", 
                userId, allowStrangerMsg, autoReadReceipt, messageNotification);
        return messageSettingFacadeService.updateBatchSettings(userId, allowStrangerMsg, autoReadReceipt, messageNotification);
    }

    // =================== 权限验证 ===================

    /**
     * 检查是否允许发送消息
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 是否允许发送
     */
    @GetMapping("/permission/send")
    @Operation(summary = "检查发送消息权限", description = "根据用户设置和关系验证是否可以向目标用户发送消息")
    public Result<Boolean> canSendMessage(
            @RequestParam @Parameter(description = "发送者ID") Long senderId,
            @RequestParam @Parameter(description = "接收者ID") Long receiverId) {
        log.info("REST请求 - 检查发送消息权限: senderId={}, receiverId={}", senderId, receiverId);
        return messageSettingFacadeService.canSendMessage(senderId, receiverId);
    }

    /**
     * 检查是否允许陌生人消息
     * 
     * @param userId 用户ID
     * @return 是否允许陌生人发消息
     */
    @GetMapping("/user/{userId}/stranger-message/allowed")
    @Operation(summary = "检查陌生人消息权限", description = "查询用户是否允许陌生人发送消息")
    public Result<Boolean> isStrangerMessageAllowed(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 检查陌生人消息权限: userId={}", userId);
        return messageSettingFacadeService.isStrangerMessageAllowed(userId);
    }

    /**
     * 检查是否开启自动已读回执
     * 
     * @param userId 用户ID
     * @return 是否开启自动已读回执
     */
    @GetMapping("/user/{userId}/read-receipt/enabled")
    @Operation(summary = "检查已读回执设置", description = "查询用户是否开启自动已读回执")
    public Result<Boolean> isAutoReadReceiptEnabled(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 检查已读回执设置: userId={}", userId);
        return messageSettingFacadeService.isAutoReadReceiptEnabled(userId);
    }

    /**
     * 检查是否开启消息通知
     * 
     * @param userId 用户ID
     * @return 是否开启消息通知
     */
    @GetMapping("/user/{userId}/notification/enabled")
    @Operation(summary = "检查消息通知设置", description = "查询用户是否开启消息通知")
    public Result<Boolean> isMessageNotificationEnabled(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 检查消息通知设置: userId={}", userId);
        return messageSettingFacadeService.isMessageNotificationEnabled(userId);
    }

    /**
     * 批量检查用户设置状态
     * 
     * @param userId 用户ID
     * @return 设置状态汇总
     */
    @GetMapping("/user/{userId}/status")
    @Operation(summary = "批量检查用户设置", description = "一次性获取用户所有设置状态")
    public Result<MessageSettingResponse> checkAllSettings(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 批量检查用户设置状态: userId={}", userId);
        return messageSettingFacadeService.checkAllSettings(userId);
    }

    // =================== 设置模板 ===================

    /**
     * 重置用户设置为默认值
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/reset")
    @Operation(summary = "重置用户设置", description = "将用户设置重置为系统默认值")
    public Result<Void> resetToDefault(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 重置用户设置: userId={}", userId);
        return messageSettingFacadeService.resetToDefault(userId);
    }

    /**
     * 获取默认消息设置
     * 
     * @return 默认设置
     */
    @GetMapping("/default")
    @Operation(summary = "获取默认设置", description = "获取系统的默认消息设置")
    public Result<MessageSettingResponse> getDefaultSetting() {
        log.info("REST请求 - 获取默认消息设置");
        return messageSettingFacadeService.getDefaultSetting();
    }

    /**
     * 复制设置到新用户
     * 
     * @param fromUserId 模板用户ID
     * @param toUserId 目标用户ID
     * @return 操作结果
     */
    @PostMapping("/copy")
    @Operation(summary = "复制用户设置", description = "从模板用户复制设置到新用户")
    public Result<Void> copySettingFromUser(
            @RequestParam @Parameter(description = "模板用户ID") Long fromUserId,
            @RequestParam @Parameter(description = "目标用户ID") Long toUserId) {
        log.info("REST请求 - 复制用户设置: fromUserId={}, toUserId={}", fromUserId, toUserId);
        return messageSettingFacadeService.copySettingFromUser(fromUserId, toUserId);
    }

    /**
     * 批量初始化用户设置
     * 
     * @param userIds 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/batch/init")
    @Operation(summary = "批量初始化用户设置", description = "为多个用户批量创建默认设置")
    public Result<Integer> batchInitSettings(@RequestBody @Parameter(description = "用户ID列表") List<Long> userIds) {
        log.info("REST请求 - 批量初始化用户设置: userIds.size={}", userIds != null ? userIds.size() : 0);
        return messageSettingFacadeService.batchInitSettings(userIds);
    }

    // =================== 设置分析 ===================

    /**
     * 获取设置统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取设置统计", description = "统计各设置项的启用情况")
    public Result<Map<String, Object>> getSettingStatistics() {
        log.info("REST请求 - 获取设置统计信息");
        return messageSettingFacadeService.getSettingStatistics();
    }

    /**
     * 获取用户设置历史
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 设置历史记录
     */
    @GetMapping("/user/{userId}/history")
    @Operation(summary = "获取用户设置历史", description = "查看用户设置的变更记录")
    public Result<List<MessageSettingResponse>> getSettingHistory(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "10") @Parameter(description = "限制数量") Integer limit) {
        log.info("REST请求 - 获取用户设置历史: userId={}, limit={}", userId, limit);
        return messageSettingFacadeService.getSettingHistory(userId, limit);
    }

    // =================== 系统功能 ===================

    /**
     * 同步用户设置
     * 
     * @param userId 用户ID
     * @return 同步结果
     */
    @PostMapping("/user/{userId}/sync")
    @Operation(summary = "同步用户设置", description = "同步用户在其他系统中的设置")
    public Result<String> syncUserSetting(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 同步用户设置: userId={}", userId);
        return messageSettingFacadeService.syncUserSetting(userId);
    }

    /**
     * 消息设置系统健康检查
     * 
     * @return 系统状态
     */
    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查消息设置系统运行状态")
    public Result<String> healthCheck() {
        log.info("REST请求 - 消息设置系统健康检查");
        return messageSettingFacadeService.healthCheck();
    }

    // =================== 便捷操作 ===================

    /**
     * 快速开启/关闭所有通知
     * 
     * @param userId 用户ID
     * @param enabled 是否开启
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/notifications/toggle")
    @Operation(summary = "快速切换通知设置", description = "一键开启或关闭所有消息通知")
    public Result<Void> toggleAllNotifications(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "是否开启通知") Boolean enabled) {
        log.info("REST请求 - 快速切换通知设置: userId={}, enabled={}", userId, enabled);
        return messageSettingFacadeService.updateNotificationSetting(userId, enabled);
    }

    /**
     * 获取推荐设置
     * 
     * @param userId 用户ID
     * @return 推荐的设置配置
     */
    @GetMapping("/user/{userId}/recommended")
    @Operation(summary = "获取推荐设置", description = "根据用户行为推荐最佳设置配置")
    public Result<MessageSettingResponse> getRecommendedSettings(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 获取推荐设置: userId={}", userId);
        // 目前返回默认设置作为推荐设置
        return messageSettingFacadeService.getUserSetting(userId);
    }

    /**
     * 应用推荐设置
     * 
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/user/{userId}/apply-recommended")
    @Operation(summary = "应用推荐设置", description = "应用系统推荐的设置配置")
    public Result<Void> applyRecommendedSettings(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 应用推荐设置: userId={}", userId);
        // 目前重置为默认设置作为推荐设置的应用
        return messageSettingFacadeService.resetToDefault(userId);
    }
}