-- ============================================
-- Tag 模块完整数据库脚本 (无连表设计)
-- 版本: 1.0
-- 创建时间: 2024-12-19
-- 描述: Tag 模块的完整数据库结构定义
-- 严格要求: 强制去除所有连表设计，仅使用单表查询
-- ============================================

USE collide;

-- ============================================
-- 1. 标签主表 (t_tag)
-- ============================================
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '标签ID，主键',
  `name`         VARCHAR(100) NOT NULL                 COMMENT '标签名称',
  `description`  TEXT                                  COMMENT '标签描述',
  `color`        VARCHAR(20)  DEFAULT '#1890ff'       COMMENT '标签颜色（十六进制）',
  `icon_url`     VARCHAR(500)                          COMMENT '标签图标URL',
  `tag_type`     VARCHAR(20)  NOT NULL DEFAULT 'content' COMMENT '标签类型：content、interest、system',
  `category_id`  BIGINT                                COMMENT '所属分类ID（仅存储ID，不做外键关联）',
  `usage_count`  BIGINT       NOT NULL DEFAULT 0      COMMENT '使用次数',
  `heat_score`   DECIMAL(10,2) NOT NULL DEFAULT 0.00  COMMENT '热度分数',
  `status`       VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '标签状态：active、inactive',
  `sort`         INT          NOT NULL DEFAULT 0      COMMENT '排序值，越小越靠前',
  `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version`      INT          NOT NULL DEFAULT 1      COMMENT '版本号，用于乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`, `tag_type`),
  KEY `idx_tag_type` (`tag_type`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_usage_count` (`usage_count`),
  KEY `idx_heat_score` (`heat_score`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签主表';

-- ============================================
-- 2. 用户兴趣标签关联表 (t_user_interest_tag)
-- ============================================
DROP TABLE IF EXISTS `t_user_interest_tag`;
CREATE TABLE `t_user_interest_tag` (
  `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
  `user_id`        BIGINT       NOT NULL                 COMMENT '用户ID（仅存储ID，不做外键关联）',
  `tag_id`         BIGINT       NOT NULL                 COMMENT '标签ID（仅存储ID，不做外键关联）',
  `interest_score` DECIMAL(5,2) NOT NULL DEFAULT 0.00   COMMENT '兴趣分数（0-100）',
  `source`         VARCHAR(50)  NOT NULL DEFAULT 'manual' COMMENT '兴趣来源：manual、behavior、system',
  `status`         VARCHAR(20)  NOT NULL DEFAULT 'active' COMMENT '状态：active、inactive',
  `create_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `version`        INT          NOT NULL DEFAULT 1      COMMENT '版本号，用于乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag` (`user_id`, `tag_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_interest_score` (`interest_score`),
  KEY `idx_source` (`source`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户兴趣标签关联表';

-- ============================================
-- 3. 内容标签关联表 (t_content_tag)
-- ============================================
DROP TABLE IF EXISTS `t_content_tag`;
CREATE TABLE `t_content_tag` (
  `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
  `content_id`  BIGINT    NOT NULL                 COMMENT '内容ID（仅存储ID，不做外键关联）',
  `tag_id`      BIGINT    NOT NULL                 COMMENT '标签ID（仅存储ID，不做外键关联）',
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `version`     INT       NOT NULL DEFAULT 1      COMMENT '版本号，用于乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_content_tag` (`content_id`, `tag_id`),
  KEY `idx_content_id` (`content_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容标签关联表';

-- ============================================
-- 4. 标签统计表 (t_tag_statistics)
-- ============================================
DROP TABLE IF EXISTS `t_tag_statistics`;
CREATE TABLE `t_tag_statistics` (
  `id`                BIGINT    NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `tag_id`            BIGINT    NOT NULL                 COMMENT '标签ID（仅存储ID，不做外键关联）',
  `daily_usage_count` BIGINT    NOT NULL DEFAULT 0      COMMENT '日使用次数',
  `weekly_usage_count` BIGINT   NOT NULL DEFAULT 0      COMMENT '周使用次数',
  `monthly_usage_count` BIGINT  NOT NULL DEFAULT 0      COMMENT '月使用次数',
  `total_user_count`  BIGINT    NOT NULL DEFAULT 0      COMMENT '总用户数',
  `active_user_count` BIGINT    NOT NULL DEFAULT 0      COMMENT '活跃用户数',
  `content_count`     BIGINT    NOT NULL DEFAULT 0      COMMENT '关联内容数',
  `stat_date`         DATE      NOT NULL                 COMMENT '统计日期',
  `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_date` (`tag_id`, `stat_date`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_daily_usage` (`daily_usage_count`),
  KEY `idx_total_user` (`total_user_count`),
  KEY `idx_content_count` (`content_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签统计表';

-- ============================================
-- 5. 初始化基础数据
-- ============================================

-- 插入系统标签
INSERT INTO `t_tag` (`name`, `description`, `color`, `tag_type`, `usage_count`, `heat_score`, `status`, `sort`) VALUES
('热门',     '热门内容标签',         '#FF4757', 'system',   1000, 95.00, 'active', 1),
('推荐',     '推荐内容标签',         '#2ED573', 'system',   900,  90.00, 'active', 2),
('精选',     '精选内容标签',         '#FFA502', 'system',   800,  85.00, 'active', 3),
('原创',     '原创内容标签',         '#3742FA', 'system',   700,  80.00, 'active', 4),
('置顶',     '置顶内容标签',         '#F8B500', 'system',   600,  75.00, 'active', 5);

-- 插入内容标签
INSERT INTO `t_tag` (`name`, `description`, `color`, `tag_type`, `usage_count`, `heat_score`, `status`, `sort`) VALUES
('技术',     '技术相关内容',         '#1890FF', 'content',  500, 70.00, 'active', 10),
('生活',     '生活相关内容',         '#52C41A', 'content',  400, 65.00, 'active', 11),
('娱乐',     '娱乐相关内容',         '#FA8C16', 'content',  350, 60.00, 'active', 12),
('教育',     '教育相关内容',         '#722ED1', 'content',  300, 55.00, 'active', 13),
('健康',     '健康相关内容',         '#13C2C2', 'content',  250, 50.00, 'active', 14),
('旅游',     '旅游相关内容',         '#EB2F96', 'content',  200, 45.00, 'active', 15),
('美食',     '美食相关内容',         '#F5222D', 'content',  180, 40.00, 'active', 16),
('时尚',     '时尚相关内容',         '#FA541C', 'content',  160, 35.00, 'active', 17),
('运动',     '运动相关内容',         '#FAAD14', 'content',  140, 30.00, 'active', 18),
('音乐',     '音乐相关内容',         '#A0D911', 'content',  120, 25.00, 'active', 19);

-- 插入兴趣标签
INSERT INTO `t_tag` (`name`, `description`, `color`, `tag_type`, `usage_count`, `heat_score`, `status`, `sort`) VALUES
('编程',     '编程兴趣',             '#1890FF', 'interest', 400, 80.00, 'active', 20),
('设计',     '设计兴趣',             '#722ED1', 'interest', 350, 75.00, 'active', 21),
('摄影',     '摄影兴趣',             '#13C2C2', 'interest', 300, 70.00, 'active', 22),
('阅读',     '阅读兴趣',             '#52C41A', 'interest', 250, 65.00, 'active', 23),
('写作',     '写作兴趣',             '#FA8C16', 'interest', 200, 60.00, 'active', 24),
('绘画',     '绘画兴趣',             '#EB2F96', 'interest', 180, 55.00, 'active', 25),
('游戏',     '游戏兴趣',             '#F5222D', 'interest', 160, 50.00, 'active', 26),
('电影',     '电影兴趣',             '#FA541C', 'interest', 140, 45.00, 'active', 27),
('动漫',     '动漫兴趣',             '#FAAD14', 'interest', 120, 40.00, 'active', 28),
('科技',     '科技兴趣',             '#A0D911', 'interest', 100, 35.00, 'active', 29);

-- ============================================
-- 6. 创建视图（可选，用于数据查询优化）
-- ============================================

-- 标签统计视图（基于单表查询）
DROP VIEW IF EXISTS `v_tag_stats`;
CREATE VIEW `v_tag_stats` AS
SELECT 
    t.id,
    t.name,
    t.description,
    t.color,
    t.tag_type,
    t.category_id,
    t.usage_count,
    t.heat_score,
    t.status,
    t.sort,
    t.create_time,
    t.update_time,
    (SELECT COUNT(*) FROM t_user_interest_tag uit WHERE uit.tag_id = t.id AND uit.status = 'active') AS interest_user_count,
    (SELECT COUNT(*) FROM t_content_tag ct WHERE ct.tag_id = t.id) AS content_count
FROM t_tag t
WHERE t.status = 'active'
ORDER BY t.heat_score DESC, t.usage_count DESC, t.sort ASC;

-- 热门标签视图
DROP VIEW IF EXISTS `v_hot_tags`;
CREATE VIEW `v_hot_tags` AS
SELECT 
    id,
    name,
    description,
    color,
    tag_type,
    usage_count,
    heat_score,
    create_time
FROM t_tag 
WHERE status = 'active' 
  AND heat_score > 0
ORDER BY heat_score DESC, usage_count DESC
LIMIT 100;

-- ============================================
-- 7. 存储过程（可选，用于数据维护）
-- ============================================

-- 更新标签热度分数的存储过程
DELIMITER //
CREATE PROCEDURE `sp_update_tag_heat_score`(IN tag_id_param BIGINT)
BEGIN
    DECLARE usage_count_val BIGINT DEFAULT 0;
    DECLARE days_since_create INT DEFAULT 0;
    DECLARE new_heat_score DECIMAL(10,2) DEFAULT 0.00;
    
    -- 获取标签使用次数和创建天数
    SELECT 
        usage_count,
        DATEDIFF(NOW(), create_time)
    INTO usage_count_val, days_since_create
    FROM t_tag 
    WHERE id = tag_id_param;
    
    -- 计算热度分数：使用次数 * 时间衰减因子
    SET new_heat_score = usage_count_val * POW(0.95, days_since_create / 7);
    
    -- 更新热度分数
    UPDATE t_tag 
    SET heat_score = new_heat_score,
        update_time = NOW()
    WHERE id = tag_id_param;
    
END //
DELIMITER ;

-- 批量更新所有标签热度分数的存储过程
DELIMITER //
CREATE PROCEDURE `sp_batch_update_heat_scores`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE tag_id_val BIGINT;
    DECLARE tag_cursor CURSOR FOR 
        SELECT id FROM t_tag WHERE status = 'active';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN tag_cursor;
    
    read_loop: LOOP
        FETCH tag_cursor INTO tag_id_val;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        CALL sp_update_tag_heat_score(tag_id_val);
    END LOOP;
    
    CLOSE tag_cursor;
END //
DELIMITER ;

-- ============================================
-- 8. 触发器（可选，用于自动维护统计数据）
-- ============================================

-- 标签使用次数更新触发器
DELIMITER //
CREATE TRIGGER `tr_tag_usage_update`
AFTER INSERT ON `t_content_tag`
FOR EACH ROW
BEGIN
    UPDATE t_tag 
    SET usage_count = usage_count + 1,
        update_time = NOW()
    WHERE id = NEW.tag_id;
END //
DELIMITER ;

-- 标签使用次数减少触发器
DELIMITER //
CREATE TRIGGER `tr_tag_usage_decrease`
AFTER DELETE ON `t_content_tag`
FOR EACH ROW
BEGIN
    UPDATE t_tag 
    SET usage_count = GREATEST(usage_count - 1, 0),
        update_time = NOW()
    WHERE id = OLD.tag_id;
END //
DELIMITER ;

-- ============================================
-- 9. 数据完整性检查
-- ============================================

-- 检查表结构
SELECT 
    TABLE_NAME,
    TABLE_COMMENT,
    TABLE_ROWS,
    CREATE_TIME
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'collide' 
  AND TABLE_NAME LIKE 't_tag%'
ORDER BY TABLE_NAME;

-- 检查索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    NON_UNIQUE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
  AND TABLE_NAME LIKE 't_tag%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- ============================================
-- 脚本执行完成
-- ============================================
SELECT 'Tag 模块数据库脚本执行完成！所有连表设计已强制去除，仅使用单表查询。' AS message; 