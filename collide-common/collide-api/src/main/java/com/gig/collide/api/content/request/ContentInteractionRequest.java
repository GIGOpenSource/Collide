package com.gig.collide.api.content.request;

import com.gig.collide.api.content.enums.SharePlatformEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 内容交互请求
 * 用于处理点赞、收藏、分享等用户交互操作
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContentInteractionRequest extends BaseRequest {

    /**
     * 内容ID（必填）
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 分享平台（分享操作时必填）
     */
    private SharePlatformEnum sharePlatform;

    /**
     * 分享文案（分享操作时可选）
     */
    @Size(max = 500, message = "分享文案长度不能超过500个字符")
    private String shareText;

    /**
     * 操作来源（可选）
     * 如：web, mobile, app等
     */
    private String source;

    /**
     * 设备信息（可选）
     */
    private String deviceInfo;

    /**
     * IP地址（可选）
     */
    private String ipAddress;

    // ===================== 便捷构造器 =====================

    /**
     * 创建点赞请求
     */
    public static ContentInteractionRequest like(Long contentId, Long userId) {
        ContentInteractionRequest request = new ContentInteractionRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        return request;
    }

    /**
     * 创建收藏请求
     */
    public static ContentInteractionRequest favorite(Long contentId, Long userId) {
        ContentInteractionRequest request = new ContentInteractionRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        return request;
    }

    /**
     * 创建分享请求
     */
    public static ContentInteractionRequest share(Long contentId, Long userId, 
                                                 SharePlatformEnum platform, String shareText) {
        ContentInteractionRequest request = new ContentInteractionRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        request.setSharePlatform(platform);
        request.setShareText(shareText);
        return request;
    }

    /**
     * 创建浏览请求
     */
    public static ContentInteractionRequest view(Long contentId, Long userId) {
        ContentInteractionRequest request = new ContentInteractionRequest();
        request.setContentId(contentId);
        request.setUserId(userId);
        return request;
    }

    /**
     * 链式设置来源
     */
    public ContentInteractionRequest withSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * 链式设置设备信息
     */
    public ContentInteractionRequest withDevice(String deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    /**
     * 链式设置IP地址
     */
    public ContentInteractionRequest withIp(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }
} 