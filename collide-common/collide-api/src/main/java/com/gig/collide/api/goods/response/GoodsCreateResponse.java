package com.gig.collide.api.goods.response;

import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品创建响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "商品创建响应")
public class GoodsCreateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 创建成功的商品信息
     */
    @Schema(description = "创建成功的商品信息")
    private GoodsInfo goodsInfo;

    /**
     * 创建的商品ID
     */
    @Schema(description = "创建的商品ID")
    private Long goodsId;

    /**
     * 创建成功响应
     */
    public static GoodsCreateResponse success(GoodsInfo goodsInfo) {
        GoodsCreateResponse response = new GoodsCreateResponse();
        response.setGoodsInfo(goodsInfo);
        response.setGoodsId(goodsInfo.getId());
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("商品创建成功");
        return response;
    }

    /**
     * 创建成功响应（只返回ID）
     */
    public static GoodsCreateResponse success(Long goodsId) {
        GoodsCreateResponse response = new GoodsCreateResponse();
        response.setGoodsId(goodsId);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("商品创建成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static GoodsCreateResponse error(String errorCode, String errorMessage) {
        GoodsCreateResponse response = new GoodsCreateResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 