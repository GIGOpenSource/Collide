package com.gig.collide.social.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.social.domain.entity.*;

/**
 * 社交互动服务接口 - 点赞、收藏、分享、评论
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
public interface SocialInteractionService {

    // ========== 点赞相关 ==========
    
    /**
     * 点赞内容
     */
    boolean likeContent(Long userId, Long contentId);

    /**
     * 取消点赞
     */
    boolean unlikeContent(Long userId, Long contentId);

    /**
     * 检查是否已点赞
     */
    boolean isLiked(Long userId, Long contentId);

    /**
     * 获取内容的点赞用户列表
     */
    IPage<SocialLike> getContentLikes(Long contentId, int pageNum, int pageSize);

    // ========== 收藏相关 ==========
    
    /**
     * 收藏内容
     */
    boolean favoriteContent(Long userId, Long contentId, Long folderId);

    /**
     * 取消收藏
     */
    boolean unfavoriteContent(Long userId, Long contentId);

    /**
     * 检查是否已收藏
     */
    boolean isFavorited(Long userId, Long contentId);

    /**
     * 获取用户的收藏内容
     */
    IPage<SocialFavorite> getUserFavorites(Long userId, int pageNum, int pageSize);

    // ========== 分享相关 ==========
    
    /**
     * 分享内容
     */
    boolean shareContent(Long userId, Long contentId, Integer shareType, String sharePlatform, String shareComment);

    /**
     * 检查是否已分享
     */
    boolean isShared(Long userId, Long contentId);

    /**
     * 获取内容的分享记录
     */
    IPage<SocialShare> getContentShares(Long contentId, int pageNum, int pageSize);

    // ========== 评论相关 ==========
    
    /**
     * 创建评论
     */
    Long createComment(SocialComment comment);

    /**
     * 删除评论
     */
    boolean deleteComment(Long commentId, Long userId);

    /**
     * 获取内容的评论列表
     */
    IPage<SocialComment> getContentComments(Long contentId, int pageNum, int pageSize);

    /**
     * 获取评论的回复列表
     */
    IPage<SocialComment> getCommentReplies(Long parentCommentId, int pageNum, int pageSize);

    // ========== 批量操作 ==========
    
    /**
     * 批量检查用户对内容的互动状态
     */
    UserInteractionStatus getUserInteractionStatus(Long userId, Long contentId);

    /**
     * 用户互动状态
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    class UserInteractionStatus {
        private boolean liked;
        private boolean favorited;
        private boolean commented;
        private boolean shared;
    }
}