/*
 * Collide 项目业务数据库表结构
 * 包含用户、内容、社交、关注、OAuth统计等核心业务表
 * 
 * @author GIG Team
 * @version 1.0.0
 */

-- 创建业务数据库
CREATE DATABASE IF NOT EXISTS `collide` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `collide`;

-- ====================================
-- 用户相关表
-- ====================================

-- 用户基础信息表
CREATE TABLE `t_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
  `salt` VARCHAR(32) NOT NULL COMMENT '密码盐值',
  `role` ENUM('user', 'vip', 'blogger', 'admin') NOT NULL DEFAULT 'user' COMMENT '用户角色',
  `status` ENUM('active', 'inactive', 'banned') NOT NULL DEFAULT 'active' COMMENT '用户状态',
  `last_login_time` TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_role` (`role`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础信息表';

-- 用户扩展信息表
CREATE TABLE `t_user_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `bio` TEXT COMMENT '个人简介',
  `birthday` DATE DEFAULT NULL COMMENT '生日',
  `gender` ENUM('male', 'female', 'unknown') DEFAULT 'unknown' COMMENT '性别',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '所在地',
  `follower_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '粉丝数',
  `following_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '关注数',
  `content_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '内容数',
  `like_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '获得点赞数',
  `vip_expire_time` TIMESTAMP NULL DEFAULT NULL COMMENT 'VIP过期时间',
  `blogger_status` ENUM('none', 'applying', 'approved', 'rejected') DEFAULT 'none' COMMENT '博主认证状态',
  `blogger_apply_time` TIMESTAMP NULL DEFAULT NULL COMMENT '博主申请时间',
  `blogger_approve_time` TIMESTAMP NULL DEFAULT NULL COMMENT '博主认证时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_follower_count` (`follower_count`),
  KEY `idx_vip_expire` (`vip_expire_time`),
  KEY `idx_blogger_status` (`blogger_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- ====================================
-- OAuth 多渠道统计表
-- ====================================

-- OAuth应用配置表
CREATE TABLE `t_oauth_application` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '应用ID',
  `app_key` VARCHAR(100) NOT NULL COMMENT '应用Key',
  `app_name` VARCHAR(200) NOT NULL COMMENT '应用名称',
  `app_type` ENUM('android', 'ios', 'web', 'mini_program') NOT NULL COMMENT '应用类型',
  `package_name` VARCHAR(200) DEFAULT NULL COMMENT '包名',
  `description` TEXT COMMENT '应用描述',
  `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_key` (`app_key`),
  KEY `idx_app_type` (`app_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth应用配置表';

-- 用户应用来源记录表
CREATE TABLE `t_user_app_source` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `app_id` BIGINT(20) NOT NULL COMMENT '应用ID',
  `first_login_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次登录时间',
  `last_login_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
  `login_count` BIGINT(20) NOT NULL DEFAULT 1 COMMENT '登录次数',
  `device_info` JSON DEFAULT NULL COMMENT '设备信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_app` (`user_id`, `app_id`),
  KEY `idx_app_id` (`app_id`),
  KEY `idx_first_login` (`first_login_time`),
  KEY `idx_last_login` (`last_login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户应用来源记录表';

-- 应用用户统计表（按日统计）
CREATE TABLE `t_app_user_statistics` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `app_id` BIGINT(20) NOT NULL COMMENT '应用ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `new_user_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '新增用户数',
  `active_user_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '活跃用户数',
  `total_user_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '累计用户数',
  `retention_rate_1d` DECIMAL(5,4) DEFAULT NULL COMMENT '1日留存率',
  `retention_rate_7d` DECIMAL(5,4) DEFAULT NULL COMMENT '7日留存率',
  `retention_rate_30d` DECIMAL(5,4) DEFAULT NULL COMMENT '30日留存率',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_app_date` (`app_id`, `stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应用用户统计表';

-- ====================================
-- 内容相关表
-- ====================================

-- 内容表（统一存储视频、图片、文本）
CREATE TABLE `t_content` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '创作者ID',
  `title` VARCHAR(500) NOT NULL COMMENT '内容标题',
  `description` TEXT COMMENT '内容描述',
  `content_type` ENUM('short_video', 'long_video', 'image', 'text', 'comic', 'novel') NOT NULL COMMENT '内容类型',
  `content_url` VARCHAR(1000) DEFAULT NULL COMMENT '内容URL（视频、图片等）',
  `cover_url` VARCHAR(1000) DEFAULT NULL COMMENT '封面URL',
  `duration` INT DEFAULT NULL COMMENT '时长（秒，仅视频）',
  `file_size` BIGINT DEFAULT NULL COMMENT '文件大小（字节）',
  `tags` JSON DEFAULT NULL COMMENT '标签列表',
  `categories` JSON DEFAULT NULL COMMENT '分类列表',
  `visibility` ENUM('public', 'private', 'friends_only') NOT NULL DEFAULT 'public' COMMENT '可见性',
  `vip_level` ENUM('free', 'paid', 'vip_free', 'vip_paid') NOT NULL DEFAULT 'free' COMMENT 'VIP等级',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '价格（元）',
  `status` ENUM('draft', 'pending_review', 'published', 'rejected', 'deleted') NOT NULL DEFAULT 'draft' COMMENT '状态',
  `review_status` ENUM('pending', 'approved', 'rejected') DEFAULT 'pending' COMMENT '审核状态',
  `review_reason` TEXT COMMENT '审核原因',
  `view_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '观看次数',
  `like_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '评论数',
  `share_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '分享数',
  `collect_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '收藏数',
  `rating_score` DECIMAL(3,2) DEFAULT NULL COMMENT '评分（1-5分）',
  `rating_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '评分人数',
  `publish_time` TIMESTAMP NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_content_type` (`content_type`),
  KEY `idx_status` (`status`),
  KEY `idx_visibility` (`visibility`),
  KEY `idx_vip_level` (`vip_level`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_like_count` (`like_count`),
  KEY `idx_rating_score` (`rating_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容表';

-- 内容交互表（点赞、收藏、评分）
CREATE TABLE `t_content_interaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `content_id` BIGINT(20) NOT NULL COMMENT '内容ID',
  `interaction_type` ENUM('like', 'collect', 'share', 'view', 'rating') NOT NULL COMMENT '交互类型',
  `rating_score` INT DEFAULT NULL COMMENT '评分（1-5，仅评分类型）',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_content_type` (`user_id`, `content_id`, `interaction_type`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_interaction_type` (`interaction_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容交互表';

-- 内容评论表
CREATE TABLE `t_content_comment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content_id` BIGINT(20) NOT NULL COMMENT '内容ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '评论者ID',
  `parent_id` BIGINT(20) DEFAULT NULL COMMENT '父评论ID',
  `root_id` BIGINT(20) DEFAULT NULL COMMENT '根评论ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `like_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '回复数',
  `status` ENUM('normal', 'deleted', 'hidden') NOT NULL DEFAULT 'normal' COMMENT '状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_root_id` (`root_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容评论表';

-- ====================================
-- 社交相关表
-- ====================================

-- 关注关系表
CREATE TABLE `t_user_follow` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `follower_id` BIGINT(20) NOT NULL COMMENT '关注者ID',
  `following_id` BIGINT(20) NOT NULL COMMENT '被关注者ID',
  `status` ENUM('active', 'deleted') NOT NULL DEFAULT 'active' COMMENT '状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follow_relation` (`follower_id`, `following_id`),
  KEY `idx_following_id` (`following_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注关系表';

-- 社交动态表（朋友圈）
CREATE TABLE `t_social_post` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '发布者ID',
  `content` TEXT NOT NULL COMMENT '动态内容',
  `media_urls` JSON DEFAULT NULL COMMENT '媒体文件URL列表',
  `media_type` ENUM('text', 'image', 'video', 'mixed') NOT NULL DEFAULT 'text' COMMENT '媒体类型',
  `visibility` ENUM('public', 'followers_only', 'private') NOT NULL DEFAULT 'public' COMMENT '可见性',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '地理位置',
  `like_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '评论数',
  `share_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '分享数',
  `status` ENUM('normal', 'deleted', 'hidden') NOT NULL DEFAULT 'normal' COMMENT '状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_visibility` (`visibility`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态表';

-- 社交动态交互表
CREATE TABLE `t_social_interaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `post_id` BIGINT(20) NOT NULL COMMENT '动态ID',
  `interaction_type` ENUM('like', 'comment', 'share') NOT NULL COMMENT '交互类型',
  `content` TEXT COMMENT '交互内容（评论内容）',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_post_type` (`user_id`, `post_id`, `interaction_type`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_interaction_type` (`interaction_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态交互表';

-- ====================================
-- 金币支付相关表
-- ====================================

-- 金币账户表
CREATE TABLE `t_coin_account` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `total_coins` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '总金币数',
  `frozen_coins` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '冻结金币数',
  `available_coins` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '可用金币数',
  `version` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='金币账户表';

-- 金币交易记录表
CREATE TABLE `t_coin_transaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `transaction_type` ENUM('recharge', 'consume', 'reward', 'refund') NOT NULL COMMENT '交易类型',
  `amount` BIGINT(20) NOT NULL COMMENT '交易金额（正数为收入，负数为支出）',
  `balance_before` BIGINT(20) NOT NULL COMMENT '交易前余额',
  `balance_after` BIGINT(20) NOT NULL COMMENT '交易后余额',
  `business_type` VARCHAR(50) NOT NULL COMMENT '业务类型',
  `business_id` VARCHAR(100) COMMENT '业务ID',
  `description` VARCHAR(500) COMMENT '交易描述',
  `status` ENUM('pending', 'success', 'failed', 'cancelled') NOT NULL DEFAULT 'pending' COMMENT '交易状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_transaction_type` (`transaction_type`),
  KEY `idx_business_type` (`business_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='金币交易记录表';

-- ====================================
-- 系统配置表
-- ====================================

-- 系统配置表
CREATE TABLE `t_system_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `config_type` ENUM('string', 'number', 'boolean', 'json') NOT NULL DEFAULT 'string' COMMENT '配置类型',
  `description` VARCHAR(500) COMMENT '配置描述',
  `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- ====================================
-- 插入初始数据
-- ====================================

-- 插入默认OAuth应用
INSERT INTO `t_oauth_application` (`app_key`, `app_name`, `app_type`, `package_name`, `description`) VALUES
('collide_android', 'Collide Android', 'android', 'com.gig.collide', 'Collide安卓应用'),
('collide_ios', 'Collide iOS', 'ios', 'com.gig.collide', 'Collide iOS应用'),
('collide_web', 'Collide Web', 'web', NULL, 'Collide网页版');

-- 插入系统配置
INSERT INTO `t_system_config` (`config_key`, `config_value`, `config_type`, `description`) VALUES
('site_name', 'Collide', 'string', '站点名称'),
('site_description', '基于短视频和长视频的社交平台', 'string', '站点描述'),
('default_avatar', 'https://example.com/default-avatar.png', 'string', '默认头像'),
('vip_price_monthly', '19.90', 'number', 'VIP月费价格'),
('vip_price_yearly', '199.00', 'number', 'VIP年费价格'),
('coin_recharge_rate', '10', 'number', '金币充值比例（1元=10金币）'),
('content_review_enabled', 'true', 'boolean', '是否启用内容审核'),
('register_enabled', 'true', 'boolean', '是否允许用户注册'); 