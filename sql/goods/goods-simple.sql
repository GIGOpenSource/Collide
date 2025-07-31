-- ==========================================
-- 商品模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

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
    `content_title`  VARCHAR(200)   COMMENT '内容标题（冗余，仅内容类型）',
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
-- 测试数据初始化
-- ==========================================

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
('Java高级教程', '深入学习Java高级特性和框架应用', 5, '付费内容', 'content', 50, 1001, 'Java高级编程实战指南', 4, '技术博主', '/images/java_tutorial.png'),
('Python爬虫实战', '从零开始学习Python网络爬虫技术', 5, '付费内容', 'content', 80, 1002, 'Python爬虫从入门到精通', 5, '编程专家', '/images/python_spider.png'),
('前端架构设计', '大型前端项目架构设计与最佳实践', 5, '付费内容', 'content', 120, 1003, '现代前端架构设计指南', 6, '前端大神', '/images/frontend_arch.png');

-- ==========================================
-- 商品类型使用说明
-- ==========================================

-- 1. 商品类型说明：
--    - coin: 金币充值包，用现金购买获得金币
--    - goods: 实体商品，用现金购买
--    - subscription: 订阅服务，用现金购买获得会员权限
--    - content: 付费内容，只能用金币购买

-- 2. 定价字段说明：
--    - price: 现金价格（适用于coin、goods、subscription类型）
--    - coin_price: 金币价格（仅适用于content类型）
--    - coin_amount: 金币数量（仅适用于coin类型，表示购买后获得的金币数）

-- 3. 特殊字段说明：
--    - content_id: 关联的内容ID（仅content类型）
--    - subscription_duration: 订阅时长天数（仅subscription类型）
--    - subscription_type: 订阅类型（仅subscription类型）

-- 4. 库存管理：
--    - stock = -1: 无限库存（适用于虚拟商品：coin、subscription、content）
--    - stock >= 0: 有限库存（适用于实体商品：goods）

-- 5. 查询示例：
--    -- 查询金币充值包
--    SELECT * FROM t_goods WHERE goods_type = 'coin' AND status = 'active';
--    
--    -- 查询付费内容
--    SELECT * FROM t_goods WHERE goods_type = 'content' AND status = 'active';
--    
--    -- 查询某个价格范围的实体商品
--    SELECT * FROM t_goods WHERE goods_type = 'goods' AND price BETWEEN 20 AND 100;
--    
--    -- 查询VIP订阅服务
--    SELECT * FROM t_goods WHERE goods_type = 'subscription' AND subscription_type = 'VIP';