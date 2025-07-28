package com.gig.collide.api.favorite.response.data;

import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.api.favorite.constant.FavoriteType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏信息数据传输对象
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

    // === 目标信息冗余字段（去连表化设计） ===

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
     * 目标摘要/描述
     */
    @Schema(description = "目标摘要/描述")
    private String targetSummary;

    /**
     * 目标作者ID
     */
    @Schema(description = "目标作者ID")
    private Long targetAuthorId;

    /**
     * 目标作者名称
     */
    @Schema(description = "目标作者名称")
    private String targetAuthorName;

    /**
     * 目标作者头像
     */
    @Schema(description = "目标作者头像")
    private String targetAuthorAvatar;

    /**
     * 目标发布时间
     */
    @Schema(description = "目标发布时间")
    private LocalDateTime targetPublishTime;

    // === 审计字段 ===

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