package com.gig.collide.api.social;

import com.gig.collide.api.social.request.InteractionQueryRequest;
import com.gig.collide.api.social.request.InteractionRequest;
import com.gig.collide.api.social.vo.InteractionVO;
import com.gig.collide.api.social.vo.UserInteractionStatusVO;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 社交互动门面服务接口
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
public interface SocialInteractionFacadeService {

    // ========== 点赞相关 ==========

    /**
     * 点赞内容
     */
    Result<Boolean> likeContent(InteractionRequest request);

    /**
     * 取消点赞
     */
    Result<Boolean> unlikeContent(Long userId, Long contentId);

    /**
     * 检查是否已点赞
     */
    Result<Boolean> checkLiked(Long userId, Long contentId);

    /**
     * 获取内容的点赞用户列表
     */
    PageResponse<InteractionVO> getContentLikes(InteractionQueryRequest request);

    // ========== 收藏相关 ==========

    /**
     * 收藏内容
     */
    Result<Boolean> favoriteContent(InteractionRequest request);

    /**
     * 取消收藏
     */
    Result<Boolean> unfavoriteContent(Long userId, Long contentId);

    /**
     * 检查是否已收藏
     */
    Result<Boolean> checkFavorited(Long userId, Long contentId);

    /**
     * 获取用户的收藏列表
     */
    PageResponse<InteractionVO> getUserFavorites(InteractionQueryRequest request);

    // ========== 分享相关 ==========

    /**
     * 分享内容
     */
    Result<Boolean> shareContent(InteractionRequest request);

    /**
     * 获取内容的分享记录
     */
    PageResponse<InteractionVO> getContentShares(InteractionQueryRequest request);

    // ========== 评论相关 ==========

    /**
     * 创建评论
     */  
    Result<Long> createComment(InteractionRequest request);

    /**
     * 删除评论
     */
    Result<Boolean> deleteComment(Long commentId, Long userId);

    /**
     * 获取内容的评论列表
     */
    PageResponse<InteractionVO> getContentComments(InteractionQueryRequest request);

    /**
     * 获取评论的回复列表
     */
    PageResponse<InteractionVO> getCommentReplies(InteractionQueryRequest request);

    // ========== 批量操作 ==========

    /**
     * 获取用户对内容的互动状态
     */
    Result<UserInteractionStatusVO> getUserInteractionStatus(Long userId, Long contentId);

    /**
     * 批量获取用户对多个内容的互动状态
     */
    Result<java.util.Map<Long, UserInteractionStatusVO>> getBatchUserInteractionStatus(Long userId, java.util.List<Long> contentIds);
}