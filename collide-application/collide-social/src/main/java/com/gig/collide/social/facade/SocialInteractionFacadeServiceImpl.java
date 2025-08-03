package com.gig.collide.social.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.social.SocialInteractionFacadeService;
import com.gig.collide.api.social.request.InteractionQueryRequest;
import com.gig.collide.api.social.request.InteractionRequest;
import com.gig.collide.api.social.vo.InteractionVO;
import com.gig.collide.api.social.vo.UserInteractionStatusVO;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.*;
import com.gig.collide.social.domain.service.SocialInteractionService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

// 用户服务相关导入
import com.gig.collide.api.user.UserProfileFacadeService;
import com.gig.collide.api.user.response.profile.UserProfileResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 社交互动门面服务实现
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SocialInteractionFacadeServiceImpl implements SocialInteractionFacadeService {

    private final SocialInteractionService interactionService;
    
    @DubboReference(version = "1.0.0")
    private UserProfileFacadeService userProfileFacadeService;

    // ========== 点赞相关 ==========

    @Override
    public Result<Boolean> likeContent(InteractionRequest request) {
        try {
            boolean result = interactionService.likeContent(request.getUserId(), request.getContentId());
            if (result) {
                return Result.success("点赞成功", true);
            }
            return Result.success("已经点赞过了", false);
        } catch (Exception e) {
            log.error("点赞失败", e);
            return Result.failure("LIKE_ERROR", "点赞失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> unlikeContent(Long userId, Long contentId) {
        try {
            boolean result = interactionService.unlikeContent(userId, contentId);
            if (result) {
                return Result.success("取消点赞成功", true);
            }
            return Result.success("还未点赞", false);
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.failure("UNLIKE_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkLiked(Long userId, Long contentId) {
        try {
            boolean isLiked = interactionService.isLiked(userId, contentId);
            return Result.success(isLiked);
        } catch (Exception e) {
            log.error("检查点赞状态失败", e);
            return Result.failure("CHECK_LIKE_ERROR", "检查点赞状态失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<InteractionVO> getContentLikes(InteractionQueryRequest request) {
        try {
            IPage<SocialLike> page = interactionService.getContentLikes(
                request.getContentId(), request.getCurrentPage(), request.getPageSize());
            
            List<InteractionVO> interactionVOs = convertLikeToVOsBatch(page.getRecords());

            return PageResponse.of(interactionVOs, page.getTotal(), request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("获取点赞列表失败", e);
            return PageResponse.fail("GET_LIKES_ERROR", "获取点赞列表失败: " + e.getMessage());
        }
    }

    // ========== 收藏相关 ==========

    @Override
    public Result<Boolean> favoriteContent(InteractionRequest request) {
        try {
            boolean result = interactionService.favoriteContent(
                request.getUserId(), request.getContentId(), request.getFolderId());
            if (result) {
                return Result.success("收藏成功", true);
            }
            return Result.success("已经收藏过了", false);
        } catch (Exception e) {
            log.error("收藏失败", e);
            return Result.failure("FAVORITE_ERROR", "收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> unfavoriteContent(Long userId, Long contentId) {
        try {
            boolean result = interactionService.unfavoriteContent(userId, contentId);
            if (result) {
                return Result.success("取消收藏成功", true);
            }
            return Result.success("还未收藏", false);
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return Result.failure("UNFAVORITE_ERROR", "取消收藏失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkFavorited(Long userId, Long contentId) {
        try {
            boolean isFavorited = interactionService.isFavorited(userId, contentId);
            return Result.success(isFavorited);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return Result.failure("CHECK_FAVORITE_ERROR", "检查收藏状态失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<InteractionVO> getUserFavorites(InteractionQueryRequest request) {
        try {
            IPage<SocialFavorite> page = interactionService.getUserFavorites(
                request.getUserId(), request.getCurrentPage(), request.getPageSize());
            
            List<InteractionVO> interactionVOs = convertFavoriteToVOsBatch(page.getRecords());

            return PageResponse.of(interactionVOs, page.getTotal(), request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("获取收藏列表失败", e);
            return PageResponse.fail("GET_FAVORITES_ERROR", "获取收藏列表失败: " + e.getMessage());
        }
    }

    // ========== 分享相关 ==========

    @Override
    public Result<Boolean> shareContent(InteractionRequest request) {
        try {
            boolean result = interactionService.shareContent(
                request.getUserId(), request.getContentId(), request.getShareType(),
                request.getSharePlatform(), request.getShareComment());
            if (result) {
                return Result.success("分享成功", true);
            }
            return Result.failure("分享失败");
        } catch (Exception e) {
            log.error("分享失败", e);
            return Result.failure("SHARE_ERROR", "分享失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<InteractionVO> getContentShares(InteractionQueryRequest request) {
        try {
            IPage<SocialShare> page = interactionService.getContentShares(
                request.getContentId(), request.getCurrentPage(), request.getPageSize());
            
            List<InteractionVO> interactionVOs = convertShareToVOsBatch(page.getRecords());

            return PageResponse.of(interactionVOs, page.getTotal(), request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("获取分享列表失败", e);
            return PageResponse.fail("GET_SHARES_ERROR", "获取分享列表失败: " + e.getMessage());
        }
    }

    // ========== 评论相关 ==========

    @Override
    public Result<Long> createComment(InteractionRequest request) {
        try {
            SocialComment comment = new SocialComment();
            comment.setUserId(request.getUserId());
            comment.setContentId(request.getContentId());
            comment.setParentCommentId(request.getParentCommentId());
            comment.setReplyToUserId(request.getReplyToUserId());
            comment.setCommentText(request.getCommentText());
            comment.setCommentImages(request.getCommentImages());

            Long commentId = interactionService.createComment(comment);
            if (commentId != null) {
                return Result.success("评论成功", commentId);
            }
            return Result.failure("评论失败");
        } catch (Exception e) {
            log.error("创建评论失败", e);
            return Result.failure("COMMENT_ERROR", "评论失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteComment(Long commentId, Long userId) {
        try {
            boolean result = interactionService.deleteComment(commentId, userId);
            if (result) {
                return Result.success("删除评论成功", true);
            }
            return Result.failure("删除评论失败");
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.failure("DELETE_COMMENT_ERROR", "删除评论失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<InteractionVO> getContentComments(InteractionQueryRequest request) {
        try {
            IPage<SocialComment> page = interactionService.getContentComments(
                request.getContentId(), request.getCurrentPage(), request.getPageSize());
            
            List<InteractionVO> interactionVOs = convertCommentToVOsBatch(page.getRecords());

            return PageResponse.of(interactionVOs, page.getTotal(), request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("获取评论列表失败", e);
            return PageResponse.fail("GET_COMMENTS_ERROR", "获取评论列表失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<InteractionVO> getCommentReplies(InteractionQueryRequest request) {
        try {
            IPage<SocialComment> page = interactionService.getCommentReplies(
                request.getParentCommentId(), request.getCurrentPage(), request.getPageSize());
            
            List<InteractionVO> interactionVOs = convertCommentToVOsBatch(page.getRecords());

            return PageResponse.of(interactionVOs, page.getTotal(), request.getPageSize(), request.getCurrentPage());
        } catch (Exception e) {
            log.error("获取评论回复失败", e);
            return PageResponse.fail("GET_REPLIES_ERROR", "获取评论回复失败: " + e.getMessage());
        }
    }

    // ========== 批量操作 ==========

    @Override
    public Result<UserInteractionStatusVO> getUserInteractionStatus(Long userId, Long contentId) {
        try {
            SocialInteractionService.UserInteractionStatus status = 
                interactionService.getUserInteractionStatus(userId, contentId);
            
            UserInteractionStatusVO statusVO = new UserInteractionStatusVO();
            statusVO.setUserId(userId);
            statusVO.setContentId(contentId);
            statusVO.setLiked(status.isLiked());
            statusVO.setFavorited(status.isFavorited());
            statusVO.setCommented(status.isCommented());
            statusVO.setShared(status.isShared());

            return Result.success(statusVO);
        } catch (Exception e) {
            log.error("获取用户互动状态失败", e);
            return Result.failure("GET_STATUS_ERROR", "获取用户互动状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, UserInteractionStatusVO>> getBatchUserInteractionStatus(Long userId, List<Long> contentIds) {
        try {
            Map<Long, UserInteractionStatusVO> resultMap = new HashMap<>();
            
            // 批量获取互动状态的优化逻辑
            for (Long contentId : contentIds) {
                SocialInteractionService.UserInteractionStatus status = 
                    interactionService.getUserInteractionStatus(userId, contentId);
                
                UserInteractionStatusVO statusVO = new UserInteractionStatusVO();
                statusVO.setUserId(userId);
                statusVO.setContentId(contentId);
                statusVO.setLiked(status.isLiked());
                statusVO.setFavorited(status.isFavorited());
                statusVO.setCommented(status.isCommented());
                statusVO.setShared(status.isShared());
                
                resultMap.put(contentId, statusVO);
            }

            return Result.success(resultMap);
        } catch (Exception e) {
            log.error("批量获取用户互动状态失败", e);
            return Result.failure("BATCH_STATUS_ERROR", "批量获取用户互动状态失败: " + e.getMessage());
        }
    }

    // ========== 转换方法 ==========
    
    /**
     * 获取用户基本信息
     */
    private void fillUserInfo(InteractionVO vo, Long userId) {
        try {
            Result<UserProfileResponse> userResult = userProfileFacadeService.getProfileByUserId(userId);
            if (userResult.getSuccess() && userResult.getData() != null) {
                UserProfileResponse profile = userResult.getData();
                vo.setUserName(profile.getNickname());
                vo.setUserAvatar(profile.getAvatar());
            } else {
                vo.setUserName("未知用户");
                vo.setUserAvatar(null);
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败, userId: {}", userId, e);
            vo.setUserName("未知用户");
            vo.setUserAvatar(null);
        }
    }

    private InteractionVO convertLikeToVO(SocialLike like) {
        InteractionVO vo = new InteractionVO();
        BeanUtils.copyProperties(like, vo);
        vo.setLikeType(like.getLikeType());
        
        // 获取用户信息
        fillUserInfo(vo, like.getUserId());
        
        return vo;
    }

    private InteractionVO convertFavoriteToVO(SocialFavorite favorite) {
        InteractionVO vo = new InteractionVO();
        BeanUtils.copyProperties(favorite, vo);
        vo.setFolderName(favorite.getFolderName());
        
        // 获取用户信息
        fillUserInfo(vo, favorite.getUserId());
        
        return vo;
    }

    private InteractionVO convertShareToVO(SocialShare share) {
        InteractionVO vo = new InteractionVO();
        BeanUtils.copyProperties(share, vo);
        vo.setShareType(share.getShareType());
        vo.setSharePlatform(share.getSharePlatform());
        vo.setShareComment(share.getShareComment());
        
        // 获取用户信息
        fillUserInfo(vo, share.getUserId());
        
        return vo;
    }

    private InteractionVO convertCommentToVO(SocialComment comment) {
        InteractionVO vo = new InteractionVO();
        BeanUtils.copyProperties(comment, vo);
        vo.setParentCommentId(comment.getParentCommentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setCommentText(comment.getCommentText());
        vo.setCommentImages(comment.getCommentImages());
        vo.setLikeCount(comment.getLikeCount());
        vo.setReplyCount(comment.getReplyCount());
        vo.setStatus(comment.getStatus());
        vo.setUpdateTime(comment.getUpdateTime());
        
        // 获取评论用户信息
        fillUserInfo(vo, comment.getUserId());
        
        // 获取被回复用户信息
        if (comment.getReplyToUserId() != null) {
            try {
                Result<UserProfileResponse> replyUserResult = userProfileFacadeService.getProfileByUserId(comment.getReplyToUserId());
                if (replyUserResult.getSuccess() && replyUserResult.getData() != null) {
                    vo.setReplyToUserName(replyUserResult.getData().getNickname());
                } else {
                    vo.setReplyToUserName("未知用户");
                }
            } catch (Exception e) {
                log.warn("获取回复用户信息失败, userId: {}", comment.getReplyToUserId(), e);
                vo.setReplyToUserName("未知用户");
            }
        }
        
        return vo;
    }
    
    /**
     * 批量转换点赞记录，优化用户信息查询
     */
    private List<InteractionVO> convertLikeToVOsBatch(List<SocialLike> likes) {
        return convertWithBatchUserInfo(
            likes,
            SocialLike::getUserId,
            (like, userInfo) -> {
                InteractionVO vo = new InteractionVO();
                BeanUtils.copyProperties(like, vo);
                vo.setLikeType(like.getLikeType());
                fillUserInfoFromMap(vo, like.getUserId(), userInfo);
                return vo;
            }
        );
    }
    
    /**
     * 批量转换收藏记录，优化用户信息查询
     */
    private List<InteractionVO> convertFavoriteToVOsBatch(List<SocialFavorite> favorites) {
        return convertWithBatchUserInfo(
            favorites,
            SocialFavorite::getUserId,
            (favorite, userInfo) -> {
                InteractionVO vo = new InteractionVO();
                BeanUtils.copyProperties(favorite, vo);
                vo.setFolderName(favorite.getFolderName());
                fillUserInfoFromMap(vo, favorite.getUserId(), userInfo);
                return vo;
            }
        );
    }
    
    /**
     * 批量转换分享记录，优化用户信息查询
     */
    private List<InteractionVO> convertShareToVOsBatch(List<SocialShare> shares) {
        return convertWithBatchUserInfo(
            shares,
            SocialShare::getUserId,
            (share, userInfo) -> {
                InteractionVO vo = new InteractionVO();
                BeanUtils.copyProperties(share, vo);
                vo.setShareType(share.getShareType());
                vo.setSharePlatform(share.getSharePlatform());
                vo.setShareComment(share.getShareComment());
                fillUserInfoFromMap(vo, share.getUserId(), userInfo);
                return vo;
            }
        );
    }
    
    /**
     * 批量转换评论记录，优化用户信息查询
     */
    private List<InteractionVO> convertCommentToVOsBatch(List<SocialComment> comments) {
        // 收集所有需要查询的用户ID（包括评论用户和被回复用户）
        List<Long> allUserIds = comments.stream()
            .flatMap(comment -> {
                List<Long> ids = List.of(comment.getUserId());
                if (comment.getReplyToUserId() != null) {
                    ids = List.of(comment.getUserId(), comment.getReplyToUserId());
                }
                return ids.stream();
            })
            .distinct()
            .collect(Collectors.toList());
        
        // 批量查询用户信息
        Map<Long, UserProfileResponse> userInfoMap = batchGetUserProfiles(allUserIds);
        
        return comments.stream()
            .map(comment -> {
                InteractionVO vo = new InteractionVO();
                BeanUtils.copyProperties(comment, vo);
                vo.setParentCommentId(comment.getParentCommentId());
                vo.setReplyToUserId(comment.getReplyToUserId());
                vo.setCommentText(comment.getCommentText());
                vo.setCommentImages(comment.getCommentImages());
                vo.setLikeCount(comment.getLikeCount());
                vo.setReplyCount(comment.getReplyCount());
                vo.setStatus(comment.getStatus());
                vo.setUpdateTime(comment.getUpdateTime());
                
                // 填充评论用户信息
                fillUserInfoFromMap(vo, comment.getUserId(), userInfoMap);
                
                // 填充被回复用户信息
                if (comment.getReplyToUserId() != null) {
                    UserProfileResponse replyUser = userInfoMap.get(comment.getReplyToUserId());
                    vo.setReplyToUserName(replyUser != null ? replyUser.getNickname() : "未知用户");
                }
                
                return vo;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 通用的批量用户信息转换方法
     */
    private <T> List<InteractionVO> convertWithBatchUserInfo(
            List<T> items,
            java.util.function.Function<T, Long> userIdExtractor,
            java.util.function.BiFunction<T, Map<Long, UserProfileResponse>, InteractionVO> converter) {
        
        // 收集所有需要查询的用户ID
        List<Long> userIds = items.stream()
            .map(userIdExtractor)
            .distinct()
            .collect(Collectors.toList());
            
        // 批量查询用户信息
        Map<Long, UserProfileResponse> userInfoMap = batchGetUserProfiles(userIds);
        
        // 转换VO
        return items.stream()
            .map(item -> converter.apply(item, userInfoMap))
            .collect(Collectors.toList());
    }
    
    /**
     * 批量获取用户资料信息
     */
    private Map<Long, UserProfileResponse> batchGetUserProfiles(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            Result<List<UserProfileResponse>> batchResult = userProfileFacadeService.batchGetProfiles(userIds);
            if (batchResult.getSuccess() && batchResult.getData() != null) {
                return batchResult.getData().stream()
                    .collect(Collectors.toMap(UserProfileResponse::getUserId, profile -> profile));
            }
        } catch (Exception e) {
            log.warn("批量获取用户信息失败", e);
        }
        
        return new HashMap<>();
    }
    
    /**
     * 从用户信息Map中填充VO的用户信息
     */
    private void fillUserInfoFromMap(InteractionVO vo, Long userId, Map<Long, UserProfileResponse> userInfoMap) {
        UserProfileResponse profile = userInfoMap.get(userId);
        if (profile != null) {
            vo.setUserName(profile.getNickname());
            vo.setUserAvatar(profile.getAvatar());
        } else {
            vo.setUserName("未知用户");
            vo.setUserAvatar(null);
        }
    }
}