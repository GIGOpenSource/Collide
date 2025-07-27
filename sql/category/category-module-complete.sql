-- =============================================
-- Collide Category 模块完整数据库表结构
-- 严格去连表化设计 - 单表自包含
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `t_category_log`;
DROP TABLE IF EXISTS `t_category_content`;
DROP TABLE IF EXISTS `t_category`;

-- =============================================
-- 主表：分类表 (t_category)
-- 严格去连表化：所有必要信息都在单表内
-- =============================================
CREATE TABLE `t_category` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id`         BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示根分类',
    `name`              VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description`       VARCHAR(500) DEFAULT NULL COMMENT '分类描述',
    `icon_url`          VARCHAR(255) DEFAULT NULL COMMENT '分类图标URL',
    `cover_url`         VARCHAR(255) DEFAULT NULL COMMENT '分类封面URL',
    `sort_order`        INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `level`             TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '分类层级',
    `path`              VARCHAR(500) NOT NULL DEFAULT '/' COMMENT '分类路径',
    `content_count`     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '该分类下的内容数量',
    `status`            VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '分类状态 (ACTIVE-激活, INACTIVE-禁用)',
    `version`           INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `creator_id`        BIGINT UNSIGNED DEFAULT NULL COMMENT '创建者ID',
    `creator_name`      VARCHAR(50) DEFAULT NULL COMMENT '创建者名称（冗余存储，去连表化）',
    `last_modifier_id`  BIGINT UNSIGNED DEFAULT NULL COMMENT '最后修改者ID',
    `last_modifier_name` VARCHAR(50) DEFAULT NULL COMMENT '最后修改者名称（冗余存储）',
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_parent_name` (`parent_id`, `name`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_level` (`level`),
    KEY `idx_content_count` (`content_count`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_sort_order` (`sort_order`, `create_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表 - 严格去连表化设计';

-- =============================================
-- 辅助表：分类内容关联表（可选使用）
-- 记录分类与内容的关联关系
-- 注意：此表为独立设计，不与其他业务表产生连表关系
-- =============================================
CREATE TABLE `t_category_content` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_id`       BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `content_id`        BIGINT UNSIGNED NOT NULL COMMENT '内容ID',
    `content_type`      VARCHAR(50) NOT NULL COMMENT '内容类型 (POST-文章, VIDEO-视频, IMAGE-图片等)',
    `content_title`     VARCHAR(200) DEFAULT NULL COMMENT '内容标题（冗余存储，避免连表）',
    `content_status`    VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '内容状态（冗余存储）',
    `creator_id`        BIGINT UNSIGNED DEFAULT NULL COMMENT '内容创建者ID',
    `creator_name`      VARCHAR(50) DEFAULT NULL COMMENT '内容创建者名称（冗余存储）',
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联创建时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_content` (`category_id`, `content_id`, `content_type`),
    KEY `idx_category_id` (`category_id`, `content_type`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类内容关联表 - 独立设计';

-- =============================================
-- 日志表：分类操作日志表（可选使用）
-- 记录分类的所有操作历史
-- =============================================
CREATE TABLE `t_category_log` (
    `id`                BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `category_id`       BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    `operation_type`    VARCHAR(20) NOT NULL COMMENT '操作类型 (CREATE-创建, UPDATE-更新, DELETE-删除)',
    `old_data`          JSON DEFAULT NULL COMMENT '变更前数据',
    `new_data`          JSON DEFAULT NULL COMMENT '变更后数据',
    `operator_id`       BIGINT UNSIGNED DEFAULT NULL COMMENT '操作人ID',
    `operator_name`     VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名（冗余存储）',
    `operation_reason`  VARCHAR(200) DEFAULT NULL COMMENT '操作原因',
    `client_ip`         VARCHAR(45) DEFAULT NULL COMMENT '操作IP地址',
    `user_agent`        VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类操作日志表';

-- =============================================
-- 初始化数据
-- 创建默认的根分类数据
-- =============================================
INSERT INTO `t_category` (
    `id`, `parent_id`, `name`, `description`, `sort_order`, `level`, `path`, `status`, 
    `creator_name`, `create_time`, `update_time`
) VALUES
(1, 0, '技术', '技术相关内容分类', 1, 1, '/技术', 'ACTIVE', 'system', NOW(), NOW()),
(2, 0, '生活', '生活相关内容分类', 2, 1, '/生活', 'ACTIVE', 'system', NOW(), NOW()),
(3, 0, '娱乐', '娱乐相关内容分类', 3, 1, '/娱乐', 'ACTIVE', 'system', NOW(), NOW()),
(4, 0, '学习', '学习教育相关内容', 4, 1, '/学习', 'ACTIVE', 'system', NOW(), NOW()),
(5, 0, '运动', '运动健身相关内容', 5, 1, '/运动', 'ACTIVE', 'system', NOW(), NOW()),

-- 二级分类 - 技术类
(10, 1, 'Java', 'Java编程语言相关', 1, 2, '/技术/Java', 'ACTIVE', 'system', NOW(), NOW()),
(11, 1, 'Python', 'Python编程语言相关', 2, 2, '/技术/Python', 'ACTIVE', 'system', NOW(), NOW()),
(12, 1, '前端开发', '前端开发技术', 3, 2, '/技术/前端开发', 'ACTIVE', 'system', NOW(), NOW()),
(13, 1, '数据库', '数据库相关技术', 4, 2, '/技术/数据库', 'ACTIVE', 'system', NOW(), NOW()),
(14, 1, '云计算', '云计算相关技术', 5, 2, '/技术/云计算', 'ACTIVE', 'system', NOW(), NOW()),

-- 二级分类 - 生活类
(20, 2, '美食', '美食分享与制作', 1, 2, '/生活/美食', 'ACTIVE', 'system', NOW(), NOW()),
(21, 2, '旅游', '旅游攻略与体验', 2, 2, '/生活/旅游', 'ACTIVE', 'system', NOW(), NOW()),
(22, 2, '家居', '家居装饰与生活', 3, 2, '/生活/家居', 'ACTIVE', 'system', NOW(), NOW()),
(23, 2, '穿搭', '时尚穿搭分享', 4, 2, '/生活/穿搭', 'ACTIVE', 'system', NOW(), NOW()),
(24, 2, '宠物', '宠物饲养与分享', 5, 2, '/生活/宠物', 'ACTIVE', 'system', NOW(), NOW()),

-- 二级分类 - 娱乐类
(30, 3, '电影', '电影推荐与评论', 1, 2, '/娱乐/电影', 'ACTIVE', 'system', NOW(), NOW()),
(31, 3, '音乐', '音乐分享与推荐', 2, 2, '/娱乐/音乐', 'ACTIVE', 'system', NOW(), NOW()),
(32, 3, '游戏', '游戏攻略与分享', 3, 2, '/娱乐/游戏', 'ACTIVE', 'system', NOW(), NOW()),
(33, 3, '动漫', '动漫相关内容', 4, 2, '/娱乐/动漫', 'ACTIVE', 'system', NOW(), NOW()),
(34, 3, '综艺', '综艺节目讨论', 5, 2, '/娱乐/综艺', 'ACTIVE', 'system', NOW(), NOW()),

-- 三级分类示例 - Java技术细分
(100, 10, 'Spring Boot', 'Spring Boot框架相关', 1, 3, '/技术/Java/Spring Boot', 'ACTIVE', 'system', NOW(), NOW()),
(101, 10, 'MyBatis', 'MyBatis持久层框架', 2, 3, '/技术/Java/MyBatis', 'ACTIVE', 'system', NOW(), NOW()),
(102, 10, 'JVM调优', 'Java虚拟机优化', 3, 3, '/技术/Java/JVM调优', 'ACTIVE', 'system', NOW(), NOW()),

-- 三级分类示例 - 前端技术细分
(110, 12, 'Vue.js', 'Vue.js框架相关', 1, 3, '/技术/前端开发/Vue.js', 'ACTIVE', 'system', NOW(), NOW()),
(111, 12, 'React', 'React框架相关', 2, 3, '/技术/前端开发/React', 'ACTIVE', 'system', NOW(), NOW()),
(112, 12, 'TypeScript', 'TypeScript开发', 3, 3, '/技术/前端开发/TypeScript', 'ACTIVE', 'system', NOW(), NOW()),

-- 三级分类示例 - 美食细分
(200, 20, '中式料理', '中式菜谱与技巧', 1, 3, '/生活/美食/中式料理', 'ACTIVE', 'system', NOW(), NOW()),
(201, 20, '西式料理', '西式菜谱与技巧', 2, 3, '/生活/美食/西式料理', 'ACTIVE', 'system', NOW(), NOW()),
(202, 20, '烘焙甜品', '烘焙与甜品制作', 3, 3, '/生活/美食/烘焙甜品', 'ACTIVE', 'system', NOW(), NOW());

-- =============================================
-- 性能优化相关
-- =============================================

-- 分析表结构
ANALYZE TABLE `t_category`;
ANALYZE TABLE `t_category_content`;
ANALYZE TABLE `t_category_log`;

-- =============================================
-- 表结构验证
-- =============================================

-- 验证索引创建
SHOW INDEX FROM `t_category`;

-- 验证表结构
DESCRIBE `t_category`;

-- =============================================
-- 初始化完成日志
-- =============================================
SELECT 
    '===========================================',
    'Category 模块数据库初始化完成',
    '表创建时间：', NOW(),
    '初始化分类数量：', COUNT(*) as category_count,
    '根分类数量：', SUM(CASE WHEN parent_id = 0 THEN 1 ELSE 0 END) as root_count,
    '二级分类数量：', SUM(CASE WHEN level = 2 THEN 1 ELSE 0 END) as level2_count,
    '三级分类数量：', SUM(CASE WHEN level = 3 THEN 1 ELSE 0 END) as level3_count,
    '==========================================='
FROM `t_category`; 