package com.gig.collide.favorite.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gig.collide.api.favorite.constant.FolderType;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏夹实体
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@TableName("t_favorite_folder")
public class FavoriteFolder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹ID
     */
    @TableId(value = "folder_id", type = IdType.ASSIGN_ID)
    private Long folderId;

    /**
     * 收藏夹名称
     */
    @TableField("folder_name")
    private String folderName;

    /**
     * 收藏夹描述
     */
    @TableField("description")
    private String description;

    /**
     * 收藏夹类型
     */
    @TableField("folder_type")
    private FolderType folderType;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否为默认收藏夹
     */
    @TableField("is_default")
    private Boolean isDefault;

    /**
     * 收藏夹封面图片
     */
    @TableField("cover_image")
    private String coverImage;

    /**
     * 排序权重
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 收藏数量
     */
    @TableField("item_count")
    private Integer itemCount;

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
     * 是否删除（0-否，1-是）
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;
} 