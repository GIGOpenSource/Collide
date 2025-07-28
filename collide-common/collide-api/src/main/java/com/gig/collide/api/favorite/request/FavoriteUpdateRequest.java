package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteStatus;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 收藏更新请求
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
@Schema(description = "收藏更新请求")
public class FavoriteUpdateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @NotNull(message = "收藏ID不能为空")
    @Schema(description = "收藏ID", required = true)
    private Long favoriteId;

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
    @Size(max = 500, message = "收藏备注长度不能超过500字符")
    @Schema(description = "收藏备注")
    private String remark;

    // === 目标信息冗余字段（去连表化设计） ===

    /**
     * 目标标题
     */
    @Size(max = 500, message = "目标标题长度不能超过500字符")
    @Schema(description = "目标标题")
    private String targetTitle;

    /**
     * 目标封面/头像
     */
    @Size(max = 500, message = "目标封面URL长度不能超过500字符")
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
    @Size(max = 100, message = "目标作者名称长度不能超过100字符")
    @Schema(description = "目标作者名称")
    private String targetAuthorName;

    /**
     * 目标作者头像
     */
    @Size(max = 500, message = "目标作者头像URL长度不能超过500字符")
    @Schema(description = "目标作者头像")
    private String targetAuthorAvatar;

    /**
     * 目标发布时间
     */
    @Schema(description = "目标发布时间")
    private LocalDateTime targetPublishTime;

    /**
     * 版本号（乐观锁）
     */
    @Schema(description = "版本号")
    private Integer version;
} 