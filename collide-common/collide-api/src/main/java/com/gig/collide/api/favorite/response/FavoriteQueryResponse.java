package com.gig.collide.api.favorite.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.base.response.BaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 收藏查询响应
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
@Schema(description = "收藏查询响应")
public class FavoriteQueryResponse<T> extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 分页数据
     */
    @Schema(description = "分页数据")
    private IPage<T> page;

    /**
     * 查询总数
     */
    @Schema(description = "查询总数")
    private Long totalCount;

    /**
     * 查询条件摘要
     */
    @Schema(description = "查询条件摘要")
    private String queryConditionSummary;

    // ===================== 静态工厂方法 =====================

    /**
     * 成功响应
     *
     * @param page 分页数据
     * @return 查询响应
     */
    public static <T> FavoriteQueryResponse<T> success(IPage<T> page) {
        FavoriteQueryResponse<T> response = new FavoriteQueryResponse<>();
        response.setPage(page);
        response.setTotalCount(page.getTotal());
        response.setSuccess(true);
        return response;
    }

    /**
     * 成功响应（带查询条件摘要）
     *
     * @param page 分页数据
     * @param summary 查询条件摘要
     * @return 查询响应
     */
    public static <T> FavoriteQueryResponse<T> success(IPage<T> page, String summary) {
        FavoriteQueryResponse<T> response = new FavoriteQueryResponse<>();
        response.setPage(page);
        response.setTotalCount(page.getTotal());
        response.setQueryConditionSummary(summary);
        response.setSuccess(true);
        return response;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return 查询响应
     */
    public static <T> FavoriteQueryResponse<T> failure(String message) {
        FavoriteQueryResponse<T> response = new FavoriteQueryResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
} 