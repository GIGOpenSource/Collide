package com.gig.collide.business.domain.tag.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 标签实体
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 标签颜色
     */
    @TableField("color")
    private String color;

    /**
     * 标签图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 标签类型
     */
    @TableField("tag_type")
    private String tagType;

    /**
     * 所属分类ID
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
     * 标签状态
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
} 