package com.gig.collide.api.search.response.data;

import com.gig.collide.api.search.constant.SearchTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 搜索结果信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchResultInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 结果ID
     */
    private Long id;

    /**
     * 结果类型
     */
    private SearchTypeEnum resultType;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述/摘要
     */
    private String description;

    /**
     * 内容片段（用于显示搜索匹配的部分）
     */
    private String snippet;

    /**
     * 高亮文本
     */
    private String highlightText;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 结果URL/链接
     */
    private String url;

    /**
     * 相关度评分
     */
    private Double relevanceScore;

    /**
     * 质量评分
     */
    private Double qualityScore;

    /**
     * 权重评分
     */
    private Double weight;

    /**
     * 作者信息
     */
    private AuthorInfo author;

    /**
     * 分类信息
     */
    private CategoryInfo category;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 统计信息
     */
    private StatisticsInfo statistics;

    /**
     * 扩展属性
     */
    private Map<String, Object> extraData;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 作者信息内嵌类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class AuthorInfo implements Serializable {
        private Long id;
        private String nickname;
        private String avatar;
        private String role;
        private Boolean verified;
    }

    /**
     * 分类信息内嵌类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class CategoryInfo implements Serializable {
        private Long id;
        private String name;
        private String path;
    }

    /**
     * 统计信息内嵌类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class StatisticsInfo implements Serializable {
        private Long viewCount;
        private Long likeCount;
        private Long commentCount;
        private Long shareCount;
        private Long favoriteCount;
    }
} 