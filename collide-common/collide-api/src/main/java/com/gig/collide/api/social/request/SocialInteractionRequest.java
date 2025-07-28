package com.gig.collide.api.social.request;

import com.gig.collide.api.social.constant.InteractionTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 社交互动请求
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialInteractionRequest extends BaseRequest {
    
    /**
     * 动态ID
     */
    @NotNull(message = "动态ID不能为空")
    private Long postId;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 互动类型
     */
    @NotNull(message = "互动类型不能为空")
    private InteractionTypeEnum interactionType;
    
    /**
     * 互动内容（如转发评论）
     */
    @Size(max = 500, message = "互动内容不能超过500字符")
    private String interactionContent;
    
    /**
     * 设备信息
     */
    @Size(max = 200, message = "设备信息不能超过200字符")
    private String deviceInfo;
    
    /**
     * IP地址
     */
    @Size(max = 45, message = "IP地址不能超过45字符")
    private String ipAddress;
    
    /**
     * 构造点赞请求
     */
    public static SocialInteractionRequest createLike(Long postId, Long userId) {
        return new SocialInteractionRequest(postId, userId, InteractionTypeEnum.LIKE, null, null, null);
    }
    
    /**
     * 构造收藏请求
     */
    public static SocialInteractionRequest createFavorite(Long postId, Long userId) {
        return new SocialInteractionRequest(postId, userId, InteractionTypeEnum.FAVORITE, null, null, null);
    }
    
    /**
     * 构造转发请求
     */
    public static SocialInteractionRequest createShare(Long postId, Long userId, String shareComment) {
        return new SocialInteractionRequest(postId, userId, InteractionTypeEnum.SHARE, shareComment, null, null);
    }
    
    /**
     * 构造浏览请求
     */
    public static SocialInteractionRequest createView(Long postId, Long userId) {
        return new SocialInteractionRequest(postId, userId, InteractionTypeEnum.VIEW, null, null, null);
    }
    
    /**
     * 构造带设备信息的互动请求
     */
    public static SocialInteractionRequest withDevice(Long postId, Long userId, InteractionTypeEnum interactionType, 
                                                     String deviceInfo, String ipAddress) {
        return new SocialInteractionRequest(postId, userId, interactionType, null, deviceInfo, ipAddress);
    }
} 