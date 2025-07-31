package com.gig.collide.message.controller;

import com.gig.collide.api.message.MessageFacadeService;
import com.gig.collide.api.message.request.MessageCreateRequest;
import com.gig.collide.api.message.request.MessageQueryRequest;
import com.gig.collide.api.message.response.MessageResponse;
import com.gig.collide.api.message.response.MessageSessionResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 消息控制器 - 简洁版
 * 基于message-simple.sql的单表设计，提供私信和留言板功能
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
@Tag(name = "消息管理", description = "私信消息和留言板相关接口")
public class MessageController {

    private final MessageFacadeService messageFacadeService;

    // =================== 消息发送 ===================

    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送私信消息，支持文本、图片、文件等类型")
    public Result<MessageResponse> sendMessage(@Valid @RequestBody MessageCreateRequest request) {
        log.debug("REST发送消息: 发送者={}, 接收者={}", request.getSenderId(), request.getReceiverId());
        return messageFacadeService.sendMessage(request);
    }

    @PostMapping("/reply")
    @Operation(summary = "回复消息", description = "回复指定消息")
    public Result<MessageResponse> replyMessage(@Valid @RequestBody MessageCreateRequest request) {
        log.debug("REST回复消息: 回复消息ID={}", request.getReplyToId());
        return messageFacadeService.replyMessage(request);
    }

    // =================== 消息查询 ===================

    @GetMapping("/{messageId}")
    @Operation(summary = "获取消息详情", description = "根据消息ID获取详细信息")
    public Result<MessageResponse> getMessageById(@PathVariable("messageId") Long messageId,
                                                @Parameter(description = "查看者用户ID") @RequestParam Long userId) {
        log.debug("REST获取消息详情: ID={}, 用户ID={}", messageId, userId);
        return messageFacadeService.getMessageById(messageId, userId);
    }

    @PostMapping("/query")
    @Operation(summary = "分页查询消息", description = "根据条件分页查询消息列表")
    public Result<PageResponse<MessageResponse>> queryMessages(@Valid @RequestBody MessageQueryRequest request) {
        log.debug("REST查询消息: 页码={}", request.getCurrentPage());
        return messageFacadeService.queryMessages(request);
    }

    @GetMapping("/chat")
    @Operation(summary = "获取聊天记录", description = "获取两个用户之间的聊天记录")
    public Result<PageResponse<MessageResponse>> getChatHistory(
            @Parameter(description = "用户1 ID") @RequestParam Long userId1,
            @Parameter(description = "用户2 ID") @RequestParam Long userId2,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取聊天记录: 用户1={}, 用户2={}", userId1, userId2);
        return messageFacadeService.getChatHistory(userId1, userId2, pageNum, pageSize);
    }

    @GetMapping("/wall/{userId}")
    @Operation(summary = "获取用户留言板", description = "获取用户个人页面的留言（包含置顶消息）")
    public Result<PageResponse<MessageResponse>> getUserWallMessages(
            @PathVariable("userId") Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取用户留言板: 用户ID={}", userId);
        return messageFacadeService.getUserWallMessages(userId, pageNum, pageSize);
    }

    @GetMapping("/replies/{messageId}")
    @Operation(summary = "获取消息回复", description = "获取指定消息的所有回复")
    public Result<PageResponse<MessageResponse>> getMessageReplies(
            @PathVariable("messageId") Long messageId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取消息回复: 消息ID={}", messageId);
        return messageFacadeService.getMessageReplies(messageId, pageNum, pageSize);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索消息", description = "在用户消息中搜索关键词")
    public Result<PageResponse<MessageResponse>> searchMessages(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST搜索消息: 用户ID={}, 关键词={}", userId, keyword);
        return messageFacadeService.searchMessages(userId, keyword, pageNum, pageSize);
    }

    // =================== 会话管理 ===================

    @GetMapping("/sessions/{userId}")
    @Operation(summary = "获取用户会话列表", description = "获取用户的所有聊天会话")
    public Result<PageResponse<MessageSessionResponse>> getUserSessions(
            @PathVariable("userId") Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST获取用户会话: 用户ID={}", userId);
        return messageFacadeService.getUserSessions(userId, pageNum, pageSize);
    }

    // =================== 消息状态 ===================

    @PutMapping("/{messageId}/read")
    @Operation(summary = "标记消息已读", description = "将指定消息标记为已读状态")
    public Result<Void> markMessageAsRead(@PathVariable("messageId") Long messageId,
                                        @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST标记消息已读: ID={}, 用户ID={}", messageId, userId);
        return messageFacadeService.markMessageAsRead(messageId, userId);
    }

    @PutMapping("/batch-read")
    @Operation(summary = "批量标记已读", description = "批量将消息标记为已读状态")
    public Result<Void> batchMarkAsRead(@Parameter(description = "消息ID列表") @RequestBody List<Long> messageIds,
                                      @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST批量标记已读: 数量={}, 用户ID={}", messageIds.size(), userId);
        return messageFacadeService.batchMarkAsRead(messageIds, userId);
    }

    @PutMapping("/{messageId}/pin")
    @Operation(summary = "置顶/取消置顶消息", description = "设置消息的置顶状态，用于留言板功能")
    public Result<Void> pinMessage(@PathVariable("messageId") Long messageId,
                                 @Parameter(description = "用户ID") @RequestParam Long userId,
                                 @Parameter(description = "是否置顶") @RequestParam Boolean isPinned) {
        log.debug("REST设置消息置顶: ID={}, 置顶={}", messageId, isPinned);
        return messageFacadeService.pinMessage(messageId, userId, isPinned);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除消息", description = "逻辑删除指定消息")
    public Result<Void> deleteMessage(@PathVariable("messageId") Long messageId,
                                    @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST删除消息: ID={}, 用户ID={}", messageId, userId);
        return messageFacadeService.deleteMessage(messageId, userId);
    }

    // =================== 统计信息 ===================

    @GetMapping("/unread-count/{userId}")
    @Operation(summary = "获取未读消息数", description = "获取用户的未读消息总数")
    public Result<Long> getUnreadCount(@PathVariable("userId") Long userId) {
        log.debug("REST获取未读消息数: 用户ID={}", userId);
        return messageFacadeService.getUnreadCount(userId);
    }

    @GetMapping("/unread-count/{userId}/with/{otherUserId}")
    @Operation(summary = "获取与特定用户的未读消息数", description = "获取与某个用户的未读消息数")
    public Result<Long> getUnreadCountWithUser(@PathVariable("userId") Long userId,
                                             @PathVariable("otherUserId") Long otherUserId) {
        log.debug("REST获取与用户的未读消息数: 用户ID={}, 对方ID={}", userId, otherUserId);
        return messageFacadeService.getUnreadCountWithUser(userId, otherUserId);
    }

    @GetMapping("/statistics/{userId}")
    @Operation(summary = "获取消息统计信息", description = "获取用户的消息统计数据")
    public Result<Map<String, Object>> getMessageStatistics(@PathVariable("userId") Long userId) {
        log.debug("REST获取消息统计: 用户ID={}", userId);
        return messageFacadeService.getMessageStatistics(userId);
    }
}