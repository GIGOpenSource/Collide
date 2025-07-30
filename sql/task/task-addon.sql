-- ==========================================
-- Collide 每日任务系统 - 增量安装脚本
-- 用于在现有 collide-simple-all.sql 基础上添加任务系统
-- 可直接执行此脚本来扩展现有数据库
-- ==========================================

USE collide;

-- ==========================================
-- 检查并创建任务模板表
-- ==========================================

SET @table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_task_template'
);

SET @sql = IF(@table_exists = 0, 
    'CREATE TABLE `t_task_template` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''任务模板ID'',
        `task_name`       VARCHAR(100) NOT NULL                COMMENT ''任务名称'',
        `task_desc`       VARCHAR(500) NOT NULL                COMMENT ''任务描述'',
        `task_type`       VARCHAR(50)  NOT NULL                COMMENT ''任务类型：daily、weekly、achievement'',
        `task_category`   VARCHAR(50)  NOT NULL                COMMENT ''任务分类：login、content、social、consume'',
        `task_action`     VARCHAR(50)  NOT NULL                COMMENT ''任务动作：login、publish_content、like、comment、share、purchase'',
        `target_count`    INT          NOT NULL DEFAULT 1      COMMENT ''目标完成次数'',
        `sort_order`      INT          NOT NULL DEFAULT 0      COMMENT ''排序值'',
        `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT ''是否启用'',
        `start_date`      DATE         NULL                    COMMENT ''任务开始日期'',
        `end_date`        DATE         NULL                    COMMENT ''任务结束日期'',
        `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        KEY `idx_task_type` (`task_type`),
        KEY `idx_task_category` (`task_category`),
        KEY `idx_active_sort` (`is_active`, `sort_order`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''任务模板表'';',
    'SELECT ''Table t_task_template already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建任务奖励表
-- ==========================================

SET @reward_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_task_reward'
);

SET @sql = IF(@reward_table_exists = 0,
    'CREATE TABLE `t_task_reward` (
        `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''奖励ID'',
        `task_id`       BIGINT       NOT NULL                COMMENT ''任务模板ID'',
        `reward_type`   VARCHAR(50)  NOT NULL                COMMENT ''奖励类型：coin、item、vip、experience'',
        `reward_name`   VARCHAR(100) NOT NULL                COMMENT ''奖励名称'',
        `reward_desc`   VARCHAR(500)                         COMMENT ''奖励描述'',
        `reward_amount` INT          NOT NULL DEFAULT 1      COMMENT ''奖励数量'',
        `reward_data`   JSON                                 COMMENT ''奖励扩展数据（商品信息等）'',
        `is_main_reward` TINYINT(1)  NOT NULL DEFAULT 1      COMMENT ''是否主要奖励'',
        `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        KEY `idx_task_id` (`task_id`),
        KEY `idx_reward_type` (`reward_type`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''任务奖励配置表'';',
    'SELECT ''Table t_task_reward already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建用户任务记录表
-- ==========================================

SET @record_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user_task_record'
);

SET @sql = IF(@record_table_exists = 0,
    'CREATE TABLE `t_user_task_record` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''记录ID'',
        `user_id`         BIGINT       NOT NULL                COMMENT ''用户ID'',
        `task_id`         BIGINT       NOT NULL                COMMENT ''任务模板ID'',
        `task_date`       DATE         NOT NULL                COMMENT ''任务日期（用于每日任务）'',
        `task_name`       VARCHAR(100) NOT NULL                COMMENT ''任务名称（冗余）'',
        `task_type`       VARCHAR(50)  NOT NULL                COMMENT ''任务类型（冗余）'',
        `task_category`   VARCHAR(50)  NOT NULL                COMMENT ''任务分类（冗余）'',
        `target_count`    INT          NOT NULL                COMMENT ''目标完成次数（冗余）'',
        `current_count`   INT          NOT NULL DEFAULT 0      COMMENT ''当前完成次数'',
        `is_completed`    TINYINT(1)   NOT NULL DEFAULT 0      COMMENT ''是否已完成'',
        `is_rewarded`     TINYINT(1)   NOT NULL DEFAULT 0      COMMENT ''是否已领取奖励'',
        `complete_time`   TIMESTAMP    NULL                    COMMENT ''完成时间'',
        `reward_time`     TIMESTAMP    NULL                    COMMENT ''奖励领取时间'',
        `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `task_date`),
        KEY `idx_user_date` (`user_id`, `task_date`),
        KEY `idx_task_id` (`task_id`),
        KEY `idx_completed` (`is_completed`),
        KEY `idx_rewarded` (`is_rewarded`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''用户任务记录表'';',
    'SELECT ''Table t_user_task_record already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建用户奖励记录表
-- ==========================================

SET @user_reward_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user_reward_record'
);

SET @sql = IF(@user_reward_table_exists = 0,
    'CREATE TABLE `t_user_reward_record` (
        `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''奖励记录ID'',
        `user_id`       BIGINT       NOT NULL                COMMENT ''用户ID'',
        `task_record_id` BIGINT      NOT NULL                COMMENT ''任务记录ID'',
        `reward_source` VARCHAR(50)  NOT NULL DEFAULT ''task'' COMMENT ''奖励来源：task、event、system'',
        `reward_type`   VARCHAR(50)  NOT NULL                COMMENT ''奖励类型：coin、item、vip、experience'',
        `reward_name`   VARCHAR(100) NOT NULL                COMMENT ''奖励名称'',
        `reward_amount` INT          NOT NULL                COMMENT ''奖励数量'',
        `reward_data`   JSON                                 COMMENT ''奖励扩展数据'',
        `status`        VARCHAR(20)  NOT NULL DEFAULT ''pending'' COMMENT ''状态：pending、success、failed'',
        `grant_time`    TIMESTAMP    NULL                    COMMENT ''发放时间'',
        `expire_time`   TIMESTAMP    NULL                    COMMENT ''过期时间'',
        `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        KEY `idx_user_id` (`user_id`),
        KEY `idx_task_record` (`task_record_id`),
        KEY `idx_reward_type` (`reward_type`),
        KEY `idx_status` (`status`),
        KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''用户奖励记录表'';',
    'SELECT ''Table t_user_reward_record already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建用户金币账户表
-- ==========================================

SET @coin_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user_coin_account'
);

SET @sql = IF(@coin_table_exists = 0,
    'CREATE TABLE `t_user_coin_account` (
        `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''账户ID'',
        `user_id`       BIGINT       NOT NULL                COMMENT ''用户ID'',
        `total_coin`    BIGINT       NOT NULL DEFAULT 0      COMMENT ''总金币数'',
        `available_coin` BIGINT      NOT NULL DEFAULT 0      COMMENT ''可用金币数'',
        `frozen_coin`   BIGINT       NOT NULL DEFAULT 0      COMMENT ''冻结金币数'',
        `total_earned`  BIGINT       NOT NULL DEFAULT 0      COMMENT ''累计获得金币'',
        `total_spent`   BIGINT       NOT NULL DEFAULT 0      COMMENT ''累计消费金币'',
        `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''用户金币账户表'';',
    'SELECT ''Table t_user_coin_account already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并扩展用户表（添加任务相关字段）
-- ==========================================

-- 检查 t_user 表是否存在 task_count 字段
SET @task_count_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user' 
    AND column_name = 'task_count'
);

SET @sql = IF(@task_count_exists = 0,
    'ALTER TABLE `t_user` ADD COLUMN `task_count` INT NOT NULL DEFAULT 0 COMMENT ''完成任务数'' AFTER `like_count`;',
    'SELECT ''Column task_count already exists in t_user'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 coin_balance 字段
SET @coin_balance_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user' 
    AND column_name = 'coin_balance'
);

SET @sql = IF(@coin_balance_exists = 0,
    'ALTER TABLE `t_user` ADD COLUMN `coin_balance` BIGINT NOT NULL DEFAULT 0 COMMENT ''金币余额'' AFTER `task_count`;',
    'SELECT ''Column coin_balance already exists in t_user'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 插入默认任务模板数据
-- ==========================================

-- 检查是否已有任务模板数据
SET @template_count = (
    SELECT COUNT(*) FROM t_task_template
);

-- 只有在没有数据时才插入
SET @sql = IF(@template_count = 0,
    'INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
    (''每日登录'', ''每日登录获得金币奖励'', ''daily'', ''login'', ''login'', 1, 1),
    (''发布内容'', ''发布1篇内容获得奖励'', ''daily'', ''content'', ''publish_content'', 1, 2),
    (''点赞互动'', ''为其他用户内容点赞5次'', ''daily'', ''social'', ''like'', 5, 3),
    (''评论互动'', ''发表3条有效评论'', ''daily'', ''social'', ''comment'', 3, 4),
    (''分享内容'', ''分享1次内容到社交平台'', ''daily'', ''social'', ''share'', 1, 5),
    (''阅读文章'', ''阅读10篇文章'', ''daily'', ''content'', ''read_content'', 10, 6),
    (''收藏内容'', ''收藏3篇优质内容'', ''daily'', ''content'', ''favorite'', 3, 7),
    (''周活跃用户'', ''连续登录7天'', ''weekly'', ''login'', ''login'', 7, 10),
    (''内容创作者'', ''本周发布5篇内容'', ''weekly'', ''content'', ''publish_content'', 5, 11),
    (''社交达人'', ''本周获得50个点赞'', ''weekly'', ''social'', ''like_received'', 50, 12),
    (''新手上路'', ''完成首次发布内容'', ''achievement'', ''content'', ''first_publish'', 1, 100),
    (''社交新星'', ''获得100个点赞'', ''achievement'', ''social'', ''like_received'', 100, 101),
    (''忠实用户'', ''连续登录30天'', ''achievement'', ''login'', ''login_streak'', 30, 102);',
    'SELECT ''Task templates already exist'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 插入默认奖励配置数据
-- ==========================================

-- 检查是否已有奖励配置数据
SET @reward_count = (
    SELECT COUNT(*) FROM t_task_reward
);

-- 只有在没有数据时才插入
SET @sql = IF(@reward_count = 0,
    'INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
    (1, ''coin'', ''金币'', ''每日登录奖励'', 10, NULL, 1),
    (2, ''coin'', ''金币'', ''发布内容奖励'', 20, NULL, 1),
    (2, ''experience'', ''经验值'', ''创作经验'', 5, NULL, 0),
    (3, ''coin'', ''金币'', ''社交互动奖励'', 15, NULL, 1),
    (4, ''coin'', ''金币'', ''评论互动奖励'', 12, NULL, 1),
    (5, ''coin'', ''金币'', ''分享内容奖励'', 8, NULL, 1),
    (6, ''coin'', ''金币'', ''阅读奖励'', 8, NULL, 1),
    (7, ''coin'', ''金币'', ''收藏奖励'', 6, NULL, 1),
    (8, ''coin'', ''金币'', ''周活跃奖励'', 100, NULL, 1),
    (8, ''item'', ''活跃宝箱'', ''随机道具宝箱'', 1, JSON_OBJECT(''item_id'', 1001, ''item_type'', ''treasure_box''), 0),
    (9, ''coin'', ''金币'', ''创作者奖励'', 150, NULL, 1),
    (10, ''coin'', ''金币'', ''社交达人奖励'', 80, NULL, 1),
    (11, ''coin'', ''金币'', ''新手礼包'', 50, NULL, 1),
    (12, ''coin'', ''金币'', ''社交新星奖励'', 200, NULL, 1),
    (13, ''coin'', ''金币'', ''忠实用户奖励'', 500, NULL, 1);',
    'SELECT ''Task rewards already exist'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 创建存储过程
-- ==========================================

DELIMITER $$

-- 更新用户任务进度的存储过程
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
    
    DECLARE task_cursor CURSOR FOR 
        SELECT id, task_name, task_type, task_category, target_count
        FROM t_task_template 
        WHERE task_action = p_task_action 
          AND is_active = 1
          AND (start_date IS NULL OR start_date <= CURDATE())
          AND (end_date IS NULL OR end_date >= CURDATE());
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN task_cursor;
    
    read_loop: LOOP
        FETCH task_cursor INTO v_task_id, v_task_name, v_task_type, v_task_category, v_target_count;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
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
    
    CLOSE task_cursor;
END$$

-- 初始化用户金币账户的存储过程
DROP PROCEDURE IF EXISTS `init_user_coin_account`$$
CREATE PROCEDURE `init_user_coin_account`(IN p_user_id BIGINT)
BEGIN
    INSERT IGNORE INTO t_user_coin_account (user_id)
    VALUES (p_user_id);
END$$

-- 更新用户金币余额的存储过程
DROP PROCEDURE IF EXISTS `update_user_coin_balance`$$
CREATE PROCEDURE `update_user_coin_balance`(
    IN p_user_id BIGINT,
    IN p_amount BIGINT,
    IN p_operation VARCHAR(10) -- 'add' 或 'subtract'
)
BEGIN
    -- 确保用户有金币账户
    CALL init_user_coin_account(p_user_id);
    
    -- 更新金币余额
    IF p_operation = 'add' THEN
        UPDATE t_user_coin_account 
        SET total_coin = total_coin + p_amount,
            available_coin = available_coin + p_amount,
            total_earned = total_earned + p_amount
        WHERE user_id = p_user_id;
        
        -- 同时更新用户表的冗余字段
        UPDATE t_user 
        SET coin_balance = coin_balance + p_amount
        WHERE id = p_user_id;
    ELSE
        UPDATE t_user_coin_account 
        SET available_coin = available_coin - p_amount,
            total_spent = total_spent + p_amount
        WHERE user_id = p_user_id AND available_coin >= p_amount;
        
        -- 同时更新用户表的冗余字段
        UPDATE t_user 
        SET coin_balance = coin_balance - p_amount
        WHERE id = p_user_id AND coin_balance >= p_amount;
    END IF;
END$$

DELIMITER ;

-- ==========================================
-- 创建触发器
-- ==========================================

-- 任务完成后自动记录奖励
DROP TRIGGER IF EXISTS `tr_task_complete_reward`;
DELIMITER $$
CREATE TRIGGER `tr_task_complete_reward`
AFTER UPDATE ON `t_user_task_record`
FOR EACH ROW
BEGIN
    IF NEW.is_completed = 1 AND OLD.is_completed = 0 AND NEW.is_rewarded = 0 THEN
        INSERT INTO t_user_reward_record (
            user_id, task_record_id, reward_source, reward_type, 
            reward_name, reward_amount, reward_data, status
        )
        SELECT 
            NEW.user_id, NEW.id, 'task', tr.reward_type,
            tr.reward_name, tr.reward_amount, tr.reward_data, 'pending'
        FROM t_task_reward tr
        WHERE tr.task_id = NEW.task_id;
        
        -- 更新用户表的任务完成数
        UPDATE t_user 
        SET task_count = task_count + 1
        WHERE id = NEW.user_id;
    END IF;
END$$
DELIMITER ;

-- 奖励发放成功后更新金币账户
DROP TRIGGER IF EXISTS `tr_reward_grant_success`;
DELIMITER $$
CREATE TRIGGER `tr_reward_grant_success`
AFTER UPDATE ON `t_user_reward_record`
FOR EACH ROW
BEGIN
    -- 如果是金币奖励且刚发放成功
    IF NEW.reward_type = 'coin' AND NEW.status = 'success' AND OLD.status = 'pending' THEN
        CALL update_user_coin_balance(NEW.user_id, NEW.reward_amount, 'add');
    END IF;
END$$
DELIMITER ;

-- ==========================================
-- 为现有用户初始化金币账户
-- ==========================================

-- 为现有用户创建金币账户
INSERT IGNORE INTO t_user_coin_account (user_id, total_coin, available_coin)
SELECT id, COALESCE(coin_balance, 0), COALESCE(coin_balance, 0)
FROM t_user 
WHERE status = 'active';

-- ==========================================
-- 创建视图（可选）
-- ==========================================

-- 用户任务进度视图
CREATE OR REPLACE VIEW v_user_task_progress AS
SELECT 
    r.user_id,
    r.task_date,
    t.task_name,
    t.task_type,
    t.task_category,
    r.target_count,
    r.current_count,
    r.is_completed,
    r.is_rewarded,
    ROUND(r.current_count * 100.0 / r.target_count, 1) as progress_percent,
    r.complete_time,
    r.reward_time
FROM t_user_task_record r
JOIN t_task_template t ON r.task_id = t.id;

-- 用户奖励统计视图
CREATE OR REPLACE VIEW v_user_reward_stats AS
SELECT 
    user_id,
    reward_type,
    SUM(reward_amount) as total_amount,
    COUNT(*) as reward_count,
    COUNT(CASE WHEN status = 'success' THEN 1 END) as success_count
FROM t_user_reward_record
GROUP BY user_id, reward_type;

-- ==========================================
-- 完成提示
-- ==========================================

SELECT '每日任务系统安装完成！' as message,
       '已创建表：t_task_template, t_task_reward, t_user_task_record, t_user_reward_record, t_user_coin_account' as tables_created,
       '已添加字段：t_user.task_count, t_user.coin_balance' as fields_added,
       '已创建存储过程、触发器和视图' as procedures_created,
       '已初始化基础任务模板和奖励配置' as data_initialized;

-- ==========================================
-- 验证安装
-- ==========================================

SELECT 
    table_name,
    table_comment,
    table_rows
FROM information_schema.tables 
WHERE table_schema = 'collide' 
  AND table_name IN ('t_task_template', 't_task_reward', 't_user_task_record', 't_user_reward_record', 't_user_coin_account')
ORDER BY table_name;

-- 显示任务模板统计
SELECT 
    task_type,
    COUNT(*) as template_count
FROM t_task_template 
WHERE is_active = 1
GROUP BY task_type;

-- 显示奖励配置统计
SELECT 
    reward_type,
    COUNT(*) as reward_count,
    SUM(reward_amount) as total_amount
FROM t_task_reward
GROUP BY reward_type;