-- ====================================
-- Collide Follow 模块索引优化脚本
-- 针对去连表化设计的性能优化
-- ====================================

-- 设置优化参数
SET SESSION sql_mode = '';
SET SESSION optimizer_switch = 'index_merge=on,index_merge_union=on,index_merge_sort_union=on,index_merge_intersection=on';

-- ====================================
-- 1. t_follow 表索引优化
-- ====================================

-- 分析现有索引使用情况
-- EXPLAIN SELECT * FROM t_follow WHERE follower_user_id = 1001 AND status = 1 AND deleted = 0;

-- 添加覆盖索引，减少回表查询
ALTER TABLE `t_follow` 
ADD INDEX `idx_follower_status_cover` (`follower_user_id`, `status`, `deleted`, `followed_user_id`, `follow_type`, `created_time`);

ALTER TABLE `t_follow` 
ADD INDEX `idx_followed_status_cover` (`followed_user_id`, `status`, `deleted`, `follower_user_id`, `follow_type`, `created_time`);

-- 相互关注查询优化索引
ALTER TABLE `t_follow` 
ADD INDEX `idx_mutual_follow` (`follower_user_id`, `followed_user_id`, `status`, `deleted`);

-- 时间范围查询优化
ALTER TABLE `t_follow` 
ADD INDEX `idx_time_range` (`created_time`, `status`, `deleted`);

-- 批量查询优化索引
ALTER TABLE `t_follow` 
ADD INDEX `idx_batch_query` (`follower_user_id`, `status`, `deleted`, `followed_user_id`);

-- ====================================
-- 2. t_follow_statistics 表索引优化
-- ====================================

-- 排行榜查询优化
ALTER TABLE `t_follow_statistics` 
ADD INDEX `idx_follower_ranking` (`follower_count` DESC, `deleted`, `user_id`);

ALTER TABLE `t_follow_statistics` 
ADD INDEX `idx_following_ranking` (`following_count` DESC, `deleted`, `user_id`);

-- 活跃用户统计优化
ALTER TABLE `t_follow_statistics` 
ADD INDEX `idx_active_users` (`following_count`, `follower_count`, `deleted`);

-- 批量统计查询优化
ALTER TABLE `t_follow_statistics` 
ADD INDEX `idx_batch_stats` (`user_id`, `deleted`, `following_count`, `follower_count`);

-- ====================================
-- 3. 分区表优化（大数据量场景）
-- ====================================

-- 如果数据量超过1000万，考虑按用户ID分区
/*
ALTER TABLE `t_follow` 
PARTITION BY HASH(`follower_user_id`) 
PARTITIONS 16;

ALTER TABLE `t_follow_statistics` 
PARTITION BY HASH(`user_id`) 
PARTITIONS 8;
*/

-- ====================================
-- 4. 查询性能测试脚本
-- ====================================

DELIMITER ;;

-- 性能测试存储过程
CREATE PROCEDURE `sp_performance_test_follow`()
BEGIN
    DECLARE start_time BIGINT;
    DECLARE end_time BIGINT;
    DECLARE execution_time INT;
    
    -- 测试关注关系查询
    SET start_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    
    SELECT COUNT(*) FROM `t_follow` 
    WHERE `follower_user_id` = 1001 
      AND `status` = 1 
      AND `deleted` = 0;
    
    SET end_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    SET execution_time = end_time - start_time;
    
    INSERT INTO `t_follow_performance_log` 
    (`operation_type`, `user_id`, `execution_time`, `success`) 
    VALUES ('QUERY_FOLLOWING', 1001, execution_time, 1);
    
    -- 测试粉丝列表查询
    SET start_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    
    SELECT `follower_user_id`, `created_time` 
    FROM `t_follow` 
    WHERE `followed_user_id` = 1001 
      AND `status` = 1 
      AND `deleted` = 0 
    ORDER BY `created_time` DESC 
    LIMIT 20;
    
    SET end_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    SET execution_time = end_time - start_time;
    
    INSERT INTO `t_follow_performance_log` 
    (`operation_type`, `user_id`, `execution_time`, `success`) 
    VALUES ('QUERY_FOLLOWERS', 1001, execution_time, 1);
    
    -- 测试相互关注查询
    SET start_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    
    SELECT f1.`followed_user_id` 
    FROM `t_follow` f1
    WHERE f1.`follower_user_id` = 1001 
      AND f1.`status` = 1 
      AND f1.`deleted` = 0
      AND EXISTS (
          SELECT 1 FROM `t_follow` f2 
          WHERE f2.`follower_user_id` = f1.`followed_user_id` 
            AND f2.`followed_user_id` = 1001 
            AND f2.`status` = 1 
            AND f2.`deleted` = 0
      );
    
    SET end_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    SET execution_time = end_time - start_time;
    
    INSERT INTO `t_follow_performance_log` 
    (`operation_type`, `user_id`, `execution_time`, `success`) 
    VALUES ('QUERY_MUTUAL', 1001, execution_time, 1);
    
    -- 测试统计查询
    SET start_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    
    SELECT `following_count`, `follower_count` 
    FROM `t_follow_statistics` 
    WHERE `user_id` = 1001 
      AND `deleted` = 0;
    
    SET end_time = UNIX_TIMESTAMP(NOW(3)) * 1000;
    SET execution_time = end_time - start_time;
    
    INSERT INTO `t_follow_performance_log` 
    (`operation_type`, `user_id`, `execution_time`, `success`) 
    VALUES ('QUERY_STATISTICS', 1001, execution_time, 1);
    
