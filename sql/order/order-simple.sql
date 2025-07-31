-- ==========================================
-- 订单模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

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
  `content_title` VARCHAR(200)                        COMMENT '内容标题（内容类商品冗余）',
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
-- 测试数据初始化
-- ==========================================

-- 金币充值订单（现金支付）
INSERT INTO `t_order` (`order_no`, `user_id`, `user_nickname`, `goods_id`, `goods_name`, `goods_type`, `coin_amount`, `quantity`, `payment_mode`, `cash_amount`, `total_amount`, `final_amount`, `pay_method`, `status`, `pay_status`) VALUES
('ORD202501310001', 1, '测试用户1', 1, '100金币充值包', 'coin', 100, 1, 'cash', 10.00, 10.00, 10.00, 'alipay', 'paid', 'paid'),
('ORD202501310002', 2, '测试用户2', 2, '500金币充值包', 'coin', 550, 1, 'cash', 50.00, 50.00, 50.00, 'wechat', 'completed', 'paid');

-- 实体商品订单（现金支付）
INSERT INTO `t_order` (`order_no`, `user_id`, `user_nickname`, `goods_id`, `goods_name`, `goods_type`, `quantity`, `payment_mode`, `cash_amount`, `total_amount`, `discount_amount`, `final_amount`, `pay_method`, `status`, `pay_status`) VALUES
('ORD202501310003', 1, '测试用户1', 4, '精美笔记本', 'goods', 2, 'cash', 59.80, 59.80, 5.00, 54.80, 'balance', 'shipped', 'paid'),
('ORD202501310004', 3, '测试用户3', 5, '定制T恤', 'goods', 1, 'cash', 89.00, 89.00, 0.00, 89.00, 'alipay', 'pending', 'unpaid');

-- 订阅服务订单（现金支付）
INSERT INTO `t_order` (`order_no`, `user_id`, `user_nickname`, `goods_id`, `goods_name`, `goods_type`, `subscription_duration`, `subscription_type`, `quantity`, `payment_mode`, `cash_amount`, `total_amount`, `final_amount`, `pay_method`, `status`, `pay_status`) VALUES
('ORD202501310005', 2, '测试用户2', 6, 'VIP会员月卡', 'subscription', 30, 'VIP', 1, 'cash', 19.90, 19.90, 19.90, 'wechat', 'completed', 'paid'),
('ORD202501310006', 1, '测试用户1', 8, 'PREMIUM会员月卡', 'subscription', 30, 'PREMIUM', 1, 'cash', 39.90, 39.90, 39.90, 'alipay', 'paid', 'paid');

-- 付费内容订单（金币支付）
INSERT INTO `t_order` (`order_no`, `user_id`, `user_nickname`, `goods_id`, `goods_name`, `goods_type`, `content_id`, `content_title`, `quantity`, `payment_mode`, `coin_cost`, `pay_method`, `status`, `pay_status`) VALUES
('ORD202501310007', 1, '测试用户1', 9, 'Java高级教程', 'content', 1001, 'Java高级编程实战指南', 1, 'coin', 50, 'coin', 'completed', 'paid'),
('ORD202501310008', 2, '测试用户2', 10, 'Python爬虫实战', 'content', 1002, 'Python爬虫从入门到精通', 1, 'coin', 80, 'coin', 'pending', 'unpaid'),
('ORD202501310009', 3, '测试用户3', 11, '前端架构设计', 'content', 1003, '现代前端架构设计指南', 1, 'coin', 120, 'coin', 'completed', 'paid');

-- ==========================================
-- 订单处理存储过程
-- ==========================================

DELIMITER $$

