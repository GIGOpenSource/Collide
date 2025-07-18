CREATE TABLE `users`
(
    `id`                   bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID（自增主键）',
    `gmt_create`           datetime NOT NULL COMMENT '创建时间',
    `gmt_modified`         datetime NOT NULL COMMENT '最后更新时间',
    `user_name`            varchar(255)                       DEFAULT NULL COMMENT '用户名',
    `nick_name`            varchar(255)                       DEFAULT NULL COMMENT '用户昵称',
    `password_hash`        varchar(255)                       DEFAULT NULL COMMENT '密码哈希',
    `state`                varchar(64)                        DEFAULT NULL COMMENT '用户状态（ACTIVE，FROZEN）',
    `invite_code`          varchar(255)                       DEFAULT NULL COMMENT '邀请码',
    `telephone`            varchar(20)                        DEFAULT NULL COMMENT '手机号码',
    `inviter_id`           varchar(255)                       DEFAULT NULL COMMENT '邀请人用户ID',
    `last_login_time`      datetime                           DEFAULT NULL COMMENT '最后登录时间',
    `profile_photo_url`    varchar(255)                       DEFAULT NULL COMMENT '用户头像URL',
    `block_chain_url`      varchar(255)                       DEFAULT NULL COMMENT '区块链地址',
    `block_chain_platform` varchar(255)                       DEFAULT NULL COMMENT '区块链平台',
    `is_blog_up`           tinyint(1) DEFAULT NULL COMMENT '实名认证状态（TRUE或FALSE）',
    `certification`        tinyint(1) DEFAULT NULL COMMENT '实名认证状态（TRUE或FALSE）',
    `real_name`            varchar(255)                       DEFAULT NULL COMMENT '真实姓名',
    `id_card_no`           varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '身份证no',
    `user_role`            varchar(128)                       DEFAULT NULL COMMENT '用户角色',
    `deleted`              int                                DEFAULT NULL COMMENT '是否逻辑删除，0为未删除，非0为已删除',
    `lock_version`         int                                DEFAULT NULL COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4  COMMENT='用户信息表'
;