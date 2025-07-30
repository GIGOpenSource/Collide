package com.gig.collide.order.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 订单模块缓存常量定义 - 缓存增强版
 * 对齐payment模块设计风格，提供统一的缓存配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class OrderCacheConstant {

    // =================== 订单详情缓存 ===================
    
    /**
     * 订单详情缓存名称
     */
    public static final String ORDER_DETAIL_CACHE = "order:detail";
    
    /**
     * 订单详情缓存Key（按ID）
     */
    public static final String ORDER_DETAIL_BY_ID_KEY = "'order:detail:id:' + #orderId";
    
    /**
     * 订单详情缓存Key（按订单号）
     */
    public static final String ORDER_DETAIL_BY_NO_KEY = "'order:detail:no:' + #orderNo";
    
    /**
     * 订单详情缓存过期时间（分钟）
     */
    public static final int ORDER_DETAIL_EXPIRE = 30;

    // =================== 用户订单记录缓存 ===================
    
    /**
     * 用户订单记录缓存名称
     */
    public static final String USER_ORDER_CACHE = "order:user:records";
    
    /**
     * 用户订单记录缓存Key
     */
    public static final String USER_ORDER_KEY = "'order:user:' + #request.userId + ':' + #request.pageNum + ':' + #request.pageSize + ':' + #request.status";
    
    /**
     * 用户订单记录缓存过期时间（分钟）
     */
    public static final int USER_ORDER_EXPIRE = 15;

    // =================== 订单统计缓存 ===================
    
    /**
     * 订单统计缓存名称
     */
    public static final String ORDER_STATISTICS_CACHE = "order:statistics";
    
    /**
     * 用户订单统计缓存Key
     */
    public static final String USER_ORDER_STATS_KEY = "'order:stats:user:' + #userId + ':' + #status";
    
    /**
     * 商品销量统计缓存Key
     */
    public static final String GOODS_SALES_STATS_KEY = "'order:stats:goods:' + #goodsId";
    
    /**
     * 订单统计缓存过期时间（分钟）
     */
    public static final int ORDER_STATISTICS_EXPIRE = 60;

    // =================== 订单状态缓存 ===================
    
    /**
     * 订单状态缓存名称
     */
    public static final String ORDER_STATUS_CACHE = "order:status";
    
    /**
     * 订单状态缓存Key
     */
    public static final String ORDER_STATUS_KEY = "'order:status:' + #orderId";
    
    /**
     * 订单状态缓存过期时间（分钟）
     */
    public static final int ORDER_STATUS_EXPIRE = 10;

    // =================== 商品订单关联缓存 ===================
    
    /**
     * 商品订单关联缓存名称
     */
    public static final String GOODS_ORDER_CACHE = "order:goods:relation";
    
    /**
     * 商品订单关联缓存Key
     */
    public static final String GOODS_ORDER_KEY = "'order:goods:' + #goodsId + ':' + #pageNum + ':' + #pageSize";
    
    /**
     * 商品订单关联缓存过期时间（分钟）
     */
    public static final int GOODS_ORDER_EXPIRE = 30;

    // =================== 订单支付状态缓存 ===================
    
    /**
     * 订单支付状态缓存名称
     */
    public static final String ORDER_PAY_STATUS_CACHE = "order:pay:status";
    
    /**
     * 订单支付状态缓存Key
     */
    public static final String ORDER_PAY_STATUS_KEY = "'order:pay:status:' + #orderId";
    
    /**
     * 订单支付状态缓存过期时间（分钟）
     */
    public static final int ORDER_PAY_STATUS_EXPIRE = 5;

    // =================== 订单流程缓存 ===================
    
    /**
     * 订单流程缓存名称
     */
    public static final String ORDER_WORKFLOW_CACHE = "order:workflow";
    
    /**
     * 订单流程缓存Key
     */
    public static final String ORDER_WORKFLOW_KEY = "'order:workflow:' + #orderId + ':' + #status";
    
    /**
     * 订单流程缓存过期时间（分钟）
     */
    public static final int ORDER_WORKFLOW_EXPIRE = 20;

    // =================== 缓存性能配置 ===================
    
    /**
     * 默认时间单位
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    
    /**
     * 缓存预热间隔（分钟）
     */
    public static final int CACHE_WARMUP_INTERVAL = 30;
    
    /**
     * 订单缓存容量
     */
    public static final int ORDER_CACHE_SIZE = 8000;
    
    /**
     * 最大缓存项数量
     */
    public static final int MAX_CACHE_ITEMS = 40000;

    // =================== 业务常量 ===================
    
    /**
     * 默认订单查询页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大订单查询页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 订单超时时间（分钟）
     */
    public static final int ORDER_TIMEOUT_MINUTES = 30;
    
    /**
     * 订单状态同步间隔（分钟）
     */
    public static final int ORDER_STATUS_SYNC_INTERVAL = 5;
    
    /**
     * 默认订单统计天数
     */
    public static final int DEFAULT_STATS_DAYS = 30;
    
    /**
     * 批量处理订单数量限制
     */
    public static final int BATCH_PROCESS_LIMIT = 1000;
}