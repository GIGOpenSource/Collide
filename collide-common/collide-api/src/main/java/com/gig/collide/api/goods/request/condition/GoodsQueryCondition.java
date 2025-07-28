package com.gig.collide.api.goods.request.condition;

import com.gig.collide.api.goods.constant.GoodsStatus;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.api.goods.constant.GoodsSortType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品查询条件基类
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class GoodsQueryCondition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品类型
     */
    private GoodsType type;

    /**
     * 商品状态
     */
    private GoodsStatus status;

    /**
     * 是否推荐
     */
    private Boolean recommended;

    /**
     * 是否热门
     */
    private Boolean hot;

    /**
     * 最小价格
     */
    private BigDecimal minPrice;

    /**
     * 最大价格
     */
    private BigDecimal maxPrice;

    /**
     * 是否有库存
     */
    private Boolean inStock;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建时间范围 - 开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间范围 - 结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 排序类型
     */
    private GoodsSortType sortType = GoodsSortType.CREATE_TIME;

    /**
     * 是否降序
     */
    private Boolean descending = true;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 20;

    /**
     * 关键字搜索（商品名称或描述）
     */
    private String keyword;

    // ===================== 便捷方法 =====================

    /**
     * 设置价格范围
     */
    public GoodsQueryCondition priceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        return this;
    }

    /**
     * 设置时间范围
     */
    public GoodsQueryCondition timeRange(LocalDateTime start, LocalDateTime end) {
        this.createTimeStart = start;
        this.createTimeEnd = end;
        return this;
    }

    /**
     * 设置排序
     */
    public GoodsQueryCondition orderBy(GoodsSortType sortType, Boolean descending) {
        this.sortType = sortType;
        this.descending = descending;
        return this;
    }

    /**
     * 设置分页
     */
    public GoodsQueryCondition page(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        return this;
    }
} 