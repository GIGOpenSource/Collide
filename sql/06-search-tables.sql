-- ===================================
-- 搜索模块相关表
-- ===================================

-- -----------------------------------
-- 搜索历史表
-- 记录用户搜索历史
-- -----------------------------------
CREATE TABLE IF NOT EXISTS `t_search_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `keyword` VARCHAR(255) NOT NULL COMMENT '搜索关键词',
    `search_type` VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '搜索类型：ALL-综合搜索, USER-用户搜索, CONTENT-内容搜索, COMMENT-评论搜索',
    `content_type` VARCHAR(20) COMMENT '内容类型过滤：NOVEL-小说, COMIC-漫画, SHORT_VIDEO-短视频, LONG_VIDEO-长视频',
    `result_count` BIGINT NOT NULL DEFAULT 0 COMMENT '搜索结果数量',
    `search_time` BIGINT NOT NULL DEFAULT 0 COMMENT '搜索耗时（毫秒）',
    `ip_address` VARCHAR(45) COMMENT 'IP地址',
    `device_info` VARCHAR(500) COMMENT '设备信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_keyword` (`keyword`),
    KEY `idx_search_type` (`search_type`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_user_keyword` (`user_id`, `keyword`),
    KEY `idx_keyword_time` (`keyword`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';

-- -----------------------------------
-- 搜索统计表
-- 记录热门搜索关键词和统计信息
-- -----------------------------------
CREATE TABLE IF NOT EXISTS `t_search_statistics` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `keyword` VARCHAR(255) NOT NULL COMMENT '搜索关键词',
    `search_count` BIGINT NOT NULL DEFAULT 1 COMMENT '搜索次数',
    `user_count` BIGINT NOT NULL DEFAULT 1 COMMENT '搜索用户数',
    `last_search_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_keyword` (`keyword`),
    KEY `idx_search_count` (`search_count`),
    KEY `idx_user_count` (`user_count`),
    KEY `idx_last_search_time` (`last_search_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索统计表';

-- -----------------------------------
-- 搜索建议表
-- 存储搜索建议词（可以手动配置）
-- -----------------------------------
CREATE TABLE IF NOT EXISTS `t_search_suggestion` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '建议ID',
    `keyword` VARCHAR(255) NOT NULL COMMENT '建议关键词',
    `suggestion_type` VARCHAR(20) NOT NULL DEFAULT 'KEYWORD' COMMENT '建议类型：KEYWORD-关键词建议, USER-用户建议, TAG-标签建议',
    `search_count` BIGINT NOT NULL DEFAULT 0 COMMENT '搜索次数',
    `weight` DOUBLE NOT NULL DEFAULT 1.0 COMMENT '权重（用于排序）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_keyword_type` (`keyword`, `suggestion_type`),
    KEY `idx_suggestion_type` (`suggestion_type`),
    KEY `idx_search_count` (`search_count`),
    KEY `idx_weight` (`weight`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索建议表';

-- 为内容表添加全文索引（用于搜索优化）
-- 注意：如果表已存在，需要检查是否已有这些索引
ALTER TABLE `t_content` 
ADD FULLTEXT INDEX `ft_title_description` (`title`, `description`),
ADD INDEX `idx_title` (`title`),
ADD INDEX `idx_author_id` (`author_id`),
ADD INDEX `idx_content_type` (`content_type`),
ADD INDEX `idx_status_review` (`status`, `review_status`),
ADD INDEX `idx_published_time` (`published_time`),
ADD INDEX `idx_view_count` (`view_count`),
ADD INDEX `idx_like_count` (`like_count`);

-- 为用户表添加搜索索引
ALTER TABLE `t_user` 
ADD INDEX `idx_username` (`username`),
ADD INDEX `idx_nickname` (`nickname`),
ADD INDEX `idx_status` (`status`);

-- 为评论表添加搜索索引
ALTER TABLE `t_comment` 
ADD FULLTEXT INDEX `ft_content` (`content`),
ADD INDEX `idx_target_id` (`target_id`),
ADD INDEX `idx_user_id` (`user_id`),
ADD INDEX `idx_status` (`status`),
ADD INDEX `idx_create_time` (`create_time`); 