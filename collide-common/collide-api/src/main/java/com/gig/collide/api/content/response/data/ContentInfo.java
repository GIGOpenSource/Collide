package com.gig.collide.api.content.response.data;

import com.gig.collide.api.content.constant.ContentStatus;
import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ReviewStatus;
import com.gig.collide.api.user.response.data.BasicUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 内容信息响应对象
 * 对应 t_content 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 内容类型
     */
    private ContentType contentType;

    /**
     * 内容数据（JSON格式，具体结构根据内容类型而定）
     */
    private Map<String, Object> contentData;

    /**
     * 封面URL
     */
    private String coverUrl;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者昵称（冗余字段，去连表化设计）
     */
    private String authorNickname;

    /**
     * 作者头像URL（冗余字段，去连表化设计）
     */
    private String authorAvatar;

    /**
     * 作者信息（详细信息，可为空）
     */
    private BasicUserInfo author;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 内容状态
     */
    private ContentStatus status;

    /**
     * 审核状态
     */
    private ReviewStatus reviewStatus;

    /**
     * 查看数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

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
     * 当前用户是否点赞
     */
    private Boolean liked;

    /**
     * 当前用户是否收藏
     */
    private Boolean favorited;

    /**
     * 是否为推荐内容
     */
    private Boolean recommended;

    /**
     * 是否置顶
     */
    private Boolean pinned;

    /**
     * 权重分数（用于推荐算法）
     */
    private Double weightScore;

    /**
     * 内容质量评分
     */
    private Double qualityScore;

    /**
     * 判断内容是否可见
     *
     * @return true if visible
     */
    public boolean isVisible() {
        return status != null && status.isVisible();
    }

    /**
     * 判断内容是否可编辑
     *
     * @return true if editable
     */
    public boolean isEditable() {
        return status != null && status.isEditable();
    }

    /**
     * 计算互动率
     *
     * @return 互动率 (点赞数+评论数+分享数) / 查看数
     */
    public double getEngagementRate() {
        if (viewCount == null || viewCount == 0) {
            return 0.0;
        }
        long interactions = (likeCount != null ? likeCount : 0) 
            + (commentCount != null ? commentCount : 0) 
            + (shareCount != null ? shareCount : 0);
        return (double) interactions / viewCount;
    }
} 