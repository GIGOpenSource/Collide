-- ============================================
-- 内容交互相关表
-- 创建时间：2024-01-01
-- 用途：支持内容点赞、收藏、分享等交互功能的幂等性
-- ============================================

USE collide_db;

-- ============================================
-- 内容点赞记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `t_content_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_user` (`content_id`, `user_id`, `deleted`) COMMENT '同一用户对同一内容只能点赞一次',
    KEY `idx_content_id` (`content_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容点赞记录表';

-- ============================================
-- 内容收藏记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `t_content_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_user` (`content_id`, `user_id`, `deleted`) COMMENT '同一用户对同一内容只能收藏一次',
    KEY `idx_content_id` (`content_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容收藏记录表';

-- ============================================
-- 内容分享记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `t_content_share` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `platform` VARCHAR(50) DEFAULT NULL COMMENT '分享平台：wechat-微信，weibo-微博，qq-QQ，link-链接等',
    `share_text` VARCHAR(500) DEFAULT NULL COMMENT '分享文案',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-正常，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_platform` (`platform`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容分享记录表（分享可重复）';

-- ============================================
-- 为现有的内容表添加索引优化（如果不存在的话）
-- ============================================

-- 检查并添加内容表的索引
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE table_schema = 'collide_db' 
     AND table_name = 't_content' 
     AND index_name = 'idx_author_status') = 0,
    'ALTER TABLE t_content ADD INDEX idx_author_status (author_id, status)',
    'SELECT ''索引 idx_author_status 已存在'''
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE table_schema = 'collide_db' 
     AND table_name = 't_content' 
     AND index_name = 'idx_category_status') = 0,
    'ALTER TABLE t_content ADD INDEX idx_category_status (category_id, status)',
    'SELECT ''索引 idx_category_status 已存在'''
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE table_schema = 'collide_db' 
     AND table_name = 't_content' 
     AND index_name = 'idx_published_time') = 0,
    'ALTER TABLE t_content ADD INDEX idx_published_time (published_time)',
    'SELECT ''索引 idx_published_time 已存在'''
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE table_schema = 'collide_db' 
     AND table_name = 't_content' 
     AND index_name = 'idx_weight_score') = 0,
    'ALTER TABLE t_content ADD INDEX idx_weight_score (weight_score)',
    'SELECT ''索引 idx_weight_score 已存在'''
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================
-- 创建视图：内容统计汇总
-- ============================================
CREATE OR REPLACE VIEW `v_content_statistics` AS
SELECT 
    c.id as content_id,
    c.title,
    c.author_id,
    c.status,
    c.view_count,
    c.like_count,
    c.comment_count,
    c.share_count,
    c.favorite_count,
    c.published_time,
    -- 实际统计数据（从关系表计算）
    COALESCE(likes.actual_like_count, 0) as actual_like_count,
    COALESCE(favorites.actual_favorite_count, 0) as actual_favorite_count,
    COALESCE(shares.actual_share_count, 0) as actual_share_count,
    -- 计算互动得分
    (c.like_count * 2 + c.comment_count * 3 + c.share_count * 5 + c.favorite_count * 4 - c.dislike_count) as interaction_score
FROM t_content c
LEFT JOIN (
    SELECT content_id, COUNT(*) as actual_like_count
    FROM t_content_like 
    WHERE deleted = 0 
    GROUP BY content_id
) likes ON c.id = likes.content_id
LEFT JOIN (
    SELECT content_id, COUNT(*) as actual_favorite_count
    FROM t_content_favorite 
    WHERE deleted = 0 
    GROUP BY content_id
) favorites ON c.id = favorites.content_id
LEFT JOIN (
    SELECT content_id, COUNT(*) as actual_share_count
    FROM t_content_share 
    WHERE deleted = 0 
    GROUP BY content_id
) shares ON c.id = shares.content_id
WHERE c.deleted = 0;

-- ============================================
-- 插入测试数据（可选，用于验证功能）
-- ============================================

-- 注意：以下测试数据仅用于开发测试，生产环境请删除
/*
-- 假设内容ID为1，用户ID为1001和1002
INSERT INTO t_content_like (content_id, user_id) VALUES
(1, 1001),
(1, 1002);

INSERT INTO t_content_favorite (content_id, user_id) VALUES
(1, 1001);

INSERT INTO t_content_share (content_id, user_id, platform, share_text) VALUES
(1, 1001, 'wechat', '分享一篇好文章'),
(1, 1002, 'weibo', '推荐阅读');
*/

-- ============================================
-- 权限设置
-- ============================================

-- 为应用程序用户授权（请根据实际情况调整用户名）
-- GRANT SELECT, INSERT, UPDATE, DELETE ON collide_db.t_content_like TO 'collide_app'@'%';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON collide_db.t_content_favorite TO 'collide_app'@'%';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON collide_db.t_content_share TO 'collide_app'@'%';
-- GRANT SELECT ON collide_db.v_content_statistics TO 'collide_app'@'%';

-- ============================================
-- 脚本执行完成提示
-- ============================================
SELECT '内容交互表创建完成！' as message,
       '✅ t_content_like - 点赞记录表' as table1,
       '✅ t_content_favorite - 收藏记录表' as table2, 
       '✅ t_content_share - 分享记录表' as table3,
       '✅ v_content_statistics - 统计视图' as view1; 