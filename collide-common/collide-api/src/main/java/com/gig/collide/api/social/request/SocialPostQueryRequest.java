package com.gig.collide.api.social.request;

import com.gig.collide.api.social.constant.SocialPostType;
import com.gig.collide.api.social.constant.SocialPostStatus;
import com.gig.collide.base.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 查询社交动态请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查询社交动态请求")
public class SocialPostQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 动态ID（查询单个动态时使用）
     */
    @Schema(description = "动态ID", example = "123")
    private Long postId;

    /**
     * 作者用户ID
     */
    @Schema(description = "作者用户ID", example = "456")
    private Long authorId;

    /**
     * 动态类型
     */
    @Schema(description = "动态类型", example = "TEXT")
    private SocialPostType postType;

    /**
     * 动态状态
     */
    @Schema(description = "动态状态", example = "PUBLISHED")
    private SocialPostStatus status;

    /**
     * 话题标签
     */
    @Schema(description = "话题标签", example = "旅行")
    private String topic;

    /**
     * 关键词搜索
     */
    @Schema(description = "关键词搜索", example = "美食")
    private String keyword;

    /**
     * 查询类型（personal-个人动态，following-关注动态，hot-热门动态，nearby-附近动态）
     */
    @Schema(description = "查询类型", example = "following")
    private String queryType = "following";

    /**
     * 当前用户ID（用于权限判断和个性化推荐）
     */
    @Schema(description = "当前用户ID", example = "789")
    private Long currentUserId;

    /**
     * 位置信息 - 经度（查询附近动态时使用）
     */
    @Schema(description = "经度", example = "116.4074")
    private Double longitude;

    /**
     * 位置信息 - 纬度（查询附近动态时使用）
     */
    @Schema(description = "纬度", example = "39.9042")
    private Double latitude;

    /**
     * 搜索半径（公里，查询附近动态时使用）
     */
    @Schema(description = "搜索半径", example = "5.0")
    private Double radius;

    /**
     * 时间范围筛选 - 开始时间（时间戳）
     */
    @Schema(description = "开始时间戳", example = "1640995200000")
    private Long startTime;

    /**
     * 时间范围筛选 - 结束时间（时间戳）
     */
    @Schema(description = "结束时间戳", example = "1641081600000")
    private Long endTime;

    /**
     * 排序方式（time-时间排序，hot-热度排序，distance-距离排序）
     */
    @Schema(description = "排序方式", example = "time")
    private String sortBy = "time";

    /**
     * 排序方向（asc-升序，desc-降序）
     */
    @Schema(description = "排序方向", example = "desc")
    private String sortOrder = "desc";

    /**
     * 包含的用户ID列表（查询指定用户的动态）
     */
    @Schema(description = "包含的用户ID列表")
    private List<Long> includedUserIds;

    /**
     * 排除的用户ID列表（屏蔽特定用户的动态）
     */
    @Schema(description = "排除的用户ID列表")
    private List<Long> excludedUserIds;
} 