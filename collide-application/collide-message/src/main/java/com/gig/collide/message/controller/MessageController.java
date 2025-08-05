package com.gig.collide.message.controller;

import com.gig.collide.api.message.MessageFacadeService;
import com.gig.collide.api.message.request.MessageCreateRequest;
import com.gig.collide.api.message.request.MessageQueryRequest;
import com.gig.collide.api.message.request.MessageUpdateRequest;
import com.gig.collide.api.message.response.MessageResponse;
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
 * 消息REST控制器 - 简洁版
 * 基于message-simple.sql的无连表设计，提供完整的消息HTTP接口
 * 支持私信、留言板、消息回复等功能
 * 
 * 主要功能：
 * - 消息发送：发送私信、回复消息、留言板消息
 * - 消息查询：聊天记录、条件查询、搜索、回复列表
 * - 消息管理：更新状态、删除、置顶、标记已读
 * - 批量操作：批量已读、批量删除、会话全部已读
 * - 统计功能：未读数统计、发送/接收统计
 * - 会话管理：最近聊天用户、最新消息
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "消息管理", description = "消息相关的API接口")
@Validated
public class MessageController {

    private final MessageFacadeService messageFacadeService;

    // =================== 消息发送 ===================

    /**
     * 发送消息
     * 
     * @param request 消息创建请求
     * @return 发送的消息
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "支持文本、图片、文件等多种消息类型")
    public Result<MessageResponse> sendMessage(@RequestBody @Validated MessageCreateRequest request) {
        log.info("REST请求 - 发送消息: 发送者={}, 接收者={}, 类型={}", 
                request.getSenderId(), request.getReceiverId(), request.getMessageType());
        return messageFacadeService.sendMessage(request);
    }

    /**
     * 回复消息
     * 
     * @param request 回复消息创建请求
     * @return 回复的消息
     */
    @PostMapping("/reply")
    @Operation(summary = "回复消息", description = "回复指定的消息，建立回复关系")
    public Result<MessageResponse> replyMessage(@RequestBody @Validated MessageCreateRequest request) {
        log.info("REST请求 - 回复消息: 发送者={}, 接收者={}, 回复消息ID={}", 
                request.getSenderId(), request.getReceiverId(), request.getReplyToId());
        return messageFacadeService.replyMessage(request);
    }

    /**
     * 发送留言板消息
     * 
     * @param request 留言消息创建请求
     * @return 发送的留言
     */
    @PostMapping("/wall")
    @Operation(summary = "发送留言板消息", description = "发送到用户留言板的公开消息")
    public Result<MessageResponse> sendWallMessage(@RequestBody @Validated MessageCreateRequest request) {
        log.info("REST请求 - 发送留言板消息: 发送者={}, 接收者={}", 
                request.getSenderId(), request.getReceiverId());
        return messageFacadeService.sendWallMessage(request);
    }

    // =================== 消息查询 ===================

    /**
     * 根据ID获取消息详情
     * 
     * @param messageId 消息ID
     * @param userId 查看者用户ID
     * @return 消息详情
     */
    @GetMapping("/{messageId}")
    @Operation(summary = "获取消息详情", description = "根据消息ID获取详细信息")
    public Result<MessageResponse> getMessageById(
            @PathVariable @Parameter(description = "消息ID") Long messageId,
            @RequestParam @Parameter(description = "查看者用户ID") Long userId) {
        log.info("REST请求 - 获取消息详情: messageId={}, userId={}", messageId, userId);
        return messageFacadeService.getMessageById(messageId, userId);
    }

    /**
     * 分页查询消息
     * 
     * @param request 查询请求
     * @return 消息列表分页
     */
    @PostMapping("/query")
    @Operation(summary = "条件查询消息", description = "支持多维度条件查询")
    public Result<PageResponse<MessageResponse>> queryMessages(@RequestBody MessageQueryRequest request) {
        log.info("REST请求 - 条件查询消息: {}", request);
        return messageFacadeService.queryMessages(request);
    }

