package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 内容章节表实体类
 * 用于小说、漫画等多章节内容的管理
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
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
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    // =================== 计算属性 ===================

    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return "PUBLISHED".equals(status);
    }

    /**
     * 判断是否草稿状态
     */
    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    /**
     * 获取内容摘要（前100字符）
     */
    public String getContentSummary() {
        if (content == null || content.length() <= 100) {
            return content;
        }
        return content.substring(0, 100) + "...";
    }

    /**
     * 计算阅读时长（按300字/分钟计算）
     */
    public Integer getEstimatedReadingTime() {
        if (wordCount == null || wordCount == 0) {
            return 1;
        }
        return Math.max(1, (int) Math.ceil(wordCount / 300.0));
    }

    /**
     * 计算字数
     */
    public void calculateWordCount() {
        if (content != null) {
            // 简单的字数计算（去除空格和换行）
            String cleanContent = content.replaceAll("\\s+", "");
            this.wordCount = cleanContent.length();
        } else {
            this.wordCount = 0;
        }
    }

    /**
     * 验证章节数据是否有效
     */
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() 
               && content != null && !content.trim().isEmpty()
               && chapterNum != null && chapterNum > 0;
    }

    /**
     * 判断是否可以发布
     */
    public boolean canPublish() {
        return isDraft() && isValid();
    }

    /**
     * 判断是否可以编辑
     */
    public boolean canEdit() {
        return isDraft() || isPublished();
    }
}