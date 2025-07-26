-- ==========================================
-- Collide 评论模块完整 SQL 脚本
-- ==========================================
-- 版本: v1.0.0
-- 创建时间: 2024-01-01
-- 说明: 完全去连表化设计，所有查询基于单表，冗余存储关联信息
-- ==========================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET time_zone = '+08:00';

-- ==========================================
-- 评论主表 - 完全去连表化设计
-- ==========================================

DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id`                    bigint       NOT NULL AUTO_INCREMENT                                    COMMENT '评论ID',
  `comment_type`          varchar(20)  NOT NULL                                                   COMMENT '评论类型：CONTENT-内容评论，DYNAMIC-动态评论',
  `target_id`             bigint       NOT NULL                                                   COMMENT '目标对象ID（内容ID、动态ID等）',
  `parent_comment_id`     bigint       NOT NULL DEFAULT 0                                         COMMENT '父评论ID，0表示根评论',
  `root_comment_id`       bigint       NOT NULL DEFAULT 0                                         COMMENT '根评论ID，0表示本身就是根评论',
  `content`               text         NOT NULL                                                   COMMENT '评论内容',
  `user_id`               bigint       NOT NULL                                                   COMMENT '评论用户ID',
  `user_nickname`         varchar(100) DEFAULT NULL                                              COMMENT '用户昵称（冗余，避免连表）',
  `user_avatar`           varchar(500) DEFAULT NULL                                              COMMENT '用户头像（冗余，避免连表）',
  `user_verified`         tinyint      DEFAULT 0                                                 COMMENT '用户认证状态（冗余）：0-未认证，1-已认证',
  `reply_to_user_id`      bigint       DEFAULT NULL                                              COMMENT '回复目标用户ID',
  `reply_to_user_nickname` varchar(100) DEFAULT NULL                                             COMMENT '回复目标用户昵称（冗余）',
  `status`                varchar(20)  NOT NULL DEFAULT 'NORMAL'                                 COMMENT '评论状态：NORMAL-正常，HIDDEN-隐藏，DELETED-已删除，PENDING-待审核',
  `like_count`            int          NOT NULL DEFAULT 0                                         COMMENT '点赞数（冗余统计，避免连表）',
  `reply_count`           int          NOT NULL DEFAULT 0                                         COMMENT '回复数（冗余统计，避免连表）',
  `report_count`          int          NOT NULL DEFAULT 0                                         COMMENT '举报数（冗余统计）',
  `is_pinned`             tinyint      NOT NULL DEFAULT 0                                         COMMENT '是否置顶：0-否，1-是',
  `is_hot`                tinyint      NOT NULL DEFAULT 0                                         COMMENT '是否热门：0-否，1-是',
  `is_essence`            tinyint      NOT NULL DEFAULT 0                                         COMMENT '是否精华：0-否，1-是',
  `quality_score`         decimal(3,2) DEFAULT 0.00                                              COMMENT '评论质量分数（0-5.00）',
  `ip_address`            varchar(50)  DEFAULT NULL                                              COMMENT 'IP地址',
  `device_info`           varchar(200) DEFAULT NULL                                              COMMENT '设备信息',
  `location`              varchar(100) DEFAULT NULL                                              COMMENT '地理位置',
  `mention_user_ids`      json         DEFAULT NULL                                              COMMENT '提及的用户ID列表',
  `images`                json         DEFAULT NULL                                              COMMENT '评论图片列表',
  `extra_data`            json         DEFAULT NULL                                              COMMENT '扩展数据',
  `audit_status`          varchar(20)  DEFAULT 'PASS'                                            COMMENT '审核状态：PASS-通过，REJECT-拒绝，PENDING-待审核',
  `audit_reason`          varchar(500) DEFAULT NULL                                              COMMENT '审核原因',
  `create_time`           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP                        COMMENT '创建时间',
  `update_time`           datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`               tinyint      NOT NULL DEFAULT 0                                         COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  -- 核心查询索引
  KEY `idx_target_type_status_time` (`target_id`, `comment_type`, `status`, `create_time`),
  KEY `idx_target_parent_status` (`target_id`, `parent_comment_id`, `status`),
  KEY `idx_root_comment_time` (`root_comment_id`, `create_time`),
  -- 用户相关索引
  KEY `idx_user_id_time` (`user_id`, `create_time`),
  KEY `idx_user_status_time` (`user_id`, `status`, `create_time`),
  -- 统计查询索引
  KEY `idx_like_count_time` (`like_count`, `create_time`),
  KEY `idx_hot_pinned_time` (`is_hot`, `is_pinned`, `create_time`),
  -- 管理查询索引
  KEY `idx_status_time` (`status`, `create_time`),
  KEY `idx_audit_status` (`audit_status`, `create_time`),
  -- 复合查询索引
  KEY `idx_target_type_parent_status_time` (`target_id`, `comment_type`, `parent_comment_id`, `status`, `create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表（完全去连表化设计）';

-- ==========================================
-- 评论点赞表 - 用于幂等性检查
-- ==========================================

DROP TABLE IF EXISTS `t_comment_like`;
CREATE TABLE `t_comment_like` (
  `id`           bigint   NOT NULL AUTO_INCREMENT                           COMMENT '主键ID',
  `comment_id`   bigint   NOT NULL                                          COMMENT '评论ID',
  `user_id`      bigint   NOT NULL                                          COMMENT '用户ID',
  `like_type`    varchar(20) NOT NULL DEFAULT 'LIKE'                       COMMENT '点赞类型：LIKE-点赞，DISLIKE-点踩',
  `ip_address`   varchar(50) DEFAULT NULL                                   COMMENT 'IP地址',
  `create_time`  datetime NOT NULL DEFAULT CURRENT_TIMESTAMP               COMMENT '点赞时间',
  `deleted`      tinyint  NOT NULL DEFAULT 0                               COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_user_type` (`comment_id`, `user_id`, `like_type`, `deleted`),
  KEY `idx_comment_id` (`comment_id`),
  KEY `idx_user_id_time` (`user_id`, `create_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

-- ==========================================
-- 评论举报表
-- ==========================================

DROP TABLE IF EXISTS `t_comment_report`;
CREATE TABLE `t_comment_report` (
  `id`             bigint      NOT NULL AUTO_INCREMENT                      COMMENT '主键ID',
  `comment_id`     bigint      NOT NULL                                     COMMENT '评论ID',
  `reporter_id`    bigint      NOT NULL                                     COMMENT '举报用户ID',
  `report_type`    varchar(50) NOT NULL                                     COMMENT '举报类型：SPAM-垃圾信息，ABUSE-辱骂，PORN-色情，VIOLENCE-暴力，OTHER-其他',
  `report_reason`  varchar(500) DEFAULT NULL                               COMMENT '举报原因',
  `report_content` text        DEFAULT NULL                                COMMENT '举报详情',
  `status`         varchar(20) NOT NULL DEFAULT 'PENDING'                  COMMENT '处理状态：PENDING-待处理，PROCESSED-已处理，REJECTED-已驳回',
  `handle_result`  varchar(500) DEFAULT NULL                               COMMENT '处理结果',
  `handler_id`     bigint      DEFAULT NULL                                COMMENT '处理人ID',
  `handle_time`    datetime    DEFAULT NULL                                COMMENT '处理时间',
  `ip_address`     varchar(50) DEFAULT NULL                                COMMENT 'IP地址',
  `create_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP          COMMENT '创建时间',
  `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`        tinyint     NOT NULL DEFAULT 0                          COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_comment_reporter` (`comment_id`, `reporter_id`, `deleted`),
  KEY `idx_comment_id` (`comment_id`),
  KEY `idx_reporter_id` (`reporter_id`),
  KEY `idx_status_time` (`status`, `create_time`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论举报表';

-- ==========================================
-- 评论统计表（可选，用于复杂统计）
-- ==========================================

DROP TABLE IF EXISTS `t_comment_statistics`;
CREATE TABLE `t_comment_statistics` (
  `target_id`      bigint      NOT NULL                                     COMMENT '目标对象ID',
  `comment_type`   varchar(20) NOT NULL                                     COMMENT '评论类型',
  `total_count`    bigint      NOT NULL DEFAULT 0                          COMMENT '总评论数',
  `root_count`     bigint      NOT NULL DEFAULT 0                          COMMENT '根评论数',
  `reply_count`    bigint      NOT NULL DEFAULT 0                          COMMENT '回复评论数',
  `today_count`    bigint      NOT NULL DEFAULT 0                          COMMENT '今日评论数',
  `hot_count`      bigint      NOT NULL DEFAULT 0                          COMMENT '热门评论数',
  `last_comment_time` datetime DEFAULT NULL                                COMMENT '最后评论时间',
  `last_comment_user_id` bigint DEFAULT NULL                               COMMENT '最后评论用户ID',
  `update_time`    datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`target_id`, `comment_type`),
  KEY `idx_total_count` (`total_count`),
  KEY `idx_last_comment_time` (`last_comment_time`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论统计表';

-- ==========================================
-- 评论敏感词表
-- ==========================================

DROP TABLE IF EXISTS `t_comment_sensitive_word`;
CREATE TABLE `t_comment_sensitive_word` (
  `id`           bigint      NOT NULL AUTO_INCREMENT                       COMMENT '主键ID',
  `word`         varchar(100) NOT NULL                                     COMMENT '敏感词',
  `word_type`    varchar(50) NOT NULL                                      COMMENT '敏感词类型：POLITICS-政治，PORN-色情，VIOLENCE-暴力，ABUSE-辱骂',
  `severity`     int         NOT NULL DEFAULT 1                           COMMENT '严重程度：1-轻微，2-中等，3-严重',
  `action`       varchar(20) NOT NULL DEFAULT 'REPLACE'                   COMMENT '处理动作：REPLACE-替换，BLOCK-拦截，AUDIT-审核',
  `replacement`  varchar(100) DEFAULT '***'                               COMMENT '替换文本',
  `status`       varchar(20) NOT NULL DEFAULT 'ACTIVE'                    COMMENT '状态：ACTIVE-生效，INACTIVE-停用',
  `create_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP           COMMENT '创建时间',
  `update_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`      tinyint     NOT NULL DEFAULT 0                           COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`, `deleted`),
  KEY `idx_word_type` (`word_type`),
  KEY `idx_status` (`status`),
  KEY `idx_severity` (`severity`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论敏感词表';

-- ==========================================
-- 插入基础敏感词数据
-- ==========================================

INSERT INTO `t_comment_sensitive_word` (`word`, `word_type`, `severity`, `action`, `replacement`) VALUES
('垃圾', 'ABUSE', 1, 'REPLACE', '***'),
('傻逼', 'ABUSE', 2, 'REPLACE', '***'),
('死去', 'VIOLENCE', 2, 'REPLACE', '***'),
('色情', 'PORN', 3, 'BLOCK', NULL),
('政治敏感', 'POLITICS', 3, 'AUDIT', NULL);

-- ==========================================
-- 创建存储过程 - 更新评论统计
-- ==========================================

DELIMITER $$

DROP PROCEDURE IF EXISTS `UpdateCommentStatistics`$$
CREATE PROCEDURE `UpdateCommentStatistics`(
    IN p_target_id BIGINT,
    IN p_comment_type VARCHAR(20),
    IN p_increment INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 更新或插入统计记录
    INSERT INTO t_comment_statistics (
        target_id, comment_type, total_count, update_time
    ) VALUES (
        p_target_id, p_comment_type, p_increment, NOW()
    ) ON DUPLICATE KEY UPDATE 
        total_count = total_count + p_increment,
        update_time = NOW();
    
    COMMIT;
END$$

DELIMITER ;

-- ==========================================
-- 创建存储过程 - 更新回复统计
-- ==========================================

DELIMITER $$

DROP PROCEDURE IF EXISTS `UpdateReplyCount`$$
CREATE PROCEDURE `UpdateReplyCount`(
    IN p_comment_id BIGINT,
    IN p_increment INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 更新评论的回复数
    UPDATE t_comment 
    SET reply_count = GREATEST(0, reply_count + p_increment),
        update_time = NOW()
    WHERE id = p_comment_id AND deleted = 0;
    
    COMMIT;
END$$

DELIMITER ;

-- ==========================================
-- 创建存储过程 - 更新点赞统计
-- ==========================================

DELIMITER $$

DROP PROCEDURE IF EXISTS `UpdateLikeCount`$$
CREATE PROCEDURE `UpdateLikeCount`(
    IN p_comment_id BIGINT,
    IN p_increment INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 更新评论的点赞数
    UPDATE t_comment 
    SET like_count = GREATEST(0, like_count + p_increment),
        update_time = NOW()
    WHERE id = p_comment_id AND deleted = 0;
    
    COMMIT;
END$$

DELIMITER ;

-- ==========================================
-- 创建触发器 - 评论创建后更新统计
-- ==========================================

DELIMITER $$

DROP TRIGGER IF EXISTS `tr_comment_after_insert`$$
CREATE TRIGGER `tr_comment_after_insert`
    AFTER INSERT ON `t_comment`
    FOR EACH ROW
BEGIN
    -- 更新目标对象的评论统计
    CALL UpdateCommentStatistics(NEW.target_id, NEW.comment_type, 1);
    
    -- 如果是回复评论，更新父评论的回复数
    IF NEW.parent_comment_id > 0 THEN
        CALL UpdateReplyCount(NEW.parent_comment_id, 1);
    END IF;
END$$

DELIMITER ;

-- ==========================================
-- 创建触发器 - 评论删除后更新统计
-- ==========================================

DELIMITER $$

DROP TRIGGER IF EXISTS `tr_comment_after_delete`$$
CREATE TRIGGER `tr_comment_after_delete`
    AFTER UPDATE ON `t_comment`
    FOR EACH ROW
BEGIN
    -- 检查是否是逻辑删除操作
    IF OLD.deleted = 0 AND NEW.deleted = 1 THEN
        -- 更新目标对象的评论统计
        CALL UpdateCommentStatistics(NEW.target_id, NEW.comment_type, -1);
        
        -- 如果是回复评论，更新父评论的回复数
        IF NEW.parent_comment_id > 0 THEN
            CALL UpdateReplyCount(NEW.parent_comment_id, -1);
        END IF;
    END IF;
END$$

DELIMITER ;

-- ==========================================
-- 创建视图 - 评论树视图（优化查询）
-- ==========================================

DROP VIEW IF EXISTS `v_comment_tree`;
CREATE VIEW `v_comment_tree` AS
SELECT 
    c.id,
    c.comment_type,
    c.target_id,
    c.parent_comment_id,
    c.root_comment_id,
    c.content,
    c.user_id,
    c.user_nickname,
    c.user_avatar,
    c.user_verified,
    c.reply_to_user_id,
    c.reply_to_user_nickname,
    c.status,
    c.like_count,
    c.reply_count,
    c.is_pinned,
    c.is_hot,
    c.is_essence,
    c.quality_score,
    c.create_time,
    c.update_time,
    -- 计算评论层级
    CASE 
        WHEN c.parent_comment_id = 0 THEN 0
        ELSE 1 
    END as comment_level,
    -- 热度分数（综合点赞数、回复数、时间等因素）
    (
        c.like_count * 2 + 
        c.reply_count * 3 + 
        (CASE WHEN c.is_pinned = 1 THEN 100 ELSE 0 END) +
        (CASE WHEN c.is_hot = 1 THEN 50 ELSE 0 END) +
        (CASE WHEN c.is_essence = 1 THEN 30 ELSE 0 END) +
        (UNIX_TIMESTAMP(NOW()) - UNIX_TIMESTAMP(c.create_time)) / 3600 * (-0.1)
    ) as hot_score
FROM t_comment c
WHERE c.deleted = 0 AND c.status = 'NORMAL';

-- ==========================================
-- 创建性能优化索引
-- ==========================================

-- 热门评论查询优化
ALTER TABLE t_comment ADD INDEX `idx_hot_score` (`target_id`, `comment_type`, `like_count` DESC, `reply_count` DESC, `create_time` DESC);

-- 用户评论历史查询优化
ALTER TABLE t_comment ADD INDEX `idx_user_comments` (`user_id`, `status`, `create_time` DESC);

-- 评论树查询优化
ALTER TABLE t_comment ADD INDEX `idx_comment_tree` (`target_id`, `comment_type`, `root_comment_id`, `parent_comment_id`, `create_time`);

-- 管理查询优化
ALTER TABLE t_comment ADD INDEX `idx_admin_query` (`status`, `audit_status`, `create_time` DESC);

-- ==========================================
-- 插入测试数据（可选）
-- ==========================================

-- 插入测试评论数据
INSERT INTO `t_comment` (
    `comment_type`, `target_id`, `content`, `user_id`, `user_nickname`, `user_avatar`, 
    `status`, `like_count`, `reply_count`
) VALUES 
('CONTENT', 1001, '这是一个很棒的内容！', 1, '张三', 'https://cdn.example.com/avatar/1.jpg', 'NORMAL', 5, 2),
('CONTENT', 1001, '我也觉得不错！', 2, '李四', 'https://cdn.example.com/avatar/2.jpg', 'NORMAL', 3, 0),
('CONTENT', 1001, '同意楼上的观点', 3, '王五', 'https://cdn.example.com/avatar/3.jpg', 'NORMAL', 1, 1),
('DYNAMIC', 2001, '今天天气真好！', 4, '赵六', 'https://cdn.example.com/avatar/4.jpg', 'NORMAL', 8, 3);

-- 插入回复评论
INSERT INTO `t_comment` (
    `comment_type`, `target_id`, `parent_comment_id`, `root_comment_id`, `content`, 
    `user_id`, `user_nickname`, `user_avatar`, `reply_to_user_id`, `reply_to_user_nickname`, `status`
) VALUES 
('CONTENT', 1001, 1, 1, '确实很棒！', 5, '孙七', 'https://cdn.example.com/avatar/5.jpg', 1, '张三', 'NORMAL'),
('CONTENT', 1001, 1, 1, '我也来支持一下', 6, '周八', 'https://cdn.example.com/avatar/6.jpg', 1, '张三', 'NORMAL');

-- ==========================================
-- 权限设置
-- ==========================================

-- 创建评论服务专用用户（生产环境使用）
-- CREATE USER 'comment_service'@'%' IDENTIFIED BY 'strong_password_here';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON collide.t_comment* TO 'comment_service'@'%';
-- GRANT EXECUTE ON PROCEDURE collide.UpdateCommentStatistics TO 'comment_service'@'%';
-- GRANT EXECUTE ON PROCEDURE collide.UpdateReplyCount TO 'comment_service'@'%';
-- GRANT EXECUTE ON PROCEDURE collide.UpdateLikeCount TO 'comment_service'@'%';

-- ==========================================
-- 完成
-- ==========================================

SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建的表
SHOW TABLES LIKE '%comment%';

-- 显示评论表结构
DESCRIBE t_comment;

-- 显示索引信息
SHOW INDEX FROM t_comment;

SELECT '==========================================';
SELECT '评论模块数据库初始化完成！';
SELECT '表结构采用完全去连表化设计';
SELECT '所有查询都基于单表，性能优化充分';
SELECT '=========================================='; 