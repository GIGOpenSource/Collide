-- ==========================================
-- 内容模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

-- 内容主表（去连表化设计）
DROP TABLE IF EXISTS `t_content`;
CREATE TABLE `t_content` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `title`           VARCHAR(200) NOT NULL                COMMENT '内容标题',
    `description`     TEXT                                 COMMENT '内容描述',
    `content_type`    VARCHAR(50)  NOT NULL                COMMENT '内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO',
    `content_data`    LONGTEXT                             COMMENT '内容数据，JSON格式',
    `cover_url`       VARCHAR(500)                         COMMENT '封面图片URL',
    `tags`            TEXT                                 COMMENT '标签，JSON数组格式',
    
    -- 作者信息（冗余字段，避免连表）
    `author_id`       BIGINT       NOT NULL                COMMENT '作者用户ID',
    `author_nickname` VARCHAR(50)                          COMMENT '作者昵称（冗余）',
    `author_avatar`   VARCHAR(500)                         COMMENT '作者头像URL（冗余）',
    
    -- 分类信息（冗余字段，避免连表）
    `category_id`     BIGINT                               COMMENT '分类ID',
    `category_name`   VARCHAR(100)                         COMMENT '分类名称（冗余）',
    
    -- 状态相关字段
    `status`          VARCHAR(50)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT、PUBLISHED、OFFLINE',
    `review_status`   VARCHAR(50)  NOT NULL DEFAULT 'PENDING' COMMENT '审核状态：PENDING、APPROVED、REJECTED',
    
    -- 统计字段（冗余存储，避免聚合查询）
    `view_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '查看数',
    `like_count`      BIGINT       NOT NULL DEFAULT 0     COMMENT '点赞数',
    `comment_count`   BIGINT       NOT NULL DEFAULT 0     COMMENT '评论数',
    `favorite_count`  BIGINT       NOT NULL DEFAULT 0     COMMENT '收藏数',
    
    -- 时间字段
    `publish_time`    DATETIME                             COMMENT '发布时间',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容主表';

-- 内容章节表（用于小说、漫画等多章节内容）
DROP TABLE IF EXISTS `t_content_chapter`;
CREATE TABLE `t_content_chapter` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `content_id`   BIGINT       NOT NULL                COMMENT '内容ID',
    `chapter_num`  INT          NOT NULL                COMMENT '章节号',
    `title`        VARCHAR(200) NOT NULL                COMMENT '章节标题',
    `content`      LONGTEXT                             COMMENT '章节内容',
    `word_count`   INT          NOT NULL DEFAULT 0     COMMENT '字数',
    `status`       VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT '状态：DRAFT、PUBLISHED',
    `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_chapter` (`content_id`, `chapter_num`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容章节表'; 