package com.gig.collide.message.controller;

import com.gig.collide.api.message.MessageSessionFacadeService;
import com.gig.collide.api.message.request.MessageSessionCreateRequest;
import com.gig.collide.api.message.request.MessageSessionQueryRequest;
import com.gig.collide.api.message.request.MessageSessionUpdateRequest;
import com.gig.collide.api.message.response.MessageSessionResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息会话REST控制器 - 简洁版
 * 基于message-simple.sql的t_message_session表设计
 * 管理用户间的会话状态、未读计数和会话列表
 * 
 * 主要功能：
 * - 会话管理：创建/更新会话、查询会话、归档管理
 * - 会话查询：用户会话列表、活跃会话、未读会话
 * - 统计功能：未读会话数、总会话数、总未读数
 * - 状态管理：最后消息更新、未读计数管理
 * - 批量操作：批量归档、批量取消归档
 * - 系统功能：会话清理、索引重建、健康检查
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "消息会话管理", description = "消息会话相关的API接口")
@Validated
public class MessageSessionController {

    private final MessageSessionFacadeService messageSessionFacadeService;

    // =================== 会话创建和管理 ===================

    /**
     * 创建或更新会话
     * 
     * @param request 会话创建请求
     * @return 会话信息
     */
    @PostMapping
    @Operation(summary = "创建或更新会话", description = "如果会话不存在则创建，存在则更新")
    public Result<MessageSessionResponse> createOrUpdateSession(@RequestBody @Validated MessageSessionCreateRequest request) {
        log.info("REST请求 - 创建或更新会话: userId={}, otherUserId={}", 
                request.getUserId(), request.getOtherUserId());
        return messageSessionFacadeService.createOrUpdateSession(request);
    }

