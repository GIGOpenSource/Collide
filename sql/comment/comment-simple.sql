-- ==========================================
-- 评论模块简洁版 SQL
-- 基于无连表设计原则，保留核心功能
-- ==========================================

USE collide;

-- 评论主表（去连表化设计）
DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `comment_type`          VARCHAR(20)  NOT NULL                COMMENT '评论类型：CONTENT、DYNAMIC',
  `target_id`             BIGINT       NOT NULL                COMMENT '目标对象ID',
  `parent_comment_id`     BIGINT       NOT NULL DEFAULT 0     COMMENT '父评论ID，0表示根评论',
  `content`               TEXT         NOT NULL                COMMENT '评论内容',
  
  -- 用户信息（冗余字段，避免连表）
  `user_id`               BIGINT       NOT NULL                COMMENT '评论用户ID',
  `user_nickname`         VARCHAR(100)                         COMMENT '用户昵称（冗余）',
  `user_avatar`           VARCHAR(500)                         COMMENT '用户头像（冗余）',
  
  -- 回复相关
  `reply_to_user_id`      BIGINT                               COMMENT '回复目标用户ID',
  `reply_to_user_nickname` VARCHAR(100)                        COMMENT '回复目标用户昵称（冗余）',
  `reply_to_user_avatar`   VARCHAR(500)                         COMMENT '回复目标用户头像（冗余）',
  
  -- 状态和统计
  `status`                VARCHAR(20)  NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL、HIDDEN、DELETED',
  `like_count`            INT          NOT NULL DEFAULT 0     COMMENT '点赞数（冗余统计）',
  `reply_count`           INT          NOT NULL DEFAULT 0     COMMENT '回复数（冗余统计）',
  
  `create_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  
  PRIMARY KEY (`id`),
  
  -- =================== 基础复合索引（MySQL 8.0优化） ===================
  -- 目标对象评论查询索引（支持状态过滤和时间排序）
  KEY `idx_target_status_time` (`target_id`, `status`, `create_time` DESC),
  
  -- 用户评论查询索引（支持状态过滤和时间排序）
  KEY `idx_user_status_time` (`user_id`, `status`, `create_time` DESC),
  
  -- 父评论查询索引（支持状态过滤和时间排序）
  KEY `idx_parent_status_time` (`parent_comment_id`, `status`, `create_time` DESC),
  
  -- 回复用户查询索引（支持状态过滤和时间排序）
  KEY `idx_reply_user_status_time` (`reply_to_user_id`, `status`, `create_time` DESC),
  
  -- =================== 热门评论索引 ===================
  -- 热门评论查询索引（支持点赞数和回复数排序）
  KEY `idx_target_status_popularity` (`target_id`, `status`, `like_count` DESC, `reply_count` DESC),
  
  -- =================== 时间查询索引 ===================
  -- 时间范围查询索引（支持按时间查询）
  KEY `idx_create_time_status` (`create_time` DESC, `status`, `comment_type`),
  
  -- 更新时间索引（用于清理操作）
  KEY `idx_update_time_status` (`update_time` DESC, `status`)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论主表';

-- =================== MySQL 8.0 高级索引优化 ===================

-- 全文搜索索引（用于评论内容搜索）
ALTER TABLE `t_comment` ADD FULLTEXT INDEX `idx_content_fulltext` (`content`) WITH PARSER ngram;

-- 函数索引（MySQL 8.0新特性，支持大小写不敏感查询）
ALTER TABLE `t_comment` ADD INDEX `idx_comment_type_lower` ((LOWER(`comment_type`)));
ALTER TABLE `t_comment` ADD INDEX `idx_status_lower` ((LOWER(`status`)));

-- 前缀索引（优化用户昵称查询，节省存储空间）
ALTER TABLE `t_comment` ADD INDEX `idx_user_nickname_prefix` (`user_nickname`(20));
ALTER TABLE `t_comment` ADD INDEX `idx_reply_nickname_prefix` (`reply_to_user_nickname`(20));

-- =================== 性能优化说明 ===================
/*
MySQL 8.0 索引优化特性说明：

1. 降序索引 (DESC)
   - 优化 ORDER BY DESC 查询
   - 减少排序操作，提升查询性能

2. 复合索引优化
   - 考虑查询条件的顺序
   - 支持多字段过滤和排序

3. 全文索引
   - 使用 ngram 解析器支持中文搜索
   - 优化评论内容搜索功能

4. 函数索引
   - 支持 LOWER() 等函数索引
   - 优化大小写不敏感查询

5. 前缀索引
   - 优化字符串字段查询
   - 减少索引存储空间

索引使用建议：
- idx_target_status_time: 用于按目标对象查询评论
- idx_user_status_time: 用于查询用户的评论
- idx_parent_status_time: 用于查询评论的回复
- idx_target_status_popularity: 用于热门评论排序
- idx_content_fulltext: 用于评论内容搜索
*/ 