-- ==========================================
-- 创建 MySQL 用户和数据库
-- ==========================================

-- 创建 collide 用户
CREATE USER IF NOT EXISTS 'collide'@'%' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON collide.* TO 'collide'@'%';

-- 创建 test_user 用户
CREATE USER IF NOT EXISTS 'test_user'@'%' IDENTIFIED BY 'test123';
GRANT ALL PRIVILEGES ON collide.* TO 'test_user'@'%';

-- 创建 collide 数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS collide CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 刷新权限
FLUSH PRIVILEGES;

-- 显示用户列表
SELECT user, host, plugin FROM mysql.user WHERE user IN ('root', 'collide', 'test_user'); 