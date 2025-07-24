/*
 * Collide 分类和标签系统数据库表结构
 * 包含内容分类、兴趣标签、用户兴趣等相关表
 * 
 * @author GIG Team
 * @version 1.0.0
 */

USE `collide`;

-- ====================================
-- 分类系统表
-- ====================================

-- 内容分类表（支持层级分类）
CREATE TABLE `t_category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父分类ID，0表示根分类',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `description` TEXT COMMENT '分类描述',
  `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '分类图标URL',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '分类封面URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `level` TINYINT NOT NULL DEFAULT 1 COMMENT '分类层级（1-根分类，2-二级分类，以此类推）',
  `path` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '分类路径（用/分隔，如：/1/2/3）',
  `content_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '该分类下的内容数量',
  `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '分类状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`),
  KEY `idx_content_count` (`content_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容分类表';

-- ====================================
-- 标签系统表
-- ====================================

-- 标签表
CREATE TABLE `t_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(100) NOT NULL COMMENT '标签名称',
  `description` TEXT COMMENT '标签描述',
  `color` VARCHAR(20) DEFAULT '#1890ff' COMMENT '标签颜色（十六进制）',
  `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '标签图标URL',
  `tag_type` ENUM('content', 'interest', 'system') NOT NULL DEFAULT 'content' COMMENT '标签类型：内容标签、兴趣标签、系统标签',
  `category_id` BIGINT(20) DEFAULT NULL COMMENT '所属分类ID（可选）',
  `usage_count` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '使用次数',
  `heat_score` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '热度分数',
  `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '标签状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`, `tag_type`),
  KEY `idx_tag_type` (`tag_type`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_usage_count` (`usage_count`),
  KEY `idx_heat_score` (`heat_score`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- ====================================
-- 用户兴趣标签关联表
-- ====================================

-- 用户兴趣标签表
CREATE TABLE `t_user_interest_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
  `interest_score` DECIMAL(5,2) NOT NULL DEFAULT 1.00 COMMENT '兴趣分数（0-5分）',
  `source` ENUM('manual', 'behavior', 'recommendation') NOT NULL DEFAULT 'manual' COMMENT '来源：手动添加、行为分析、推荐算法',
  `status` ENUM('active', 'inactive') NOT NULL DEFAULT 'active' COMMENT '状态',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_interest_score` (`interest_score`),
  KEY `idx_source` (`source`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户兴趣标签表';

-- ====================================
-- 内容标签关联表（用于复杂查询优化）
-- ====================================

-- 内容标签关联表（冗余表，提高查询性能）
CREATE TABLE `t_content_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` BIGINT(20) NOT NULL COMMENT '内容ID',
  `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容标签关联表';

-- 内容分类关联表（冗余表，提高查询性能）
CREATE TABLE `t_content_category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `content_id` BIGINT(20) NOT NULL COMMENT '内容ID',
  `category_id` BIGINT(20) NOT NULL COMMENT '分类ID',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_category` (`content_id`, `category_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容分类关联表';

-- ====================================
-- 插入初始分类数据
-- ====================================

-- 根分类
INSERT INTO `t_category` (`id`, `parent_id`, `name`, `description`, `sort_order`, `level`, `path`) VALUES
(1, 0, '娱乐', '娱乐内容分类', 1, 1, '/1'),
(2, 0, '教育', '教育学习内容', 2, 1, '/2'),
(3, 0, '生活', '生活方式内容', 3, 1, '/3'),
(4, 0, '科技', '科技数码内容', 4, 1, '/4'),
(5, 0, '体育', '体育运动内容', 5, 1, '/5');

-- 娱乐子分类
INSERT INTO `t_category` (`parent_id`, `name`, `description`, `sort_order`, `level`, `path`) VALUES
(1, '音乐', '音乐相关内容', 1, 2, '/1/6'),
(1, '影视', '电影电视剧内容', 2, 2, '/1/7'),
(1, '游戏', '游戏相关内容', 3, 2, '/1/8'),
(1, '搞笑', '搞笑幽默内容', 4, 2, '/1/9'),
(1, '舞蹈', '舞蹈表演内容', 5, 2, '/1/10');

-- 教育子分类
INSERT INTO `t_category` (`parent_id`, `name`, `description`, `sort_order`, `level`, `path`) VALUES
(2, '编程', '编程技术教学', 1, 2, '/2/11'),
(2, '语言学习', '外语学习内容', 2, 2, '/2/12'),
(2, '职场技能', '职场技能培训', 3, 2, '/2/13'),
(2, '学术知识', '学术研究内容', 4, 2, '/2/14');

-- 生活子分类
INSERT INTO `t_category` (`parent_id`, `name`, `description`, `sort_order`, `level`, `path`) VALUES
(3, '美食', '美食制作分享', 1, 2, '/3/15'),
(3, '旅行', '旅行攻略分享', 2, 2, '/3/16'),
(3, '时尚', '时尚穿搭内容', 3, 2, '/3/17'),
(3, '健康', '健康养生内容', 4, 2, '/3/18');

-- ====================================
-- 插入初始标签数据
-- ====================================

-- 内容标签
INSERT INTO `t_tag` (`name`, `description`, `color`, `tag_type`, `category_id`) VALUES
-- 娱乐相关标签
('热门', '热门内容', '#ff4d4f', 'content', 1),
('搞笑', '搞笑幽默', '#faad14', 'content', 1),
('音乐', '音乐相关', '#722ed1', 'content', 1),
('舞蹈', '舞蹈表演', '#eb2f96', 'content', 1),
('游戏', '游戏内容', '#52c41a', 'content', 1),

-- 教育相关标签
('编程', '编程技术', '#1890ff', 'content', 2),
('Java', 'Java编程', '#1890ff', 'content', 2),
('Python', 'Python编程', '#1890ff', 'content', 2),
('前端', '前端开发', '#1890ff', 'content', 2),
('算法', '算法学习', '#1890ff', 'content', 2),

-- 生活相关标签
('美食', '美食制作', '#fa8c16', 'content', 3),
('旅行', '旅行分享', '#13c2c2', 'content', 3),
('健身', '健身运动', '#52c41a', 'content', 3),
('摄影', '摄影技巧', '#722ed1', 'content', 3);

-- 兴趣标签（用户可选择的兴趣）
INSERT INTO `t_tag` (`name`, `description`, `color`, `tag_type`) VALUES
('编程爱好者', '对编程技术感兴趣', '#1890ff', 'interest'),
('音乐发烧友', '热爱音乐', '#722ed1', 'interest'),
('美食达人', '喜欢美食制作', '#fa8c16', 'interest'),
('旅行达人', '热爱旅行', '#13c2c2', 'interest'),
('健身达人', '热爱健身运动', '#52c41a', 'interest'),
('摄影爱好者', '喜欢摄影', '#722ed1', 'interest'),
('游戏玩家', '热爱游戏', '#52c41a', 'interest'),
('学习达人', '热爱学习', '#1890ff', 'interest');

-- 系统标签
INSERT INTO `t_tag` (`name`, `description`, `color`, `tag_type`) VALUES
('推荐', '系统推荐内容', '#ff4d4f', 'system'),
('热门', '平台热门内容', '#faad14', 'system'),
('新人', '新用户内容', '#52c41a', 'system'),
('精选', '编辑精选内容', '#722ed1', 'system');

-- ====================================
-- 更新现有内容表的分类和标签
-- ====================================

-- 为了向后兼容，我们需要确保现有内容表的索引优化
-- 添加全文搜索索引（如果还没有的话）
ALTER TABLE `t_content` ADD FULLTEXT INDEX `ft_title_description` (`title`, `description`);

-- 为 tags 和 categories JSON 字段添加虚拟列索引（MySQL 5.7+）
-- ALTER TABLE `t_content` ADD COLUMN `tag_names` TEXT GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(`tags`, '$[*].name'))) STORED;
-- ALTER TABLE `t_content` ADD COLUMN `category_names` TEXT GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(`categories`, '$[*].name'))) STORED;
-- ALTER TABLE `t_content` ADD INDEX `idx_tag_names` (`tag_names`);
-- ALTER TABLE `t_content` ADD INDEX `idx_category_names` (`category_names`); 