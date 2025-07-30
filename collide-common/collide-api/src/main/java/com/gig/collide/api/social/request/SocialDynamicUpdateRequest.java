package com.gig.collide.api.social.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * 社交动态更新请求 - 简洁版
 * 基于t_social_dynamic表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class SocialDynamicUpdateRequest implements Serializable {

    @NotNull(message = "动态ID不能为空")
    private Long id;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Size(max = 5000, message = "动态内容长度不能超过5000字符")
    private String content;

    @Pattern(regexp = "^(text|image|video|share)$", message = "动态类型只能是text、image、video或share")
    private String dynamicType;

    /**
     * 图片列表，JSON格式
     */
    private String images;

    /**
     * 视频URL
     */
    @Size(max = 500, message = "视频URL长度不能超过500字符")
    private String videoUrl;

    @Pattern(regexp = "^(normal|hidden|deleted)$", message = "状态只能是normal、hidden或deleted")
    private String status;
} 