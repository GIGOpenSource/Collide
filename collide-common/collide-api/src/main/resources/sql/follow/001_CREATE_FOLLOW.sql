-- -----------------------------------
-- 关注表
-- 存储用户之间的关注关系
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_follow` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关注ID',
    `follower_user_id` BIGINT NOT NULL COMMENT '关注者用户ID',
    `followed_user_id` BIGINT NOT NULL COMMENT '被关注者用户ID',
    `follow_type` TINYINT NOT NULL DEFAULT 1 COMMENT '关注类型：1-普通关注，2-特别关注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-已取消，1-正常，2-已屏蔽',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follower_followed` (`follower_user_id`, `followed_user_id`),
    KEY `idx_follower_user_id` (`follower_user_id`),
    KEY `idx_followed_user_id` (`followed_user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注表';

-- -----------------------------------
-- 关注统计表
-- 统计用户的关注数和粉丝数
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_follow_statistics` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `following_count` INT NOT NULL DEFAULT 0 COMMENT '关注数（我关注的人数）',
    `follower_count` INT NOT NULL DEFAULT 0 COMMENT '粉丝数（关注我的人数）',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注统计表';

-- -----------------------------------
-- 插入测试数据（可选）
-- -----------------------------------

-- 插入用户1关注用户2的记录
-- INSERT INTO `t_follow` (`follower_user_id`, `followed_user_id`, `follow_type`, `status`) 
-- VALUES (1, 2, 1, 1);

-- 插入用户2关注用户1的记录（相互关注）
-- INSERT INTO `t_follow` (`follower_user_id`, `followed_user_id`, `follow_type`, `status`) 
-- VALUES (2, 1, 1, 1);

-- 初始化统计数据
-- INSERT INTO `t_follow_statistics` (`user_id`, `following_count`, `follower_count`) 
-- VALUES (1, 1, 1), (2, 1, 1);

-- -----------------------------------
-- 索引说明
-- -----------------------------------
-- uk_follower_followed: 防止重复关注同一用户
-- idx_follower_user_id: 优化查询某用户的关注列表
-- idx_followed_user_id: 优化查询某用户的粉丝列表  
-- idx_status: 优化按状态查询
-- idx_created_time: 优化按时间排序查询 