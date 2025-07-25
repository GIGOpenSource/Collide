-- ==========================================
-- Dubbo 超时问题数据库性能优化脚本
-- ==========================================

USE `collide`;

-- ==========================================
-- 1. 优化用户表查询性能
-- ==========================================

-- 检查并优化用户名查询索引
-- 确保用户名查询使用唯一索引，避免全表扫描
EXPLAIN SELECT * FROM t_user WHERE username = 'test_user' AND deleted = 0;

-- 优化用户表统计信息
ANALYZE TABLE t_user;

-- 检查用户表数据分布
SELECT
    COUNT(*) as total_users,
    COUNT(CASE WHEN deleted = 0 THEN 1 END) as active_users,
    COUNT(CASE WHEN deleted = 1 THEN 1 END) as deleted_users
FROM t_user;

-- ==========================================
-- 2. 优化数据库连接池配置
-- ==========================================

-- 检查当前数据库连接数
SHOW STATUS LIKE 'Threads_connected';
SHOW STATUS LIKE 'Max_used_connections';

-- 检查慢查询日志
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- 启用慢查询日志（如果需要）
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 1;

-- ==========================================
-- 3. 优化查询语句
-- ==========================================

-- 创建优化的用户名查询存储过程
DELIMITER $$

CREATE PROCEDURE IF NOT EXISTS `GetUserByUsername`(IN p_username VARCHAR(50))
BEGIN
    -- 使用索引优化的查询
SELECT
    id, username, nickname, avatar, email, phone,
    password_hash, salt, role, status,
    last_login_time, create_time, update_time
FROM t_user
WHERE username = p_username
  AND deleted = 0
    LIMIT 1;
END$$

DELIMITER ;

-- ==========================================
-- 4. 添加性能监控视图
-- ==========================================

-- 创建用户查询性能监控视图
CREATE OR REPLACE VIEW v_user_query_performance AS
SELECT
    'username_query' as query_type,
    COUNT(*) as total_queries,
    AVG(TIMESTAMPDIFF(MICROSECOND, start_time, end_time)) as avg_response_time_microseconds,
    MAX(TIMESTAMPDIFF(MICROSECOND, start_time, end_time)) as max_response_time_microseconds,
    MIN(TIMESTAMPDIFF(MICROSECOND, start_time, end_time)) as min_response_time_microseconds
FROM (
         -- 这里可以添加实际的查询性能统计表
         SELECT NOW() as start_time, NOW() as end_time
         FROM t_user
         WHERE username = 'test_user'
             LIMIT 1
     ) as performance_data;

-- ==========================================
-- 5. 数据库配置优化建议
-- ==========================================

-- 检查并优化 InnoDB 配置
SHOW VARIABLES LIKE 'innodb_buffer_pool_size';
SHOW VARIABLES LIKE 'innodb_log_file_size';
SHOW VARIABLES LIKE 'innodb_flush_log_at_trx_commit';

-- 检查查询缓存配置
SHOW VARIABLES LIKE 'query_cache_type';
SHOW VARIABLES LIKE 'query_cache_size';

-- ==========================================
-- 6. 创建性能测试数据
-- ==========================================

-- 插入测试用户数据（用于性能测试）
INSERT IGNORE INTO t_user (username, nickname, email, password_hash, salt, role, status, deleted) VALUES
('perf_test_001', '性能测试用户1', 'perf1@test.com', 'test_hash', 'test_salt', 'user', 'active', 0),
('perf_test_002', '性能测试用户2', 'perf2@test.com', 'test_hash', 'test_salt', 'user', 'active', 0),
('perf_test_003', '性能测试用户3', 'perf3@test.com', 'test_hash', 'test_salt', 'user', 'active', 0),
('perf_test_004', '性能测试用户4', 'perf4@test.com', 'test_hash', 'test_salt', 'user', 'active', 0),
('perf_test_005', '性能测试用户5', 'perf5@test.com', 'test_hash', 'test_salt', 'user', 'active', 0);

-- ==========================================
-- 7. 性能优化建议
-- ==========================================

/*
性能优化建议：

1. 数据库层面：
   - 确保 innodb_buffer_pool_size 设置为可用内存的 70-80%
   - 启用 query_cache（如果适用）
   - 优化 innodb_flush_log_at_trx_commit 设置

2. 应用层面：
   - 增加 Dubbo 超时时间（已修改为 60 秒）
   - 添加重试机制（已配置重试 2 次）
   - 使用连接池优化数据库连接

3. 监控层面：
   - 启用慢查询日志
   - 监控数据库连接数
   - 定期分析查询性能

4. 缓存层面：
   - 对热点用户数据进行 Redis 缓存
   - 实现用户名到用户ID的映射缓存
   - 使用本地缓存减少数据库访问

5. 代码层面：
   - 优化查询语句，只查询必要字段
   - 使用批量查询减少网络往返
   - 实现异步处理减少响应时间
*/

-- ==========================================
-- 8. 清理测试数据
-- ==========================================

-- 清理性能测试数据（可选）
-- DELETE FROM t_user WHERE username LIKE 'perf_test_%';

-- 显示优化结果
SELECT 'Database performance optimization completed' as status;