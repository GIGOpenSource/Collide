-- ==========================================
-- 内容模块 MySQL 8.4.1 索引优化脚本
-- 基于MySQL 8.4.1新特性进行索引优化
-- ==========================================

USE collide;

-- =================== t_content 表索引优化 ===================

-- 1. 复合索引优化（利用最左前缀原则）
CREATE INDEX idx_content_status_publish ON t_content(status, review_status, publish_time DESC);

-- 2. 作者内容复合索引
CREATE INDEX idx_author_status_publish ON t_content(author_id, status, publish_time DESC);

-- 3. 分类内容复合索引
CREATE INDEX idx_category_status_publish ON t_content(category_id, status, publish_time DESC);

-- 4. 内容类型复合索引
CREATE INDEX idx_type_status_publish ON t_content(content_type, status, publish_time DESC);

-- 5. 函数索引优化标题搜索（MySQL 8.4.1新特性）
CREATE INDEX idx_title_search ON t_content((LOWER(title)));

-- 6. 标签搜索函数索引
CREATE INDEX idx_tags_search ON t_content((JSON_EXTRACT(tags, '$')));

-- 7. 评分统计复合索引
CREATE INDEX idx_score_stats ON t_content(score_count DESC, score_total DESC);

-- 8. 热门内容复合索引
CREATE INDEX idx_hot_content ON t_content(view_count DESC, like_count DESC, publish_time DESC);

-- 9. 审核状态索引
CREATE INDEX idx_review_status ON t_content(review_status, status);

-- 10. 创建时间索引（用于最新内容）
CREATE INDEX idx_create_time ON t_content(create_time DESC);

-- =================== t_content_payment 表索引优化 ===================

-- 1. VIP相关复合索引
CREATE INDEX idx_vip_status ON t_content_payment(vip_free, vip_only, status);

-- 2. 付费类型状态复合索引
CREATE INDEX idx_payment_status ON t_content_payment(payment_type, status, create_time DESC);

-- 3. 价格范围复合索引
CREATE INDEX idx_price_range ON t_content_payment(coin_price ASC, original_price ASC);

-- 4. 试读功能复合索引
CREATE INDEX idx_trial_status ON t_content_payment(trial_enabled, status);

-- 5. 永久性复合索引
CREATE INDEX idx_permanent_status ON t_content_payment(is_permanent, status);

-- 6. 销售统计复合索引
CREATE INDEX idx_sales_stats ON t_content_payment(total_sales DESC, total_revenue DESC);

-- 7. 折扣计算函数索引
CREATE INDEX idx_discount_calc ON t_content_payment((original_price - coin_price));

-- 8. 性价比复合索引
CREATE INDEX idx_value_ratio ON t_content_payment((total_sales / NULLIF(coin_price, 0)) DESC);

-- 9. 创建时间索引
CREATE INDEX idx_create_time ON t_content_payment(create_time DESC);

-- 10. 隐藏索引（用于测试）
CREATE INDEX idx_test_hidden ON t_content_payment(content_id) INVISIBLE;

-- =================== t_content_chapter 表索引优化 ===================

-- 1. 内容章节状态复合索引
CREATE INDEX idx_content_status ON t_content_chapter(content_id, status, chapter_num ASC);

-- 2. 章节号范围查询索引
CREATE INDEX idx_chapter_range ON t_content_chapter(content_id, chapter_num ASC, status);

-- 3. 字数统计复合索引
CREATE INDEX idx_word_count ON t_content_chapter(content_id, word_count DESC);

-- 4. 创建时间索引
CREATE INDEX idx_create_time ON t_content_chapter(create_time DESC);

-- 5. 状态时间复合索引
CREATE INDEX idx_status_time ON t_content_chapter(status, create_time DESC);

-- 6. 内容字数统计函数索引
CREATE INDEX idx_content_words ON t_content_chapter(content_id, (SUM(word_count)));

-- 7. 章节标题搜索函数索引
CREATE INDEX idx_title_search ON t_content_chapter((LOWER(title)));

-- 8. 不可见索引（用于测试）
CREATE INDEX idx_test_invisible ON t_content_chapter(content_id) INVISIBLE;

-- =================== t_user_content_purchase 表索引优化 ===================

-- 1. 用户购买状态复合索引
CREATE INDEX idx_user_status ON t_user_content_purchase(user_id, status, purchase_time DESC);

-- 2. 内容购买统计复合索引
CREATE INDEX idx_content_sales ON t_user_content_purchase(content_id, status, purchase_time DESC);

