-- ===============================================
-- Collide 微服务数据库表结构
-- 包含商品、订单、支付、权限四个核心业务域
-- ===============================================

-- -------------------------------------------
-- 1. 商品服务相关表
-- -------------------------------------------

-- 商品表
CREATE TABLE `goods` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `type` VARCHAR(20) NOT NULL COMMENT '商品类型：COIN-金币类，SUBSCRIPTION-订阅类',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '商品状态：DRAFT-草稿，ON_SALE-销售中，OFF_SALE-下架，SOLD_OUT-售罄，DISABLED-禁用',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格（元）',
    `image_url` VARCHAR(500) COMMENT '商品图片URL',
    `detail_images` JSON COMMENT '商品详情图片URLs（JSON格式）',
    `category_id` BIGINT COMMENT '商品分类ID',
    `sort_weight` INT DEFAULT 0 COMMENT '排序权重',
    `stock` INT DEFAULT -1 COMMENT '库存数量（-1表示无限库存）',
    `sold_count` INT DEFAULT 0 COMMENT '已售数量',
    `subscription_days` INT COMMENT '订阅周期天数（订阅类商品使用）',
    `coin_amount` INT COMMENT '金币数量（金币类商品购买后获得的金币数）',
    `recommended` TINYINT(1) DEFAULT 0 COMMENT '是否推荐',
    `hot` TINYINT(1) DEFAULT 0 COMMENT '是否热门',
    `on_sale_time` DATETIME COMMENT '上架时间',
    `off_sale_time` DATETIME COMMENT '下架时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `updater_id` BIGINT COMMENT '更新者ID',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识',
    `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    KEY `idx_type_status` (`type`, `status`),
    KEY `idx_status_recommended` (`status`, `recommended`),
    KEY `idx_status_hot` (`status`, `hot`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_creator_id` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- -------------------------------------------
-- 2. 订单服务相关表
-- -------------------------------------------

-- 订单表
CREATE TABLE `order_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `goods_id` BIGINT NOT NULL COMMENT '商品ID',
    `goods_name` VARCHAR(200) NOT NULL COMMENT '商品名称（冗余存储）',
    `goods_type` VARCHAR(20) NOT NULL COMMENT '商品类型（冗余存储）',
    `goods_price` DECIMAL(10,2) NOT NULL COMMENT '商品价格（冗余存储）',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'CREATE' COMMENT '订单状态：CREATE-创建，UNPAID-未付款，PAID-已支付，CANCELLED-已取消，REFUNDED-已退款',
    `pay_type` VARCHAR(20) COMMENT '支付方式：ALIPAY-支付宝，WECHAT-微信，TEST-测试支付',
    `pay_time` DATETIME COMMENT '支付时间',
    `expire_time` DATETIME COMMENT '订单过期时间',
    `remark` TEXT COMMENT '订单备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识',
    `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id_status` (`user_id`, `status`),
    KEY `idx_goods_id` (`goods_id`),
    KEY `idx_status_create_time` (`status`, `create_time`),
    KEY `idx_pay_time` (`pay_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单内容关联表
