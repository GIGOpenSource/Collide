-- ==========================================
-- Collide 每日任务系统 - 优化版
-- 用户每日任务功能，支持金币和商品奖励
-- 设计原则：简洁、高效、易扩展
-- 基于用户系统优化经验进行重构
-- ==========================================

USE collide;

-- ==========================================
-- 任务类型和状态常量说明
-- ==========================================

-- 任务类型常量：1-daily, 2-weekly, 3-monthly, 4-achievement
-- 任务分类常量：1-login, 2-content, 3-social, 4-consume, 5-invite
-- 任务动作常量：1-login, 2-publish_content, 3-like, 4-comment, 5-share, 6-purchase, 7-invite_user
-- 奖励类型常量：1-coin, 2-item, 3-vip, 4-experience, 5-badge
-- 奖励来源常量：1-task, 2-event, 3-system, 4-admin
-- 奖励状态常量：1-pending, 2-success, 3-failed, 4-expired

-- ==========================================
-- 任务定义表
-- ==========================================

-- 任务模板表（系统预定义的任务类型）
DROP TABLE IF EXISTS `t_task_template`;
CREATE TABLE `t_task_template` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务模板ID',
    `task_name`       VARCHAR(60)  NOT NULL                COMMENT '任务名称',
    `task_desc`       VARCHAR(200) NOT NULL                COMMENT '任务描述',
    `task_type`       TINYINT      NOT NULL                COMMENT '任务类型：1-daily, 2-weekly, 3-monthly, 4-achievement',
    `task_category`   TINYINT      NOT NULL                COMMENT '任务分类：1-login, 2-content, 3-social, 4-consume, 5-invite',
    `task_action`     TINYINT      NOT NULL                COMMENT '任务动作：1-login, 2-publish_content, 3-like, 4-comment, 5-share, 6-purchase, 7-invite_user',
    `target_count`    INT          NOT NULL DEFAULT 1      COMMENT '目标完成次数',
    `sort_order`      SMALLINT     NOT NULL DEFAULT 0      COMMENT '排序值',
    `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT '是否启用',
    `start_date`      DATE         NULL                    COMMENT '任务开始日期',
    `end_date`        DATE         NULL                    COMMENT '任务结束日期',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_task_category` (`task_category`),
    KEY `idx_task_action` (`task_action`) USING HASH,
    KEY `idx_active_sort` (`is_active`, `sort_order`),
    KEY `idx_date_range` (`start_date`, `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务模板表';

-- ==========================================
-- 任务奖励配置表
-- ==========================================

-- 任务奖励表
DROP TABLE IF EXISTS `t_task_reward`;
CREATE TABLE `t_task_reward` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '奖励ID',
    `task_id`       BIGINT       NOT NULL                COMMENT '任务模板ID',
    `reward_type`   TINYINT      NOT NULL                COMMENT '奖励类型：1-coin, 2-item, 3-vip, 4-experience, 5-badge',
    `reward_name`   VARCHAR(60)  NOT NULL                COMMENT '奖励名称',
    `reward_desc`   VARCHAR(200)                         COMMENT '奖励描述',
    `reward_amount` INT          NOT NULL DEFAULT 1      COMMENT '奖励数量',
    `reward_data`   JSON                                 COMMENT '奖励扩展数据（商品信息等）',
    `is_main_reward` TINYINT(1)  NOT NULL DEFAULT 1      COMMENT '是否主要奖励',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_id` (`task_id`),
    KEY `idx_reward_type` (`reward_type`) USING HASH,
    KEY `idx_main_reward` (`is_main_reward`, `reward_type`)
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
    
    -- 任务信息冗余（避免连表查询，数据类型与模板表一致）
    `task_name`       VARCHAR(60)  NOT NULL                COMMENT '任务名称（冗余）',
    `task_type`       TINYINT      NOT NULL                COMMENT '任务类型（冗余）',
    `task_category`   TINYINT      NOT NULL                COMMENT '任务分类（冗余）',
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
    KEY `idx_task_id` (`task_id`) USING HASH,
    KEY `idx_task_type` (`task_type`, `task_date`),
    KEY `idx_completed_rewarded` (`is_completed`, `is_rewarded`),
    KEY `idx_complete_time` (`complete_time` DESC)
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
    `reward_source` TINYINT      NOT NULL DEFAULT 1      COMMENT '奖励来源：1-task, 2-event, 3-system, 4-admin',
    
    -- 奖励信息
    `reward_type`   TINYINT      NOT NULL                COMMENT '奖励类型：1-coin, 2-item, 3-vip, 4-experience, 5-badge',
    `reward_name`   VARCHAR(60)  NOT NULL                COMMENT '奖励名称',
    `reward_amount` INT          NOT NULL                COMMENT '奖励数量',
    `reward_data`   JSON                                 COMMENT '奖励扩展数据',
    
    -- 发放状态
    `status`        TINYINT      NOT NULL DEFAULT 1      COMMENT '状态：1-pending, 2-success, 3-failed, 4-expired',
    `grant_time`    TIMESTAMP    NULL                    COMMENT '发放时间',
    `expire_time`   TIMESTAMP    NULL                    COMMENT '过期时间',
    
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_task_record` (`task_record_id`) USING HASH,
    KEY `idx_reward_type` (`reward_type`) USING HASH,
    KEY `idx_status` (`status`) USING HASH,
    KEY `idx_reward_source` (`reward_source`, `reward_type`),
    KEY `idx_grant_time` (`grant_time` DESC),
    KEY `idx_create_time` (`create_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户奖励记录表';

-- ==========================================
-- 钱包系统集成说明
-- ==========================================

-- 本任务系统与用户钱包系统完全集成，使用 t_user_wallet 表统一管理资产
-- 钱包表字段说明：
-- - balance: 现金余额（DECIMAL，支持充值购买）  
-- - coin_balance: 金币余额（BIGINT，任务奖励虚拟货币）
-- - coin_total_earned: 累计获得金币（任务奖励统计）
-- - coin_total_spent: 累计消费金币（商城消费统计）
-- - frozen_amount: 冻结金额（风控使用）
-- - total_income: 总收入（现金统计）
-- - total_expense: 总支出（现金统计）
-- - status: 钱包状态（active、frozen）

-- ==========================================
-- 初始化任务模板数据
-- ==========================================

-- 每日任务模板
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('每日登录', '每日登录获得金币奖励', 1, 1, 1, 1, 1),
('发布内容', '发布1篇内容获得奖励', 1, 2, 2, 1, 2),
('点赞互动', '为其他用户内容点赞5次', 1, 3, 3, 5, 3),
('评论互动', '发表3条有效评论', 1, 3, 4, 3, 4),
('分享内容', '分享1次内容到社交平台', 1, 3, 5, 1, 5),
('邀请好友', '邀请1位好友注册', 1, 5, 7, 1, 6);

-- 周常任务模板
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('周活跃用户', '连续登录7天', 2, 1, 1, 7, 10),
('内容创作者', '本周发布5篇内容', 2, 2, 2, 5, 11),
('社交达人', '本周点赞50次', 2, 3, 3, 50, 12),
('消费体验', '本周购买商品1次', 2, 4, 6, 1, 13);

-- 月度任务模板  
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('月度活跃', '本月登录20天', 3, 1, 1, 20, 20),
('创作达人', '本月发布20篇内容', 3, 2, 2, 20, 21),
('邀请达人', '本月邀请5位好友', 3, 5, 7, 5, 22);

-- ==========================================
-- 初始化任务奖励配置
-- ==========================================

-- 每日任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `is_main_reward`) VALUES
-- 每日登录奖励
(1, 1, '金币', '每日登录奖励', 10, 1),
-- 发布内容奖励
(2, 1, '金币', '发布内容奖励', 20, 1),
(2, 4, '经验值', '创作经验', 5, 0),
-- 点赞互动奖励
(3, 1, '金币', '社交互动奖励', 15, 1),
-- 评论互动奖励
(4, 1, '金币', '评论互动奖励', 12, 1),
-- 分享内容奖励
(5, 1, '金币', '分享内容奖励', 8, 1),
-- 邀请好友奖励
(6, 1, '金币', '邀请好友奖励', 50, 1),
(6, 5, '邀请达人', '邀请达人徽章', 1, 0);

