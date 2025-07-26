-- ========================================================================
-- Collide Goods Module - Complete Database Schema
-- 商品模块 - 完整数据库表结构
-- 
-- 设计原则：
-- 1. 严格字段对齐 - 所有字段与 Goods 实体类完全一致
-- 2. 去连表化设计 - 避免复杂连表查询，提升查询性能
-- 3. 数据冗余策略 - 在商品表中包含所有必要的显示字段
-- 4. 乐观锁支持 - 使用 version 字段进行并发控制
-- 5. 逻辑删除支持 - 使用 deleted 字段进行软删除
-- ========================================================================

-- 删除已存在的表
DROP TABLE IF EXISTS `goods`;

-- 创建商品表
CREATE TABLE `goods` (
    -- ====== 基础字段 ======
    `id`                BIGINT          NOT NULL                COMMENT '商品ID - 主键',
    `name`              VARCHAR(100)    NOT NULL                COMMENT '商品名称',
    `description`       TEXT                                    COMMENT '商品描述',
    `type`              VARCHAR(20)     NOT NULL                COMMENT '商品类型: COIN-金币类, SUBSCRIPTION-订阅类',
    `status`            VARCHAR(20)     NOT NULL DEFAULT 'DRAFT' COMMENT '商品状态: DRAFT-草稿, ON_SALE-销售中, OFF_SALE-下架, SOLD_OUT-售罄, DISABLED-禁用',
    
    -- ====== 价格与库存字段 ======
    `price`             DECIMAL(10,2)   NOT NULL                COMMENT '商品价格（元）',
    `image_url`         VARCHAR(500)                            COMMENT '商品图片URL',
    `stock`             INT                      DEFAULT -1     COMMENT '库存数量（金币类商品使用，-1表示无限库存）',
    `sold_count`        INT             NOT NULL DEFAULT 0      COMMENT '已售数量',
    
    -- ====== 商品特性字段 ======
    `subscription_days` INT                                     COMMENT '订阅周期天数（订阅类商品使用）',
    `coin_amount`       INT                                     COMMENT '金币数量（金币类商品购买后获得的金币数）',
    `recommended`       TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '是否推荐: 0-否, 1-是',
    `hot`               TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '是否热门: 0-否, 1-是',
    
    -- ====== 审计字段 ======
    `create_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `creator_id`        BIGINT                                  COMMENT '创建者ID',
    
    -- ====== 系统控制字段 ======
    `deleted`           TINYINT(1)      NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    `version`           INT             NOT NULL DEFAULT 0      COMMENT '版本号（乐观锁）',
    
    -- ====== 主键约束 ======
    PRIMARY KEY (`id`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci 
  COMMENT='商品表 - 支持金币类和订阅类商品的完整信息存储';

-- ========================================================================
-- 索引设计 - 基于去连表化查询需求优化
-- ========================================================================

-- 商品状态索引（用于查询在售商品）
CREATE INDEX `idx_goods_status` ON `goods` (`status`, `deleted`);

-- 商品类型索引（用于按类型查询）
CREATE INDEX `idx_goods_type` ON `goods` (`type`, `deleted`);

-- 推荐商品索引（用于查询推荐商品）
CREATE INDEX `idx_goods_recommended` ON `goods` (`recommended`, `status`, `deleted`);

-- 热门商品索引（用于查询热门商品）
CREATE INDEX `idx_goods_hot` ON `goods` (`hot`, `status`, `deleted`);

-- 创建者索引（用于查询某个创建者的商品）
CREATE INDEX `idx_goods_creator` ON `goods` (`creator_id`, `deleted`);

-- 价格索引（用于价格排序）
CREATE INDEX `idx_goods_price` ON `goods` (`price`);

-- 销量索引（用于销量排序）
CREATE INDEX `idx_goods_sold_count` ON `goods` (`sold_count`);

-- 创建时间索引（用于时间排序）
CREATE INDEX `idx_goods_create_time` ON `goods` (`create_time`);

-- 复合索引：状态+推荐+热门（用于复杂查询优化）
CREATE INDEX `idx_goods_status_rec_hot` ON `goods` (`status`, `recommended`, `hot`, `deleted`);

-- 复合索引：类型+状态+库存（用于可购买商品查询）
CREATE INDEX `idx_goods_type_status_stock` ON `goods` (`type`, `status`, `stock`, `deleted`);

-- ========================================================================
-- 字段约束和业务规则检查
-- ========================================================================

-- 商品类型约束
ALTER TABLE `goods` ADD CONSTRAINT `chk_goods_type` 
CHECK (`type` IN ('COIN', 'SUBSCRIPTION'));

-- 商品状态约束
ALTER TABLE `goods` ADD CONSTRAINT `chk_goods_status` 
CHECK (`status` IN ('DRAFT', 'ON_SALE', 'OFF_SALE', 'SOLD_OUT', 'DISABLED'));

-- 价格约束（价格必须为正数）
ALTER TABLE `goods` ADD CONSTRAINT `chk_goods_price` 
CHECK (`price` > 0);

-- 已售数量约束（不能为负数）
ALTER TABLE `goods` ADD CONSTRAINT `chk_goods_sold_count` 
CHECK (`sold_count` >= 0);

-- 订阅天数约束（订阅类商品的天数必须为正数）
ALTER TABLE `goods` ADD CONSTRAINT `chk_goods_subscription_days` 
CHECK ((`type` = 'SUBSCRIPTION' AND `subscription_days` > 0) OR (`type` != 'SUBSCRIPTION'));

-- 金币数量约束（金币类商品的金币数必须为正数）
ALTER TABLE `goods` ADD CONSTRAINT `chk_goods_coin_amount` 
CHECK ((`type` = 'COIN' AND `coin_amount` > 0) OR (`type` != 'COIN'));

-- ========================================================================
-- 初始化数据 - 示例商品数据
-- ========================================================================

-- 插入金币类商品示例
INSERT INTO `goods` (
    `id`, `name`, `description`, `type`, `status`, `price`, `image_url`, 
    `stock`, `sold_count`, `coin_amount`, `recommended`, `hot`, `creator_id`
) VALUES 
-- 推荐热门金币包
(1001, '超值金币包', '购买即可获得1000金币，性价比超高！', 'COIN', 'ON_SALE', 9.99, 
 'https://cdn.collide.com/goods/coin-pack-1000.jpg', 1000, 156, 1000, 1, 1, 10001),

-- 普通金币包
(1002, '标准金币包', '日常使用的标准金币包，获得500金币', 'COIN', 'ON_SALE', 4.99, 
 'https://cdn.collide.com/goods/coin-pack-500.jpg', 2000, 89, 500, 0, 0, 10001),

-- 大额金币包
(1003, '豪华金币包', '一次性获得5000金币，大额优惠！', 'COIN', 'ON_SALE', 39.99, 
 'https://cdn.collide.com/goods/coin-pack-5000.jpg', 500, 234, 5000, 1, 0, 10001),

-- 限量金币包
(1004, '限量金币包', '限量发售的特殊金币包，获得2000金币', 'COIN', 'ON_SALE', 19.99, 
 'https://cdn.collide.com/goods/coin-pack-limited.jpg', 100, 45, 2000, 0, 1, 10001);

-- 插入订阅类商品示例
INSERT INTO `goods` (
    `id`, `name`, `description`, `type`, `status`, `price`, `image_url`, 
    `stock`, `sold_count`, `subscription_days`, `recommended`, `hot`, `creator_id`
) VALUES 
-- 月度会员
(2001, 'Collide月度会员', '享受30天会员特权，解锁更多功能', 'SUBSCRIPTION', 'ON_SALE', 19.99, 
 'https://cdn.collide.com/goods/membership-monthly.jpg', -1, 1245, 30, 1, 1, 10001),

-- 季度会员
(2002, 'Collide季度会员', '3个月会员特权，享受更多优惠', 'SUBSCRIPTION', 'ON_SALE', 49.99, 
 'https://cdn.collide.com/goods/membership-quarterly.jpg', -1, 567, 90, 1, 0, 10001),

-- 年度会员
(2003, 'Collide年度会员', '一年会员特权，超值优惠价格', 'SUBSCRIPTION', 'ON_SALE', 179.99, 
 'https://cdn.collide.com/goods/membership-yearly.jpg', -1, 892, 365, 1, 1, 10001),

-- 周体验会员
(2004, 'Collide体验会员', '7天体验会员，感受会员特权', 'SUBSCRIPTION', 'ON_SALE', 6.99, 
 'https://cdn.collide.com/goods/membership-trial.jpg', -1, 445, 7, 0, 0, 10001);

-- ========================================================================
-- 统计视图 - 便于数据分析和监控
-- ========================================================================

-- 商品销售统计视图
CREATE VIEW `v_goods_sales_stats` AS
SELECT 
    `type` AS '商品类型',
    COUNT(*) AS '商品总数',
    SUM(`sold_count`) AS '总销量',
    AVG(`price`) AS '平均价格',
    SUM(`price` * `sold_count`) AS '总销售额'
FROM `goods` 
WHERE `deleted` = 0 
GROUP BY `type`;

-- 热门推荐商品视图
CREATE VIEW `v_goods_featured` AS
SELECT 
    `id`,
    `name`,
    `type`,
    `price`,
    `sold_count`,
    `recommended`,
    `hot`,
    `status`
FROM `goods` 
WHERE `deleted` = 0 
  AND `status` = 'ON_SALE'
  AND (`recommended` = 1 OR `hot` = 1)
ORDER BY `sold_count` DESC, `create_time` DESC;

-- ========================================================================
-- 数据库表结构验证
-- ========================================================================

-- 显示表结构
SHOW CREATE TABLE `goods`;

-- 显示表索引
SHOW INDEX FROM `goods`;

-- 显示表统计信息
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '行数',
    DATA_LENGTH as '数据大小',
    INDEX_LENGTH as '索引大小',
    TABLE_COMMENT as '表注释'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'goods';

-- ========================================================================
-- 数据完整性验证查询
-- ========================================================================

-- 验证商品类型和对应字段的一致性
SELECT 
    '类型一致性检查' as '检查项',
    COUNT(*) as '不一致记录数'
FROM `goods` 
WHERE `deleted` = 0 
  AND (
    (`type` = 'COIN' AND `coin_amount` IS NULL) OR 
    (`type` = 'SUBSCRIPTION' AND `subscription_days` IS NULL)
  );

-- 验证商品状态和库存的一致性
SELECT 
    '库存状态检查' as '检查项',
    COUNT(*) as '异常记录数'
FROM `goods` 
WHERE `deleted` = 0 
  AND `status` = 'SOLD_OUT' 
  AND (`stock` IS NULL OR `stock` > 0);

-- ========================================================================
-- SQL文件执行完成提示
-- ========================================================================
SELECT 'Goods模块数据库表结构创建完成！' as '执行结果';
SELECT CONCAT('商品表记录数：', COUNT(*)) as '数据统计' FROM `goods` WHERE `deleted` = 0; 