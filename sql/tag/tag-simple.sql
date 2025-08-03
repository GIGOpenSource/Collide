-- =====================================================
-- Collide Tag模块数据库表结构 - 兴趣标签系统
-- 功能：标签管理、用户关注标签、内容打标签、协同过滤推荐
-- 版本：1.0.0 (简化版)
-- 作者：GIG Team
-- 日期：2024-01-16
-- =====================================================

-- =================== 标签主表 ===================

CREATE TABLE `t_tag` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `tag_name`        VARCHAR(50)  NOT NULL COMMENT '标签名称',
    `tag_description` VARCHAR(200) DEFAULT NULL COMMENT '标签描述',
    `tag_icon`        VARCHAR(255) DEFAULT NULL COMMENT '标签图标URL',
    `weight`          INT          NOT NULL DEFAULT 50 COMMENT '权重（1-100，管理员配置）',
    `hotness`         BIGINT       NOT NULL DEFAULT 0 COMMENT '热度值（定时计算）',
    `follow_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '关注人数',
    `content_count`   BIGINT       NOT NULL DEFAULT 0 COMMENT '内容数量',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_name` (`tag_name`),
    KEY `idx_weight_hotness` (`weight` DESC, `hotness` DESC),
    KEY `idx_status_hotness` (`status`, `hotness` DESC),
    KEY `idx_follow_count` (`follow_count` DESC),
    KEY `idx_content_count` (`content_count` DESC),
    CONSTRAINT `chk_weight` CHECK (`weight` >= 1 AND `weight` <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签主表';

-- =================== 用户关注标签关系表 ===================

CREATE TABLE `t_user_tag_follow` (
    `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `user_id`     BIGINT    NOT NULL COMMENT '用户ID',
    `tag_id`      BIGINT    NOT NULL COMMENT '标签ID',
    `follow_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_tag_id` (`tag_id`),
    KEY `idx_follow_time` (`follow_time` DESC),
    KEY `idx_user_follow_time` (`user_id`, `follow_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注标签关系表（每用户最多9个）';

-- =================== 内容标签关系表 ===================

CREATE TABLE `t_content_tag` (
    `id`         BIGINT    NOT NULL AUTO_INCREMENT COMMENT '关系ID',
    `content_id` BIGINT    NOT NULL COMMENT '内容ID',
    `tag_id`     BIGINT    NOT NULL COMMENT '标签ID',
    `tag_time`   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打标签时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_content_tag` (`content_id`, `tag_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_tag_id` (`tag_id`),
    KEY `idx_tag_time` (`tag_time` DESC),
    KEY `idx_tag_content_time` (`tag_id`, `tag_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容标签关系表（每内容最多9个）';

-- =================== 初始化数据 ===================

-- 插入一些基础标签
INSERT INTO `t_tag` (`tag_name`, `tag_description`, `weight`, `status`) VALUES
('技术', '科技相关内容', 80, 1),
('生活', '日常生活分享', 70, 1),
('娱乐', '娱乐休闲内容', 60, 1),
('教育', '学习教育资源', 85, 1),
('健康', '健康养生知识', 75, 1),
('旅游', '旅行游记攻略', 65, 1),
('美食', '美食制作分享', 70, 1),
('运动', '健身运动相关', 60, 1),
('音乐', '音乐作品推荐', 55, 1),
('电影', '影视作品讨论', 55, 1),
('阅读', '读书心得分享', 75, 1),
('摄影', '摄影作品技巧', 65, 1);

-- =================== 性能优化视图 ===================

-- 活跃标签用户视图（用于协同过滤优化）
CREATE VIEW `v_active_tag_users` AS
SELECT 
    user_id, 
    COUNT(*) as tag_count,
    GROUP_CONCAT(tag_id ORDER BY follow_time DESC) as tag_ids
FROM t_user_tag_follow 
WHERE follow_time >= DATE_SUB(NOW(), INTERVAL 90 DAY)
GROUP BY user_id
HAVING tag_count >= 1
ORDER BY tag_count DESC;

-- 热门标签统计视图
CREATE VIEW `v_hot_tags` AS
SELECT 
    t.id,
    t.tag_name,
    t.weight,
    t.follow_count,
    t.content_count,
    t.hotness,
    (t.follow_count * 0.6 + t.content_count * 0.4) * (t.weight / 100.0) as calculated_score
FROM t_tag t
WHERE t.status = 1
ORDER BY calculated_score DESC, t.hotness DESC;

-- =================== 存储过程 ===================

-- 更新标签统计数据的存储过程
DELIMITER $$
CREATE PROCEDURE UpdateTagStats()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE tag_id_var BIGINT;
    DECLARE new_follow_count BIGINT;
    DECLARE new_content_count BIGINT;
    DECLARE new_hotness BIGINT;
    
    DECLARE tag_cursor CURSOR FOR 
        SELECT id FROM t_tag WHERE status = 1;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN tag_cursor;
    
    tag_loop: LOOP
        FETCH tag_cursor INTO tag_id_var;
        IF done THEN
            LEAVE tag_loop;
        END IF;
        
        -- 计算关注数
        SELECT COUNT(*) INTO new_follow_count 
        FROM t_user_tag_follow 
        WHERE tag_id = tag_id_var;
        
        -- 计算内容数
        SELECT COUNT(*) INTO new_content_count 
        FROM t_content_tag 
        WHERE tag_id = tag_id_var;
        
        -- 计算热度值
        SELECT ROUND((new_follow_count * 0.6 + new_content_count * 0.4) * (weight / 100.0)) 
        INTO new_hotness
        FROM t_tag 
        WHERE id = tag_id_var;
        
        -- 更新统计数据
        UPDATE t_tag 
        SET follow_count = new_follow_count,
            content_count = new_content_count,
            hotness = new_hotness
        WHERE id = tag_id_var;
        
    END LOOP;
    
    CLOSE tag_cursor;
END$$
DELIMITER ;

-- =================== 触发器 ===================

-- 用户关注标签时自动更新计数
DELIMITER $$
CREATE TRIGGER tr_user_tag_follow_insert 
AFTER INSERT ON t_user_tag_follow 
FOR EACH ROW 
BEGIN
    UPDATE t_tag 
    SET follow_count = follow_count + 1 
    WHERE id = NEW.tag_id;
END$$
DELIMITER ;

-- 用户取消关注标签时自动更新计数
DELIMITER $$
CREATE TRIGGER tr_user_tag_follow_delete 
AFTER DELETE ON t_user_tag_follow 
FOR EACH ROW 
BEGIN
    UPDATE t_tag 
    SET follow_count = follow_count - 1 
    WHERE id = OLD.tag_id;
END$$
DELIMITER ;

-- 内容打标签时自动更新计数
DELIMITER $$
CREATE TRIGGER tr_content_tag_insert 
AFTER INSERT ON t_content_tag 
FOR EACH ROW 
BEGIN
    UPDATE t_tag 
    SET content_count = content_count + 1 
    WHERE id = NEW.tag_id;
END$$
DELIMITER ;

-- 内容移除标签时自动更新计数
DELIMITER $$
CREATE TRIGGER tr_content_tag_delete 
AFTER DELETE ON t_content_tag 
FOR EACH ROW 
BEGIN
    UPDATE t_tag 
    SET content_count = content_count - 1 
    WHERE id = OLD.tag_id;
END$$
DELIMITER ;