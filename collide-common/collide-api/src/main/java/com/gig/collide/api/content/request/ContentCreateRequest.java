package com.gig.collide.api.content.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 内容创建请求 - 简洁版
 * 基于content-simple.sql的无连表设计，包含作者和分类信息冗余
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
public class ContentCreateRequest {

    /**
     * 内容标题
     */
    @NotBlank(message = "内容标题不能为空")
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    @NotBlank(message = "内容类型不能为空")
    private String contentType;

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

    // =================== 作者信息（冗余字段） ===================

    /**
     * 作者用户ID
     */
    @NotNull(message = "作者ID不能为空")
    private Long authorId;

    /**
     * 作者昵称（冗余）
     */
    private String authorNickname;

    /**
     * 作者头像URL（冗余）
     */
    private String authorAvatar;

    // =================== 分类信息（冗余字段） ===================

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（冗余）
     */
    private String categoryName;

    /**
     * 状态（默认为DRAFT）
     */
    private String status = "DRAFT";

    /**
     * 审核状态（默认为PENDING）
     */
    private String reviewStatus = "PENDING";
} 