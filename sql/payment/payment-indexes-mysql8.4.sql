-- ==========================================
-- 支付模块 MySQL 8.0/8.4 索引优化
-- 基于大白鲨支付业务场景的高性能索引设计
-- ==========================================

USE collide;

-- ==========================================
-- t_payment_channel 表索引优化
-- ==========================================

-- 基础索引（已在表定义中包含）
-- PRIMARY KEY (`id`)
-- UNIQUE KEY `uk_channel_code` (`channel_code`)
-- KEY `idx_provider_status` (`provider`, `status`)
-- KEY `idx_priority` (`priority` DESC)

-- 补充复合索引
ALTER TABLE `t_payment_channel` 
ADD INDEX `idx_status_priority_limit` (`status`, `priority` DESC, `single_limit` DESC) 
COMMENT '渠道选择优化：状态+优先级+限额';

ALTER TABLE `t_payment_channel` 
ADD INDEX `idx_provider_type_status` (`provider`, `channel_type`, `status`) 
COMMENT '按提供商和类型筛选可用渠道';

-- ==========================================
-- t_payment_order 表索引优化
-- ==========================================

-- 基础索引（已在表定义中包含）
-- PRIMARY KEY (`id`)
-- UNIQUE KEY `uk_order_no` (`order_no`)
-- UNIQUE KEY `uk_platform_order` (`platform_order_no`)
-- KEY `idx_user_status` (`user_id`, `status`)
-- KEY `idx_channel_status` (`channel_code`, `status`)
-- KEY `idx_player_id` (`player_id`) -- 注意：player_id可能为NULL
-- KEY `idx_create_time` (`create_time` DESC)
-- KEY `idx_pay_time` (`pay_time` DESC)

-- 核心业务复合索引
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_user_channel_status_time` (`user_id`, `channel_code`, `status`, `create_time` DESC) 
COMMENT '用户支付记录查询优化（covering index）';

ALTER TABLE `t_payment_order` 
ADD INDEX `idx_status_amount_time` (`status`, `amount` DESC, `create_time` DESC) 
COMMENT '按状态和金额范围查询优化';

ALTER TABLE `t_payment_order` 
ADD INDEX `idx_channel_paytype_status` (`channel_code`, `pay_type`, `status`) 
COMMENT '渠道支付类型统计优化';

-- 时间范围查询优化（降序索引 - MySQL 8.0特性）
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_pay_time_desc_status` (`pay_time` DESC, `status`) 
COMMENT '支付时间降序查询优化（MySQL 8.0 descending index）';

ALTER TABLE `t_payment_order` 
ADD INDEX `idx_create_time_desc_covering` (`create_time` DESC, `user_id`, `status`, `amount`) 
COMMENT '订单列表查询covering index（MySQL 8.0 descending index）';

-- 玩家信息查询优化（注意：玩家信息字段可能为NULL）
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_player_device_ip` (`player_id`, `device_id`, `player_ip`) 
COMMENT '玩家设备信息查询优化（风控需要，注意NULL值处理）';

-- 金额范围查询优化
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_amount_range_status` (`amount` ASC, `status`, `channel_code`) 
COMMENT '金额范围查询优化';

-- 过期订单清理优化
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_expire_time_status` (`expire_time` ASC, `status`) 
COMMENT '过期订单清理查询优化';

-- 回调时间查询优化
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_notify_time_status` (`notify_time` DESC, `status`) 
COMMENT '回调处理时间查询优化';

-- ==========================================
-- t_payment_notify_log 表索引优化
-- ==========================================

-- 基础索引（已在表定义中包含）
-- PRIMARY KEY (`id`)
-- KEY `idx_order_no` (`order_no`)
-- KEY `idx_platform_order` (`platform_order_no`)
-- KEY `idx_channel_status` (`channel_code`, `process_status`)
-- KEY `idx_notify_time` (`notify_time` DESC)

-- 回调处理优化索引
ALTER TABLE `t_payment_notify_log` 
ADD INDEX `idx_process_status_retry` (`process_status`, `retry_times`, `notify_time` DESC) 
COMMENT '回调重试查询优化';

ALTER TABLE `t_payment_notify_log` 
ADD INDEX `idx_order_channel_time` (`order_no`, `channel_code`, `notify_time` DESC) 
COMMENT '订单回调历史查询优化';

