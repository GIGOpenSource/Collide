package com.gig.collide.api.social.response.data;

import com.gig.collide.api.social.constant.InteractionTypeEnum;
import com.gig.collide.api.social.constant.InteractionStatusEnum;
import com.gig.collide.api.social.constant.PostTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社交互动信息 DTO
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialInteractionInfo implements Serializable {
    
    /**
     * 互动记录ID
     */
    private Long id;
    
    /**
     * 动态ID
     */
    private Long postId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 互动类型
     */
    private InteractionTypeEnum interactionType;
    
    /**
     * 互动状态
     */
    private InteractionStatusEnum interactionStatus;
    
    /**
     * 用户昵称
     */
    private String userNickname;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 动态作者ID
     */
    private Long postAuthorId;
    
    /**
     * 动态类型
     */
    private PostTypeEnum postType;
    
    /**
     * 动态标题或前50字符
     */
    private String postTitle;
    
    /**
     * 互动内容（如转发评论）
     */
    private String interactionContent;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * IP地址
     */
    private String ipAddress;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 是否为有效互动
     */
    public boolean isActive() {
        return interactionStatus != null && interactionStatus.isActive();
    }
    
    /**
     * 是否为主动互动
     */
    public boolean isActiveInteraction() {
        return interactionType != null && interactionType.isActiveInteraction();
    }
    
    /**
     * 是否可撤销
     */
    public boolean canRevoke() {
        return interactionType != null && interactionType.isRevocable() && isActive();
    }
    
    /**
     * 获取互动描述
     */
    public String getInteractionDescription() {
        if (interactionType == null || interactionStatus == null) {
            return "未知互动";
        }
        return interactionStatus.getActionDescription(interactionType);
    }
    
    /**
     * 获取权重值
     */
    public double getWeight() {
        return interactionType != null ? interactionType.getWeight() : 0.0;
    }
    
    /**
     * 是否需要通知作者
     */
    public boolean shouldNotifyAuthor() {
        return interactionType != null && interactionType.shouldNotifyAuthor() && isActive();
    }
} 