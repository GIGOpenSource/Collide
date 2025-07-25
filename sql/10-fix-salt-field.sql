-- ==========================================
-- 修复 salt 字段长度问题
-- ==========================================

USE `collide`;

-- 1. 检查当前 salt 字段长度
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'collide' 
  AND TABLE_NAME = 't_user' 
  AND COLUMN_NAME = 'salt';

-- 2. 修改 salt 字段长度为 64（足够存储 UUID + 其他盐值）
ALTER TABLE `t_user` MODIFY COLUMN `salt` varchar(64) DEFAULT NULL COMMENT '密码盐值';

-- 3. 验证修改结果
DESCRIBE `t_user`;

-- 4. 检查是否有现有数据需要处理
SELECT COUNT(*) as total_users, 
       COUNT(salt) as users_with_salt,
       MAX(LENGTH(salt)) as max_salt_length
FROM `t_user` 
WHERE deleted = 0;

-- 5. 如果现有用户的 salt 字段为空，可以设置默认值（可选）
-- UPDATE `t_user` SET salt = UUID() WHERE salt IS NULL AND deleted = 0; 