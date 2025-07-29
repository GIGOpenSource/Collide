package com.gig.collide.api.content.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 章节响应对象 - 简洁版
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
public class ChapterResponse {

    /**
     * 章节ID
     */
    private Long id;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 章节号
     */
    private Integer chapterNum;

    /**
     * 章节标题
     */
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
     * 状态：DRAFT、PUBLISHED
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 计算属性 ===================

    /**
     * 是否为草稿状态
     */
    @JsonIgnore
    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    /**
     * 是否已发布
     */
    @JsonIgnore
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /**
     * 获取章节创建天数
     */
    @JsonIgnore
    public long getCreateDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 获取阅读时长估算（按300字/分钟计算）
     */
    @JsonIgnore
    public Integer getEstimateReadMinutes() {
        if (wordCount == null || wordCount <= 0) {
            return 0;
        }
        return Math.max(1, wordCount / 300);
    }
}