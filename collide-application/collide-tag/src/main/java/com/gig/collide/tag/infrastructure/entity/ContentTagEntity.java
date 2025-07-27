package com.gig.collide.tag.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 内容标签关联实体类
 * 对应数据库表：t_content_tag
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_content_tag")
public class ContentTagEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
} 