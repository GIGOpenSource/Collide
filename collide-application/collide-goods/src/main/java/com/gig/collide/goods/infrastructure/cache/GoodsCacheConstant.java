package com.gig.collide.goods.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 商品模块缓存常量配置
 * 统一管理缓存键名、过期时间等配置
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-31
 */
public class GoodsCacheConstant {

    // =================== 缓存名称定义 ===================
    
    /**
     * 商品详情缓存
     */
    public static final String GOODS_DETAIL_CACHE = "goods:detail";
    
    /**
     * 商品列表缓存
     */
    public static final String GOODS_LIST_CACHE = "goods:list";
    
    /**
     * 热门商品缓存
     */
    public static final String GOODS_HOT_CACHE = "goods:hot";
    
    /**
     * 商品搜索缓存
     */
    public static final String GOODS_SEARCH_CACHE = "goods:search";
    
    /**
     * 分类商品缓存
     */
    public static final String GOODS_CATEGORY_CACHE = "goods:category";
    
    /**
     * 商家商品缓存
     */
    public static final String GOODS_SELLER_CACHE = "goods:seller";
    
    /**
     * 商品统计缓存
     */
    public static final String GOODS_STATISTICS_CACHE = "goods:statistics";
    
    /**
     * 低库存商品缓存
     */
    public static final String GOODS_LOW_STOCK_CACHE = "goods:lowstock";

    // =================== 缓存键模板 ===================
    
    /**
     * 商品详情键模板：goods:detail:{goodsId}
     */
    public static final String GOODS_DETAIL_KEY = "goods:detail:";
    
    /**
     * 商品列表键模板：goods:list:{type}:{status}:{page}:{size}
     */
    public static final String GOODS_LIST_KEY = "goods:list:";
    
    /**
     * 热门商品键模板：goods:hot:{type}:{page}:{size}
     */
    public static final String GOODS_HOT_KEY = "goods:hot:";
    
    /**
     * 商品搜索键模板：goods:search:{keyword}:{page}:{size}
     */
    public static final String GOODS_SEARCH_KEY = "goods:search:";
    
    /**
     * 分类商品键模板：goods:category:{categoryId}:{page}:{size}
     */
    public static final String GOODS_CATEGORY_KEY = "goods:category:";
    
    /**
     * 商家商品键模板：goods:seller:{sellerId}:{page}:{size}
     */
    public static final String GOODS_SELLER_KEY = "goods:seller:";

    // =================== 缓存过期时间 ===================
    
    /**
     * 商品详情缓存过期时间：30分钟
     */
    public static final int DETAIL_EXPIRE = 30;
    
    /**
     * 商品列表缓存过期时间：15分钟
     */
    public static final int LIST_EXPIRE = 15;
    
    /**
     * 热门商品缓存过期时间：60分钟
     */
    public static final int HOT_EXPIRE = 60;
    
    /**
     * 搜索结果缓存过期时间：10分钟
     */
    public static final int SEARCH_EXPIRE = 10;
    
    /**
     * 统计数据缓存过期时间：5分钟
     */
    public static final int STATISTICS_EXPIRE = 5;
    
    /**
     * 低库存商品缓存过期时间：3分钟
     */
    public static final int LOW_STOCK_EXPIRE = 3;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位：分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 防穿透配置 ===================
    
    /**
     * 空值缓存时间：1分钟（防止缓存穿透）
     */
    public static final int NULL_CACHE_EXPIRE = 1;
    
    /**
     * 随机过期时间范围：±5分钟（防止缓存雪崩）
     */
    public static final int RANDOM_EXPIRE_RANGE = 5;

    // =================== 工具方法 ===================
    
    /**
     * 构建商品详情缓存键
     */
    public static String buildDetailKey(Long goodsId) {
        return GOODS_DETAIL_KEY + goodsId;
    }
    
    /**
     * 构建商品列表缓存键
     */
    public static String buildListKey(String type, String status, int page, int size) {
        return GOODS_LIST_KEY + type + ":" + status + ":" + page + ":" + size;
    }
    
    /**
     * 构建热门商品缓存键
     */
    public static String buildHotKey(String type, int page, int size) {
        return GOODS_HOT_KEY + type + ":" + page + ":" + size;
    }
    
    /**
     * 构建搜索缓存键
     */
    public static String buildSearchKey(String keyword, int page, int size) {
        return GOODS_SEARCH_KEY + keyword + ":" + page + ":" + size;
    }
    
    /**
     * 构建分类商品缓存键
     */
    public static String buildCategoryKey(Long categoryId, int page, int size) {
        return GOODS_CATEGORY_KEY + categoryId + ":" + page + ":" + size;
    }
    
    /**
     * 构建商家商品缓存键
     */
    public static String buildSellerKey(Long sellerId, int page, int size) {
        return GOODS_SELLER_KEY + sellerId + ":" + page + ":" + size;
    }
}