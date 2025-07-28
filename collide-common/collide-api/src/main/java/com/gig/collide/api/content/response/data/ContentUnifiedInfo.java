package com.gig.collide.api.content.response.data;

import com.gig.collide.api.content.enums.ContentStatusEnum;
import com.gig.collide.api.content.enums.ContentTypeEnum;
import com.gig.collide.api.content.enums.ReviewStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容统一信息传输对象
 * 包含完整的内容信息和统计数据
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentUnifiedInfo extends BasicContentInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 内容类型
     */
    private ContentTypeEnum contentType;

    /**
     * 内容数据（JSON格式）
     */
    private String contentData;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 内容状态
     */
    private ContentStatusEnum status;

    /**
     * 审核状态
     */
    private ReviewStatusEnum reviewStatus;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 审核员ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewedTime;

    /**
     * 查看数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 点踩数
     */
    private Long dislikeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 分享数
     */
    private Long shareCount;

    /**
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 权重分数
     */
    private Double weightScore;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 是否置顶
     */
    private Boolean isPinned;

    /**
     * 是否允许评论
     */
    private Boolean allowComment;

    /**
     * 是否允许分享
     */
    private Boolean allowShare;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 发布时间
     */
    private LocalDateTime publishedTime;

    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;

    /**
     * 当前用户是否已收藏
     */
    private Boolean isFavorited;

    /**
     * 当前用户是否已关注作者
     */
    private Boolean isFollowed;

    /**
     * 判断内容是否已发布
     *
     * @return true如果已发布
     */
    public boolean isPublished() {
        return status != null && status.isPublished();
    }

    /**
     * 判断内容是否可编辑
     *
     * @return true如果可编辑
     */
    public boolean isEditable() {
        return status != null && status.isEditable();
    }

    /**
     * 判断审核是否通过
     *
     * @return true如果审核通过
     */
    public boolean isReviewApproved() {
        return reviewStatus != null && reviewStatus.isApproved();
    }

    /**
     * 计算内容热度分数
     * 基于各项统计数据计算综合热度
     *
     * @return 热度分数
     */
    public double calculateHotScore() {
        long views = viewCount != null ? viewCount : 0;
        long likes = likeCount != null ? likeCount : 0;
        long comments = commentCount != null ? commentCount : 0;
        long shares = shareCount != null ? shareCount : 0;
        long favorites = favoriteCount != null ? favoriteCount : 0;
        long dislikes = dislikeCount != null ? dislikeCount : 0;

        // 热度计算公式：浏览*0.1 + 点赞*1 + 评论*2 + 分享*3 + 收藏*2 - 点踩*0.5
        return views * 0.1 + likes + comments * 2 + shares * 3 + favorites * 2 - dislikes * 0.5;
    }
} 