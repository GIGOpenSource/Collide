-- -----------------------------------
-- 评论表
-- 存储所有类型内容的评论信息
-- 支持多级评论和树状结构
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID（被评论的内容）',
    `content_type` VARCHAR(50) NOT NULL COMMENT '内容类型：CONTENT、ARTICLE、VIDEO等',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
    `root_id` BIGINT DEFAULT 0 COMMENT '根评论ID，用于快速查询评论树',
    `reply_to_user_id` BIGINT COMMENT '回复的目标用户ID',
    `comment_text` TEXT NOT NULL COMMENT '评论内容',
    `comment_type` VARCHAR(50) NOT NULL DEFAULT 'TEXT' COMMENT '评论类型：TEXT、IMAGE、EMOJI',
    `status` VARCHAR(50) NOT NULL DEFAULT 'PUBLISHED' COMMENT '评论状态：PUBLISHED、HIDDEN、DELETED、PENDING_REVIEW',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
    `is_pinned` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
    `is_hot` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否热门评论',
    `ip_address` VARCHAR(45) COMMENT '评论IP地址',
    `device_info` VARCHAR(200) COMMENT '设备信息',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_root_id` (`root_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_like_count` (`like_count`),
    KEY `idx_is_pinned` (`is_pinned`),
    KEY `idx_is_hot` (`is_hot`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_content_status` (`content_id`, `status`),
    KEY `idx_content_parent` (`content_id`, `parent_id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_content_created` (`content_id`, `created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- -----------------------------------
-- 评论统计表
-- 存储内容的评论统计数据
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_comment_statistics` (
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `content_type` VARCHAR(50) NOT NULL COMMENT '内容类型',
    `total_comment_count` INT NOT NULL DEFAULT 0 COMMENT '总评论数',
    `top_comment_count` INT NOT NULL DEFAULT 0 COMMENT '顶级评论数',
    `today_comment_count` INT NOT NULL DEFAULT 0 COMMENT '今日评论数',
    `this_week_comment_count` INT NOT NULL DEFAULT 0 COMMENT '本周评论数',
    `this_month_comment_count` INT NOT NULL DEFAULT 0 COMMENT '本月评论数',
    `last_comment_time` DATETIME COMMENT '最后评论时间',
    `last_comment_user_id` BIGINT COMMENT '最后评论用户ID',
    `hot_comment_id` BIGINT COMMENT '热门评论ID',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`content_id`, `content_type`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_total_count` (`total_comment_count`),
    KEY `idx_last_comment_time` (`last_comment_time`),
    KEY `idx_updated_time` (`updated_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论统计表';

-- -----------------------------------
-- 评论点赞表
-- 存储用户对评论的点赞记录
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_comment_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `comment_id` BIGINT NOT NULL COMMENT '评论ID',
    `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
    `like_type` TINYINT NOT NULL DEFAULT 1 COMMENT '点赞类型：1-点赞，-1-点踩',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_user` (`comment_id`, `user_id`),
    KEY `idx_comment_id` (`comment_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_like_type` (`like_type`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- -----------------------------------
-- 插入基础测试数据
-- -----------------------------------

-- 插入测试评论数据
INSERT INTO `t_comment` (`content_id`, `content_type`, `user_id`, `parent_id`, `root_id`, `comment_text`, `comment_type`, `status`, `like_count`, `reply_count`) VALUES
(1, 'CONTENT', 1, 0, 0, '这是一个很棒的内容！', 'TEXT', 'PUBLISHED', 5, 2),
(1, 'CONTENT', 2, 1, 1, '我也觉得非常不错', 'TEXT', 'PUBLISHED', 3, 0),
(1, 'CONTENT', 3, 1, 1, '作者很用心', 'TEXT', 'PUBLISHED', 1, 0),
(2, 'CONTENT', 1, 0, 0, '期待下一篇文章', 'TEXT', 'PUBLISHED', 8, 1),
(2, 'CONTENT', 4, 4, 4, '同样期待！', 'TEXT', 'PUBLISHED', 2, 0);

-- 插入评论统计数据
INSERT INTO `t_comment_statistics` (`content_id`, `content_type`, `total_comment_count`, `top_comment_count`, `today_comment_count`, `last_comment_time`, `last_comment_user_id`) VALUES
(1, 'CONTENT', 3, 1, 3, NOW(), 3),
(2, 'CONTENT', 2, 1, 2, NOW(), 4);

-- 插入评论点赞数据
INSERT INTO `t_comment_like` (`comment_id`, `user_id`, `like_type`) VALUES
(1, 2, 1),
(1, 3, 1),
(1, 4, 1),
(1, 5, 1),
(1, 6, 1),
(2, 1, 1),
(2, 4, 1),
(2, 5, 1),
(3, 2, 1),
(4, 2, 1),
(4, 3, 1),
(4, 5, 1),
(4, 6, 1),
(4, 7, 1),
(4, 8, 1),
(4, 9, 1),
(4, 10, 1),
(5, 1, 1),
(5, 3, 1); 