-- ==========================================
-- Collide 每日任务系统 - 简洁版
-- 用户每日任务功能，支持金币和商品奖励
-- 设计原则：简洁、高效、易扩展
-- ==========================================

USE collide;

-- ==========================================
-- 任务定义表
-- ==========================================

-- 任务模板表（系统预定义的任务类型）
DROP TABLE IF EXISTS `t_task_template`;
CREATE TABLE `t_task_template` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务模板ID',
    `task_name`       VARCHAR(100) NOT NULL                COMMENT '任务名称',
    `task_desc`       VARCHAR(500) NOT NULL                COMMENT '任务描述',
    `task_type`       VARCHAR(50)  NOT NULL                COMMENT '任务类型：daily、weekly、achievement',
    `task_category`   VARCHAR(50)  NOT NULL                COMMENT '任务分类：login、content、social、consume',
    `task_action`     VARCHAR(50)  NOT NULL                COMMENT '任务动作：login、publish_content、like、comment、share、purchase',
    `target_count`    INT          NOT NULL DEFAULT 1      COMMENT '目标完成次数',
    `sort_order`      INT          NOT NULL DEFAULT 0      COMMENT '排序值',
    `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否启用',
    `start_date`      DATE         NULL                    COMMENT '任务开始日期',
    `end_date`        DATE         NULL                    COMMENT '任务结束日期',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_task_category` (`task_category`),
    KEY `idx_active_sort` (`is_active`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板表';

-- ==========================================
-- 任务奖励配置表
-- ==========================================

-- 任务奖励表
DROP TABLE IF EXISTS `t_task_reward`;
CREATE TABLE `t_task_reward` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
    `task_id`       BIGINT       NOT NULL                COMMENT '任务模板ID',
    `reward_type`   VARCHAR(50)  NOT NULL                COMMENT '奖励类型：coin、item、vip、experience',
    `reward_name`   VARCHAR(100) NOT NULL                COMMENT '奖励名称',
    `reward_desc`   VARCHAR(500)                         COMMENT '奖励描述',
    `reward_amount` INT          NOT NULL DEFAULT 1      COMMENT '奖励数量',
    `reward_data`   JSON                                 COMMENT '奖励扩展数据（商品信息等）',
    `is_main_reward` TINYINT(1)  NOT NULL DEFAULT 1      COMMENT '是否主要奖励',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_reward_type` (`reward_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务奖励配置表';

-- ==========================================
-- 用户任务记录表
-- ==========================================

-- 用户任务记录表
DROP TABLE IF EXISTS `t_user_task_record`;
CREATE TABLE `t_user_task_record` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`         BIGINT       NOT NULL                COMMENT '用户ID',
    `task_id`         BIGINT       NOT NULL                COMMENT '任务模板ID',
    `task_date`       DATE         NOT NULL                COMMENT '任务日期（用于每日任务）',
    
    -- 任务信息冗余（避免连表查询）
    `task_name`       VARCHAR(100) NOT NULL                COMMENT '任务名称（冗余）',
    `task_type`       VARCHAR(50)  NOT NULL                COMMENT '任务类型（冗余）',
    `task_category`   VARCHAR(50)  NOT NULL                COMMENT '任务分类（冗余）',
    `target_count`    INT          NOT NULL                COMMENT '目标完成次数（冗余）',
    
    -- 完成情况
    `current_count`   INT          NOT NULL DEFAULT 0      COMMENT '当前完成次数',
    `is_completed`    TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否已完成',
    `is_rewarded`     TINYINT(1)   NOT NULL DEFAULT 0      COMMENT '是否已领取奖励',
    `complete_time`   TIMESTAMP    NULL                    COMMENT '完成时间',
    `reward_time`     TIMESTAMP    NULL                    COMMENT '奖励领取时间',
    
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `task_date`),
    KEY `idx_user_date` (`user_id`, `task_date`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_completed` (`is_completed`),
    KEY `idx_rewarded` (`is_rewarded`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户任务记录表';

-- ==========================================
-- 用户奖励记录表
-- ==========================================

-- 用户奖励记录表
DROP TABLE IF EXISTS `t_user_reward_record`;
CREATE TABLE `t_user_reward_record` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '奖励记录ID',
    `user_id`       BIGINT       NOT NULL                COMMENT '用户ID',
    `task_record_id` BIGINT      NOT NULL                COMMENT '任务记录ID',
    `reward_source` VARCHAR(50)  NOT NULL DEFAULT 'task' COMMENT '奖励来源：task、event、system',
    
    -- 奖励信息
    `reward_type`   VARCHAR(50)  NOT NULL                COMMENT '奖励类型：coin、item、vip、experience',
    `reward_name`   VARCHAR(100) NOT NULL                COMMENT '奖励名称',
    `reward_amount` INT          NOT NULL                COMMENT '奖励数量',
    `reward_data`   JSON                                 COMMENT '奖励扩展数据',
    
    -- 发放状态
    `status`        VARCHAR(20)  NOT NULL DEFAULT 'pending' COMMENT '状态：pending、success、failed',
    `grant_time`    TIMESTAMP    NULL                    COMMENT '发放时间',
    `expire_time`   TIMESTAMP    NULL                    COMMENT '过期时间',
    
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_task_record` (`task_record_id`),
    KEY `idx_reward_type` (`reward_type`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户奖励记录表';

-- ==========================================
-- 用户金币账户表（可选扩展）
-- ==========================================

-- 用户金币账户表
DROP TABLE IF EXISTS `t_user_coin_account`;
CREATE TABLE `t_user_coin_account` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '账户ID',
    `user_id`       BIGINT       NOT NULL                COMMENT '用户ID',
    `total_coin`    BIGINT       NOT NULL DEFAULT 0      COMMENT '总金币数',
    `available_coin` BIGINT      NOT NULL DEFAULT 0      COMMENT '可用金币数',
    `frozen_coin`   BIGINT       NOT NULL DEFAULT 0      COMMENT '冻结金币数',
    `total_earned`  BIGINT       NOT NULL DEFAULT 0      COMMENT '累计获得金币',
    `total_spent`   BIGINT       NOT NULL DEFAULT 0      COMMENT '累计消费金币',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户金币账户表';

-- ==========================================
-- 初始化任务模板数据
-- ==========================================

-- 每日任务模板
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('每日登录', '每日登录获得金币奖励', 'daily', 'login', 'login', 1, 1),
('发布内容', '发布1篇内容获得奖励', 'daily', 'content', 'publish_content', 1, 2),
('点赞互动', '为其他用户内容点赞5次', 'daily', 'social', 'like', 5, 3),
('评论互动', '发表3条有效评论', 'daily', 'social', 'comment', 3, 4),
('分享内容', '分享1次内容到社交平台', 'daily', 'social', 'share', 1, 5);

-- 周常任务模板
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('周活跃用户', '连续登录7天', 'weekly', 'login', 'login', 7, 10),
('内容创作者', '本周发布5篇内容', 'weekly', 'content', 'publish_content', 5, 11),
('社交达人', '本周获得50个点赞', 'weekly', 'social', 'like_received', 50, 12);

-- ==========================================
-- 初始化任务奖励配置
-- ==========================================

-- 每日任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `is_main_reward`) VALUES
-- 每日登录奖励
(1, 'coin', '金币', '每日登录奖励', 10, 1),
-- 发布内容奖励
(2, 'coin', '金币', '发布内容奖励', 20, 1),
(2, 'experience', '经验值', '创作经验', 5, 0),
-- 点赞互动奖励
(3, 'coin', '金币', '社交互动奖励', 15, 1),
-- 评论互动奖励
(4, 'coin', '金币', '评论互动奖励', 12, 1),
-- 分享内容奖励
(5, 'coin', '金币', '分享内容奖励', 8, 1);

-- 周常任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
-- 周活跃用户奖励
(6, 'coin', '金币', '周活跃奖励', 100, NULL, 1),
(6, 'item', '活跃宝箱', '随机道具宝箱', 1, JSON_OBJECT('item_id', 1001, 'item_type', 'treasure_box'), 0),
-- 内容创作者奖励
(7, 'coin', '金币', '创作者奖励', 150, NULL, 1),
(7, 'vip', 'VIP体验', 'VIP功能体验3天', 3, JSON_OBJECT('vip_type', 'premium', 'duration_days', 3), 0),
-- 社交达人奖励
(8, 'coin', '金币', '社交达人奖励', 80, NULL, 1);

-- ==========================================
-- 常用查询示例
-- ==========================================

-- 1. 获取用户今日任务列表
-- SELECT 
--     t.id as task_id,
--     t.task_name,
--     t.task_desc,
--     t.target_count,
--     COALESCE(r.current_count, 0) as current_count,
--     COALESCE(r.is_completed, 0) as is_completed,
--     COALESCE(r.is_rewarded, 0) as is_rewarded
-- FROM t_task_template t
-- LEFT JOIN t_user_task_record r ON t.id = r.task_id 
--     AND r.user_id = #{userId} 
--     AND r.task_date = CURDATE()
-- WHERE t.task_type = 'daily' 
--   AND t.is_active = 1
-- ORDER BY t.sort_order;

-- 2. 获取用户任务进度统计
-- SELECT 
--     task_type,
--     COUNT(*) as total_tasks,
--     SUM(is_completed) as completed_tasks,
--     SUM(is_rewarded) as rewarded_tasks
-- FROM t_user_task_record 
-- WHERE user_id = #{userId} 
--   AND task_date = CURDATE()
-- GROUP BY task_type;

-- 3. 获取用户奖励记录
-- SELECT 
--     reward_type,
--     reward_name,
--     reward_amount,
--     status,
--     create_time
-- FROM t_user_reward_record 
-- WHERE user_id = #{userId}
-- ORDER BY create_time DESC
-- LIMIT 20;

-- ==========================================
-- 存储过程（任务进度更新）
-- ==========================================

DELIMITER $$

-- 更新用户任务进度
DROP PROCEDURE IF EXISTS `update_user_task_progress`$$
CREATE PROCEDURE `update_user_task_progress`(
    IN p_user_id BIGINT,
    IN p_task_action VARCHAR(50),
    IN p_increment INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_task_id BIGINT;
    DECLARE v_task_name VARCHAR(100);
    DECLARE v_task_type VARCHAR(50);
    DECLARE v_task_category VARCHAR(50);
    DECLARE v_target_count INT;
    
    -- 定义游标
    DECLARE task_cursor CURSOR FOR 
        SELECT id, task_name, task_type, task_category, target_count
        FROM t_task_template 
        WHERE task_action = p_task_action 
          AND is_active = 1
          AND (start_date IS NULL OR start_date <= CURDATE())
          AND (end_date IS NULL OR end_date >= CURDATE());
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 打开游标
    OPEN task_cursor;
    
    read_loop: LOOP
        FETCH task_cursor INTO v_task_id, v_task_name, v_task_type, v_task_category, v_target_count;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 插入或更新任务记录
        INSERT INTO t_user_task_record (
            user_id, task_id, task_date, task_name, task_type, task_category, 
            target_count, current_count, is_completed
        )
        VALUES (
            p_user_id, v_task_id, CURDATE(), v_task_name, v_task_type, v_task_category,
            v_target_count, p_increment, 
            CASE WHEN p_increment >= v_target_count THEN 1 ELSE 0 END
        )
        ON DUPLICATE KEY UPDATE
            current_count = LEAST(current_count + p_increment, target_count),
            is_completed = CASE WHEN current_count >= target_count THEN 1 ELSE 0 END,
            complete_time = CASE WHEN current_count >= target_count AND complete_time IS NULL THEN NOW() ELSE complete_time END;
            
    END LOOP;
    
    -- 关闭游标
    CLOSE task_cursor;
END$$

DELIMITER ;

-- ==========================================
-- 触发器（自动发放奖励）
-- ==========================================

-- 任务完成后自动记录奖励
DROP TRIGGER IF EXISTS `tr_task_complete_reward`;
DELIMITER $$
CREATE TRIGGER `tr_task_complete_reward`
AFTER UPDATE ON `t_user_task_record`
FOR EACH ROW
BEGIN
    -- 如果任务刚完成且未发放奖励
    IF NEW.is_completed = 1 AND OLD.is_completed = 0 AND NEW.is_rewarded = 0 THEN
        -- 为该任务创建奖励记录
        INSERT INTO t_user_reward_record (
            user_id, task_record_id, reward_source, reward_type, 
            reward_name, reward_amount, reward_data, status
        )
        SELECT 
            NEW.user_id, NEW.id, 'task', tr.reward_type,
            tr.reward_name, tr.reward_amount, tr.reward_data, 'pending'
        FROM t_task_reward tr
        WHERE tr.task_id = NEW.task_id;
    END IF;
END$$
DELIMITER ;

-- ==========================================
-- 索引优化说明
-- ==========================================

-- 1. uk_user_task_date: 确保用户每日任务唯一性
-- 2. idx_user_date: 用于查询用户指定日期任务
-- 3. idx_task_id: 用于任务模板关联查询
-- 4. idx_completed/rewarded: 用于任务状态统计
-- 5. idx_reward_type: 用于奖励类型统计

-- ==========================================
-- 功能特性说明
-- ==========================================

-- 1. 任务系统：
--    - 支持每日、周常、成就等多种任务类型
--    - 灵活的任务动作定义（登录、发布、互动等）
--    - 可配置的任务目标和奖励

-- 2. 奖励系统：
--    - 支持金币、道具、VIP、经验值等多种奖励
--    - JSON扩展字段支持复杂奖励配置
--    - 自动发放机制

-- 3. 进度追踪：
--    - 实时更新任务进度
--    - 自动判断任务完成状态
--    - 防重复完成机制

-- 4. 性能优化：
--    - 冗余字段避免复杂连表查询
--    - 合理索引支持高并发访问
--    - 存储过程批量处理任务更新