package com.gig.collide.api.favorite.response.data;

import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.api.favorite.constant.FavoriteType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础收藏信息数据传输对象
 * 用于公开显示，不包含敏感信息
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
@Schema(description = "基础收藏信息")
public class BasicFavoriteInfo implements Serializable {

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
     * 收藏状态
     */
    @Schema(description = "收藏状态")
    private FavoriteStatus status;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    private LocalDateTime favoriteTime;

    /**
     * 目标标题
     */
    @Schema(description = "目标标题")
    private String targetTitle;

    /**
     * 目标封面/头像
     */
    @Schema(description = "目标封面/头像")
    private String targetCover;

    /**
     * 目标作者名称
     */
    @Schema(description = "目标作者名称")
    private String targetAuthorName;

    /**
     * 目标发布时间
     */
    @Schema(description = "目标发布时间")
    private LocalDateTime targetPublishTime;
} 