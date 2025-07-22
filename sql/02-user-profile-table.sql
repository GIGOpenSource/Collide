-- ==========================================
-- 用户扩展信息表 (t_user_profile) 创建脚本
-- ==========================================

SET NAMES utf8mb4;

-- 创建用户扩展信息表（无外键约束）
CREATE TABLE IF NOT EXISTS `t_user_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `bio` varchar(500) DEFAULT NULL COMMENT '个人简介',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `gender` varchar(20) DEFAULT 'unknown' COMMENT '性别：male-男，female-女，unknown-未知',
  `location` varchar(100) DEFAULT NULL COMMENT '所在地',
  `follower_count` bigint DEFAULT '0' COMMENT '粉丝数',
  `following_count` bigint DEFAULT '0' COMMENT '关注数',
  `content_count` bigint DEFAULT '0' COMMENT '内容数',
  `like_count` bigint DEFAULT '0' COMMENT '获得点赞数',
  `vip_expire_time` datetime DEFAULT NULL COMMENT 'VIP过期时间',
  `blogger_status` varchar(20) DEFAULT 'none' COMMENT '博主认证状态：none-无，applying-申请中，approved-已通过，rejected-已拒绝',
  `blogger_apply_time` datetime DEFAULT NULL COMMENT '博主申请时间',
  `blogger_approve_time` datetime DEFAULT NULL COMMENT '博主认证时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_blogger_status` (`blogger_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户扩展信息表';

-- 为现有用户创建默认扩展信息
INSERT IGNORE INTO `t_user_profile` (`user_id`, `follower_count`, `following_count`, `content_count`, `like_count`, `blogger_status`) 
SELECT `id`, 0, 0, 0, 0, 'none' FROM `t_user` WHERE `deleted` = 0;

-- 执行完成提示
SELECT 'User Profile 表创建完成！' AS message; 