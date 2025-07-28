package com.gig.collide.api.goods.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品删除响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "商品删除响应")
public class GoodsDeleteResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 删除的商品ID
     */
    @Schema(description = "删除的商品ID")
    private Long goodsId;

    /**
     * 是否真实删除（true为物理删除，false为逻辑删除）
     */
    @Schema(description = "是否真实删除")
    private Boolean physicalDelete;

    /**
     * 删除时间戳
     */
    @Schema(description = "删除时间戳")
    private Long deleteTimestamp;

    /**
     * 删除成功响应
     */
    public static GoodsDeleteResponse success(Long goodsId) {
        GoodsDeleteResponse response = new GoodsDeleteResponse();
        response.setGoodsId(goodsId);
        response.setPhysicalDelete(false);
        response.setDeleteTimestamp(System.currentTimeMillis());
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("商品删除成功");
        return response;
    }

    /**
     * 删除成功响应（指定删除类型）
     */
    public static GoodsDeleteResponse success(Long goodsId, Boolean physicalDelete) {
        GoodsDeleteResponse response = new GoodsDeleteResponse();
        response.setGoodsId(goodsId);
        response.setPhysicalDelete(physicalDelete);
        response.setDeleteTimestamp(System.currentTimeMillis());
        response.setResponseCode("SUCCESS");
        response.setResponseMessage(physicalDelete ? "商品物理删除成功" : "商品逻辑删除成功");
        return response;
    }

    /**
     * 删除失败响应
     */
    public static GoodsDeleteResponse error(String errorCode, String errorMessage) {
        GoodsDeleteResponse response = new GoodsDeleteResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }

    /**
     * 商品不存在响应
     */
    public static GoodsDeleteResponse notFound(Long goodsId) {
        GoodsDeleteResponse response = new GoodsDeleteResponse();
        response.setGoodsId(goodsId);
        response.setResponseCode("GOODS_NOT_FOUND");
        response.setResponseMessage("商品不存在");
        return response;
    }

    /**
     * 无权限删除响应
     */
    public static GoodsDeleteResponse noPermission(Long goodsId) {
        GoodsDeleteResponse response = new GoodsDeleteResponse();
        response.setGoodsId(goodsId);
        response.setResponseCode("NO_PERMISSION");
        response.setResponseMessage("无权限删除该商品");
        return response;
    }
} 