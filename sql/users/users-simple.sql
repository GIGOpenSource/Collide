-- ==========================================
-- 用户模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

-- 用户统一信息表（去连表设计）
# DROP TABLE IF EXISTS `t_user`;
# CREATE TABLE `t_user` (
#     `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
#     `username`        VARCHAR(50)  NOT NULL                COMMENT '用户名',
#     `nickname`        VARCHAR(100) NOT NULL                COMMENT '昵称',
#     `avatar`          VARCHAR(500)                         COMMENT '头像URL',
#     `email`           VARCHAR(100)                         COMMENT '邮箱',
#     `phone`           VARCHAR(20)                          COMMENT '手机号',
#     `password_hash`   VARCHAR(255) NOT NULL                COMMENT '密码哈希',
#     `role`            VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '用户角色：user、blogger、admin、vip',
#     `status`          VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '用户状态：active、inactive、suspended、banned',
#
#     -- 扩展信息字段
#     `bio`             TEXT                                 COMMENT '个人简介',
#     `birthday`        DATE                                 COMMENT '生日',
#     `gender`          VARCHAR(10)  DEFAULT 'unknown'       COMMENT '性别：male、female、unknown',
#     `location`        VARCHAR(100)                         COMMENT '所在地',
#
#     -- 统计字段（冗余设计，避免连表）
#     `follower_count`  BIGINT       NOT NULL DEFAULT 0     COMMENT '粉丝数',
#     `following_count` BIGINT       NOT NULL DEFAULT 0     COMMENT '关注数',
#     `content_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '内容数',
#     `like_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '获得点赞数',
#
#     -- VIP相关字段
#     `vip_expire_time` DATETIME                             COMMENT 'VIP过期时间',
#
#     -- 登录相关
#     `last_login_time` DATETIME                             COMMENT '最后登录时间',
#     `login_count`     BIGINT       NOT NULL DEFAULT 0     COMMENT '登录次数',
#
#     -- 邀请相关
#     `invite_code`     VARCHAR(20)                          COMMENT '邀请码',
#     `inviter_id`      BIGINT                               COMMENT '邀请人ID',
#     `invited_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '邀请人数',
#
#     `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#
#     PRIMARY KEY (`id`),
#     UNIQUE KEY `uk_username` (`username`),
#     UNIQUE KEY `uk_email` (`email`),
#     UNIQUE KEY `uk_phone` (`phone`),
#     UNIQUE KEY `uk_invite_code` (`invite_code`),
#     KEY `idx_role` (`role`),
#     KEY `idx_status` (`status`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户统一信息表';

