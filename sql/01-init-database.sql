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

-- 内容表
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
  `comment_count` bigint DEFAULT '0' COMMENT '评论数',
  `share_count` bigint DEFAULT '0' COMMENT '分享数',
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
  KEY `idx_published_time` (`published_time`)
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

SET FOREIGN_KEY_CHECKS = 1; 