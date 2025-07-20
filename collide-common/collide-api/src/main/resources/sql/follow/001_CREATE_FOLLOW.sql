-- 创建关注表
CREATE TABLE `t_follow` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '关注ID',
    `follower_user_id` BIGINT(20) NOT NULL COMMENT '关注者用户ID',
    `followed_user_id` BIGINT(20) NOT NULL COMMENT '被关注者用户ID',
    `follow_type` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '关注类型：NORMAL-普通关注，SPECIAL-特别关注',
    `status` TINYINT(4) NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-已取消',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follower_followed` (`follower_user_id`, `followed_user_id`),
    KEY `idx_follower_user_id` (`follower_user_id`),
    KEY `idx_followed_user_id` (`followed_user_id`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- 创建关注统计表
CREATE TABLE `t_follow_statistics` (
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `following_count` INT(11) NOT NULL DEFAULT 0 COMMENT '关注数量',
    `follower_count` INT(11) NOT NULL DEFAULT 0 COMMENT '粉丝数量',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注统计表'; 