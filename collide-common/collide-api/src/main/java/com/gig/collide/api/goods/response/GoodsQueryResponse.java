package com.gig.collide.api.goods.response;

import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.api.goods.response.data.BasicGoodsInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 商品查询响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@Schema(description = "商品查询响应")
public class GoodsQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 商品信息列表
     */
    @Schema(description = "商品信息列表")
    private List<T> goodsInfos;

    /**
     * 单个商品信息（单个查询时使用）
     */
    @Schema(description = "单个商品信息")
    private T goodsInfo;

    /**
     * 总数量
     */
    @Schema(description = "总数量")
    private Long total;

    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private Integer currentPage;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private Integer pageSize;

    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private Integer totalPages;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    // ===================== 静态构造方法 =====================

    /**
     * 创建成功的单个商品查询响应
     */
    public static <T> GoodsQueryResponse<T> success(T goodsInfo) {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setGoodsInfo(goodsInfo);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的商品列表查询响应
     */
    public static <T> GoodsQueryResponse<T> success(List<T> goodsInfos, Long total, Integer currentPage, Integer pageSize) {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setGoodsInfos(goodsInfos);
        response.setTotal(total);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        
        if (pageSize != null && pageSize > 0) {
            response.setTotalPages((int) Math.ceil((double) total / pageSize));
            response.setHasNext(currentPage * pageSize < total);
        }
        
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建成功的无分页商品列表查询响应
     */
    public static <T> GoodsQueryResponse<T> success(List<T> goodsInfos) {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setGoodsInfos(goodsInfos);
        response.setTotal((long) goodsInfos.size());
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功");
        return response;
    }

    /**
     * 创建失败响应
     */
    public static <T> GoodsQueryResponse<T> error(String errorCode, String errorMessage) {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }

    /**
     * 创建空结果响应
     */
    public static <T> GoodsQueryResponse<T> empty() {
        GoodsQueryResponse<T> response = new GoodsQueryResponse<>();
        response.setGoodsInfos(List.of());
        response.setTotal(0L);
        response.setResponseCode("SUCCESS");
        response.setResponseMessage("查询成功，暂无数据");
        return response;
    }
} 