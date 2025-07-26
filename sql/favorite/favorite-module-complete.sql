-- =====================================================
-- Collide 收藏模块 - 完整数据库设计
-- 基于去连表化设计，提升查询性能
-- Author: Collide Team
-- Version: 1.0
-- Date: 2024-01-01
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

-- =====================================================
-- 1. 收藏主表 - 去连表化设计
-- =====================================================
DROP TABLE IF EXISTS `t_favorite`;
CREATE TABLE `t_favorite` (
    -- === 主键和基础字段 ===
    `favorite_id`           BIGINT          NOT NULL        COMMENT '收藏ID',
    `favorite_type`         VARCHAR(20)     NOT NULL        COMMENT '收藏类型：CONTENT-内容, USER-用户, SOCIAL-动态, COMMENT-评论, TOPIC-话题',
    `target_id`             BIGINT          NOT NULL        COMMENT '目标ID（内容ID、用户ID等）',
    `user_id`               BIGINT          NOT NULL        COMMENT '收藏用户ID',
    `folder_id`             BIGINT          DEFAULT 1       COMMENT '收藏夹ID，默认为1（默认收藏夹）',
    `status`                VARCHAR(20)     DEFAULT 'NORMAL' COMMENT '收藏状态：NORMAL-正常, CANCELLED-已取消',
    `remark`                VARCHAR(500)    DEFAULT ''      COMMENT '收藏备注',
    `favorite_time`         DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',

    -- === 目标信息冗余字段（去连表化设计） ===
    `target_title`          VARCHAR(500)    DEFAULT ''      COMMENT '目标标题（冗余字段）',
    `target_cover`          VARCHAR(500)    DEFAULT ''      COMMENT '目标封面/头像（冗余字段）',
    `target_summary`        TEXT                            COMMENT '目标摘要/描述（冗余字段）',
    `target_author_id`      BIGINT                          COMMENT '目标作者ID（冗余字段）',
    `target_author_name`    VARCHAR(100)    DEFAULT ''      COMMENT '目标作者名称（冗余字段）',
    `target_author_avatar`  VARCHAR(500)    DEFAULT ''      COMMENT '目标作者头像（冗余字段）',
    `target_publish_time`   DATETIME                        COMMENT '目标发布时间（冗余字段）',

    -- === 审计字段 ===
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`            TINYINT         DEFAULT 0       COMMENT '是否删除：0-否，1-是',
    `version`               INT             DEFAULT 0       COMMENT '版本号（乐观锁）',

    -- === 主键和索引 ===
    PRIMARY KEY (`favorite_id`),
    
    -- 幂等性约束：同一用户对同一目标同一类型只能收藏一次（排除已删除）
    UNIQUE KEY `uk_user_favorite_target` (`user_id`, `favorite_type`, `target_id`, `is_deleted`),
    
    -- 查询索引
    KEY `idx_user_id_type_folder` (`user_id`, `favorite_type`, `folder_id`, `status`),
    KEY `idx_target_id_type` (`target_id`, `favorite_type`, `status`),
    KEY `idx_folder_id_time` (`folder_id`, `favorite_time`),
    KEY `idx_favorite_time` (`favorite_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status_deleted` (`status`, `is_deleted`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表 - 去连表化设计';

-- =====================================================
-- 2. 收藏夹表
-- =====================================================
DROP TABLE IF EXISTS `t_favorite_folder`;
CREATE TABLE `t_favorite_folder` (
    -- === 主键和基础字段 ===
    `folder_id`             BIGINT          NOT NULL        COMMENT '收藏夹ID',
    `folder_name`           VARCHAR(100)    NOT NULL        COMMENT '收藏夹名称',
    `description`           VARCHAR(500)    DEFAULT ''      COMMENT '收藏夹描述',
    `folder_type`           VARCHAR(20)     DEFAULT 'CUSTOM' COMMENT '收藏夹类型：DEFAULT-默认, PUBLIC-公开, PRIVATE-私密, CUSTOM-自定义',
    `user_id`               BIGINT          NOT NULL        COMMENT '用户ID',
    
    -- === 功能字段 ===
    `is_default`            TINYINT         DEFAULT 0       COMMENT '是否为默认收藏夹：0-否，1-是',
    `cover_image`           VARCHAR(500)    DEFAULT ''      COMMENT '收藏夹封面图片',
    `sort_order`            INT             DEFAULT 10      COMMENT '排序权重，数值越小越靠前',
    `item_count`            INT             DEFAULT 0       COMMENT '收藏数量（冗余字段，提升性能）',

    -- === 审计字段 ===
    `create_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`            TINYINT         DEFAULT 0       COMMENT '是否删除：0-否，1-是',
    `version`               INT             DEFAULT 0       COMMENT '版本号（乐观锁）',

    -- === 主键和索引 ===
    PRIMARY KEY (`folder_id`),
    
    -- 业务约束：同一用户的收藏夹名称不能重复（排除已删除）
    UNIQUE KEY `uk_user_folder_name` (`user_id`, `folder_name`, `is_deleted`),
    
    -- 默认收藏夹约束：每个用户只能有一个默认收藏夹（排除已删除）
    UNIQUE KEY `uk_user_default_folder` (`user_id`, `is_default`, `is_deleted`),
    
    -- 查询索引
    KEY `idx_user_id_sort` (`user_id`, `sort_order`, `is_deleted`),
    KEY `idx_folder_type` (`folder_type`),
    KEY `idx_create_time` (`create_time`)
    
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏夹表';

-- =====================================================
-- 3. 统计视图（可选 - 用于数据分析）
-- =====================================================
CREATE OR REPLACE VIEW `v_favorite_statistics` AS
SELECT 
    f.`favorite_type`,
    f.`target_id`,
    COUNT(*) as `favorite_count`,
    COUNT(DISTINCT f.`user_id`) as `user_count`,
    MIN(f.`favorite_time`) as `first_favorite_time`,
    MAX(f.`favorite_time`) as `last_favorite_time`
FROM `t_favorite` f
WHERE f.`status` = 'NORMAL' AND f.`is_deleted` = 0
GROUP BY f.`favorite_type`, f.`target_id`;

-- =====================================================
-- 4. 初始化数据
-- =====================================================

-- 插入默认收藏夹（用户注册时自动创建）
-- 注意：这里只是示例，实际应用中通过业务代码创建
INSERT INTO `t_favorite_folder` (
    `folder_id`, `folder_name`, `description`, `folder_type`, 
    `user_id`, `is_default`, `sort_order`, `item_count`
) VALUES 
-- 系统默认收藏夹（ID=1，用于兼容旧数据）
(1, '默认收藏夹', '系统默认收藏夹', 'DEFAULT', 0, 1, 0, 0);

-- =====================================================
-- 5. 触发器（维护统计数据一致性）
-- =====================================================

-- 收藏数量统计触发器
DELIMITER $$

-- 新增收藏时更新收藏夹计数
CREATE TRIGGER `tr_favorite_insert_update_count` 
AFTER INSERT ON `t_favorite`
FOR EACH ROW
BEGIN
    IF NEW.`status` = 'NORMAL' AND NEW.`is_deleted` = 0 THEN
        UPDATE `t_favorite_folder` 
        SET `item_count` = `item_count` + 1,
            `update_time` = CURRENT_TIMESTAMP
        WHERE `folder_id` = NEW.`folder_id` AND `is_deleted` = 0;
    END IF;
END$$

-- 更新收藏时维护收藏夹计数
CREATE TRIGGER `tr_favorite_update_update_count` 
AFTER UPDATE ON `t_favorite`
FOR EACH ROW
BEGIN
    -- 如果状态从正常变为取消或删除
    IF OLD.`status` = 'NORMAL' AND OLD.`is_deleted` = 0 
       AND (NEW.`status` != 'NORMAL' OR NEW.`is_deleted` = 1) THEN
        UPDATE `t_favorite_folder` 
        SET `item_count` = GREATEST(`item_count` - 1, 0),
            `update_time` = CURRENT_TIMESTAMP
        WHERE `folder_id` = OLD.`folder_id` AND `is_deleted` = 0;
    END IF;
    
    -- 如果状态从取消变为正常
    IF (OLD.`status` != 'NORMAL' OR OLD.`is_deleted` = 1)
       AND NEW.`status` = 'NORMAL' AND NEW.`is_deleted` = 0 THEN
        UPDATE `t_favorite_folder` 
        SET `item_count` = `item_count` + 1,
            `update_time` = CURRENT_TIMESTAMP
        WHERE `folder_id` = NEW.`folder_id` AND `is_deleted` = 0;
    END IF;
    
    -- 如果移动到不同的收藏夹
    IF OLD.`folder_id` != NEW.`folder_id` 
       AND NEW.`status` = 'NORMAL' AND NEW.`is_deleted` = 0 THEN
        -- 原收藏夹减1
        UPDATE `t_favorite_folder` 
        SET `item_count` = GREATEST(`item_count` - 1, 0),
            `update_time` = CURRENT_TIMESTAMP
        WHERE `folder_id` = OLD.`folder_id` AND `is_deleted` = 0;
        
        -- 新收藏夹加1
        UPDATE `t_favorite_folder` 
        SET `item_count` = `item_count` + 1,
            `update_time` = CURRENT_TIMESTAMP
        WHERE `folder_id` = NEW.`folder_id` AND `is_deleted` = 0;
    END IF;
END$$

DELIMITER ;

-- =====================================================
-- 6. 存储过程（常用查询优化）
-- =====================================================

DELIMITER $$

-- 获取用户收藏统计
CREATE PROCEDURE `sp_get_user_favorite_stats`(
    IN `p_user_id` BIGINT
)
BEGIN
    SELECT 
        favorite_type,
        COUNT(*) as total_count,
        COUNT(CASE WHEN DATE(favorite_time) = CURDATE() THEN 1 END) as today_count,
        COUNT(CASE WHEN favorite_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) THEN 1 END) as week_count
    FROM t_favorite 
    WHERE user_id = p_user_id 
      AND status = 'NORMAL' 
      AND is_deleted = 0
    GROUP BY favorite_type;
END$$

-- 获取热门收藏目标
CREATE PROCEDURE `sp_get_hot_favorites`(
    IN `p_favorite_type` VARCHAR(20),
    IN `p_days` INT,
    IN `p_limit` INT
)
BEGIN
    SELECT 
        target_id,
        target_title,
        target_cover,
        COUNT(*) as favorite_count,
        COUNT(DISTINCT user_id) as user_count
    FROM t_favorite 
    WHERE favorite_type = p_favorite_type
      AND status = 'NORMAL' 
      AND is_deleted = 0
      AND favorite_time >= DATE_SUB(CURDATE(), INTERVAL p_days DAY)
    GROUP BY target_id, target_title, target_cover
    ORDER BY favorite_count DESC, user_count DESC
    LIMIT p_limit;
END$$

DELIMITER ;

-- =====================================================
-- 7. 数据迁移和清理脚本
-- =====================================================

-- 清理无效数据的存储过程
DELIMITER $$

CREATE PROCEDURE `sp_cleanup_favorite_data`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_folder_id BIGINT;
    DECLARE v_actual_count INT;
    DECLARE folder_cursor CURSOR FOR 
        SELECT folder_id FROM t_favorite_folder WHERE is_deleted = 0;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 修复收藏夹计数不一致的问题
    OPEN folder_cursor;
    read_loop: LOOP
        FETCH folder_cursor INTO v_folder_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 重新计算实际收藏数量
        SELECT COUNT(*) INTO v_actual_count
        FROM t_favorite 
        WHERE folder_id = v_folder_id 
          AND status = 'NORMAL' 
          AND is_deleted = 0;
        
        -- 更新收藏夹计数
        UPDATE t_favorite_folder 
        SET item_count = v_actual_count,
            update_time = CURRENT_TIMESTAMP
        WHERE folder_id = v_folder_id;
        
    END LOOP;
    CLOSE folder_cursor;
    
    -- 清理孤立的收藏记录（引用不存在的收藏夹）
    UPDATE t_favorite f
    SET is_deleted = 1, update_time = CURRENT_TIMESTAMP
    WHERE f.folder_id > 1  -- 保留默认收藏夹的记录
      AND f.is_deleted = 0
      AND NOT EXISTS (
          SELECT 1 FROM t_favorite_folder ff 
          WHERE ff.folder_id = f.folder_id AND ff.is_deleted = 0
      );
      
    SELECT 'Favorite data cleanup completed' as message;
END$$

DELIMITER ;

-- =====================================================
-- 8. 性能优化建议
-- =====================================================

-- 定期优化表
-- ALTER TABLE t_favorite OPTIMIZE;
-- ALTER TABLE t_favorite_folder OPTIMIZE;

-- 分析表统计信息
-- ANALYZE TABLE t_favorite;
-- ANALYZE TABLE t_favorite_folder;

-- =====================================================
-- 9. 监控查询（用于性能监控）
-- =====================================================

-- 查询收藏数据量统计
-- SELECT 
--     favorite_type,
--     COUNT(*) as total_records,
--     COUNT(CASE WHEN is_deleted = 0 THEN 1 END) as active_records,
--     COUNT(CASE WHEN DATE(create_time) = CURDATE() THEN 1 END) as today_created
-- FROM t_favorite 
-- GROUP BY favorite_type;

-- 查询收藏夹使用情况
-- SELECT 
--     folder_type,
--     COUNT(*) as folder_count,
--     SUM(item_count) as total_items,
--     AVG(item_count) as avg_items_per_folder
-- FROM t_favorite_folder 
-- WHERE is_deleted = 0
-- GROUP BY folder_type;

-- =====================================================
-- 完成
-- =====================================================
SELECT 'Collide Favorite Module Database Setup Completed!' as status; 