END;;

DELIMITER ;

-- ====================================
-- 5. 索引使用情况监控
-- ====================================

DELIMITER ;;

-- 索引使用情况分析
CREATE PROCEDURE `sp_analyze_index_usage`()
BEGIN
    -- 显示表的索引信息
    SELECT 
        TABLE_NAME,
        INDEX_NAME,
        COLUMN_NAME,
        CARDINALITY,
        INDEX_TYPE
    FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = DATABASE() 
      AND TABLE_NAME IN ('t_follow', 't_follow_statistics')
    ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;
    
    -- 显示索引长度统计
    SELECT 
        TABLE_NAME,
        INDEX_NAME,
        ROUND(((INDEX_LENGTH) / 1024 / 1024), 2) AS 'Index Size (MB)'
    FROM information_schema.TABLES 
    WHERE TABLE_SCHEMA = DATABASE() 
      AND TABLE_NAME IN ('t_follow', 't_follow_statistics');
      
END;;

DELIMITER ;

-- ====================================
-- 6. 慢查询优化建议
-- ====================================

-- 开启慢查询日志分析
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 0.1;  -- 记录超过100ms的查询

-- 常见慢查询优化案例：

-- 案例1：大表全表扫描优化
-- 原查询：SELECT COUNT(*) FROM t_follow WHERE status = 1;
-- 优化：添加状态索引，或使用统计表

-- 案例2：排序查询优化  
-- 原查询：SELECT * FROM t_follow ORDER BY created_time DESC LIMIT 100;
-- 优化：使用覆盖索引 idx_time_range

-- 案例3：复杂条件查询优化
-- 原查询：SELECT * FROM t_follow WHERE follower_user_id IN (1,2,3,4,5) AND status = 1;
-- 优化：使用复合索引 idx_follower_status_cover

-- ====================================
-- 7. 表维护脚本
-- ====================================

DELIMITER ;;

-- 表维护和优化
CREATE PROCEDURE `sp_maintain_follow_tables`()
BEGIN
    -- 优化表结构
    OPTIMIZE TABLE `t_follow`;
    OPTIMIZE TABLE `t_follow_statistics`;
    
    -- 分析表统计信息
    ANALYZE TABLE `t_follow`;
    ANALYZE TABLE `t_follow_statistics`;
    
    -- 检查表完整性
    CHECK TABLE `t_follow`;
    CHECK TABLE `t_follow_statistics`;
    
    -- 更新表统计信息
    UPDATE `t_follow_performance_log` 
    SET `operation_type` = 'MAINTENANCE', 
        `user_id` = 0, 
        `execution_time` = 0, 
        `success` = 1,
        `error_message` = 'Table maintenance completed'
    WHERE `id` = LAST_INSERT_ID();
    
END;;

DELIMITER ;

-- ====================================
-- 8. 执行优化验证
-- ====================================

-- 运行性能测试
CALL `sp_performance_test_follow`();

-- 分析索引使用情况
CALL `sp_analyze_index_usage`();

-- 显示执行计划示例
EXPLAIN FORMAT=JSON
SELECT f.`followed_user_id`, f.`created_time`
FROM `t_follow` f
WHERE f.`follower_user_id` = 1001 
  AND f.`status` = 1 
  AND f.`deleted` = 0
ORDER BY f.`created_time` DESC
LIMIT 20;

-- ====================================
-- 优化完成提示
-- ====================================
SELECT 'Follow module index optimization completed!' AS result,
       'Use sp_performance_test_follow() to test performance' AS note; 