package com.gig.collide.social.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏实体
 * 对应表: t_social_favorite
 * 
 * @author GIG Team
 * @since 2024-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_social_favorite")
public class SocialFavorite extends SocialInteraction {

    @TableField("folder_id")
    private Long folderId; // 收藏夹ID(0为默认收藏夹)

    @TableField("folder_name")
    private String folderName; // 收藏夹名称

    // 默认收藏夹
    public static final Long DEFAULT_FOLDER_ID = 0L;
    public static final String DEFAULT_FOLDER_NAME = "默认收藏夹";
}