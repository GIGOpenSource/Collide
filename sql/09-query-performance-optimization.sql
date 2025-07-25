-- ==========================================
-- 数据库查询性能优化脚本
-- ==========================================

USE `collide`;

-- 1. 检查当前索引情况
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    CARDINALITY
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 2. 分析表结构
ANALYZE TABLE t_user;
ANALYZE TABLE t_user_profile;
ANALYZE TABLE t_search_history;

-- 3. 检查慢查询日志（如果启用）
-- SHOW VARIABLES LIKE 'slow_query_log%';
-- SHOW VARIABLES LIKE 'long_query_time';

-- 4. 优化用户表查询
-- 为用户名查询添加复合索引
CREATE INDEX IF NOT EXISTS idx_user_username_deleted ON t_user(username, deleted);
CREATE INDEX IF NOT EXISTS idx_user_email_deleted ON t_user(email, deleted);
CREATE INDEX IF NOT EXISTS idx_user_phone_deleted ON t_user(phone, deleted);

-- 5. 优化用户资料表
CREATE INDEX IF NOT EXISTS idx_user_profile_user_id ON t_user_profile(user_id);

-- 6. 优化搜索历史表
CREATE INDEX IF NOT EXISTS idx_search_history_user_id ON t_search_history(user_id);
CREATE INDEX IF NOT EXISTS idx_search_history_create_time ON t_search_history(create_time);
CREATE INDEX IF NOT EXISTS idx_search_history_user_create ON t_search_history(user_id, create_time);

-- 7. 检查表大小和碎片
SELECT 
    TABLE_NAME,
    ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) AS 'Size (MB)',
    TABLE_ROWS,
    DATA_FREE
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'collide'
ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- 8. 优化表（重建索引和碎片整理）
OPTIMIZE TABLE t_user;
OPTIMIZE TABLE t_user_profile;
OPTIMIZE TABLE t_search_history;

-- 9. 检查查询执行计划
EXPLAIN SELECT * FROM t_user WHERE username = 'testuser' AND deleted = 0;

-- 10. 创建性能监控视图
CREATE OR REPLACE VIEW v_query_performance AS
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    CARDINALITY,
    ROUND(CARDINALITY / TABLE_ROWS * 100, 2) AS 'Selectivity %'
FROM information_schema.STATISTICS s
JOIN information_schema.TABLES t ON s.TABLE_NAME = t.TABLE_NAME
WHERE s.TABLE_SCHEMA = 'collide' 
    AND t.TABLE_SCHEMA = 'collide'
    AND s.SEQ_IN_INDEX = 1
ORDER BY TABLE_NAME, INDEX_NAME;

-- 11. 检查当前连接数
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Max_used_connections';

-- 12. 检查查询缓存（如果启用）
SHOW STATUS LIKE 'Qcache%';

-- 13. 优化 InnoDB 设置
-- 这些需要在 my.cnf 中设置，这里只是显示当前值
SHOW VARIABLES LIKE 'innodb_buffer_pool_size';
SHOW VARIABLES LIKE 'innodb_log_file_size';
SHOW VARIABLES LIKE 'innodb_flush_log_at_trx_commit';

-- 14. 创建测试数据（如果需要）
INSERT IGNORE INTO t_user (username, password, email, nickname, phone, status, deleted, create_time, update_time)
SELECT 
    CONCAT('testuser', LPAD(seq, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa' as password,
    CONCAT('test', LPAD(seq, 6, '0'), '@example.com') as email,
    CONCAT('测试用户', seq) as nickname,
    CONCAT('138', LPAD(seq, 8, '0')) as phone,
    1 as status,
    0 as deleted,
    NOW() as create_time,
    NOW() as update_time
FROM (
    SELECT 1 as seq UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
    UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) numbers;

-- 15. 显示优化结果
SELECT '=== 索引优化完成 ===' as message;
SELECT '请检查上面的索引创建结果' as note; 