-- 周常任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
-- 周活跃用户奖励
(7, 1, '金币', '周活跃奖励', 100, NULL, 1),
(7, 2, '活跃宝箱', '随机道具宝箱', 1, JSON_OBJECT('item_id', 1001, 'item_type', 'treasure_box'), 0),
-- 内容创作者奖励
(8, 1, '金币', '创作者奖励', 150, NULL, 1),
(8, 3, 'VIP体验', 'VIP功能体验3天', 3, JSON_OBJECT('vip_type', 'premium', 'duration_days', 3), 0),
-- 社交达人奖励
(9, 1, '金币', '社交达人奖励', 80, NULL, 1),
(9, 5, '社交达人', '社交达人徽章', 1, NULL, 0),
-- 消费体验奖励
(10, 1, '金币', '消费体验奖励', 60, NULL, 1);

-- 月度任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
-- 月度活跃奖励
(11, 1, '金币', '月度活跃奖励', 500, NULL, 1),
(11, 3, 'VIP月卡', 'VIP功能体验30天', 30, JSON_OBJECT('vip_type', 'premium', 'duration_days', 30), 1),
-- 创作达人奖励
(12, 1, '金币', '创作达人奖励', 800, NULL, 1),
(12, 5, '创作达人', '创作达人徽章', 1, NULL, 0),
-- 邀请达人奖励
(13, 1, '金币', '邀请达人奖励', 1000, NULL, 1),
(13, 5, '超级邀请达人', '超级邀请达人徽章', 1, NULL, 0);

