package com.gig.collide.api.social.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 内容创建请求
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class ContentCreateRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "内容类型不能为空")
    private Integer contentType; // 1-短视频,2-长视频,3-图片,4-文字

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    private String mediaUrls; // JSON格式的媒体地址

    private String coverUrl;

    private Integer duration; // 视频时长(秒)

    private String mediaInfo; // JSON格式的媒体信息

    // 付费相关
    private Integer isPaid; // 0-免费,1-付费

    private Integer price; // 价格(金币)

    private Integer freeDuration; // 免费试看时长(秒)

    private Integer privacy; // 1-公开,2-仅关注者,3-私密
}