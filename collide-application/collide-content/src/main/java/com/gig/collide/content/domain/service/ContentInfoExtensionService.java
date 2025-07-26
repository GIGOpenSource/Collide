package com.gig.collide.content.domain.service;

import com.gig.collide.api.content.response.data.ContentInfo;
import com.gig.collide.content.domain.entity.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 内容信息扩展服务
 * 用于为ContentInfo添加作者信息、分类名称等扩展信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentInfoExtensionService {

    private final ContentInteractionService contentInteractionService;

    /**
     * 为ContentInfo设置扩展信息
     *
     * @param contentInfo 内容DTO
     * @param content     原始内容实体
     * @param currentUserId 当前用户ID（可选，用于设置个性化信息）
     */
    public void setExtensionInfo(ContentInfo contentInfo, Content content, Long currentUserId) {
        if (contentInfo == null || content == null) {
            return;
        }

        try {
            // 设置去连表化设计的冗余字段
            setRedundantFields(contentInfo, content);
            
            // 设置用户个性化信息（点赞、收藏状态）
            if (currentUserId != null) {
                setUserPersonalInfo(contentInfo, content.getId(), currentUserId);
            }

        } catch (Exception e) {
            log.warn("设置内容扩展信息失败，内容ID：{}", content.getId(), e);
        }
    }

    /**
     * 批量设置扩展信息
     *
     * @param contentInfoList 内容DTO列表
     * @param contents        原始内容实体列表
     * @param currentUserId   当前用户ID
     */
    public void batchSetExtensionInfo(List<ContentInfo> contentInfoList, List<Content> contents, Long currentUserId) {
        if (contentInfoList == null || contents == null || contentInfoList.size() != contents.size()) {
            return;
        }

        for (int i = 0; i < contentInfoList.size(); i++) {
            setExtensionInfo(contentInfoList.get(i), contents.get(i), currentUserId);
        }
    }

    /**
     * 设置去连表化设计的冗余字段
     *
     * @param contentInfo 内容DTO
     * @param content     内容实体
     */
    private void setRedundantFields(ContentInfo contentInfo, Content content) {
        // TODO: 待ContentInfo类的新字段setter方法生效后启用
        // 设置作者冗余信息
        // contentInfo.setAuthorNickname(content.getAuthorNickname());
        // contentInfo.setAuthorAvatar(content.getAuthorAvatar());
        
        // 设置推荐相关字段
        // contentInfo.setPinned(content.getIsPinned());
        // contentInfo.setWeightScore(content.getWeightScore());
        
        log.debug("冗余字段设置暂时跳过，作者：{}，分类：{}", 
            content.getAuthorNickname(), content.getCategoryName());
    }

    /**
     * 设置用户个性化信息
     *
     * @param contentInfo 内容DTO
     * @param contentId   内容ID
     * @param userId      用户ID
     */
    private void setUserPersonalInfo(ContentInfo contentInfo, Long contentId, Long userId) {
        try {
            // 设置用户是否已点赞
            boolean liked = contentInteractionService.isContentLikedByUser(contentId, userId);
            contentInfo.setLiked(liked);

            // 设置用户是否已收藏
            boolean favorited = contentInteractionService.isContentFavoritedByUser(contentId, userId);
            contentInfo.setFavorited(favorited);

        } catch (Exception e) {
            log.warn("设置用户个性化信息失败，内容ID：{}，用户ID：{}", contentId, userId, e);
        }
    }
} 