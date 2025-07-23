-- ==========================================
-- Collide 项目数据库初始化脚本
-- ==========================================

-- 设置字符集和时区
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET time_zone = '+08:00';

-- ==========================================
-- 用户相关表
-- ==========================================

-- 用户表
CREATE TABLE IF NOT EXISTS `t_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `bio` varchar(500) DEFAULT NULL COMMENT '个人简介',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户操作流表
CREATE TABLE IF NOT EXISTS `t_user_operate_stream` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `operate_type` varchar(50) NOT NULL COMMENT '操作类型',
  `operate_data` json DEFAULT NULL COMMENT '操作数据',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operate_type` (`operate_type`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作流表';

-- ==========================================
-- 关注相关表
-- ==========================================

-- 关注关系表
CREATE TABLE IF NOT EXISTS `t_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_user_id` bigint NOT NULL COMMENT '关注者用户ID',
  `followed_user_id` bigint NOT NULL COMMENT '被关注者用户ID',
  `follow_type` varchar(20) DEFAULT 'NORMAL' COMMENT '关注类型：NORMAL-普通关注',
  `status` tinyint DEFAULT '1' COMMENT '状态：0-已取消，1-正常',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followed` (`follower_user_id`, `followed_user_id`),
  KEY `idx_followed_user_id` (`followed_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表';

-- 关注统计表
CREATE TABLE IF NOT EXISTS `t_follow_statistics` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `following_count` int DEFAULT '0' COMMENT '关注数',
  `follower_count` int DEFAULT '0' COMMENT '粉丝数',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注统计表';

-- ==========================================
-- 内容相关表
-- ==========================================

-- 内容表（包含自己的统计字段，避免连表）
CREATE TABLE IF NOT EXISTS `t_content` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `description` varchar(1000) DEFAULT NULL COMMENT '描述',
  `content_type` varchar(20) NOT NULL COMMENT '内容类型：NOVEL-小说，COMIC-漫画，SHORT_VIDEO-短视频，LONG_VIDEO-长视频',
  `content_data` json NOT NULL COMMENT '内容数据',
  `cover_url` varchar(500) DEFAULT NULL COMMENT '封面URL',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `tags` json DEFAULT NULL COMMENT '标签',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，PENDING-待审核，PUBLISHED-已发布，REJECTED-已拒绝',
  `review_status` varchar(20) DEFAULT 'PENDING' COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
  `view_count` bigint DEFAULT '0' COMMENT '查看数',
  `like_count` bigint DEFAULT '0' COMMENT '点赞数',
  `dislike_count` bigint DEFAULT '0' COMMENT '点踩数',
  `comment_count` bigint DEFAULT '0' COMMENT '评论数',
  `share_count` bigint DEFAULT '0' COMMENT '分享数',
  `favorite_count` bigint DEFAULT '0' COMMENT '收藏数',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `published_time` datetime DEFAULT NULL COMMENT '发布时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_status` (`status`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_published_time` (`published_time`),
  KEY `idx_like_count` (`like_count`),
  KEY `idx_view_count` (`view_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';

-- 内容审核表
CREATE TABLE IF NOT EXISTS `t_content_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `content_id` bigint NOT NULL COMMENT '内容ID',
  `reviewer_id` bigint DEFAULT NULL COMMENT '审核员ID',
  `review_status` varchar(20) NOT NULL COMMENT '审核状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
  `review_comment` varchar(1000) DEFAULT NULL COMMENT '审核意见',
  `review_round` int DEFAULT '1' COMMENT '审核轮次',
  `review_data` json DEFAULT NULL COMMENT '审核数据',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_reviewer_id` (`reviewer_id`),
  KEY `idx_review_status` (`review_status`),
  KEY `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核表';

-- ==========================================
-- 评论相关表（完全去连表化设计，包含统计冗余字段）
-- ==========================================

-- 评论表
CREATE TABLE IF NOT EXISTS `t_comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `comment_type` VARCHAR(50) NOT NULL COMMENT '评论类型：CONTENT、REPLY、SOCIAL',
    `target_id` BIGINT NOT NULL COMMENT '目标ID（内容ID等）',
    `parent_comment_id` BIGINT DEFAULT 0 COMMENT '父评论ID，0表示顶级评论',
    `root_comment_id` BIGINT DEFAULT 0 COMMENT '根评论ID，用于快速查询评论树',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
    `reply_to_user_id` BIGINT COMMENT '回复的目标用户ID',
    `status` VARCHAR(50) NOT NULL DEFAULT 'NORMAL' COMMENT '评论状态：NORMAL、DELETED、BLOCKED',
    `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数（冗余字段，避免连表查询）',
    `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数（冗余字段，避免连表查询）',
    `is_pinned` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否置顶',
    `is_hot` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否热门评论',
    `ip_address` VARCHAR(45) COMMENT '评论IP地址',
    `device_info` VARCHAR(200) COMMENT '设备信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_comment_type` (`comment_type`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_comment_id` (`parent_comment_id`),
    KEY `idx_root_comment_id` (`root_comment_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_like_count` (`like_count`),
    KEY `idx_is_pinned` (`is_pinned`),
    KEY `idx_is_hot` (`is_hot`),
    KEY `idx_is_deleted` (`is_deleted`),
    KEY `idx_target_status` (`target_id`, `status`),
    KEY `idx_target_parent` (`target_id`, `parent_comment_id`),
    KEY `idx_user_status` (`user_id`, `status`),
    KEY `idx_target_created` (`target_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- ==========================================
-- 统一点赞表（处理所有类型的点赞，无连表依赖）
-- ==========================================

-- 点赞表
CREATE TABLE IF NOT EXISTS `t_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT NOT NULL COMMENT '目标对象ID（内容ID、评论ID等）',
    `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型：CONTENT、COMMENT、SOCIAL_POST、USER',
    `action_type` TINYINT NOT NULL DEFAULT 1 COMMENT '操作类型：1-点赞，0-取消，-1-点踩',
    `ip_address` VARCHAR(45) COMMENT '点赞IP地址',
    `device_info` VARCHAR(200) COMMENT '设备信息',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target_type` (`user_id`, `target_id`, `target_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_action_type` (`action_type`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_target_action` (`target_id`, `target_type`, `action_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一点赞表';

-- ==========================================
-- 社交动态相关表（完全去连表化设计）
-- ==========================================

-- 社交动态表
CREATE TABLE IF NOT EXISTS `t_social_post` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '动态ID',
    `post_type` varchar(20) NOT NULL COMMENT '动态类型：TEXT-文字，IMAGE-图片，VIDEO-视频，LINK-链接，ARTICLE-文章，AUDIO-音频，POLL-投票，LOCATION-位置',
    `content` text NOT NULL COMMENT '动态内容',
    `media_urls` json DEFAULT NULL COMMENT '媒体文件URL列表',
    `location` varchar(200) DEFAULT NULL COMMENT '位置信息',
    `longitude` double DEFAULT NULL COMMENT '经度',
    `latitude` double DEFAULT NULL COMMENT '纬度',
    `topics` json DEFAULT NULL COMMENT '话题标签列表',
    `mentioned_user_ids` json DEFAULT NULL COMMENT '提及的用户ID列表',
    `status` varchar(20) DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，HIDDEN-已隐藏，DELETED-已删除',
    `visibility` tinyint DEFAULT '0' COMMENT '可见性：0-公开，1-仅关注者，2-仅自己',
    `allow_comments` boolean DEFAULT true COMMENT '是否允许评论',
    `allow_shares` boolean DEFAULT true COMMENT '是否允许转发',
    
    -- 作者信息（冗余，避免连接用户表）
    `author_id` bigint NOT NULL COMMENT '作者用户ID',
    `author_username` varchar(50) NOT NULL COMMENT '作者用户名（冗余）',
    `author_nickname` varchar(100) DEFAULT NULL COMMENT '作者昵称（冗余）',
    `author_avatar` varchar(500) DEFAULT NULL COMMENT '作者头像URL（冗余）',
    `author_verified` boolean DEFAULT false COMMENT '作者认证状态（冗余）',
    
    -- 统计信息（冗余，避免连接统计表）
    `like_count` bigint DEFAULT '0' COMMENT '点赞数',
    `comment_count` bigint DEFAULT '0' COMMENT '评论数',
    `share_count` bigint DEFAULT '0' COMMENT '转发数',
    `view_count` bigint DEFAULT '0' COMMENT '浏览数',
    `favorite_count` bigint DEFAULT '0' COMMENT '收藏数',
    `hot_score` double DEFAULT '0' COMMENT '热度分数',
    
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `published_time` datetime DEFAULT NULL COMMENT '发布时间',
    `deleted` boolean DEFAULT false COMMENT '逻辑删除标志',
    `version` int DEFAULT '1' COMMENT '版本号',
    
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`),
    KEY `idx_visibility` (`visibility`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_hot_score` (`hot_score`),
    KEY `idx_location` (`longitude`, `latitude`),
    KEY `idx_author_status_time` (`author_id`, `status`, `created_time`),
    KEY `idx_status_visibility_hot` (`status`, `visibility`, `hot_score`),
    FULLTEXT KEY `ft_content` (`content`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态表（去连表化设计）';

-- 社交动态互动记录表
CREATE TABLE IF NOT EXISTS `t_social_post_interaction` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '互动ID',
    `post_id` bigint NOT NULL COMMENT '动态ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `interaction_type` varchar(20) NOT NULL COMMENT '互动类型：LIKE-点赞，SHARE-转发，FAVORITE-收藏，REPORT-举报',
    `status` tinyint DEFAULT '1' COMMENT '状态：0-取消，1-有效',
    `extra_data` json DEFAULT NULL COMMENT '额外数据',
    
    -- 冗余信息（避免连表）
    `post_author_id` bigint NOT NULL COMMENT '动态作者ID（冗余）',
    `post_author_nickname` varchar(100) DEFAULT NULL COMMENT '动态作者昵称（冗余）',
    `user_nickname` varchar(100) DEFAULT NULL COMMENT '用户昵称（冗余）',
    
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user_type` (`post_id`, `user_id`, `interaction_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_post_author_id` (`post_author_id`),
    KEY `idx_interaction_type` (`interaction_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态互动记录表（去连表化设计）';

-- 用户社交统计表
CREATE TABLE IF NOT EXISTS `t_user_social_stats` (
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `post_count` bigint DEFAULT '0' COMMENT '发布动态数',
    `post_like_total` bigint DEFAULT '0' COMMENT '动态获赞总数',
    `post_view_total` bigint DEFAULT '0' COMMENT '动态浏览总数',
    `like_given_count` bigint DEFAULT '0' COMMENT '点赞他人次数',
    `following_count` int DEFAULT '0' COMMENT '关注数',
    `follower_count` int DEFAULT '0' COMMENT '粉丝数',
    `last_post_time` datetime DEFAULT NULL COMMENT '最后发布时间',
    `last_active_time` datetime DEFAULT NULL COMMENT '最后活跃时间',
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`user_id`),
    KEY `idx_post_count` (`post_count`),
    KEY `idx_follower_count` (`follower_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户社交统计表（去连表化设计）';

-- ==========================================
-- 插入初始数据
-- ==========================================

-- 插入测试用户
INSERT IGNORE INTO `t_user` (`id`, `username`, `email`, `nickname`, `avatar`, `status`) VALUES
(1, 'admin', 'admin@collide.com', '管理员', 'https://example.com/avatar/admin.jpg', 1),
(2, 'test_user', 'test@collide.com', '测试用户', 'https://example.com/avatar/test.jpg', 1),
(3, 'content_creator', 'creator@collide.com', '内容创作者', 'https://example.com/avatar/creator.jpg', 1);

-- 插入关注统计初始数据
INSERT IGNORE INTO `t_follow_statistics` (`user_id`, `following_count`, `follower_count`) VALUES
(1, 0, 0),
(2, 0, 0),
(3, 0, 0);

-- 插入测试内容数据（包含统计字段）
INSERT IGNORE INTO `t_content` (`id`, `title`, `description`, `content_type`, `content_data`, `author_id`, `status`, `review_status`, `view_count`, `like_count`, `dislike_count`, `comment_count`, `published_time`) VALUES
(1, '测试小说章节', '这是一个测试小说章节的描述', 'NOVEL', '{"chapters": [{"title": "第一章", "content": "这是小说的第一章内容..."}]}', 3, 'PUBLISHED', 'APPROVED', 150, 3, 0, 3, NOW()),
(2, '测试短视频', '一个有趣的短视频内容', 'SHORT_VIDEO', '{"videoUrl": "https://example.com/video1.mp4", "duration": 30}', 2, 'PUBLISHED', 'APPROVED', 320, 1, 1, 2, NOW());

-- 插入测试评论数据（包含统计字段）
INSERT IGNORE INTO `t_comment` (`id`, `comment_type`, `target_id`, `parent_comment_id`, `root_comment_id`, `content`, `user_id`, `reply_to_user_id`, `status`, `like_count`, `reply_count`) VALUES
(1, 'CONTENT', 1, 0, 0, '这是一个很棒的小说！情节引人入胜，期待后续章节。', 1, NULL, 'NORMAL', 2, 2),
(2, 'CONTENT', 1, 1, 1, '我也觉得非常不错，作者的文笔很棒！', 2, 1, 'NORMAL', 0, 0),
(3, 'CONTENT', 1, 1, 1, '作者很用心，支持！', 3, 1, 'NORMAL', 1, 0),
(4, 'CONTENT', 2, 0, 0, '这个短视频太搞笑了，忍不住多看了几遍！', 1, NULL, 'NORMAL', 1, 1),
(5, 'CONTENT', 2, 4, 4, '哈哈哈，确实很有趣！', 3, 4, 'NORMAL', 0, 0);

-- 插入统一点赞数据
INSERT IGNORE INTO `t_like` (`id`, `user_id`, `target_id`, `target_type`, `action_type`) VALUES
-- 内容点赞
(1, 1, 1, 'CONTENT', 1),
(2, 2, 1, 'CONTENT', 1),
(3, 3, 1, 'CONTENT', 1),
(4, 1, 2, 'CONTENT', 1),
(5, 2, 2, 'CONTENT', -1),
-- 评论点赞
(6, 1, 1, 'COMMENT', 1),
(7, 2, 1, 'COMMENT', 1),
(8, 3, 3, 'COMMENT', 1),
(9, 2, 4, 'COMMENT', 1);

SET FOREIGN_KEY_CHECKS = 1; 