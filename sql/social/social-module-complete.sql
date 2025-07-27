-- =============================================
-- Collide Social Module - 完全去连表化设计
-- 社交动态模块数据库表结构
-- 版本: 1.0.0
-- 设计原则: 完全避免连表查询，通过冗余数据换取查询性能
-- =============================================

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `t_social_post_interaction`;
DROP TABLE IF EXISTS `t_social_post`;

-- =============================================
-- 1. 社交动态主表 (完全去连表化设计)
-- =============================================
CREATE TABLE `t_social_post` (
    `id`                        BIGINT AUTO_INCREMENT           COMMENT '动态ID',
    `post_type`                 VARCHAR(20)     NOT NULL        COMMENT '动态类型(TEXT-文本,IMAGE-图片,VIDEO-视频,SHARE-转发)',
    `content`                   TEXT                            COMMENT '动态内容',
    `media_urls`                JSON                            COMMENT '媒体文件URL列表(JSON数组)',
    `location`                  VARCHAR(200)                    COMMENT '位置信息',
    `longitude`                 DECIMAL(10,7)                   COMMENT '经度',
    `latitude`                  DECIMAL(10,7)                   COMMENT '纬度',
    `topics`                    JSON                            COMMENT '话题标签列表(JSON数组)',
    `mentioned_user_ids`        JSON                            COMMENT '提及的用户ID列表(JSON数组)',
    `status`                    VARCHAR(20)     NOT NULL        COMMENT '动态状态(DRAFT-草稿,PUBLISHED-已发布,DELETED-已删除)',
    `visibility`                TINYINT         NOT NULL DEFAULT 0 COMMENT '可见性(0-公开,1-仅关注者,2-仅自己)',
    `allow_comments`            BOOLEAN         NOT NULL DEFAULT TRUE COMMENT '是否允许评论',
    `allow_shares`              BOOLEAN         NOT NULL DEFAULT TRUE COMMENT '是否允许转发',
    
    -- === 作者信息 (冗余字段，避免连表查询) ===
    `author_id`                 BIGINT          NOT NULL        COMMENT '作者用户ID',
    `author_username`           VARCHAR(50)     NOT NULL        COMMENT '作者用户名(冗余)',
    `author_nickname`           VARCHAR(50)     NOT NULL        COMMENT '作者昵称(冗余)',
    `author_avatar`             VARCHAR(500)                    COMMENT '作者头像URL(冗余)',
    `author_verified`           BOOLEAN         NOT NULL DEFAULT FALSE COMMENT '作者认证状态(冗余)',
    
    -- === 统计信息 (冗余字段，避免聚合查询) ===
    `like_count`                BIGINT          NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count`             BIGINT          NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count`               BIGINT          NOT NULL DEFAULT 0 COMMENT '转发数',
    `view_count`                BIGINT          NOT NULL DEFAULT 0 COMMENT '浏览数',
    `favorite_count`            BIGINT          NOT NULL DEFAULT 0 COMMENT '收藏数',
    `hot_score`                 DECIMAL(10,3)   NOT NULL DEFAULT 0.000 COMMENT '热度分数',
    
    -- === 时间信息 ===
    `published_time`            DATETIME                        COMMENT '发布时间',
    `created_time`              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- === 通用字段 ===
    `deleted`                   TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除标志(0-未删除,1-已删除)',
    `version`                   INT             NOT NULL DEFAULT 1 COMMENT '版本号(乐观锁)',
    
    PRIMARY KEY (`id`),
    
    -- === 核心业务索引 ===
    INDEX `idx_author_published` (`author_id`, `status`, `published_time` DESC) COMMENT '作者动态查询索引',
    INDEX `idx_status_published` (`status`, `published_time` DESC) COMMENT '公开动态时间线索引',
    INDEX `idx_hot_score` (`status`, `visibility`, `hot_score` DESC, `published_time` DESC) COMMENT '热门动态索引',
    INDEX `idx_location` (`status`, `visibility`, `longitude`, `latitude`) COMMENT '地理位置索引',
    INDEX `idx_created_time` (`created_time` DESC) COMMENT '创建时间索引',
    INDEX `idx_updated_time` (`updated_time` DESC) COMMENT '更新时间索引',
    
    -- === 复合查询索引 ===
    INDEX `idx_author_status_time` (`author_id`, `status`, `created_time` DESC) COMMENT '用户动态查询复合索引',
    INDEX `idx_visibility_hot` (`visibility`, `status`, `hot_score` DESC) COMMENT '可见性热度复合索引',
    INDEX `idx_type_status_time` (`post_type`, `status`, `published_time` DESC) COMMENT '类型状态时间复合索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='社交动态表-完全去连表化设计';

-- =============================================
-- 2. 用户互动记录表 (用于记录详细的互动关系)
-- =============================================
CREATE TABLE `t_social_post_interaction` (
    `id`                        BIGINT AUTO_INCREMENT           COMMENT '互动记录ID',
    `post_id`                   BIGINT          NOT NULL        COMMENT '动态ID',
    `user_id`                   BIGINT          NOT NULL        COMMENT '用户ID',
    `interaction_type`          VARCHAR(20)     NOT NULL        COMMENT '互动类型(LIKE-点赞,FAVORITE-收藏,SHARE-转发,VIEW-浏览)',
    `interaction_status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '互动状态(0-取消,1-有效)',
    
    -- === 冗余用户信息 (避免连表查询) ===
    `user_nickname`             VARCHAR(50)     NOT NULL        COMMENT '用户昵称(冗余)',
    `user_avatar`               VARCHAR(500)                    COMMENT '用户头像(冗余)',
    
    -- === 冗余动态信息 (避免连表查询) ===
    `post_author_id`            BIGINT          NOT NULL        COMMENT '动态作者ID(冗余)',
    `post_type`                 VARCHAR(20)     NOT NULL        COMMENT '动态类型(冗余)',
    `post_title`                VARCHAR(200)                    COMMENT '动态标题或前50字符(冗余)',
    
    -- === 扩展信息 ===
    `interaction_content`       TEXT                            COMMENT '互动内容(如转发评论)',
    `device_info`               VARCHAR(200)                    COMMENT '设备信息',
    `ip_address`                VARCHAR(45)                     COMMENT 'IP地址',
    
    -- === 时间信息 ===
    `created_time`              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time`              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- === 通用字段 ===
    `deleted`                   TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    
    PRIMARY KEY (`id`),
    
    -- === 唯一约束 ===
    UNIQUE KEY `uk_post_user_type` (`post_id`, `user_id`, `interaction_type`) COMMENT '防重复互动',
    
    -- === 业务索引 ===
    INDEX `idx_post_id` (`post_id`, `interaction_type`, `interaction_status`) COMMENT '动态互动查询索引',
    INDEX `idx_user_id` (`user_id`, `interaction_type`, `created_time` DESC) COMMENT '用户互动历史索引',
    INDEX `idx_created_time` (`created_time` DESC) COMMENT '时间索引',
    INDEX `idx_post_author` (`post_author_id`, `interaction_type`, `created_time` DESC) COMMENT '作者收到的互动索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='社交动态互动记录表';

-- =============================================
-- 3. 初始化数据和配置
-- =============================================

-- 插入测试数据
INSERT INTO `t_social_post` (
    `post_type`, `content`, `status`, `visibility`, `allow_comments`, `allow_shares`,
    `author_id`, `author_username`, `author_nickname`, `author_avatar`, `author_verified`,
    `like_count`, `comment_count`, `share_count`, `view_count`, `favorite_count`, `hot_score`,
    `published_time`, `created_time`
) VALUES 
(
    'TEXT', '欢迎来到Collide社交平台！这是第一条动态分享。', 'PUBLISHED', 0, TRUE, TRUE,
    1, 'admin', '管理员', '/avatar/admin.jpg', TRUE,
    0, 0, 0, 0, 0, 0.000,
    NOW(), NOW()
),
(
    'IMAGE', '分享一张美丽的风景照片 🌄', 'PUBLISHED', 0, TRUE, TRUE,
    2, 'user001', '摄影师小王', '/avatar/user001.jpg', FALSE,
    0, 0, 0, 0, 0, 0.000,
    NOW(), NOW()
);

-- =============================================
-- 4. 性能优化配置
-- =============================================

-- 分区表配置（按月分区）
-- ALTER TABLE `t_social_post_interaction` 
-- PARTITION BY RANGE (TO_DAYS(created_time)) (
--     PARTITION p202401 VALUES LESS THAN (TO_DAYS('2024-02-01')),
--     PARTITION p202402 VALUES LESS THAN (TO_DAYS('2024-03-01')),
--     PARTITION p202403 VALUES LESS THAN (TO_DAYS('2024-04-01')),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- =============================================
-- 5. 数据维护存储过程
-- =============================================

DELIMITER $$

-- 清理过期的浏览记录（保留30天）
CREATE PROCEDURE `CleanOldViewRecords`()
BEGIN
    DELETE FROM `t_social_post_interaction` 
    WHERE `interaction_type` = 'VIEW' 
    AND `created_time` < DATE_SUB(NOW(), INTERVAL 30 DAY);
END$$

-- 重新计算动态热度分数
CREATE PROCEDURE `RecalculateHotScore`(IN post_id BIGINT)
BEGIN
    DECLARE like_weight DECIMAL(5,2) DEFAULT 1.0;
    DECLARE comment_weight DECIMAL(5,2) DEFAULT 2.0;
    DECLARE share_weight DECIMAL(5,2) DEFAULT 3.0;
    DECLARE favorite_weight DECIMAL(5,2) DEFAULT 1.5;
    DECLARE time_decay_factor DECIMAL(5,2) DEFAULT 0.1;
    DECLARE hours_since_publish INT;
    DECLARE new_hot_score DECIMAL(10,3);
    
    -- 计算发布后经过的小时数
    SELECT TIMESTAMPDIFF(HOUR, published_time, NOW()) INTO hours_since_publish
    FROM `t_social_post` 
    WHERE `id` = post_id;
    
    -- 计算新的热度分数
    SELECT (
        like_count * like_weight + 
        comment_count * comment_weight + 
        share_count * share_weight + 
        favorite_count * favorite_weight -
        hours_since_publish * time_decay_factor
    ) INTO new_hot_score
    FROM `t_social_post` 
    WHERE `id` = post_id;
    
    -- 更新热度分数
    UPDATE `t_social_post` 
    SET `hot_score` = GREATEST(new_hot_score, 0.0), 
        `updated_time` = NOW()
    WHERE `id` = post_id;
END$$

-- 批量更新用户信息冗余字段
CREATE PROCEDURE `UpdateUserInfoInPosts`(
    IN user_id BIGINT,
    IN new_username VARCHAR(50),
    IN new_nickname VARCHAR(50),
    IN new_avatar VARCHAR(500),
    IN new_verified BOOLEAN
)
BEGIN
    -- 更新动态表中的冗余用户信息
    UPDATE `t_social_post` 
    SET `author_username` = new_username,
        `author_nickname` = new_nickname,
        `author_avatar` = new_avatar,
        `author_verified` = new_verified,
        `updated_time` = NOW()
    WHERE `author_id` = user_id;
    
    -- 更新互动表中的冗余用户信息
    UPDATE `t_social_post_interaction` 
    SET `user_nickname` = new_nickname,
        `user_avatar` = new_avatar,
        `updated_time` = NOW()
    WHERE `user_id` = user_id;
END$$

DELIMITER ;

-- =============================================
-- 6. 视图定义（只读查询优化）
-- =============================================

-- 热门动态视图
CREATE VIEW `v_hot_posts` AS
SELECT 
    `id` as `post_id`,
    `post_type`,
    `content`,
    `author_id`,
    `author_username`,
    `author_nickname`,
    `author_avatar`,
    `author_verified`,
    `like_count`,
    `comment_count`,
    `share_count`,
    `view_count`,
    `favorite_count`,
    `hot_score`,
    `published_time`,
    `created_time`
FROM `t_social_post`
WHERE `status` = 'PUBLISHED' 
AND `visibility` = 0 
AND `deleted` = 0
ORDER BY `hot_score` DESC, `published_time` DESC;

-- 用户动态统计视图
CREATE VIEW `v_user_post_stats` AS
SELECT 
    `author_id` as `user_id`,
    `author_username` as `username`,
    `author_nickname` as `nickname`,
    COUNT(*) as `total_posts`,
    SUM(`like_count`) as `total_likes`,
    SUM(`comment_count`) as `total_comments`,
    SUM(`share_count`) as `total_shares`,
    SUM(`view_count`) as `total_views`,
    AVG(`hot_score`) as `avg_hot_score`,
    MAX(`published_time`) as `last_post_time`
FROM `t_social_post`
WHERE `status` = 'PUBLISHED' 
AND `deleted` = 0
GROUP BY `author_id`, `author_username`, `author_nickname`;

-- =============================================
-- 7. 触发器定义（数据一致性保证）
-- =============================================

DELIMITER $$

-- 动态发布时自动设置发布时间
CREATE TRIGGER `tr_social_post_publish`
    BEFORE UPDATE ON `t_social_post`
    FOR EACH ROW
BEGIN
    IF OLD.status != 'PUBLISHED' AND NEW.status = 'PUBLISHED' THEN
        SET NEW.published_time = NOW();
    END IF;
END$$

-- 互动记录插入时更新统计数据
CREATE TRIGGER `tr_interaction_insert`
    AFTER INSERT ON `t_social_post_interaction`
    FOR EACH ROW
BEGIN
    IF NEW.interaction_status = 1 THEN
        CASE NEW.interaction_type
            WHEN 'LIKE' THEN
                UPDATE `t_social_post` SET `like_count` = `like_count` + 1 WHERE `id` = NEW.post_id;
            WHEN 'FAVORITE' THEN
                UPDATE `t_social_post` SET `favorite_count` = `favorite_count` + 1 WHERE `id` = NEW.post_id;
            WHEN 'SHARE' THEN
                UPDATE `t_social_post` SET `share_count` = `share_count` + 1 WHERE `id` = NEW.post_id;
            WHEN 'VIEW' THEN
                UPDATE `t_social_post` SET `view_count` = `view_count` + 1 WHERE `id` = NEW.post_id;
        END CASE;
    END IF;
END$$

-- 互动记录更新时同步统计数据
CREATE TRIGGER `tr_interaction_update`
    AFTER UPDATE ON `t_social_post_interaction`
    FOR EACH ROW
BEGIN
    -- 如果状态从有效变为无效
    IF OLD.interaction_status = 1 AND NEW.interaction_status = 0 THEN
        CASE NEW.interaction_type
            WHEN 'LIKE' THEN
                UPDATE `t_social_post` SET `like_count` = GREATEST(`like_count` - 1, 0) WHERE `id` = NEW.post_id;
            WHEN 'FAVORITE' THEN
                UPDATE `t_social_post` SET `favorite_count` = GREATEST(`favorite_count` - 1, 0) WHERE `id` = NEW.post_id;
            WHEN 'SHARE' THEN
                UPDATE `t_social_post` SET `share_count` = GREATEST(`share_count` - 1, 0) WHERE `id` = NEW.post_id;
        END CASE;
    -- 如果状态从无效变为有效
    ELSEIF OLD.interaction_status = 0 AND NEW.interaction_status = 1 THEN
        CASE NEW.interaction_type
            WHEN 'LIKE' THEN
                UPDATE `t_social_post` SET `like_count` = `like_count` + 1 WHERE `id` = NEW.post_id;
            WHEN 'FAVORITE' THEN
                UPDATE `t_social_post` SET `favorite_count` = `favorite_count` + 1 WHERE `id` = NEW.post_id;
            WHEN 'SHARE' THEN
                UPDATE `t_social_post` SET `share_count` = `share_count` + 1 WHERE `id` = NEW.post_id;
        END CASE;
    END IF;
END$$

DELIMITER ;

-- =============================================
-- 8. 数据库配置优化
-- =============================================

-- 设置相关参数
SET GLOBAL innodb_buffer_pool_size = 1073741824; -- 1GB
SET GLOBAL innodb_log_file_size = 268435456;     -- 256MB
SET GLOBAL innodb_flush_log_at_trx_commit = 2;   -- 性能优化
SET GLOBAL query_cache_type = 1;                 -- 启用查询缓存
SET GLOBAL query_cache_size = 67108864;          -- 64MB查询缓存

-- =============================================
-- 执行完成提示
-- =============================================
SELECT 'Social模块数据库初始化完成！' AS message;
SELECT 'Key Features:' AS title, 
       '1. 完全去连表化设计
        2. 冗余存储用户信息
        3. 直接存储统计数据
        4. 优化的索引策略
        5. 自动数据同步触发器
        6. 性能监控视图' AS features; 