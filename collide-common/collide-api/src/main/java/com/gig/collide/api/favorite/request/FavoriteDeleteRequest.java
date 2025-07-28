package com.gig.collide.api.favorite.request;

import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 收藏删除请求
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
@Schema(description = "收藏删除请求")
public class FavoriteDeleteRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID
     */
    @NotNull(message = "收藏ID不能为空")
    @Schema(description = "收藏ID", required = true)
    private Long favoriteId;

    /**
     * 是否物理删除
     */
    @Schema(description = "是否物理删除", example = "false")
    private Boolean physicalDelete = false;

    /**
     * 删除原因
     */
    @Schema(description = "删除原因")
    private String deleteReason;

    /**
     * 版本号（乐观锁）
     */
    @Schema(description = "版本号")
    private Integer version;
} 