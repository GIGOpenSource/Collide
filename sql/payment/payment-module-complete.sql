-- =============================================
-- Collide 支付模块数据库表结构（去连表设计）
-- 版本: 2.0.0
-- 创建时间: 2024-01-01
-- 更新时间: 2024-01-15
-- 功能: 支付记录、回调记录等表结构（无连表设计）
-- 设计原则: 去连表化，通过冗余字段避免JOIN操作
-- =============================================

-- 使用支付数据库
CREATE DATABASE IF NOT EXISTS collide_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE collide_payment;

-- =============================================
-- 支付记录表（去连表设计）
-- =============================================
DROP TABLE IF EXISTS t_payment_record;
CREATE TABLE t_payment_record (
    -- 基础信息
    id                          BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '主键ID',
    order_no                    VARCHAR(64)     NOT NULL                    COMMENT '订单号',
    transaction_no              VARCHAR(128)    DEFAULT NULL                COMMENT '支付流水号（外部第三方）',
    internal_transaction_no     VARCHAR(128)    NOT NULL                    COMMENT '内部支付流水号（系统生成）',
    
    -- 用户信息（冗余字段，避免联表查询用户表）
    user_id                     BIGINT          NOT NULL                    COMMENT '用户ID',
    user_name                   VARCHAR(64)     NOT NULL                    COMMENT '用户名称（冗余字段）',
    user_phone                  VARCHAR(20)     DEFAULT NULL                COMMENT '用户手机号（冗余字段）',
    user_email                  VARCHAR(128)    DEFAULT NULL                COMMENT '用户邮箱（冗余字段）',
    
    -- 订单信息（冗余字段，避免联表查询订单表）
    order_title                 VARCHAR(255)    NOT NULL                    COMMENT '订单标题（冗余字段）',
    product_name                VARCHAR(255)    DEFAULT NULL                COMMENT '商品名称（冗余字段）',
    product_type                VARCHAR(50)     DEFAULT NULL                COMMENT '商品类型（冗余字段）',
    merchant_id                 BIGINT          DEFAULT NULL                COMMENT '商户ID（冗余字段）',
    merchant_name               VARCHAR(128)    DEFAULT NULL                COMMENT '商户名称（冗余字段）',
    
    -- 金额信息
    pay_amount                  DECIMAL(15,2)   NOT NULL                    COMMENT '支付金额（元）',
    actual_pay_amount           DECIMAL(15,2)   DEFAULT NULL                COMMENT '实际支付金额（元）',
    discount_amount             DECIMAL(15,2)   DEFAULT 0.00                COMMENT '优惠金额（元）',
    currency_code               VARCHAR(10)     NOT NULL DEFAULT 'CNY'      COMMENT '货币代码',
    
    -- 支付信息
    pay_type                    VARCHAR(20)     NOT NULL                    COMMENT '支付方式：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，TEST-测试',
    pay_status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING'  COMMENT '支付状态：PENDING-待支付，SUCCESS-成功，FAILED-失败，CANCELLED-已取消，REFUNDING-退款中，REFUNDED-已退款',
    pay_channel                 VARCHAR(50)     DEFAULT NULL                COMMENT '支付渠道详细信息',
    pay_scene                   VARCHAR(20)     DEFAULT 'WEB'               COMMENT '支付场景：WEB-网页，MOBILE-手机，APP-应用内，MINI-小程序',
    pay_method                  VARCHAR(50)     DEFAULT NULL                COMMENT '具体支付方式（如：余额支付、信用卡等）',
    
    -- 时间信息
    pay_time                    DATETIME        DEFAULT NULL                COMMENT '支付发起时间',
    complete_time               DATETIME        DEFAULT NULL                COMMENT '支付完成时间',
    expire_time                 DATETIME        NOT NULL                    COMMENT '支付过期时间',
    cancel_time                 DATETIME        DEFAULT NULL                COMMENT '取消时间',
    
    -- 网络信息
    client_ip                   VARCHAR(45)     DEFAULT NULL                COMMENT '客户端IP地址',
    user_agent                  VARCHAR(512)    DEFAULT NULL                COMMENT '用户代理信息',
    device_info                 VARCHAR(255)    DEFAULT NULL                COMMENT '设备信息',
    
    -- 回调通知信息
    notify_url                  VARCHAR(512)    DEFAULT NULL                COMMENT '异步回调通知URL',
    return_url                  VARCHAR(512)    DEFAULT NULL                COMMENT '同步返回URL',
    notify_status               VARCHAR(20)     NOT NULL DEFAULT 'PENDING'  COMMENT '回调状态：PENDING-未回调，SUCCESS-成功，FAILED-失败',
    notify_count                INT             NOT NULL DEFAULT 0          COMMENT '回调重试次数',
    last_notify_time            DATETIME        DEFAULT NULL                COMMENT '最后回调时间',
    max_notify_count            INT             NOT NULL DEFAULT 5          COMMENT '最大回调次数',
    
    -- 退款信息
    refund_amount               DECIMAL(15,2)   DEFAULT 0.00                COMMENT '已退款金额（元）',
    refund_reason               VARCHAR(255)    DEFAULT NULL                COMMENT '退款原因',
    refund_status               VARCHAR(20)     DEFAULT NULL                COMMENT '退款状态：PENDING-退款中，SUCCESS-退款成功，FAILED-退款失败',
    refund_time                 DATETIME        DEFAULT NULL                COMMENT '退款时间',
    
    -- 失败信息
    failure_code                VARCHAR(50)     DEFAULT NULL                COMMENT '失败错误码',
    failure_reason              VARCHAR(255)    DEFAULT NULL                COMMENT '支付失败原因',
    failure_time                DATETIME        DEFAULT NULL                COMMENT '失败时间',
    
    -- 风控信息
    risk_level                  VARCHAR(20)     DEFAULT 'LOW'               COMMENT '风险等级：LOW-低风险，MEDIUM-中风险，HIGH-高风险',
    risk_score                  INT             DEFAULT 0                   COMMENT '风险评分（0-100）',
    is_blocked                  TINYINT(1)      NOT NULL DEFAULT 0          COMMENT '是否被风控拦截：0-否，1-是',
    
    -- 扩展信息
    extra_data                  JSON            DEFAULT NULL                COMMENT '扩展信息（JSON格式）',
    third_party_data            JSON            DEFAULT NULL                COMMENT '第三方支付平台返回数据',
    business_data               JSON            DEFAULT NULL                COMMENT '业务相关数据',
    remark                      VARCHAR(500)    DEFAULT NULL                COMMENT '备注信息',
    
    -- 系统字段
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                 COMMENT '创建时间',
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted                     TINYINT         NOT NULL DEFAULT 0          COMMENT '逻辑删除标识：0-未删除，1-已删除',
    version                     INT             NOT NULL DEFAULT 0          COMMENT '版本号（乐观锁）',
    
    -- 主键和索引
    PRIMARY KEY (id),
    UNIQUE KEY uk_internal_transaction_no (internal_transaction_no),
    UNIQUE KEY uk_order_no_user_id (order_no, user_id),
    KEY idx_order_no (order_no),
    KEY idx_transaction_no (transaction_no),
    KEY idx_user_id (user_id),
    KEY idx_user_name (user_name),
    KEY idx_user_phone (user_phone),
    KEY idx_pay_type (pay_type),
    KEY idx_pay_status (pay_status),
    KEY idx_pay_scene (pay_scene),
    KEY idx_merchant_id (merchant_id),
    KEY idx_notify_status (notify_status),
    KEY idx_refund_status (refund_status),
    KEY idx_risk_level (risk_level),
    KEY idx_create_time (create_time),
    KEY idx_update_time (update_time),
    KEY idx_pay_time (pay_time),
    KEY idx_complete_time (complete_time),
    KEY idx_expire_time (expire_time),
    
    -- 复合索引（优化查询性能）
    KEY idx_user_pay_status_time (user_id, pay_status, create_time),
    KEY idx_pay_type_status_time (pay_type, pay_status, create_time),
    KEY idx_merchant_status_time (merchant_id, pay_status, create_time),
    KEY idx_status_notify_time (pay_status, notify_status, last_notify_time),
    KEY idx_phone_status_time (user_phone, pay_status, create_time)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表（去连表设计）';

-- =============================================
-- 支付回调记录表（去连表设计）
-- =============================================
DROP TABLE IF EXISTS t_payment_callback;
CREATE TABLE t_payment_callback (
    -- 基础信息
    id                          BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '主键ID',
    payment_record_id           BIGINT          NOT NULL                    COMMENT '支付记录ID（不使用外键约束）',
    
    -- 冗余支付信息（避免联表查询支付表）
    order_no                    VARCHAR(64)     NOT NULL                    COMMENT '订单号（冗余字段）',
    transaction_no              VARCHAR(128)    DEFAULT NULL                COMMENT '支付流水号（冗余字段）',
    internal_transaction_no     VARCHAR(128)    NOT NULL                    COMMENT '内部支付流水号（冗余字段）',
    user_id                     BIGINT          NOT NULL                    COMMENT '用户ID（冗余字段）',
    user_name                   VARCHAR(64)     NOT NULL                    COMMENT '用户名称（冗余字段）',
    pay_amount                  DECIMAL(15,2)   NOT NULL                    COMMENT '支付金额（冗余字段）',
    pay_type                    VARCHAR(20)     NOT NULL                    COMMENT '支付方式（冗余字段）',
    
    -- 回调信息
    callback_type               VARCHAR(20)     NOT NULL                    COMMENT '回调类型：PAYMENT-支付回调，REFUND-退款回调，CANCEL-取消回调',
    callback_source             VARCHAR(20)     NOT NULL                    COMMENT '回调来源：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，TEST-测试',
    callback_status             VARCHAR(20)     NOT NULL DEFAULT 'PENDING'  COMMENT '回调处理状态：SUCCESS-成功，FAILED-失败，PENDING-处理中',
    callback_result             VARCHAR(20)     DEFAULT NULL                COMMENT '回调业务结果：SUCCESS-业务成功，FAILED-业务失败',
    
    -- 回调数据
    callback_content            TEXT            DEFAULT NULL                COMMENT '回调原始内容数据',
    callback_signature          VARCHAR(512)    DEFAULT NULL                COMMENT '回调签名信息',
    signature_valid             TINYINT(1)      DEFAULT NULL                COMMENT '签名验证结果：0-失败，1-成功，NULL-未验证',
    callback_params             JSON            DEFAULT NULL                COMMENT '回调参数（JSON格式）',
    
    -- 处理信息
    process_result              VARCHAR(20)     DEFAULT NULL                COMMENT '处理结果状态',
    process_message             VARCHAR(500)    DEFAULT NULL                COMMENT '处理结果消息',
    error_code                  VARCHAR(50)     DEFAULT NULL                COMMENT '错误代码',
    error_message               VARCHAR(500)    DEFAULT NULL                COMMENT '错误信息详情',
    
    -- 网络信息
    client_ip                   VARCHAR(45)     DEFAULT NULL                COMMENT '回调请求IP地址',
    user_agent                  VARCHAR(500)    DEFAULT NULL                COMMENT '用户代理信息',
    request_headers             JSON            DEFAULT NULL                COMMENT '请求头信息（JSON格式）',
    
    -- 性能信息
    process_start_time          DATETIME        DEFAULT NULL                COMMENT '处理开始时间',
    process_end_time            DATETIME        DEFAULT NULL                COMMENT '处理结束时间',
    process_time_ms             BIGINT          DEFAULT NULL                COMMENT '处理耗时（毫秒）',
    
    -- 重试信息
    retry_count                 INT             NOT NULL DEFAULT 0          COMMENT '重试次数',
    max_retry_count             INT             NOT NULL DEFAULT 3          COMMENT '最大重试次数',
    next_retry_time             DATETIME        DEFAULT NULL                COMMENT '下次重试时间',
    
    -- 系统字段
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                 COMMENT '创建时间',
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted                     TINYINT         NOT NULL DEFAULT 0          COMMENT '逻辑删除标识：0-未删除，1-已删除',
    
    -- 主键和索引
    PRIMARY KEY (id),
    KEY idx_payment_record_id (payment_record_id),
    KEY idx_order_no (order_no),
    KEY idx_transaction_no (transaction_no),
    KEY idx_internal_transaction_no (internal_transaction_no),
    KEY idx_user_id (user_id),
    KEY idx_callback_type (callback_type),
    KEY idx_callback_source (callback_source),
    KEY idx_callback_status (callback_status),
    KEY idx_callback_result (callback_result),
    KEY idx_signature_valid (signature_valid),
    KEY idx_process_result (process_result),
    KEY idx_retry_count (retry_count),
    KEY idx_create_time (create_time),
    KEY idx_update_time (update_time),
    KEY idx_process_start_time (process_start_time),
    KEY idx_next_retry_time (next_retry_time),
    
    -- 复合索引（优化查询性能）
    KEY idx_order_type_status (order_no, callback_type, callback_status),
    KEY idx_source_status_time (callback_source, callback_status, create_time),
    KEY idx_user_type_status (user_id, callback_type, callback_status),
    KEY idx_payment_type_result (payment_record_id, callback_type, callback_result),
    KEY idx_status_retry_time (callback_status, retry_count, next_retry_time)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付回调记录表（去连表设计）';

-- =============================================
-- 支付配置表
-- =============================================
DROP TABLE IF EXISTS t_payment_config;
CREATE TABLE t_payment_config (
    -- 基础信息
    id                          BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '主键ID',
    config_key                  VARCHAR(100)    NOT NULL                    COMMENT '配置键名',
    config_value                TEXT            NOT NULL                    COMMENT '配置值内容',
    config_type                 VARCHAR(20)     NOT NULL                    COMMENT '配置类型：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，SYSTEM-系统',
    config_group                VARCHAR(50)     DEFAULT 'DEFAULT'           COMMENT '配置分组',
    config_desc                 VARCHAR(255)    DEFAULT NULL                COMMENT '配置描述信息',
    
    -- 配置属性
    is_encrypted                TINYINT(1)      NOT NULL DEFAULT 0          COMMENT '是否加密存储：0-否，1-是',
    is_enabled                  TINYINT(1)      NOT NULL DEFAULT 1          COMMENT '是否启用：0-否，1-是',
    is_readonly                 TINYINT(1)      NOT NULL DEFAULT 0          COMMENT '是否只读：0-否，1-是',
    priority                    INT             NOT NULL DEFAULT 0          COMMENT '优先级（数值越大优先级越高）',
    
    -- 环境信息
    env_profile                 VARCHAR(20)     DEFAULT 'prod'              COMMENT '环境标识：dev-开发，test-测试，prod-生产',
    valid_from                  DATETIME        DEFAULT NULL                COMMENT '生效开始时间',
    valid_to                    DATETIME        DEFAULT NULL                COMMENT '生效结束时间',
    
    -- 版本信息
    config_version              VARCHAR(20)     DEFAULT '1.0'               COMMENT '配置版本号',
    last_modified_by            VARCHAR(64)     DEFAULT NULL                COMMENT '最后修改人',
    
    -- 系统字段
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                 COMMENT '创建时间',
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted                     TINYINT         NOT NULL DEFAULT 0          COMMENT '逻辑删除标识：0-未删除，1-已删除',
    
    -- 主键和索引
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key_type_env (config_key, config_type, env_profile),
    KEY idx_config_key (config_key),
    KEY idx_config_type (config_type),
    KEY idx_config_group (config_group),
    KEY idx_is_enabled (is_enabled),
    KEY idx_env_profile (env_profile),
    KEY idx_priority (priority),
    KEY idx_valid_time (valid_from, valid_to),
    KEY idx_config_version (config_version)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付配置表';

-- =============================================
-- 支付统计表（去连表设计）
-- =============================================
DROP TABLE IF EXISTS t_payment_statistics;
CREATE TABLE t_payment_statistics (
    -- 基础信息
    id                          BIGINT          NOT NULL AUTO_INCREMENT     COMMENT '主键ID',
    stat_date                   DATE            NOT NULL                    COMMENT '统计日期',
    stat_hour                   TINYINT         DEFAULT NULL                COMMENT '统计小时（0-23，NULL表示日统计）',
    
    -- 维度信息（冗余字段，避免联表统计）
    pay_type                    VARCHAR(20)     NOT NULL                    COMMENT '支付方式',
    pay_scene                   VARCHAR(20)     DEFAULT NULL                COMMENT '支付场景',
    merchant_id                 BIGINT          DEFAULT NULL                COMMENT '商户ID（冗余字段）',
    merchant_name               VARCHAR(128)    DEFAULT NULL                COMMENT '商户名称（冗余字段）',
    user_type                   VARCHAR(20)     DEFAULT NULL                COMMENT '用户类型（冗余字段）',
    product_type                VARCHAR(50)     DEFAULT NULL                COMMENT '商品类型（冗余字段）',
    
    -- 数量统计
    total_count                 INT             NOT NULL DEFAULT 0          COMMENT '总支付笔数',
    success_count               INT             NOT NULL DEFAULT 0          COMMENT '成功支付笔数',
    failed_count                INT             NOT NULL DEFAULT 0          COMMENT '失败支付笔数',
    cancelled_count             INT             NOT NULL DEFAULT 0          COMMENT '取消支付笔数',
    pending_count               INT             NOT NULL DEFAULT 0          COMMENT '待支付笔数',
    refund_count                INT             NOT NULL DEFAULT 0          COMMENT '退款笔数',
    
    -- 金额统计
    total_amount                DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '总支付金额（元）',
    success_amount              DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '成功支付金额（元）',
    failed_amount               DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '失败支付金额（元）',
    cancelled_amount            DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '取消支付金额（元）',
    pending_amount              DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '待支付金额（元）',
    refund_amount               DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '退款金额（元）',
    discount_amount             DECIMAL(15,2)   NOT NULL DEFAULT 0.00       COMMENT '优惠金额（元）',
    
    -- 平均值统计
    avg_pay_amount              DECIMAL(15,2)   DEFAULT NULL                COMMENT '平均支付金额（元）',
    avg_process_time_ms         BIGINT          DEFAULT NULL                COMMENT '平均处理时长（毫秒）',
    
    -- 成功率统计
    success_rate                DECIMAL(5,4)    DEFAULT NULL                COMMENT '支付成功率（0-1）',
    failure_rate                DECIMAL(5,4)    DEFAULT NULL                COMMENT '支付失败率（0-1）',
    
    -- 用户统计（冗余字段）
    unique_user_count           INT             DEFAULT 0                   COMMENT '独立用户数',
    new_user_count              INT             DEFAULT 0                   COMMENT '新用户数',
    
    -- 系统字段
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP                 COMMENT '创建时间',
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 主键和索引
    PRIMARY KEY (id),
    UNIQUE KEY uk_stat_date_hour_type_merchant (stat_date, stat_hour, pay_type, IFNULL(merchant_id, 0)),
    KEY idx_stat_date (stat_date),
    KEY idx_stat_hour (stat_hour),
    KEY idx_pay_type (pay_type),
    KEY idx_pay_scene (pay_scene),
    KEY idx_merchant_id (merchant_id),
    KEY idx_user_type (user_type),
    KEY idx_product_type (product_type),
    KEY idx_success_rate (success_rate),
    KEY idx_total_amount (total_amount),
    KEY idx_create_time (create_time)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付统计表（去连表设计）';

-- =============================================
-- 插入初始配置数据
-- =============================================
INSERT INTO t_payment_config (config_key, config_value, config_type, config_group, config_desc, is_encrypted, is_enabled, env_profile) VALUES
-- 系统基础配置
('payment.timeout.default',        '1800',     'SYSTEM',   'TIMEOUT',      '默认支付超时时间（秒）',                 0, 1, 'prod'),
('payment.timeout.alipay',         '1800',     'SYSTEM',   'TIMEOUT',      '支付宝支付超时时间（秒）',               0, 1, 'prod'),
('payment.timeout.wechat',         '1800',     'SYSTEM',   'TIMEOUT',      '微信支付超时时间（秒）',                 0, 1, 'prod'),
('payment.retry.max_count',        '3',        'SYSTEM',   'RETRY',        '支付最大重试次数',                       0, 1, 'prod'),
('payment.notify.max_count',       '5',        'SYSTEM',   'NOTIFY',       '回调通知最大重试次数',                   0, 1, 'prod'),
('payment.notify.retry_interval',  '300',      'SYSTEM',   'NOTIFY',       '回调重试间隔时间（秒）',                 0, 1, 'prod'),
('payment.test.enabled',           'false',    'SYSTEM',   'TEST',         '是否启用测试模式',                       0, 1, 'prod'),
('payment.test.enabled',           'true',     'SYSTEM',   'TEST',         '是否启用测试模式',                       0, 1, 'dev'),
('payment.risk.max_daily_amount',  '50000.00', 'SYSTEM',   'RISK',         '单用户单日最大支付金额限制（元）',       0, 1, 'prod'),
('payment.risk.max_single_amount', '10000.00', 'SYSTEM',   'RISK',         '单笔最大支付金额限制（元）',             0, 1, 'prod'),

-- 支付宝配置（生产环境）
('alipay.app_id',               '',             'ALIPAY',   'PROD',         '支付宝应用ID',                          0, 0, 'prod'),
('alipay.private_key',          '',             'ALIPAY',   'PROD',         '支付宝应用私钥',                        1, 0, 'prod'),
('alipay.public_key',           '',             'ALIPAY',   'PROD',         '支付宝公钥',                            1, 0, 'prod'),
('alipay.gateway_url',          'https://openapi.alipay.com/gateway.do', 'ALIPAY', 'PROD', '支付宝网关地址', 0, 0, 'prod'),
('alipay.notify_url',           '',             'ALIPAY',   'PROD',         '支付宝异步通知地址',                    0, 0, 'prod'),
('alipay.return_url',           '',             'ALIPAY',   'PROD',         '支付宝同步返回地址',                    0, 0, 'prod'),

-- 支付宝配置（测试环境）
('alipay.app_id',               '2021000000000000',    'ALIPAY',   'DEV',    '支付宝应用ID（沙箱）',                  0, 1, 'dev'),
('alipay.gateway_url',          'https://openapi.alipaydev.com/gateway.do', 'ALIPAY', 'DEV', '支付宝网关地址（沙箱）', 0, 1, 'dev'),

-- 微信支付配置（生产环境）
('wechat.app_id',               '',             'WECHAT',   'PROD',         '微信应用ID',                            0, 0, 'prod'),
('wechat.mch_id',               '',             'WECHAT',   'PROD',         '微信商户号',                            0, 0, 'prod'),
('wechat.api_key',              '',             'WECHAT',   'PROD',         '微信API密钥',                           1, 0, 'prod'),
('wechat.cert_path',            '',             'WECHAT',   'PROD',         '微信支付证书路径',                      0, 0, 'prod'),
('wechat.notify_url',           '',             'WECHAT',   'PROD',         '微信异步通知地址',                      0, 0, 'prod'),
('wechat.api_url',              'https://api.mch.weixin.qq.com', 'WECHAT', 'PROD', '微信支付API地址', 0, 0, 'prod'),

-- 微信支付配置（测试环境）
('wechat.app_id',               'wx1234567890123456',   'WECHAT',   'DEV',    '微信应用ID（测试）',                    0, 1, 'dev'),
('wechat.mch_id',               '1234567890',           'WECHAT',   'DEV',    '微信商户号（测试）',                    0, 1, 'dev');

-- =============================================
-- 创建视图便于查询（去连表设计）
-- =============================================

-- 支付成功记录视图
CREATE OR REPLACE VIEW v_payment_success AS
SELECT 
    id,
    order_no,
    transaction_no,
    internal_transaction_no,
    user_id,
    user_name,
    user_phone,
    order_title,
    product_name,
    merchant_name,
    pay_amount,
    actual_pay_amount,
    discount_amount,
    pay_type,
    pay_channel,
    pay_scene,
    pay_time,
    complete_time,
    currency_code,
    create_time
FROM t_payment_record 
WHERE pay_status = 'SUCCESS' AND deleted = 0
ORDER BY complete_time DESC;

-- 待处理回调通知视图
CREATE OR REPLACE VIEW v_pending_notifications AS
SELECT 
    id,
    order_no,
    user_name,
    order_title,
    pay_amount,
    pay_type,
    notify_url,
    notify_count,
    max_notify_count,
    last_notify_time,
    complete_time,
    TIMESTAMPDIFF(MINUTE, IFNULL(last_notify_time, complete_time), NOW()) as minutes_since_last_notify
FROM t_payment_record 
WHERE pay_status = 'SUCCESS' 
  AND notify_status != 'SUCCESS' 
  AND notify_count < max_notify_count
  AND notify_url IS NOT NULL
  AND deleted = 0
ORDER BY complete_time ASC;

-- 今日支付统计视图
CREATE OR REPLACE VIEW v_today_payment_summary AS
SELECT 
    pay_type,
    pay_scene,
    COUNT(*) as total_count,
    COUNT(CASE WHEN pay_status = 'SUCCESS' THEN 1 END) as success_count,
    COUNT(CASE WHEN pay_status = 'FAILED' THEN 1 END) as failed_count,
    COUNT(CASE WHEN pay_status = 'PENDING' THEN 1 END) as pending_count,
    SUM(pay_amount) as total_amount,
    SUM(CASE WHEN pay_status = 'SUCCESS' THEN actual_pay_amount ELSE 0 END) as success_amount,
    SUM(discount_amount) as total_discount,
    AVG(CASE WHEN pay_status = 'SUCCESS' THEN actual_pay_amount END) as avg_success_amount,
    ROUND(COUNT(CASE WHEN pay_status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(*), 2) as success_rate
FROM t_payment_record 
WHERE DATE(create_time) = CURDATE() 
  AND deleted = 0
GROUP BY pay_type, pay_scene
ORDER BY total_amount DESC;

-- =============================================
-- 存储过程：生成支付统计数据（去连表设计）
-- =============================================
DELIMITER //

CREATE PROCEDURE sp_generate_payment_statistics(
    IN stat_date DATE,
    IN include_hourly BOOLEAN DEFAULT FALSE
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 删除当天的统计数据
    DELETE FROM t_payment_statistics WHERE stat_date = stat_date;

    -- 生成日统计数据
    INSERT INTO t_payment_statistics (
        stat_date, stat_hour, pay_type, pay_scene, merchant_id, merchant_name, 
        product_type, total_count, success_count, failed_count, cancelled_count, 
        pending_count, refund_count, total_amount, success_amount, failed_amount, 
        cancelled_amount, pending_amount, refund_amount, discount_amount,
        avg_pay_amount, success_rate, failure_rate, unique_user_count
    )
    SELECT 
        stat_date,
        NULL as stat_hour,
        pay_type,
        pay_scene,
        merchant_id,
        merchant_name,
        product_type,
        COUNT(*) as total_count,
        SUM(CASE WHEN pay_status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count,
        SUM(CASE WHEN pay_status = 'FAILED' THEN 1 ELSE 0 END) as failed_count,
        SUM(CASE WHEN pay_status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_count,
        SUM(CASE WHEN pay_status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
        SUM(CASE WHEN pay_status = 'REFUNDED' THEN 1 ELSE 0 END) as refund_count,
        SUM(pay_amount) as total_amount,
        SUM(CASE WHEN pay_status = 'SUCCESS' THEN IFNULL(actual_pay_amount, pay_amount) ELSE 0 END) as success_amount,
        SUM(CASE WHEN pay_status = 'FAILED' THEN pay_amount ELSE 0 END) as failed_amount,
        SUM(CASE WHEN pay_status = 'CANCELLED' THEN pay_amount ELSE 0 END) as cancelled_amount,
        SUM(CASE WHEN pay_status = 'PENDING' THEN pay_amount ELSE 0 END) as pending_amount,
        SUM(CASE WHEN pay_status = 'REFUNDED' THEN IFNULL(refund_amount, 0) ELSE 0 END) as refund_amount,
        SUM(IFNULL(discount_amount, 0)) as discount_amount,
        AVG(pay_amount) as avg_pay_amount,
        ROUND(SUM(CASE WHEN pay_status = 'SUCCESS' THEN 1 ELSE 0 END) / COUNT(*), 4) as success_rate,
        ROUND(SUM(CASE WHEN pay_status = 'FAILED' THEN 1 ELSE 0 END) / COUNT(*), 4) as failure_rate,
        COUNT(DISTINCT user_id) as unique_user_count
    FROM t_payment_record
    WHERE DATE(create_time) = stat_date AND deleted = 0
    GROUP BY pay_type, pay_scene, merchant_id, merchant_name, product_type;

    -- 如果需要生成小时统计数据
    IF include_hourly THEN
        INSERT INTO t_payment_statistics (
            stat_date, stat_hour, pay_type, pay_scene, merchant_id, merchant_name, 
            product_type, total_count, success_count, failed_count, cancelled_count, 
            pending_count, refund_count, total_amount, success_amount, failed_amount, 
            cancelled_amount, pending_amount, refund_amount, discount_amount,
            avg_pay_amount, success_rate, failure_rate, unique_user_count
        )
        SELECT 
            stat_date,
            HOUR(create_time) as stat_hour,
            pay_type,
            pay_scene,
            merchant_id,
            merchant_name,
            product_type,
            COUNT(*) as total_count,
            SUM(CASE WHEN pay_status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count,
            SUM(CASE WHEN pay_status = 'FAILED' THEN 1 ELSE 0 END) as failed_count,
            SUM(CASE WHEN pay_status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_count,
            SUM(CASE WHEN pay_status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
            SUM(CASE WHEN pay_status = 'REFUNDED' THEN 1 ELSE 0 END) as refund_count,
            SUM(pay_amount) as total_amount,
            SUM(CASE WHEN pay_status = 'SUCCESS' THEN IFNULL(actual_pay_amount, pay_amount) ELSE 0 END) as success_amount,
            SUM(CASE WHEN pay_status = 'FAILED' THEN pay_amount ELSE 0 END) as failed_amount,
            SUM(CASE WHEN pay_status = 'CANCELLED' THEN pay_amount ELSE 0 END) as cancelled_amount,
            SUM(CASE WHEN pay_status = 'PENDING' THEN pay_amount ELSE 0 END) as pending_amount,
            SUM(CASE WHEN pay_status = 'REFUNDED' THEN IFNULL(refund_amount, 0) ELSE 0 END) as refund_amount,
            SUM(IFNULL(discount_amount, 0)) as discount_amount,
            AVG(pay_amount) as avg_pay_amount,
            ROUND(SUM(CASE WHEN pay_status = 'SUCCESS' THEN 1 ELSE 0 END) / COUNT(*), 4) as success_rate,
            ROUND(SUM(CASE WHEN pay_status = 'FAILED' THEN 1 ELSE 0 END) / COUNT(*), 4) as failure_rate,
            COUNT(DISTINCT user_id) as unique_user_count
        FROM t_payment_record
        WHERE DATE(create_time) = stat_date AND deleted = 0
        GROUP BY HOUR(create_time), pay_type, pay_scene, merchant_id, merchant_name, product_type;
    END IF;

    COMMIT;
END //

DELIMITER ;

-- =============================================
-- 存储过程：清理过期数据
-- =============================================
DELIMITER //

CREATE PROCEDURE sp_cleanup_expired_data(
    IN days_to_keep INT DEFAULT 365
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- 清理过期的回调记录（保留成功的重要回调）
    DELETE FROM t_payment_callback 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL days_to_keep DAY)
      AND callback_status = 'FAILED'
      AND deleted = 0;

    -- 清理过期的失败支付记录（保留成功的支付记录）
    UPDATE t_payment_record 
    SET deleted = 1 
    WHERE create_time < DATE_SUB(NOW(), INTERVAL days_to_keep DAY)
      AND pay_status IN ('FAILED', 'CANCELLED')
      AND deleted = 0;

    -- 清理过期的统计数据
    DELETE FROM t_payment_statistics 
    WHERE stat_date < DATE_SUB(CURDATE(), INTERVAL (days_to_keep * 2) DAY);

    COMMIT;
END //

DELIMITER ;

-- =============================================
-- 定时任务示例（需要开启事件调度器）
-- =============================================
-- SET GLOBAL event_scheduler = ON;

-- 每日凌晨1点生成前一天的支付统计
-- CREATE EVENT IF NOT EXISTS evt_daily_payment_statistics
-- ON SCHEDULE EVERY 1 DAY
-- STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY + INTERVAL 1 HOUR)
-- DO
--   CALL sp_generate_payment_statistics(DATE_SUB(CURRENT_DATE, INTERVAL 1 DAY), TRUE);

-- 每周日凌晨3点清理过期数据
-- CREATE EVENT IF NOT EXISTS evt_weekly_cleanup
-- ON SCHEDULE EVERY 1 WEEK
-- STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL (7 - WEEKDAY(CURRENT_DATE)) DAY + INTERVAL 3 HOUR)
-- DO
--   CALL sp_cleanup_expired_data(365);

-- =============================================
-- 权限设置示例
-- =============================================
-- 创建支付服务专用用户
-- CREATE USER 'collide_payment'@'%' IDENTIFIED BY 'secure_payment_password_2024';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON collide_payment.* TO 'collide_payment'@'%';
-- GRANT EXECUTE ON PROCEDURE collide_payment.sp_generate_payment_statistics TO 'collide_payment'@'%';
-- GRANT EXECUTE ON PROCEDURE collide_payment.sp_cleanup_expired_data TO 'collide_payment'@'%';
-- FLUSH PRIVILEGES;

-- =============================================
-- 性能优化建议
-- =============================================
-- 1. 根据业务需求，考虑对大表进行分区
-- 2. 定期分析表结构，优化索引
-- 3. 对于历史数据，考虑归档到历史表
-- 4. 监控慢查询，及时优化
-- 5. 定期更新表统计信息：ANALYZE TABLE table_name; 