-- 签名验证失败查询优化
ALTER TABLE `t_payment_notify_log` 
ADD INDEX `idx_sign_verify_time` (`sign_verify`, `notify_time` DESC) 
COMMENT '签名验证失败记录查询';

-- 回调类型统计优化
ALTER TABLE `t_payment_notify_log` 
ADD INDEX `idx_notify_type_channel_status` (`notify_type`, `channel_code`, `process_status`) 
COMMENT '回调类型统计查询优化';

-- ==========================================
-- t_payment_statistics 表索引优化  
-- ==========================================

-- 基础索引（已在表定义中包含）
-- PRIMARY KEY (`id`)
-- UNIQUE KEY `uk_stat_channel_type_date` (`stat_date`, `channel_code`, `pay_type`)
-- KEY `idx_date_channel` (`stat_date` DESC, `channel_code`)

-- 统计报表查询优化
ALTER TABLE `t_payment_statistics` 
ADD INDEX `idx_date_desc_covering` (`stat_date` DESC, `channel_code`, `pay_type`, `success_rate`, `total_amount`) 
COMMENT '统计报表covering index（MySQL 8.0 descending index）';

-- 渠道成功率排序优化
ALTER TABLE `t_payment_statistics` 
ADD INDEX `idx_date_success_rate` (`stat_date`, `success_rate` DESC, `channel_code`) 
COMMENT '按成功率排序渠道优化';

-- 金额统计优化
ALTER TABLE `t_payment_statistics` 
ADD INDEX `idx_date_amount_desc` (`stat_date`, `success_amount` DESC, `total_amount` DESC) 
COMMENT '金额统计排序优化（MySQL 8.0 descending index）';

-- ==========================================
-- 函数索引（MySQL 8.0新特性）
-- ==========================================

-- 支付订单表的日期函数索引
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_create_date_func` ((DATE(`create_time`)), `status`, `channel_code`) 
COMMENT 'MySQL 8.0 函数索引：按创建日期统计';

ALTER TABLE `t_payment_order` 
ADD INDEX `idx_pay_date_func` ((DATE(`pay_time`)), `status`) 
COMMENT 'MySQL 8.0 函数索引：按支付日期统计';

-- 金额函数索引（用于金额区间统计）
ALTER TABLE `t_payment_order` 
ADD INDEX `idx_amount_range_func` ((
    CASE 
        WHEN `amount` <= 100 THEN 'small'
        WHEN `amount` <= 1000 THEN 'medium' 
        WHEN `amount` <= 10000 THEN 'large'
        ELSE 'huge' 
    END
), `status`, `channel_code`) 
COMMENT 'MySQL 8.0 函数索引：金额区间分类';

-- ==========================================
-- 验证索引创建结果
-- ==========================================

-- 查看 t_payment_order 表的索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    INDEX_TYPE,
    NON_UNIQUE,
    COLUMN_NAME,
    INDEX_COMMENT
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
    AND TABLE_NAME = 't_payment_order'
    AND INDEX_NAME != 'PRIMARY'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 查看 t_payment_channel 表的索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    INDEX_TYPE,
    NON_UNIQUE,
    COLUMN_NAME,
    INDEX_COMMENT
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
    AND TABLE_NAME = 't_payment_channel'
    AND INDEX_NAME != 'PRIMARY'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- ==========================================
-- 性能优化建议
-- ==========================================

/*
MySQL 8.0/8.4 索引优化特点总结：

1. 降序索引 (Descending Index)：
   - 用于 ORDER BY ... DESC 查询优化
   - 如：idx_create_time_desc_covering

2. 函数索引 (Functional Index)：
   - 基于表达式或函数创建索引
   - 如：DATE(create_time) 的日期函数索引

3. Covering Index（覆盖索引）：
   - 索引包含查询所需的所有列
   - 避免回表查询，提升性能

4. 复合索引优化：
   - 遵循最左前缀原则
   - 区分度高的列放在前面
   - 经常一起查询的列组合索引

5. 索引提示：
   - 在业务代码中可以使用 FORCE INDEX 强制使用特定索引
   - 通过 EXPLAIN 分析查询计划

注意事项：
- 索引会增加写入成本，需要平衡读写性能
- 定期使用 ANALYZE TABLE 更新统计信息
- 监控慢查询日志，持续优化索引策略
*/