package com.gig.collide.api.social.request;

import com.gig.collide.api.social.constant.SocialPostType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 创建社交动态请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "创建社交动态请求")
public class SocialPostCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 动态类型
     */
    @NotNull(message = "动态类型不能为空")
    @Schema(description = "动态类型", example = "TEXT")
    private SocialPostType postType;

    /**
     * 动态内容
     */
    @NotBlank(message = "动态内容不能为空")
    @Size(max = 2000, message = "动态内容不能超过2000字")
    @Schema(description = "动态内容", example = "今天天气真好！")
    private String content;

    /**
     * 媒体文件URL列表（图片、视频等）
     */
    @Schema(description = "媒体文件URL列表")
    private List<String> mediaUrls;

    /**
     * 位置信息
     */
    @Schema(description = "位置信息", example = "北京市朝阳区")
    private String location;

    /**
     * 位置坐标 - 经度
     */
    @Schema(description = "经度", example = "116.4074")
    private Double longitude;

    /**
     * 位置坐标 - 纬度
     */
    @Schema(description = "纬度", example = "39.9042")
    private Double latitude;

    /**
     * 话题标签列表
     */
    @Schema(description = "话题标签列表", example = "[\"春天\", \"旅行\", \"美食\"]")
    private List<String> topics;

    /**
     * 提及的用户ID列表
     */
    @Schema(description = "提及的用户ID列表")
    private List<Long> mentionedUserIds;

    /**
     * 是否允许评论
     */
    @Schema(description = "是否允许评论", example = "true")
    private Boolean allowComments = true;

    /**
     * 是否允许转发
     */
    @Schema(description = "是否允许转发", example = "true")
    private Boolean allowShares = true;

    /**
     * 可见性设置（0-公开，1-仅关注者，2-仅自己）
     */
    @Schema(description = "可见性设置", example = "0")
    private Integer visibility = 0;

    /**
     * 发布者用户ID（由系统从token中获取）
     */
    @Schema(description = "发布者用户ID", hidden = true)
    private Long authorId;
} 