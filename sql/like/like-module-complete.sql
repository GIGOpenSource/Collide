-- ================================================================
-- Collide 点赞模块完整数据库脚本
-- 版本: v1.0.0  
-- 描述: 去连表化设计，高性能点赞系统
-- 作者: Collide Team
-- 更新时间: 2024-01-01
-- ================================================================

-- ================================================================
-- 1. 核心点赞表 (t_like)
-- ================================================================

-- 删除现有表（仅开发环境）
-- DROP TABLE IF EXISTS `t_like`;

CREATE TABLE `t_like` (
  `id`               BIGINT(20)      NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID',
  `user_id`          BIGINT(20)      NOT NULL                COMMENT '点赞用户ID',
  `target_id`        BIGINT(20)      NOT NULL                COMMENT '目标对象ID（内容ID/评论ID等）',
  `target_type`      VARCHAR(32)     NOT NULL                COMMENT '目标类型：CONTENT/COMMENT/USER/GOODS',
  `action_type`      TINYINT(2)      NOT NULL DEFAULT 1      COMMENT '操作类型：1=点赞 0=取消 -1=点踩',
  
  -- 冗余用户信息（避免连表查询）
  `user_nickname`    VARCHAR(50)     DEFAULT NULL            COMMENT '用户昵称（冗余字段）',
  `user_avatar`      VARCHAR(512)    DEFAULT NULL            COMMENT '用户头像URL（冗余字段）',
  
  -- 冗余目标信息（避免连表查询）
  `target_title`     VARCHAR(200)    DEFAULT NULL            COMMENT '目标对象标题（冗余字段）',
  `target_author_id` BIGINT(20)      DEFAULT NULL            COMMENT '目标对象作者ID（冗余字段）',
  
  -- 追踪信息
  `ip_address`       VARCHAR(45)     DEFAULT NULL            COMMENT '操作IP地址',
  `device_info`      VARCHAR(500)    DEFAULT NULL            COMMENT '设备信息JSON',
  `platform`         VARCHAR(32)     DEFAULT 'WEB'           COMMENT '平台：WEB/APP/H5',
  
  -- 时间字段
  `created_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  -- 状态字段
  `status`           TINYINT(1)      NOT NULL DEFAULT 1      COMMENT '状态：1=正常 0=无效',
  `deleted`          TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '删除标记：0=正常 1=删除',
  
  PRIMARY KEY (`id`),
  
  -- 核心索引（防重复点赞）
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  
  -- 查询优化索引
  KEY `idx_user_id_status` (`user_id`, `status`, `deleted`),
  KEY `idx_target_id_type` (`target_id`, `target_type`, `action_type`, `deleted`),
  KEY `idx_target_type_action` (`target_type`, `action_type`, `created_time`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_action_type` (`action_type`, `deleted`),
  
  -- 统计查询索引
  KEY `idx_statistics` (`target_type`, `target_id`, `action_type`, `status`, `deleted`)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
  COMMENT='统一点赞表-去连表化设计';

-- ================================================================
-- 2. 点赞统计表 (t_like_statistics) - 可选的预聚合表
-- ================================================================

CREATE TABLE `t_like_statistics` (
  `id`                BIGINT(20)      NOT NULL AUTO_INCREMENT COMMENT '统计记录ID',
  `target_id`         BIGINT(20)      NOT NULL                COMMENT '目标对象ID',
  `target_type`       VARCHAR(32)     NOT NULL                COMMENT '目标类型',
  
  -- 点赞统计
  `total_like_count`    BIGINT(20)    NOT NULL DEFAULT 0      COMMENT '总点赞数',
  `total_dislike_count` BIGINT(20)    NOT NULL DEFAULT 0      COMMENT '总点踩数',
  
  -- 时间段统计
  `today_like_count`    INT(11)       NOT NULL DEFAULT 0      COMMENT '今日点赞数',
  `week_like_count`     INT(11)       NOT NULL DEFAULT 0      COMMENT '本周点赞数',
  `month_like_count`    INT(11)       NOT NULL DEFAULT 0      COMMENT '本月点赞数',
  
  -- 计算字段
  `like_rate`           DECIMAL(5,2)  NOT NULL DEFAULT 0.00   COMMENT '点赞率(%)',
  `last_like_time`      DATETIME      DEFAULT NULL            COMMENT '最后点赞时间',
  
  -- 维护字段
  `stat_date`           DATE          NOT NULL                COMMENT '统计日期',
  `updated_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_target_date` (`target_id`, `target_type`, `stat_date`),
  KEY `idx_target_type` (`target_type`, `stat_date`),
  KEY `idx_like_count` (`total_like_count` DESC),
  KEY `idx_today_count` (`today_like_count` DESC)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
  COMMENT='点赞统计表-预聚合数据';

-- ================================================================
-- 3. 为其他表添加点赞统计字段（去连表化）
-- ================================================================

-- 为内容表添加点赞统计字段
ALTER TABLE `t_content` 
ADD COLUMN IF NOT EXISTS `like_count`     BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点赞数（冗余字段）',
ADD COLUMN IF NOT EXISTS `dislike_count`  BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点踩数（冗余字段）',
ADD COLUMN IF NOT EXISTS `like_rate`      DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '点赞率（冗余字段）',
ADD COLUMN IF NOT EXISTS `last_like_time` DATETIME DEFAULT NULL COMMENT '最后点赞时间（冗余字段）';

-- 为评论表添加点赞统计字段
ALTER TABLE `t_comment` 
ADD COLUMN IF NOT EXISTS `like_count`     BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点赞数（冗余字段）',
ADD COLUMN IF NOT EXISTS `dislike_count`  BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点踩数（冗余字段）',
ADD COLUMN IF NOT EXISTS `like_rate`      DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '点赞率（冗余字段）',
ADD COLUMN IF NOT EXISTS `last_like_time` DATETIME DEFAULT NULL COMMENT '最后点赞时间（冗余字段）';

-- 为商品表添加点赞统计字段（如果存在）
ALTER TABLE `t_goods` 
ADD COLUMN IF NOT EXISTS `like_count`     BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点赞数（冗余字段）',
ADD COLUMN IF NOT EXISTS `dislike_count`  BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点踩数（冗余字段）',
ADD COLUMN IF NOT EXISTS `like_rate`      DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '点赞率（冗余字段）',
ADD COLUMN IF NOT EXISTS `last_like_time` DATETIME DEFAULT NULL COMMENT '最后点赞时间（冗余字段）';

-- 为用户表添加点赞统计字段
ALTER TABLE `t_user` 
ADD COLUMN IF NOT EXISTS `received_like_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '收到的点赞数（冗余字段）',
ADD COLUMN IF NOT EXISTS `given_like_count`    BIGINT(20) NOT NULL DEFAULT 0 COMMENT '给出的点赞数（冗余字段）';

-- ================================================================
-- 4. 性能优化索引
-- ================================================================

-- 内容表点赞相关索引
ALTER TABLE `t_content` 
ADD INDEX IF NOT EXISTS `idx_like_count_desc` (`like_count` DESC, `status`, `deleted`),
ADD INDEX IF NOT EXISTS `idx_like_rate_desc` (`like_rate` DESC, `status`, `deleted`),
ADD INDEX IF NOT EXISTS `idx_last_like_time` (`last_like_time` DESC);

-- 评论表点赞相关索引
ALTER TABLE `t_comment` 
ADD INDEX IF NOT EXISTS `idx_like_count_desc` (`like_count` DESC, `status`, `deleted`),
ADD INDEX IF NOT EXISTS `idx_like_rate_desc` (`like_rate` DESC, `status`, `deleted`);

-- 商品表点赞相关索引（如果存在）
ALTER TABLE `t_goods` 
ADD INDEX IF NOT EXISTS `idx_like_count_desc` (`like_count` DESC, `status`, `deleted`),
ADD INDEX IF NOT EXISTS `idx_like_rate_desc` (`like_rate` DESC, `status`, `deleted`);

-- ================================================================
-- 5. 数据一致性检查视图
-- ================================================================

CREATE OR REPLACE VIEW `v_like_consistency_check` AS
-- 内容表一致性检查
SELECT 
    'CONTENT' AS target_type,
    c.id AS target_id,
    c.title AS target_title,
    c.like_count AS cached_like_count,
    c.dislike_count AS cached_dislike_count,
    COALESCE(l.actual_like_count, 0) AS actual_like_count,
    COALESCE(l.actual_dislike_count, 0) AS actual_dislike_count,
    (c.like_count != COALESCE(l.actual_like_count, 0)) AS like_inconsistent,
    (c.dislike_count != COALESCE(l.actual_dislike_count, 0)) AS dislike_inconsistent,
    ABS(c.like_count - COALESCE(l.actual_like_count, 0)) AS like_diff,
    ABS(c.dislike_count - COALESCE(l.actual_dislike_count, 0)) AS dislike_diff
FROM t_content c
LEFT JOIN (
    SELECT 
        target_id,
        SUM(CASE WHEN action_type = 1 AND status = 1 AND deleted = 0 THEN 1 ELSE 0 END) AS actual_like_count,
        SUM(CASE WHEN action_type = -1 AND status = 1 AND deleted = 0 THEN 1 ELSE 0 END) AS actual_dislike_count
    FROM t_like 
    WHERE target_type = 'CONTENT' 
    GROUP BY target_id
) l ON c.id = l.target_id

UNION ALL

-- 评论表一致性检查
SELECT 
    'COMMENT' AS target_type,
    c.id AS target_id,
    c.content AS target_title,
    c.like_count AS cached_like_count,
    c.dislike_count AS cached_dislike_count,
    COALESCE(l.actual_like_count, 0) AS actual_like_count,
    COALESCE(l.actual_dislike_count, 0) AS actual_dislike_count,
    (c.like_count != COALESCE(l.actual_like_count, 0)) AS like_inconsistent,
    (c.dislike_count != COALESCE(l.actual_dislike_count, 0)) AS dislike_inconsistent,
    ABS(c.like_count - COALESCE(l.actual_like_count, 0)) AS like_diff,
    ABS(c.dislike_count - COALESCE(l.actual_dislike_count, 0)) AS dislike_diff
FROM t_comment c
LEFT JOIN (
    SELECT 
        target_id,
        SUM(CASE WHEN action_type = 1 AND status = 1 AND deleted = 0 THEN 1 ELSE 0 END) AS actual_like_count,
        SUM(CASE WHEN action_type = -1 AND status = 1 AND deleted = 0 THEN 1 ELSE 0 END) AS actual_dislike_count
    FROM t_like 
    WHERE target_type = 'COMMENT' 
    GROUP BY target_id
) l ON c.id = l.target_id;

-- ================================================================
-- 6. 数据修复和初始化
-- ================================================================

-- 修复内容表的点赞统计数据
UPDATE t_content c 
SET 
    like_count = (
        SELECT COUNT(*) 
        FROM t_like l 
        WHERE l.target_id = c.id 
          AND l.target_type = 'CONTENT' 
          AND l.action_type = 1 
          AND l.status = 1 
          AND l.deleted = 0
    ),
    dislike_count = (
        SELECT COUNT(*) 
        FROM t_like l 
        WHERE l.target_id = c.id 
          AND l.target_type = 'CONTENT' 
          AND l.action_type = -1 
          AND l.status = 1 
          AND l.deleted = 0
    ),
    last_like_time = (
        SELECT MAX(l.created_time)
        FROM t_like l 
        WHERE l.target_id = c.id 
          AND l.target_type = 'CONTENT' 
          AND l.action_type = 1 
          AND l.status = 1 
          AND l.deleted = 0
    );

-- 修复评论表的点赞统计数据
UPDATE t_comment c 
SET 
    like_count = (
        SELECT COUNT(*) 
        FROM t_like l 
        WHERE l.target_id = c.id 
          AND l.target_type = 'COMMENT' 
          AND l.action_type = 1 
          AND l.status = 1 
          AND l.deleted = 0
    ),
    dislike_count = (
        SELECT COUNT(*) 
        FROM t_like l 
        WHERE l.target_id = c.id 
          AND l.target_type = 'COMMENT' 
          AND l.action_type = -1 
          AND l.status = 1 
          AND l.deleted = 0
    ),
    last_like_time = (
        SELECT MAX(l.created_time)
        FROM t_like l 
        WHERE l.target_id = c.id 
          AND l.target_type = 'COMMENT' 
          AND l.action_type = 1 
          AND l.status = 1 
          AND l.deleted = 0
    );

-- 更新点赞率
UPDATE t_content 
SET like_rate = CASE 
    WHEN (like_count + dislike_count) > 0 
    THEN ROUND((like_count * 100.0) / (like_count + dislike_count), 2)
    ELSE 0.00 
END;

UPDATE t_comment 
SET like_rate = CASE 
    WHEN (like_count + dislike_count) > 0 
    THEN ROUND((like_count * 100.0) / (like_count + dislike_count), 2)
    ELSE 0.00 
END;

-- ================================================================
-- 7. 常用查询示例（验证去连表化效果）
-- ================================================================

-- 示例1: 获取用户点赞状态（单表查询）
-- SELECT action_type, created_time 
-- FROM t_like 
-- WHERE user_id = 12345 AND target_id = 67890 AND target_type = 'CONTENT' AND deleted = 0;

-- 示例2: 获取内容点赞统计（单表查询）
-- SELECT like_count, dislike_count, like_rate 
-- FROM t_content 
-- WHERE id = 67890;

-- 示例3: 获取最受欢迎内容（单表查询）
-- SELECT id, title, like_count, like_rate 
-- FROM t_content 
-- WHERE status = 1 AND deleted = 0 
-- ORDER BY like_count DESC, like_rate DESC 
-- LIMIT 10;

-- 示例4: 获取用户点赞历史（单表查询）
-- SELECT target_id, target_type, target_title, action_type, created_time 
-- FROM t_like 
-- WHERE user_id = 12345 AND action_type = 1 AND deleted = 0 
-- ORDER BY created_time DESC 
-- LIMIT 20;

-- ================================================================
-- 8. 数据库触发器（可选 - 实时更新统计）
-- ================================================================

-- 注意：生产环境建议使用消息队列异步更新，避免触发器性能问题

DELIMITER $$

-- 点赞记录插入后更新统计
CREATE TRIGGER `tr_like_after_insert` 
AFTER INSERT ON `t_like` 
FOR EACH ROW 
BEGIN
    IF NEW.target_type = 'CONTENT' THEN
        UPDATE t_content 
        SET like_count = like_count + IF(NEW.action_type = 1, 1, 0),
            dislike_count = dislike_count + IF(NEW.action_type = -1, 1, 0),
            last_like_time = IF(NEW.action_type = 1, NEW.created_time, last_like_time)
        WHERE id = NEW.target_id;
    ELSEIF NEW.target_type = 'COMMENT' THEN
        UPDATE t_comment 
        SET like_count = like_count + IF(NEW.action_type = 1, 1, 0),
            dislike_count = dislike_count + IF(NEW.action_type = -1, 1, 0),
            last_like_time = IF(NEW.action_type = 1, NEW.created_time, last_like_time)
        WHERE id = NEW.target_id;
    END IF;
END$$

-- 点赞记录更新后调整统计
CREATE TRIGGER `tr_like_after_update` 
AFTER UPDATE ON `t_like` 
FOR EACH ROW 
BEGIN
    IF OLD.target_type = 'CONTENT' THEN
        UPDATE t_content 
        SET like_count = like_count 
                        - IF(OLD.action_type = 1, 1, 0) 
                        + IF(NEW.action_type = 1, 1, 0),
            dislike_count = dislike_count 
                           - IF(OLD.action_type = -1, 1, 0) 
                           + IF(NEW.action_type = -1, 1, 0)
        WHERE id = NEW.target_id;
    ELSEIF OLD.target_type = 'COMMENT' THEN
        UPDATE t_comment 
        SET like_count = like_count 
                        - IF(OLD.action_type = 1, 1, 0) 
                        + IF(NEW.action_type = 1, 1, 0),
            dislike_count = dislike_count 
                           - IF(OLD.action_type = -1, 1, 0) 
                           + IF(NEW.action_type = -1, 1, 0)
        WHERE id = NEW.target_id;
    END IF;
END$$

DELIMITER ;

-- ================================================================
-- 完成
-- ================================================================

COMMIT;

-- 查询数据不一致的记录（维护用）
-- SELECT * FROM v_like_consistency_check 
-- WHERE like_inconsistent = 1 OR dislike_inconsistent = 1 
-- ORDER BY like_diff DESC, dislike_diff DESC; 