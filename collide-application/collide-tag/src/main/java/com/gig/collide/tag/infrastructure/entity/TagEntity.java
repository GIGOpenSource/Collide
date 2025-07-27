package com.gig.collide.tag.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 标签实体类
 * 对应数据库表：t_tag
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_tag")
public class TagEntity {

    /**
     * 标签ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 标签描述
     */
    @TableField("description")
    private String description;

    /**
     * 标签颜色（十六进制）
     */
    @TableField("color")
    private String color;

    /**
     * 标签图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 标签类型：content-内容标签、interest-兴趣标签、system-系统标签
     */
    @TableField("tag_type")
    private String tagType;

    /**
     * 所属分类ID（可选）
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 使用次数
     */
    @TableField("usage_count")
    private Long usageCount;

    /**
     * 热度分数
     */
    @TableField("heat_score")
    private BigDecimal heatScore;

    /**
     * 标签状态：active-活跃、inactive-非活跃
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

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
} 