package com.gig.collide.api.content.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 章节创建请求 - 简洁版
 * 基于content-simple.sql的t_content_chapter表结构
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
public class ChapterCreateRequest {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 章节号
     */
    @NotNull(message = "章节号不能为空")
    private Integer chapterNum;

    /**
     * 章节标题
     */
    @NotBlank(message = "章节标题不能为空")
    private String title;

    /**
     * 章节内容
     */
    private String content;

    /**
     * 字数
     */
    private Integer wordCount;

    /**
     * 状态（默认为DRAFT）
     */
    private String status = "DRAFT";

    /**
     * 作者ID（用于权限验证）
     */
    @NotNull(message = "作者ID不能为空")
    private Long authorId;
}