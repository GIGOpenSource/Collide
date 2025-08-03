package com.gig.collide.order.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 订单模块缓存常量配置
 * 统一管理缓存键名、过期时间等配置
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-31
 */
public class OrderCacheConstant {

    // =================== 缓存名称定义 ===================
    
    /**
     * 订单详情缓存
     */
    public static final String ORDER_DETAIL_CACHE = "order:detail";
    
    /**
     * 订单列表缓存
     */
    public static final String ORDER_LIST_CACHE = "order:list";
    
    /**
     * 用户订单缓存
     */
    public static final String USER_ORDER_CACHE = "order:user";
    
    /**
     * 商品订单缓存
     */
    public static final String GOODS_ORDER_CACHE = "order:goods";
    
    /**
     * 订单统计缓存
     */
    public static final String ORDER_STATISTICS_CACHE = "order:statistics";
    
    /**
     * 热门商品缓存
     */
    public static final String HOT_GOODS_CACHE = "order:hotgoods";
    
    /**
     * 用户购买记录缓存
     */
    public static final String USER_PURCHASE_CACHE = "order:purchase";
    
    /**
     * 营收统计缓存
     */
    public static final String REVENUE_CACHE = "order:revenue";

    // =================== 缓存键模板 ===================
    
    /**
     * 订单详情键模板：order:detail:{orderId}
     */
    public static final String ORDER_DETAIL_KEY = "order:detail:";
    
    /**
     * 订单号键模板：order:no:{orderNo}
     */
    public static final String ORDER_NO_KEY = "order:no:";
    
    /**
     * 用户订单键模板：order:user:{userId}:{status}:{page}:{size}
     */
    public static final String USER_ORDER_KEY = "order:user:";
    
    /**
     * 商品订单键模板：order:goods:{goodsId}:{page}:{size}
     */
    public static final String GOODS_ORDER_KEY = "order:goods:";
    
    /**
     * 商家订单键模板：order:seller:{sellerId}:{page}:{size}
     */
    public static final String SELLER_ORDER_KEY = "order:seller:";
    
    /**
     * 用户统计键模板：order:stats:user:{userId}
     */
    public static final String USER_STATS_KEY = "order:stats:user:";
    
    /**
     * 商品销售统计键模板：order:stats:goods:{goodsId}
     */
    public static final String GOODS_STATS_KEY = "order:stats:goods:";

    // =================== 缓存过期时间 ===================
    
    /**
     * 订单详情缓存过期时间：15分钟
     */
    public static final int DETAIL_EXPIRE = 15;
    
    /**
     * 订单列表缓存过期时间：10分钟
     */
    public static final int LIST_EXPIRE = 10;
    
    /**
     * 用户订单缓存过期时间：20分钟
     */
    public static final int USER_ORDER_EXPIRE = 20;
    
    /**
     * 统计数据缓存过期时间：30分钟
     */
    public static final int STATISTICS_EXPIRE = 30;
    
    /**
     * 热门商品缓存过期时间：60分钟
     */
    public static final int HOT_GOODS_EXPIRE = 60;
    
    /**
     * 营收统计缓存过期时间：120分钟
     */
    public static final int REVENUE_EXPIRE = 120;
    
    /**
     * 用户购买记录缓存过期时间：30分钟
     */
    public static final int PURCHASE_EXPIRE = 30;

    // =================== 缓存时间单位 ===================
    
    /**
     * 默认时间单位：分钟
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    // =================== 防穿透配置 ===================
    
    /**
     * 空值缓存时间：2分钟（防止缓存穿透）
     */
    public static final int NULL_CACHE_EXPIRE = 2;
    
    /**
     * 随机过期时间范围：±3分钟（防止缓存雪崩）
     */
    public static final int RANDOM_EXPIRE_RANGE = 3;

    // =================== 工具方法 ===================
    
    /**
     * 构建订单详情缓存键
     */
    public static String buildDetailKey(Long orderId) {
        return ORDER_DETAIL_KEY + orderId;
    }
    
    /**
     * 构建订单号缓存键
     */
    public static String buildOrderNoKey(String orderNo) {
        return ORDER_NO_KEY + orderNo;
    }
    
    /**
     * 构建用户订单缓存键
     */
    public static String buildUserOrderKey(Long userId, String status, int page, int size) {
        return USER_ORDER_KEY + userId + ":" + status + ":" + page + ":" + size;
    }
    
    /**
     * 构建商品订单缓存键
     */
    public static String buildGoodsOrderKey(Long goodsId, int page, int size) {
        return GOODS_ORDER_KEY + goodsId + ":" + page + ":" + size;
    }
    
    /**
     * 构建商家订单缓存键
     */
    public static String buildSellerOrderKey(Long sellerId, int page, int size) {
        return SELLER_ORDER_KEY + sellerId + ":" + page + ":" + size;
    }
    
    /**
     * 构建用户统计缓存键
     */
    public static String buildUserStatsKey(Long userId) {
        return USER_STATS_KEY + userId;
    }
    
    /**
     * 构建商品销售统计缓存键
     */
    public static String buildGoodsStatsKey(Long goodsId) {
        return GOODS_STATS_KEY + goodsId;
    }
    
    /**
     * 构建营收统计缓存键
     */
    public static String buildRevenueKey(String startDate, String endDate) {
        return "order:revenue:" + startDate + ":" + endDate;
    }
}