CREATE TABLE `t_user`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `email`         VARCHAR(100) COMMENT '邮箱',
    `phone`         VARCHAR(20) COMMENT '手机号',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-active, 2-inactive, 3-suspended, 4-banned',
    `invite_code`   VARCHAR(20) COMMENT '邀请码',
    `inviter_id`    BIGINT COMMENT '邀请人ID',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`) USING HASH,
    UNIQUE KEY `uk_phone` (`phone`) USING HASH,
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_inviter_id` (`inviter_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户核心表';

CREATE TABLE `t_user_profile`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '资料ID',
    `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
    `nickname`    VARCHAR(100) NOT NULL COMMENT '昵称',
    `avatar`      VARCHAR(500) COMMENT '头像URL',
    `bio`         VARCHAR(500) COMMENT '个人简介',
    `birthday`    DATE COMMENT '生日',
    `gender`      TINYINT DEFAULT 0 COMMENT '性别：0-unknown, 1-male, 2-female',
    `location`    VARCHAR(100) COMMENT '所在地',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_nickname` (`nickname`),
    KEY `idx_gender` (`gender`),
    KEY `idx_location` (`location`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户资料表';

CREATE TABLE `t_user_stats`
(
    `id`              BIGINT    NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `user_id`         BIGINT    NOT NULL COMMENT '用户ID',
    `follower_count`  INT       NOT NULL DEFAULT 0 COMMENT '粉丝数',
    `following_count` INT       NOT NULL DEFAULT 0 COMMENT '关注数',
    `content_count`   INT       NOT NULL DEFAULT 0 COMMENT '内容数',
    `like_count`      INT       NOT NULL DEFAULT 0 COMMENT '获得点赞数',
    `login_count`     INT       NOT NULL DEFAULT 0 COMMENT '登录次数',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `activity_score`  DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '活跃度分数',
    `influence_score` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '影响力分数',
    `create_time`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_follower_count` (`follower_count` DESC),
    KEY `idx_content_count` (`content_count` DESC),
    KEY `idx_like_count` (`like_count` DESC),
    KEY `idx_activity_score` (`activity_score` DESC),
    KEY `idx_last_login_time` (`last_login_time` DESC)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户统计表';

CREATE TABLE `t_user_role`
(
    `id`          INT                                   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT                                NOT NULL,
    `role`        ENUM ('user','blogger','admin','vip') NOT NULL DEFAULT 'user',
    `expire_time` DATETIME COMMENT '角色过期时间',
    `status`      VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '角色状态：active, revoked, expired',
    `assign_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    `assign_by`   BIGINT COMMENT '分配人ID',
    `revoke_time` DATETIME COMMENT '撤销时间',
    `revoke_by`   BIGINT COMMENT '撤销人ID',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户角色表';

-- 用户钱包表（扩展版：支持任务金币系统）
DROP TABLE IF EXISTS `t_user_wallet`;
CREATE TABLE `t_user_wallet`
(
    `id`                BIGINT         NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    `user_id`           BIGINT         NOT NULL COMMENT '用户ID',

    -- 现金资产字段
    `balance`           DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '现金余额',
    `frozen_amount`     DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',

    -- 虚拟货币字段（任务系统）
    `coin_balance`      BIGINT         NOT NULL DEFAULT 0 COMMENT '金币余额（任务奖励虚拟货币）',
    `coin_total_earned` BIGINT         NOT NULL DEFAULT 0 COMMENT '累计获得金币',
    `coin_total_spent`  BIGINT         NOT NULL DEFAULT 0 COMMENT '累计消费金币',

    -- 统计字段
    `total_income`      DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总收入',
    `total_expense`     DECIMAL(15, 2) NOT NULL DEFAULT 0.00 COMMENT '总支出',

    `status`            VARCHAR(20)    NOT NULL DEFAULT 'active' COMMENT '状态：active、frozen',
    `create_time`       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_coin_balance` (`coin_balance`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户钱包表（支持现金+金币）';

-- 用户拉黑表（无连表设计）
DROP TABLE IF EXISTS `t_user_block`;
CREATE TABLE `t_user_block`
(
    `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '拉黑记录ID',
    `user_id`          BIGINT      NOT NULL COMMENT '拉黑者用户ID',
    `blocked_user_id`  BIGINT      NOT NULL COMMENT '被拉黑用户ID',

    -- 冗余用户信息，避免连表查询
    `user_username`    VARCHAR(50) NOT NULL COMMENT '拉黑者用户名',
    `blocked_username` VARCHAR(50) NOT NULL COMMENT '被拉黑用户名',

    `status`           VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '拉黑状态：active、cancelled',
    `reason`           VARCHAR(200) COMMENT '拉黑原因',

    `create_time`      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '拉黑时间',
    `update_time`      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_blocked` (`user_id`, `blocked_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_blocked_user_id` (`blocked_user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户拉黑关系表';

-- ==========================================
-- 初始化管理员用户
-- ==========================================

INSERT INTO `t_user` (`username`, `nickname`, `email`, `password_hash`, `role`, `status`)
VALUES ('admin', '系统管理员', 'admin@collide.com', '$2a$10$encrypted_password_hash', 'admin', 'active'),
       ('blogger', '博主示例', 'blogger@collide.com', '$2a$10$encrypted_password_hash', 'blogger', 'active');

-- 初始化管理员钱包
INSERT INTO `t_user_wallet` (`user_id`, `balance`, `coin_balance`, `coin_total_earned`)
VALUES (1, 1000.00, 500, 500),
       (2, 100.00, 100, 100);
