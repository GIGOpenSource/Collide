-- ==========================================
-- 支付模块初始化数据 - 大白鲨支付配置
-- 基于大白鲨支付接口文档v1.1.5
-- ==========================================

USE collide;

-- 清空现有数据
DELETE FROM `t_payment_channel` WHERE `channel_code` = 'shark_pay';

-- 插入大白鲨支付渠道配置
INSERT INTO `t_payment_channel` (
    `channel_code`, `channel_name`, `provider`, `channel_type`,
    `merchant_id`, `app_secret`, `api_gateway`, `timeout`, `retry_times`,
    `status`, `priority`, `daily_limit`, `single_limit`,
    `fee_type`, `fee_rate`, `min_fee`, `max_fee`,
    `create_time`, `update_time`
) VALUES (
    'shark_pay',                                    -- 渠道代码
    '大白鲨支付',                                   -- 渠道名称
    'shark',                                        -- 支付提供商
    'H5',                                          -- 渠道类型
    '10001',                                       -- 商户编号（需要从大白鲨支付获取）
    'your-app-secret-key-here',                    -- 商户密钥（需要配置实际密钥）
    'https://pay.example.com',                     -- API网关地址（需要配置实际地址）
    30000,                                         -- 超时时间30秒
    3,                                             -- 重试3次
    'active',                                      -- 激活状态
    100,                                           -- 优先级
    9999999.99,                                    -- 日限额
    50000.00,                                      -- 单笔限额
    'percentage',                                  -- 百分比费率
    0.0060,                                        -- 0.6%费率
    0.01,                                          -- 最小手续费1分
    50.00,                                         -- 最大手续费50元
    NOW(),                                         -- 创建时间
    NOW()                                          -- 更新时间
);

-- 插入大白鲨支付支持的支付类型配置（基于文档中的渠道类别）
-- 注意：这些是大白鲨支付文档中支持的type类型
INSERT INTO `t_payment_channel` (
    `channel_code`, `channel_name`, `provider`, `channel_type`,
    `merchant_id`, `app_secret`, `api_gateway`, `timeout`, `retry_times`,
    `status`, `priority`, `daily_limit`, `single_limit`,
    `fee_type`, `fee_rate`, `min_fee`, `max_fee`,
    `create_time`, `update_time`
) VALUES 
-- 支付宝相关渠道
('shark_alipay', '大白鲨-支付宝', 'shark', 'H5', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 95, 999999.99, 50000.00, 'percentage', 0.0055, 0.01, 50.00, NOW(), NOW()),
('shark_alipayWap', '大白鲨-支付宝WAP', 'shark', 'H5', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 94, 999999.99, 50000.00, 'percentage', 0.0055, 0.01, 50.00, NOW(), NOW()),

-- 微信相关渠道
('shark_wechat', '大白鲨-微信支付', 'shark', 'H5', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 93, 999999.99, 50000.00, 'percentage', 0.0058, 0.01, 50.00, NOW(), NOW()),
('shark_wechatWap', '大白鲨-微信WAP', 'shark', 'H5', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 92, 999999.99, 50000.00, 'percentage', 0.0058, 0.01, 50.00, NOW(), NOW()),

-- 银联相关渠道
('shark_unionCard', '大白鲨-银联转卡', 'shark', 'CARD', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 91, 999999.99, 50000.00, 'percentage', 0.0070, 0.01, 50.00, NOW(), NOW()),
('shark_quickUnion', '大白鲨-快捷银联', 'shark', 'H5', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 90, 999999.99, 50000.00, 'percentage', 0.0065, 0.01, 50.00, NOW(), NOW()),

-- USDT渠道
('shark_usdt', '大白鲨-USDT', 'shark', 'H5', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 89, 999999.99, 50000.00, 'fixed', 5.00, 5.00, 5.00, NOW(), NOW()),

-- 固定QQ和IOS支付
('shark_fixedQqPay', '大白鲨-固定QQ', 'shark', 'NATIVE', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 88, 999999.99, 50000.00, 'percentage', 0.0060, 0.01, 50.00, NOW(), NOW()),
('shark_fixedIosPay', '大白鲨-固定IOS', 'shark', 'APP', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 87, 999999.99, 50000.00, 'percentage', 0.0060, 0.01, 50.00, NOW(), NOW()),

-- EEPAY渠道
('shark_eepayCard', '大白鲨-EEPAY', 'shark', 'CARD', '10001', 'your-app-secret-key-here', 'https://pay.example.com', 30000, 3, 'active', 86, 999999.99, 50000.00, 'percentage', 0.0070, 0.01, 50.00, NOW(), NOW());

-- 查看插入结果
SELECT 
    channel_code,
    channel_name,
    channel_type,
    status,
    priority,
    fee_rate,
    single_limit
FROM t_payment_channel 
WHERE provider = 'shark'
ORDER BY priority DESC;

-- 支付统计表的示例数据（可选）
-- 注意：实际使用中这些数据应该由定时任务或触发器自动生成
/*
INSERT INTO `t_payment_statistics` (
    `stat_date`, `channel_code`, `pay_type`,
    `total_orders`, `success_orders`, `failed_orders`,
    `total_amount`, `success_amount`, `total_fee`,
    `success_rate`, `avg_amount`
) VALUES 
(CURDATE(), 'shark_alipay', 'alipay', 0, 0, 0, 0.00, 0.00, 0.00, 0.0000, 0.00),
(CURDATE(), 'shark_wechat', 'wechat', 0, 0, 0, 0.00, 0.00, 0.00, 0.0000, 0.00),
(CURDATE(), 'shark_unionCard', 'unionCard', 0, 0, 0, 0.00, 0.00, 0.00, 0.0000, 0.00);
*/