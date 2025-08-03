package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 内容标签实体 - 对应 t_content_tag 表
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_content_tag")
public class ContentTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 打标签时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime tagTime;

    // =================== 业务方法 ===================

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.tagTime == null) {
            this.tagTime = LocalDateTime.now();
        }
    }

    /**
     * 获取打标签天数
     */
    public long getTaggedDays() {
        if (this.tagTime == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(this.tagTime.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    /**
     * 检查是否为今日打标签
     */
    public boolean isTaggedToday() {
        if (this.tagTime == null) {
            return false;
        }
        return this.tagTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * 检查是否为最近打标签（7天内）
     */
    public boolean isRecentTag() {
        if (this.tagTime == null) {
            return false;
        }
        return this.tagTime.isAfter(LocalDateTime.now().minusDays(7));
    }

    /**
     * 检查是否为新内容标签（24小时内）
     */
    public boolean isNewContentTag() {
        if (this.tagTime == null) {
            return false;
        }
        return this.tagTime.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * 获取标签时长描述
     */
    public String getTagDurationDesc() {
        long days = getTaggedDays();
        if (days == 0) {
            return "今日标签";
        } else if (days < 7) {
            return days + "天前标签";
        } else if (days < 30) {
            return (days / 7) + "周前标签";
        } else if (days < 365) {
            return (days / 30) + "个月前标签";
        } else {
            return (days / 365) + "年前标签";
        }
    }

    /**
     * 创建内容标签关系
     */
    public static ContentTag create(Long contentId, Long tagId) {
        ContentTag contentTag = new ContentTag();
        contentTag.setContentId(contentId);
        contentTag.setTagId(tagId);
        contentTag.initDefaults();
        return contentTag;
    }

    /**
     * 验证内容标签关系的有效性
     */
    public boolean isValid() {
        return this.contentId != null && this.contentId > 0 
                && this.tagId != null && this.tagId > 0
                && this.tagTime != null;
    }

    /**
     * 获取内容新鲜度分数（用于推荐算法）
     */
    public double getFreshnessScore() {
        long days = getTaggedDays();
        if (days == 0) {
            return 1.0; // 今日内容分数最高
        } else if (days <= 3) {
            return 0.9; // 3天内内容分数很高
        } else if (days <= 7) {
            return 0.8; // 一周内内容分数较高
        } else if (days <= 14) {
            return 0.6; // 两周内内容分数中等
        } else if (days <= 30) {
            return 0.4; // 一月内内容分数较低
        } else {
            return 0.2; // 较老内容分数最低
        }
    }

    /**
     * 获取时间权重（用于热度计算）
     */
    public double getTimeWeight() {
        long hours = ChronoUnit.HOURS.between(this.tagTime, LocalDateTime.now());
        
        // 时间衰减函数：新内容权重高，随时间递减
        if (hours <= 1) {
            return 2.0; // 1小时内权重最高
        } else if (hours <= 6) {
            return 1.8; // 6小时内权重很高
        } else if (hours <= 24) {
            return 1.5; // 24小时内权重较高
        } else if (hours <= 72) {
            return 1.2; // 3天内权重中等
        } else if (hours <= 168) {
            return 1.0; // 一周内权重正常
        } else {
            return 0.8; // 超过一周权重较低
        }
    }

    /**
     * 检查内容是否为热门内容（基于时间）
     */
    public boolean isHotContent() {
        return isRecentTag() && getFreshnessScore() >= 0.8;
    }
}