    /**
     * 根据用户ID获取会话详情
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 会话详情
     */
    @GetMapping
    @Operation(summary = "获取会话详情", description = "根据用户ID查询会话信息")
    public Result<MessageSessionResponse> getSessionByUserIds(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "对方用户ID") Long otherUserId) {
        log.info("REST请求 - 获取会话详情: userId={}, otherUserId={}", userId, otherUserId);
        return messageSessionFacadeService.getSessionByUserIds(userId, otherUserId);
    }

    /**
     * 更新会话信息
     * 
     * @param sessionId 会话ID
     * @param request 更新请求
     * @return 更新后的会话信息
     */
    @PutMapping("/{sessionId}")
    @Operation(summary = "更新会话信息", description = "更新会话的状态和信息")
    public Result<MessageSessionResponse> updateSession(
            @PathVariable @Parameter(description = "会话ID") Long sessionId,
            @RequestBody @Validated MessageSessionUpdateRequest request) {
        log.info("REST请求 - 更新会话信息: sessionId={}, updateType={}", 
                sessionId, request.getUpdateType());
        request.setSessionId(sessionId);
        return messageSessionFacadeService.updateSession(request);
    }

    /**
     * 更新会话归档状态
     * 
     * @param sessionId 会话ID
     * @param isArchived 是否归档
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @PutMapping("/{sessionId}/archive")
    @Operation(summary = "更新会话归档状态", description = "设置或取消会话归档")
    public Result<Void> updateArchiveStatus(
            @PathVariable @Parameter(description = "会话ID") Long sessionId,
            @RequestParam @Parameter(description = "是否归档") Boolean isArchived,
            @RequestParam @Parameter(description = "操作用户ID") Long userId) {
        log.info("REST请求 - 更新会话归档状态: sessionId={}, isArchived={}, userId={}", 
                sessionId, isArchived, userId);
        return messageSessionFacadeService.updateArchiveStatus(sessionId, isArchived, userId);
    }

    // =================== 会话查询 ===================

    /**
     * 分页查询用户会话列表
     * 
     * @param request 查询请求
     * @return 会话列表分页
     */
    @PostMapping("/query")
    @Operation(summary = "条件查询用户会话", description = "支持按归档状态、未读状态筛选")
    public Result<PageResponse<MessageSessionResponse>> queryUserSessions(@RequestBody MessageSessionQueryRequest request) {
        log.info("REST请求 - 条件查询用户会话: {}", request);
        return messageSessionFacadeService.queryUserSessions(request);
    }

    /**
     * 获取用户的所有会话
     * 
     * @param userId 用户ID
     * @param isArchived 是否归档
     * @param hasUnread 是否有未读
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 会话列表分页
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户会话列表", description = "分页获取用户的会话列表")
    public Result<PageResponse<MessageSessionResponse>> getUserSessions(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(required = false) @Parameter(description = "是否归档") Boolean isArchived,
            @RequestParam(required = false) @Parameter(description = "是否有未读") Boolean hasUnread,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 获取用户会话列表: userId={}, isArchived={}, hasUnread={}, page={}/{}", 
                userId, isArchived, hasUnread, currentPage, pageSize);
        return messageSessionFacadeService.getUserSessions(userId, isArchived, hasUnread, currentPage, pageSize);
    }

    /**
     * 获取用户的活跃会话
     * 
     * @param userId 用户ID
     * @param sinceTime 起始时间
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 活跃会话分页
     */
    @GetMapping("/user/{userId}/active")
    @Operation(summary = "获取用户活跃会话", description = "查询指定时间后有消息交互的会话")
    public Result<PageResponse<MessageSessionResponse>> getActiveSessions(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(required = false) @Parameter(description = "起始时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime sinceTime,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 获取用户活跃会话: userId={}, sinceTime={}, page={}/{}", 
                userId, sinceTime, currentPage, pageSize);
        return messageSessionFacadeService.getActiveSessions(userId, sinceTime, currentPage, pageSize);
    }

    /**
     * 获取用户的未读会话列表
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 未读会话分页
     */
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "获取用户未读会话", description = "获取有未读消息的会话列表")
    public Result<PageResponse<MessageSessionResponse>> getUnreadSessions(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 获取用户未读会话: userId={}, page={}/{}", userId, currentPage, pageSize);
        return messageSessionFacadeService.getUnreadSessions(userId, currentPage, pageSize);
    }

    // =================== 统计功能 ===================

    /**
     * 统计用户的未读会话数
     * 
     * @param userId 用户ID
     * @return 未读会话数
     */
    @GetMapping("/user/{userId}/unread/count")
    @Operation(summary = "统计未读会话数", description = "获取用户的未读会话总数")
    public Result<Long> getUnreadSessionCount(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 统计未读会话数: userId={}", userId);
        return messageSessionFacadeService.getUnreadSessionCount(userId);
    }

    /**
     * 统计用户的总会话数
     * 
     * @param userId 用户ID
     * @param isArchived 是否归档
     * @return 总会话数
     */
    @GetMapping("/user/{userId}/count")
    @Operation(summary = "统计用户会话总数", description = "获取用户的会话总数")
    public Result<Long> getUserSessionCount(
            @PathVariable @Parameter(description = "用户ID") Long userId,
            @RequestParam(required = false) @Parameter(description = "是否归档") Boolean isArchived) {
        log.info("REST请求 - 统计用户会话总数: userId={}, isArchived={}", userId, isArchived);
        return messageSessionFacadeService.getUserSessionCount(userId, isArchived);
    }

    /**
     * 获取用户所有会话的未读总数
     * 
     * @param userId 用户ID
     * @return 未读总数
     */
    @GetMapping("/user/{userId}/unread/total")
    @Operation(summary = "获取用户总未读数", description = "统计用户所有会话的未读消息总数")
    public Result<Long> getTotalUnreadCount(@PathVariable @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 获取用户总未读数: userId={}", userId);
        return messageSessionFacadeService.getTotalUnreadCount(userId);
    }

    // =================== 会话状态管理 ===================

    /**
     * 更新会话的最后消息信息
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @param lastMessageId 最后消息ID
     * @param lastMessageTime 最后消息时间
     * @return 操作结果
     */
    @PutMapping("/last-message")
    @Operation(summary = "更新会话最后消息", description = "在新消息到达时自动调用")
    public Result<Void> updateLastMessage(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "对方用户ID") Long otherUserId,
            @RequestParam @Parameter(description = "最后消息ID") Long lastMessageId,
            @RequestParam @Parameter(description = "最后消息时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastMessageTime) {
        log.info("REST请求 - 更新会话最后消息: userId={}, otherUserId={}, lastMessageId={}", 
                userId, otherUserId, lastMessageId);
        return messageSessionFacadeService.updateLastMessage(userId, otherUserId, lastMessageId, lastMessageTime);
    }

    /**
     * 增加会话的未读计数
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 操作结果
     */
    @PutMapping("/unread/increment")
    @Operation(summary = "增加会话未读计数", description = "新消息到达时增加未读数")
    public Result<Void> incrementUnreadCount(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "对方用户ID") Long otherUserId) {
        log.info("REST请求 - 增加会话未读计数: userId={}, otherUserId={}", userId, otherUserId);
        return messageSessionFacadeService.incrementUnreadCount(userId, otherUserId);
    }

    /**
     * 清零会话的未读计数
     * 
     * @param userId 用户ID
     * @param otherUserId 对方用户ID
     * @return 操作结果
     */
    @PutMapping("/unread/clear")
    @Operation(summary = "清零会话未读计数", description = "用户查看消息时调用")
    public Result<Void> clearUnreadCount(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "对方用户ID") Long otherUserId) {
        log.info("REST请求 - 清零会话未读计数: userId={}, otherUserId={}", userId, otherUserId);
        return messageSessionFacadeService.clearUnreadCount(userId, otherUserId);
    }

    /**
     * 处理新消息事件
     * 
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param messageId 消息ID
     * @param messageTime 消息时间
     * @return 处理结果
     */
    @PostMapping("/new-message")
    @Operation(summary = "处理新消息事件", description = "自动创建或更新相关用户的会话状态")
    public Result<Void> handleNewMessage(
            @RequestParam @Parameter(description = "发送者ID") Long senderId,
            @RequestParam @Parameter(description = "接收者ID") Long receiverId,
            @RequestParam @Parameter(description = "消息ID") Long messageId,
            @RequestParam @Parameter(description = "消息时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime messageTime) {
        log.info("REST请求 - 处理新消息事件: senderId={}, receiverId={}, messageId={}", 
                senderId, receiverId, messageId);
        return messageSessionFacadeService.handleNewMessage(senderId, receiverId, messageId, messageTime);
    }

    // =================== 会话清理 ===================

    /**
     * 删除空会话
     * 
     * @param userId 用户ID（可选）
     * @return 删除的会话数量
     */
    @DeleteMapping("/empty")
    @Operation(summary = "删除空会话", description = "删除没有消息记录的会话")
    public Result<Integer> deleteEmptySessions(
            @RequestParam(required = false) @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 删除空会话: userId={}", userId);
        return messageSessionFacadeService.deleteEmptySessions(userId);
    }

    /**
     * 删除指定时间前的归档会话
     * 
     * @param beforeTime 截止时间
     * @param userId 用户ID（可选）
     * @return 删除的会话数量
     */
    @DeleteMapping("/archived")
    @Operation(summary = "删除归档会话", description = "删除指定时间前的归档会话")
    public Result<Integer> deleteArchivedSessions(
            @RequestParam @Parameter(description = "截止时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beforeTime,
            @RequestParam(required = false) @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 删除归档会话: beforeTime={}, userId={}", beforeTime, userId);
        return messageSessionFacadeService.deleteArchivedSessions(beforeTime, userId);
    }

    /**
     * 批量归档会话
     * 
     * @param sessionIds 会话ID列表
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @PutMapping("/batch/archive")
    @Operation(summary = "批量归档会话", description = "批量设置会话为归档状态")
    public Result<Void> batchArchiveSessions(
            @RequestBody @Parameter(description = "会话ID列表") List<Long> sessionIds,
            @RequestParam @Parameter(description = "操作用户ID") Long userId) {
        log.info("REST请求 - 批量归档会话: sessionIds.size={}, userId={}", 
                sessionIds != null ? sessionIds.size() : 0, userId);
        return messageSessionFacadeService.batchArchiveSessions(sessionIds, userId);
    }

    /**
     * 批量取消归档会话
     * 
     * @param sessionIds 会话ID列表
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @PutMapping("/batch/unarchive")
    @Operation(summary = "批量取消归档会话", description = "批量取消会话的归档状态")
    public Result<Void> batchUnarchiveSessions(
            @RequestBody @Parameter(description = "会话ID列表") List<Long> sessionIds,
            @RequestParam @Parameter(description = "操作用户ID") Long userId) {
        log.info("REST请求 - 批量取消归档会话: sessionIds.size={}, userId={}", 
                sessionIds != null ? sessionIds.size() : 0, userId);
        return messageSessionFacadeService.batchUnarchiveSessions(sessionIds, userId);
    }

    // =================== 系统功能 ===================

    /**
     * 重建会话索引
     * 
     * @param userId 用户ID（可选）
     * @return 处理结果
     */
    @PostMapping("/rebuild-index")
    @Operation(summary = "重建会话索引", description = "系统维护功能，重新计算会话的统计信息")
    public Result<String> rebuildSessionIndex(
            @RequestParam(required = false) @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 重建会话索引: userId={}", userId);
        return messageSessionFacadeService.rebuildSessionIndex(userId);
    }

    /**
     * 会话系统健康检查
     * 
     * @return 系统状态
     */
    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查会话系统运行状态")
    public Result<String> healthCheck() {
        log.info("REST请求 - 会话系统健康检查");
        return messageSessionFacadeService.healthCheck();
    }
}