-- ==========================================
-- 支付配置中心数据库设计
-- 用于管理各种支付平台的配置信息
-- ==========================================

USE collide;

-- 支付平台配置表
DROP TABLE IF EXISTS `t_payment_config`;
CREATE TABLE `t_payment_config` (
  `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `platform_name`  VARCHAR(100) NOT NULL                COMMENT '平台名字（如：shark_pay、alipay、wechat_pay等）',
  `base_url`        VARCHAR(500) NOT NULL                COMMENT '平台base_url（API接口基础地址）',
  `merchant_id`     VARCHAR(100) NOT NULL                COMMENT '商户ID',
  `app_secret`      VARCHAR(500) NOT NULL                COMMENT '商户密钥',
  `sub_merchant_id` VARCHAR(100)                         COMMENT '子商户ID（可选）',
  `payment_method`  VARCHAR(50)  DEFAULT 'H5'           COMMENT '支付渠道（默认H5）',
  `notify_url`      VARCHAR(500) NOT NULL                COMMENT '回调通知地址',
  `sign_type`       VARCHAR(20)  DEFAULT 'MD5'          COMMENT '签名类型（MD5、SHA256、RSA等）',
  `deleted`         INT          DEFAULT 0               COMMENT '软删除标记（0：未删除，大于0：已删除）',
  `create_time`     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_name` (`platform_name`, `deleted`) COMMENT '平台名称唯一（排除已删除）',
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_create_time` (`create_time` DESC),
  KEY `idx_platform_deleted` (`platform_name`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付平台配置表';

-- 插入配置中心的初始化数据（示例）
INSERT INTO `t_payment_config` (
  `platform_name`, 
  `base_url`, 
  `merchant_id`, 
  `app_secret`, 
  `sub_merchant_id`, 
  `payment_method`, 
  `notify_url`, 
  `sign_type`, 
  `deleted`
) VALUES 
-- 大白鲨支付配置
(
  'shark_pay',
  'https://pay.example.com',
  '10001',
  'your-shark-pay-secret-key-here',
  NULL,
  'H5',
  'https://your-domain.com/payment/api/v1/payment/notify/shark',
  'MD5',
  0
),
-- 支付宝直连配置（示例）
(
  'alipay_direct',
  'https://openapi.alipay.com/gateway.do',
  'your-alipay-app-id',
  'your-alipay-private-key',
  NULL,
  'H5',
  'https://your-domain.com/payment/api/v1/payment/notify/alipay',
  'RSA',
  0
),
-- 微信支付配置（示例）
(
  'wechat_pay',
  'https://api.mch.weixin.qq.com',
  'your-wechat-merchant-id',
  'your-wechat-api-key',
  'your-wechat-sub-merchant-id',
  'H5',
  'https://your-domain.com/payment/api/v1/payment/notify/wechat',
  'MD5',
  0
);

-- 查看插入结果
SELECT 
  id,
  platform_name,
  base_url,
  merchant_id,
  CONCAT(LEFT(app_secret, 10), '****') AS app_secret_masked,
  sub_merchant_id,
  payment_method,
  notify_url,
  sign_type,
  deleted,
  create_time
FROM t_payment_config 
WHERE deleted = 0
ORDER BY create_time DESC;