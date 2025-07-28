package com.gig.collide.api.goods.request;

import com.gig.collide.base.request.BaseRequest;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品删除请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商品删除请求")
public class GoodsDeleteRequest extends BaseRequest {

    /**
     * 商品ID
     */
    @Schema(description = "商品ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 版本号（乐观锁）
     */
    @Schema(description = "版本号（乐观锁）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    private Integer version;

    /**
     * 删除原因
     */
    @Schema(description = "删除原因")
    private String deleteReason;

    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /**
     * 是否强制删除（忽略约束检查）
     */
    @Schema(description = "是否强制删除（忽略约束检查）")
    private Boolean forceDelete = false;

    // ===================== 便捷构造器 =====================

    /**
     * 单个商品删除
     */
    public static GoodsDeleteRequest of(Long id, Integer version) {
        return new GoodsDeleteRequest(id, version, null, null, false);
    }

    /**
     * 带原因的删除
     */
    public static GoodsDeleteRequest withReason(Long id, Integer version, String reason) {
        return new GoodsDeleteRequest(id, version, reason, null, false);
    }

    /**
     * 强制删除
     */
    public static GoodsDeleteRequest force(Long id, Integer version, String reason) {
        return new GoodsDeleteRequest(id, version, reason, null, true);
    }
} 