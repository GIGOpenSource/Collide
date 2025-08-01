-- ==========================================
-- Collide 广告系统 - 极简版
-- 只保留核心功能：广告类型 + 图片 + 链接
-- 设计原则：简洁、实用、易维护
-- ==========================================

USE collide;

-- ==========================================
-- 广告表（唯一核心表）
-- ==========================================

-- 广告表（存储广告的核心信息）
DROP TABLE IF EXISTS `t_ad`;
CREATE TABLE `t_ad` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '广告ID',
    `ad_name`         VARCHAR(200) NOT NULL                COMMENT '广告名称',
    `ad_title`        VARCHAR(300) NOT NULL                COMMENT '广告标题',
    `ad_description`  VARCHAR(500)                         COMMENT '广告描述',
    
    -- 广告类型（核心字段）
    `ad_type`         VARCHAR(50)  NOT NULL                COMMENT '广告类型：banner、sidebar、popup、feed',
    
    -- 广告素材（核心字段）
    `image_url`       VARCHAR(1000) NOT NULL               COMMENT '广告图片URL',
    `click_url`       VARCHAR(1000) NOT NULL               COMMENT '点击跳转链接',
    
    -- 可选字段
    `alt_text`        VARCHAR(200)                         COMMENT '图片替代文本',
    `target_type`     VARCHAR(30)  NOT NULL DEFAULT '_blank' COMMENT '链接打开方式：_blank、_self',
    
    -- 状态管理
    `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否启用（1启用，0禁用）',
    `sort_order`      INT          NOT NULL DEFAULT 0      COMMENT '排序权重（数值越大优先级越高）',
    
    -- 时间字段
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_ad_type` (`ad_type`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='广告表';

-- ==========================================
-- 初始化示例数据
-- ==========================================

-- 创建示例广告
INSERT INTO `t_ad` (`ad_name`, `ad_title`, `ad_description`, `ad_type`, `image_url`, `click_url`, `alt_text`, `sort_order`) VALUES
('首页横幅广告', '最新科技产品推广', '体验未来科技，提升工作效率', 'banner', 'https://example.com/images/tech-banner.jpg', 'https://example.com/tech-product', '科技产品广告', 100),
('侧边栏推荐', '在线教育课程', '零基础学编程，30天成为开发者', 'sidebar', 'https://example.com/images/course-sidebar.jpg', 'https://example.com/courses', '编程课程广告', 90),
('信息流广告', '手机APP下载', '新用户注册立享专属优惠', 'feed', 'https://example.com/images/app-feed.jpg', 'https://example.com/app-download', '手机APP广告', 80),
('弹窗广告', '限时优惠活动', '年终大促，全场8折起', 'popup', 'https://example.com/images/sale-popup.jpg', 'https://example.com/sale', '优惠活动广告', 70),
('推荐商品', '精品好物推荐', '品质生活，从选择开始', 'banner', 'https://example.com/images/product-banner.jpg', 'https://example.com/products', '商品推荐广告', 60);

-- ==========================================
-- 常用查询功能（替代存储过程）
-- ==========================================

-- 1. 获取指定类型的所有启用广告（按权重排序）
-- SELECT * FROM t_ad WHERE ad_type = 'banner' AND is_active = 1 ORDER BY sort_order DESC, id ASC;

-- 2. 随机获取指定类型的单个广告
-- SELECT * FROM t_ad WHERE ad_type = 'banner' AND is_active = 1 ORDER BY RAND() LIMIT 1;

-- 3. 随机获取指定类型的多个广告
-- SELECT * FROM t_ad WHERE ad_type = 'feed' AND is_active = 1 ORDER BY RAND() LIMIT 3;

-- 4. 获取所有广告类型
-- SELECT DISTINCT ad_type FROM t_ad WHERE is_active = 1;

-- 5. 按权重获取推荐广告
-- SELECT * FROM t_ad WHERE is_active = 1 ORDER BY sort_order DESC LIMIT 5;

-- ==========================================
-- 功能说明
-- ==========================================

-- 1. 支持的广告类型：
--    - banner: 横幅广告
--    - sidebar: 侧边栏广告
--    - popup: 弹窗广告
--    - feed: 信息流广告

-- 2. 核心功能：
--    - 根据类型查询广告列表
--    - 随机获取单个或多个广告
--    - 启用/禁用广告管理
--    - 权重排序

-- 3. 表字段说明：
--    - id: 广告唯一标识
--    - ad_name: 广告名称
--    - ad_title: 广告标题
--    - ad_description: 广告描述
--    - ad_type: 广告类型（核心字段）
--    - image_url: 广告图片链接（核心字段）
--    - click_url: 点击跳转链接（核心字段）
--    - alt_text: 图片替代文本
--    - target_type: 链接打开方式
--    - is_active: 是否启用
--    - sort_order: 排序权重

-- ==========================================
-- 使用说明
-- ==========================================

-- 这是一个极简的广告系统，只包含一个核心表 t_ad
-- 通过 ad_type 字段来区分不同类型的广告
-- 通过 is_active 字段来控制广告的启用状态
-- 通过 sort_order 字段来控制广告的显示优先级