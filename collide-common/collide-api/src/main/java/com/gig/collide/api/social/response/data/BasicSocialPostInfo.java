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
 * 基础社交动态信息 DTO
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasicSocialPostInfo implements Serializable {
    
    /**
     * 动态ID
     */
    private Long id;
    
    /**
     * 动态类型
     */
    private PostTypeEnum postType;
    
    /**
     * 动态内容（截取前200字符）
     */
    private String content;
    
    /**
     * 首张媒体文件URL
     */
    private String firstMediaUrl;
    
    /**
     * 媒体文件数量
     */
    private Integer mediaCount;
    
    /**
     * 位置信息
     */
    private String location;
    
    /**
     * 话题标签列表
     */
    private List<String> topics;
    
    /**
     * 动态状态
     */
    private PostStatusEnum status;
    
    /**
     * 可见性
     */
    private VisibilityEnum visibility;
    
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
     * 热度分数
     */
    private BigDecimal hotScore;
    
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
     * 是否为热门动态
     */
    public boolean isHot() {
        return hotScore != null && hotScore.compareTo(BigDecimal.valueOf(50)) >= 0;
    }
    
    /**
     * 是否有媒体内容
     */
    public boolean hasMedia() {
        return mediaCount != null && mediaCount > 0;
    }
    
    /**
     * 是否为多媒体动态
     */
    public boolean isMultiMedia() {
        return mediaCount != null && mediaCount > 1;
    }
    
    /**
     * 获取内容摘要（限制长度）
     */
    public String getContentSummary() {
        if (content == null) {
            return "";
        }
        return content.length() > 100 ? content.substring(0, 100) + "..." : content;
    }
} 