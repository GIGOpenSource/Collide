package com.gig.collide.api.content.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 内容查询请求 - 简洁版
 * 基于content-simple.sql的字段，支持多种查询条件
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContentQueryRequest implements Serializable {

    /**
     * 内容ID
     */
    private Long id;

    /**
     * 标题关键词
     */
    private String title;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    private String contentType;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者昵称关键词
     */
    private String authorNickname;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称关键词
     */
    private String categoryName;

    /**
     * 状态：DRAFT、PUBLISHED、OFFLINE
     */
    private String status;

    /**
     * 审核状态：PENDING、APPROVED、REJECTED
     */
    private String reviewStatus;

    /**
     * 标签关键词
     */
    private String tagsKeyword;

    /**
     * 搜索关键词（标题+描述）
     */
    private String keyword;

    /**
     * 查询类型：author、category、popular、latest、search
     */
    private String queryType;

    /**
     * 时间范围（天），用于热门内容查询
     */
    private Integer timeRange;

    /**
     * 最小浏览量
     */
    private Long minViewCount;

    /**
     * 最小点赞数
     */
    private Long minLikeCount;

    /**
     * 最小评分
     */
    private Double minScore;

    // =================== 分页参数 ===================

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段：create_time、publish_time、view_count、like_count、score
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "DESC";

    /**
     * 是否包含下线内容
     */
    private Boolean includeOffline = false;
}