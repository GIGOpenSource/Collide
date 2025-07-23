-- ==========================================
-- 社交动态表（完全去连表化设计）
-- 通过冗余字段避免所有连表查询，提升查询性能
-- ==========================================

CREATE TABLE IF NOT EXISTS `t_social_post` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动态ID',
    `post_type` VARCHAR(20) NOT NULL COMMENT '动态类型：TEXT-文字，IMAGE-图片，VIDEO-视频，LINK-链接，ARTICLE-文章，AUDIO-音频，POLL-投票，LOCATION-位置',
    `content` TEXT NOT NULL COMMENT '动态内容',
    `media_urls` JSON DEFAULT NULL COMMENT '媒体文件URL列表（图片、视频等）',
    `location` VARCHAR(200) DEFAULT NULL COMMENT '位置信息',
    `longitude` DOUBLE DEFAULT NULL COMMENT '经度',
    `latitude` DOUBLE DEFAULT NULL COMMENT '纬度',
    `topics` JSON DEFAULT NULL COMMENT '话题标签列表',
    `mentioned_user_ids` JSON DEFAULT NULL COMMENT '提及的用户ID列表',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：DRAFT-草稿，PUBLISHED-已发布，HIDDEN-已隐藏，DELETED-已删除，REPORTED-被举报，REVIEWING-审核中，REJECTED-审核不通过',
    `visibility` TINYINT NOT NULL DEFAULT 0 COMMENT '可见性：0-公开，1-仅关注者，2-仅自己',
    `allow_comments` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否允许评论',
    `allow_shares` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否允许转发',
    
    -- 作者信息（冗余字段，避免连接用户表）
    `author_id` BIGINT NOT NULL COMMENT '作者用户ID',
    `author_username` VARCHAR(50) NOT NULL COMMENT '作者用户名（冗余）',
    `author_nickname` VARCHAR(100) DEFAULT NULL COMMENT '作者昵称（冗余）',
    `author_avatar` VARCHAR(500) DEFAULT NULL COMMENT '作者头像URL（冗余）',
    `author_verified` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '作者认证状态（冗余）',
    
    -- 统计信息（冗余字段，避免连接统计表）
    `like_count` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comment_count` BIGINT NOT NULL DEFAULT 0 COMMENT '评论数',
    `share_count` BIGINT NOT NULL DEFAULT 0 COMMENT '转发数',
    `view_count` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览数',
    `favorite_count` BIGINT NOT NULL DEFAULT 0 COMMENT '收藏数',
    `hot_score` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '热度分数（用于热门排序）',
    
    -- 时间信息
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `published_time` DATETIME DEFAULT NULL COMMENT '发布时间',
    
    -- 逻辑删除和版本控制
    `deleted` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '逻辑删除标志',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    
    PRIMARY KEY (`id`),
    
    -- 核心索引（支持各种无连表查询）
    KEY `idx_author_id` (`author_id`),                                    -- 用户时间线查询
    KEY `idx_status` (`status`),                                          -- 状态过滤
    KEY `idx_visibility` (`visibility`),                                  -- 可见性过滤
    KEY `idx_post_type` (`post_type`),                                   -- 动态类型过滤
    KEY `idx_created_time` (`created_time`),                             -- 时间排序
    KEY `idx_published_time` (`published_time`),                         -- 发布时间排序
    KEY `idx_hot_score` (`hot_score`),                                   -- 热度排序
    KEY `idx_location` (`longitude`, `latitude`),                        -- 地理位置查询
    
    -- 复合索引（优化复杂查询）
    KEY `idx_author_status_time` (`author_id`, `status`, `created_time`), -- 用户动态查询
    KEY `idx_status_visibility_hot` (`status`, `visibility`, `hot_score`), -- 热门动态查询
    KEY `idx_status_visibility_time` (`status`, `visibility`, `created_time`), -- 时间线查询
    KEY `idx_author_visibility_time` (`author_id`, `visibility`, `created_time`), -- 个人时间线
    
    -- 全文索引（支持内容搜索）
    FULLTEXT KEY `ft_content` (`content`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态表（去连表化设计）';

-- ==========================================
-- 社交动态互动记录表（单独存储，避免连表）
-- 记录用户对动态的点赞、转发等操作
-- ==========================================

CREATE TABLE IF NOT EXISTS `t_social_post_interaction` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '互动ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `interaction_type` VARCHAR(20) NOT NULL COMMENT '互动类型：LIKE-点赞，DISLIKE-点踩，SHARE-转发，FAVORITE-收藏，REPORT-举报，VIEW-查看',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-取消，1-有效',
    `extra_data` JSON DEFAULT NULL COMMENT '额外数据（如转发评论、举报原因等）',
    
    -- 冗余动态信息（避免连接动态表）
    `post_author_id` BIGINT NOT NULL COMMENT '动态作者ID（冗余）',
    `post_author_nickname` VARCHAR(100) DEFAULT NULL COMMENT '动态作者昵称（冗余）',
    `post_content_preview` VARCHAR(200) DEFAULT NULL COMMENT '动态内容预览（冗余）',
    
    -- 冗余用户信息（避免连接用户表）
    `user_nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称（冗余）',
    `user_avatar` VARCHAR(500) DEFAULT NULL COMMENT '用户头像（冗余）',
    
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user_type` (`post_id`, `user_id`, `interaction_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_post_author_id` (`post_author_id`),
    KEY `idx_interaction_type` (`interaction_type`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_user_type_time` (`user_id`, `interaction_type`, `created_time`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态互动记录表（去连表化设计）';

-- ==========================================
-- 社交动态查看记录表（去重统计专用）
-- 用于统计真实浏览量，避免重复计算
-- ==========================================

CREATE TABLE IF NOT EXISTS `t_social_post_view` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '查看ID',
    `post_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID（未登录用户为NULL）',
    `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `user_agent` VARCHAR(500) DEFAULT NULL COMMENT '用户代理',
    `device_info` VARCHAR(200) DEFAULT NULL COMMENT '设备信息',
    `view_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '查看时间',
    `view_duration` INT DEFAULT 0 COMMENT '查看时长（秒）',
    
    -- 冗余动态信息（避免连表统计）
    `post_author_id` BIGINT NOT NULL COMMENT '动态作者ID（冗余）',
    `post_type` VARCHAR(20) NOT NULL COMMENT '动态类型（冗余）',
    
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_author_id` (`post_author_id`),
    KEY `idx_view_time` (`view_time`),
    KEY `idx_post_user` (`post_id`, `user_id`),
    KEY `idx_ip_time` (`ip_address`, `view_time`),
    KEY `idx_post_time` (`post_id`, `view_time`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社交动态查看记录表（去连表化设计）';

-- ==========================================
-- 热门话题表（独立管理，避免实时统计）
-- 预计算话题热度，避免动态查询时连表统计
-- ==========================================

CREATE TABLE IF NOT EXISTS `t_social_topic` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '话题ID',
    `topic_name` VARCHAR(100) NOT NULL COMMENT '话题名称',
    `topic_description` VARCHAR(500) DEFAULT NULL COMMENT '话题描述',
    `topic_cover` VARCHAR(500) DEFAULT NULL COMMENT '话题封面图',
    
    -- 统计信息（定时更新，避免实时计算）
    `post_count` BIGINT NOT NULL DEFAULT 0 COMMENT '动态数量',
    `participant_count` BIGINT NOT NULL DEFAULT 0 COMMENT '参与人数',
    `today_post_count` BIGINT NOT NULL DEFAULT 0 COMMENT '今日动态数',
    `hot_score` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '热度分数',
    
    -- 推荐信息
    `is_trending` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否趋势话题',
    `is_featured` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否精选话题',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重',
    
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-热门',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `last_active_time` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_topic_name` (`topic_name`),
    KEY `idx_hot_score` (`hot_score`),
    KEY `idx_post_count` (`post_count`),
    KEY `idx_status` (`status`),
    KEY `idx_trending` (`is_trending`, `hot_score`),
    KEY `idx_featured` (`is_featured`, `sort_order`),
    KEY `idx_active_time` (`last_active_time`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='热门话题表（去连表化设计）';

-- ==========================================
-- 用户社交统计表（冗余统计信息）
-- 避免实时统计用户的社交数据
-- ==========================================

CREATE TABLE IF NOT EXISTS `t_user_social_stats` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    
    -- 发布统计
    `post_count` BIGINT NOT NULL DEFAULT 0 COMMENT '发布动态数',
    `post_like_total` BIGINT NOT NULL DEFAULT 0 COMMENT '动态获赞总数',
    `post_view_total` BIGINT NOT NULL DEFAULT 0 COMMENT '动态浏览总数',
    
    -- 互动统计
    `like_given_count` BIGINT NOT NULL DEFAULT 0 COMMENT '点赞他人次数',
    `comment_given_count` BIGINT NOT NULL DEFAULT 0 COMMENT '评论他人次数',
    `share_given_count` BIGINT NOT NULL DEFAULT 0 COMMENT '转发他人次数',
    
    -- 关注统计（从关注服务同步）
    `following_count` INT NOT NULL DEFAULT 0 COMMENT '关注数',
    `follower_count` INT NOT NULL DEFAULT 0 COMMENT '粉丝数',
    
    -- 活跃度统计
    `last_post_time` DATETIME DEFAULT NULL COMMENT '最后发布时间',
    `last_active_time` DATETIME DEFAULT NULL COMMENT '最后活跃时间',
    `active_days` INT NOT NULL DEFAULT 0 COMMENT '活跃天数',
    
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`user_id`),
    KEY `idx_post_count` (`post_count`),
    KEY `idx_post_like_total` (`post_like_total`),
    KEY `idx_follower_count` (`follower_count`),
    KEY `idx_last_post_time` (`last_post_time`),
    KEY `idx_last_active_time` (`last_active_time`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户社交统计表（去连表化设计）'; 