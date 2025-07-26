package com.gig.collide.comment.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.comment.constant.CommentStatus;
import com.gig.collide.api.comment.constant.CommentType;
import com.gig.collide.api.comment.request.CommentCreateRequest;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.infrastructure.exception.CommentErrorCode;
import com.gig.collide.comment.infrastructure.mapper.CommentMapper;
import com.gig.collide.base.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.gig.collide.mq.producer.StreamProducer;
import com.gig.collide.mq.constant.MqConstant;
import com.gig.collide.cache.constant.CacheConstant;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 评论领域服务
 * 完全去连表化设计，统计基于冗余字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentDomainService {

    private final CommentMapper commentMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final StreamProducer streamProducer;

    private static final String COMMENT_IDEMPOTENT_PREFIX = "comment:idempotent:";
    private static final String LIKE_IDEMPOTENT_PREFIX = "comment:like:";
    private static final Duration IDEMPOTENT_TTL = Duration.ofMinutes(5);

    /**
     * 创建评论（去连表化设计，包含幂等性检查）
     */
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(CommentCreateRequest createRequest) {
        // 幂等性检查
        String idempotentKey = generateIdempotentKey(createRequest);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotentKey))) {
            log.warn("重复评论请求被拦截，用户ID: {}, 目标ID: {}", 
                createRequest.getUserId(), createRequest.getTargetId());
            throw new BizException("请不要重复提交评论", CommentErrorCode.COMMENT_CREATE_FAILED);
        }

        // 参数验证
        validateCreateRequest(createRequest);

        // 构建评论实体
        Comment comment = buildCommentFromRequest(createRequest);

        // 设置父评论关系
        if (createRequest.getParentCommentId() != null && createRequest.getParentCommentId() > 0) {
            Comment parentComment = commentMapper.selectById(createRequest.getParentCommentId());
            if (parentComment == null) {
                throw new BizException(CommentErrorCode.PARENT_COMMENT_NOT_FOUND);
            }
            comment.setParentCommentId(createRequest.getParentCommentId());
            comment.setRootCommentId(parentComment.getRootCommentId() != null && parentComment.getRootCommentId() > 0 
                ? parentComment.getRootCommentId() : parentComment.getId());
            comment.setReplyToUserId(createRequest.getReplyToUserId());
        } else {
            comment.setParentCommentId(0L);
            comment.setRootCommentId(0L);
        }

        // 保存评论
        int result = commentMapper.insert(comment);
        if (result <= 0) {
            throw new BizException(CommentErrorCode.COMMENT_CREATE_FAILED);
        }

        // 设置幂等性标记
        redisTemplate.opsForValue().set(idempotentKey, comment.getId().toString(), IDEMPOTENT_TTL);

        // 异步更新父评论的回复数（冗余字段更新）
        if (comment.getParentCommentId() != null && comment.getParentCommentId() > 0) {
            updateParentReplyCount(comment.getParentCommentId(), 1);
        }

        // ✅ 新增：发送评论创建事件
        publishCommentCreatedEvent(comment);

        return comment;
    }

    /**
     * 删除评论（软删除，更新冗余统计）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BizException(CommentErrorCode.COMMENT_NOT_FOUND);
        }

        // 权限检查
        if (!comment.getUserId().equals(currentUserId)) {
            throw new BizException(CommentErrorCode.NO_PERMISSION_TO_DELETE);
        }

        // 使用MyBatis Plus的逻辑删除
        comment.setDeleted(1);
        comment.setUpdateTime(LocalDateTime.now());
        int result = commentMapper.updateById(comment);

        if (result > 0) {
            // 异步更新父评论的回复数
            if (comment.getParentCommentId() != null && comment.getParentCommentId() > 0) {
                updateParentReplyCount(comment.getParentCommentId(), -1);
            }
            
            // ✅ 新增：发送评论删除事件
            publishCommentDeletedEvent(comment);
        }

        return result > 0;
    }

    /**
     * 分页查询评论（单表查询，无连表）
     */
    public IPage<Comment> queryCommentPage(CommentType commentType, Long targetId, 
                                         Long parentCommentId, CommentStatus status,
                                         int pageNum, int pageSize, String orderBy) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectCommentPage(page, commentType, targetId, parentCommentId, status, orderBy);
    }

    /**
     * 查询评论树（单表递归查询）
     */
    public List<Comment> queryCommentTree(Long targetId, CommentType commentType, CommentStatus status) {
        return commentMapper.selectCommentTree(targetId, commentType, status);
    }
    
    /**
     * 查询评论树（重载方法，适配Facade调用）
     */
    public List<Comment> queryCommentTree(CommentType commentType, Long targetId) {
        return commentMapper.selectCommentTree(targetId, commentType, CommentStatus.NORMAL);
    }

    /**
     * 统计评论数量（单表统计）
     */
    public Long countComments(Long targetId, CommentType commentType, 
                            Long parentCommentId, CommentStatus status) {
        return commentMapper.countComments(targetId, commentType, parentCommentId, status);
    }
    
    /**
     * 统计评论数量（重载方法，适配Facade调用）
     */
    public Long countComments(CommentType commentType, Long targetId) {
        return commentMapper.countComments(targetId, commentType, null, CommentStatus.NORMAL);
    }
    
    /**
     * 查询评论列表（适配Facade调用）
     */
    public List<Comment> queryComments(CommentType commentType, Long targetId, 
                                     CommentStatus status, Long parentCommentId, 
                                     int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        IPage<Comment> result = commentMapper.selectCommentPage(page, commentType, targetId, parentCommentId, status, "create_time DESC");
        return result.getRecords();
    }
    
    /**
     * 分页查询评论（适配Facade调用）
     */
    public IPage<Comment> pageQueryComments(CommentType commentType, CommentStatus status, 
                                          int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectCommentPage(page, commentType, null, null, status, "create_time DESC");
    }
    
    /**
     * 更新评论状态（适配Facade调用）
     */
    public void updateCommentStatus(Long commentId, Long userId, CommentStatus status) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment != null) {
            comment.setStatus(status);
            comment.setUpdateTime(LocalDateTime.now());
            commentMapper.updateById(comment);
        }
    }

    /**
     * 查询用户评论历史（单表查询）
     */
    public IPage<Comment> queryUserComments(Long userId, CommentType commentType, 
                                          CommentStatus status, int pageNum, int pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return commentMapper.selectUserComments(page, userId, commentType, status);
    }

    /**
     * 查询热门评论（基于冗余统计字段）
     */
    public List<Comment> queryHotComments(Long targetId, CommentType commentType, Integer limit) {
        return commentMapper.selectHotComments(targetId, commentType, limit);
    }

    /**
     * 获取评论统计信息（基于冗余字段，无连表）
     */
    public Map<String, Object> getCommentStatistics(Long targetId, CommentType commentType) {
        return commentMapper.getCommentStatistics(targetId, commentType);
    }

    /**
     * 更新评论点赞数（冗余字段更新，包含幂等性检查）
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCommentLikeCount(Long commentId, Long userId, Integer increment) {
        // 点赞幂等性检查
        String likeKey = LIKE_IDEMPOTENT_PREFIX + userId + ":" + commentId;
        
        if (increment > 0) {
            // 点赞操作：检查是否已经点过赞
            if (Boolean.TRUE.equals(redisTemplate.hasKey(likeKey))) {
                log.warn("用户已点赞该评论，用户ID: {}, 评论ID: {}", userId, commentId);
                return false;
            }
            // 设置点赞标记
            redisTemplate.opsForValue().set(likeKey, "1", Duration.ofDays(30));
        } else {
            // 取消点赞操作：删除点赞标记
            redisTemplate.delete(likeKey);
        }
        
        int result = commentMapper.updateLikeCount(commentId, increment);
        
        if (result > 0) {
            // ✅ 新增：发送点赞事件
            publishCommentLikedEvent(commentId, userId, increment > 0);
        }
        
        return result > 0;
    }

    /**
     * 检查用户是否已点赞评论
     */
    public boolean isCommentLikedByUser(Long commentId, Long userId) {
        String likeKey = LIKE_IDEMPOTENT_PREFIX + userId + ":" + commentId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(likeKey));
    }

    /**
     * 查询子评论列表
     */
    public List<Comment> queryChildComments(Long parentCommentId, Integer limit) {
        return commentMapper.selectChildComments(parentCommentId, limit);
    }

    /**
     * 验证创建请求
     */
    private void validateCreateRequest(CommentCreateRequest request) {
        if (request.getTargetId() == null || request.getTargetId() <= 0) {
            throw new BizException(CommentErrorCode.INVALID_TARGET_ID);
        }
        if (request.getUserId() == null || request.getUserId() <= 0) {
            throw new BizException(CommentErrorCode.INVALID_USER_ID);
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BizException(CommentErrorCode.COMMENT_CONTENT_EMPTY);
        }
        if (request.getContent().length() > 2000) {
            throw new BizException(CommentErrorCode.COMMENT_CONTENT_TOO_LONG);
        }
    }

    /**
     * 从请求构建评论实体
     */
    private Comment buildCommentFromRequest(CommentCreateRequest request) {
        Comment comment = new Comment();
        comment.setCommentType(request.getCommentType());
        comment.setTargetId(request.getTargetId());
        comment.setContent(request.getContent().trim());
        comment.setUserId(request.getUserId());
        comment.setStatus(CommentStatus.NORMAL);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        comment.setIsPinned(false);
        comment.setIsHot(false);
        // 这些字段从请求中获取不到，使用默认值或从上下文获取
        // comment.setIpAddress(getClientIpAddress());
        // comment.setDeviceInfo(getClientDeviceInfo());
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setDeleted(0);
        return comment;
    }

    /**
     * 生成幂等性检查的键
     * ✅ 优化：使用 MD5 哈希算法提高安全性和唯一性
     */
    private String generateIdempotentKey(CommentCreateRequest request) {
        // 构建唯一标识字符串
        String uniqueString = String.format("%d:%d:%s:%d", 
            request.getUserId(),
            request.getTargetId(),
            request.getContent().trim(),
            request.getParentCommentId() != null ? request.getParentCommentId() : 0L
        );
        
        // 使用 MD5 生成哈希值
        String contentHash = DigestUtils.md5DigestAsHex(uniqueString.getBytes(StandardCharsets.UTF_8));
        
        return COMMENT_IDEMPOTENT_PREFIX + contentHash;
    }

    /**
     * 更新父评论的回复数（异步处理冗余字段）
     */
    private void updateParentReplyCount(Long parentCommentId, Integer increment) {
        try {
            commentMapper.updateReplyCount(parentCommentId, increment);
        } catch (Exception e) {
            log.error("更新父评论回复数失败，parentCommentId: {}, increment: {}", 
                    parentCommentId, increment, e);
            // 这里可以发送消息队列进行异步重试
        }
    }

    /**
     * ✅ 发送评论创建事件
     */
    private void publishCommentCreatedEvent(Comment comment) {
        try {
            Map<String, Object> eventData = Map.of(
                "commentId", comment.getId(),
                "targetId", comment.getTargetId(),
                "commentType", comment.getCommentType().name(),
                "userId", comment.getUserId(),
                "parentCommentId", comment.getParentCommentId() != null ? comment.getParentCommentId() : 0L,
                "content", comment.getContent(),
                "eventTime", LocalDateTime.now().toString()
            );
            
            // 使用标准化的StreamProducer发送消息
            streamProducer.send(
                MqConstant.COMMENT_CREATED_TOPIC,
                MqConstant.CREATE_TAG,
                JSON.toJSONString(eventData)
            );
            
            log.info("评论创建事件发送成功，评论ID: {}", comment.getId());
                
        } catch (Exception e) {
            log.error("发送评论创建事件异常，评论ID: {}", comment.getId(), e);
        }
    }

    /**
     * ✅ 发送评论删除事件
     */
    private void publishCommentDeletedEvent(Comment comment) {
        try {
            Map<String, Object> eventData = Map.of(
                "commentId", comment.getId(),
                "targetId", comment.getTargetId(),
                "commentType", comment.getCommentType().name(),
                "userId", comment.getUserId(),
                "eventTime", LocalDateTime.now().toString()
            );
            
            // 使用标准化的StreamProducer发送消息
            streamProducer.send(
                MqConstant.COMMENT_DELETED_TOPIC,
                MqConstant.DELETE_TAG,
                JSON.toJSONString(eventData)
            );
            
            log.info("评论删除事件发送成功，评论ID: {}", comment.getId());
                
        } catch (Exception e) {
            log.error("发送评论删除事件异常，评论ID: {}", comment.getId(), e);
        }
    }

    /**
     * ✅ 发送评论点赞事件
     */
    private void publishCommentLikedEvent(Long commentId, Long userId, Boolean isLike) {
        try {
            Map<String, Object> eventData = Map.of(
                "commentId", commentId,
                "userId", userId,
                "isLike", isLike,
                "increment", isLike ? 1 : -1,
                "eventTime", LocalDateTime.now().toString()
            );
            
            // 使用标准化的StreamProducer发送消息
            streamProducer.send(
                MqConstant.COMMENT_LIKED_TOPIC,
                MqConstant.UPDATE_TAG,
                JSON.toJSONString(eventData)
            );
            
            log.info("评论点赞事件发送成功，评论ID: {}, 点赞: {}", commentId, isLike);
                
        } catch (Exception e) {
            log.error("发送评论点赞事件异常，评论ID: {}", commentId, e);
        }
    }

    /**
     * ✅ 发送统计变更事件
     */
    private void publishStatisticsChangedEvent(Long targetId, String targetType, String statisticsType, Integer delta) {
        try {
            Map<String, Object> eventData = Map.of(
                "targetId", targetId,
                "targetType", targetType,
                "statisticsType", statisticsType,
                "delta", delta,
                "eventTime", LocalDateTime.now().toString()
            );
            
            // 使用标准化的StreamProducer发送消息
            streamProducer.send(
                MqConstant.COMMENT_STATISTICS_CHANGED_TOPIC,
                MqConstant.STATISTICS_TAG,
                JSON.toJSONString(eventData)
            );
            
            log.info("统计变更事件发送成功，目标ID: {}, 类型: {}", targetId, statisticsType);
                
        } catch (Exception e) {
            log.error("发送统计变更事件异常，目标ID: {}", targetId, e);
        }
    }
} 