-- ====================================
-- Collide Follow 模块完整 SQL 脚本
-- 去连表化设计，性能优化
-- ====================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================
-- 1. 关注关系表 (t_follow)
-- ====================================
DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow` (
  `id`                BIGINT(20)      NOT NULL AUTO_INCREMENT COMMENT '关注ID，主键',
  `follower_user_id`  BIGINT(20)      NOT NULL COMMENT '关注者用户ID',
  `followed_user_id`  BIGINT(20)      NOT NULL COMMENT '被关注者用户ID',
  `follow_type`       TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '关注类型：1-普通关注，2-特别关注',
  `status`            TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '状态：0-已取消，1-正常，2-已屏蔽',
  `created_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`           TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followed` (`follower_user_id`, `followed_user_id`),
  KEY `idx_follower_user_id` (`follower_user_id`),
  KEY `idx_followed_user_id` (`followed_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_follow_type` (`follow_type`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_updated_time` (`updated_time`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_follower_status_deleted` (`follower_user_id`, `status`, `deleted`),
  KEY `idx_followed_status_deleted` (`followed_user_id`, `status`, `deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表';

-- ====================================
-- 2. 关注统计表 (t_follow_statistics)
-- ====================================
DROP TABLE IF EXISTS `t_follow_statistics`;
CREATE TABLE `t_follow_statistics` (
  `user_id`           BIGINT(20)      NOT NULL COMMENT '用户ID，主键',
  `following_count`   INT(11)         NOT NULL DEFAULT 0 COMMENT '关注数（我关注的人数）',
  `follower_count`    INT(11)         NOT NULL DEFAULT 0 COMMENT '粉丝数（关注我的人数）',
  `mutual_follow_count` INT(11)       NOT NULL DEFAULT 0 COMMENT '相互关注数（预留字段）',
  `created_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`           TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`user_id`),
  KEY `idx_following_count` (`following_count`),
  KEY `idx_follower_count` (`follower_count`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_updated_time` (`updated_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注统计表';

-- ====================================
-- 3. 创建性能优化视图（可选）
-- ====================================

-- 热门用户视图（粉丝数排行）
CREATE OR REPLACE VIEW `v_popular_users` AS
SELECT 
    `user_id`,
    `follower_count`,
    `following_count`,
    `mutual_follow_count`,
    CASE 
        WHEN `follower_count` >= 10000 THEN '大V用户'
        WHEN `follower_count` >= 1000 THEN '活跃用户'
        WHEN `follower_count` >= 100 THEN '普通用户'
        ELSE '新用户'
    END AS `user_level`,
    `updated_time`
FROM `t_follow_statistics`
WHERE `deleted` = 0 
  AND `follower_count` > 0
ORDER BY `follower_count` DESC;

-- ====================================
-- 4. 插入示例数据
-- ====================================

-- 插入关注统计数据
INSERT INTO `t_follow_statistics` (`user_id`, `following_count`, `follower_count`, `mutual_follow_count`) VALUES
(1001, 156, 89, 12),
(1002, 203, 456, 18),
(1003, 89, 234, 8),
(1004, 345, 678, 25),
(1005, 67, 123, 5),
(1006, 234, 567, 15),
(1007, 123, 234, 7),
(1008, 456, 789, 30),
(1009, 78, 145, 4),
(1010, 189, 345, 11);

-- 插入关注关系数据
INSERT INTO `t_follow` (`follower_user_id`, `followed_user_id`, `follow_type`, `status`) VALUES
-- 用户1001的关注关系
(1001, 1002, 1, 1),
(1001, 1003, 1, 1),
(1001, 1004, 2, 1),
(1001, 1005, 1, 1),
-- 用户1002的关注关系
(1002, 1001, 1, 1),
(1002, 1003, 1, 1),
(1002, 1006, 1, 1),
-- 用户1003的关注关系
(1003, 1001, 1, 1),
(1003, 1002, 1, 1),
(1003, 1007, 2, 1),
-- 用户1004的关注关系
(1004, 1001, 1, 1),
(1004, 1008, 1, 1),
-- 相互关注示例
(1005, 1006, 1, 1),
(1006, 1005, 1, 1),
(1007, 1008, 1, 1),
(1008, 1007, 1, 1);

-- ====================================
-- 5. 性能优化存储过程
-- ====================================

DELIMITER ;;

-- 重新计算用户关注统计
CREATE PROCEDURE `sp_recalculate_follow_statistics`(IN `target_user_id` BIGINT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 计算关注数和粉丝数
    INSERT INTO `t_follow_statistics` (
        `user_id`, 
        `following_count`, 
        `follower_count`,
        `mutual_follow_count`
    )
    SELECT 
        `target_user_id`,
        COALESCE((
            SELECT COUNT(1) 
            FROM `t_follow` 
            WHERE `follower_user_id` = `target_user_id` 
              AND `status` = 1 
              AND `deleted` = 0
        ), 0) AS `following_count`,
        COALESCE((
            SELECT COUNT(1) 
            FROM `t_follow` 
            WHERE `followed_user_id` = `target_user_id` 
              AND `status` = 1 
              AND `deleted` = 0
        ), 0) AS `follower_count`,
        0 AS `mutual_follow_count`
    ON DUPLICATE KEY UPDATE 
        `following_count` = VALUES(`following_count`),
        `follower_count` = VALUES(`follower_count`),
        `updated_time` = CURRENT_TIMESTAMP;

    COMMIT;
END;;

-- 批量重新计算所有用户统计
CREATE PROCEDURE `sp_batch_recalculate_statistics`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE user_id BIGINT;
    DECLARE user_cursor CURSOR FOR 
        SELECT DISTINCT `follower_user_id` FROM `t_follow` WHERE `deleted` = 0
        UNION 
        SELECT DISTINCT `followed_user_id` FROM `t_follow` WHERE `deleted` = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN user_cursor;

    read_loop: LOOP
        FETCH user_cursor INTO user_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        CALL `sp_recalculate_follow_statistics`(user_id);
    END LOOP;

    CLOSE user_cursor;
END;;

DELIMITER ;

-- ====================================
-- 6. 数据一致性检查函数
-- ====================================

DELIMITER ;;

-- 检查关注数据一致性
CREATE FUNCTION `fn_check_follow_consistency`(target_user_id BIGINT) 
RETURNS VARCHAR(255)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE actual_following INT DEFAULT 0;
    DECLARE actual_follower INT DEFAULT 0;
    DECLARE recorded_following INT DEFAULT 0;
    DECLARE recorded_follower INT DEFAULT 0;
    DECLARE result VARCHAR(255) DEFAULT 'CONSISTENT';

    -- 获取实际关注数
    SELECT COUNT(1) INTO actual_following
    FROM `t_follow` 
    WHERE `follower_user_id` = target_user_id 
      AND `status` = 1 
      AND `deleted` = 0;

    -- 获取实际粉丝数
    SELECT COUNT(1) INTO actual_follower
    FROM `t_follow` 
    WHERE `followed_user_id` = target_user_id 
      AND `status` = 1 
      AND `deleted` = 0;

    -- 获取记录的统计数据
    SELECT `following_count`, `follower_count` 
    INTO recorded_following, recorded_follower
    FROM `t_follow_statistics` 
    WHERE `user_id` = target_user_id 
      AND `deleted` = 0;

    -- 检查一致性
    IF recorded_following IS NULL THEN
        SET result = 'STATISTICS_NOT_FOUND';
    ELSEIF actual_following != recorded_following OR actual_follower != recorded_follower THEN
        SET result = CONCAT('INCONSISTENT: actual(', actual_following, ',', actual_follower, ') vs recorded(', recorded_following, ',', recorded_follower, ')');
    END IF;

    RETURN result;
END;;

DELIMITER ;

-- ====================================
-- 7. 性能监控表（可选）
-- ====================================

DROP TABLE IF EXISTS `t_follow_performance_log`;
CREATE TABLE `t_follow_performance_log` (
  `id`              BIGINT(20)      NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operation_type`  VARCHAR(50)     NOT NULL COMMENT '操作类型：FOLLOW,UNFOLLOW,QUERY',
  `user_id`         BIGINT(20)      NOT NULL COMMENT '操作用户ID',
  `target_user_id`  BIGINT(20)      DEFAULT NULL COMMENT '目标用户ID',
  `execution_time`  INT(11)         NOT NULL COMMENT '执行时间（毫秒）',
  `success`         TINYINT(1)      NOT NULL DEFAULT 1 COMMENT '是否成功：0-失败，1-成功',
  `error_message`   TEXT            DEFAULT NULL COMMENT '错误信息',
  `created_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_execution_time` (`execution_time`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_success` (`success`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注操作性能日志表';

-- ====================================
-- 8. 数据库版本信息
-- ====================================

INSERT INTO `t_follow_performance_log` (`operation_type`, `user_id`, `execution_time`, `success`, `error_message`) VALUES
('INIT', 0, 0, 1, 'Follow module database initialized successfully');

-- ====================================
-- 9. 权限和安全设置
-- ====================================

-- 建议为应用程序创建专用数据库用户
-- CREATE USER 'collide_follow'@'%' IDENTIFIED BY 'your_secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON collide.t_follow* TO 'collide_follow'@'%';
-- GRANT EXECUTE ON PROCEDURE collide.sp_recalculate_follow_statistics TO 'collide_follow'@'%';
-- GRANT EXECUTE ON PROCEDURE collide.sp_batch_recalculate_statistics TO 'collide_follow'@'%';
-- FLUSH PRIVILEGES;

-- ====================================
-- 10. 性能优化建议
-- ====================================

/*
性能优化要点：

1. 索引策略：
   - 主键索引：使用 AUTO_INCREMENT 确保插入性能
   - 唯一索引：防止重复关注，保证数据一致性
   - 复合索引：针对常见查询组合优化
   - 覆盖索引：减少回表查询

2. 分区策略（大数据量场景）：
   ALTER TABLE t_follow PARTITION BY HASH(follower_user_id) PARTITIONS 16;

3. 缓存策略：
   - 热点用户关注关系缓存
   - 统计数据缓存
   - 查询结果缓存

4. 数据归档（历史数据管理）：
   - 定期归档已取消的关注记录
   - 保留最近6个月的活跃数据

5. 监控指标：
   - 查询响应时间
   - 索引使用率
   - 锁等待时间
   - 缓存命中率
*/

SET FOREIGN_KEY_CHECKS = 1;

-- ====================================
-- 执行完成提示
-- ====================================
SELECT 'Follow module database setup completed successfully!' AS result; 