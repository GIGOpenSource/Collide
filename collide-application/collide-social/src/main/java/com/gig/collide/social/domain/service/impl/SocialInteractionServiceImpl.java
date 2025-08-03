package com.gig.collide.social.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.social.domain.entity.*;
import com.gig.collide.social.domain.service.InteractionStatsService;
import com.gig.collide.social.domain.service.SocialInteractionService;
import com.gig.collide.social.infrastructure.mapper.SocialCommentMapper;
import com.gig.collide.social.infrastructure.mapper.SocialContentMapper;
import com.gig.collide.social.infrastructure.mapper.SocialInteractionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 社交互动服务实现
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialInteractionServiceImpl implements SocialInteractionService {

    private final SocialInteractionMapper interactionMapper;
    private final SocialCommentMapper commentMapper;
    private final SocialContentMapper contentMapper;
    private final InteractionStatsService statsService;

    // ========== 点赞相关 ==========

    @Override
    @Transactional
    public boolean likeContent(Long userId, Long contentId) {
        // 检查是否已点赞
        if (isLiked(userId, contentId)) {
            return false;
        }

        // 获取内容作者ID
        SocialContent content = contentMapper.selectById(contentId);
        if (content == null) {
            return false;
        }

        // 插入点赞记录
        int inserted = interactionMapper.insertLike(userId, contentId, content.getUserId(), 
                                                   SocialLike.LikeType.NORMAL.getCode());
        if (inserted > 0) {
            // 更新统计
            statsService.incrementLikeCount(contentId);
            log.info("用户[{}]点赞内容[{}]成功", userId, contentId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unlikeContent(Long userId, Long contentId) {
        int deleted = interactionMapper.deleteLike(userId, contentId);
        if (deleted > 0) {
            // 更新统计
            statsService.decrementLikeCount(contentId);
            log.info("用户[{}]取消点赞内容[{}]成功", userId, contentId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLiked(Long userId, Long contentId) {
        return interactionMapper.countLike(userId, contentId) > 0;
    }

    @Override
    public IPage<SocialLike> getContentLikes(Long contentId, int pageNum, int pageSize) {
        Page<SocialLike> page = new Page<>(pageNum, pageSize);
        return interactionMapper.selectContentLikes(page, contentId);
    }

    // ========== 收藏相关 ==========

    @Override
    @Transactional
    public boolean favoriteContent(Long userId, Long contentId, Long folderId) {
        // 检查是否已收藏
        if (isFavorited(userId, contentId)) {
            return false;
        }

        // 获取内容作者ID
        SocialContent content = contentMapper.selectById(contentId);
        if (content == null) {
            return false;
        }

        // 设置默认收藏夹
        if (folderId == null) {
            folderId = SocialFavorite.DEFAULT_FOLDER_ID;
        }

        int inserted = interactionMapper.insertFavorite(userId, contentId, content.getUserId(), 
                                                       folderId, SocialFavorite.DEFAULT_FOLDER_NAME);
        if (inserted > 0) {
            // 更新统计
            statsService.incrementFavoriteCount(contentId);
            log.info("用户[{}]收藏内容[{}]成功", userId, contentId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unfavoriteContent(Long userId, Long contentId) {
        int deleted = interactionMapper.deleteFavorite(userId, contentId);
        if (deleted > 0) {
            // 更新统计
            statsService.decrementFavoriteCount(contentId);
            log.info("用户[{}]取消收藏内容[{}]成功", userId, contentId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFavorited(Long userId, Long contentId) {
        return interactionMapper.countFavorite(userId, contentId) > 0;
    }

    @Override
    public IPage<SocialFavorite> getUserFavorites(Long userId, int pageNum, int pageSize) {
        Page<SocialFavorite> page = new Page<>(pageNum, pageSize);
        return interactionMapper.selectUserFavorites(page, userId);
    }

    // ========== 分享相关 ==========

    @Override
    @Transactional
    public boolean shareContent(Long userId, Long contentId, Integer shareType, String sharePlatform, String shareComment) {
        // 获取内容作者ID
        SocialContent content = contentMapper.selectById(contentId);
        if (content == null) {
            return false;
        }

        int inserted = interactionMapper.insertShare(userId, contentId, content.getUserId(), 
                                                    shareType, sharePlatform, shareComment);
        if (inserted > 0) {
            // 更新统计
            statsService.incrementShareCount(contentId);
            log.info("用户[{}]分享内容[{}]成功, 类型:{}", userId, contentId, shareType);
            return true;
        }
        return false;
    }

    @Override
    public IPage<SocialShare> getContentShares(Long contentId, int pageNum, int pageSize) {
        Page<SocialShare> page = new Page<>(pageNum, pageSize);
        return interactionMapper.selectContentShares(page, contentId);
    }

    // ========== 评论相关 ==========

    @Override
    @Transactional
    public Long createComment(SocialComment comment) {
        // 设置默认值
        if (comment.getStatus() == null) {
            comment.setStatus(SocialComment.Status.NORMAL.getCode());
        }
        if (comment.getParentCommentId() == null) {
            comment.setParentCommentId(0L);
        }
        comment.setLikeCount(0);
        comment.setReplyCount(0);

        // 获取内容信息
        SocialContent content = contentMapper.selectById(comment.getContentId());
        if (content == null) {
            return null;
        }
        comment.setContentOwnerId(content.getUserId());

        int inserted = commentMapper.insert(comment);
        if (inserted > 0) {
            // 更新统计
            statsService.incrementCommentCount(comment.getContentId());
            
            // 如果是回复，更新父评论的回复数
            if (comment.getParentCommentId() > 0) {
                commentMapper.incrementReplyCount(comment.getParentCommentId());
            }
            
            log.info("用户[{}]评论内容[{}]成功", comment.getUserId(), comment.getContentId());
            return comment.getId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteComment(Long commentId, Long userId) {
        int updated = commentMapper.softDeleteComment(commentId, userId);
        if (updated > 0) {
            // 获取评论信息来更新统计
            SocialComment comment = commentMapper.selectById(commentId);
            if (comment != null) {
                // 更新内容评论数
                statsService.decrementCommentCount(comment.getContentId());
                
                // 如果是回复，更新父评论的回复数
                if (comment.getParentCommentId() > 0) {
                    commentMapper.decrementReplyCount(comment.getParentCommentId());
                }
            }
            
            log.info("用户[{}]删除评论[{}]成功", userId, commentId);
            return true;
        }
        return false;
    }

    @Override
    public IPage<SocialComment> getContentComments(Long contentId, int pageNum, int pageSize) {
        Page<SocialComment> page = new Page<>(pageNum, pageSize);
        return interactionMapper.selectContentComments(page, contentId);
    }

    @Override
    public IPage<SocialComment> getCommentReplies(Long parentCommentId, int pageNum, int pageSize) {
        Page<SocialComment> page = new Page<>(pageNum, pageSize);
        return interactionMapper.selectCommentReplies(page, parentCommentId);
    }

    // ========== 批量操作 ==========

    @Override
    public UserInteractionStatus getUserInteractionStatus(Long userId, Long contentId) {
        return interactionMapper.getUserInteractionStatus(userId, contentId);
    }
}