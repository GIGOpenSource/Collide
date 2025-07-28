-- ==========================================
-- Collide 项目简洁版数据库脚本
-- 包含所有模块的核心表结构
-- 基于无连表设计原则，遵循KISS原则
-- 服务端口：从9500开始（避免中间件端口冲突）
-- ==========================================

USE collide;

-- ==========================================
-- 1. 用户模块
-- ==========================================

-- 用户统一信息表
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL                COMMENT '用户名',
    `nickname`        VARCHAR(100) NOT NULL                COMMENT '昵称',
    `avatar`          VARCHAR(500)                         COMMENT '头像URL',
    `email`           VARCHAR(100)                         COMMENT '邮箱',
    `phone`           VARCHAR(20)                          COMMENT '手机号',
    `password_hash`   VARCHAR(255) NOT NULL                COMMENT '密码哈希',
    `role`            VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '用户角色：user、blogger、admin、vip',
    `status`          VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '用户状态：active、inactive、suspended、banned',
    `bio`             TEXT                                 COMMENT '个人简介',
    `follower_count`  BIGINT       NOT NULL DEFAULT 0     COMMENT '粉丝数',
    `following_count` BIGINT       NOT NULL DEFAULT 0     COMMENT '关注数',
    `content_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '内容数',
    `like_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '获得点赞数',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户统一信息表';

-- ==========================================
-- 2. 分类模块
-- ==========================================

