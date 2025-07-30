-- ==========================================
-- Collide 广告系统 - 增量安装脚本（简洁版）
-- 用于在现有数据库基础上添加广告系统
-- 可直接执行此脚本来扩展现有数据库
-- ==========================================

USE collide;

-- ==========================================
-- 检查并创建广告模板表
-- ==========================================

SET @table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_ad_template'
);

SET @sql = IF(@table_exists = 0, 
    'CREATE TABLE `t_ad_template` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''广告ID'',
        `ad_name`         VARCHAR(200) NOT NULL                COMMENT ''广告名称'',
        `ad_title`        VARCHAR(300) NOT NULL                COMMENT ''广告标题'',
        `ad_description`  VARCHAR(500)                         COMMENT ''广告描述'',
        `ad_type`         VARCHAR(50)  NOT NULL                COMMENT ''广告类型：banner、popup、feed、video、text'',
        `ad_category`     VARCHAR(50)  NOT NULL                COMMENT ''广告分类：product、service、app、brand'',
        `image_url`       VARCHAR(1000)                        COMMENT ''广告图片URL'',
        `video_url`       VARCHAR(1000)                        COMMENT ''视频广告URL（可选）'',
        `icon_url`        VARCHAR(500)                         COMMENT ''广告图标URL（可选）'',
        `click_url`       VARCHAR(1000) NOT NULL               COMMENT ''点击跳转链接'',
        `target_type`     VARCHAR(30)  NOT NULL DEFAULT ''_blank'' COMMENT ''链接打开方式：_blank、_self'',
        `advertiser_name`  VARCHAR(200)                        COMMENT ''广告主名称'',
        `brand_name`      VARCHAR(200)                         COMMENT ''品牌名称'',
        `status`          VARCHAR(30)  NOT NULL DEFAULT ''active'' COMMENT ''状态：active、paused、expired'',
        `priority`        INT          NOT NULL DEFAULT 0      COMMENT ''优先级（数值越高优先级越高）'',
        `start_time`      TIMESTAMP    NULL                    COMMENT ''广告开始时间'',
        `end_time`        TIMESTAMP    NULL                    COMMENT ''广告结束时间'',
        `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        KEY `idx_ad_type` (`ad_type`),
        KEY `idx_ad_category` (`ad_category`),
        KEY `idx_status` (`status`),
        KEY `idx_priority` (`priority`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''广告模板表'';',
    'SELECT ''Table t_ad_template already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建广告位表
-- ==========================================

SET @placement_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_ad_placement'
);

SET @sql = IF(@placement_table_exists = 0,
    'CREATE TABLE `t_ad_placement` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''广告位ID'',
        `placement_name`  VARCHAR(200) NOT NULL                COMMENT ''广告位名称'',
        `placement_code`  VARCHAR(100) NOT NULL                COMMENT ''广告位代码（唯一标识）'',
        `placement_desc`  VARCHAR(500)                         COMMENT ''广告位描述'',
        `page_type`       VARCHAR(50)  NOT NULL                COMMENT ''页面类型：home、content、search、category'',
        `position`        VARCHAR(50)  NOT NULL                COMMENT ''位置：top、bottom、left、right、center'',
        `width`           INT                                   COMMENT ''宽度（像素）'',
        `height`          INT                                   COMMENT ''高度（像素）'',
        `supported_types` VARCHAR(200)                         COMMENT ''支持的广告类型（逗号分隔）'',
        `price_per_click` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT ''每次点击价格'',
        `price_per_view`  DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT ''每次展示价格'',
        `is_active`       TINYINT(1)   NOT NULL DEFAULT 1      COMMENT ''是否启用'',
        `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk_placement_code` (`placement_code`),
        KEY `idx_page_type` (`page_type`),
        KEY `idx_active` (`is_active`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''广告位表'';',
    'SELECT ''Table t_ad_placement already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建广告投放表
-- ==========================================

SET @campaign_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_ad_campaign'
);

SET @sql = IF(@campaign_table_exists = 0,
    'CREATE TABLE `t_ad_campaign` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''投放ID'',
        `campaign_name`   VARCHAR(200) NOT NULL                COMMENT ''投放名称'',
        `ad_id`           BIGINT       NOT NULL                COMMENT ''广告ID'',
        `placement_id`    BIGINT       NOT NULL                COMMENT ''广告位ID'',
        `weight`          INT          NOT NULL DEFAULT 100    COMMENT ''权重（影响展示概率）'',
        `daily_budget`    DECIMAL(10,2) NOT NULL DEFAULT 0.00  COMMENT ''每日预算（0表示无限制）'',
        `start_date`      DATE         NOT NULL                COMMENT ''投放开始日期'',
        `end_date`        DATE                                 COMMENT ''投放结束日期'',
        `total_views`     BIGINT       NOT NULL DEFAULT 0      COMMENT ''总展示次数（实时统计）'',
        `total_clicks`    BIGINT       NOT NULL DEFAULT 0      COMMENT ''总点击次数（实时统计）'',
        `total_revenue`   DECIMAL(10,2) NOT NULL DEFAULT 0.00  COMMENT ''总收益（实时统计）'',
        `click_rate`      DECIMAL(5,4) NOT NULL DEFAULT 0.0000 COMMENT ''点击率（实时计算）'',
        `status`          VARCHAR(30)  NOT NULL DEFAULT ''active'' COMMENT ''状态：active、paused、completed'',
        `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        KEY `idx_ad_id` (`ad_id`),
        KEY `idx_placement_id` (`placement_id`),
        KEY `idx_status` (`status`),
        KEY `idx_start_end_date` (`start_date`, `end_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''广告投放表'';',
    'SELECT ''Table t_ad_campaign already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建广告展示记录表
-- ==========================================

SET @record_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_ad_record'
);

SET @sql = IF(@record_table_exists = 0,
    'CREATE TABLE `t_ad_record` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''记录ID'',
        `campaign_id`     BIGINT       NOT NULL                COMMENT ''投放ID'',
        `user_id`         BIGINT                               COMMENT ''用户ID（未登录时为空）'',
        `session_id`      VARCHAR(100)                         COMMENT ''会话ID'',
        `ip_address`      VARCHAR(45)                          COMMENT ''IP地址'',
        `user_agent`      TEXT                                 COMMENT ''用户代理信息'',
        `page_url`        VARCHAR(1000)                        COMMENT ''页面URL'',
        `view_time`       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''展示时间'',
        `is_clicked`      TINYINT(1)   NOT NULL DEFAULT 0      COMMENT ''是否被点击'',
        `click_time`      TIMESTAMP    NULL                    COMMENT ''点击时间'',
        `view_revenue`    DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT ''展示收益'',
        `click_revenue`   DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT ''点击收益'',
        PRIMARY KEY (`id`),
        KEY `idx_campaign_id` (`campaign_id`),
        KEY `idx_user_id` (`user_id`),
        KEY `idx_view_time` (`view_time`),
        KEY `idx_is_clicked` (`is_clicked`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''广告展示记录表'';',
    'SELECT ''Table t_ad_record already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 检查并创建广告统计表
-- ==========================================

SET @stats_table_exists = (
    SELECT COUNT(*)
    FROM information_schema.tables 
    WHERE table_schema = 'collide' 
    AND table_name = 't_ad_statistics'
);

SET @sql = IF(@stats_table_exists = 0,
    'CREATE TABLE `t_ad_statistics` (
        `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT ''统计ID'',
        `stat_date`       DATE         NOT NULL                COMMENT ''统计日期'',
        `campaign_id`     BIGINT       NOT NULL                COMMENT ''投放ID'',
        `total_views`     BIGINT       NOT NULL DEFAULT 0      COMMENT ''展示次数'',
        `total_clicks`    BIGINT       NOT NULL DEFAULT 0      COMMENT ''点击次数'',
        `unique_users`    BIGINT       NOT NULL DEFAULT 0      COMMENT ''独立用户数'',
        `click_rate`      DECIMAL(5,4) NOT NULL DEFAULT 0.0000 COMMENT ''点击率'',
        `total_revenue`   DECIMAL(10,2) NOT NULL DEFAULT 0.00  COMMENT ''总收益'',
        `avg_revenue_per_click` DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT ''平均每次点击收益'',
        `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT ''创建时间'',
        `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk_stat_date_campaign` (`stat_date`, `campaign_id`),
        KEY `idx_stat_date` (`stat_date`),
        KEY `idx_campaign_id` (`campaign_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT=''广告统计表'';',
    'SELECT ''Table t_ad_statistics already exists'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 插入基础广告位数据
-- ==========================================

-- 检查是否已有广告位数据
SET @placement_count = (
    SELECT COUNT(*) FROM t_ad_placement
);

-- 只有在没有数据时才插入
SET @sql = IF(@placement_count = 0,
    'INSERT INTO `t_ad_placement` (`placement_name`, `placement_code`, `placement_desc`, `page_type`, `position`, `width`, `height`, `supported_types`, `price_per_click`, `price_per_view`) VALUES
    (''首页顶部横幅'', ''home_top_banner'', ''首页顶部横幅广告位'', ''home'', ''top'', 1200, 200, ''banner,text'', 1.0000, 0.0100),
    (''首页右侧广告'', ''home_right_ad'', ''首页右侧展示广告位'', ''home'', ''right'', 300, 250, ''banner,popup'', 0.8000, 0.0080),
    (''内容页广告'', ''content_ad'', ''文章内容页广告位'', ''content'', ''top'', 728, 90, ''banner,text'', 1.2000, 0.0120),
    (''搜索页广告'', ''search_ad'', ''搜索结果页广告位'', ''search'', ''top'', 600, 100, ''banner,text'', 1.5000, 0.0150),
    (''信息流广告'', ''feed_ad'', ''信息流中的广告位'', ''home'', ''center'', 400, 300, ''feed,banner'', 2.0000, 0.0200);',
    'SELECT ''Ad placements already exist'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 插入示例广告模板数据
-- ==========================================

-- 检查是否已有广告模板数据
SET @template_count = (
    SELECT COUNT(*) FROM t_ad_template
);

-- 只有在没有数据时才插入
SET @sql = IF(@template_count = 0,
    'INSERT INTO `t_ad_template` (`ad_name`, `ad_title`, `ad_description`, `ad_type`, `ad_category`, `image_url`, `click_url`, `advertiser_name`, `brand_name`, `priority`) VALUES
    (''科技产品推广'', ''最新AI智能产品'', ''体验未来科技，提升工作效率'', ''banner'', ''product'', ''https://example.com/ads/tech-product.jpg'', ''https://example.com/tech-product'', ''科技公司'', ''TechBrand'', 100),
    (''在线教育课程'', ''编程课程限时优惠'', ''零基础学编程，30天成为开发者'', ''banner'', ''service'', ''https://example.com/ads/coding-course.jpg'', ''https://example.com/courses'', ''教育平台'', ''EduBrand'', 90),
    (''手机APP推广'', ''下载APP送好礼'', ''新用户注册立享专属优惠'', ''feed'', ''app'', ''https://example.com/ads/mobile-app.jpg'', ''https://example.com/app-download'', ''应用开发商'', ''AppBrand'', 80),
    (''品牌推广'', ''知名品牌形象展示'', ''品质生活，从选择开始'', ''popup'', ''brand'', ''https://example.com/ads/brand.jpg'', ''https://example.com/brand'', ''品牌方'', ''LifeBrand'', 70);',
    'SELECT ''Ad templates already exist'' as message;'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ==========================================
-- 创建存储过程
-- ==========================================

DELIMITER $$

-- 获取广告位可展示的广告
DROP PROCEDURE IF EXISTS `get_ads_for_placement`$$
CREATE PROCEDURE `get_ads_for_placement`(
    IN p_placement_code VARCHAR(100),
    IN p_limit INT
)
BEGIN
    SELECT 
        c.id as campaign_id,
        a.id as ad_id,
        a.ad_name,
        a.ad_title,
        a.ad_type,
        a.image_url,
        a.video_url,
        a.icon_url,
        a.click_url,
        a.target_type,
        c.weight,
        p.width,
        p.height
    FROM t_ad_campaign c
    JOIN t_ad_template a ON c.ad_id = a.id
    JOIN t_ad_placement p ON c.placement_id = p.id
    WHERE p.placement_code = p_placement_code
      AND c.status = 'active'
      AND a.status = 'active'
      AND p.is_active = 1
      AND c.start_date <= CURDATE()
      AND (c.end_date IS NULL OR c.end_date >= CURDATE())
      AND (a.start_time IS NULL OR a.start_time <= NOW())
      AND (a.end_time IS NULL OR a.end_time >= NOW())
      AND FIND_IN_SET(a.ad_type, p.supported_types) > 0
    ORDER BY c.weight DESC, RAND()
    LIMIT p_limit;
END$$

-- 记录广告展示
DROP PROCEDURE IF EXISTS `record_ad_view`$$
CREATE PROCEDURE `record_ad_view`(
    IN p_campaign_id BIGINT,
    IN p_user_id BIGINT,
    IN p_session_id VARCHAR(100),
    IN p_ip_address VARCHAR(45),
    IN p_user_agent TEXT,
    IN p_page_url VARCHAR(1000)
)
BEGIN
    DECLARE v_view_price DECIMAL(10,4) DEFAULT 0.0000;
    
    -- 获取展示价格
    SELECT pl.price_per_view INTO v_view_price
    FROM t_ad_campaign c
    JOIN t_ad_placement pl ON c.placement_id = pl.id
    WHERE c.id = p_campaign_id;
    
    -- 插入展示记录
    INSERT INTO t_ad_record (
        campaign_id, user_id, session_id, ip_address, 
        user_agent, page_url, view_revenue
    ) VALUES (
        p_campaign_id, p_user_id, p_session_id, p_ip_address,
        p_user_agent, p_page_url, v_view_price
    );
    
    -- 更新投放统计
    UPDATE t_ad_campaign 
    SET total_views = total_views + 1,
        total_revenue = total_revenue + v_view_price
    WHERE id = p_campaign_id;
END$$

-- 记录广告点击
DROP PROCEDURE IF EXISTS `record_ad_click`$$
CREATE PROCEDURE `record_ad_click`(
    IN p_record_id BIGINT
)
BEGIN
    DECLARE v_campaign_id BIGINT;
    DECLARE v_click_price DECIMAL(10,4) DEFAULT 0.0000;
    
    -- 获取投放ID
    SELECT campaign_id INTO v_campaign_id
    FROM t_ad_record 
    WHERE id = p_record_id;
    
    -- 获取点击价格
    SELECT pl.price_per_click INTO v_click_price
    FROM t_ad_campaign c
    JOIN t_ad_placement pl ON c.placement_id = pl.id
    WHERE c.id = v_campaign_id;
    
    -- 更新点击记录
    UPDATE t_ad_record 
    SET is_clicked = 1,
        click_time = NOW(),
        click_revenue = v_click_price
    WHERE id = p_record_id;
    
    -- 更新投放统计
    UPDATE t_ad_campaign 
    SET total_clicks = total_clicks + 1,
        total_revenue = total_revenue + v_click_price,
        click_rate = total_clicks / total_views
    WHERE id = v_campaign_id;
END$$

DELIMITER ;

-- ==========================================
-- 创建触发器
-- ==========================================

-- 自动更新点击率
DROP TRIGGER IF EXISTS `tr_update_click_rate`;
DELIMITER $$
CREATE TRIGGER `tr_update_click_rate`
AFTER UPDATE ON `t_ad_campaign`
FOR EACH ROW
BEGIN
    -- 更新点击率
    IF NEW.total_views > 0 THEN
        UPDATE t_ad_campaign 
        SET click_rate = total_clicks / total_views
        WHERE id = NEW.id;
    END IF;
END$$
DELIMITER ;

-- ==========================================
-- 完成提示
-- ==========================================

SELECT '广告系统安装完成！' as message,
       '已创建表：t_ad_template, t_ad_placement, t_ad_campaign, t_ad_record, t_ad_statistics' as tables_created,
       '已创建存储过程：get_ads_for_placement, record_ad_view, record_ad_click' as procedures_created,
       '已创建触发器：tr_update_click_rate' as triggers_created,
       '已初始化基础广告位和示例广告模板' as data_initialized;

-- ==========================================
-- 验证安装
-- ==========================================

SELECT 
    table_name,
    table_comment,
    table_rows
FROM information_schema.tables 
WHERE table_schema = 'collide' 
  AND table_name IN ('t_ad_template', 't_ad_placement', 't_ad_campaign', 't_ad_record', 't_ad_statistics')
ORDER BY table_name;

-- 显示广告位统计
SELECT 
    page_type,
    COUNT(*) as placement_count
FROM t_ad_placement 
WHERE is_active = 1
GROUP BY page_type;

-- 显示广告类型统计
SELECT 
    ad_type,
    COUNT(*) as template_count
FROM t_ad_template
WHERE status = 'active'
GROUP BY ad_type;