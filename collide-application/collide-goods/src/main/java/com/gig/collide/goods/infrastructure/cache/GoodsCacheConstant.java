package com.gig.collide.goods.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 商品模块缓存常量定义 - 缓存增强版
 * 对齐like模块设计风格，提供统一的缓存配置
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class GoodsCacheConstant {

    // =================== 商品详情缓存 ===================

    /**
     * 商品详情缓存名称
     */
    public static final String GOODS_DETAIL_CACHE = "goods:detail";

    /**
     * 商品详情缓存Key
     */
    public static final String GOODS_DETAIL_BY_ID_KEY = "'goods:detail:id:' + #goodsId";

    /**
     * 商品详情缓存过期时间（分钟）
     */
    public static final int GOODS_DETAIL_EXPIRE = 30;

    // =================== 商品列表缓存 ===================

    /**
     * 商品列表缓存名称
     */
    public static final String GOODS_LIST_CACHE = "goods:list";

    /**
     * 商品分页列表缓存Key
     */
    public static final String GOODS_LIST_KEY = "'goods:list:' + #request.pageNum + ':' + #request.pageSize + ':' + #request.sellerId + ':' + #request.categoryId + ':' + #request.status";

    /**
     * 商品列表缓存过期时间（分钟）
     */
    public static final int GOODS_LIST_EXPIRE = 15;

    // =================== 商品分类缓存 ===================

    /**
     * 商品分类缓存名称
     */
    public static final String GOODS_CATEGORY_CACHE = "goods:category";

    /**
     * 分类下商品列表缓存Key
     */
    public static final String CATEGORY_GOODS_KEY = "'goods:category:' + #categoryId + ':' + #pageNum + ':' + #pageSize";

    /**
     * 商品分类缓存过期时间（分钟）
     */
    public static final int GOODS_CATEGORY_EXPIRE = 60;

    // =================== 商品搜索缓存 ===================

    /**
     * 商品搜索缓存名称
     */
    public static final String GOODS_SEARCH_CACHE = "goods:search";

    /**
     * 搜索结果缓存Key
     */
    public static final String GOODS_SEARCH_KEY = "'goods:search:' + #request.keyword + ':' + #request.pageNum + ':' + #request.pageSize + ':' + #request.categoryId + ':' + #request.priceMin + ':' + #request.priceMax";

    /**
     * 搜索结果缓存过期时间（分钟）
     */
    public static final int GOODS_SEARCH_EXPIRE = 10;

    // =================== 商品统计缓存 ===================

    /**
     * 商品统计缓存名称
     */
    public static final String GOODS_STATISTICS_CACHE = "goods:statistics";

    /**
     * 商家商品统计缓存Key
     */
    public static final String SELLER_GOODS_STATS_KEY = "'goods:stats:seller:' + #sellerId";

    /**
     * 分类商品统计缓存Key
     */
    public static final String CATEGORY_GOODS_STATS_KEY = "'goods:stats:category:' + #categoryId";

    /**
     * 商品统计缓存过期时间（分钟）
     */
    public static final int GOODS_STATISTICS_EXPIRE = 30;

    // =================== 库存管理缓存 ===================

    /**
     * 库存管理缓存名称
     */
    public static final String GOODS_INVENTORY_CACHE = "goods:inventory";

    /**
     * 商品库存缓存Key
     */
    public static final String GOODS_INVENTORY_KEY = "'goods:inventory:' + #goodsId";

    /**
     * 批量库存缓存Key
     */
    public static final String BATCH_INVENTORY_KEY = "'goods:inventory:batch:' + #goodsIds.size()";

    /**
     * 库存缓存过期时间（分钟）
     */
    public static final int GOODS_INVENTORY_EXPIRE = 5;

    // =================== 商品状态缓存 ===================

    /**
     * 商品状态缓存名称
     */
    public static final String GOODS_STATUS_CACHE = "goods:status";

    /**
     * 商品状态缓存Key
     */
    public static final String GOODS_STATUS_KEY = "'goods:status:' + #goodsId";

    /**
     * 状态下商品列表缓存Key
     */
    public static final String STATUS_GOODS_LIST_KEY = "'goods:status:list:' + #status + ':' + #pageNum + ':' + #pageSize";

    /**
     * 商品状态缓存过期时间（分钟）
     */
    public static final int GOODS_STATUS_EXPIRE = 20;

    // =================== 商家商品缓存 ===================

    /**
     * 商家商品缓存名称
     */
    public static final String SELLER_GOODS_CACHE = "goods:seller";

    /**
     * 商家商品列表缓存Key
     */
    public static final String SELLER_GOODS_LIST_KEY = "'goods:seller:' + #sellerId + ':' + #pageNum + ':' + #pageSize + ':' + #status";

    /**
     * 商家商品数量缓存Key
     */
    public static final String SELLER_GOODS_COUNT_KEY = "'goods:seller:count:' + #sellerId + ':' + #status";

    /**
     * 商家商品缓存过期时间（分钟）
     */
    public static final int SELLER_GOODS_EXPIRE = 25;

    // =================== 热门商品缓存 ===================

    /**
     * 热门商品缓存名称
     */
    public static final String HOT_GOODS_CACHE = "goods:hot";

    /**
     * 热门商品列表缓存Key
     */
    public static final String HOT_GOODS_LIST_KEY = "'goods:hot:' + #categoryId + ':' + #limit";

    /**
     * 推荐商品缓存Key
     */
    public static final String RECOMMEND_GOODS_KEY = "'goods:recommend:' + #userId + ':' + #limit";

    /**
     * 热门商品缓存过期时间（分钟）
     */
    public static final int HOT_GOODS_EXPIRE = 60;

    // =================== 商品操作缓存 ===================

    /**
     * 商品操作记录缓存名称
     */
    public static final String GOODS_OPERATION_CACHE = "goods:operation";

    /**
     * 商品创建操作缓存Key
     */
    public static final String GOODS_CREATE_OP_KEY = "'goods:op:create:' + #sellerId + ':' + #name";

    /**
     * 商品更新操作缓存Key
     */
    public static final String GOODS_UPDATE_OP_KEY = "'goods:op:update:' + #goodsId";

    /**
     * 商品操作缓存过期时间（分钟）
     */
    public static final int GOODS_OPERATION_EXPIRE = 3;

    // =================== 缓存性能配置 ===================

    /**
     * 默认时间单位
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    /**
     * 缓存预热间隔（分钟）
     */
    public static final int CACHE_WARMUP_INTERVAL = 15;

    /**
     * 商品缓存容量
     */
    public static final int GOODS_CACHE_SIZE = 10000;

    /**
     * 最大缓存项数量
     */
    public static final int MAX_CACHE_ITEMS = 50000;

    // =================== 业务常量 ===================

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 批量操作最大数量
     */
    public static final int MAX_BATCH_SIZE = 500;

    /**
     * 商品状态：上架
     */
    public static final String GOODS_STATUS_ACTIVE = "active";

    /**
     * 商品状态：下架
     */
    public static final String GOODS_STATUS_INACTIVE = "inactive";

    /**
     * 商品状态：草稿
     */
    public static final String GOODS_STATUS_DRAFT = "draft";

    /**
     * 商品状态：已删除
     */
    public static final String GOODS_STATUS_DELETED = "deleted";

    /**
     * 热门商品阈值
     */
    public static final int HOT_GOODS_THRESHOLD = 100;

    /**
     * 默认统计时间范围（天）
     */
    public static final int DEFAULT_STATS_DAYS = 7;

    /**
     * 库存预警阈值
     */
    public static final int INVENTORY_WARNING_THRESHOLD = 10;
}