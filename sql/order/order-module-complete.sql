-- ========================================================================
-- Collide Order Module - Complete Database Schema
-- 订单模块 - 完整数据库表结构
-- 
-- 设计原则：
-- 1. 严格字段对齐 - 所有字段与 OrderInfo、OrderContentAssociation 实体类完全一致
-- 2. 去连表化设计 - 避免复杂连表查询，提升查询性能
-- 3. 数据冗余策略 - 在订单表中包含商品信息，在关联表中包含内容信息
-- 4. 乐观锁支持 - 使用 version 字段进行并发控制
-- 5. 逻辑删除支持 - 使用 deleted 字段进行软删除
-- 6. 幂等性支持 - 通过唯一索引和状态控制保证操作幂等性
-- ========================================================================

-- 删除已存在的表
DROP TABLE IF EXISTS `order_content_association`;
DROP TABLE IF EXISTS `order_info`;

-- ========================================================================
-- 1. 订单信息表 (order_info)
-- 与 OrderInfo 实体类完全对齐，支持商品购买和内容直购两种模式
-- ========================================================================
CREATE TABLE `order_info` (
    -- ====== 基础字段 ======
    `id`                BIGINT          NOT NULL                COMMENT '主键ID',
    `order_no`          VARCHAR(64)     NOT NULL                COMMENT '订单号 - 业务唯一标识',
    `user_id`           BIGINT          NOT NULL                COMMENT '用户ID',
    
    -- ====== 订单类型字段 ======
    `order_type`        VARCHAR(20)     NOT NULL                COMMENT '订单类型: GOODS-商品购买, CONTENT-内容直购',
    
    -- ====== 商品信息（商品购买时使用，冗余存储避免连表查询） ======
    `goods_id`          BIGINT                                  COMMENT '商品ID（商品购买时必填）',
    `goods_name`        VARCHAR(200)                            COMMENT '商品名称（冗余存储）',
    `goods_type`        VARCHAR(20)                             COMMENT '商品类型（冗余存储）: COIN-金币类, SUBSCRIPTION-订阅类',
    `goods_price`       DECIMAL(10,2)                           COMMENT '商品价格（冗余存储）',
    
    -- ====== 内容信息（内容直购时使用，冗余存储避免连表查询） ======
    `content_id`        BIGINT                                  COMMENT '内容ID（内容直购时必填）',
    `content_title`     VARCHAR(200)                            COMMENT '内容标题（冗余存储）',
    `content_type`      VARCHAR(20)                             COMMENT '内容类型（冗余存储）: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程',
    `content_price`     DECIMAL(10,2)                           COMMENT '内容价格（冗余存储）',
    
    -- ====== 通用订单字段 ======
    `quantity`          INT             NOT NULL DEFAULT 1      COMMENT '购买数量',
    `total_amount`      DECIMAL(10,2)   NOT NULL                COMMENT '订单总金额',
    
    -- ====== 订单状态字段 ======
    `status`            VARCHAR(20)     NOT NULL DEFAULT 'CREATE' COMMENT '订单状态: CREATE-创建订单, UNPAID-未付款, PAID-已支付, CANCELLED-已取消, REFUNDED-已退款',
    
    -- ====== 支付信息字段 ======
    `pay_type`          VARCHAR(20)                             COMMENT '支付方式: ALIPAY-支付宝, WECHAT-微信, TEST-测试支付',
    `pay_time`          DATETIME                                COMMENT '支付时间',
    `expire_time`       DATETIME                                COMMENT '订单过期时间',
    
    -- ====== 扩展字段 ======
    `remark`            TEXT                                    COMMENT '订单备注',
    
    -- ====== 系统字段 ======
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    `version`           INT             NOT NULL DEFAULT 1      COMMENT '版本号（乐观锁）',
    
    -- ====== 主键和索引 ======
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),                     -- 订单号唯一索引
    KEY `idx_user_id_status` (`user_id`, `status`),            -- 用户订单状态复合索引
    KEY `idx_user_id_create_time` (`user_id`, `create_time`),  -- 用户订单时间索引
    KEY `idx_order_type` (`order_type`),                       -- 订单类型索引
    KEY `idx_goods_id` (`goods_id`),                           -- 商品ID索引
    KEY `idx_content_id` (`content_id`),                       -- 内容ID索引
    KEY `idx_user_order_type` (`user_id`, `order_type`),       -- 用户订单类型复合索引
    KEY `idx_status_create_time` (`status`, `create_time`),    -- 状态时间复合索引
    KEY `idx_pay_time` (`pay_time`),                           -- 支付时间索引
    KEY `idx_expire_time` (`expire_time`),                     -- 过期时间索引
    KEY `idx_create_time` (`create_time`),                     -- 创建时间索引
    KEY `idx_deleted` (`deleted`)                              -- 逻辑删除索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单信息表';