-- ==========================================
-- 常用查询示例（已优化为数字常量）
-- ==========================================

-- 1. 获取用户今日任务列表
-- SELECT 
--     t.id as task_id,
--     t.task_name,
--     t.task_desc,
--     t.task_type,
--     t.target_count,
--     COALESCE(r.current_count, 0) as current_count,
--     COALESCE(r.is_completed, 0) as is_completed,
--     COALESCE(r.is_rewarded, 0) as is_rewarded
-- FROM t_task_template t
-- LEFT JOIN t_user_task_record r ON t.id = r.task_id 
--     AND r.user_id = #{userId} 
--     AND r.task_date = CURDATE()
-- WHERE t.task_type = 1  -- 1=daily
--   AND t.is_active = 1
-- ORDER BY t.sort_order;

-- 2. 获取用户任务进度统计
-- SELECT 
--     task_type,
--     CASE task_type 
--         WHEN 1 THEN '每日任务'
--         WHEN 2 THEN '周常任务'
--         WHEN 3 THEN '月度任务'
--         WHEN 4 THEN '成就任务'
--     END as task_type_name,
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
--     CASE reward_type 
--         WHEN 1 THEN '金币'
--         WHEN 2 THEN '道具'
--         WHEN 3 THEN 'VIP'
--         WHEN 4 THEN '经验'
--         WHEN 5 THEN '徽章'
--     END as reward_type_name,
--     reward_name,
--     reward_amount,
--     status,
--     create_time
-- FROM t_user_reward_record 
-- WHERE user_id = #{userId}
-- ORDER BY create_time DESC
-- LIMIT 20;

-- 4. 获取用户金币排行榜（与钱包系统集成）
-- SELECT 
--     u.username,
--     u.id as user_id,
--     w.coin_balance,
--     w.coin_total_earned,
--     RANK() OVER (ORDER BY w.coin_balance DESC) as coin_rank
-- FROM t_user u 
-- JOIN t_user_wallet w ON u.id = w.user_id 
-- WHERE u.status = 1  -- 1=active
--   AND w.status = 'active'
-- ORDER BY w.coin_balance DESC 
-- LIMIT 50;

-- ==========================================
-- 存储过程（任务进度更新）
-- ==========================================

DELIMITER $$