-- 分类主表
DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name`         VARCHAR(100) NOT NULL                COMMENT '分类名称',
  `description`  TEXT                                 COMMENT '分类描述',
  `parent_id`    BIGINT       NOT NULL DEFAULT 0     COMMENT '父分类ID，0表示顶级分类',
  `sort`         INT          NOT NULL DEFAULT 0     COMMENT '排序值',
  `content_count` BIGINT      NOT NULL DEFAULT 0     COMMENT '内容数量（冗余统计）',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_parent` (`name`, `parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类主表';

-- ==========================================
-- 3. 标签模块
-- ==========================================

-- 标签主表
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name`         VARCHAR(100) NOT NULL                 COMMENT '标签名称',
  `tag_type`     VARCHAR(20)  NOT NULL DEFAULT 'content' COMMENT '标签类型：content、interest、system',
  `category_id`  BIGINT                                COMMENT '所属分类ID',
  `usage_count`  BIGINT       NOT NULL DEFAULT 0      COMMENT '使用次数',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`, `tag_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签主表';

-- ==========================================
-- 4. 内容模块
-- ==========================================

-- 内容主表
DROP TABLE IF EXISTS `t_content`;
CREATE TABLE `t_content` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `title`           VARCHAR(200) NOT NULL                COMMENT '内容标题',
    `description`     TEXT                                 COMMENT '内容描述',
    `content_type`    VARCHAR(50)  NOT NULL                COMMENT '内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO',
    `content_data`    LONGTEXT                             COMMENT '内容数据，JSON格式',
    `cover_url`       VARCHAR(500)                         COMMENT '封面图片URL',
    `author_id`       BIGINT       NOT NULL                COMMENT '作者用户ID',
    `author_nickname` VARCHAR(50)                          COMMENT '作者昵称（冗余）',
    `category_id`     BIGINT                               COMMENT '分类ID',
    `category_name`   VARCHAR(100)                         COMMENT '分类名称（冗余）',
    `status`          VARCHAR(50)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT、PUBLISHED、OFFLINE',
    `view_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '查看数',
    `like_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '点赞数',
    `comment_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '评论数',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表';

-- ==========================================
-- 5. 评论模块
-- ==========================================

-- 评论主表
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `comment_type`          VARCHAR(20)  NOT NULL                COMMENT '评论类型：CONTENT、DYNAMIC',
  `target_id`             BIGINT       NOT NULL                COMMENT '目标对象ID',
  `parent_comment_id`     BIGINT       NOT NULL DEFAULT 0     COMMENT '父评论ID，0表示根评论',
  `content`               TEXT         NOT NULL                COMMENT '评论内容',
  `user_id`               BIGINT       NOT NULL                COMMENT '评论用户ID',
  `user_nickname`         VARCHAR(100)                         COMMENT '用户昵称（冗余）',
  `status`                VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL、HIDDEN、DELETED',
  `like_count`            INT          NOT NULL DEFAULT 0     COMMENT '点赞数（冗余统计）',
  `create_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论主表';

-- ==========================================
-- 6. 点赞模块
-- ==========================================

-- 点赞主表
DROP TABLE IF EXISTS `t_like`;
CREATE TABLE `t_like` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `like_type`   VARCHAR(20)  NOT NULL                COMMENT '点赞类型：CONTENT、COMMENT、DYNAMIC',
  `target_id`   BIGINT       NOT NULL                COMMENT '目标对象ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '点赞用户ID',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、cancelled',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `like_type`, `target_id`),
  KEY `idx_target_id` (`target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞主表';

-- ==========================================
-- 7. 关注模块
-- ==========================================

-- 关注关系表
DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` BIGINT       NOT NULL                COMMENT '关注者用户ID',
  `followee_id` BIGINT       NOT NULL                COMMENT '被关注者用户ID',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、cancelled',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followee` (`follower_id`, `followee_id`),
  KEY `idx_follower_id` (`follower_id`),
  KEY `idx_followee_id` (`followee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注关系表';

-- ==========================================
-- 8. 收藏模块
-- ==========================================

-- 收藏主表
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `favorite_type` VARCHAR(20) NOT NULL                COMMENT '收藏类型：CONTENT、GOODS',
  `target_id`   BIGINT       NOT NULL                COMMENT '收藏目标ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '收藏用户ID',
  `status`      VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、cancelled',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `favorite_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏主表';

-- ==========================================
-- 9. 商品模块
-- ==========================================

-- 商品主表
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name`         VARCHAR(200) NOT NULL                COMMENT '商品名称',
  `description`  TEXT                                 COMMENT '商品描述',
  `price`        DECIMAL(10,2) NOT NULL              COMMENT '商品价格',
  `stock`        INT          NOT NULL DEFAULT 0     COMMENT '库存数量',
  `seller_id`    BIGINT       NOT NULL                COMMENT '商家ID',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive、sold_out',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品主表';

-- ==========================================
-- 10. 订单模块
-- ==========================================

-- 订单主表
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no`     VARCHAR(50)  NOT NULL                COMMENT '订单号',
  `user_id`      BIGINT       NOT NULL                COMMENT '用户ID',
  `goods_id`     BIGINT       NOT NULL                COMMENT '商品ID',
  `quantity`     INT          NOT NULL DEFAULT 1     COMMENT '购买数量',
  `total_amount` DECIMAL(10,2) NOT NULL              COMMENT '订单总金额',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '订单状态：pending、paid、completed、cancelled',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- ==========================================
-- 11. 社交动态模块
-- ==========================================

-- 动态主表
DROP TABLE IF EXISTS `t_social_dynamic`;
CREATE TABLE `t_social_dynamic` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `content`      TEXT         NOT NULL                COMMENT '动态内容',
  `dynamic_type` VARCHAR(20)  NOT NULL DEFAULT 'text' COMMENT '动态类型：text、image、video',
  `user_id`      BIGINT       NOT NULL                COMMENT '发布用户ID',
  `user_nickname` VARCHAR(100)                        COMMENT '用户昵称（冗余）',
  `like_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '点赞数（冗余统计）',
  `comment_count` BIGINT      NOT NULL DEFAULT 0     COMMENT '评论数（冗余统计）',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'normal' COMMENT '状态：normal、hidden、deleted',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社交动态主表';

-- ==========================================
-- 12. 搜索模块
-- ==========================================

-- 搜索历史表
DROP TABLE IF EXISTS `t_search_history`;
CREATE TABLE `t_search_history` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '搜索历史ID',
  `user_id`     BIGINT       NOT NULL                COMMENT '用户ID',
  `keyword`     VARCHAR(200) NOT NULL                COMMENT '搜索关键词',
  `search_type` VARCHAR(20)  NOT NULL DEFAULT 'content' COMMENT '搜索类型：content、goods、user',
  `create_time` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- ==========================================
-- 初始化基础数据
-- ==========================================

-- 插入管理员用户
INSERT INTO `t_user` (`username`, `nickname`, `email`, `password_hash`, `role`, `status`) VALUES
('admin', '系统管理员', 'admin@collide.com', '$2a$10$encrypted_password_hash', 'admin', 'active');

-- 插入基础分类
INSERT INTO `t_category` (`name`, `description`, `parent_id`, `sort`, `status`) VALUES
('小说', '文学小说类内容', 0, 1, 'active'),
('漫画', '漫画插画类内容', 0, 2, 'active'),
('视频', '视频影像类内容', 0, 3, 'active'),
('文章', '图文资讯类内容', 0, 4, 'active');

-- 插入基础标签
INSERT INTO `t_tag` (`name`, `tag_type`, `usage_count`, `status`) VALUES
('热门', 'system', 1000, 'active'),
('推荐', 'system', 900, 'active'),
('技术', 'content', 500, 'active'),
('生活', 'content', 400, 'active');

-- ==========================================
-- 脚本执行完成
-- ==========================================
SELECT 'Collide 简洁版数据库脚本执行完成！' AS message; 