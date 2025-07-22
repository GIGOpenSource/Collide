-- ==========================================
-- 认证服务相关表（无外键约束）
-- ==========================================

-- 设置字符集
SET NAMES utf8mb4;

-- 更新用户表，添加认证相关字段（忽略重复字段错误）
SET sql_mode = '';

-- 添加密码哈希字段
ALTER TABLE `t_user` ADD COLUMN `password_hash` varchar(255) DEFAULT NULL COMMENT '密码哈希' AFTER `nickname`;

-- 添加密码盐值字段  
ALTER TABLE `t_user` ADD COLUMN `salt` varchar(100) DEFAULT NULL COMMENT '密码盐值' AFTER `password_hash`;

-- 添加用户角色字段
ALTER TABLE `t_user` ADD COLUMN `role` varchar(20) DEFAULT 'user' COMMENT '用户角色：user-普通用户，vip-VIP，blogger-博主，admin-管理员' AFTER `salt`;

-- 添加最后登录时间字段
ALTER TABLE `t_user` ADD COLUMN `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间' AFTER `status`;

-- 添加创建时间字段
ALTER TABLE `t_user` ADD COLUMN `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

-- 添加更新时间字段
ALTER TABLE `t_user` ADD COLUMN `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间';

-- 恢复sql_mode
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- OAuth应用配置表（无外键）
CREATE TABLE IF NOT EXISTS `t_oauth_application` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '应用ID',
    `app_key` varchar(50) NOT NULL COMMENT '应用Key',
    `app_name` varchar(100) NOT NULL COMMENT '应用名称',
    `app_type` varchar(20) NOT NULL COMMENT '应用类型：android, ios, web, mini_program',
    `package_name` varchar(200) DEFAULT NULL COMMENT '包名',
    `description` varchar(500) DEFAULT NULL COMMENT '应用描述',
    `status` varchar(20) DEFAULT 'active' COMMENT '状态：active-启用，inactive-停用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_key` (`app_key`),
    KEY `idx_app_name` (`app_name`),
    KEY `idx_app_type` (`app_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth应用配置表';

-- 用户应用来源表（无外键）
CREATE TABLE IF NOT EXISTS `t_user_app_source` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` bigint NOT NULL COMMENT '用户ID（关联t_user.id，无外键约束）',
    `app_id` bigint NOT NULL COMMENT '应用ID（关联t_oauth_application.id，无外键约束）',
    `first_login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '首次登录时间',
    `last_login_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
    `login_count` bigint DEFAULT 1 COMMENT '登录次数',
    `device_info` text DEFAULT NULL COMMENT '设备信息',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_app` (`user_id`, `app_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_app_id` (`app_id`),
    KEY `idx_first_login_time` (`first_login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户应用来源表';

-- ==========================================
-- 插入初始OAuth应用数据
-- ==========================================
INSERT IGNORE INTO `t_oauth_application` (`app_key`, `app_name`, `app_type`, `description`, `status`) VALUES
('android', 'Collide Android App', 'android', 'Collide官方Android应用', 'active'),
('ios', 'Collide iOS App', 'ios', 'Collide官方iOS应用', 'active'),
('web', 'Collide Web', 'web', 'Collide官方网页应用', 'active'),
('mini_program', 'Collide Mini Program', 'mini_program', 'Collide官方小程序', 'active'); 