package com.gig.collide.api.content.request;

import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ContentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容查询请求
 */
@Data
public class ContentQueryRequest {

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容类型
     */
    private ContentType contentType;

    /**
     * 内容状态
     */
    private ContentStatus status;

    /**
     * 创建者用户ID
     */
    private Long creatorUserId;

    /**
     * 标题关键词
     */
    private String titleKeyword;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 分类
     */
    private String category;

    /**
     * 是否原创
     */
    private Boolean isOriginal;

    /**
     * 语言
     */
    private String language;

    /**
     * 最小查看次数
     */
    private Long minViewCount;

    /**
     * 最大查看次数
     */
    private Long maxViewCount;

    /**
     * 最小点赞次数
     */
    private Long minLikeCount;

    /**
     * 最大点赞次数
     */
    private Long maxLikeCount;

    /**
     * 最小评分
     */
    private Double minRating;

    /**
     * 最大评分
     */
    private Double maxRating;

    /**
     * 创建时间开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 发布时间开始
     */
    private LocalDateTime publishTimeStart;

    /**
     * 发布时间结束
     */
    private LocalDateTime publishTimeEnd;
} 