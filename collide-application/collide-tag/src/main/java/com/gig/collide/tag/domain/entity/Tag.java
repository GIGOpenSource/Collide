package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体 - 简洁版
 * 对应t_tag表
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_tag")
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签类型：content、interest、system
     */
    private String tagType;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 使用次数
     */
    private Long usageCount;

    /**
     * 状态：active、inactive
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 