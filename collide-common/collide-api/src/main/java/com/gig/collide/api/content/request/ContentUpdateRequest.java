package com.gig.collide.api.content.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 内容更新请求 - 简洁版
 * 支持内容信息、状态、统计数据等更新
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
public class ContentUpdateRequest implements Serializable {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long id;

    /**
     * 内容标题
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 内容数据，JSON格式
     */
    private String contentData;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 标签，JSON数组格式
     */
    private String tags;

    // =================== 分类信息更新 ===================

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（冗余）
     */
    private String categoryName;

    // =================== 状态更新 ===================

    /**
     * 状态：DRAFT、PUBLISHED、OFFLINE
     */
    private String status;

    /**
     * 审核状态：PENDING、APPROVED、REJECTED
     */
    private String reviewStatus;

    // =================== 统计数据更新 ===================

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
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 评分数
     */
    private Long scoreCount;

    /**
     * 总评分
     */
    private Long scoreTotal;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 更新类型：CONTENT、STATUS、STATS
     */
    private String updateType;
}