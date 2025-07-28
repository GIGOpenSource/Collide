package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteType;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 收藏创建请求
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
@Schema(description = "收藏创建请求")
public class FavoriteCreateRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏类型
     */
    @NotNull(message = "收藏类型不能为空")
    @Schema(description = "收藏类型", required = true)
    private FavoriteType favoriteType;

    /**
     * 目标ID
     */
    @NotNull(message = "目标ID不能为空")
    @Schema(description = "目标ID", required = true)
    private Long targetId;

    /**
     * 收藏用户ID
     */
    @NotNull(message = "收藏用户ID不能为空")
    @Schema(description = "收藏用户ID", required = true)
    private Long userId;

    /**
     * 收藏夹ID，默认为1（默认收藏夹）
     */
    @Schema(description = "收藏夹ID", example = "1")
    private Long folderId = 1L;

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
} 