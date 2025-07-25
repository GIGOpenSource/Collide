-- ==========================================
-- 修复数据库死锁问题
-- ==========================================

USE `collide`;

-- 1. 检查当前事务隔离级别
SELECT @@global.tx_isolation, @@session.tx_isolation;

-- 2. 检查当前锁等待情况
SHOW ENGINE INNODB STATUS;

-- 3. 检查表锁情况
SHOW OPEN TABLES WHERE In_use > 0;

-- 4. 检查当前进程
SHOW PROCESSLIST;

-- 5. 优化表结构，减少锁竞争
-- 为 t_user 表添加更好的索引策略
ALTER TABLE `t_user` 
ADD INDEX `idx_username_deleted` (`username`, `deleted`),
ADD INDEX `idx_email_deleted` (`email`, `deleted`),
ADD INDEX `idx_phone_deleted` (`phone`, `deleted`);

-- 6. 分析表，优化查询计划
ANALYZE TABLE `t_user`;

-- 7. 检查表状态
SHOW TABLE STATUS LIKE 't_user';

-- 8. 检查索引使用情况
SHOW INDEX FROM `t_user`;

-- 9. 设置更宽松的事务隔离级别（可选，需要重启MySQL）
-- SET GLOBAL tx_isolation = 'READ-COMMITTED';
-- SET SESSION tx_isolation = 'READ-COMMITTED';

-- 10. 检查死锁统计
SHOW STATUS LIKE 'Innodb_deadlocks'; 