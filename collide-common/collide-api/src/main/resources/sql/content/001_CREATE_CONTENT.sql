-- -----------------------------------
-- 内容表
-- 存储平台所有类型的内容信息
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_content` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `title` VARCHAR(200) NOT NULL COMMENT '内容标题',
    `description` TEXT COMMENT '内容描述/摘要',
    `content_type` VARCHAR(50) NOT NULL COMMENT '内容类型：NOVEL、COMIC、SHORT_VIDEO、LONG_VIDEO、ARTICLE、AUDIO',
    `content_data` LONGTEXT COMMENT '内容数据（JSON格式存储）',
    `cover_url` VARCHAR(500) COMMENT '封面图片URL',
    `author_id` BIGINT NOT NULL COMMENT '作者用户ID',
    `category_id` BIGINT COMMENT '分类ID',
    `tags` TEXT COMMENT '标签（JSON数组格式存储）',
    `status` VARCHAR(50) NOT NULL DEFAULT 'DRAFT' COMMENT '内容状态：DRAFT、PENDING、PUBLISHED、REJECTED、OFFLINE',
    `review_status` VARCHAR(50) NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING、APPROVED、REJECTED',
    `review_comment` TEXT COMMENT '审核意见',
    `reviewer_id` BIGINT COMMENT '审核员ID',
    `reviewed_time` DATETIME COMMENT '审核时间',
    `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '查看数',
    `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count` BIGINT NOT NULL DEFAULT 0 COMMENT '分享数',
    `favorite_count` BIGINT NOT NULL DEFAULT 0 COMMENT '收藏数',
    `weight_score` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '权重分数（用于推荐算法）',
    `is_recommended` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否推荐',
    `is_pinned` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
    `allow_comment` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否允许评论',
    `allow_share` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否允许分享',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `published_time` DATETIME COMMENT '发布时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_status` (`status`),
    KEY `idx_review_status` (`review_status`),
    KEY `idx_published_time` (`published_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_weight_score` (`weight_score`),
    KEY `idx_view_count` (`view_count`),
    KEY `idx_like_count` (`like_count`),
    KEY `idx_is_recommended` (`is_recommended`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_author_status` (`author_id`, `status`),
    KEY `idx_type_status` (`content_type`, `status`),
    KEY `idx_status_published` (`status`, `published_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容信息表';

-- -----------------------------------
-- 内容分类表
-- 存储内容的分类信息
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_content_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT COMMENT '父分类ID，NULL表示顶级分类',
    `description` TEXT COMMENT '分类描述',
    `icon_url` VARCHAR(500) COMMENT '分类图标URL',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_deleted` (`deleted`),
    UNIQUE KEY `uk_name_parent` (`name`, `parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容分类表';

-- -----------------------------------
-- 内容互动表
-- 存储用户对内容的互动行为（点赞、收藏等）
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_content_interaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '互动ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `interaction_type` VARCHAR(50) NOT NULL COMMENT '互动类型：LIKE、FAVORITE、SHARE、VIEW',
    `interaction_data` TEXT COMMENT '互动数据（JSON格式，用于存储扩展信息）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_interaction_type` (`interaction_type`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_deleted` (`deleted`),
    UNIQUE KEY `uk_user_content_type` (`user_id`, `content_id`, `interaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容互动表';

-- -----------------------------------
-- 内容评论表
-- 存储用户对内容的评论信息
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_content_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `parent_id` BIGINT COMMENT '父评论ID，NULL表示一级评论',
    `reply_to_id` BIGINT COMMENT '回复的评论ID',
    `comment_text` TEXT NOT NULL COMMENT '评论内容',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
    `status` VARCHAR(50) NOT NULL DEFAULT 'NORMAL' COMMENT '评论状态：NORMAL、HIDDEN、DELETED',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_reply_to_id` (`reply_to_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_content_status` (`content_id`, `status`),
    KEY `idx_content_create` (`content_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容评论表';

-- -----------------------------------
-- 内容统计表
-- 存储内容的详细统计数据
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_content_statistics` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `stat_date` DATE NOT NULL COMMENT '统计日期',
    `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '当日查看数',
    `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '当日点赞数',
    `comment_count` BIGINT NOT NULL DEFAULT 0 COMMENT '当日评论数',
    `share_count` BIGINT NOT NULL DEFAULT 0 COMMENT '当日分享数',
    `favorite_count` BIGINT NOT NULL DEFAULT 0 COMMENT '当日收藏数',
    `avg_view_duration` DECIMAL(10,2) COMMENT '平均观看时长（秒）',
    `bounce_rate` DECIMAL(5,2) COMMENT '跳出率',
    `completion_rate` DECIMAL(5,2) COMMENT '完成率（适用于视频/音频）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_stat_date` (`stat_date`),
    KEY `idx_content_date` (`content_id`, `stat_date`),
    UNIQUE KEY `uk_content_date` (`content_id`, `stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容统计表';

-- -----------------------------------
-- 插入初始分类数据
-- -----------------------------------

INSERT INTO `t_content_category` (`name`, `parent_id`, `description`, `sort_order`) VALUES
('小说', NULL, '各类小说作品', 1),
('漫画', NULL, '漫画作品', 2),
('视频', NULL, '视频内容', 3),
('图文', NULL, '图文内容', 4),
('音频', NULL, '音频内容', 5),
('玄幻', 1, '玄幻小说', 1),
('都市', 1, '都市小说', 2),
('历史', 1, '历史小说', 3),
('科幻', 1, '科幻小说', 4),
('日漫', 2, '日式漫画', 1),
('国漫', 2, '国产漫画', 2),
('短视频', 3, '短视频内容', 1),
('长视频', 3, '长视频内容', 2),
('教程', 4, '教程类图文', 1),
('资讯', 4, '资讯类图文', 2),
('音乐', 5, '音乐作品', 1),
('有声书', 5, '有声读物', 2);

-- 执行完成提示
SELECT 'Content 相关表创建完成！' AS message; 