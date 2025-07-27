-- ================================================
-- Collide 搜索模块完整 SQL 脚本
-- 去连表化设计，所有搜索基于单表，冗余存储关联信息
-- 版本: v1.0.0
-- 创建时间: 2024-01-01
-- 说明: 搜索模块表结构，支持用户、内容、评论的综合搜索
--      - 完全去连表化设计，所有查询基于冗余存储
--      - 支持搜索记录、统计和建议功能
--      - 内置幂等性保护机制
-- ================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET time_zone = '+08:00';

-- ================================================
-- 1. 搜索历史表（去连表化设计）
-- ================================================

DROP TABLE IF EXISTS `t_search_history`;
CREATE TABLE `t_search_history` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT                                    COMMENT '搜索历史ID',
    `user_id`           BIGINT       DEFAULT NULL                                               COMMENT '用户ID（可为空，支持匿名搜索）',
    
    -- 用户信息（冗余字段，避免与用户表连表）
    `user_nickname`     VARCHAR(100) DEFAULT NULL                                              COMMENT '用户昵称（冗余字段，去连表化设计）',
    `user_avatar`       VARCHAR(500) DEFAULT NULL                                              COMMENT '用户头像URL（冗余字段，去连表化设计）',
    `user_role`         VARCHAR(20)  DEFAULT 'user'                                           COMMENT '用户角色（冗余字段）：user/vip/blogger/admin',
    
    -- 搜索核心信息
    `keyword`           VARCHAR(255) NOT NULL                                                  COMMENT '搜索关键词',
    `search_type`       VARCHAR(20)  NOT NULL DEFAULT 'ALL'                                   COMMENT '搜索类型：ALL-综合搜索, USER-用户搜索, CONTENT-内容搜索, COMMENT-评论搜索',
    `content_type`      VARCHAR(20)  DEFAULT NULL                                              COMMENT '内容类型过滤：NOVEL/COMIC/SHORT_VIDEO/LONG_VIDEO/ARTICLE/AUDIO',
    `result_count`      BIGINT       NOT NULL DEFAULT 0                                        COMMENT '搜索结果数量',
    `search_time`       BIGINT       NOT NULL DEFAULT 0                                        COMMENT '搜索耗时（毫秒）',
    
    -- 设备和环境信息
    `ip_address`        VARCHAR(45)  DEFAULT NULL                                              COMMENT 'IP地址',
    `device_info`       VARCHAR(500) DEFAULT NULL                                              COMMENT '设备信息',
    `user_agent`        VARCHAR(1000) DEFAULT NULL                                             COMMENT '用户代理信息',
    `app_version`       VARCHAR(20)  DEFAULT NULL                                              COMMENT '应用版本',
    
    -- 搜索过滤条件（JSON格式存储）
    `filters`           JSON         DEFAULT NULL                                              COMMENT '搜索过滤条件，JSON格式',
    `sort_type`         VARCHAR(20)  DEFAULT 'RELEVANCE'                                      COMMENT '排序类型：RELEVANCE-相关度，TIME-时间，POPULARITY-热度',
    
    -- 扩展数据
    `extra_data`        JSON         DEFAULT NULL                                              COMMENT '扩展数据',
    
    -- 系统字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    -- 核心查询索引
    KEY `idx_keyword_time` (`keyword`, `create_time`),
    KEY `idx_search_type_time` (`search_type`, `create_time`),
    KEY `idx_content_type_time` (`content_type`, `create_time`),
    -- 用户相关索引
    KEY `idx_user_id_time` (`user_id`, `create_time`),
    KEY `idx_user_keyword` (`user_id`, `keyword`),
    -- 统计分析索引
    KEY `idx_create_time` (`create_time`),
    KEY `idx_result_count` (`result_count`),
    KEY `idx_search_time` (`search_time`),
    -- 复合索引
    KEY `idx_keyword_type_time` (`keyword`, `search_type`, `create_time`),
    KEY `idx_user_type_time` (`user_id`, `search_type`, `create_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表（完全去连表化设计）';

-- ================================================
-- 2. 搜索统计表（去连表化设计）
-- ================================================

DROP TABLE IF EXISTS `t_search_statistics`;
CREATE TABLE `t_search_statistics` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT                                    COMMENT '统计ID',
    `keyword`           VARCHAR(255) NOT NULL                                                  COMMENT '搜索关键词',
    `search_count`      BIGINT       NOT NULL DEFAULT 1                                        COMMENT '搜索总次数',
    `unique_user_count` BIGINT       NOT NULL DEFAULT 1                                        COMMENT '唯一用户搜索数',
    `today_count`       BIGINT       NOT NULL DEFAULT 0                                        COMMENT '今日搜索次数',
    `week_count`        BIGINT       NOT NULL DEFAULT 0                                        COMMENT '本周搜索次数',
    `month_count`       BIGINT       NOT NULL DEFAULT 0                                        COMMENT '本月搜索次数',
    
    -- 搜索结果统计
    `avg_result_count`  BIGINT       NOT NULL DEFAULT 0                                        COMMENT '平均结果数量',
    `avg_search_time`   BIGINT       NOT NULL DEFAULT 0                                        COMMENT '平均搜索耗时（毫秒）',
    `max_result_count`  BIGINT       NOT NULL DEFAULT 0                                        COMMENT '最大结果数量',
    `min_result_count`  BIGINT       NOT NULL DEFAULT 0                                        COMMENT '最小结果数量',
    
    -- 搜索类型分布（冗余统计，避免聚合查询）
    `all_search_count`      BIGINT   NOT NULL DEFAULT 0                                        COMMENT '综合搜索次数',
    `user_search_count`     BIGINT   NOT NULL DEFAULT 0                                        COMMENT '用户搜索次数',
    `content_search_count`  BIGINT   NOT NULL DEFAULT 0                                        COMMENT '内容搜索次数',
    `comment_search_count`  BIGINT   NOT NULL DEFAULT 0                                        COMMENT '评论搜索次数',
    
    -- 热度评分和排名
    `hot_score`         DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '热度评分（基于搜索频次和时间衰减）',
    `rank_score`        DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '排名分数',
    `trend_score`       DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '趋势分数（上升/下降趋势）',
    
    -- 时间统计
    `first_search_time` DATETIME     DEFAULT NULL                                              COMMENT '首次搜索时间',
    `last_search_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                       COMMENT '最后搜索时间',
    `peak_time`         DATETIME     DEFAULT NULL                                              COMMENT '搜索高峰时间',
    
    -- 系统字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_keyword` (`keyword`),
    -- 热度和排名索引
    KEY `idx_hot_score` (`hot_score` DESC),
    KEY `idx_search_count` (`search_count` DESC),
    KEY `idx_unique_user_count` (`unique_user_count` DESC),
    KEY `idx_rank_score` (`rank_score` DESC),
    KEY `idx_trend_score` (`trend_score` DESC),
    -- 时间统计索引
    KEY `idx_today_count` (`today_count` DESC),
    KEY `idx_week_count` (`week_count` DESC),
    KEY `idx_month_count` (`month_count` DESC),
    KEY `idx_last_search_time` (`last_search_time`),
    KEY `idx_create_time` (`create_time`),
    -- 复合索引
    KEY `idx_hot_score_time` (`hot_score` DESC, `last_search_time` DESC),
    KEY `idx_count_score` (`search_count` DESC, `hot_score` DESC),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索统计表（完全去连表化设计）';

-- ================================================
-- 3. 搜索建议表（去连表化设计）
-- ================================================

DROP TABLE IF EXISTS `t_search_suggestion`;
CREATE TABLE `t_search_suggestion` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT                                    COMMENT '建议ID',
    `keyword`           VARCHAR(255) NOT NULL                                                  COMMENT '建议关键词',
    `suggestion_type`   VARCHAR(20)  NOT NULL DEFAULT 'KEYWORD'                               COMMENT '建议类型：KEYWORD-关键词建议, USER-用户建议, TAG-标签建议, CONTENT-内容建议',
    `original_text`     VARCHAR(255) DEFAULT NULL                                              COMMENT '原始文本（用于高亮显示）',
    `highlight_text`    VARCHAR(500) DEFAULT NULL                                              COMMENT '高亮文本（HTML格式）',
    
    -- 关联信息（冗余字段，避免连表查询）
    `target_id`         BIGINT       DEFAULT NULL                                              COMMENT '关联目标ID（用户ID、内容ID等）',
    `target_type`       VARCHAR(20)  DEFAULT NULL                                              COMMENT '关联目标类型：USER/CONTENT/TAG',
    `target_title`      VARCHAR(255) DEFAULT NULL                                              COMMENT '关联目标标题（冗余字段）',
    `target_avatar`     VARCHAR(500) DEFAULT NULL                                              COMMENT '关联目标头像URL（冗余字段）',
    `target_description` TEXT        DEFAULT NULL                                              COMMENT '关联目标描述（冗余字段）',
    
    -- 统计和权重信息
    `search_count`      BIGINT       NOT NULL DEFAULT 0                                        COMMENT '搜索次数',
    `click_count`       BIGINT       NOT NULL DEFAULT 0                                        COMMENT '点击次数',
    `weight`            DOUBLE       NOT NULL DEFAULT 1.0                                      COMMENT '权重（用于排序）',
    `relevance_score`   DOUBLE       NOT NULL DEFAULT 0.0                                      COMMENT '相关度评分',
    `quality_score`     DOUBLE       NOT NULL DEFAULT 0.0                                      COMMENT '质量评分',
    
    -- 配置信息
    `is_manual`         TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否手动配置：0-自动生成，1-手动配置',
    `priority`          INT          NOT NULL DEFAULT 0                                         COMMENT '优先级（数值越大优先级越高）',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'                                 COMMENT '状态：ACTIVE-启用，INACTIVE-禁用，EXPIRED-过期',
    
    -- 分类和标签
    `category`          VARCHAR(50)  DEFAULT NULL                                              COMMENT '分类',
    `tags`              JSON         DEFAULT NULL                                              COMMENT '标签列表',
    `extra_data`        JSON         DEFAULT NULL                                              COMMENT '扩展数据',
    
    -- 时间有效性
    `start_time`        DATETIME     DEFAULT NULL                                              COMMENT '生效开始时间',
    `end_time`          DATETIME     DEFAULT NULL                                              COMMENT '生效结束时间',
    
    -- 系统字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_keyword_type` (`keyword`, `suggestion_type`, `deleted`),
    -- 搜索和过滤索引
    KEY `idx_suggestion_type_status` (`suggestion_type`, `status`),
    KEY `idx_keyword_status` (`keyword`, `status`),
    KEY `idx_target_type_id` (`target_type`, `target_id`),
    -- 排序和统计索引
    KEY `idx_weight_priority` (`weight` DESC, `priority` DESC),
    KEY `idx_search_count` (`search_count` DESC),
    KEY `idx_click_count` (`click_count` DESC),
    KEY `idx_relevance_score` (`relevance_score` DESC),
    KEY `idx_quality_score` (`quality_score` DESC),
    -- 配置管理索引
    KEY `idx_status_priority` (`status`, `priority` DESC),
    KEY `idx_is_manual` (`is_manual`),
    KEY `idx_category` (`category`),
    -- 时间相关索引
    KEY `idx_start_end_time` (`start_time`, `end_time`),
    KEY `idx_create_time` (`create_time`),
    -- 复合索引
    KEY `idx_type_status_weight` (`suggestion_type`, `status`, `weight` DESC),
    KEY `idx_keyword_type_status` (`keyword`, `suggestion_type`, `status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索建议表（完全去连表化设计）';

-- ================================================
-- 4. 搜索用户索引表（去连表化设计）
-- ================================================

DROP TABLE IF EXISTS `t_search_user_index`;
CREATE TABLE `t_search_user_index` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT                                    COMMENT '索引ID',
    `user_id`           BIGINT       NOT NULL                                                   COMMENT '用户ID',
    
    -- 用户基础信息（完全冗余，避免任何连表）
    `username`          VARCHAR(50)  NOT NULL                                                  COMMENT '用户名',
    `nickname`          VARCHAR(100) NOT NULL                                                  COMMENT '用户昵称',
    `avatar`            VARCHAR(500) DEFAULT NULL                                              COMMENT '用户头像URL',
    `bio`               TEXT         DEFAULT NULL                                              COMMENT '个人简介',
    `role`              VARCHAR(20)  NOT NULL DEFAULT 'user'                                  COMMENT '用户角色：user/vip/blogger/admin',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'active'                                COMMENT '用户状态：active/inactive/suspended/banned',
    
    -- 认证信息（冗余字段）
    `is_verified`       TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否认证：0-未认证，1-已认证',
    `blogger_status`    VARCHAR(20)  DEFAULT 'none'                                            COMMENT '博主认证状态：none/applying/approved/rejected',
    `vip_level`         INT          NOT NULL DEFAULT 0                                         COMMENT 'VIP等级：0-普通用户，1-VIP1，2-VIP2',
    `vip_expire_time`   DATETIME     DEFAULT NULL                                              COMMENT 'VIP过期时间',
    
    -- 统计信息（完全冗余，避免聚合查询）
    `follower_count`    BIGINT       NOT NULL DEFAULT 0                                        COMMENT '粉丝数',
    `following_count`   BIGINT       NOT NULL DEFAULT 0                                        COMMENT '关注数',
    `content_count`     BIGINT       NOT NULL DEFAULT 0                                        COMMENT '内容数',
    `like_count`        BIGINT       NOT NULL DEFAULT 0                                        COMMENT '获得点赞数',
    `view_count`        BIGINT       NOT NULL DEFAULT 0                                        COMMENT '被查看数',
    
    -- 活跃度评分
    `activity_score`    DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '活跃度评分',
    `influence_score`   DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '影响力评分',
    `quality_score`     DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '内容质量评分',
    
    -- 地理位置信息
    `location`          VARCHAR(100)  DEFAULT NULL                                             COMMENT '所在地',
    `location_code`     VARCHAR(20)   DEFAULT NULL                                             COMMENT '地区编码',
    
    -- 搜索权重和标签
    `search_weight`     DOUBLE       NOT NULL DEFAULT 1.0                                      COMMENT '搜索权重',
    `search_tags`       JSON         DEFAULT NULL                                              COMMENT '搜索标签，JSON数组格式',
    `keywords`          TEXT         DEFAULT NULL                                              COMMENT '关键词（用于全文搜索）',
    
    -- 时间信息
    `last_active_time`  DATETIME     DEFAULT NULL                                              COMMENT '最后活跃时间',
    `register_time`     DATETIME     DEFAULT NULL                                              COMMENT '注册时间（冗余字段）',
    
    -- 系统字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`, `deleted`),
    -- 全文搜索索引
    FULLTEXT KEY `ft_username_nickname_bio` (`username`, `nickname`, `bio`, `keywords`),
    -- 基础搜索索引
    KEY `idx_username` (`username`),
    KEY `idx_nickname` (`nickname`),
    KEY `idx_role_status` (`role`, `status`),
    KEY `idx_blogger_status` (`blogger_status`),
    KEY `idx_is_verified` (`is_verified`),
    -- 统计排序索引
    KEY `idx_follower_count` (`follower_count` DESC),
    KEY `idx_content_count` (`content_count` DESC),
    KEY `idx_like_count` (`like_count` DESC),
    KEY `idx_activity_score` (`activity_score` DESC),
    KEY `idx_influence_score` (`influence_score` DESC),
    KEY `idx_search_weight` (`search_weight` DESC),
    -- 地理位置索引
    KEY `idx_location` (`location`),
    KEY `idx_location_code` (`location_code`),
    -- 时间索引
    KEY `idx_last_active_time` (`last_active_time`),
    KEY `idx_register_time` (`register_time`),
    KEY `idx_create_time` (`create_time`),
    -- 复合索引
    KEY `idx_status_score` (`status`, `activity_score` DESC),
    KEY `idx_role_status_score` (`role`, `status`, `influence_score` DESC),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索用户索引表（完全去连表化设计）';

-- ================================================
-- 5. 搜索内容索引表（去连表化设计）
-- ================================================

DROP TABLE IF EXISTS `t_search_content_index`;
CREATE TABLE `t_search_content_index` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT                                    COMMENT '索引ID',
    `content_id`        BIGINT       NOT NULL                                                   COMMENT '内容ID',
    
    -- 内容基础信息（完全冗余，避免任何连表）
    `title`             VARCHAR(200) NOT NULL                                                  COMMENT '内容标题',
    `description`       TEXT         DEFAULT NULL                                              COMMENT '内容描述/摘要',
    `content_type`      VARCHAR(50)  NOT NULL                                                  COMMENT '内容类型：NOVEL/COMIC/SHORT_VIDEO/LONG_VIDEO/ARTICLE/AUDIO',
    `cover_url`         VARCHAR(500) DEFAULT NULL                                              COMMENT '封面图片URL',
    `content_text`      LONGTEXT     DEFAULT NULL                                              COMMENT '内容文本（用于全文搜索）',
    
    -- 作者信息（完全冗余，避免连表）
    `author_id`         BIGINT       NOT NULL                                                   COMMENT '作者用户ID',
    `author_nickname`   VARCHAR(100) DEFAULT NULL                                              COMMENT '作者昵称',
    `author_avatar`     VARCHAR(500) DEFAULT NULL                                              COMMENT '作者头像URL',
    `author_role`       VARCHAR(20)  DEFAULT 'user'                                           COMMENT '作者角色：user/vip/blogger/admin',
    `author_verified`   TINYINT      NOT NULL DEFAULT 0                                         COMMENT '作者是否认证：0-未认证，1-已认证',
    
    -- 分类信息（完全冗余，避免连表）
    `category_id`       BIGINT       DEFAULT NULL                                              COMMENT '分类ID',
    `category_name`     VARCHAR(100) DEFAULT NULL                                              COMMENT '分类名称',
    `category_path`     VARCHAR(500) DEFAULT NULL                                              COMMENT '分类路径（如：科技/人工智能/机器学习）',
    
    -- 标签信息（完全冗余）
    `tags`              JSON         DEFAULT NULL                                              COMMENT '标签列表，JSON数组格式',
    `tag_names`         TEXT         DEFAULT NULL                                              COMMENT '标签名称（用于搜索，逗号分隔）',
    
    -- 状态信息
    `status`            VARCHAR(50)  NOT NULL DEFAULT 'PUBLISHED'                             COMMENT '内容状态：DRAFT/PENDING/PUBLISHED/REJECTED/OFFLINE',
    `review_status`     VARCHAR(50)  NOT NULL DEFAULT 'APPROVED'                              COMMENT '审核状态：PENDING/APPROVED/REJECTED',
    `is_top`            TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否置顶：0-否，1-是',
    `is_hot`            TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否热门：0-否，1-是',
    `is_recommended`    TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否推荐：0-否，1-是',
    
    -- 统计信息（完全冗余，避免聚合查询）
    `view_count`        BIGINT       NOT NULL DEFAULT 0                                        COMMENT '查看数',
    `like_count`        BIGINT       NOT NULL DEFAULT 0                                        COMMENT '点赞数',
    `dislike_count`     BIGINT       NOT NULL DEFAULT 0                                        COMMENT '点踩数',
    `comment_count`     BIGINT       NOT NULL DEFAULT 0                                        COMMENT '评论数',
    `share_count`       BIGINT       NOT NULL DEFAULT 0                                        COMMENT '分享数',
    `favorite_count`    BIGINT       NOT NULL DEFAULT 0                                        COMMENT '收藏数',
    
    -- 质量和权重评分
    `quality_score`     DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '内容质量评分',
    `popularity_score`  DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '热度评分',
    `search_weight`     DOUBLE       NOT NULL DEFAULT 1.0                                      COMMENT '搜索权重',
    `relevance_score`   DOUBLE       NOT NULL DEFAULT 0.0                                      COMMENT '相关度评分',
    
    -- 时间信息
    `published_time`    DATETIME     DEFAULT NULL                                              COMMENT '发布时间',
    `last_modified_time` DATETIME    DEFAULT NULL                                              COMMENT '最后修改时间',
    
    -- 搜索优化字段
    `keywords`          TEXT         DEFAULT NULL                                              COMMENT '关键词（用于全文搜索）',
    `search_boost`      DOUBLE       NOT NULL DEFAULT 1.0                                      COMMENT '搜索加权系数',
    
    -- 系统字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_id` (`content_id`, `deleted`),
    -- 全文搜索索引
    FULLTEXT KEY `ft_title_description_content` (`title`, `description`, `content_text`, `keywords`),
    FULLTEXT KEY `ft_title_tags` (`title`, `tag_names`),
    -- 基础搜索索引
    KEY `idx_title` (`title`),
    KEY `idx_content_type` (`content_type`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_author_nickname` (`author_nickname`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_category_name` (`category_name`),
    -- 状态和标记索引
    KEY `idx_status_review` (`status`, `review_status`),
    KEY `idx_is_top_hot_recommended` (`is_top`, `is_hot`, `is_recommended`),
    -- 统计排序索引
    KEY `idx_view_count` (`view_count` DESC),
    KEY `idx_like_count` (`like_count` DESC),
    KEY `idx_comment_count` (`comment_count` DESC),
    KEY `idx_quality_score` (`quality_score` DESC),
    KEY `idx_popularity_score` (`popularity_score` DESC),
    KEY `idx_search_weight` (`search_weight` DESC),
    -- 时间索引
    KEY `idx_published_time` (`published_time`),
    KEY `idx_last_modified_time` (`last_modified_time`),
    KEY `idx_create_time` (`create_time`),
    -- 复合索引
    KEY `idx_type_status_score` (`content_type`, `status`, `popularity_score` DESC),
    KEY `idx_category_status_time` (`category_id`, `status`, `published_time` DESC),
    KEY `idx_author_status_time` (`author_id`, `status`, `published_time` DESC),
    KEY `idx_status_hot_score` (`status`, `is_hot`, `quality_score` DESC),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索内容索引表（完全去连表化设计）';

-- ================================================
-- 6. 搜索评论索引表（去连表化设计）
-- ================================================

DROP TABLE IF EXISTS `t_search_comment_index`;
CREATE TABLE `t_search_comment_index` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT                                    COMMENT '索引ID',
    `comment_id`        BIGINT       NOT NULL                                                   COMMENT '评论ID',
    
    -- 评论基础信息（完全冗余，避免任何连表）
    `content`           TEXT         NOT NULL                                                   COMMENT '评论内容',
    `comment_type`      VARCHAR(20)  NOT NULL                                                   COMMENT '评论类型：CONTENT-内容评论，DYNAMIC-动态评论',
    `target_id`         BIGINT       NOT NULL                                                   COMMENT '目标对象ID（内容ID、动态ID等）',
    `parent_comment_id` BIGINT       NOT NULL DEFAULT 0                                         COMMENT '父评论ID，0表示根评论',
    `root_comment_id`   BIGINT       NOT NULL DEFAULT 0                                         COMMENT '根评论ID，0表示本身就是根评论',
    
    -- 评论用户信息（完全冗余，避免连表）
    `user_id`           BIGINT       NOT NULL                                                   COMMENT '评论用户ID',
    `user_nickname`     VARCHAR(100) DEFAULT NULL                                              COMMENT '用户昵称',
    `user_avatar`       VARCHAR(500) DEFAULT NULL                                              COMMENT '用户头像',
    `user_role`         VARCHAR(20)  DEFAULT 'user'                                           COMMENT '用户角色',
    `user_verified`     TINYINT      DEFAULT 0                                                 COMMENT '用户认证状态：0-未认证，1-已认证',
    
    -- 目标内容信息（完全冗余，避免连表）
    `target_title`      VARCHAR(200) DEFAULT NULL                                              COMMENT '目标内容标题',
    `target_type`       VARCHAR(50)  DEFAULT NULL                                              COMMENT '目标内容类型',
    `target_author_id`  BIGINT       DEFAULT NULL                                              COMMENT '目标内容作者ID',
    `target_author_nickname` VARCHAR(100) DEFAULT NULL                                         COMMENT '目标内容作者昵称',
    
    -- 回复信息（完全冗余）
    `reply_to_user_id`  BIGINT       DEFAULT NULL                                              COMMENT '回复目标用户ID',
    `reply_to_user_nickname` VARCHAR(100) DEFAULT NULL                                         COMMENT '回复目标用户昵称',
    
    -- 状态信息
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'NORMAL'                                 COMMENT '评论状态：NORMAL-正常，HIDDEN-隐藏，DELETED-已删除，PENDING-待审核',
    `audit_status`      VARCHAR(20)  DEFAULT 'PASS'                                            COMMENT '审核状态：PASS-通过，REJECT-拒绝，PENDING-待审核',
    `is_pinned`         TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否置顶：0-否，1-是',
    `is_hot`            TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否热门：0-否，1-是',
    `is_essence`        TINYINT      NOT NULL DEFAULT 0                                         COMMENT '是否精华：0-否，1-是',
    
    -- 统计信息（完全冗余，避免聚合查询）
    `like_count`        INT          NOT NULL DEFAULT 0                                         COMMENT '点赞数',
    `reply_count`       INT          NOT NULL DEFAULT 0                                         COMMENT '回复数',
    `report_count`      INT          NOT NULL DEFAULT 0                                         COMMENT '举报数',
    
    -- 质量评分
    `quality_score`     DECIMAL(3,2) DEFAULT 0.00                                              COMMENT '评论质量分数（0-5.00）',
    `popularity_score`  DECIMAL(10,2) NOT NULL DEFAULT 0.00                                   COMMENT '热度评分',
    `search_weight`     DOUBLE       NOT NULL DEFAULT 1.0                                      COMMENT '搜索权重',
    
    -- 位置和设备信息
    `ip_address`        VARCHAR(50)  DEFAULT NULL                                              COMMENT 'IP地址',
    `location`          VARCHAR(100) DEFAULT NULL                                              COMMENT '地理位置',
    `device_info`       VARCHAR(200) DEFAULT NULL                                              COMMENT '设备信息',
    
    -- 扩展信息
    `mention_user_ids`  JSON         DEFAULT NULL                                              COMMENT '提及的用户ID列表',
    `images`            JSON         DEFAULT NULL                                              COMMENT '评论图片列表',
    `keywords`          TEXT         DEFAULT NULL                                              COMMENT '关键词（用于全文搜索）',
    
    -- 时间信息
    `comment_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '评论时间（冗余字段）',
    
    -- 系统字段
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`           TINYINT      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_comment_id` (`comment_id`, `deleted`),
    -- 全文搜索索引
    FULLTEXT KEY `ft_content_keywords` (`content`, `keywords`),
    -- 基础搜索索引
    KEY `idx_comment_type_target` (`comment_type`, `target_id`),
    KEY `idx_target_id_status` (`target_id`, `status`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_nickname` (`user_nickname`),
    KEY `idx_parent_comment_id` (`parent_comment_id`),
    KEY `idx_root_comment_id` (`root_comment_id`),
    -- 状态和标记索引
    KEY `idx_status_audit` (`status`, `audit_status`),
    KEY `idx_is_pinned_hot_essence` (`is_pinned`, `is_hot`, `is_essence`),
    -- 统计排序索引
    KEY `idx_like_count` (`like_count` DESC),
    KEY `idx_reply_count` (`reply_count` DESC),
    KEY `idx_quality_score` (`quality_score` DESC),
    KEY `idx_popularity_score` (`popularity_score` DESC),
    KEY `idx_search_weight` (`search_weight` DESC),
    -- 时间索引
    KEY `idx_comment_time` (`comment_time`),
    KEY `idx_create_time` (`create_time`),
    -- 复合索引
    KEY `idx_target_status_time` (`target_id`, `status`, `comment_time` DESC),
    KEY `idx_user_status_time` (`user_id`, `status`, `comment_time` DESC),
    KEY `idx_type_status_score` (`comment_type`, `status`, `quality_score` DESC),
    KEY `idx_status_hot_score` (`status`, `is_hot`, `popularity_score` DESC),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索评论索引表（完全去连表化设计）';

-- ================================================
-- 7. 插入初始数据
-- ================================================

-- 插入默认搜索建议
INSERT IGNORE INTO `t_search_suggestion` (
    `keyword`, `suggestion_type`, `search_count`, `weight`, `priority`, 
    `status`, `is_manual`, `create_time`
) VALUES 
('Java', 'KEYWORD', 1000, 10.0, 10, 'ACTIVE', 1, NOW()),
('Spring Boot', 'KEYWORD', 800, 9.0, 9, 'ACTIVE', 1, NOW()),
('微服务', 'KEYWORD', 600, 8.0, 8, 'ACTIVE', 1, NOW()),
('数据库', 'KEYWORD', 500, 7.0, 7, 'ACTIVE', 1, NOW()),
('Redis', 'KEYWORD', 400, 6.0, 6, 'ACTIVE', 1, NOW()),
('MySQL', 'KEYWORD', 350, 5.5, 5, 'ACTIVE', 1, NOW()),
('React', 'KEYWORD', 300, 5.0, 4, 'ACTIVE', 1, NOW()),
('Vue', 'KEYWORD', 250, 4.5, 3, 'ACTIVE', 1, NOW()),
('JavaScript', 'KEYWORD', 200, 4.0, 2, 'ACTIVE', 1, NOW()),
('Python', 'KEYWORD', 150, 3.5, 1, 'ACTIVE', 1, NOW());

-- ================================================
-- 8. 创建存储过程（用于数据同步）
-- ================================================

DELIMITER $$

-- 同步用户索引数据的存储过程
CREATE PROCEDURE `sync_user_search_index`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_user_id BIGINT;
    DECLARE cur CURSOR FOR 
        SELECT id FROM t_user WHERE deleted = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    user_loop: LOOP
        FETCH cur INTO v_user_id;
        IF done THEN
            LEAVE user_loop;
        END IF;
        
        -- 更新或插入用户搜索索引
        INSERT INTO t_search_user_index (
            user_id, username, nickname, avatar, bio, role, status,
            is_verified, blogger_status, vip_level, vip_expire_time,
            follower_count, following_count, content_count, like_count,
            activity_score, influence_score, quality_score,
            location, search_weight, last_active_time, register_time
        )
        SELECT 
            u.id, u.username, u.nickname, u.avatar, u.bio, u.role, u.status,
            CASE WHEN u.blogger_status = 'approved' THEN 1 ELSE 0 END,
            u.blogger_status,
            CASE WHEN u.vip_expire_time > NOW() THEN 1 ELSE 0 END,
            u.vip_expire_time,
            u.follower_count, u.following_count, u.content_count, u.like_count,
            -- 计算活跃度评分
            LEAST(100.0, (u.content_count * 0.3 + u.like_count * 0.2 + u.follower_count * 0.5)),
            -- 计算影响力评分
            LEAST(100.0, (u.follower_count * 0.6 + u.like_count * 0.4)),
            -- 计算质量评分
            CASE WHEN u.content_count > 0 THEN LEAST(100.0, u.like_count / u.content_count * 10) ELSE 0 END,
            u.location,
            CASE 
                WHEN u.role = 'admin' THEN 10.0
                WHEN u.role = 'blogger' THEN 5.0
                WHEN u.role = 'vip' THEN 3.0
                ELSE 1.0
            END,
            u.last_login_time, u.create_time
        FROM t_user u
        WHERE u.id = v_user_id AND u.deleted = 0
        ON DUPLICATE KEY UPDATE
            username = VALUES(username),
            nickname = VALUES(nickname),
            avatar = VALUES(avatar),
            bio = VALUES(bio),
            role = VALUES(role),
            status = VALUES(status),
            is_verified = VALUES(is_verified),
            blogger_status = VALUES(blogger_status),
            vip_level = VALUES(vip_level),
            vip_expire_time = VALUES(vip_expire_time),
            follower_count = VALUES(follower_count),
            following_count = VALUES(following_count),
            content_count = VALUES(content_count),
            like_count = VALUES(like_count),
            activity_score = VALUES(activity_score),
            influence_score = VALUES(influence_score),
            quality_score = VALUES(quality_score),
            location = VALUES(location),
            search_weight = VALUES(search_weight),
            last_active_time = VALUES(last_active_time),
            update_time = NOW();
            
    END LOOP;
    CLOSE cur;
END$$

DELIMITER ;

-- ================================================
-- 9. 设置定时任务（需要开启事件调度器）
-- ================================================

-- 开启事件调度器
-- SET GLOBAL event_scheduler = ON;

-- 创建定时更新搜索统计的事件
-- CREATE EVENT IF NOT EXISTS `update_search_statistics`
-- ON SCHEDULE EVERY 1 HOUR
-- DO
-- BEGIN
--     -- 更新今日搜索统计
--     UPDATE t_search_statistics s
--     SET today_count = (
--         SELECT COUNT(*) FROM t_search_history h 
--         WHERE h.keyword = s.keyword 
--         AND DATE(h.create_time) = CURDATE()
--     );
--     
--     -- 更新热度评分
--     UPDATE t_search_statistics
--     SET hot_score = search_count * 0.6 + today_count * 0.4;
-- END;

SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- 完成标记
-- ================================================
-- 搜索模块完整SQL脚本创建完成
-- 包含完全去连表化设计的6个核心表
-- 支持用户、内容、评论的高效搜索
-- 内置幂等性保护和数据同步机制
-- ================================================ 