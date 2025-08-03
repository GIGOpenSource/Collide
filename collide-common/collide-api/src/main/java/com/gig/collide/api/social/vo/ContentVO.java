package com.gig.collide.api.social.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容信息VO
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
public class ContentVO {

    private Long id;

    private Long userId;

    private Long categoryId;

    private String categoryPath;

    private Integer contentType;

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

    private Integer purchaseCount;

    // 统计信息
    private Integer likeCount;

    private Integer commentCount;

    private Integer shareCount;

    private Integer favoriteCount;

    private Integer viewCount;

    private BigDecimal recommendScore;

    private BigDecimal qualityScore;

    private Integer status;

    private Integer privacy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // 用户相关信息（冗余字段，避免多次查询）
    private String authorName;

    private String authorAvatar;

    // 当前用户的互动状态
    private Boolean isLiked;

    private Boolean isFavorited;

    private Boolean isPurchased;
    
    // 标签信息
    private List<TagInfo> tags;
    
    /**
     * 标签简化信息
     */
    @Data
    public static class TagInfo {
        private Long id;
        private String tagName;
        private String tagIcon;
        private Integer weight;
    }
}