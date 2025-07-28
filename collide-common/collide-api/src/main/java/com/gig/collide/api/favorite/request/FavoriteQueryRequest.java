package com.gig.collide.api.favorite.request;

import com.gig.collide.api.favorite.constant.FavoriteSortType;
import com.gig.collide.api.favorite.request.condition.FavoriteQueryCondition;
import com.gig.collide.base.request.BaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

/**
 * 收藏查询请求
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
@Schema(description = "收藏查询请求")
public class FavoriteQueryRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    /**
     * 查询条件列表
     */
    @Valid
    @Schema(description = "查询条件列表")
    private List<FavoriteQueryCondition> conditions;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "FAVORITE_TIME")
    private FavoriteSortType sortType;

    /**
     * 是否升序排序
     */
    @Schema(description = "是否升序排序", example = "false")
    private Boolean ascending = false;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小必须大于0")
    @Max(value = 100, message = "页大小不能超过100")
    @Schema(description = "页大小", example = "20")
    private Integer pageSize = 20;

    /**
     * 是否包含已删除数据
     */
    @Schema(description = "是否包含已删除数据", example = "false")
    private Boolean includeDeleted = false;

    /**
     * 是否只返回统计信息
     */
    @Schema(description = "是否只返回统计信息", example = "false")
    private Boolean countOnly = false;
} 