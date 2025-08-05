-- ==========================================
-- 订单模块 MySQL 8.0/8.4 索引优化
-- 基于OrderMapper.xml查询模式的高性能索引设计
-- ==========================================

USE collide;

-- ==========================================
-- t_order 表索引优化
-- ==========================================

-- 基础索引（已在表定义中包含）
-- PRIMARY KEY (`id`)
-- UNIQUE KEY `uk_order_no` (`order_no`)
-- KEY `idx_user_id` (`user_id`)
-- KEY `idx_goods_id` (`goods_id`)  
-- KEY `idx_goods_type` (`goods_type`)
-- KEY `idx_payment_mode` (`payment_mode`)
-- KEY `idx_status` (`status`)
-- KEY `idx_pay_status` (`pay_status`)
-- KEY `idx_content_id` (`content_id`)

-- ==========================================
-- 复合索引优化（MySQL 8.0特性）
-- ==========================================

-- 用户订单查询优化（selectByUserId）
ALTER TABLE `t_order` 
ADD INDEX `idx_user_status_time_desc` (`user_id`, `status`, `create_time` DESC) 
COMMENT 'MySQL 8.0 descending index：用户订单查询（按创建时间降序）';

-- 商品类型订单查询优化（selectByGoodsType）
ALTER TABLE `t_order` 
ADD INDEX `idx_goods_type_status_time_desc` (`goods_type`, `status`, `create_time` DESC) 
COMMENT 'MySQL 8.0 descending index：商品类型订单查询';

-- 支付模式查询优化（selectByPaymentMode）
ALTER TABLE `t_order` 
ADD INDEX `idx_payment_mode_pay_status_time_desc` (`payment_mode`, `pay_status`, `create_time` DESC) 
COMMENT 'MySQL 8.0 descending index：支付模式查询优化';

-- 超时订单查询优化（selectTimeoutOrders）
ALTER TABLE `t_order` 
ADD INDEX `idx_timeout_orders` (`status`, `pay_status`, `create_time` ASC) 
COMMENT '超时订单查询优化：pending + unpaid + 时间升序';

-- 时间范围查询优化（selectByTimeRange）
ALTER TABLE `t_order` 
ADD INDEX `idx_time_range_status` (`create_time` DESC, `status`) 
COMMENT 'MySQL 8.0 descending index：时间范围查询优化';

-- 用户金币消费订单优化（selectUserCoinOrders）
ALTER TABLE `t_order` 
ADD INDEX `idx_user_coin_paid_time` (`user_id`, `payment_mode`, `pay_status`, `pay_time` DESC) 
COMMENT 'MySQL 8.0 descending index：用户金币消费查询';

-- 用户充值订单优化（selectUserRechargeOrders）
ALTER TABLE `t_order` 
ADD INDEX `idx_user_recharge_paid_time` (`user_id`, `goods_type`, `pay_status`, `pay_time` DESC) 
COMMENT 'MySQL 8.0 descending index：用户充值订单查询';

-- 最近购买记录优化（selectUserRecentOrders）
ALTER TABLE `t_order` 
ADD INDEX `idx_user_paid_time_desc` (`user_id`, `pay_status`, `pay_time` DESC) 
COMMENT 'MySQL 8.0 descending index：用户最近购买记录';

-- ==========================================
-- 统计查询优化索引
-- ==========================================

-- 用户订单统计优化（selectUserOrderStatistics）
ALTER TABLE `t_order` 
ADD INDEX `idx_user_stats_covering` (`user_id`, `status`, `pay_status`, `payment_mode`, `final_amount`, `coin_cost`) 
COMMENT 'Covering index：用户订单统计（避免回表）';

-- 商品销售统计优化（selectGoodsSalesStatistics）
ALTER TABLE `t_order` 
ADD INDEX `idx_goods_sales_covering` (`goods_id`, `pay_status`, `quantity`, `payment_mode`, `final_amount`) 
COMMENT 'Covering index：商品销售统计（避免回表）';

-- 商品类型统计优化（selectOrderStatisticsByType）
ALTER TABLE `t_order` 
ADD INDEX `idx_type_stats_covering` (`pay_status`, `goods_type`, `payment_mode`, `status`, `quantity`, `final_amount`, `coin_cost`) 
COMMENT 'Covering index：商品类型统计查询';

-- 热门商品统计优化（selectHotGoods）
ALTER TABLE `t_order` 
ADD INDEX `idx_hot_goods_covering` (`pay_status`, `goods_id`, `goods_name`, `goods_type`, `quantity`, `payment_mode`, `final_amount`) 
COMMENT 'Covering index：热门商品统计（避免回表）';

