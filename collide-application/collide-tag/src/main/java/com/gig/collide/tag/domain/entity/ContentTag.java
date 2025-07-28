package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容标签实体 - 简洁版
 * 对应t_content_tag表
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_content_tag")
public class ContentTag {

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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 