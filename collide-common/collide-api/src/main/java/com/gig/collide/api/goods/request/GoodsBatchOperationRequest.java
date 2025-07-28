package com.gig.collide.api.goods.request;

import com.gig.collide.api.goods.constant.GoodsStatus;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 商品批量操作请求
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
@Schema(description = "商品批量操作请求")
public class GoodsBatchOperationRequest extends BaseRequest {

    /**
     * 商品ID列表
     */
    @Schema(description = "商品ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "商品ID列表不能为空")
    @Size(max = 100, message = "单次批量操作不能超过100个商品")
    private List<Long> goodsIds;

    /**
     * 操作类型
     */
    @Schema(description = "操作类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "操作类型不能为空")
    private OperationType operationType;

    /**
     * 目标状态（状态变更操作使用）
     */
    @Schema(description = "目标状态")
    private GoodsStatus targetStatus;

    /**
     * 推荐标记（推荐操作使用）
     */
    @Schema(description = "推荐标记")
    private Boolean recommended;

    /**
     * 热门标记（热门操作使用）
     */
    @Schema(description = "热门标记")
    private Boolean hot;

    /**
     * 操作人ID
     */
    @Schema(description = "操作人ID")
    private Long operatorId;

    /**
     * 操作原因
     */
    @Schema(description = "操作原因")
    private String reason;

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        /**
         * 批量删除
         */
        DELETE("DELETE", "批量删除"),
        
        /**
         * 批量更新状态
         */
        UPDATE_STATUS("UPDATE_STATUS", "批量更新状态"),
        
        /**
         * 批量设置推荐
         */
        SET_RECOMMENDED("SET_RECOMMENDED", "批量设置推荐"),
        
        /**
         * 批量设置热门
         */
        SET_HOT("SET_HOT", "批量设置热门"),
        
        /**
         * 批量上架
         */
        ON_SALE("ON_SALE", "批量上架"),
        
        /**
         * 批量下架
         */
        OFF_SALE("OFF_SALE", "批量下架");
        
        private final String code;
        private final String description;
        
        OperationType(String code, String description) {
            this.code = code;
            this.description = description;
        }
        
        public String getCode() { return code; }
        public String getDescription() { return description; }
    }

    // ===================== 便捷构造器 =====================

    /**
     * 批量删除
     */
    public static GoodsBatchOperationRequest delete(List<Long> goodsIds) {
        GoodsBatchOperationRequest request = new GoodsBatchOperationRequest();
        request.setGoodsIds(goodsIds);
        request.setOperationType(OperationType.DELETE);
        return request;
    }

    /**
     * 批量更新状态
     */
    public static GoodsBatchOperationRequest updateStatus(List<Long> goodsIds, GoodsStatus status) {
        GoodsBatchOperationRequest request = new GoodsBatchOperationRequest();
        request.setGoodsIds(goodsIds);
        request.setOperationType(OperationType.UPDATE_STATUS);
        request.setTargetStatus(status);
        return request;
    }

    /**
     * 批量设置推荐
     */
    public static GoodsBatchOperationRequest setRecommended(List<Long> goodsIds, Boolean recommended) {
        GoodsBatchOperationRequest request = new GoodsBatchOperationRequest();
        request.setGoodsIds(goodsIds);
        request.setOperationType(OperationType.SET_RECOMMENDED);
        request.setRecommended(recommended);
        return request;
    }

    /**
     * 批量设置热门
     */
    public static GoodsBatchOperationRequest setHot(List<Long> goodsIds, Boolean hot) {
        GoodsBatchOperationRequest request = new GoodsBatchOperationRequest();
        request.setGoodsIds(goodsIds);
        request.setOperationType(OperationType.SET_HOT);
        request.setHot(hot);
        return request;
    }

    /**
     * 批量上架
     */
    public static GoodsBatchOperationRequest onSale(List<Long> goodsIds) {
        return updateStatus(goodsIds, GoodsStatus.ON_SALE);
    }

    /**
     * 批量下架
     */
    public static GoodsBatchOperationRequest offSale(List<Long> goodsIds) {
        return updateStatus(goodsIds, GoodsStatus.OFF_SALE);
    }
} 