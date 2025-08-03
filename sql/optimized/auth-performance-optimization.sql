-- ==========================================
-- 认证系统性能优化 SQL
-- 针对登录注册5-7秒延迟问题的索引优化
-- ==========================================

USE collide;

-- =================== 索引优化 ===================

-- 优化用户表索引，提升登录查询性能
-- 复合索引：用户名+状态（避免回表查询）
ALTER TABLE `t_user` ADD INDEX `idx_username_status` (`username`, `status`);

-- 优化登录相关查询索引
ALTER TABLE `t_user` ADD INDEX `idx_status_create_time` (`status`, `create_time`);

-- 分析现有索引使用情况
ANALYZE TABLE `t_user`;

-- =================== 查询性能测试 ===================

-- 测试用户名查询性能（优化前后对比）
EXPLAIN SELECT id, username, password_hash, role, status 
FROM t_user 
WHERE username = 'test_user' AND status != 'deleted';

-- 测试登录查询性能
EXPLAIN SELECT id, username, nickname, avatar, role, status, invite_code, inviter_id, invited_count,
       last_login_time, login_count, create_time, update_time
FROM t_user 
WHERE username = 'test_user' AND status != 'deleted';

-- =================== 存储过程优化 ===================

-- 创建高性能登录验证存储过程（一次性操作）
DROP PROCEDURE IF EXISTS `fast_user_login`$$
DELIMITER $$
CREATE PROCEDURE `fast_user_login`(
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255),
    OUT p_user_id BIGINT,
    OUT p_login_success TINYINT
)
BEGIN
    DECLARE v_user_id BIGINT DEFAULT NULL;
    DECLARE v_stored_hash VARCHAR(255) DEFAULT NULL;
    DECLARE v_status VARCHAR(20) DEFAULT NULL;
    
    -- 查询用户信息（使用复合索引）
    SELECT id, password_hash, status
    INTO v_user_id, v_stored_hash, v_status
    FROM t_user
    WHERE username = p_username AND status != 'deleted'
    LIMIT 1;
    
    -- 验证用户存在且密码匹配
    IF v_user_id IS NOT NULL AND v_stored_hash = p_password_hash AND v_status = 'active' THEN
        -- 更新登录信息
        UPDATE t_user 
        SET last_login_time = NOW(),
            login_count = login_count + 1,
            update_time = NOW()
        WHERE id = v_user_id;
        
        SET p_user_id = v_user_id;
        SET p_login_success = 1;
    ELSE
        SET p_user_id = NULL;
        SET p_login_success = 0;
    END IF;
END$$
DELIMITER ;

-- =================== 性能监控查询 ===================

-- 查看慢查询日志设置
SHOW VARIABLES LIKE 'slow_query%';
SHOW VARIABLES LIKE 'long_query_time';

-- 查看表统计信息
SHOW TABLE STATUS LIKE 't_user'\G

-- 查看索引使用情况
SHOW INDEX FROM t_user;

-- =================== 缓存优化建议 ===================

-- 查看查询缓存设置
SHOW VARIABLES LIKE 'query_cache%';

-- 建议的MySQL配置优化参数：
/*
# 在my.cnf中添加以下配置：

[mysqld]
# 查询缓存优化
query_cache_type = 1
query_cache_size = 268435456  # 256MB

# InnoDB优化
innodb_buffer_pool_size = 1G  # 根据服务器内存调整
innodb_log_file_size = 256M
innodb_flush_log_at_trx_commit = 2
innodb_flush_method = O_DIRECT

# 连接优化
max_connections = 500
thread_cache_size = 50

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/mysql-slow.log
long_query_time = 0.5  # 记录超过0.5秒的查询
*/

-- =================== 测试数据准备 ===================

-- 创建测试用户（用于性能测试）
INSERT INTO t_user (username, nickname, password_hash, role, status, invite_code) 
VALUES 
('perf_test_1', 'PerfTest1', '$2a$10$example_hash_here', 'user', 'active', 'TEST001'),
('perf_test_2', 'PerfTest2', '$2a$10$example_hash_here', 'user', 'active', 'TEST002'),
('perf_test_3', 'PerfTest3', '$2a$10$example_hash_here', 'user', 'active', 'TEST003');

-- 批量插入测试数据（模拟真实环境）
INSERT INTO t_user (username, nickname, password_hash, role, status, invite_code)
SELECT 
    CONCAT('test_user_', n.id) as username,
    CONCAT('TestUser', n.id) as nickname,
    '$2a$10$example_hash_for_testing' as password_hash,
    'user' as role,
    'active' as status,
    CONCAT('INV', n.id) as invite_code
FROM (
    SELECT a.N + b.N * 10 + c.N * 100 + 1 as id
    FROM 
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
        (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4) c
) n
WHERE n.id <= 5000;  -- 插入5000条测试数据

-- =================== 性能测试脚本 ===================

-- 测试用户名查询性能（执行多次取平均值）
SELECT BENCHMARK(10000, (
    SELECT id FROM t_user WHERE username = 'perf_test_1' AND status != 'deleted'
)) as username_query_performance;

-- 测试完整用户信息查询性能
SELECT BENCHMARK(5000, (
    SELECT id, username, nickname, avatar, role, status 
    FROM t_user 
    WHERE username = 'perf_test_1' AND status != 'deleted'
)) as full_user_query_performance;

-- 查看执行计划分析
EXPLAIN FORMAT=JSON 
SELECT id, username, nickname, avatar, role, status, invite_code, inviter_id, invited_count,
       last_login_time, login_count, create_time, update_time
FROM t_user 
WHERE username = 'perf_test_1' AND status != 'deleted'\G