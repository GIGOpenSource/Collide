-- ==========================================
-- 修复 t_user 表 status 字段类型
-- ==========================================

-- 设置字符集
SET NAMES utf8mb4;

-- 修改 status 字段类型为 varchar
ALTER TABLE `t_user` MODIFY COLUMN `status` varchar(20) DEFAULT 'active' COMMENT '状态：active-正常，inactive-未激活，banned-已封禁';

-- 更新现有数据（如果有的话）
UPDATE `t_user` SET `status` = 
    CASE 
        WHEN `status` = '1' OR `status` = 1 THEN 'active'
        WHEN `status` = '0' OR `status` = 0 THEN 'banned'
        ELSE 'active'
    END 
WHERE `status` IN ('0', '1', 0, 1);

-- 修改 role 字段类型（确保兼容性）
ALTER TABLE `t_user` MODIFY COLUMN `role` varchar(20) DEFAULT 'user' COMMENT '用户角色：user-普通用户，vip-VIP，blogger-博主，admin-管理员'; 