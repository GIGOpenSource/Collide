package com.gig.collide.api.social.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 社交动态创建请求 - 简洁版
 * 基于t_social_dynamic表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class SocialDynamicCreateRequest {

    @NotBlank(message = "动态内容不能为空")
    @Size(max = 5000, message = "动态内容长度不能超过5000字符")
    private String content;

    @Pattern(regexp = "^(text|image|video|share)$", message = "动态类型只能是text、image、video或share")
    private String dynamicType = "text";

    /**
     * 图片列表，JSON格式
     */
    private String images;

    /**
     * 视频URL
     */
    @Size(max = 500, message = "视频URL长度不能超过500字符")
    private String videoUrl;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户昵称（冗余存储）
     */
    @Size(max = 100, message = "用户昵称长度不能超过100字符")
    private String userNickname;

    /**
     * 用户头像（冗余存储）
     */
    @Size(max = 500, message = "用户头像URL长度不能超过500字符")
    private String userAvatar;

    /**
     * 分享目标类型：content、goods
     */
    @Pattern(regexp = "^(content|goods)$", message = "分享目标类型只能是content或goods")
    private String shareTargetType;

    /**
     * 分享目标ID
     */
    private Long shareTargetId;

    /**
     * 分享目标标题（冗余存储）
     */
    @Size(max = 200, message = "分享目标标题长度不能超过200字符")
    private String shareTargetTitle;
} 