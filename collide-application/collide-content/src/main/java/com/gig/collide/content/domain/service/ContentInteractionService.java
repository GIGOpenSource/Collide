package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.Content;
import com.gig.collide.content.domain.entity.ContentLike;
import com.gig.collide.content.domain.entity.ContentFavorite;
import com.gig.collide.content.domain.entity.ContentShare;
import com.gig.collide.content.infrastructure.mapper.ContentMapper;
import com.gig.collide.content.infrastructure.mapper.ContentLikeMapper;
import com.gig.collide.content.infrastructure.mapper.ContentFavoriteMapper;
import com.gig.collide.content.infrastructure.mapper.ContentShareMapper;
import com.gig.collide.base.exception.BizException;
import com.gig.collide.base.exception.CommonErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 内容交互服务
 * 专门处理点赞、收藏、分享等交互操作
 * 确保操作的幂等性和数据一致性
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentInteractionService {

    private final ContentMapper contentMapper;
    private final ContentLikeMapper contentLikeMapper;
    private final ContentFavoriteMapper contentFavoriteMapper;
    private final ContentShareMapper contentShareMapper;

    /**
     * 点赞内容（幂等性操作）
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean likeContent(Long contentId, Long userId) {
        log.info("用户点赞内容，用户ID: {}, 内容ID: {}", userId, contentId);
        
        // 验证内容是否存在
        Content content = contentMapper.selectById(contentId);
        if (content == null || content.getDeleted() == 1) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 检查是否已经点赞（幂等性检查）
        boolean alreadyLiked = contentLikeMapper.existsByContentIdAndUserId(contentId, userId);
        if (alreadyLiked) {
            log.info("用户已经点赞过该内容，用户ID: {}, 内容ID: {}", userId, contentId);
            return true; // 已经点赞过了，返回成功
        }

        try {
            // 记录点赞关系
            ContentLike contentLike = new ContentLike(contentId, userId);
            int insertResult = contentLikeMapper.insert(contentLike);
            
            if (insertResult > 0) {
                // 增加内容点赞数
                int updateResult = contentMapper.incrementLikeCount(contentId);
                if (updateResult > 0) {
                    log.info("用户点赞成功，用户ID: {}, 内容ID: {}", userId, contentId);
                    return true;
                } else {
                    throw new BizException("更新点赞数失败", CommonErrorCode.OPERATION_FAILED);
                }
            } else {
                throw new BizException("记录点赞失败", CommonErrorCode.OPERATION_FAILED);
            }
        } catch (Exception e) {
            log.error("点赞操作失败，用户ID: {}, 内容ID: {}", userId, contentId, e);
            throw new BizException("点赞操作失败", CommonErrorCode.OPERATION_FAILED);
        }
    }

    /**
     * 取消点赞内容（幂等性操作）
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unlikeContent(Long contentId, Long userId) {
        log.info("用户取消点赞，用户ID: {}, 内容ID: {}", userId, contentId);

        // 检查是否已经点赞
        boolean alreadyLiked = contentLikeMapper.existsByContentIdAndUserId(contentId, userId);
        if (!alreadyLiked) {
            log.info("用户未点赞该内容，无需取消，用户ID: {}, 内容ID: {}", userId, contentId);
            return true; // 没有点赞过，返回成功
        }

        try {
            // 删除点赞记录（逻辑删除）
            int deleteResult = contentLikeMapper.deleteByContentIdAndUserId(contentId, userId);
            
            if (deleteResult > 0) {
                // 减少内容点赞数
                int updateResult = contentMapper.decrementLikeCount(contentId);
                if (updateResult > 0) {
                    log.info("用户取消点赞成功，用户ID: {}, 内容ID: {}", userId, contentId);
                    return true;
                } else {
                    throw new BizException("更新点赞数失败", CommonErrorCode.OPERATION_FAILED);
                }
            } else {
                throw new BizException("删除点赞记录失败", CommonErrorCode.OPERATION_FAILED);
            }
        } catch (Exception e) {
            log.error("取消点赞操作失败，用户ID: {}, 内容ID: {}", userId, contentId, e);
            throw new BizException("取消点赞操作失败", CommonErrorCode.OPERATION_FAILED);
        }
    }

    /**
     * 收藏内容（幂等性操作）
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean favoriteContent(Long contentId, Long userId) {
        log.info("用户收藏内容，用户ID: {}, 内容ID: {}", userId, contentId);
        
        // 验证内容是否存在
        Content content = contentMapper.selectById(contentId);
        if (content == null || content.getDeleted() == 1) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 检查是否已经收藏（幂等性检查）
        boolean alreadyFavorited = contentFavoriteMapper.existsByContentIdAndUserId(contentId, userId);
        if (alreadyFavorited) {
            log.info("用户已经收藏过该内容，用户ID: {}, 内容ID: {}", userId, contentId);
            return true; // 已经收藏过了，返回成功
        }

        try {
            // 记录收藏关系
            ContentFavorite contentFavorite = new ContentFavorite(contentId, userId);
            int insertResult = contentFavoriteMapper.insert(contentFavorite);
            
            if (insertResult > 0) {
                // 增加内容收藏数
                int updateResult = contentMapper.incrementFavoriteCount(contentId);
                if (updateResult > 0) {
                    log.info("用户收藏成功，用户ID: {}, 内容ID: {}", userId, contentId);
                    return true;
                } else {
                    throw new BizException("更新收藏数失败", CommonErrorCode.OPERATION_FAILED);
                }
            } else {
                throw new BizException("记录收藏失败", CommonErrorCode.OPERATION_FAILED);
            }
        } catch (Exception e) {
            log.error("收藏操作失败，用户ID: {}, 内容ID: {}", userId, contentId, e);
            throw new BizException("收藏操作失败", CommonErrorCode.OPERATION_FAILED);
        }
    }

    /**
     * 取消收藏内容（幂等性操作）
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean unfavoriteContent(Long contentId, Long userId) {
        log.info("用户取消收藏，用户ID: {}, 内容ID: {}", userId, contentId);

        // 检查是否已经收藏
        boolean alreadyFavorited = contentFavoriteMapper.existsByContentIdAndUserId(contentId, userId);
        if (!alreadyFavorited) {
            log.info("用户未收藏该内容，无需取消，用户ID: {}, 内容ID: {}", userId, contentId);
            return true; // 没有收藏过，返回成功
        }

        try {
            // 删除收藏记录（逻辑删除）
            int deleteResult = contentFavoriteMapper.deleteByContentIdAndUserId(contentId, userId);
            
            if (deleteResult > 0) {
                // 减少内容收藏数
                int updateResult = contentMapper.decrementFavoriteCount(contentId);
                if (updateResult > 0) {
                    log.info("用户取消收藏成功，用户ID: {}, 内容ID: {}", userId, contentId);
                    return true;
                } else {
                    throw new BizException("更新收藏数失败", CommonErrorCode.OPERATION_FAILED);
                }
            } else {
                throw new BizException("删除收藏记录失败", CommonErrorCode.OPERATION_FAILED);
            }
        } catch (Exception e) {
            log.error("取消收藏操作失败，用户ID: {}, 内容ID: {}", userId, contentId, e);
            throw new BizException("取消收藏操作失败", CommonErrorCode.OPERATION_FAILED);
        }
    }

    /**
     * 分享内容（每次分享都记录）
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @param platform  分享平台
     * @param shareText 分享文案
     * @return 是否操作成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean shareContent(Long contentId, Long userId, String platform, String shareText) {
        log.info("用户分享内容，用户ID: {}, 内容ID: {}, 平台: {}", userId, contentId, platform);
        
        // 验证内容是否存在
        Content content = contentMapper.selectById(contentId);
        if (content == null || content.getDeleted() == 1) {
            throw new BizException("内容不存在", CommonErrorCode.RESOURCE_NOT_FOUND);
        }

        // 检查内容是否允许分享
        if (content.getAllowShare() == null || !content.getAllowShare()) {
            throw new BizException("该内容不允许分享", CommonErrorCode.OPERATION_NOT_ALLOWED);
        }

        try {
            // 记录分享记录（分享可以重复，每次都记录）
            ContentShare contentShare = new ContentShare(contentId, userId, platform, shareText);
            int insertResult = contentShareMapper.insert(contentShare);
            
            if (insertResult > 0) {
                // 增加内容分享数
                int updateResult = contentMapper.incrementShareCount(contentId);
                if (updateResult > 0) {
                    log.info("用户分享成功，用户ID: {}, 内容ID: {}, 平台: {}", userId, contentId, platform);
                    return true;
                } else {
                    throw new BizException("更新分享数失败", CommonErrorCode.OPERATION_FAILED);
                }
            } else {
                throw new BizException("记录分享失败", CommonErrorCode.OPERATION_FAILED);
            }
        } catch (Exception e) {
            log.error("分享操作失败，用户ID: {}, 内容ID: {}, 平台: {}", userId, contentId, platform, e);
            throw new BizException("分享操作失败", CommonErrorCode.OPERATION_FAILED);
        }
    }

    /**
     * 检查用户是否已点赞内容
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否已点赞
     */
    public boolean isContentLikedByUser(Long contentId, Long userId) {
        return contentLikeMapper.existsByContentIdAndUserId(contentId, userId);
    }

    /**
     * 检查用户是否已收藏内容
     *
     * @param contentId 内容ID
     * @param userId    用户ID
     * @return 是否已收藏
     */
    public boolean isContentFavoritedByUser(Long contentId, Long userId) {
        return contentFavoriteMapper.existsByContentIdAndUserId(contentId, userId);
    }
} 