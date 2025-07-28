package com.gig.collide.api.social.response.data;

import com.gig.collide.api.social.constant.PostTypeEnum;
import com.gig.collide.api.social.constant.PostStatusEnum;
import com.gig.collide.api.social.constant.VisibilityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 社交动态信息 DTO
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostInfo implements Serializable {
    
    /**
     * 动态ID
     */
    private Long id;
    
    /**
     * 动态类型
     */
    private PostTypeEnum postType;
    
    /**
     * 动态内容
     */
    private String content;
    
    /**
     * 媒体文件URL列表
     */
    private List<String> mediaUrls;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 经度
     */
    private BigDecimal longitude;
    
    /**
     * 纬度
     */
    private BigDecimal latitude;
    
    /**
     * 话题标签列表
     */
    private List<String> topics;
    
    /**
     * 提及的用户ID列表
     */
    private List<Long> mentionedUserIds;
    
    /**
     * 动态状态
     */
    private PostStatusEnum status;
    
    /**
     * 可见性
     */
    private VisibilityEnum visibility;
    
    /**
     * 是否允许评论
     */
    private Boolean allowComments;
    
    /**
     * 是否允许转发
     */
    private Boolean allowShares;
    
    /**
     * 作者用户ID
     */
    private Long authorId;
    
    /**
     * 作者用户名
     */
    private String authorUsername;
    
    /**
     * 作者昵称
     */
    private String authorNickname;
    
    /**
     * 作者头像URL
     */
    private String authorAvatar;
    
    /**
     * 作者认证状态
     */
    private Boolean authorVerified;
    
    /**
     * 点赞数
     */
    private Long likeCount;
    
    /**
     * 评论数
     */
    private Long commentCount;
    
    /**
     * 转发数
     */
    private Long shareCount;
    
    /**
     * 浏览数
     */
    private Long viewCount;
    
    /**
     * 收藏数
     */
    private Long favoriteCount;
    
    /**
     * 热度分数
     */
    private BigDecimal hotScore;
    
    /**
     * 发布时间
     */
    private LocalDateTime publishedTime;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 当前用户是否已收藏
     */
    private Boolean isFavorited;
    
    /**
     * 当前用户是否已转发
     */
    private Boolean isShared;
    
    /**
     * 是否为热门动态
     */
    public boolean isHot() {
        return hotScore != null && hotScore.compareTo(BigDecimal.valueOf(100)) >= 0;
    }
    
    /**
     * 是否为媒体类型动态
     */
    public boolean isMediaPost() {
        return postType != null && postType.isMediaType();
    }
    
    /**
     * 是否为原创内容
     */
    public boolean isOriginalContent() {
        return postType != null && postType.isOriginalContent();
    }
    
    /**
     * 是否可见
     */
    public boolean isVisible() {
        return status != null && status.isVisible();
    }
    
    /**
     * 是否可以互动
     */
    public boolean canInteract() {
        return status != null && status.canInteract();
    }
    
    /**
     * 获取总互动数
     */
    public Long getTotalInteractions() {
        return (likeCount != null ? likeCount : 0L) + 
               (commentCount != null ? commentCount : 0L) + 
               (shareCount != null ? shareCount : 0L) + 
               (favoriteCount != null ? favoriteCount : 0L);
    }
    
    /**
     * 获取参与度（互动数/浏览数）
     */
    public Double getEngagementRate() {
        if (viewCount == null || viewCount == 0) {
            return 0.0;
        }
        return getTotalInteractions().doubleValue() / viewCount.doubleValue();
    }
} 