-- 3. 订单相关复合索引
CREATE INDEX idx_order_info ON t_user_content_purchase(order_id, order_no, status);

-- 4. 过期时间复合索引
CREATE INDEX idx_expire_time ON t_user_content_purchase(status, expire_time ASC);

-- 5. 访问统计复合索引
CREATE INDEX idx_access_stats ON t_user_content_purchase(access_count DESC, last_access_time DESC);

-- 6. 消费金额复合索引
CREATE INDEX idx_coin_amount ON t_user_content_purchase(coin_amount DESC, purchase_time DESC);

-- 7. 折扣计算函数索引
CREATE INDEX idx_discount_calc ON t_user_content_purchase((original_price - coin_amount));

-- 8. 内容类型购买复合索引
CREATE INDEX idx_content_type ON t_user_content_purchase(user_id, content_type, purchase_time DESC);

-- 9. 作者购买复合索引
CREATE INDEX idx_author_purchase ON t_user_content_purchase(user_id, author_id, purchase_time DESC);

-- 10. 隐藏索引（用于测试）
CREATE INDEX idx_test_hidden ON t_user_content_purchase(user_id) INVISIBLE;

-- =================== 索引使用情况查询 ===================

-- 查看所有索引
SHOW INDEX FROM t_content;
SHOW INDEX FROM t_content_payment;
SHOW INDEX FROM t_content_chapter;
SHOW INDEX FROM t_user_content_purchase;

-- 查看索引使用情况
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    CARDINALITY,
    SUB_PART,
    PACKED,
    NULLABLE,
    INDEX_TYPE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
AND TABLE_NAME IN ('t_content', 't_content_payment', 't_content_chapter', 't_user_content_purchase')
ORDER BY TABLE_NAME, INDEX_NAME;

-- =================== 性能测试查询 ===================

-- 测试内容查询性能
EXPLAIN SELECT * FROM t_content 
WHERE status = 'PUBLISHED' AND review_status = 'APPROVED' 
ORDER BY publish_time DESC LIMIT 20;

-- 测试标题搜索性能
EXPLAIN SELECT * FROM t_content 
WHERE LOWER(title) LIKE '%测试%' 
AND status = 'PUBLISHED' AND review_status = 'APPROVED';

-- 测试VIP内容查询性能
EXPLAIN SELECT * FROM t_content_payment 
WHERE (vip_free = 1 OR vip_only = 1) AND status = 'ACTIVE';

-- 测试章节查询性能
EXPLAIN SELECT * FROM t_content_chapter 
WHERE content_id = 1 AND status = 'PUBLISHED' 
ORDER BY chapter_num ASC;

-- 测试购买记录查询性能
EXPLAIN SELECT * FROM t_user_content_purchase 
WHERE user_id = 1 AND status = 'ACTIVE' 
ORDER BY purchase_time DESC;

-- =================== 索引维护脚本 ===================

-- 分析表统计信息
ANALYZE TABLE t_content;
ANALYZE TABLE t_content_payment;
ANALYZE TABLE t_content_chapter;
ANALYZE TABLE t_user_content_purchase;

-- 优化表结构
OPTIMIZE TABLE t_content;
OPTIMIZE TABLE t_content_payment;
OPTIMIZE TABLE t_content_chapter;
OPTIMIZE TABLE t_user_content_purchase;

-- =================== 监控查询 ===================

-- 查看慢查询
SELECT * FROM mysql.slow_log 
WHERE start_time > DATE_SUB(NOW(), INTERVAL 1 HOUR)
ORDER BY start_time DESC;

-- 查看索引使用统计
SELECT 
    object_schema,
    object_name,
    index_name,
    count_read,
    count_write,
    count_fetch,
    count_insert,
    count_update,
    count_delete
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE object_schema = 'collide'
AND object_name IN ('t_content', 't_content_payment', 't_content_chapter', 't_user_content_purchase');

-- =================== 清理脚本（如果需要删除索引） ===================

/*
-- 删除测试索引（谨慎使用）
DROP INDEX idx_test_hidden ON t_content_payment;
DROP INDEX idx_test_invisible ON t_content_chapter;
DROP INDEX idx_test_hidden ON t_user_content_purchase;

-- 删除函数索引（如果需要）
DROP INDEX idx_title_search ON t_content;
DROP INDEX idx_tags_search ON t_content;
DROP INDEX idx_discount_calc ON t_content_payment;
DROP INDEX idx_content_words ON t_content_chapter;
DROP INDEX idx_title_search ON t_content_chapter;
DROP INDEX idx_discount_calc ON t_user_content_purchase;
*/ 