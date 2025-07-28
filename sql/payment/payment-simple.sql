-- ==========================================
-- 支付模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

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