    /**
     * 查询两用户间的聊天记录
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @param status 消息状态
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 聊天记录分页
     */
    @GetMapping("/chat")
    @Operation(summary = "查询聊天记录", description = "获取两用户间的消息记录")
    public Result<PageResponse<MessageResponse>> getChatHistory(
            @RequestParam @Parameter(description = "用户1ID") Long userId1,
            @RequestParam @Parameter(description = "用户2ID") Long userId2,
            @RequestParam(required = false) @Parameter(description = "消息状态") String status,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 查询聊天记录: userId1={}, userId2={}, page={}/{}", 
                userId1, userId2, currentPage, pageSize);
        return messageFacadeService.getChatHistory(userId1, userId2, status, currentPage, pageSize);
    }

    /**
     * 查询用户留言板消息
     * 
     * @param receiverId 接收者ID
     * @param status 消息状态
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 留言板消息分页
     */
    @GetMapping("/wall/{receiverId}")
    @Operation(summary = "查询留言板消息", description = "获取用户留言板的消息")
    public Result<PageResponse<MessageResponse>> getWallMessages(
            @PathVariable @Parameter(description = "接收者ID") Long receiverId,
            @RequestParam(required = false) @Parameter(description = "消息状态") String status,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 查询留言板消息: receiverId={}, page={}/{}", 
                receiverId, currentPage, pageSize);
        return messageFacadeService.getWallMessages(receiverId, status, currentPage, pageSize);
    }

    /**
     * 查询消息回复列表
     * 
     * @param replyToId 原消息ID
     * @param status 消息状态
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 回复列表分页
     */
    @GetMapping("/{replyToId}/replies")
    @Operation(summary = "查询消息回复", description = "获取消息的回复列表")
    public Result<PageResponse<MessageResponse>> getMessageReplies(
            @PathVariable @Parameter(description = "原消息ID") Long replyToId,
            @RequestParam(required = false) @Parameter(description = "消息状态") String status,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 查询消息回复: replyToId={}, page={}/{}", 
                replyToId, currentPage, pageSize);
        return messageFacadeService.getMessageReplies(replyToId, status, currentPage, pageSize);
    }

    /**
     * 搜索用户消息
     * 
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param status 消息状态
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 搜索结果分页
     */
    @GetMapping("/search")
    @Operation(summary = "搜索用户消息", description = "支持消息内容关键词搜索")
    public Result<PageResponse<MessageResponse>> searchMessages(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam @Parameter(description = "搜索关键词") String keyword,
            @RequestParam(required = false) @Parameter(description = "消息状态") String status,
            @RequestParam(defaultValue = "1") @Parameter(description = "当前页码") Integer currentPage,
            @RequestParam(defaultValue = "20") @Parameter(description = "页面大小") Integer pageSize) {
        log.info("REST请求 - 搜索用户消息: userId={}, keyword={}, page={}/{}", 
                userId, keyword, currentPage, pageSize);
        return messageFacadeService.searchMessages(userId, keyword, status, currentPage, pageSize);
    }

    // =================== 消息管理 ===================

    /**
     * 更新消息
     * 
     * @param messageId 消息ID
     * @param request 更新请求
     * @return 更新后的消息
     */
    @PutMapping("/{messageId}")
    @Operation(summary = "更新消息", description = "支持更新消息内容（仅限发送者）")
    public Result<MessageResponse> updateMessage(
            @PathVariable @Parameter(description = "消息ID") Long messageId,
            @RequestBody @Validated MessageUpdateRequest request) {
        log.info("REST请求 - 更新消息: messageId={}, operatorId={}", messageId, request.getOperatorId());
        request.setMessageId(messageId);
        return messageFacadeService.updateMessage(request);
    }

    /**
     * 删除消息
     * 
     * @param messageId 消息ID
     * @param userId 操作用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息", description = "逻辑删除，支持发送者和接收者删除")
    public Result<Void> deleteMessage(
            @PathVariable @Parameter(description = "消息ID") Long messageId,
            @RequestParam @Parameter(description = "操作用户ID") Long userId) {
        log.info("REST请求 - 删除消息: messageId={}, userId={}", messageId, userId);
        return messageFacadeService.deleteMessage(messageId, userId);
    }

    /**
     * 标记消息为已读
     * 
     * @param messageId 消息ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/{messageId}/read")
    @Operation(summary = "标记消息已读", description = "将指定消息标记为已读状态")
    public Result<Void> markAsRead(
            @PathVariable @Parameter(description = "消息ID") Long messageId,
            @RequestParam @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 标记消息已读: messageId={}, userId={}", messageId, userId);
        return messageFacadeService.markAsRead(messageId, userId);
    }

    /**
     * 更新消息置顶状态
     * 
     * @param messageId 消息ID
     * @param isPinned 是否置顶
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @PutMapping("/{messageId}/pin")
    @Operation(summary = "更新消息置顶状态", description = "设置或取消消息置顶")
    public Result<Void> updatePinnedStatus(
            @PathVariable @Parameter(description = "消息ID") Long messageId,
            @RequestParam @Parameter(description = "是否置顶") Boolean isPinned,
            @RequestParam @Parameter(description = "操作用户ID") Long userId) {
        log.info("REST请求 - 更新消息置顶状态: messageId={}, isPinned={}, userId={}", 
                messageId, isPinned, userId);
        return messageFacadeService.updatePinnedStatus(messageId, isPinned, userId);
    }

    // =================== 批量操作 ===================

    /**
     * 批量标记消息为已读
     * 
     * @param messageIds 消息ID列表
     * @param userId 用户ID
     * @return 操作结果
     */
    @PutMapping("/batch/read")
    @Operation(summary = "批量标记消息已读", description = "批量将消息标记为已读状态")
    public Result<Void> batchMarkAsRead(
            @RequestBody @Parameter(description = "消息ID列表") List<Long> messageIds,
            @RequestParam @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 批量标记消息已读: messageIds.size={}, userId={}", 
                messageIds != null ? messageIds.size() : 0, userId);
        return messageFacadeService.batchMarkAsRead(messageIds, userId);
    }

    /**
     * 批量删除消息
     * 
     * @param messageIds 消息ID列表
     * @param userId 操作用户ID
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除消息", description = "批量逻辑删除消息")
    public Result<Void> batchDeleteMessages(
            @RequestBody @Parameter(description = "消息ID列表") List<Long> messageIds,
            @RequestParam @Parameter(description = "操作用户ID") Long userId) {
        log.info("REST请求 - 批量删除消息: messageIds.size={}, userId={}", 
                messageIds != null ? messageIds.size() : 0, userId);
        return messageFacadeService.batchDeleteMessages(messageIds, userId);
    }

    /**
     * 标记会话中所有消息为已读
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 操作结果
     */
    @PutMapping("/session/read")
    @Operation(summary = "标记会话已读", description = "将会话中所有消息标记为已读")
    public Result<Void> markSessionAsRead(
            @RequestParam @Parameter(description = "接收者ID") Long receiverId,
            @RequestParam @Parameter(description = "发送者ID") Long senderId) {
        log.info("REST请求 - 标记会话已读: receiverId={}, senderId={}", receiverId, senderId);
        return messageFacadeService.markSessionAsRead(receiverId, senderId);
    }

    // =================== 统计功能 ===================

    /**
     * 统计用户未读消息数
     * 
     * @param userId 用户ID
     * @return 未读消息数
     */
    @GetMapping("/unread/count")
    @Operation(summary = "统计未读消息数", description = "获取用户的未读消息总数")
    public Result<Long> getUnreadMessageCount(
            @RequestParam @Parameter(description = "用户ID") Long userId) {
        log.info("REST请求 - 统计未读消息数: userId={}", userId);
        return messageFacadeService.getUnreadMessageCount(userId);
    }

    /**
     * 统计与某用户的未读消息数
     * 
     * @param receiverId 接收者ID
     * @param senderId 发送者ID
     * @return 未读消息数
     */
    @GetMapping("/unread/count/with")
    @Operation(summary = "统计与用户的未读消息数", description = "获取与特定用户的未读消息数")
    public Result<Long> getUnreadCountWithUser(
            @RequestParam @Parameter(description = "接收者ID") Long receiverId,
            @RequestParam @Parameter(description = "发送者ID") Long senderId) {
        log.info("REST请求 - 统计与用户的未读消息数: receiverId={}, senderId={}", receiverId, senderId);
        return messageFacadeService.getUnreadCountWithUser(receiverId, senderId);
    }

    /**
     * 统计用户发送的消息数
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 发送消息数
     */
    @GetMapping("/sent/count")
    @Operation(summary = "统计发送消息数", description = "统计用户在指定时间范围内发送的消息数")
    public Result<Long> getSentMessageCount(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam(required = false) @Parameter(description = "开始时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @Parameter(description = "结束时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("REST请求 - 统计发送消息数: userId={}, startTime={}, endTime={}", 
                userId, startTime, endTime);
        return messageFacadeService.getSentMessageCount(userId, startTime, endTime);
    }

    /**
     * 统计用户接收的消息数
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 接收消息数
     */
    @GetMapping("/received/count")
    @Operation(summary = "统计接收消息数", description = "统计用户在指定时间范围内接收的消息数")
    public Result<Long> getReceivedMessageCount(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam(required = false) @Parameter(description = "开始时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @Parameter(description = "结束时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        log.info("REST请求 - 统计接收消息数: userId={}, startTime={}, endTime={}", 
                userId, startTime, endTime);
        return messageFacadeService.getReceivedMessageCount(userId, startTime, endTime);
    }

    // =================== 会话管理 ===================

    /**
     * 获取用户最近的聊天用户列表
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近聊天用户ID列表
     */
    @GetMapping("/recent/users")
    @Operation(summary = "获取最近聊天用户", description = "获取用户最近聊天的用户列表")
    public Result<List<Long>> getRecentChatUsers(
            @RequestParam @Parameter(description = "用户ID") Long userId,
            @RequestParam(defaultValue = "20") @Parameter(description = "限制数量") Integer limit) {
        log.info("REST请求 - 获取最近聊天用户: userId={}, limit={}", userId, limit);
        return messageFacadeService.getRecentChatUsers(userId, limit);
    }

    /**
     * 获取两用户间的最新消息
     * 
     * @param userId1 用户1ID
     * @param userId2 用户2ID
     * @return 最新消息
     */
    @GetMapping("/latest")
    @Operation(summary = "获取最新消息", description = "获取两用户间的最新一条消息")
    public Result<MessageResponse> getLatestMessage(
            @RequestParam @Parameter(description = "用户1ID") Long userId1,
            @RequestParam @Parameter(description = "用户2ID") Long userId2) {
        log.info("REST请求 - 获取最新消息: userId1={}, userId2={}", userId1, userId2);
        return messageFacadeService.getLatestMessage(userId1, userId2);
    }

    // =================== 系统管理 ===================

    /**
     * 清理过期删除消息
     * 
     * @param beforeTime 截止时间
     * @return 清理的消息数量
     */
    @DeleteMapping("/cleanup")
    @Operation(summary = "清理过期消息", description = "物理删除指定时间前的已删除消息")
    public Result<Integer> cleanupExpiredMessages(
            @RequestParam @Parameter(description = "截止时间") 
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beforeTime) {
        log.info("REST请求 - 清理过期消息: beforeTime={}", beforeTime);
        return messageFacadeService.cleanupExpiredMessages(beforeTime);
    }

    /**
     * 消息系统健康检查
     * 
     * @return 系统状态
     */
    @GetMapping("/health")
    @Operation(summary = "系统健康检查", description = "检查消息系统运行状态")
    public Result<String> healthCheck() {
        log.info("REST请求 - 消息系统健康检查");
        return messageFacadeService.healthCheck();
    }
}