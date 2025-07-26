package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容点赞记录实体类
 * 对应 t_content_like 表
 * 用于解决点赞操作的幂等性问题
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_content_like")
public class ContentLike {

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
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 点赞时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 构造函数（用于创建点赞记录）
     */
    public ContentLike(Long contentId, Long userId) {
        this.contentId = contentId;
        this.userId = userId;
        this.createdTime = LocalDateTime.now();
        this.deleted = 0;
    }
} 