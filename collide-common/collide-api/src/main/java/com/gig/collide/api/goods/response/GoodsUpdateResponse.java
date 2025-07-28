package com.gig.collide.api.goods.response;

import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 商品更新响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "商品更新响应")
public class GoodsUpdateResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 更新后的商品信息
     */
    @Schema(description = "更新后的商品信息")
    private GoodsInfo goodsInfo;

    /**
     * 更新的商品ID
     */
    @Schema(description = "更新的商品ID")
    private Long goodsId;

    /**
     * 新版本号
     */
    @Schema(description = "新版本号")
    private Integer newVersion;

    /**
     * 更新成功的字段数量
     */
    @Schema(description = "更新成功的字段数量")
    private Integer updatedFieldCount;

    /**
     * 更新成功响应
     */
    public static GoodsUpdateResponse success(GoodsInfo goodsInfo) {
        GoodsUpdateResponse response = new GoodsUpdateResponse();
        response.setGoodsInfo(goodsInfo);
        response.setGoodsId(goodsInfo.getId());
        response.setNewVersion(goodsInfo.getVersion());
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("商品更新成功");
        return response;
    }

    /**
     * 更新成功响应（简化版）
     */
    public static GoodsUpdateResponse success(Long goodsId, Integer newVersion) {
        GoodsUpdateResponse response = new GoodsUpdateResponse();
        response.setGoodsId(goodsId);
        response.setNewVersion(newVersion);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("商品更新成功");
        return response;
    }

    /**
     * 更新失败响应
     */
    public static GoodsUpdateResponse error(String errorCode, String errorMessage) {
        GoodsUpdateResponse response = new GoodsUpdateResponse();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }

    /**
     * 乐观锁冲突响应
     */
    public static GoodsUpdateResponse versionConflict(Long goodsId, Integer currentVersion) {
        GoodsUpdateResponse response = new GoodsUpdateResponse();
        response.setGoodsId(goodsId);
        response.setNewVersion(currentVersion);
        response.setResponseCode("VERSION_CONFLICT");
        response.setResponseMessage("商品版本冲突，请重新获取最新数据后再试");
        return response;
    }
} 