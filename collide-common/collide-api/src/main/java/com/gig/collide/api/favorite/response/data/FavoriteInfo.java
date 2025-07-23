package com.gig.collide.api.favorite.response.data;

import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.api.favorite.constant.FavoriteStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@Schema(description = "收藏信息")
public class FavoriteInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID")
    private Long favoriteId;

    /**
     * 收藏类型
     */
    @Schema(description = "收藏类型")
    private FavoriteType favoriteType;

    /**
     * 目标ID
     */
    @Schema(description = "目标ID")
    private Long targetId;

    /**
     * 收藏用户ID
     */
    @Schema(description = "收藏用户ID")
    private Long userId;

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
     * 收藏状态
     */
    @Schema(description = "收藏状态")
    private FavoriteStatus status;

    /**
     * 收藏备注
     */
    @Schema(description = "收藏备注")
    private String remark;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    /**
     * 目标标题（如果是内容收藏）
     */
    @Schema(description = "目标标题")
    private String targetTitle;

    /**
     * 目标封面（如果是内容收藏）
     */
    @Schema(description = "目标封面")
    private String targetCover;

    /**
     * 目标作者（如果是内容收藏）
     */
    @Schema(description = "目标作者")
    private String targetAuthor;

    /**
     * 目标作者头像（如果是内容收藏）
     */
    @Schema(description = "目标作者头像")
    private String targetAuthorAvatar;

    /**
     * 是否可以取消收藏
     */
    @Schema(description = "是否可以取消收藏")
    private Boolean canUnfavorite;

    /**
     * 是否可以移动到其他收藏夹
     */
    @Schema(description = "是否可以移动到其他收藏夹")
    private Boolean canMove;
} 