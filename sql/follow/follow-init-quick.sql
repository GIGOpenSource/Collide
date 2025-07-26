-- ====================================
-- Collide Follow 模块快速初始化脚本
-- 用于开发环境快速部署
-- ====================================

USE collide;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ====================================
-- 1. 清理现有数据（谨慎使用）
-- ====================================
-- DROP TABLE IF EXISTS `t_follow`;
-- DROP TABLE IF EXISTS `t_follow_statistics`;

-- ====================================
-- 2. 创建关注关系表
-- ====================================
CREATE TABLE IF NOT EXISTS `t_follow` (
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
  KEY `idx_deleted` (`deleted`),
  KEY `idx_follower_status_deleted` (`follower_user_id`, `status`, `deleted`),
  KEY `idx_followed_status_deleted` (`followed_user_id`, `status`, `deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表';

-- ====================================
-- 3. 创建关注统计表
-- ====================================
CREATE TABLE IF NOT EXISTS `t_follow_statistics` (
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
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注统计表';

-- ====================================
-- 4. 插入测试数据
-- ====================================

-- 插入关注统计测试数据
INSERT IGNORE INTO `t_follow_statistics` (`user_id`, `following_count`, `follower_count`, `mutual_follow_count`) VALUES
(1001, 156, 89, 12),
(1002, 203, 456, 18),
(1003, 89, 234, 8),
(1004, 345, 678, 25),
(1005, 67, 123, 5);

-- 插入关注关系测试数据
INSERT IGNORE INTO `t_follow` (`follower_user_id`, `followed_user_id`, `follow_type`, `status`) VALUES
-- 用户1001的关注关系
(1001, 1002, 1, 1),
(1001, 1003, 1, 1),
(1001, 1004, 2, 1),
-- 用户1002的关注关系
(1002, 1001, 1, 1),
(1002, 1003, 1, 1),
-- 相互关注示例
(1003, 1001, 1, 1),
(1004, 1001, 1, 1);

-- ====================================
-- 5. 验证数据
-- ====================================
SELECT 'Follow module quick setup completed!' AS result;
SELECT COUNT(*) AS follow_count FROM `t_follow`;
SELECT COUNT(*) AS statistics_count FROM `t_follow_statistics`;

SET FOREIGN_KEY_CHECKS = 1; 