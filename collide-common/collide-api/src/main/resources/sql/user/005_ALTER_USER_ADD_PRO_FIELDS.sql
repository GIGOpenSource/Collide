-- 为用户表添加付费相关字段
ALTER TABLE `users` 
ADD COLUMN `user_type` varchar(32) DEFAULT 'CUSTOMER' COMMENT '用户类型（CUSTOMER普通用户，PRO付费用户）' AFTER `user_role`,
ADD COLUMN `user_permission` varchar(32) DEFAULT 'BASIC' COMMENT '用户权限（BASIC基本，AUTH认证，PRO付费，FROZEN冻结，NONE无权限）' AFTER `user_type`;

-- 为用户类型和权限添加索引
ALTER TABLE `users` 
ADD INDEX `idx_user_type` (`user_type`),
ADD INDEX `idx_user_permission` (`user_permission`); 