package com.gig.collide.payment.infrastructure.cache;

import java.util.concurrent.TimeUnit;

/**
 * 支付模块缓存常量定义 - 缓存增强版
 * 对齐search模块设计风格，提供统一的缓存配置
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
public class PaymentCacheConstant {

    // =================== 支付详情缓存 ===================
    
    /**
     * 支付详情缓存名称
     */
    public static final String PAYMENT_DETAIL_CACHE = "payment:detail";
    
    /**
     * 支付详情缓存Key（按ID）
     */
    public static final String PAYMENT_DETAIL_BY_ID_KEY = "'payment:detail:id:' + #paymentId";
    
    /**
     * 支付详情缓存Key（按支付单号）
     */
    public static final String PAYMENT_DETAIL_BY_NO_KEY = "'payment:detail:no:' + #paymentNo";
    
    /**
     * 支付详情缓存过期时间（分钟）
     */
    public static final int PAYMENT_DETAIL_EXPIRE = 30;

    // =================== 用户支付记录缓存 ===================
    
    /**
     * 用户支付记录缓存名称
     */
    public static final String USER_PAYMENT_CACHE = "payment:user:records";
    
    /**
     * 用户支付记录缓存Key
     */
    public static final String USER_PAYMENT_KEY = "'payment:user:' + #request.userId + ':' + #request.currentPage + ':' + #request.pageSize + ':' + #request.status";
    
    /**
     * 用户支付记录缓存过期时间（分钟）
     */
    public static final int USER_PAYMENT_EXPIRE = 15;

    // =================== 支付统计缓存 ===================
    
    /**
     * 支付统计缓存名称
     */
    public static final String PAYMENT_STATISTICS_CACHE = "payment:statistics";
    
    /**
     * 用户支付统计缓存Key
     */
    public static final String USER_PAYMENT_STATS_KEY = "'payment:stats:user:' + #userId + ':' + #timeRange";
    
    /**
     * 支付方式统计缓存Key
     */
    public static final String PAYMENT_METHOD_STATS_KEY = "'payment:stats:method:' + #payMethod + ':' + #timeRange";
    
    /**
     * 支付统计缓存过期时间（分钟）
     */
    public static final int PAYMENT_STATISTICS_EXPIRE = 60;

    // =================== 订单支付状态缓存 ===================
    
    /**
     * 订单支付状态缓存名称
     */
    public static final String ORDER_PAYMENT_STATUS_CACHE = "payment:order:status";
    
    /**
     * 订单支付状态缓存Key
     */
    public static final String ORDER_PAYMENT_STATUS_KEY = "'payment:order:status:' + #orderId";
    
    /**
     * 订单支付状态缓存过期时间（分钟）
     */
    public static final int ORDER_PAYMENT_STATUS_EXPIRE = 5;

    // =================== 支付方式配置缓存 ===================
    
    /**
     * 支付方式配置缓存名称
     */
    public static final String PAYMENT_METHOD_CONFIG_CACHE = "payment:method:config";
    
    /**
     * 支付方式配置缓存Key
     */
    public static final String PAYMENT_METHOD_CONFIG_KEY = "'payment:method:config:' + #payMethod";
    
    /**
     * 支付方式配置缓存过期时间（分钟）
     */
    public static final int PAYMENT_METHOD_CONFIG_EXPIRE = 120;

    // =================== 第三方支付缓存 ===================
    
    /**
     * 第三方支付结果缓存名称
     */
    public static final String THIRD_PARTY_PAYMENT_CACHE = "payment:third:party";
    
    /**
     * 第三方支付结果缓存Key
     */
    public static final String THIRD_PARTY_PAYMENT_KEY = "'payment:third:party:' + #thirdPartyNo";
    
    /**
     * 第三方支付结果缓存过期时间（分钟）
     */
    public static final int THIRD_PARTY_PAYMENT_EXPIRE = 10;

    // =================== 支付渠道状态缓存 ===================
    
    /**
     * 支付渠道状态缓存名称
     */
    public static final String PAYMENT_CHANNEL_STATUS_CACHE = "payment:channel:status";
    
    /**
     * 支付渠道状态缓存Key
     */
    public static final String PAYMENT_CHANNEL_STATUS_KEY = "'payment:channel:status:' + #payChannel";
    
    /**
     * 支付渠道状态缓存过期时间（分钟）
     */
    public static final int PAYMENT_CHANNEL_STATUS_EXPIRE = 30;

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
     * 支付缓存容量
     */
    public static final int PAYMENT_CACHE_SIZE = 5000;
    
    /**
     * 最大缓存项数量
     */
    public static final int MAX_CACHE_ITEMS = 25000;

    // =================== 业务常量 ===================
    
    /**
     * 默认支付查询页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 最大支付查询页大小
     */
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 支付超时时间（分钟）
     */
    public static final int PAYMENT_TIMEOUT_MINUTES = 30;
    
    /**
     * 支付回调重试次数
     */
    public static final int CALLBACK_RETRY_TIMES = 3;
    
    /**
     * 默认支付统计天数
     */
    public static final int DEFAULT_STATS_DAYS = 30;
}