CREATE TABLE `order_content_association` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID（关联collide-content）',
    `content_type` VARCHAR(20) NOT NULL COMMENT '内容类型：VIDEO-视频，ARTICLE-文章，LIVE-直播，COURSE-课程',
    `content_title` VARCHAR(200) COMMENT '内容标题（冗余存储）',
    `access_type` VARCHAR(20) NOT NULL COMMENT '访问权限类型：PERMANENT-永久，TEMPORARY-临时，SUBSCRIPTION_BASED-基于订阅',
    `access_start_time` DATETIME NOT NULL COMMENT '权限开始时间',
    `access_end_time` DATETIME COMMENT '权限结束时间（null表示永久）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '权限状态：ACTIVE-激活，EXPIRED-已过期，REVOKED-已撤销',
    `goods_id` BIGINT NOT NULL COMMENT '购买时的商品ID',
    `goods_type` VARCHAR(20) NOT NULL COMMENT '购买时的商品类型',
    `consumed_coins` INT COMMENT '消费的金币数量',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识',
    `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_content` (`order_id`, `content_id`),
    KEY `idx_user_content` (`user_id`, `content_id`),
    KEY `idx_content_id_status` (`content_id`, `status`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_access_end_time` (`access_end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单内容关联表';

-- -------------------------------------------
-- 3. 支付服务相关表
-- -------------------------------------------

-- 支付记录表
CREATE TABLE `payment_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
    `payment_no` VARCHAR(64) NOT NULL COMMENT '支付流水号',
    `order_no` VARCHAR(64) NOT NULL COMMENT '关联订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `pay_type` VARCHAR(20) NOT NULL COMMENT '支付方式：ALIPAY-支付宝，WECHAT-微信，TEST-测试支付',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `status` VARCHAR(20) NOT NULL DEFAULT 'CREATED' COMMENT '支付状态：CREATED-创建，PROCESSING-处理中，SUCCESS-成功，FAILED-失败，CANCELLED-取消，TIMEOUT-超时',
    `third_party_trade_no` VARCHAR(100) COMMENT '三方支付流水号',
    `pay_url` VARCHAR(500) COMMENT '支付链接',
    `qr_code_url` VARCHAR(500) COMMENT '二维码链接',
    `notify_url` VARCHAR(500) COMMENT '异步通知地址',
    `return_url` VARCHAR(500) COMMENT '同步跳转地址',
    `pay_success_time` DATETIME COMMENT '支付成功时间',
    `expire_time` DATETIME COMMENT '支付过期时间',
    `callback_data` JSON COMMENT '支付回调数据',
    `error_msg` TEXT COMMENT '错误信息',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识',
    `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_no` (`payment_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_user_id_status` (`user_id`, `status`),
    KEY `idx_third_party_trade_no` (`third_party_trade_no`),
    KEY `idx_status_create_time` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- -------------------------------------------
-- 4. 权限服务相关表
-- -------------------------------------------

-- 用户权限表
CREATE TABLE `user_permission` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content_id` BIGINT NOT NULL COMMENT '内容ID',
    `permission_type` VARCHAR(30) NOT NULL COMMENT '权限类型：NORMAL_USER_PAID-普通用户付费，NORMAL_USER_FREE-普通用户免费，VIP_USER_PAID-VIP用户付费，VIP_USER_FREE-VIP用户免费',
    `permission_source` VARCHAR(30) NOT NULL COMMENT '权限来源：ORDER_PURCHASE-订单购买，VIP_PRIVILEGE-VIP特权，FREE_CONTENT-免费内容，SUBSCRIPTION-订阅',
    `related_order_no` VARCHAR(64) COMMENT '关联订单号（如果通过购买获得）',
    `start_time` DATETIME NOT NULL COMMENT '权限开始时间',
    `end_time` DATETIME COMMENT '权限结束时间（null表示永久）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '权限状态：ACTIVE-激活，EXPIRED-已过期，REVOKED-已撤销',
    `grant_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '授权时间',
    `revoke_time` DATETIME COMMENT '撤销时间',
    `remark` TEXT COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识',
    `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_content_type` (`user_id`, `content_id`, `permission_type`),
    KEY `idx_content_id_status` (`content_id`, `status`),
    KEY `idx_related_order_no` (`related_order_no`),
    KEY `idx_end_time` (`end_time`),
    KEY `idx_permission_source` (`permission_source`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限表';

-- 用户VIP信息表
CREATE TABLE `user_vip_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'VIP信息ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `vip_type` VARCHAR(20) NOT NULL COMMENT 'VIP类型：MONTHLY-月度，YEARLY-年度，LIFETIME-终身',
    `vip_level` INT NOT NULL DEFAULT 1 COMMENT 'VIP等级',
    `start_time` DATETIME NOT NULL COMMENT 'VIP开始时间',
    `end_time` DATETIME COMMENT 'VIP结束时间（null表示终身）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'VIP状态：ACTIVE-激活，EXPIRED-已过期，CANCELLED-已取消',
    `source_order_no` VARCHAR(64) COMMENT '开通VIP的订单号',
    `auto_renew` TINYINT(1) DEFAULT 0 COMMENT '是否自动续费',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '逻辑删除标识',
    `version` INT DEFAULT 1 COMMENT '版本号（乐观锁）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_end_time_status` (`end_time`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户VIP信息表';

-- -------------------------------------------
-- 5. 初始化数据
-- -------------------------------------------

-- 插入测试商品数据
INSERT INTO `goods` (`name`, `description`, `type`, `status`, `price`, `stock`, `coin_amount`, `subscription_days`, `recommended`, `hot`, `creator_id`) VALUES
('金币包-100', '100个金币，可用于购买付费内容', 'COIN', 'ON_SALE', 10.00, -1, 100, NULL, 1, 1, 1),
('金币包-500', '500个金币，可用于购买付费内容', 'COIN', 'ON_SALE', 45.00, -1, 500, NULL, 1, 0, 1),
('金币包-1000', '1000个金币，可用于购买付费内容', 'COIN', 'ON_SALE', 80.00, -1, 1000, NULL, 1, 1, 1),
('月度VIP会员', '30天VIP会员，享受专属内容和优惠', 'SUBSCRIPTION', 'ON_SALE', 29.90, -1, NULL, 30, 1, 1, 1),
('年度VIP会员', '365天VIP会员，享受专属内容和优惠', 'SUBSCRIPTION', 'ON_SALE', 299.00, -1, NULL, 365, 1, 1, 1),
('终身VIP会员', '终身VIP会员，永久享受专属内容', 'SUBSCRIPTION', 'ON_SALE', 999.00, -1, NULL, 99999, 1, 1, 1);

-- -------------------------------------------
-- 6. 创建索引优化
-- -------------------------------------------

-- 权限查询优化索引
CREATE INDEX `idx_user_permission_check` ON `user_permission` (`user_id`, `content_id`, `status`, `start_time`, `end_time`);

-- 订单内容关联查询优化索引  
CREATE INDEX `idx_order_content_access` ON `order_content_association` (`user_id`, `content_id`, `status`, `access_start_time`, `access_end_time`);

-- 支付记录查询优化索引
CREATE INDEX `idx_payment_query` ON `payment_record` (`order_no`, `status`, `pay_success_time`);

-- 商品销售统计索引
CREATE INDEX `idx_goods_sales_stats` ON `order_info` (`goods_id`, `status`, `pay_time`);

-- -------------------------------------------
-- 7. 存储过程（可选）
-- -------------------------------------------

-- 检查用户内容权限的存储过程
DELIMITER $$
CREATE PROCEDURE `CheckUserContentPermission`(
    IN p_user_id BIGINT,
    IN p_content_id BIGINT,
    OUT p_has_permission TINYINT,
    OUT p_permission_type VARCHAR(30),
    OUT p_expire_time DATETIME
)
BEGIN
    DECLARE v_count INT DEFAULT 0;
    DECLARE v_now DATETIME DEFAULT NOW();
    
    -- 检查是否有有效权限
    SELECT COUNT(*), permission_type, end_time
    INTO v_count, p_permission_type, p_expire_time
    FROM user_permission 
    WHERE user_id = p_user_id 
      AND content_id = p_content_id 
      AND status = 'ACTIVE'
      AND start_time <= v_now
      AND (end_time IS NULL OR end_time > v_now)
    LIMIT 1;
    
    IF v_count > 0 THEN
        SET p_has_permission = 1;
    ELSE
        SET p_has_permission = 0;
        SET p_permission_type = NULL;
        SET p_expire_time = NULL;
    END IF;
END$$
DELIMITER ; 