-- ==========================================
-- 商品模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

-- 商品主表（去连表化设计）
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods`
(
    `id`             BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name`           VARCHAR(200)   NOT NULL COMMENT '商品名称',
    `description`    TEXT COMMENT '商品描述',
    `category_id`    BIGINT         NOT NULL COMMENT '分类ID',
    `category_name`  VARCHAR(100) COMMENT '分类名称（冗余）',
    `price`          DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    `original_price` DECIMAL(10, 2) COMMENT '原价',
    `coin_amount`    DECIMAL(15, 2) COMMENT '金币数量（仅金币类商品有效）',
    `stock`          INT            NOT NULL DEFAULT 0 COMMENT '库存数量',
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
    KEY `idx_category_id` (`category_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_status` (`status`),
    KEY `idx_price` (`price`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='商品主表';