package com.gig.collide.api.social.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * 内容更新请求
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class ContentUpdateRequest {

    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String title;

    private String description;

    private String mediaUrls;

    private String coverUrl;

    private Integer duration;

    private String mediaInfo;

    // 付费相关
    private Integer isPaid;

    private Integer price;

    private Integer freeDuration;

    private Integer privacy;
}