-- ==========================================
-- 日期相关查询优化（MySQL 8.0函数索引）
-- ==========================================

-- 日营收统计优化（selectDailyRevenue）
ALTER TABLE `t_order` 
ADD INDEX `idx_pay_date_func` ((DATE(`pay_time`)), `pay_status`, `payment_mode`, `final_amount`, `coin_cost`) 
COMMENT 'MySQL 8.0 函数索引：日营收统计查询';

-- 月份统计函数索引
ALTER TABLE `t_order` 
ADD INDEX `idx_pay_month_func` ((DATE_FORMAT(`pay_time`, '%Y-%m')), `pay_status`) 
COMMENT 'MySQL 8.0 函数索引：月份统计查询';

-- ==========================================
-- 搜索查询优化
-- ==========================================

-- 订单搜索优化（searchOrders）- 需要结合全文索引
-- 注意：由于搜索字段较短且为精确匹配，暂不使用全文索引，依赖现有LIKE查询

-- 订单号前缀搜索优化
ALTER TABLE `t_order` 
ADD INDEX `idx_order_no_prefix` (`order_no`(10), `create_time` DESC) 
COMMENT '订单号前缀搜索优化（前10个字符）';

-- ==========================================
-- 计数查询优化
-- ==========================================

-- 商品订单计数优化（countByGoodsId）
ALTER TABLE `t_order` 
ADD INDEX `idx_goods_count_covering` (`goods_id`, `status`) 
COMMENT 'Covering index：商品订单计数查询';

-- 用户订单计数优化（countByUserId）
ALTER TABLE `t_order` 
ADD INDEX `idx_user_count_covering` (`user_id`, `status`) 
COMMENT 'Covering index：用户订单计数查询';

-- ==========================================
-- 业务特定优化索引
-- ==========================================

-- 支付状态更新优化（updatePaymentInfo）
ALTER TABLE `t_order` 
ADD INDEX `idx_payment_update` (`id`, `pay_status`, `pay_method`, `pay_time`) 
COMMENT 'Covering index：支付信息更新优化';

-- 批量状态更新优化（batchUpdateStatus）
-- 主键索引已足够，无需额外索引

-- 商家订单查询优化（selectBySellerId）
-- 注意：此查询违反去连表化原则，建议在Order表中添加seller_id冗余字段
ALTER TABLE `t_order` 
ADD INDEX `idx_goods_seller_subquery` (`goods_id`, `status`, `create_time` DESC) 
COMMENT '商家订单子查询优化（临时方案，建议添加seller_id冗余字段）';

-- ==========================================
-- 性能监控索引
-- ==========================================

-- 系统性能监控
ALTER TABLE `t_order` 
ADD INDEX `idx_performance_monitor` (`create_time` DESC, `status`, `payment_mode`, `pay_status`) 
COMMENT 'MySQL 8.0 descending index：系统性能监控查询';

-- ==========================================
-- 验证索引创建结果
-- ==========================================

-- 查看 t_order 表的所有索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    INDEX_TYPE,
    NON_UNIQUE,
    COLUMN_NAME,
    INDEX_COMMENT
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
    AND TABLE_NAME = 't_order'
    AND INDEX_NAME != 'PRIMARY'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- ==========================================
-- 索引使用建议
-- ==========================================

/*
MySQL 8.0/8.4 订单模块索引优化特点：

1. 降序索引 (Descending Index)：
   - 专门为 ORDER BY ... DESC 查询优化
   - 如：idx_user_status_time_desc, idx_time_range_status

2. 函数索引 (Functional Index)：
   - 基于日期函数的索引，优化日期统计查询
   - 如：idx_pay_date_func, idx_pay_month_func

3. 覆盖索引 (Covering Index)：
   - 包含查询所需的所有列，避免回表查询
   - 如：idx_user_stats_covering, idx_goods_sales_covering

4. 复合索引策略：
   - 遵循最左前缀原则
   - 高选择性字段在前，排序字段在后
   - 查询频率高的组合优先创建

5. 业务优化建议：
   - selectBySellerId：建议在Order表添加seller_id冗余字段
   - 搜索功能：考虑使用Elasticsearch等专门搜索引擎
   - 统计查询：考虑使用数据仓库或缓存层

6. 索引维护：
   - 定期使用 ANALYZE TABLE t_order 更新统计信息
   - 监控慢查询日志，持续优化索引策略
   - 避免过度索引，平衡读写性能

注意事项：
- 索引会增加写入成本，需要平衡读写性能
- 复合索引的字段顺序很重要，需要根据实际查询模式调整
- MySQL 8.0的新特性需要确保数据库版本支持
*/