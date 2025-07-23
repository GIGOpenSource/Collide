package com.gig.collide.api.social.response.data;

import com.gig.collide.api.social.constant.SocialPostType;
import com.gig.collide.api.social.constant.SocialPostStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态信息
 * 
 * <p>基于去连表化设计，包含作者信息等冗余字段</p>
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "社交动态信息")
public class SocialPostInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 动态ID
     */
    @Schema(description = "动态ID")
    private Long postId;

    /**
     * 动态类型
     */
    @Schema(description = "动态类型")
    private SocialPostType postType;

    /**
     * 动态内容
     */
    @Schema(description = "动态内容")
    private String content;

    /**
     * 媒体文件URL列表
     */
    @Schema(description = "媒体文件URL列表")
    private List<String> mediaUrls;

    /**
     * 位置信息
     */
    @Schema(description = "位置信息")
    private String location;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double latitude;

    /**
     * 话题标签列表
     */
    @Schema(description = "话题标签列表")
    private List<String> topics;

    /**
     * 提及的用户ID列表
     */
    @Schema(description = "提及的用户ID列表")
    private List<Long> mentionedUserIds;

    /**
     * 动态状态
     */
    @Schema(description = "动态状态")
    private SocialPostStatus status;

    /**
     * 可见性设置
     */
    @Schema(description = "可见性设置")
    private Integer visibility;

    /**
     * 是否允许评论
     */
    @Schema(description = "是否允许评论")
    private Boolean allowComments;

    /**
     * 是否允许转发
     */
    @Schema(description = "是否允许转发")
    private Boolean allowShares;

    // === 作者信息（冗余字段） ===

    /**
     * 作者用户ID
     */
    @Schema(description = "作者用户ID")
    private Long authorId;

    /**
     * 作者用户名
     */
    @Schema(description = "作者用户名")
    private String authorUsername;

    /**
     * 作者昵称
     */
    @Schema(description = "作者昵称")
    private String authorNickname;

    /**
     * 作者头像URL
     */
    @Schema(description = "作者头像URL")
    private String authorAvatar;

    /**
     * 作者认证状态
     */
    @Schema(description = "作者认证状态")
    private Boolean authorVerified;

    // === 统计信息（冗余字段） ===

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Long likeCount;

    /**
     * 评论数
     */
    @Schema(description = "评论数")
    private Long commentCount;

    /**
     * 转发数
     */
    @Schema(description = "转发数")
    private Long shareCount;

    /**
     * 浏览数
     */
    @Schema(description = "浏览数")
    private Long viewCount;

    /**
     * 收藏数
     */
    @Schema(description = "收藏数")
    private Long favoriteCount;

    // === 时间信息 ===

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 发布时间
     */
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;

    // === 当前用户相关信息 ===

    /**
     * 当前用户是否已点赞
     */
    @Schema(description = "当前用户是否已点赞")
    private Boolean currentUserLiked;

    /**
     * 当前用户是否已收藏
     */
    @Schema(description = "当前用户是否已收藏")
    private Boolean currentUserFavorited;

    /**
     * 当前用户是否已关注作者
     */
    @Schema(description = "当前用户是否已关注作者")
    private Boolean currentUserFollowed;

    /**
     * 是否可以编辑（当前用户是作者）
     */
    @Schema(description = "是否可以编辑")
    private Boolean canEdit;

    /**
     * 是否可以删除（当前用户是作者或管理员）
     */
    @Schema(description = "是否可以删除")
    private Boolean canDelete;

    /**
     * 热度分数（用于热门排序）
     */
    @Schema(description = "热度分数")
    private Double hotScore;

    /**
     * 距离（查询附近动态时使用，单位：公里）
     */
    @Schema(description = "距离")
    private Double distance;
} 