-- 更新用户任务进度（已优化为数字常量）
DROP PROCEDURE IF EXISTS `update_user_task_progress`$$
CREATE PROCEDURE `update_user_task_progress`(
    IN p_user_id BIGINT,
    IN p_task_action TINYINT,  -- 改为数字常量
    IN p_increment INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_task_id BIGINT;
    DECLARE v_task_name VARCHAR(60);
    DECLARE v_task_type TINYINT;      -- 改为数字类型
    DECLARE v_task_category TINYINT;  -- 改为数字类型
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

-- 任务完成后自动记录奖励并发放金币（已优化为数字常量）
DROP TRIGGER IF EXISTS `tr_task_complete_reward`;
DELIMITER $$
CREATE TRIGGER `tr_task_complete_reward`
AFTER UPDATE ON `t_user_task_record`
FOR EACH ROW
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_reward_type TINYINT;     -- 改为数字类型
    DECLARE v_reward_amount INT;
    DECLARE v_reward_cursor CURSOR FOR 
        SELECT reward_type, reward_amount 
        FROM t_task_reward 
        WHERE task_id = NEW.task_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 如果任务刚完成且未发放奖励
    IF NEW.is_completed = 1 AND OLD.is_completed = 0 AND NEW.is_rewarded = 0 THEN
        -- 创建奖励记录
        INSERT INTO t_user_reward_record (
            user_id, task_record_id, reward_source, reward_type, 
            reward_name, reward_amount, reward_data, status
        )
        SELECT 
            NEW.user_id, NEW.id, 1, tr.reward_type,    -- 1=task
            tr.reward_name, tr.reward_amount, tr.reward_data, 1  -- 1=pending
        FROM t_task_reward tr
        WHERE tr.task_id = NEW.task_id;
        
        -- 自动发放金币奖励
        OPEN v_reward_cursor;
        reward_loop: LOOP
            FETCH v_reward_cursor INTO v_reward_type, v_reward_amount;
            IF done THEN
                LEAVE reward_loop;
            END IF;
            
            -- 如果是金币奖励，直接发放到钱包
            IF v_reward_type = 1 THEN  -- 1=coin
                CALL grant_coin_reward(NEW.user_id, v_reward_amount, 'task_reward');
                
                -- 更新奖励记录状态为已发放
                UPDATE t_user_reward_record 
                SET status = 2, grant_time = NOW()  -- 2=success
                WHERE task_record_id = NEW.id AND reward_type = 1;  -- 1=coin
            END IF;
        END LOOP;
        CLOSE v_reward_cursor;
        
        -- 标记任务奖励已发放
        UPDATE t_user_task_record 
        SET is_rewarded = 1, reward_time = NOW()
        WHERE id = NEW.id;
    END IF;
END$$
DELIMITER ;

-- ==========================================
-- 索引优化说明
-- ==========================================

-- 1. 主键和唯一索引：
--    - PRIMARY KEY: 所有表的主键索引
--    - uk_user_task_date: 确保用户每日任务唯一性

-- 2. HASH索引（精确查询优化）：
--    - idx_task_action: 任务动作精确匹配
--    - idx_reward_type: 奖励类型精确匹配  
--    - idx_task_record: 任务记录关联查询
--    - idx_status: 状态精确匹配

-- 3. 复合索引（组合查询优化）：
--    - idx_active_sort: 启用状态+排序
--    - idx_date_range: 日期范围查询
--    - idx_task_type: 任务类型+日期组合
--    - idx_completed_rewarded: 完成状态+奖励状态
--    - idx_reward_source: 奖励来源+类型组合

-- 4. 降序索引（排行榜优化）：
--    - idx_complete_time: 完成时间降序
--    - idx_grant_time: 发放时间降序
--    - idx_create_time: 创建时间降序

-- ==========================================
-- 优化特性说明
-- ==========================================

-- 1. 数据类型优化：
--    - 枚举字段使用 TINYINT 替代 VARCHAR，节省存储空间
--    - 字段长度精准控制，避免空间浪费
--    - 数字常量提高查询效率和类型安全

-- 2. 索引优化：
--    - HASH索引用于精确匹配，B-Tree用于范围查询
--    - 复合索引覆盖高频查询场景
--    - 降序索引优化排行榜和时间序列查询

-- 3. 任务系统扩展：
--    - 支持每日、周常、月度、成就等多种任务类型
--    - 新增邀请、消费等任务分类和动作
--    - 灵活的任务配置和奖励机制

-- 4. 奖励系统完善：
--    - 支持金币、道具、VIP、经验、徽章等多种奖励
--    - JSON扩展字段支持复杂奖励配置
--    - 自动发放机制确保奖励及时到账

-- 5. 性能优化：
--    - 数据类型一致性避免类型转换开销
--    - 冗余字段减少JOIN查询
--    - 存储过程和触发器自动化处理

-- 6. 钱包系统深度集成：
--    - 完全兼容用户钱包表结构
--    - 金币奖励自动同步，支持实时消费
--    - 统一资产管理，避免数据不一致

-- ==========================================
-- 使用示例（优化版）
-- ==========================================

-- 1. 模拟用户完成各种任务：
--    -- 登录任务 (task_action=1)
--    CALL update_user_task_progress(123, 1, 1);
--    
--    -- 发布内容任务 (task_action=2)  
--    CALL update_user_task_progress(123, 2, 1);
--    
--    -- 点赞任务 (task_action=3)
--    CALL update_user_task_progress(123, 3, 1);
--    
--    -- 邀请好友任务 (task_action=7)
--    CALL update_user_task_progress(123, 7, 1);

-- 2. 查看用户任务完成情况和金币余额：
--    SELECT u.username, u.id, w.coin_balance, w.coin_total_earned,
--           (SELECT COUNT(*) FROM t_user_task_record 
--            WHERE user_id = u.id AND is_completed = 1 
--            AND task_date = CURDATE()) as today_completed_tasks
--    FROM t_user u 
--    JOIN t_user_wallet w ON u.id = w.user_id 
--    WHERE u.id = 123;

-- 3. 查看今日任务奖励记录：
--    SELECT 
--        CASE reward_type 
--            WHEN 1 THEN '金币' WHEN 2 THEN '道具' 
--            WHEN 3 THEN 'VIP' WHEN 4 THEN '经验' 
--            WHEN 5 THEN '徽章' 
--        END as reward_type_name,
--        reward_name, reward_amount, 
--        CASE status 
--            WHEN 1 THEN '待发放' WHEN 2 THEN '已发放' 
--            WHEN 3 THEN '失败' WHEN 4 THEN '已过期' 
--        END as status_name,
--        grant_time
--    FROM t_user_reward_record 
--    WHERE user_id = 123 
--      AND DATE(create_time) = CURDATE()
--    ORDER BY create_time DESC;