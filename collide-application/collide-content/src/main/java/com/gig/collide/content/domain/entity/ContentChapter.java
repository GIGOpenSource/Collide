package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 内容章节实体类 - 简洁版
 * 基于content-simple.sql的t_content_chapter表结构
 * 用于小说、漫画等多章节内容管理
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
@TableName("t_content_chapter")
public class ContentChapter {

    /**
     * 章节ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 章节号
     */
    @TableField("chapter_num")
    private Integer chapterNum;

    /**
     * 章节标题
     */
    @TableField("title")
    private String title;

    /**
     * 章节内容
     */
    @TableField("content")
    private String content;

    /**
     * 字数
     */
    @TableField("word_count")
    private Integer wordCount;

    /**
     * 状态：DRAFT、PUBLISHED
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 是否为草稿状态
     */
    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    /**
     * 是否已发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /**
     * 发布章节
     */
    public void publish() {
        this.status = "PUBLISHED";
    }

    /**
     * 设为草稿
     */
    public void setToDraft() {
        this.status = "DRAFT";
    }

    /**
     * 计算字数（如果没有提供）
     */
    public void calculateWordCount() {
        if (content != null) {
            this.wordCount = content.replaceAll("\\s", "").length();
        } else {
            this.wordCount = 0;
        }
    }

    /**
     * 获取阅读时长估算（按300字/分钟计算）
     */
    public Integer getEstimateReadMinutes() {
        if (wordCount == null || wordCount <= 0) {
            return 0;
        }
        return Math.max(1, wordCount / 300);
    }

    /**
     * 是否可以发布
     */
    public boolean canPublish() {
        return isDraft() && title != null && !title.trim().isEmpty() 
               && content != null && !content.trim().isEmpty();
    }

    /**
     * 是否可以编辑
     */
    public boolean canEdit() {
        return isDraft();
    }

    /**
     * 获取章节摘要（前100个字符）
     */
    public String getSummary() {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }
        String cleanContent = content.replaceAll("\\s", "");
        return cleanContent.length() > 100 ? cleanContent.substring(0, 100) + "..." : cleanContent;
    }

    /**
     * 验证章节数据
     */
    public boolean isValid() {
        return contentId != null && chapterNum != null && chapterNum > 0
               && title != null && !title.trim().isEmpty();
    }

    /**
     * 生成唯一键
     */
    public String generateUniqueKey() {
        return contentId + "_" + chapterNum;
    }
}