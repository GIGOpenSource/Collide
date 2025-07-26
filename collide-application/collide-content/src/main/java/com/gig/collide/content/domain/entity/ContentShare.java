package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容分享记录实体类
 * 对应 t_content_share 表
 * 用于记录分享操作（分享可以重复，但需要记录）
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_content_share")
public class ContentShare {

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
     * 分享平台
     */
    @TableField("platform")
    private String platform;

    /**
     * 分享文案
     */
    @TableField("share_text")
    private String shareText;

    /**
     * 分享时间
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
     * 构造函数（用于创建分享记录）
     */
    public ContentShare(Long contentId, Long userId, String platform, String shareText) {
        this.contentId = contentId;
        this.userId = userId;
        this.platform = platform;
        this.shareText = shareText;
        this.createdTime = LocalDateTime.now();
        this.deleted = 0;
    }
} 