-- 处理订单支付（根据商品类型执行不同逻辑）
DROP PROCEDURE IF EXISTS `process_order_payment`$$
CREATE PROCEDURE `process_order_payment`(
    IN p_order_id BIGINT,
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_goods_type VARCHAR(20);
    DECLARE v_payment_mode VARCHAR(20);
    DECLARE v_user_id BIGINT;
    DECLARE v_coin_cost BIGINT DEFAULT 0;
    DECLARE v_coin_amount BIGINT DEFAULT 0;
    DECLARE v_current_coin_balance BIGINT DEFAULT 0;
    DECLARE v_order_status VARCHAR(20);
    DECLARE v_pay_status VARCHAR(20);
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_result = -1;
        SET p_message = '系统错误，支付处理失败';
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 获取订单信息
    SELECT goods_type, payment_mode, user_id, coin_cost, coin_amount, status, pay_status
    INTO v_goods_type, v_payment_mode, v_user_id, v_coin_cost, v_coin_amount, v_order_status, v_pay_status
    FROM t_order 
    WHERE id = p_order_id;
    
    -- 检查订单状态
    IF v_order_status != 'pending' OR v_pay_status != 'unpaid' THEN
        SET p_result = 0;
        SET p_message = '订单状态不允许支付';
        ROLLBACK;
    ELSE
        -- 根据支付模式处理
        IF v_payment_mode = 'coin' THEN
            -- 金币支付（仅内容类商品）
            IF v_goods_type != 'content' THEN
                SET p_result = 0;
                SET p_message = '该商品不支持金币支付';
                ROLLBACK;
            ELSE
                -- 检查金币余额
                SELECT coin_balance INTO v_current_coin_balance
                FROM t_user_wallet 
                WHERE user_id = v_user_id;
                
                IF v_current_coin_balance < v_coin_cost THEN
                    SET p_result = 0;
                    SET p_message = '金币余额不足';
                    ROLLBACK;
                ELSE
                    -- 扣减金币
                    CALL consume_coin(v_user_id, v_coin_cost, CONCAT('购买内容-订单:', p_order_id), @coin_result);
                    
                    IF @coin_result != 1 THEN
                        SET p_result = 0;
                        SET p_message = '金币扣减失败';
                        ROLLBACK;
                    ELSE
                        -- 更新订单状态
                        UPDATE t_order 
                        SET pay_status = 'paid', 
                            status = 'completed',
                            pay_time = NOW(),
                            update_time = NOW()
                        WHERE id = p_order_id;
                        
                        SET p_result = 1;
                        SET p_message = '金币支付成功';
                    END IF;
                END IF;
            END IF;
        ELSE
            -- 现金支付（金币、商品、订阅类商品）
            -- 更新订单支付状态
            UPDATE t_order 
            SET pay_status = 'paid', 
                pay_time = NOW(),
                status = CASE 
                    WHEN goods_type = 'goods' THEN 'paid'  -- 实体商品需要发货
                    ELSE 'completed'  -- 虚拟商品直接完成
                END,
                update_time = NOW()
            WHERE id = p_order_id;
            
            -- 如果是金币充值包，发放金币
            IF v_goods_type = 'coin' THEN
                CALL grant_coin_reward(v_user_id, v_coin_amount, CONCAT('充值-订单:', p_order_id));
            END IF;
            
            SET p_result = 1;
            SET p_message = '现金支付成功';
        END IF;
    END IF;
    
    COMMIT;
END$$

-- 创建订单（自动计算金额和支付模式）
DROP PROCEDURE IF EXISTS `create_order`$$
CREATE PROCEDURE `create_order`(
    IN p_user_id BIGINT,
    IN p_goods_id BIGINT,
    IN p_quantity INT,
    OUT p_order_id BIGINT,
    OUT p_order_no VARCHAR(50),
    OUT p_result INT,
    OUT p_message VARCHAR(255)
)
BEGIN
    DECLARE v_goods_type VARCHAR(20);
    DECLARE v_goods_name VARCHAR(200);
    DECLARE v_price DECIMAL(10,2);
    DECLARE v_coin_price BIGINT;
    DECLARE v_coin_amount BIGINT;
    DECLARE v_content_id BIGINT;
    DECLARE v_content_title VARCHAR(200);
    DECLARE v_subscription_duration INT;
    DECLARE v_subscription_type VARCHAR(50);
    DECLARE v_user_nickname VARCHAR(100);
    DECLARE v_goods_cover VARCHAR(500);
    DECLARE v_category_name VARCHAR(100);
    DECLARE v_stock INT;
    DECLARE v_payment_mode VARCHAR(20);
    DECLARE v_total_amount DECIMAL(10,2);
    DECLARE v_coin_cost BIGINT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        SET p_result = -1;
        SET p_message = '系统错误，订单创建失败';
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 获取商品信息
    SELECT goods_type, name, price, coin_price, coin_amount, content_id, content_title,
           subscription_duration, subscription_type, cover_url, category_name, stock
    INTO v_goods_type, v_goods_name, v_price, v_coin_price, v_coin_amount, v_content_id, v_content_title,
         v_subscription_duration, v_subscription_type, v_goods_cover, v_category_name, v_stock
    FROM t_goods 
    WHERE id = p_goods_id AND status = 'active';
    
    -- 获取用户信息
    SELECT nickname INTO v_user_nickname FROM t_user WHERE id = p_user_id;
    
    -- 检查库存（实体商品）
    IF v_goods_type = 'goods' AND v_stock >= 0 AND v_stock < p_quantity THEN
        SET p_result = 0;
        SET p_message = '库存不足';
        ROLLBACK;
    ELSE
        -- 确定支付模式和金额
        IF v_goods_type = 'content' THEN
            SET v_payment_mode = 'coin';
            SET v_coin_cost = v_coin_price * p_quantity;
            SET v_total_amount = 0.00;
        ELSE
            SET v_payment_mode = 'cash';
            SET v_total_amount = v_price * p_quantity;
            SET v_coin_cost = 0;
        END IF;
        
        -- 生成订单号
        SET p_order_no = CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d%H%i%s'), LPAD(p_user_id, 4, '0'));
        
        -- 创建订单
        INSERT INTO t_order (
            order_no, user_id, user_nickname, goods_id, goods_name, goods_type,
            goods_cover, goods_category_name, coin_amount, content_id, content_title,
            subscription_duration, subscription_type, quantity, payment_mode,
            cash_amount, coin_cost, total_amount, final_amount
        ) VALUES (
            p_order_no, p_user_id, v_user_nickname, p_goods_id, v_goods_name, v_goods_type,
            v_goods_cover, v_category_name, v_coin_amount, v_content_id, v_content_title,
            v_subscription_duration, v_subscription_type, p_quantity, v_payment_mode,
            v_total_amount, v_coin_cost, v_total_amount, v_total_amount
        );
        
        SET p_order_id = LAST_INSERT_ID();
        
        -- 扣减库存（实体商品）
        IF v_goods_type = 'goods' AND v_stock >= 0 THEN
            UPDATE t_goods SET stock = stock - p_quantity WHERE id = p_goods_id;
        END IF;
        
        SET p_result = 1;
        SET p_message = '订单创建成功';
    END IF;
    
    COMMIT;
END$$

DELIMITER ;

-- ==========================================
-- 商品类型和支付模式说明
-- ==========================================

-- 1. 商品类型和支付方式对应关系：
--    - coin (金币充值包): 只能现金支付 → 获得金币
--    - goods (实体商品): 只能现金支付 → 发货配送
--    - subscription (订阅服务): 只能现金支付 → 获得会员权限
--    - content (付费内容): 只能金币支付 → 获得内容访问权

-- 2. 支付模式字段说明：
--    - payment_mode = 'cash': 现金支付，使用 cash_amount、total_amount、final_amount
--    - payment_mode = 'coin': 金币支付，使用 coin_cost 字段

-- 3. 订单状态流转：
--    - pending → paid → shipped → completed (实体商品)
--    - pending → completed (虚拟商品：金币、订阅、内容)

-- 4. 支付方式：
--    - alipay、wechat、balance: 现金支付渠道
--    - coin: 金币支付

-- 5. 使用示例：
--    -- 创建金币充值订单
--    CALL create_order(1, 1, 1, @order_id, @order_no, @result, @message);
--    
--    -- 处理订单支付
--    CALL process_order_payment(@order_id, @result, @message);
--    
--    -- 查询用户订单
--    SELECT * FROM t_order WHERE user_id = 1 ORDER BY create_time DESC;
--    
--    -- 查询内容类订单（金币支付）
--    SELECT * FROM t_order WHERE goods_type = 'content' AND payment_mode = 'coin';
--    
--    -- 查询今日充值订单
--    SELECT o.*, u.username FROM t_order o 
--    LEFT JOIN t_user u ON o.user_id = u.id 
--    WHERE o.goods_type = 'coin' AND DATE(o.create_time) = CURDATE();

-- 6. 业务流程总结：
--    金币充值：现金支付 → 订单完成 → 自动发放金币到钱包
--    实体商品：现金支付 → 发货 → 确认收货 → 订单完成  
--    订阅服务：现金支付 → 立即开通会员 → 订单完成
--    付费内容：金币支付 → 扣减金币 → 开放内容访问 → 订单完成 