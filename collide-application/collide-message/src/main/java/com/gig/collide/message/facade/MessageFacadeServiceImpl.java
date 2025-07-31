package com.gig.collide.message.facade;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.message.MessageFacadeService;
import com.gig.collide.api.message.request.MessageCreateRequest;
import com.gig.collide.api.message.request.MessageQueryRequest;
import com.gig.collide.api.message.response.MessageResponse;
import com.gig.collide.api.message.response.MessageSessionResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.message.domain.entity.Message;
import com.gig.collide.message.domain.entity.MessageSession;
import com.gig.collide.message.domain.service.MessageService;
import com.gig.collide.message.domain.service.MessageSessionService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息门面服务实现 - 简洁版
 * 基于message-simple.sql的单表设计，对接Dubbo服务
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService
@RequiredArgsConstructor
public class MessageFacadeServiceImpl implements MessageFacadeService {

    private final MessageService messageService;
    private final MessageSessionService messageSessionService;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // =================== 基础操作 ===================

    @Override
    public Result<MessageResponse> sendMessage(MessageCreateRequest request) {
        try {
            log.debug("门面发送消息: 发送者={}, 接收者={}", request.getSenderId(), request.getReceiverId());
            
            // 验证请求参数
            if (request.getSenderId() == null || request.getReceiverId() == null) {
                return Result.error("INVALID_PARAM", "发送者ID和接收者ID不能为空");
            }
            
            if (request.getSenderId().equals(request.getReceiverId())) {
                return Result.error("INVALID_PARAM", "不能给自己发送消息");
            }
            
            // 转换为实体对象
            Message message = convertToEntity(request);
            
            // 发送消息
            Message savedMessage = messageService.sendMessage(message);
            if (savedMessage == null) {
                return Result.error("SEND_FAILED", "消息发送失败");
            }
            
            // 处理会话更新
            messageSessionService.handleNewMessage(
                request.getSenderId(), 
                request.getReceiverId(), 
                savedMessage.getId(), 
                savedMessage.getCreateTime()
            );
            
            // 转换为响应对象
            MessageResponse response = convertToResponse(savedMessage);
            
            log.info("门面发送消息成功: ID={}", savedMessage.getId());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面发送消息异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<MessageResponse> getMessageById(Long messageId, Long userId) {
        try {
            log.debug("门面获取消息详情: ID={}, 用户ID={}", messageId, userId);
            
            if (messageId == null || userId == null) {
                return Result.error("INVALID_PARAM", "消息ID和用户ID不能为空");
            }
            
            // 验证权限
            if (!messageService.canUserViewMessage(messageId, userId)) {
                return Result.error("ACCESS_DENIED", "无权限查看该消息");
            }
            
            Message message = messageService.getMessageById(messageId);
            if (message == null) {
                return Result.error("MESSAGE_NOT_FOUND", "消息不存在");
            }
            
            MessageResponse response = convertToResponse(message);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面获取消息详情异常: ID={}", messageId, e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> markMessageAsRead(Long messageId, Long userId) {
        try {
            log.debug("门面标记消息已读: ID={}, 用户ID={}", messageId, userId);
            
            if (messageId == null || userId == null) {
                return Result.error("INVALID_PARAM", "消息ID和用户ID不能为空");
            }
            
            boolean success = messageService.markMessageAsRead(messageId, userId);
            if (!success) {
                return Result.error("MARK_FAILED", "标记已读失败");
            }
            
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("门面标记消息已读异常: ID={}", messageId, e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchMarkAsRead(List<Long> messageIds, Long userId) {
        try {
            log.debug("门面批量标记已读: 数量={}, 用户ID={}", messageIds.size(), userId);
            
            if (messageIds == null || messageIds.isEmpty() || userId == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            boolean success = messageService.batchMarkAsRead(messageIds, userId);
            if (!success) {
                return Result.error("BATCH_MARK_FAILED", "批量标记已读失败");
            }
            
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("门面批量标记已读异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteMessage(Long messageId, Long userId) {
        try {
            log.debug("门面删除消息: ID={}, 用户ID={}", messageId, userId);
            
            if (messageId == null || userId == null) {
                return Result.error("INVALID_PARAM", "消息ID和用户ID不能为空");
            }
            
            boolean success = messageService.deleteMessage(messageId, userId);
            if (!success) {
                return Result.error("DELETE_FAILED", "删除消息失败");
            }
            
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("门面删除消息异常: ID={}", messageId, e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    // =================== 查询操作 ===================

    @Override
    public Result<PageResponse<MessageResponse>> queryMessages(MessageQueryRequest request) {
        try {
            log.debug("门面查询消息: 页码={}", request.getCurrentPage());
            
            // 解析时间参数
            LocalDateTime startTime = parseDateTime(request.getStartTime());
            LocalDateTime endTime = parseDateTime(request.getEndTime());
            
            Page<Message> page = messageService.queryMessages(
                request.getSenderId(), request.getReceiverId(), request.getMessageType(),
                request.getStatus(), request.getIsPinned(), request.getReplyToId(),
                request.getKeyword(), startTime, endTime,
                request.getOrderBy(), request.getOrderDirection(),
                request.getCurrentPage(), request.getPageSize()
            );
            
            PageResponse<MessageResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面查询消息异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> getChatHistory(Long userId1, Long userId2, Integer currentPage, Integer pageSize) {
        try {
            log.debug("门面获取聊天记录: 用户1={}, 用户2={}", userId1, userId2);
            
            if (userId1 == null || userId2 == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Page<Message> page = messageService.getChatHistory(userId1, userId2, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面获取聊天记录异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<MessageSessionResponse>> getUserSessions(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("门面获取用户会话: 用户ID={}", userId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Page<MessageSession> page = messageSessionService.getUserSessions(
                userId, false, null, "last_message_time", "DESC", currentPage, pageSize
            );
            
            PageResponse<MessageSessionResponse> response = convertToSessionPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面获取用户会话异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Long> getUnreadCount(Long userId) {
        try {
            log.debug("门面获取未读消息数: 用户ID={}", userId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Long count = messageService.getUnreadCount(userId);
            return Result.success(count != null ? count : 0L);
            
        } catch (Exception e) {
            log.error("门面获取未读消息数异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Long> getUnreadCountWithUser(Long userId, Long otherUserId) {
        try {
            log.debug("门面获取与用户的未读消息数: 用户ID={}, 对方ID={}", userId, otherUserId);
            
            if (userId == null || otherUserId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Long count = messageService.getUnreadCountWithUser(userId, otherUserId);
            return Result.success(count != null ? count : 0L);
            
        } catch (Exception e) {
            log.error("门面获取与用户的未读消息数异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> getUserWallMessages(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("门面获取用户留言板: 用户ID={}", userId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Page<Message> page = messageService.getUserWallMessages(userId, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面获取用户留言板异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Void> pinMessage(Long messageId, Long userId, Boolean isPinned) {
        try {
            log.debug("门面设置消息置顶: ID={}, 用户ID={}, 置顶={}", messageId, userId, isPinned);
            
            if (messageId == null || userId == null || isPinned == null) {
                return Result.error("INVALID_PARAM", "参数不能为空");
            }
            
            boolean success = messageService.pinMessage(messageId, userId, isPinned);
            if (!success) {
                return Result.error("PIN_FAILED", "设置置顶失败");
            }
            
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("门面设置消息置顶异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<MessageResponse> replyMessage(MessageCreateRequest request) {
        try {
            log.debug("门面回复消息: 回复消息ID={}", request.getReplyToId());
            
            if (request.getReplyToId() == null) {
                return Result.error("INVALID_PARAM", "回复消息ID不能为空");
            }
            
            // 复用发送消息的逻辑
            return sendMessage(request);
            
        } catch (Exception e) {
            log.error("门面回复消息异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> getMessageReplies(Long messageId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("门面获取消息回复: 消息ID={}", messageId);
            
            if (messageId == null) {
                return Result.error("INVALID_PARAM", "消息ID不能为空");
            }
            
            Page<Message> page = messageService.getMessageReplies(messageId, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面获取消息回复异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<MessageResponse>> searchMessages(Long userId, String keyword, Integer currentPage, Integer pageSize) {
        try {
            log.debug("门面搜索消息: 用户ID={}, 关键词={}", userId, keyword);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Page<Message> page = messageService.searchMessages(userId, keyword, currentPage, pageSize);
            PageResponse<MessageResponse> response = convertToPageResponse(page);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("门面搜索消息异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getMessageStatistics(Long userId) {
        try {
            log.debug("门面获取消息统计: 用户ID={}", userId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Map<String, Object> statistics = messageService.getMessageStatistics(userId);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("门面获取消息统计异常", e);
            return Result.error("SYSTEM_ERROR", "系统错误：" + e.getMessage());
        }
    }

    // =================== 私有转换方法 ===================

    private Message convertToEntity(MessageCreateRequest request) {
        Message message = new Message();
        BeanUtils.copyProperties(request, message);
        return message;
    }

    private MessageResponse convertToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        BeanUtils.copyProperties(message, response);
        return response;
    }

    private PageResponse<MessageResponse> convertToPageResponse(Page<Message> page) {
        List<MessageResponse> responses = page.getRecords().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return PageResponse.of(
            responses,
            (int) page.getTotal(),
            (int) page.getSize(),
            (int) page.getCurrent()
        );
    }

    private MessageSessionResponse convertToSessionResponse(MessageSession session) {
        MessageSessionResponse response = new MessageSessionResponse();
        BeanUtils.copyProperties(session, response);
        return response;
    }

    private PageResponse<MessageSessionResponse> convertToSessionPageResponse(Page<MessageSession> page) {
        List<MessageSessionResponse> responses = page.getRecords().stream()
            .map(this::convertToSessionResponse)
            .collect(Collectors.toList());
        
        return PageResponse.of(
            responses,
            (int) page.getTotal(),
            (int) page.getSize(),
            (int) page.getCurrent()
        );
    }

    private LocalDateTime parseDateTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(timeStr, TIME_FORMATTER);
        } catch (Exception e) {
            log.warn("时间格式解析失败: {}", timeStr);
            return null;
        }
    }
}