-- ========================================================================
-- 2. 订单内容关联表 (order_content_association)
-- 与 OrderContentAssociation 实体类完全对齐，包含内容和商品冗余信息
-- ========================================================================
CREATE TABLE `order_content_association` (
    -- ====== 基础字段 ======
    `id`                BIGINT          NOT NULL                COMMENT '关联ID - 主键',
    `order_id`          BIGINT          NOT NULL                COMMENT '订单ID',
    `order_no`          VARCHAR(64)     NOT NULL                COMMENT '订单号（冗余存储）',
    `user_id`           BIGINT          NOT NULL                COMMENT '用户ID（冗余存储）',
    
    -- ====== 内容信息（冗余存储，避免连表查询） ======
    `content_id`        BIGINT          NOT NULL                COMMENT '内容ID（关联 collide-content）',
    `content_type`      VARCHAR(20)     NOT NULL                COMMENT '内容类型: VIDEO-视频, ARTICLE-文章, LIVE-直播, COURSE-课程',
    `content_title`     VARCHAR(200)                            COMMENT '内容标题（冗余存储）',
    
    -- ====== 访问权限字段 ======
    `access_type`       VARCHAR(20)     NOT NULL                COMMENT '访问权限类型: PERMANENT-永久访问, TEMPORARY-临时访问, SUBSCRIPTION_BASED-基于订阅',
    `access_start_time` DATETIME        NOT NULL                COMMENT '权限开始时间',
    `access_end_time`   DATETIME                                COMMENT '权限结束时间（null表示永久）',
    `status`            VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE' COMMENT '权限状态: ACTIVE-激活状态, EXPIRED-已过期, REVOKED-已撤销',
    
    -- ====== 商品信息（冗余存储） ======
    `goods_id`          BIGINT          NOT NULL                COMMENT '购买时的商品ID（冗余存储）',
    `goods_type`        VARCHAR(20)     NOT NULL                COMMENT '购买时的商品类型（冗余存储）',
    `consumed_coins`    INT                                     COMMENT '消费的金币数量（如果是金币购买）',
    
    -- ====== 扩展字段 ======
    `remark`            TEXT                                    COMMENT '备注',
    
    -- ====== 系统字段 ======
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    `version`           INT             NOT NULL DEFAULT 1      COMMENT '版本号（乐观锁）',
    
    -- ====== 主键和索引 ======
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_content` (`order_id`, `content_id`),   -- 订单内容唯一索引（防重复）
    KEY `idx_order_no` (`order_no`),                           -- 订单号索引
    KEY `idx_user_content` (`user_id`, `content_id`),          -- 用户内容复合索引
    KEY `idx_user_id_status` (`user_id`, `status`),            -- 用户状态复合索引
    KEY `idx_content_id_status` (`content_id`, `status`),      -- 内容状态复合索引
    KEY `idx_access_end_time` (`access_end_time`),             -- 访问结束时间索引（用于过期检查）
    KEY `idx_access_start_time` (`access_start_time`),         -- 访问开始时间索引
    KEY `idx_goods_id` (`goods_id`),                           -- 商品ID索引
    KEY `idx_create_time` (`create_time`),                     -- 创建时间索引
    KEY `idx_deleted` (`deleted`)                              -- 逻辑删除索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单内容关联表';

-- ========================================================================
-- 3. 初始化数据（可选）
-- ========================================================================

-- 插入测试订单数据（开发环境）
INSERT INTO `order_info` (
    `id`, `order_no`, `user_id`, `order_type`, `goods_id`, `goods_name`, `goods_type`, 
    `goods_price`, `content_id`, `content_title`, `content_type`, `content_price`,
    `quantity`, `total_amount`, `status`, `pay_type`, `pay_time`, `expire_time`, `remark`
) VALUES 
-- 商品购买订单
(1, 'ORDER20240101001', 1001, 'GOODS', 1, '金币充值包-100金币', 'COIN', 9.99, NULL, NULL, NULL, NULL, 1, 9.99, 'PAID', 'ALIPAY', NOW(), NULL, '金币充值订单'),
(2, 'ORDER20240101002', 1002, 'GOODS', 2, '月度会员订阅', 'SUBSCRIPTION', 29.99, NULL, NULL, NULL, NULL, 1, 29.99, 'PAID', 'WECHAT', NOW(), NULL, '会员订阅订单'),
(3, 'ORDER20240101003', 1003, 'GOODS', 3, '高级课程包', 'SUBSCRIPTION', 199.99, NULL, NULL, NULL, NULL, 1, 199.99, 'UNPAID', NULL, NULL, DATE_ADD(NOW(), INTERVAL 30 MINUTE), '待支付商品订单'),

-- 内容直购订单
(4, 'ORDER20240101004', 1004, 'CONTENT', NULL, NULL, NULL, NULL, 101, 'Java高级编程实战课程', 'COURSE', 89.99, 1, 89.99, 'PAID', 'ALIPAY', NOW(), NULL, '课程直购订单'),
(5, 'ORDER20240101005', 1005, 'CONTENT', NULL, NULL, NULL, NULL, 102, 'Spring Boot微服务实战', 'VIDEO', 59.99, 1, 59.99, 'PAID', 'WECHAT', NOW(), NULL, '视频直购订单'),
(6, 'ORDER20240101006', 1006, 'CONTENT', NULL, NULL, NULL, NULL, 103, '前端框架Vue3深度解析', 'ARTICLE', 19.99, 1, 19.99, 'UNPAID', NULL, NULL, DATE_ADD(NOW(), INTERVAL 15 MINUTE), '待支付内容订单');

-- 插入测试订单内容关联数据
INSERT INTO `order_content_association` (
    `id`, `order_id`, `order_no`, `user_id`, `content_id`, `content_type`, 
    `content_title`, `access_type`, `access_start_time`, `access_end_time`, 
    `status`, `goods_id`, `goods_type`, `consumed_coins`
) VALUES 
-- 商品购买产生的内容权限（会员订阅获得课程访问权限）
(1, 2, 'ORDER20240101002', 1002, 201, 'COURSE', '会员专享Java高级编程', 'TEMPORARY', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'ACTIVE', 2, 'SUBSCRIPTION', NULL),
(2, 2, 'ORDER20240101002', 1002, 202, 'VIDEO', '会员专享Spring Boot实战', 'TEMPORARY', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 'ACTIVE', 2, 'SUBSCRIPTION', NULL),

-- 内容直购产生的内容权限（直接购买内容获得永久访问权限）
(3, 4, 'ORDER20240101004', 1004, 101, 'COURSE', 'Java高级编程实战课程', 'PERMANENT', NOW(), NULL, 'ACTIVE', NULL, NULL, NULL),
(4, 5, 'ORDER20240101005', 1005, 102, 'VIDEO', 'Spring Boot微服务实战', 'PERMANENT', NOW(), NULL, 'ACTIVE', NULL, NULL, NULL);

-- ========================================================================
-- 4. 性能优化建议
-- ========================================================================

-- 4.1 分区建议（大数据量时）
-- ALTER TABLE order_info PARTITION BY RANGE (YEAR(create_time)) (
--     PARTITION p2023 VALUES LESS THAN (2024),
--     PARTITION p2024 VALUES LESS THAN (2025),
--     PARTITION p2025 VALUES LESS THAN (2026)
-- );

-- 4.2 索引优化建议
-- 根据实际查询模式添加复合索引：
-- KEY `idx_user_goods_status` (`user_id`, `goods_id`, `status`),     -- 用户商品状态查询
-- KEY `idx_pay_time_status` (`pay_time`, `status`),                  -- 支付时间状态查询

-- 4.3 数据清理建议
-- 定期清理过期的未支付订单：
-- DELETE FROM order_info WHERE status = 'UNPAID' AND expire_time < NOW() AND deleted = 0;
-- UPDATE order_info SET deleted = 1 WHERE status = 'UNPAID' AND expire_time < NOW() AND deleted = 0;

-- ========================================================================
-- 5. 数据一致性检查脚本
-- ========================================================================

-- 检查订单与关联表数据一致性
-- SELECT 
--     oi.order_no,
--     oi.user_id,
--     oi.status as order_status,
--     COUNT(oca.id) as association_count
-- FROM order_info oi
-- LEFT JOIN order_content_association oca ON oi.id = oca.order_id AND oca.deleted = 0
-- WHERE oi.deleted = 0
-- GROUP BY oi.id
-- HAVING COUNT(oca.id) = 0 AND oi.status = 'PAID';  -- 已支付但无关联的订单

-- ========================================================================
-- 说明：
-- 1. 所有字段严格对应 OrderInfo 和 OrderContentAssociation 实体类
-- 2. 采用去连表化设计，冗余存储商品和内容信息
-- 3. 支持两种订单类型：
--    - GOODS: 商品购买（goods_id有值，content_id为空）
--    - CONTENT: 内容直购（content_id有值，goods_id为空）
-- 4. 支持乐观锁并发控制和逻辑删除
-- 5. 索引设计针对常用查询场景优化
-- 6. 支持订单状态机和权限管理
-- 7. 内容直购订单通常提供永久访问权限
-- 8. 商品购买订单可能提供临时或永久权限（取决于商品类型）
-- ======================================================================== 