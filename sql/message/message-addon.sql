-- ==========================================
-- Collide 私信留言板模块 - 增量更新脚本
-- 用于在现有 collide-simple-all.sql 基础上添加私信功能
-- 可直接执行此脚本来扩展现有数据库
-- ==========================================

USE collide;

-- ==========================================
-- 检查并添加私信模块表
-- ==========================================

-- 检查 t_message 表是否存在
SET @table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_message'
);

-- 如果表不存在则创建
SET @sql = IF(@table_exists = 0, 
    'CREATE TABLE `t_message` (
        `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''消息ID'',
        `sender_id`     BIGINT       NOT NULL                COMMENT ''发送者ID'',
        `receiver_id`   BIGINT       NOT NULL                COMMENT ''接收者ID'',
        `content`       TEXT         NOT NULL                COMMENT ''消息内容'',
        `message_type`  VARCHAR(20)  NOT NULL DEFAULT ''text'' COMMENT ''消息类型：text、image、file、system'',
        `extra_data`    JSON                                 COMMENT ''扩展数据（图片URL、文件信息等）'',
        `status`        VARCHAR(20)  NOT NULL DEFAULT ''sent'' COMMENT ''消息状态：sent、delivered、read、deleted'',
        `read_time`     TIMESTAMP    NULL                    COMMENT ''已读时间'',
        `reply_to_id`   BIGINT       NULL                    COMMENT ''回复的消息ID（引用消息）'',
        `is_pinned`     TINYINT(1)   NOT NULL DEFAULT 0     COMMENT ''是否置顶（留言板功能）'',
        `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        KEY `idx_sender_receiver` (`sender_id`, `receiver_id`),
        KEY `idx_receiver_status` (`receiver_id`, `status`),
        KEY `idx_create_time` (`create_time`),
        KEY `idx_reply_to` (`reply_to_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''私信消息表'';',
    'SELECT ''Table t_message already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 创建会话统计表
-- ==========================================

SET @session_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_message_session'
);

SET @sql = IF(@session_table_exists = 0,
    'CREATE TABLE `t_message_session` (
        `id`                BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''会话ID'',
        `user_id`           BIGINT       NOT NULL                COMMENT ''用户ID'',
        `other_user_id`     BIGINT       NOT NULL                COMMENT ''对方用户ID'',
        `last_message_id`   BIGINT       NULL                    COMMENT ''最后一条消息ID'',
        `last_message_time` TIMESTAMP    NULL                    COMMENT ''最后消息时间'',
        `unread_count`      INT          NOT NULL DEFAULT 0     COMMENT ''未读消息数'',
        `is_archived`       TINYINT(1)   NOT NULL DEFAULT 0     COMMENT ''是否归档'',
        `create_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk_user_other` (`user_id`, `other_user_id`),
        KEY `idx_user_time` (`user_id`, `last_message_time`),
        KEY `idx_last_message` (`last_message_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''用户会话统计表'';',
    'SELECT ''Table t_message_session already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 创建消息设置表
-- ==========================================

SET @setting_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_message_setting'
);

SET @sql = IF(@setting_table_exists = 0,
    'CREATE TABLE `t_message_setting` (
        `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''设置ID'',
        `user_id`               BIGINT       NOT NULL                COMMENT ''用户ID'',
        `allow_stranger_msg`    TINYINT(1)   NOT NULL DEFAULT 1     COMMENT ''是否允许陌生人发消息'',
        `auto_read_receipt`     TINYINT(1)   NOT NULL DEFAULT 1     COMMENT ''是否自动发送已读回执'',
        `message_notification`  TINYINT(1)   NOT NULL DEFAULT 1     COMMENT ''是否开启消息通知'',
        `create_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk_user_id` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''用户消息设置表'';',
    'SELECT ''Table t_message_setting already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并更新用户表（添加消息相关字段）
-- ==========================================

-- 检查 t_user 表是否存在 message_count 字段
SET @message_count_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user' 
    AND column_name = 'message_count'
);

-- 如果字段不存在则添加
SET @sql = IF(@message_count_exists = 0,
    'ALTER TABLE `t_user` ADD COLUMN `message_count` BIGINT NOT NULL DEFAULT 0 COMMENT ''发送消息数'' AFTER `like_count`;',
    'SELECT ''Column message_count already exists in t_user'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查并添加 unread_message_count 字段
SET @unread_count_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns 
    WHERE table_schema = 'collide' 
    AND table_name = 't_user' 
    AND column_name = 'unread_message_count'
);

SET @sql = IF(@unread_count_exists = 0,
    'ALTER TABLE `t_user` ADD COLUMN `unread_message_count` INT NOT NULL DEFAULT 0 COMMENT ''未读消息数'' AFTER `message_count`;',
    'SELECT ''Column unread_message_count already exists in t_user'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 插入初始化数据
-- ==========================================

-- 插入系统欢迎消息（如果不存在）
INSERT IGNORE INTO `t_message` (`id`, `sender_id`, `receiver_id`, `content`, `message_type`, `status`)
VALUES (1, 0, 0, '欢迎使用Collide私信功能！', 'system', 'read');

-- ==========================================
-- 创建存储过程（用于消息统计更新）
-- ==========================================

DELIMITER $$

-- 更新用户消息统计的存储过程
DROP PROCEDURE IF EXISTS `update_user_message_stats`$$
CREATE PROCEDURE `update_user_message_stats`(IN user_id BIGINT)
BEGIN
    DECLARE message_count INT DEFAULT 0;
    DECLARE unread_count INT DEFAULT 0;
    
    -- 计算发送的消息数
    SELECT COUNT(*) INTO message_count 
    FROM t_message 
    WHERE sender_id = user_id AND message_type != 'system';
    
    -- 计算未读消息数
    SELECT COUNT(*) INTO unread_count 
    FROM t_message 
    WHERE receiver_id = user_id AND status != 'read' AND message_type != 'system';
    
    -- 更新用户表
    UPDATE t_user 
    SET message_count = message_count, 
        unread_message_count = unread_count
    WHERE id = user_id;
END$$

-- 更新所有用户消息统计的存储过程
DROP PROCEDURE IF EXISTS `update_all_user_message_stats`$$
CREATE PROCEDURE `update_all_user_message_stats`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE user_id BIGINT;
    
    -- 定义游标
    DECLARE user_cursor CURSOR FOR 
        SELECT id FROM t_user WHERE status = 'active';
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 打开游标
    OPEN user_cursor;
    
    read_loop: LOOP
        FETCH user_cursor INTO user_id;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 调用单个用户统计更新
        CALL update_user_message_stats(user_id);
    END LOOP;
    
    -- 关闭游标
    CLOSE user_cursor;
END$$

DELIMITER ;

-- ==========================================
-- 创建触发器（自动维护统计数据）
-- ==========================================

-- 消息插入后更新统计
DROP TRIGGER IF EXISTS `tr_message_after_insert`;
DELIMITER $$
CREATE TRIGGER `tr_message_after_insert`
AFTER INSERT ON `t_message`
FOR EACH ROW
BEGIN
    -- 更新发送者的消息数（排除系统消息）
    IF NEW.sender_id > 0 THEN
        UPDATE t_user 
        SET message_count = message_count + 1 
        WHERE id = NEW.sender_id;
    END IF;
    
    -- 更新接收者的未读消息数（排除系统消息）
    IF NEW.receiver_id > 0 AND NEW.status != 'read' THEN
        UPDATE t_user 
        SET unread_message_count = unread_message_count + 1 
        WHERE id = NEW.receiver_id;
    END IF;
    
    -- 更新或插入会话记录
    INSERT INTO t_message_session (user_id, other_user_id, last_message_id, last_message_time, unread_count)
    VALUES (NEW.receiver_id, NEW.sender_id, NEW.id, NEW.create_time, 
            CASE WHEN NEW.status != 'read' THEN 1 ELSE 0 END)
    ON DUPLICATE KEY UPDATE
        last_message_id = NEW.id,
        last_message_time = NEW.create_time,
        unread_count = unread_count + CASE WHEN NEW.status != 'read' THEN 1 ELSE 0 END;
        
    -- 反向会话记录
    INSERT INTO t_message_session (user_id, other_user_id, last_message_id, last_message_time, unread_count)
    VALUES (NEW.sender_id, NEW.receiver_id, NEW.id, NEW.create_time, 0)
    ON DUPLICATE KEY UPDATE
        last_message_id = NEW.id,
        last_message_time = NEW.create_time;
END$$
DELIMITER ;

-- 消息状态更新后维护统计
DROP TRIGGER IF EXISTS `tr_message_after_update`;
DELIMITER $$
CREATE TRIGGER `tr_message_after_update`
AFTER UPDATE ON `t_message`
FOR EACH ROW
BEGIN
    -- 如果消息状态从未读变为已读
    IF OLD.status != 'read' AND NEW.status = 'read' THEN
        -- 更新接收者的未读消息数
        UPDATE t_user 
        SET unread_message_count = unread_message_count - 1 
        WHERE id = NEW.receiver_id AND unread_message_count > 0;
        
        -- 更新会话的未读数
        UPDATE t_message_session 
        SET unread_count = unread_count - 1 
        WHERE user_id = NEW.receiver_id 
          AND other_user_id = NEW.sender_id 
          AND unread_count > 0;
    END IF;
END$$
DELIMITER ;

-- ==========================================
-- 完成提示
-- ==========================================

SELECT '私信留言板模块安装完成！' as message,
       '已创建表：t_message, t_message_session, t_message_setting' as tables_created,
       '已添加字段：t_user.message_count, t_user.unread_message_count' as fields_added,
       '已创建存储过程和触发器用于数据维护' as procedures_created;

-- ==========================================
-- 验证安装
-- ==========================================

-- 显示创建的表信息
SELECT 
    table_name,
    table_comment,
    table_rows
FROM information_schema.tables 
WHERE table_schema = 'collide' 
  AND table_name IN ('t_message', 't_message_session', 't_message_setting')
ORDER BY table_name;