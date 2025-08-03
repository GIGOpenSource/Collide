-- ==========================================
-- Collide 项目完整数据库初始化脚本
-- 包含所有模块的简洁版表结构和基础数据
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

-- ==========================================
-- 1. 用户模块 - 核心基础表
-- ==========================================

-- 用户统一信息表（去连表设计）
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `nickname`        VARCHAR(100) NOT NULL                COMMENT '昵称',
    `avatar`          VARCHAR(500)                         COMMENT '头像URL',
    `email`           VARCHAR(100)                         COMMENT '邮箱',
    `phone`           VARCHAR(20)                          COMMENT '手机号',
    `password_hash`   VARCHAR(255) NOT NULL                COMMENT '密码哈希',
    `role`            VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '用户角色：user、blogger、admin、vip',
    `status`          VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '用户状态：active、inactive、suspended、banned',
    
    -- 扩展信息字段
    `bio`             TEXT                                 COMMENT '个人简介',
    `birthday`        DATE                                 COMMENT '生日',
    `gender`          VARCHAR(10)  DEFAULT 'unknown'       COMMENT '性别：male、female、unknown',
    `location`        VARCHAR(100)                         COMMENT '所在地',
    
    -- 统计字段（冗余设计，避免连表）
    `follower_count`  BIGINT       NOT NULL DEFAULT 0     COMMENT '粉丝数',
    `following_count` BIGINT       NOT NULL DEFAULT 0     COMMENT '关注数',
    `content_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '内容数',
    `like_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '获得点赞数',
    
    -- VIP相关字段
    `vip_expire_time` DATETIME                             COMMENT 'VIP过期时间',
    
    -- 登录相关
    `last_login_time` DATETIME                             COMMENT '最后登录时间',
    `login_count`     BIGINT       NOT NULL DEFAULT 0     COMMENT '登录次数',
    
    -- 邀请相关
    `invite_code`     VARCHAR(20)                          COMMENT '邀请码',
    `inviter_id`      BIGINT                               COMMENT '邀请人ID',
    `invited_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '邀请人数',
    
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户统一信息表';

-- 用户钱包表（扩展版：支持任务金币系统）
DROP TABLE IF EXISTS `t_user_wallet`;
CREATE TABLE `t_user_wallet` (
    `id`               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    `user_id`          BIGINT        NOT NULL                COMMENT '用户ID',
    
    -- 现金资产字段
    `balance`          DECIMAL(15,2) NOT NULL DEFAULT 0.00   COMMENT '现金余额',
    `frozen_amount`    DECIMAL(15,2) NOT NULL DEFAULT 0.00   COMMENT '冻结金额',
    
    -- 虚拟货币字段（任务系统）
    `coin_balance`     BIGINT        NOT NULL DEFAULT 0      COMMENT '金币余额（任务奖励虚拟货币）',
    `coin_total_earned` BIGINT       NOT NULL DEFAULT 0      COMMENT '累计获得金币',
    `coin_total_spent` BIGINT        NOT NULL DEFAULT 0      COMMENT '累计消费金币',
    
    -- 统计字段
    `total_income`     DECIMAL(15,2) NOT NULL DEFAULT 0.00   COMMENT '总收入',
    `total_expense`    DECIMAL(15,2) NOT NULL DEFAULT 0.00   COMMENT '总支出',
    
    `status`           VARCHAR(20)   NOT NULL DEFAULT 'active' COMMENT '状态：active、frozen',
    `create_time`      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_coin_balance` (`coin_balance`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表（支持现金+金币）';

-- 用户拉黑表（无连表设计）
DROP TABLE IF EXISTS `t_user_block`;
CREATE TABLE `t_user_block` (
    `id`               BIGINT      NOT NULL AUTO_INCREMENT COMMENT '拉黑记录ID',
    `user_id`          BIGINT      NOT NULL                COMMENT '拉黑者用户ID',
    `blocked_user_id`  BIGINT      NOT NULL                COMMENT '被拉黑用户ID',
    
    -- 冗余用户信息，避免连表查询
    `user_username`    VARCHAR(50) NOT NULL                COMMENT '拉黑者用户名',
    `blocked_username` VARCHAR(50) NOT NULL                COMMENT '被拉黑用户名',
    
    `status`           VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '拉黑状态：active、cancelled',
    `reason`           VARCHAR(200)                        COMMENT '拉黑原因',
    
    `create_time`      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '拉黑时间',
    `update_time`      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_blocked` (`user_id`, `blocked_user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_blocked_user_id` (`blocked_user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户拉黑关系表';

-- ==========================================
-- 2. 分类模块
-- ==========================================

-- 分类主表
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`         VARCHAR(100) NOT NULL                COMMENT '分类名称',
  `description`  TEXT                                 COMMENT '分类描述',
  `parent_id`    BIGINT       NOT NULL DEFAULT 0     COMMENT '父分类ID，0表示顶级分类',
  `icon_url`     VARCHAR(500)                         COMMENT '分类图标URL',
  `sort`         INT          NOT NULL DEFAULT 0     COMMENT '排序值',
  `content_count` BIGINT      NOT NULL DEFAULT 0     COMMENT '内容数量（冗余统计）',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_parent` (`name`, `parent_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类主表';

-- ==========================================
-- 3. 标签模块
-- ==========================================

-- 标签主表
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name`         VARCHAR(100) NOT NULL                 COMMENT '标签名称',
  `description`  TEXT                                  COMMENT '标签描述',
  `tag_type`     VARCHAR(20)  NOT NULL DEFAULT 'content' COMMENT '标签类型：content、interest、system',
  `category_id`  BIGINT                                COMMENT '所属分类ID',
  `usage_count`  BIGINT       NOT NULL DEFAULT 0      COMMENT '使用次数',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`, `tag_type`),
  KEY `idx_tag_type` (`tag_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签主表';

-- 用户兴趣标签关联表
DROP TABLE IF EXISTS `t_user_interest_tag`;
CREATE TABLE `t_user_interest_tag` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id`        BIGINT       NOT NULL                 COMMENT '用户ID',
  `tag_id`         BIGINT       NOT NULL                 COMMENT '标签ID',
  `interest_score` DECIMAL(5,2) NOT NULL DEFAULT 0.00   COMMENT '兴趣分数（0-100）',
  `status`         VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户兴趣标签关联表';

-- 内容标签关联表
DROP TABLE IF EXISTS `t_content_tag`;
CREATE TABLE `t_content_tag` (
  `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `content_id`  BIGINT    NOT NULL                 COMMENT '内容ID',
  `tag_id`      BIGINT    NOT NULL                 COMMENT '标签ID',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`, `tag_id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关联表';

-- ==========================================
-- 4. 内容模块
-- ==========================================

-- 内容主表（去连表化设计）
DROP TABLE IF EXISTS `t_content`;
CREATE TABLE `t_content` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `title`           VARCHAR(200) NOT NULL                COMMENT '内容标题',
    `description`     TEXT                                 COMMENT '内容描述',
    `content_type`    VARCHAR(50)  NOT NULL                COMMENT '内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO',
    `content_data`    LONGTEXT                             COMMENT '内容数据，JSON格式',
    `cover_url`       VARCHAR(500)                         COMMENT '封面图片URL',
    `tags`            TEXT                                 COMMENT '标签，JSON数组格式',
    
    -- 作者信息（冗余字段，避免连表）
    `author_id`       BIGINT       NOT NULL                COMMENT '作者用户ID',
    `author_nickname` VARCHAR(50)                          COMMENT '作者昵称（冗余）',
    `author_avatar`   VARCHAR(500)                         COMMENT '作者头像URL（冗余）',
    
    -- 分类信息（冗余字段，避免连表）
    `category_id`     BIGINT                               COMMENT '分类ID',
    `category_name`   VARCHAR(100)                         COMMENT '分类名称（冗余）',
    
    -- 状态相关字段
    `status`          VARCHAR(50)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT、PUBLISHED、OFFLINE',
    `review_status`   VARCHAR(50)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING、APPROVED、REJECTED',
    
    -- 统计字段（冗余存储，避免聚合查询）
    `view_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '查看数',
    `like_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '点赞数',
    `comment_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '评论数',
    `favorite_count`  BIGINT       NOT NULL DEFAULT 0     COMMENT '收藏数',
    `score_count`     BIGINT       NOT NULL DEFAULT 0     COMMENT '评分数',
    `score_total`     BIGINT       NOT NULL DEFAULT 0     COMMENT '总评分',
    
    -- 时间字段
    `publish_time`    DATETIME                             COMMENT '发布时间',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表';

-- 内容章节表（用于小说、漫画等多章节内容）
DROP TABLE IF EXISTS `t_content_chapter`;
CREATE TABLE `t_content_chapter` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `content_id`   BIGINT       NOT NULL                COMMENT '内容ID',
    `chapter_num`  INT          NOT NULL                COMMENT '章节号',
    `title`        VARCHAR(200) NOT NULL                COMMENT '章节标题',
    `content`      LONGTEXT                             COMMENT '章节内容',
    `word_count`   INT          NOT NULL DEFAULT 0     COMMENT '字数',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT、PUBLISHED',
    `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_chapter` (`content_id`, `chapter_num`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容章节表';

-- 用户内容购买记录表（记录用户购买的付费内容）
DROP TABLE IF EXISTS `t_user_content_purchase`;
CREATE TABLE `t_user_content_purchase` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '购买记录ID',
    `user_id`      BIGINT       NOT NULL                COMMENT '用户ID',
    `content_id`   BIGINT       NOT NULL                COMMENT '内容ID',
    
    -- 作者信息（保留author_id用于业务逻辑）
    `author_id`         BIGINT                          COMMENT '作者ID',
    
    -- 购买相关信息
    `order_id`          BIGINT                          COMMENT '关联订单ID',
    `order_no`          VARCHAR(50)                     COMMENT '订单号（冗余）',
    `coin_amount`       BIGINT       NOT NULL           COMMENT '支付金币数量',
    `original_price`    BIGINT                          COMMENT '原价金币',
    `discount_amount`   BIGINT       DEFAULT 0          COMMENT '优惠金币数量',
    
    -- 购买状态
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE、EXPIRED、REFUNDED',
    `purchase_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
    `expire_time`       DATETIME                        COMMENT '过期时间（为空表示永久有效）',
    
    -- 访问统计
    `access_count`      INT          NOT NULL DEFAULT 0 COMMENT '访问次数',
    `last_access_time`  DATETIME                        COMMENT '最后访问时间',
    
    -- 时间字段
    `create_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_content` (`user_id`, `content_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_status` (`status`),
    KEY `idx_purchase_time` (`purchase_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户内容购买记录表';

-- 内容付费配置表（记录内容的付费信息）
DROP TABLE IF EXISTS `t_content_payment`;
CREATE TABLE `t_content_payment` (
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `content_id`      BIGINT      NOT NULL                COMMENT '内容ID',
    
    -- 付费类型配置
    `payment_type`    VARCHAR(20) NOT NULL DEFAULT 'FREE' COMMENT '付费类型：FREE、COIN_PAY、VIP_FREE、TIME_LIMITED',
    `coin_price`      BIGINT      NOT NULL DEFAULT 0      COMMENT '金币价格',
    `original_price`  BIGINT                              COMMENT '原价（用于折扣显示）',
    
    -- 权限配置
    `vip_free`        TINYINT     NOT NULL DEFAULT 0      COMMENT '会员免费：0否，1是',
    `vip_only`        TINYINT     NOT NULL DEFAULT 0      COMMENT '是否只有VIP才能购买：0否，1是',
    `trial_enabled`   TINYINT     NOT NULL DEFAULT 0      COMMENT '是否支持试读：0否，1是',
    `trial_content`   TEXT                                COMMENT '试读内容',
    `trial_word_count` INT         NOT NULL DEFAULT 0     COMMENT '试读字数',
    
    -- 时效配置
    `is_permanent`    TINYINT     NOT NULL DEFAULT 1      COMMENT '是否永久有效：0否，1是',
    `valid_days`      INT                                 COMMENT '有效天数（非永久时使用）',
    
    -- 销售统计
    `total_sales`     BIGINT      NOT NULL DEFAULT 0      COMMENT '总销量',
    `total_revenue`   BIGINT      NOT NULL DEFAULT 0      COMMENT '总收入（金币）',
    
    -- 状态配置
    `status`          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE、INACTIVE',
    
    -- 时间字段
    `create_time`     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_id` (`content_id`),
    KEY `idx_payment_type` (`payment_type`),
    KEY `idx_coin_price` (`coin_price`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容付费配置表';

-- ==========================================
-- 5. 商品模块
-- ==========================================

-- 商品主表（去连表化设计，支持四种商品类型）
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`
(
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name`           VARCHAR(200)   NOT NULL COMMENT '商品名称',
    `description`    TEXT COMMENT '商品描述',
    `category_id`    BIGINT         NOT NULL COMMENT '分类ID',
    `category_name`  VARCHAR(100) COMMENT '分类名称（冗余）',
    
    -- 商品类型和定价策略
    `goods_type`     VARCHAR(20)    NOT NULL COMMENT '商品类型：coin-金币、goods-商品、subscription-订阅、content-内容',
    `price`          DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '现金价格（内容类型为0）',
    `original_price` DECIMAL(10, 2) COMMENT '原价',
    `coin_price`     BIGINT         NOT NULL DEFAULT 0 COMMENT '金币价格（内容类型专用，其他类型为0）',
    `coin_amount`    BIGINT         COMMENT '金币数量（仅金币类商品：购买后获得的金币数）',
    
    -- 特殊字段
    `content_id`     BIGINT         COMMENT '关联内容ID（仅内容类型有效）',
    `content_title`  VARCHAR(200)   COMMENT '内容标题（冗余，仅内容类型有效）',
    `subscription_duration` INT     COMMENT '订阅时长（天数，仅订阅类型有效）',
    `subscription_type` VARCHAR(50) COMMENT '订阅类型（VIP、PREMIUM等，仅订阅类型有效）',
    
    `stock`          INT            NOT NULL DEFAULT -1 COMMENT '库存数量（-1表示无限库存，适用于虚拟商品）',
    `cover_url`      VARCHAR(500) COMMENT '商品封面图',
    `images`         TEXT COMMENT '商品图片，JSON数组格式',

    -- 商家信息（冗余字段，避免连表）
    `seller_id`      BIGINT         NOT NULL COMMENT '商家ID',
    `seller_name`    VARCHAR(100)   NOT NULL COMMENT '商家名称（冗余）',

    -- 状态和统计
    `status`         VARCHAR(20)    NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive、sold_out',
    `sales_count`    BIGINT         NOT NULL DEFAULT 0 COMMENT '销量（冗余统计）',
    `view_count`     BIGINT         NOT NULL DEFAULT 0 COMMENT '查看数（冗余统计）',

    `create_time`    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    KEY `idx_goods_type` (`goods_type`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_status` (`status`),
    KEY `idx_price` (`price`),
    KEY `idx_coin_price` (`coin_price`),
    KEY `idx_content_id` (`content_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品主表（支持金币、商品、订阅、内容四种类型）';

-- ==========================================
-- 6. 订单模块
-- ==========================================

-- 订单主表（去连表化设计，支持四种商品类型和双支付模式）
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`     VARCHAR(50)  NOT NULL                COMMENT '订单号',
  `user_id`      BIGINT       NOT NULL                COMMENT '用户ID',
  `user_nickname` VARCHAR(100)                        COMMENT '用户昵称（冗余）',
  
  -- 商品信息（冗余字段，避免连表）
  `goods_id`     BIGINT       NOT NULL                COMMENT '商品ID',
  `goods_name`   VARCHAR(200)                         COMMENT '商品名称（冗余）',
  `goods_type`   VARCHAR(20)  NOT NULL                COMMENT '商品类型：coin、goods、subscription、content',
  `goods_cover`  VARCHAR(500)                         COMMENT '商品封面（冗余）',
  `goods_category_name` VARCHAR(100)                  COMMENT '商品分类名称（冗余）',
  
  -- 商品特殊信息（根据类型使用）
  `coin_amount`  BIGINT                               COMMENT '金币数量（金币类商品：购买后获得金币数）',
  `content_id`   BIGINT                               COMMENT '内容ID（内容类商品）',
  `subscription_duration` INT                         COMMENT '订阅时长天数（订阅类商品）',
  `subscription_type` VARCHAR(50)                     COMMENT '订阅类型（订阅类商品）',
  
  `quantity`     INT          NOT NULL DEFAULT 1     COMMENT '购买数量',
  
  -- 双支付模式：现金支付 vs 金币支付
  `payment_mode` VARCHAR(20)  NOT NULL                COMMENT '支付模式：cash-现金支付、coin-金币支付',
  `cash_amount`  DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '现金金额（现金支付时使用）',
  `coin_cost`    BIGINT       NOT NULL DEFAULT 0     COMMENT '消耗金币数（金币支付时使用）',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额（现金）',
  `discount_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠金额',
  `final_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额（现金）',
  
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '订单状态：pending、paid、shipped、completed、cancelled',
  `pay_status`   VARCHAR(20)  NOT NULL DEFAULT 'unpaid' COMMENT '支付状态：unpaid、paid、refunded',
  `pay_method`   VARCHAR(20)                          COMMENT '支付方式：alipay、wechat、balance、coin',
  `pay_time`     DATETIME                             COMMENT '支付时间',
  
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_goods_id` (`goods_id`),
  KEY `idx_goods_type` (`goods_type`),
  KEY `idx_payment_mode` (`payment_mode`),
  KEY `idx_status` (`status`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_content_id` (`content_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表（支持四种商品类型和双支付模式）';

-- ==========================================
-- 7. 支付模块
-- ==========================================

-- 支付记录表（去连表化设计）
DROP TABLE IF EXISTS `t_payment`;
CREATE TABLE `t_payment` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `payment_no`   VARCHAR(50)  NOT NULL                COMMENT '支付单号',
  `order_id`     BIGINT       NOT NULL                COMMENT '订单ID',
  `order_no`     VARCHAR(50)                          COMMENT '订单号（冗余）',
  `user_id`      BIGINT       NOT NULL                COMMENT '用户ID',
  `user_nickname` VARCHAR(100)                        COMMENT '用户昵称（冗余）',
  
  `amount`       DECIMAL(10,2) NOT NULL              COMMENT '支付金额',
  `pay_method`   VARCHAR(20)  NOT NULL                COMMENT '支付方式：alipay、wechat、balance',
  `pay_channel`  VARCHAR(50)                          COMMENT '支付渠道',
  `third_party_no` VARCHAR(100)                       COMMENT '第三方支付单号',
  
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '支付状态：pending、success、failed、cancelled',
  `pay_time`     DATETIME                             COMMENT '支付完成时间',
  `notify_time`  DATETIME                             COMMENT '回调通知时间',
  
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_third_party_no` (`third_party_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- ==========================================
-- 8. 关注模块
-- ==========================================

-- 关注关系表（去连表化设计）
DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` BIGINT       NOT NULL                COMMENT '关注者用户ID',
  `followee_id` BIGINT       NOT NULL                COMMENT '被关注者用户ID',
  
  -- 关注者信息（冗余字段，避免连表）
  `follower_nickname` VARCHAR(100)                   COMMENT '关注者昵称（冗余）',
  `follower_avatar`   VARCHAR(500)                   COMMENT '关注者头像（冗余）',
  
  -- 被关注者信息（冗余字段，避免连表）
  `followee_nickname` VARCHAR(100)                   COMMENT '被关注者昵称（冗余）',
  `followee_avatar`   VARCHAR(500)                   COMMENT '被关注者头像（冗余）',
  
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、cancelled',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followee` (`follower_id`, `followee_id`),
  KEY `idx_follower_id` (`follower_id`),
  KEY `idx_followee_id` (`followee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系表';

-- ==========================================
-- 9. 点赞模块
-- ==========================================

-- 点赞主表（去连表化设计）
DROP TABLE IF EXISTS `t_like`;
CREATE TABLE `t_like` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `like_type`   VARCHAR(20)  NOT NULL                COMMENT '点赞类型：CONTENT、COMMENT、DYNAMIC',
  `target_id`   BIGINT       NOT NULL                COMMENT '目标对象ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '点赞用户ID',
  
  -- 目标对象信息（冗余字段，避免连表）
  `target_title`    VARCHAR(200)                     COMMENT '目标对象标题（冗余）',
  `target_author_id` BIGINT                          COMMENT '目标对象作者ID（冗余）',
  
  -- 用户信息（冗余字段，避免连表）
  `user_nickname`   VARCHAR(100)                     COMMENT '用户昵称（冗余）',
  `user_avatar`     VARCHAR(500)                     COMMENT '用户头像（冗余）',
  
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、cancelled',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `like_type`, `target_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_like_type` (`like_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞主表';

-- ==========================================
-- 10. 收藏模块
-- ==========================================

-- 收藏主表（去连表化设计）
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `favorite_type` VARCHAR(20) NOT NULL                COMMENT '收藏类型：CONTENT、GOODS',
  `target_id`   BIGINT       NOT NULL                COMMENT '收藏目标ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '收藏用户ID',
  
  -- 收藏对象信息（冗余字段，避免连表）
  `target_title`    VARCHAR(200)                     COMMENT '收藏对象标题（冗余）',
  `target_cover`    VARCHAR(500)                     COMMENT '收藏对象封面（冗余）',
  `target_author_id` BIGINT                          COMMENT '收藏对象作者ID（冗余）',
  
  -- 用户信息（冗余字段，避免连表）
  `user_nickname`   VARCHAR(100)                     COMMENT '用户昵称（冗余）',
  
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、cancelled',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `favorite_type`, `target_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_favorite_type` (`favorite_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏主表';

-- ==========================================
-- 11. 评论模块
-- ==========================================

-- 评论主表（去连表化设计）
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `comment_type`          VARCHAR(20)  NOT NULL                COMMENT '评论类型：CONTENT、DYNAMIC',
  `target_id`             BIGINT       NOT NULL                COMMENT '目标对象ID',
  `parent_comment_id`     BIGINT       NOT NULL DEFAULT 0     COMMENT '父评论ID，0表示根评论',
  `content`               TEXT         NOT NULL                COMMENT '评论内容',
  
  -- 用户信息（冗余字段，避免连表）
  `user_id`               BIGINT       NOT NULL                COMMENT '评论用户ID',
  `user_nickname`         VARCHAR(100)                         COMMENT '用户昵称（冗余）',
  `user_avatar`           VARCHAR(500)                         COMMENT '用户头像（冗余）',
  
  -- 回复相关
  `reply_to_user_id`      BIGINT                               COMMENT '回复目标用户ID',
  `reply_to_user_nickname` VARCHAR(100)                        COMMENT '回复目标用户昵称（冗余）',
  `reply_to_user_avatar`   VARCHAR(500)                         COMMENT '回复目标用户头像（冗余）',
  
  -- 状态和统计
  `status`                VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL、HIDDEN、DELETED',
  `like_count`            INT          NOT NULL DEFAULT 0     COMMENT '点赞数（冗余统计）',
  `reply_count`           INT          NOT NULL DEFAULT 0     COMMENT '回复数（冗余统计）',
  
  `create_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_comment_id` (`parent_comment_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论主表';

-- ==========================================
-- 12. 社交动态模块
-- ==========================================

-- 动态主表（去连表化设计）
DROP TABLE IF EXISTS `t_social_dynamic`;
CREATE TABLE `t_social_dynamic` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `content`      TEXT         NOT NULL                COMMENT '动态内容',
  `dynamic_type` VARCHAR(20)  NOT NULL DEFAULT 'text' COMMENT '动态类型：text、image、video、share',
  `images`       TEXT                                 COMMENT '图片列表，JSON格式',
  `video_url`    VARCHAR(500)                         COMMENT '视频URL',
  
  -- 用户信息（冗余字段，避免连表）
  `user_id`      BIGINT       NOT NULL                COMMENT '发布用户ID',
  `user_nickname` VARCHAR(100)                        COMMENT '用户昵称（冗余）',
  `user_avatar`  VARCHAR(500)                         COMMENT '用户头像（冗余）',
  
  -- 分享相关（如果是分享动态）
  `share_target_type` VARCHAR(20)                     COMMENT '分享目标类型：content、goods',
  `share_target_id`   BIGINT                          COMMENT '分享目标ID',
  `share_target_title` VARCHAR(200)                   COMMENT '分享目标标题（冗余）',
  
  -- 统计字段（冗余存储，避免聚合查询）
  `like_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '点赞数（冗余统计）',
  `comment_count` BIGINT      NOT NULL DEFAULT 0     COMMENT '评论数（冗余统计）',
  `share_count`  BIGINT       NOT NULL DEFAULT 0     COMMENT '分享数（冗余统计）',
  
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'normal' COMMENT '状态：normal、hidden、deleted',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dynamic_type` (`dynamic_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交动态主表';

-- ==========================================
-- 13. 消息模块
-- ==========================================

-- 私信消息主表
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    `sender_id`     BIGINT       NOT NULL                COMMENT '发送者ID',
    `receiver_id`   BIGINT       NOT NULL                COMMENT '接收者ID',
    `content`       TEXT         NOT NULL                COMMENT '消息内容',
    `message_type`  VARCHAR(20)  NOT NULL DEFAULT 'text' COMMENT '消息类型：text、image、file、system',
    `extra_data`    JSON                                 COMMENT '扩展数据（图片URL、文件信息等）',
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'sent' COMMENT '消息状态：sent、delivered、read、deleted',
    `read_time`     TIMESTAMP    NULL                    COMMENT '已读时间',
    `reply_to_id`   BIGINT       NULL                    COMMENT '回复的消息ID（引用消息）',
    `is_pinned`     TINYINT(1)   NOT NULL DEFAULT 0     COMMENT '是否置顶（留言板功能）',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_sender_receiver` (`sender_id`, `receiver_id`),
    KEY `idx_receiver_status` (`receiver_id`, `status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_reply_to` (`reply_to_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信消息表';

-- 用户会话统计表
DROP TABLE IF EXISTS `t_message_session`;
CREATE TABLE `t_message_session` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT '会话ID',
    `user_id`           BIGINT       NOT NULL                COMMENT '用户ID',
    `other_user_id`     BIGINT       NOT NULL                COMMENT '对方用户ID',
    `last_message_id`   BIGINT       NULL                    COMMENT '最后一条消息ID',
    `last_message_time` TIMESTAMP    NULL                    COMMENT '最后消息时间',
    `unread_count`      INT          NOT NULL DEFAULT 0     COMMENT '未读消息数',
    `is_archived`       TINYINT(1)   NOT NULL DEFAULT 0     COMMENT '是否归档',
    `create_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_other` (`user_id`, `other_user_id`),
    KEY `idx_user_time` (`user_id`, `last_message_time`),
    KEY `idx_last_message` (`last_message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户会话统计表';

-- 用户消息设置表
DROP TABLE IF EXISTS `t_message_setting`;
CREATE TABLE `t_message_setting` (
    `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '设置ID',
    `user_id`               BIGINT       NOT NULL                COMMENT '用户ID',
    `allow_stranger_msg`    TINYINT(1)   NOT NULL DEFAULT 1     COMMENT '是否允许陌生人发消息',
    `auto_read_receipt`     TINYINT(1)   NOT NULL DEFAULT 1     COMMENT '是否自动发送已读回执',
    `message_notification`  TINYINT(1)   NOT NULL DEFAULT 1     COMMENT '是否开启消息通知',
    `create_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户消息设置表';

-- ==========================================
-- 14. 搜索模块
-- ==========================================

-- 搜索历史表
DROP TABLE IF EXISTS `t_search_history`;
CREATE TABLE `t_search_history` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
  `keyword`     VARCHAR(200) NOT NULL                COMMENT '搜索关键词',
  `search_type` VARCHAR(20)  NOT NULL DEFAULT 'content' COMMENT '搜索类型：content、goods、user',
  `result_count` INT         NOT NULL DEFAULT 0     COMMENT '搜索结果数量',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword` (`keyword`),
  KEY `idx_search_type` (`search_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- 热门搜索表
DROP TABLE IF EXISTS `t_hot_search`;
CREATE TABLE `t_hot_search` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '热搜ID',
  `keyword`     VARCHAR(200) NOT NULL                COMMENT '搜索关键词',
  `search_count` BIGINT      NOT NULL DEFAULT 0     COMMENT '搜索次数',
  `trend_score` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '趋势分数',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`),
  KEY `idx_search_count` (`search_count`),
  KEY `idx_trend_score` (`trend_score`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='热门搜索表';

-- ==========================================
-- 15. 任务模块
-- ==========================================

-- 任务模板表（系统预定义的任务类型）
DROP TABLE IF EXISTS `t_task_template`;
CREATE TABLE `t_task_template` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务模板ID',
    `task_name`       VARCHAR(100) NOT NULL                COMMENT '任务名称',
    `task_desc`       VARCHAR(500) NOT NULL                COMMENT '任务描述',
    `task_type`       VARCHAR(50)  NOT NULL                COMMENT '任务类型：daily、weekly、achievement',
    `task_category`   VARCHAR(50)  NOT NULL                COMMENT '任务分类：login、content、social、consume',
    `task_action`     VARCHAR(50)  NOT NULL                COMMENT '任务动作：login、publish_content、like、comment、share、purchase',
    `target_count`    INT          NOT NULL DEFAULT 1      COMMENT '目标完成次数',
    `sort_order`      INT          NOT NULL DEFAULT 0      COMMENT '排序值',
    `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否启用',
    `start_date`      DATE         NULL                    COMMENT '任务开始日期',
    `end_date`        DATE         NULL                    COMMENT '任务结束日期',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_task_category` (`task_category`),
    KEY `idx_active_sort` (`is_active`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板表';

-- 任务奖励表
DROP TABLE IF EXISTS `t_task_reward`;
CREATE TABLE `t_task_reward` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
    `task_id`       BIGINT       NOT NULL                COMMENT '任务模板ID',
    `reward_type`   VARCHAR(50)  NOT NULL                COMMENT '奖励类型：coin、item、vip、experience',
    `reward_name`   VARCHAR(100) NOT NULL                COMMENT '奖励名称',
    `reward_desc`   VARCHAR(500)                         COMMENT '奖励描述',
    `reward_amount` INT          NOT NULL DEFAULT 1      COMMENT '奖励数量',
    `reward_data`   JSON                                 COMMENT '奖励扩展数据（商品信息等）',
    `is_main_reward` TINYINT(1)  NOT NULL DEFAULT 1      COMMENT '是否主要奖励',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_reward_type` (`reward_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务奖励配置表';

-- 用户任务记录表
DROP TABLE IF EXISTS `t_user_task_record`;
CREATE TABLE `t_user_task_record` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`         BIGINT       NOT NULL                COMMENT '用户ID',
    `task_id`         BIGINT       NOT NULL                COMMENT '任务模板ID',
    `task_date`       DATE         NOT NULL                COMMENT '任务日期（用于每日任务）',
    
    -- 任务信息冗余（避免连表查询）
    `task_name`       VARCHAR(100) NOT NULL                COMMENT '任务名称（冗余）',
    `task_type`       VARCHAR(50)  NOT NULL                COMMENT '任务类型（冗余）',
    `task_category`   VARCHAR(50)  NOT NULL                COMMENT '任务分类（冗余）',
    `target_count`    INT          NOT NULL                COMMENT '目标完成次数（冗余）',
    
    -- 完成情况
    `current_count`   INT          NOT NULL DEFAULT 0      COMMENT '当前完成次数',
    `is_completed`    TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否已完成',
    `is_rewarded`     TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否已领取奖励',
    `complete_time`   TIMESTAMP    NULL                    COMMENT '完成时间',
    `reward_time`     TIMESTAMP    NULL                    COMMENT '奖励领取时间',
    
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `task_date`),
    KEY `idx_user_date` (`user_id`, `task_date`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_completed` (`is_completed`),
    KEY `idx_rewarded` (`is_rewarded`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户任务记录表';

-- 用户奖励记录表
DROP TABLE IF EXISTS `t_user_reward_record`;
CREATE TABLE `t_user_reward_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '奖励记录ID',
    `user_id`       BIGINT       NOT NULL                COMMENT '用户ID',
    `task_record_id` BIGINT      NOT NULL                COMMENT '任务记录ID',
    `reward_source` VARCHAR(50)  NOT NULL DEFAULT 'task' COMMENT '奖励来源：task、event、system',
    
    -- 奖励信息
    `reward_type`   VARCHAR(50)  NOT NULL                COMMENT '奖励类型：coin、item、vip、experience',
    `reward_name`   VARCHAR(100) NOT NULL                COMMENT '奖励名称',
    `reward_amount` INT          NOT NULL                COMMENT '奖励数量',
    `reward_data`   JSON                                 COMMENT '奖励扩展数据',
    
    -- 发放状态
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending、success、failed',
    `grant_time`    TIMESTAMP    NULL                    COMMENT '发放时间',
    `expire_time`   TIMESTAMP    NULL                    COMMENT '过期时间',
    
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_task_record` (`task_record_id`),
    KEY `idx_reward_type` (`reward_type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户奖励记录表';

-- ==========================================
-- 16. 广告模块 - 极简版
-- ==========================================

-- 广告表（唯一核心表）
DROP TABLE IF EXISTS `t_ad`;
CREATE TABLE `t_ad` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '广告ID',
    `ad_name`         VARCHAR(200) NOT NULL                COMMENT '广告名称',
    `ad_title`        VARCHAR(300) NOT NULL                COMMENT '广告标题',
    `ad_description`  VARCHAR(500)                         COMMENT '广告描述',
    
    -- 广告类型（核心字段）
    `ad_type`         VARCHAR(50)  NOT NULL                COMMENT '广告类型：banner、sidebar、popup、modal',
    
    -- 广告素材（核心字段）
    `image_url`       VARCHAR(1000) NOT NULL               COMMENT '广告图片URL',
    `click_url`       VARCHAR(1000) NOT NULL               COMMENT '点击跳转链接',
    
    -- 可选字段
    `alt_text`        VARCHAR(200)                         COMMENT '图片替代文本',
    `target_type`     VARCHAR(30)  NOT NULL DEFAULT '_blank' COMMENT '链接打开方式：_blank、_self',
    
    -- 状态管理
    `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否启用（1启用，0禁用）',
    `sort_order`      INT          NOT NULL DEFAULT 0      COMMENT '排序权重（数值越大优先级越高）',
    
    -- 时间字段
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_ad_type` (`ad_type`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告表';

-- ==========================================
-- 钱包操作存储过程
-- ==========================================

DELIMITER $$

-- 金币奖励发放（任务系统调用）
DROP PROCEDURE IF EXISTS `grant_coin_reward`$$
CREATE PROCEDURE `grant_coin_reward`(
    IN p_user_id BIGINT,
    IN p_amount BIGINT,
    IN p_source VARCHAR(50)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 更新用户金币余额
    INSERT INTO `t_user_wallet` (`user_id`, `coin_balance`, `coin_total_earned`)
    VALUES (p_user_id, p_amount, p_amount)
    ON DUPLICATE KEY UPDATE
        `coin_balance` = `coin_balance` + p_amount,
        `coin_total_earned` = `coin_total_earned` + p_amount,
        `update_time` = CURRENT_TIMESTAMP;
    
    COMMIT;
END$$

-- 金币消费（商城等系统调用）
DROP PROCEDURE IF EXISTS `consume_coin`$$
CREATE PROCEDURE `consume_coin`(
    IN p_user_id BIGINT,
    IN p_amount BIGINT,
    IN p_reason VARCHAR(100),
    OUT p_result INT
)
BEGIN
    DECLARE v_current_balance BIGINT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_result = -1;
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 检查余额是否充足
    SELECT `coin_balance` INTO v_current_balance
    FROM `t_user_wallet`
    WHERE `user_id` = p_user_id
    FOR UPDATE;
    
    IF v_current_balance IS NULL THEN
        -- 用户钱包不存在，创建钱包
        INSERT INTO `t_user_wallet` (`user_id`) VALUES (p_user_id);
        SET v_current_balance = 0;
    END IF;
    
    IF v_current_balance < p_amount THEN
        SET p_result = 0; -- 余额不足
        ROLLBACK;
    ELSE
        -- 扣减金币
        UPDATE `t_user_wallet`
        SET `coin_balance` = `coin_balance` - p_amount,
            `coin_total_spent` = `coin_total_spent` + p_amount,
            `update_time` = CURRENT_TIMESTAMP
        WHERE `user_id` = p_user_id;
        
        SET p_result = 1; -- 成功
        COMMIT;
    END IF;
END$$

-- 现金充值
DROP PROCEDURE IF EXISTS `recharge_balance`$$
CREATE PROCEDURE `recharge_balance`(
    IN p_user_id BIGINT,
    IN p_amount DECIMAL(15,2),
    IN p_payment_method VARCHAR(50)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 更新用户现金余额
    INSERT INTO `t_user_wallet` (`user_id`, `balance`, `total_income`)
    VALUES (p_user_id, p_amount, p_amount)
    ON DUPLICATE KEY UPDATE
        `balance` = `balance` + p_amount,
        `total_income` = `total_income` + p_amount,
        `update_time` = CURRENT_TIMESTAMP;
    
    COMMIT;
END$$

DELIMITER ;

-- ==========================================
-- 初始化基础数据
-- ==========================================

-- 初始化分类数据
INSERT INTO `t_category` (`name`, `description`, `parent_id`, `sort`, `status`) VALUES
('小说', '文学小说类内容', 0, 1, 'active'),
('漫画', '漫画插画类内容', 0, 2, 'active'),
('视频', '视频影像类内容', 0, 3, 'active'),
('文章', '图文资讯类内容', 0, 4, 'active'),
('音频', '音频播客类内容', 0, 5, 'active');

-- 初始化标签数据
INSERT INTO `t_tag` (`name`, `description`, `tag_type`, `usage_count`, `status`) VALUES
('热门', '热门内容标签', 'system', 1000, 'active'),
('推荐', '推荐内容标签', 'system', 900, 'active'),
('精选', '精选内容标签', 'system', 800, 'active'),
('技术', '技术相关内容', 'content', 500, 'active'),
('生活', '生活相关内容', 'content', 400, 'active'),
('娱乐', '娱乐相关内容', 'content', 350, 'active'),
('编程', '编程兴趣', 'interest', 400, 'active'),
('设计', '设计兴趣', 'interest', 350, 'active'),
('摄影', '摄影兴趣', 'interest', 300, 'active');

-- 初始化管理员用户
INSERT INTO `t_user` (`username`, `nickname`, `email`, `password_hash`, `role`, `status`) VALUES
('admin', '系统管理员', 'admin@collide.com', '$2a$10$encrypted_password_hash', 'admin', 'active'),
('blogger', '博主示例', 'blogger@collide.com', '$2a$10$encrypted_password_hash', 'blogger', 'active');

-- 初始化管理员钱包
INSERT INTO `t_user_wallet` (`user_id`, `balance`, `coin_balance`, `coin_total_earned`) VALUES
(1, 1000.00, 500, 500),
(2, 100.00, 100, 100);

-- 初始化商品数据
-- 金币类商品（充值包）
INSERT INTO `t_goods` (`name`, `description`, `category_id`, `category_name`, `goods_type`, `price`, `coin_amount`, `seller_id`, `seller_name`, `cover_url`) VALUES
('100金币充值包', '充值100金币到您的账户', 1, '金币充值', 'coin', 10.00, 100, 1, '官方商城', '/images/coin_100.png'),
('500金币充值包', '充值500金币到您的账户，送50金币', 1, '金币充值', 'coin', 50.00, 550, 1, '官方商城', '/images/coin_500.png'),
('1000金币充值包', '充值1000金币到您的账户，送200金币', 1, '金币充值', 'coin', 100.00, 1200, 1, '官方商城', '/images/coin_1000.png');

-- 实体商品
INSERT INTO `t_goods` (`name`, `description`, `category_id`, `category_name`, `goods_type`, `price`, `original_price`, `stock`, `seller_id`, `seller_name`, `cover_url`) VALUES
('精美笔记本', '高质量皮质笔记本，适合记录灵感', 2, '文具用品', 'goods', 29.90, 39.90, 100, 2, '文具专营店', '/images/notebook.png'),
('定制T恤', '个性化定制T恤，多种颜色可选', 3, '服装配饰', 'goods', 89.00, 120.00, 50, 3, '服装旗舰店', '/images/tshirt.png');

-- 订阅服务
INSERT INTO `t_goods` (`name`, `description`, `category_id`, `category_name`, `goods_type`, `price`, `subscription_duration`, `subscription_type`, `seller_id`, `seller_name`, `cover_url`) VALUES
('VIP会员月卡', '享受VIP特权，无广告浏览，专属内容', 4, '会员服务', 'subscription', 19.90, 30, 'VIP', 1, '官方商城', '/images/vip_monthly.png'),
('VIP会员年卡', '享受VIP特权，无广告浏览，专属内容', 4, '会员服务', 'subscription', 199.00, 365, 'VIP', 1, '官方商城', '/images/vip_yearly.png'),
('PREMIUM会员月卡', '高级会员权益，包含VIP所有功能+更多特权', 4, '会员服务', 'subscription', 39.90, 30, 'PREMIUM', 1, '官方商城', '/images/premium_monthly.png');

-- 付费内容
INSERT INTO `t_goods` (`name`, `description`, `category_id`, `category_name`, `goods_type`, `coin_price`, `content_id`, `content_title`, `seller_id`, `seller_name`, `cover_url`) VALUES
('Java高级教程', '深入学习Java高级特性和框架应用', 5, '付费内容', 'content', 50, 1001, 'Java高级教程', 4, '技术博主', '/images/java_tutorial.png'),
('Python爬虫实战', '从零开始学习Python网络爬虫技术', 5, '付费内容', 'content', 80, 1002, 'Python爬虫实战', 5, '编程专家', '/images/python_spider.png'),
('前端架构设计', '大型前端项目架构设计与最佳实践', 5, '付费内容', 'content', 120, 1003, '前端架构设计', 6, '前端大神', '/images/frontend_arch.png');

-- 初始化任务模板
-- 每日任务模板
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('每日登录', '每日登录获得金币奖励', 'daily', 'login', 'login', 1, 1),
('发布内容', '发布1篇内容获得奖励', 'daily', 'content', 'publish_content', 1, 2),
('点赞互动', '为其他用户内容点赞5次', 'daily', 'social', 'like', 5, 3),
('评论互动', '发表3条有效评论', 'daily', 'social', 'comment', 3, 4),
('分享内容', '分享1次内容到社交平台', 'daily', 'social', 'share', 1, 5);

-- 周常任务模板
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('周活跃用户', '连续登录7天', 'weekly', 'login', 'login', 7, 10),
('内容创作者', '本周发布5篇内容', 'weekly', 'content', 'publish_content', 5, 11),
('社交达人', '本周获得50个点赞', 'weekly', 'social', 'like_received', 50, 12);

-- 初始化任务奖励配置
-- 每日任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `is_main_reward`) VALUES
-- 每日登录奖励
(1, 'coin', '金币', '每日登录奖励', 10, 1),
-- 发布内容奖励
(2, 'coin', '金币', '发布内容奖励', 20, 1),
(2, 'experience', '经验值', '创作经验', 5, 0),
-- 点赞互动奖励
(3, 'coin', '金币', '社交互动奖励', 15, 1),
-- 评论互动奖励
(4, 'coin', '金币', '评论互动奖励', 12, 1),
-- 分享内容奖励
(5, 'coin', '金币', '分享内容奖励', 8, 1);

-- 周常任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
-- 周活跃用户奖励
(6, 'coin', '金币', '周活跃奖励', 100, NULL, 1),
(6, 'item', '活跃宝箱', '随机道具宝箱', 1, JSON_OBJECT('item_id', 1001, 'item_type', 'treasure_box'), 0),
-- 内容创作者奖励
(7, 'coin', '金币', '创作者奖励', 150, NULL, 1),
(7, 'vip', 'VIP体验', 'VIP功能体验3天', 3, JSON_OBJECT('vip_type', 'premium', 'duration_days', 3), 0),
-- 社交达人奖励
(8, 'coin', '金币', '社交达人奖励', 80, NULL, 1);

-- 创建示例广告
INSERT INTO `t_ad` (`ad_name`, `ad_title`, `ad_description`, `ad_type`, `image_url`, `click_url`, `alt_text`, `sort_order`) VALUES
('首页横幅广告', '最新科技产品推广', '体验未来科技，提升工作效率', 'banner', 'https://example.com/images/tech-banner.jpg', 'https://example.com/tech-product', '科技产品广告', 100),
('侧边栏推荐', '在线教育课程', '零基础学编程，30天成为开发者', 'sidebar', 'https://example.com/images/course-sidebar.jpg', 'https://example.com/courses', '编程课程广告', 90),
('弹窗广告', '限时优惠活动', '年终大促，全场8折起', 'popup', 'https://example.com/images/sale-popup.jpg', 'https://example.com/sale', '优惠活动广告', 80),
('模态框广告', '手机APP下载', '新用户注册立享专属优惠', 'modal', 'https://example.com/images/app-modal.jpg', 'https://example.com/app-download', '手机APP广告', 70),
('推荐商品', '精品好物推荐', '品质生活，从选择开始', 'banner', 'https://example.com/images/product-banner.jpg', 'https://example.com/products', '商品推荐广告', 60);

-- 插入默认系统消息类型配置
INSERT INTO `t_message` (`sender_id`, `receiver_id`, `content`, `message_type`, `status`) VALUES
(0, 0, '欢迎使用Collide私信功能！', 'system', 'read');

-- ==========================================
-- 数据库初始化完成
-- ==========================================

-- 数据库总表数: 22个表
-- 支持功能模块: 用户、分类、标签、内容、商品、订单、支付、关注、点赞、收藏、评论、社交动态、消息、搜索、任务、广告（极简版）
-- 设计原则: 无连表设计，冗余字段避免JOIN查询，提高性能
-- 扩展能力: 支持多种商品类型、双支付模式、任务奖励系统、极简广告管理等
-- 广告系统: 采用极简设计，只保留核心功能（广告类型、图片、链接、权重排序）
