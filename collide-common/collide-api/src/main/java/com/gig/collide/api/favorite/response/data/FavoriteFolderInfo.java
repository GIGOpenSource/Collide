package com.gig.collide.api.favorite.response.data;

import com.gig.collide.api.favorite.constant.FolderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏夹信息数据传输对象
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "收藏夹信息")
public class FavoriteFolderInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏夹ID
     */
    @Schema(description = "收藏夹ID")
    private Long folderId;

    /**
     * 收藏夹名称
     */
    @Schema(description = "收藏夹名称")
    private String folderName;

    /**
     * 收藏夹描述
     */
    @Schema(description = "收藏夹描述")
    private String description;

    /**
     * 收藏夹类型
     */
    @Schema(description = "收藏夹类型")
    private FolderType folderType;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 是否为默认收藏夹
     */
    @Schema(description = "是否为默认收藏夹")
    private Boolean isDefault;

    /**
     * 收藏夹封面图片
     */
    @Schema(description = "收藏夹封面图片")
    private String coverImage;

    /**
     * 排序权重
     */
    @Schema(description = "排序权重")
    private Integer sortOrder;

    /**
     * 收藏数量
     */
    @Schema(description = "收藏数量")
    private Integer itemCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean deleted;

    /**
     * 版本号
     */
    @Schema(description = "版本号")
    private Integer version;
} 