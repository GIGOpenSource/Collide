package com.gig.collide.comment.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.comment.domain.entity.Comment;
import com.gig.collide.comment.domain.service.CommentService;
import com.gig.collide.comment.infrastructure.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论业务服务实现 - 简洁版
 * 基于单表无连表设计，包含完整业务逻辑
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        log.info("创建评论：{}", comment);
        
        // 验证基础参数
        validateCommentForCreate(comment);
        
        // 初始化默认值
        comment.initDefaults();
        
        // 如果是回复评论，需要更新父评论的回复数
        if (comment.isReplyComment()) {
            increaseReplyCount(comment.getParentCommentId(), 1);
        }
        
        // 保存评论
        int result = commentMapper.insert(comment);
        if (result > 0) {
            log.info("评论创建成功，ID：{}", comment.getId());
            return comment;
        }
        
        throw new RuntimeException("评论创建失败");
    }

    @Override
    @Transactional
    public Comment updateComment(Comment comment) {
        log.info("更新评论：{}", comment);
        
        // 验证评论是否存在
        Comment existing = getCommentById(comment.getId(), true);
        if (existing == null) {
            throw new RuntimeException("评论不存在，ID：" + comment.getId());
        }
        
        // 验证权限和状态
        if (!existing.canBeDeleted()) {
            throw new RuntimeException("评论状态不允许修改");
        }
        
        // 更新时间
        comment.setUpdateTime(LocalDateTime.now());
        
        int result = commentMapper.updateById(comment);
        if (result > 0) {
            log.info("评论更新成功，ID：{}", comment.getId());
            return commentMapper.selectById(comment.getId());
        }
        
        throw new RuntimeException("评论更新失败");
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        log.info("删除评论，ID：{}，用户：{}", commentId, userId);
        
        Comment comment = getCommentById(commentId, true);
        if (comment == null) {
            log.warn("评论不存在，无法删除，ID：{}", commentId);
            return false;
        }
        
        // 验证权限
        if (!validateCommentPermission(commentId, userId, "delete")) {
            throw new RuntimeException("无权限删除该评论");
        }
        
        // 逻辑删除
        comment.delete();
        int result = commentMapper.updateById(comment);
        
        // 如果是回复评论，需要减少父评论的回复数
        if (comment.isReplyComment() && result > 0) {
            increaseReplyCount(comment.getParentCommentId(), -1);
        }
        
        log.info("评论删除成功，ID：{}", commentId);
        return result > 0;
    }

    @Override
    public Comment getCommentById(Long commentId, Boolean includeDeleted) {
        if (commentId == null) {
            return null;
        }
        
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getId, commentId);
        
        if (!Boolean.TRUE.equals(includeDeleted)) {
            wrapper.ne(Comment::getStatus, "DELETED");
        }
        
        return commentMapper.selectOne(wrapper);
    }

    @Override
    public List<Comment> getCommentsByIds(List<Long> commentIds, Boolean includeDeleted) {
        if (commentIds == null || commentIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Comment::getId, commentIds);
        
        if (!Boolean.TRUE.equals(includeDeleted)) {
            wrapper.ne(Comment::getStatus, "DELETED");
        }
        
        return commentMapper.selectList(wrapper);
    }

    @Override
    public IPage<Comment> queryComments(Long targetId, String commentType, Long parentCommentId,
                                      String status, Integer pageNum, Integer pageSize,
                                      String orderBy, String orderDirection) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        if (targetId != null) {
            return commentMapper.selectTargetCommentsPage(page, targetId, commentType, 
                parentCommentId, status, orderBy, orderDirection);
        }
        
        // 通用查询
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        if (commentType != null) {
            wrapper.eq(Comment::getCommentType, commentType);
        }
        if (parentCommentId != null) {
            wrapper.eq(Comment::getParentCommentId, parentCommentId);
        }
        if (status != null) {
            wrapper.eq(Comment::getStatus, status);
        }
        
        // 排序
        if ("like_count".equals(orderBy)) {
            wrapper.orderBy(true, "ASC".equals(orderDirection), Comment::getLikeCount);
        } else if ("reply_count".equals(orderBy)) {
            wrapper.orderBy(true, "ASC".equals(orderDirection), Comment::getReplyCount);
        } else if ("update_time".equals(orderBy)) {
            wrapper.orderBy(true, "ASC".equals(orderDirection), Comment::getUpdateTime);
        } else {
            wrapper.orderBy(true, "ASC".equals(orderDirection), Comment::getCreateTime);
        }
        
        return commentMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<Comment> getTargetComments(Long targetId, String commentType, Long parentCommentId,
                                          Integer pageNum, Integer pageSize, String orderBy, String orderDirection) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        String defaultStatus = "NORMAL";
        
        return commentMapper.selectTargetCommentsPage(page, targetId, commentType, 
            parentCommentId, defaultStatus, orderBy, orderDirection);
    }

    @Override
    public IPage<Comment> getCommentReplies(Long parentCommentId, Integer pageNum, Integer pageSize,
                                          String orderBy, String orderDirection) {
        return getTargetComments(null, null, parentCommentId, pageNum, pageSize, orderBy, orderDirection);
    }

    @Override
    public IPage<Comment> getCommentTree(Long targetId, String commentType, Integer maxDepth,
                                       Integer pageNum, Integer pageSize, String orderBy, String orderDirection) {
        // 获取所有相关评论
        List<Comment> allComments = commentMapper.selectCommentTree(targetId, commentType, 
            maxDepth, "NORMAL", orderBy, orderDirection);
        
        // 构建树形结构
        List<Comment> treeComments = buildCommentTree(allComments, maxDepth);
        
        // 手动分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, treeComments.size());
        List<Comment> pageData = start < treeComments.size() ? 
            treeComments.subList(start, end) : new ArrayList<>();
        
        Page<Comment> page = new Page<>(pageNum, pageSize, treeComments.size());
        page.setRecords(pageData);
        
        return page;
    }

    @Override
    public IPage<Comment> getUserComments(Long userId, String commentType, String status,
                                        Integer pageNum, Integer pageSize, String orderBy, String orderDirection) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        return commentMapper.selectUserCommentsPage(page, userId, commentType, 
            status != null ? status : "NORMAL", orderBy, orderDirection);
    }

    @Override
    public IPage<Comment> getUserReplies(Long userId, Integer pageNum, Integer pageSize,
                                       String orderBy, String orderDirection) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        return commentMapper.selectUserRepliesPage(page, userId, "NORMAL", orderBy, orderDirection);
    }

    @Override
    @Transactional
    public boolean updateCommentStatus(Long commentId, String status, Long operatorId) {
        log.info("更新评论状态，ID：{}，状态：{}，操作人：{}", commentId, status, operatorId);
        
        Comment comment = getCommentById(commentId, true);
        if (comment == null) {
            return false;
        }
        
        // 验证权限
        if (!validateCommentPermission(commentId, operatorId, "status")) {
            throw new RuntimeException("无权限修改评论状态");
        }
        
        comment.setStatus(status);
        comment.setUpdateTime(LocalDateTime.now());
        
        return commentMapper.updateById(comment) > 0;
    }

    @Override
    @Transactional
    public int batchUpdateCommentStatus(List<Long> commentIds, String status, Long operatorId) {
        if (commentIds == null || commentIds.isEmpty()) {
            return 0;
        }
        
        log.info("批量更新评论状态，数量：{}，状态：{}", commentIds.size(), status);
        
        return commentMapper.batchUpdateStatus(commentIds, status);
    }

    @Override
    public boolean hideComment(Long commentId, Long operatorId) {
        return updateCommentStatus(commentId, "HIDDEN", operatorId);
    }

    @Override
    public boolean restoreComment(Long commentId, Long operatorId) {
        return updateCommentStatus(commentId, "NORMAL", operatorId);
    }

    @Override
    @Transactional
    public int increaseLikeCount(Long commentId, Integer increment) {
        if (commentId == null || increment == null) {
            return 0;
        }
        
        int result = commentMapper.increaseLikeCount(commentId, increment);
        if (result > 0) {
            Comment comment = commentMapper.selectById(commentId);
            return comment != null ? comment.getLikeCount() : 0;
        }
        
        return 0;
    }

    @Override
    @Transactional
    public int increaseReplyCount(Long commentId, Integer increment) {
        if (commentId == null || increment == null) {
            return 0;
        }
        
        int result = commentMapper.increaseReplyCount(commentId, increment);
        if (result > 0) {
            Comment comment = commentMapper.selectById(commentId);
            return comment != null ? comment.getReplyCount() : 0;
        }
        
        return 0;
    }

    @Override
    public long countTargetComments(Long targetId, String commentType, String status) {
        return commentMapper.countTargetComments(targetId, commentType, 
            status != null ? status : "NORMAL");
    }

    @Override
    public long countUserComments(Long userId, String commentType, String status) {
        return commentMapper.countUserComments(userId, commentType, 
            status != null ? status : "NORMAL");
    }

    @Override
    public Map<String, Object> getCommentStatistics(Long commentId) {
        return commentMapper.selectCommentStatistics(commentId);
    }

    @Override
    @Transactional
    public int updateUserInfo(Long userId, String nickname, String avatar) {
        log.info("更新用户信息冗余字段，用户：{}，昵称：{}", userId, nickname);
        
        return commentMapper.updateUserInfo(userId, nickname, avatar);
    }

    @Override
    public IPage<Comment> searchComments(String keyword, String commentType, Long targetId,
                                       Integer pageNum, Integer pageSize, String orderBy, String orderDirection) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        return commentMapper.searchComments(page, keyword, commentType, targetId, 
            "NORMAL", orderBy, orderDirection);
    }

    @Override
    public IPage<Comment> getPopularComments(Long targetId, String commentType, Integer timeRange,
                                           Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        return commentMapper.selectPopularComments(page, targetId, commentType, 
            timeRange, 1, "NORMAL");
    }

    @Override
    public IPage<Comment> getLatestComments(Long targetId, String commentType,
                                          Integer pageNum, Integer pageSize) {
        Page<Comment> page = new Page<>(pageNum, pageSize);
        
        return commentMapper.selectLatestComments(page, targetId, commentType, "NORMAL");
    }

    @Override
    @Transactional
    public int batchDeleteTargetComments(Long targetId, String commentType, Long operatorId) {
        log.info("批量删除目标对象评论，目标：{}，类型：{}", targetId, commentType);
        
        return commentMapper.batchDeleteTargetComments(targetId, commentType, "DELETED");
    }

    @Override
    public List<Comment> buildCommentTree(List<Comment> comments, Integer maxDepth) {
        if (comments == null || comments.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 按parent_comment_id分组
        Map<Long, List<Comment>> commentMap = comments.stream()
            .collect(Collectors.groupingBy(Comment::getParentCommentId));
        
        // 获取根评论（parent_comment_id = 0）
        List<Comment> rootComments = commentMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建树形结构
        for (Comment root : rootComments) {
            buildCommentChildren(root, commentMap, maxDepth, 1);
        }
        
        return rootComments;
    }

    @Override
    public boolean validateCommentPermission(Long commentId, Long userId, String operation) {
        Comment comment = getCommentById(commentId, true);
        if (comment == null) {
            return false;
        }
        
        // 自己的评论可以编辑和删除
        if ("edit".equals(operation) || "delete".equals(operation)) {
            return comment.getUserId().equals(userId);
        }
        
        // 管理员操作（简化处理，实际应该结合权限系统）
        if ("hide".equals(operation) || "restore".equals(operation) || "status".equals(operation)) {
            return true; // 这里应该检查管理员权限
        }
        
        return false;
    }

    @Override
    @Transactional
    public int cleanDeletedComments(Integer beforeDays) {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(beforeDays);
        
        log.info("清理已删除评论，保留天数：{}，截止时间：{}", beforeDays, beforeTime);
        
        return commentMapper.cleanDeletedComments(beforeTime);
    }

    // =================== 私有方法 ===================

    /**
     * 验证评论创建参数
     */
    private void validateCommentForCreate(Comment comment) {
        if (comment.getCommentType() == null || comment.getCommentType().trim().isEmpty()) {
            throw new RuntimeException("评论类型不能为空");
        }
        
        if (comment.getTargetId() == null) {
            throw new RuntimeException("目标对象ID不能为空");
        }
        
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }
        
        if (comment.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        // 验证评论类型
        if (!"CONTENT".equals(comment.getCommentType()) && !"DYNAMIC".equals(comment.getCommentType())) {
            throw new RuntimeException("不支持的评论类型：" + comment.getCommentType());
        }
        
        // 如果是回复评论，验证父评论是否存在
        if (comment.isReplyComment()) {
            Comment parentComment = getCommentById(comment.getParentCommentId(), false);
            if (parentComment == null) {
                throw new RuntimeException("父评论不存在或已删除");
            }
        }
    }

    /**
     * 递归构建评论子节点
     */
    private void buildCommentChildren(Comment parent, Map<Long, List<Comment>> commentMap, 
                                    Integer maxDepth, int currentDepth) {
        if (maxDepth != null && currentDepth >= maxDepth) {
            return;
        }
        
        List<Comment> children = commentMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            parent.initChildren();
            for (Comment child : children) {
                parent.addChild(child);
                buildCommentChildren(child, commentMap, maxDepth, currentDepth + 1);
            }
        }
    }
}