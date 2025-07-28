package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 标签统计表实体
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_tag_statistics")
public class TagStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计ID，主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签ID（仅存储ID，不做外键关联）
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 日使用次数
     */
    @TableField("daily_usage_count")
    private Long dailyUsageCount;

    /**
     * 周使用次数
     */
    @TableField("weekly_usage_count")
    private Long weeklyUsageCount;

    /**
     * 月使用次数
     */
    @TableField("monthly_usage_count")
    private Long monthlyUsageCount;

    /**
     * 总用户数
     */
    @TableField("total_user_count")
    private Long totalUserCount;

    /**
     * 活跃用户数
     */
    @TableField("active_user_count")
    private Long activeUserCount;

    /**
     * 关联内容数
     */
    @TableField("content_count")
    private Long contentCount;

    /**
     * 统计日期
     */
    @TableField("stat_date")
    private LocalDate statDate;

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
} 