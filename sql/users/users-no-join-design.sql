-- =====================================================
-- 用户模块去连表设计 SQL
-- 参考 Code/ 项目的设计思想，将用户信息合并为单表
-- 作者: GIG Team
-- 日期: 2024-01-01
-- =====================================================

-- 1. 创建统一的用户信息表（去连表设计）
DROP TABLE IF EXISTS `t_user_unified`;
CREATE TABLE `t_user_unified` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `nickname` VARCHAR(100) NOT NULL COMMENT '昵称',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `salt` VARCHAR(50) NOT NULL COMMENT '密码盐值',
    `role` ENUM('user', 'blogger', 'admin', 'vip') NOT NULL DEFAULT 'user' COMMENT '用户角色',
    `status` ENUM('inactive', 'active', 'suspended', 'banned') NOT NULL DEFAULT 'inactive' COMMENT '用户状态',
    
    -- 扩展信息字段（原UserProfile表字段）
    `bio` TEXT COMMENT '个人简介',
    `birthday` DATE COMMENT '生日',
    `gender` ENUM('male', 'female', 'unknown') DEFAULT 'unknown' COMMENT '性别',
    `location` VARCHAR(100) COMMENT '所在地',
    
    -- 统计字段（冗余设计，避免连表统计）
    `follower_count` BIGINT NOT NULL DEFAULT 0 COMMENT '粉丝数',
    `following_count` BIGINT NOT NULL DEFAULT 0 COMMENT '关注数',
    `content_count` BIGINT NOT NULL DEFAULT 0 COMMENT '内容数',
    `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '获得点赞数',
    
    -- VIP相关字段
    `vip_expire_time` DATETIME COMMENT 'VIP过期时间',
    
    -- 博主认证字段
    `blogger_status` ENUM('none', 'applying', 'approved', 'rejected') DEFAULT 'none' COMMENT '博主认证状态',
    `blogger_apply_time` DATETIME COMMENT '博主申请时间',
    `blogger_approve_time` DATETIME COMMENT '博主认证时间',
    
    -- 登录相关
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `login_count` BIGINT NOT NULL DEFAULT 0 COMMENT '登录次数',
    
    -- 邀请相关
    `invite_code` VARCHAR(20) UNIQUE COMMENT '邀请码',
    `inviter_id` BIGINT COMMENT '邀请人ID',
    `invited_count` BIGINT NOT NULL DEFAULT 0 COMMENT '邀请人数',
    
    -- 系统字段
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记',
    `version` INT NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁和幂等性控制）',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_status` (`status`),
    KEY `idx_role` (`role`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_last_login_time` (`last_login_time`),
    KEY `idx_blogger_status` (`blogger_status`),
    KEY `idx_inviter_id` (`inviter_id`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户统一信息表（去连表设计）';

-- 2. 数据迁移脚本（从分离的User和UserProfile表迁移到统一表）
INSERT INTO `t_user_unified` (
    `id`, `username`, `nickname`, `avatar`, `email`, `phone`, 
    `password_hash`, `salt`, `role`, `status`, `last_login_time`,
    `bio`, `birthday`, `gender`, `location`, 
    `follower_count`, `following_count`, `content_count`, `like_count`,
    `vip_expire_time`, `blogger_status`, `blogger_apply_time`, `blogger_approve_time`,
    `create_time`, `update_time`, `deleted`, `version`
)
SELECT 
    u.id, u.username, u.nickname, u.avatar, u.email, u.phone,
    u.password_hash, u.salt, u.role, u.status, u.last_login_time,
    COALESCE(p.bio, '') as bio,
    p.birthday,
    COALESCE(p.gender, 'unknown') as gender,
    p.location,
    COALESCE(p.follower_count, 0) as follower_count,
    COALESCE(p.following_count, 0) as following_count,
    COALESCE(p.content_count, 0) as content_count,
    COALESCE(p.like_count, 0) as like_count,
    p.vip_expire_time,
    COALESCE(p.blogger_status, 'none') as blogger_status,
    p.blogger_apply_time,
    p.blogger_approve_time,
    u.create_time,
    GREATEST(u.update_time, COALESCE(p.update_time, u.update_time)) as update_time,
    u.deleted,
    0 as version
FROM t_user u
LEFT JOIN t_user_profile p ON u.id = p.user_id
WHERE u.deleted = 0;

-- 3. 创建用户操作流水表（记录用户行为）
DROP TABLE IF EXISTS `t_user_operate_log`;
CREATE TABLE `t_user_operate_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `operate_type` ENUM('register', 'login', 'logout', 'update_profile', 'apply_blogger', 'approve_blogger', 'reject_blogger', 'upgrade_vip', 'invite_user') NOT NULL COMMENT '操作类型',
    `operate_desc` VARCHAR(500) COMMENT '操作描述',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(1000) COMMENT '用户代理',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_operate_type` (`operate_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作日志表';

-- 4. 创建用户邀请关系表（处理邀请层级关系）
DROP TABLE IF EXISTS `t_user_invite_relation`;
CREATE TABLE `t_user_invite_relation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '被邀请用户ID',
    `inviter_id` BIGINT NOT NULL COMMENT '邀请人ID',
    `invite_code` VARCHAR(20) NOT NULL COMMENT '使用的邀请码',
    `invite_level` INT NOT NULL DEFAULT 1 COMMENT '邀请层级',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '邀请时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_inviter_id` (`inviter_id`),
    KEY `idx_invite_code` (`invite_code`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户邀请关系表';

-- 5. 用户统计信息更新存储过程
DELIMITER $$
CREATE PROCEDURE UpdateUserStatistics(
    IN p_user_id BIGINT,
    IN p_field_name VARCHAR(50),
    IN p_increment_value INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    CASE p_field_name
        WHEN 'follower_count' THEN
            UPDATE t_user_unified SET follower_count = follower_count + p_increment_value WHERE id = p_user_id;
        WHEN 'following_count' THEN
            UPDATE t_user_unified SET following_count = following_count + p_increment_value WHERE id = p_user_id;
        WHEN 'content_count' THEN
            UPDATE t_user_unified SET content_count = content_count + p_increment_value WHERE id = p_user_id;
        WHEN 'like_count' THEN
            UPDATE t_user_unified SET like_count = like_count + p_increment_value WHERE id = p_user_id;
        WHEN 'login_count' THEN
            UPDATE t_user_unified SET login_count = login_count + p_increment_value WHERE id = p_user_id;
        WHEN 'invited_count' THEN
            UPDATE t_user_unified SET invited_count = invited_count + p_increment_value WHERE id = p_user_id;
    END CASE;
    
    COMMIT;
END$$
DELIMITER ;

-- 6. 创建索引优化查询性能
-- 复合索引用于常见查询场景
CREATE INDEX idx_status_role_create_time ON t_user_unified(status, role, create_time);
CREATE INDEX idx_blogger_status_apply_time ON t_user_unified(blogger_status, blogger_apply_time);
CREATE INDEX idx_username_nickname_search ON t_user_unified(username, nickname);

-- 7. 插入测试数据
INSERT INTO t_user_unified (username, nickname, email, phone, password_hash, salt, role, status, bio, gender, location, invite_code)
VALUES 
('admin', '系统管理员', 'admin@collide.com', '13800000000', MD5('admin123'), 'salt_admin', 'admin', 'active', '系统管理员账号', 'unknown', '北京', 'ADMIN001'),
('blogger001', '知名博主', 'blogger@collide.com', '13800000001', MD5('blogger123'), 'salt_blogger', 'blogger', 'active', '我是一名知名博主', 'male', '上海', 'BLOG001'),
('user001', '普通用户', 'user@collide.com', '13800000002', MD5('user123'), 'salt_user', 'user', 'active', '这是我的个人简介', 'female', '深圳', 'USER001');

-- 8. 创建视图兼容旧的查询（过渡期使用）
CREATE VIEW v_user_info AS
SELECT 
    id, username, nickname, avatar, email, phone, password_hash, salt, role, status, last_login_time,
    create_time, update_time, deleted
FROM t_user_unified;

CREATE VIEW v_user_profile AS
SELECT 
    id, id as user_id, bio, birthday, gender, location,
    follower_count, following_count, content_count, like_count,
    vip_expire_time, blogger_status, blogger_apply_time, blogger_approve_time,
    create_time, update_time, deleted
FROM t_user_unified; 