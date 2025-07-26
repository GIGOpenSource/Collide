-- ================================================
-- Content 模块完整SQL脚本
-- 去连表化设计，冗余存储关联信息，避免JOIN查询
-- 版本：1.0
-- 创建时间：2024-01-01
-- ================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ================================================
-- 1. 创建内容主表 t_content（去连表化设计）
-- ================================================
DROP TABLE IF EXISTS `t_content`;

CREATE TABLE `t_content` (
    -- 基础字段
    `id`                BIGINT      NOT NULL AUTO_INCREMENT COMMENT '内容ID，主键',
    `title`             VARCHAR(200) NOT NULL                COMMENT '内容标题',
    `description`       TEXT                                 COMMENT '内容描述/摘要',
    
    -- 内容相关字段
    `content_type`      VARCHAR(50)  NOT NULL                COMMENT '内容类型：NOVEL/COMIC/SHORT_VIDEO/LONG_VIDEO/ARTICLE/AUDIO',
    `content_data`      LONGTEXT                             COMMENT '内容数据，JSON格式存储',
    `cover_url`         VARCHAR(500)                         COMMENT '封面图片URL',
    `tags`              TEXT                                 COMMENT '标签，JSON数组格式：["标签1","标签2"]',
    
    -- 作者信息（冗余字段，避免与用户表连表）
    `author_id`         BIGINT       NOT NULL                COMMENT '作者用户ID',
    `author_nickname`   VARCHAR(50)                          COMMENT '作者昵称（冗余字段，去连表化设计）',
    `author_avatar`     VARCHAR(500)                         COMMENT '作者头像URL（冗余字段，去连表化设计）',
    
    -- 分类信息（冗余字段，避免与分类表连表）
    `category_id`       BIGINT                               COMMENT '分类ID',
    `category_name`     VARCHAR(100)                         COMMENT '分类名称（冗余字段，去连表化设计）',
    
    -- 状态相关字段
    `status`            VARCHAR(50)  NOT NULL DEFAULT 'DRAFT' COMMENT '内容状态：DRAFT/PENDING/PUBLISHED/REJECTED/OFFLINE',
    `review_status`     VARCHAR(50)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING/APPROVED/REJECTED',
    `review_comment`    TEXT                                 COMMENT '审核意见',
    `reviewer_id`       BIGINT                               COMMENT '审核员ID',
    `reviewed_time`     DATETIME                             COMMENT '审核时间',
    
    -- 统计字段（冗余存储，避免聚合查询）
    `view_count`        BIGINT       NOT NULL DEFAULT 0      COMMENT '查看数',
    `like_count`        BIGINT       NOT NULL DEFAULT 0      COMMENT '点赞数',
    `dislike_count`     BIGINT       NOT NULL DEFAULT 0      COMMENT '点踩数',
    `comment_count`     BIGINT       NOT NULL DEFAULT 0      COMMENT '评论数',
    `share_count`       BIGINT       NOT NULL DEFAULT 0      COMMENT '分享数',
    `favorite_count`    BIGINT       NOT NULL DEFAULT 0      COMMENT '收藏数',
    
    -- 推荐相关字段
    `weight_score`      DOUBLE       NOT NULL DEFAULT 0.0    COMMENT '权重分数，用于推荐算法',
    `is_recommended`    TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否推荐：0-否，1-是',
    `is_pinned`         TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否置顶：0-否，1-是',
    
    -- 功能开关字段
    `allow_comment`     TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否允许评论：0-否，1-是',
    `allow_share`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否允许分享：0-否，1-是',
    
    -- 时间字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `published_time`    DATETIME                             COMMENT '发布时间',
    `deleted`           INT          NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    
    -- 核心业务索引
    INDEX `idx_author_content` (`author_id`, `status`, `deleted`, `create_time` DESC) COMMENT '作者内容查询索引',
    INDEX `idx_category_content` (`category_id`, `status`, `deleted`, `published_time` DESC) COMMENT '分类内容查询索引',
    INDEX `idx_status_review` (`status`, `review_status`, `deleted`) COMMENT '状态审核查询索引',
    INDEX `idx_content_type` (`content_type`, `status`, `deleted`, `published_time` DESC) COMMENT '内容类型查询索引',
    
    -- 推荐系统索引
    INDEX `idx_recommended` (`is_recommended`, `weight_score` DESC, `deleted`) COMMENT '推荐内容查询索引',
    INDEX `idx_hot_content` (`status`, `deleted`, `like_count` DESC, `view_count` DESC) COMMENT '热门内容排序索引',
    INDEX `idx_latest_content` (`status`, `deleted`, `published_time` DESC) COMMENT '最新内容查询索引',
    
    -- 统计查询索引
    INDEX `idx_statistics` (`deleted`, `published_time`, `view_count`) COMMENT '统计查询索引',
    INDEX `idx_review_queue` (`review_status`, `create_time` ASC, `deleted`) COMMENT '审核队列索引'
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容主表（去连表化设计）';

-- ================================================
-- 2. 创建内容点赞记录表 t_content_like（幂等性控制）
-- ================================================
DROP TABLE IF EXISTS `t_content_like`;

CREATE TABLE `t_content_like` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
    `content_id`    BIGINT      NOT NULL                COMMENT '内容ID',
    `user_id`       BIGINT      NOT NULL                COMMENT '用户ID',
    `created_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    `deleted`       INT         NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_user_like` (`content_id`, `user_id`, `deleted`) COMMENT '用户内容点赞唯一约束',
    INDEX `idx_content_like` (`content_id`, `deleted`, `created_time` DESC) COMMENT '内容点赞查询索引',
    INDEX `idx_user_like` (`user_id`, `deleted`, `created_time` DESC) COMMENT '用户点赞查询索引'
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容点赞记录表';

-- ================================================
-- 3. 创建内容收藏记录表 t_content_favorite（幂等性控制）
-- ================================================
DROP TABLE IF EXISTS `t_content_favorite`;

CREATE TABLE `t_content_favorite` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
    `content_id`    BIGINT      NOT NULL                COMMENT '内容ID',
    `user_id`       BIGINT      NOT NULL                COMMENT '用户ID',
    `created_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    `deleted`       INT         NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_user_favorite` (`content_id`, `user_id`, `deleted`) COMMENT '用户内容收藏唯一约束',
    INDEX `idx_content_favorite` (`content_id`, `deleted`, `created_time` DESC) COMMENT '内容收藏查询索引',
    INDEX `idx_user_favorite` (`user_id`, `deleted`, `created_time` DESC) COMMENT '用户收藏查询索引'
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容收藏记录表';

-- ================================================
-- 4. 创建内容分享记录表 t_content_share（记录分享行为）
-- ================================================
DROP TABLE IF EXISTS `t_content_share`;

CREATE TABLE `t_content_share` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
    `content_id`    BIGINT      NOT NULL                COMMENT '内容ID',
    `user_id`       BIGINT      NOT NULL                COMMENT '用户ID',
    `platform`      VARCHAR(50)                         COMMENT '分享平台：WECHAT/WEIBO/QQ/LINK等',
    `share_text`    TEXT                                COMMENT '分享文案',
    `created_time`  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分享时间',
    `deleted`       INT         NOT NULL DEFAULT 0      COMMENT '逻辑删除标记：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    INDEX `idx_content_share` (`content_id`, `deleted`, `created_time` DESC) COMMENT '内容分享查询索引',
    INDEX `idx_user_share` (`user_id`, `deleted`, `created_time` DESC) COMMENT '用户分享查询索引',
    INDEX `idx_platform_share` (`platform`, `deleted`, `created_time` DESC) COMMENT '平台分享统计索引'
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='内容分享记录表';

-- ================================================
-- 5. 创建内容统计视图 v_content_statistics（实时统计）
-- ================================================
DROP VIEW IF EXISTS `v_content_statistics`;

CREATE VIEW `v_content_statistics` AS
SELECT 
    c.id AS content_id,
    c.title,
    c.content_type,
    c.author_id,
    c.author_nickname,
    c.category_id,
    c.category_name,
    c.status,
    c.published_time,
    
    -- 直接统计数据（来自主表冗余字段）
    c.view_count AS total_views,
    c.like_count AS total_likes,
    c.dislike_count AS total_dislikes,
    c.comment_count AS total_comments,
    c.share_count AS total_shares,
    c.favorite_count AS total_favorites,
    
    -- 实时统计数据（来自交互记录表）
    IFNULL(like_stats.real_like_count, 0) AS real_like_count,
    IFNULL(favorite_stats.real_favorite_count, 0) AS real_favorite_count,
    IFNULL(share_stats.real_share_count, 0) AS real_share_count,
    
    -- 今日统计
    IFNULL(today_stats.today_likes, 0) AS today_likes,
    IFNULL(today_stats.today_favorites, 0) AS today_favorites,
    IFNULL(today_stats.today_shares, 0) AS today_shares,
    
    -- 互动评分
    c.weight_score,
    (c.like_count + c.comment_count * 2 + c.share_count * 3 + c.favorite_count * 2 - c.dislike_count) AS engagement_score,
    
    c.create_time,
    c.update_time
    
FROM t_content c

-- 实时点赞统计
LEFT JOIN (
    SELECT content_id, COUNT(*) AS real_like_count
    FROM t_content_like 
    WHERE deleted = 0 
    GROUP BY content_id
) like_stats ON c.id = like_stats.content_id

-- 实时收藏统计
LEFT JOIN (
    SELECT content_id, COUNT(*) AS real_favorite_count
    FROM t_content_favorite 
    WHERE deleted = 0 
    GROUP BY content_id
) favorite_stats ON c.id = favorite_stats.content_id

-- 实时分享统计
LEFT JOIN (
    SELECT content_id, COUNT(*) AS real_share_count
    FROM t_content_share 
    WHERE deleted = 0 
    GROUP BY content_id
) share_stats ON c.id = share_stats.content_id

-- 今日统计
LEFT JOIN (
    SELECT 
        l.content_id,
        COUNT(DISTINCT l.id) AS today_likes,
        COUNT(DISTINCT f.id) AS today_favorites,
        COUNT(DISTINCT s.id) AS today_shares
    FROM t_content_like l
    LEFT JOIN t_content_favorite f ON l.content_id = f.content_id 
        AND f.deleted = 0 AND DATE(f.created_time) = CURDATE()
    LEFT JOIN t_content_share s ON l.content_id = s.content_id 
        AND s.deleted = 0 AND DATE(s.created_time) = CURDATE()
    WHERE l.deleted = 0 AND DATE(l.created_time) = CURDATE()
    GROUP BY l.content_id
) today_stats ON c.id = today_stats.content_id

WHERE c.deleted = 0;

-- ================================================
-- 6. 数据初始化（测试数据）
-- ================================================

-- 插入示例内容数据
INSERT INTO `t_content` (
    `title`, `description`, `content_type`, `content_data`, `cover_url`,
    `author_id`, `author_nickname`, `author_avatar`,
    `category_id`, `category_name`, `tags`,
    `status`, `review_status`, `view_count`, `like_count`,
    `allow_comment`, `allow_share`, `published_time`
) VALUES 
(
    '修仙传奇：逆天之路', 
    '一个关于修仙的精彩故事，讲述主角从凡人到仙人的传奇经历...', 
    'NOVEL',
    '{"totalChapters": 100, "totalWords": 200000, "writingStatus": "ONGOING", "lastUpdateChapter": "第10章 初入宗门"}',
    'https://example.com/covers/novel1.jpg',
    1001, '知名作者', 'https://example.com/avatars/author1.jpg',
    1, '玄幻小说', '["玄幻","修仙","热血","升级流"]',
    'PUBLISHED', 'APPROVED', 15000, 1200,
    1, 1, '2024-01-01 10:00:00'
),
(
    '都市异能：重生之路', 
    '重生归来，都市异能觉醒，重新书写人生...', 
    'NOVEL',
    '{"totalChapters": 50, "totalWords": 100000, "writingStatus": "ONGOING", "lastUpdateChapter": "第15章 异能觉醒"}',
    'https://example.com/covers/novel2.jpg',
    1002, '新星作者', 'https://example.com/avatars/author2.jpg',
    2, '都市异能', '["都市","异能","重生","爽文"]',
    'PUBLISHED', 'APPROVED', 8500, 680,
    1, 1, '2024-01-02 14:30:00'
),
(
    '搞笑日常漫画', 
    '轻松搞笑的校园日常故事...', 
    'COMIC',
    '{"totalEpisodes": 30, "style": "COLOR", "orientation": "VERTICAL", "lastUpdateEpisode": "第30话 考试风波"}',
    'https://example.com/covers/comic1.jpg',
    1003, '漫画家', 'https://example.com/avatars/author3.jpg',
    10, '搞笑漫画', '["搞笑","校园","日常","治愈"]',
    'PUBLISHED', 'APPROVED', 12000, 950,
    1, 1, '2024-01-03 09:15:00'
);

-- 插入示例交互数据
INSERT INTO `t_content_like` (`content_id`, `user_id`) VALUES 
(1, 2001), (1, 2002), (1, 2003), (1, 2004), (1, 2005),
(2, 2001), (2, 2006), (2, 2007),
(3, 2002), (3, 2008), (3, 2009), (3, 2010);

INSERT INTO `t_content_favorite` (`content_id`, `user_id`) VALUES 
(1, 2001), (1, 2003), (1, 2005),
(2, 2001), (2, 2007),
(3, 2002), (3, 2010);

INSERT INTO `t_content_share` (`content_id`, `user_id`, `platform`, `share_text`) VALUES 
(1, 2001, 'WECHAT', '推荐一本超好看的修仙小说！'),
(1, 2002, 'WEIBO', '#修仙传奇# 这本书太精彩了！'),
(2, 2001, 'QQ', '都市异能题材，很不错的小说'),
(3, 2002, 'LINK', '分享一个搞笑漫画');

-- ================================================
-- 7. 存储过程（性能优化）
-- ================================================

-- 原子更新内容统计数据的存储过程
DELIMITER $$

DROP PROCEDURE IF EXISTS `UpdateContentStatistics`$$

CREATE PROCEDURE `UpdateContentStatistics`(
    IN p_content_id BIGINT,
    IN p_action VARCHAR(20),  -- 'like', 'unlike', 'favorite', 'unfavorite', 'share', 'view'
    IN p_increment INT DEFAULT 1
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    CASE p_action
        WHEN 'like' THEN
            UPDATE t_content SET like_count = like_count + p_increment, update_time = CURRENT_TIMESTAMP WHERE id = p_content_id;
        WHEN 'unlike' THEN
            UPDATE t_content SET like_count = GREATEST(like_count - p_increment, 0), update_time = CURRENT_TIMESTAMP WHERE id = p_content_id;
        WHEN 'favorite' THEN
            UPDATE t_content SET favorite_count = favorite_count + p_increment, update_time = CURRENT_TIMESTAMP WHERE id = p_content_id;
        WHEN 'unfavorite' THEN
            UPDATE t_content SET favorite_count = GREATEST(favorite_count - p_increment, 0), update_time = CURRENT_TIMESTAMP WHERE id = p_content_id;
        WHEN 'share' THEN
            UPDATE t_content SET share_count = share_count + p_increment, update_time = CURRENT_TIMESTAMP WHERE id = p_content_id;
        WHEN 'view' THEN
            UPDATE t_content SET view_count = view_count + p_increment, update_time = CURRENT_TIMESTAMP WHERE id = p_content_id;
        ELSE
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid action type';
    END CASE;
    
    COMMIT;
END$$

DELIMITER ;

-- ================================================
-- 8. 数据一致性校验
-- ================================================

-- 创建数据一致性校验存储过程
DELIMITER $$

DROP PROCEDURE IF EXISTS `VerifyContentStatistics`$$

CREATE PROCEDURE `VerifyContentStatistics`()
BEGIN
    -- 校验点赞数据一致性
    SELECT 
        'like_count_mismatch' AS issue_type,
        c.id AS content_id,
        c.like_count AS table_count,
        IFNULL(real_count.cnt, 0) AS real_count
    FROM t_content c
    LEFT JOIN (
        SELECT content_id, COUNT(*) AS cnt 
        FROM t_content_like 
        WHERE deleted = 0 
        GROUP BY content_id
    ) real_count ON c.id = real_count.content_id
    WHERE c.deleted = 0 
    AND c.like_count != IFNULL(real_count.cnt, 0)
    
    UNION ALL
    
    -- 校验收藏数据一致性
    SELECT 
        'favorite_count_mismatch' AS issue_type,
        c.id AS content_id,
        c.favorite_count AS table_count,
        IFNULL(real_count.cnt, 0) AS real_count
    FROM t_content c
    LEFT JOIN (
        SELECT content_id, COUNT(*) AS cnt 
        FROM t_content_favorite 
        WHERE deleted = 0 
        GROUP BY content_id
    ) real_count ON c.id = real_count.content_id
    WHERE c.deleted = 0 
    AND c.favorite_count != IFNULL(real_count.cnt, 0);
    
END$$

DELIMITER ;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- SQL脚本执行完成
-- ================================================

SELECT 'Content模块数据库初始化完成！' AS status,
       '已创建表：t_content, t_content_like, t_content_favorite, t_content_share' AS tables_created,
       '已创建视图：v_content_statistics' AS views_created,
       '已创建存储过程：UpdateContentStatistics, VerifyContentStatistics' AS procedures_created; 