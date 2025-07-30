-- ==========================================
-- Collide 广告系统 - 测试数据（简洁版）
-- 用于测试广告展示和点击统计功能
-- ==========================================

USE collide;

-- ==========================================
-- 清空现有数据（测试用）
-- ==========================================

-- 注意：生产环境请勿执行清空操作
-- DELETE FROM t_ad_statistics;
-- DELETE FROM t_ad_record;
-- DELETE FROM t_ad_campaign;
-- DELETE FROM t_ad_template;
-- DELETE FROM t_ad_placement;

-- ==========================================
-- 补充广告位测试数据
-- ==========================================

-- 更多广告位
INSERT INTO `t_ad_placement` (`placement_name`, `placement_code`, `placement_desc`, `page_type`, `position`, `width`, `height`, `supported_types`, `price_per_click`, `price_per_view`) VALUES
('内容页底部', 'content_bottom', '文章内容底部广告位', 'content', 'bottom', 728, 90, 'banner,text', 0.8000, 0.0080),
('分类页顶部', 'category_top', '分类列表页顶部广告', 'category', 'top', 970, 250, 'banner,video', 1.5000, 0.0150),
('分类页右侧', 'category_right', '分类页面右侧广告', 'category', 'right', 300, 600, 'banner,popup', 1.0000, 0.0100),
('搜索页右侧', 'search_right', '搜索结果右侧广告', 'search', 'right', 250, 400, 'banner,text', 1.2000, 0.0120),
('移动端顶部', 'mobile_top', '移动设备顶部横幅', 'home', 'top', 320, 50, 'banner,text', 0.6000, 0.0060),
('移动端底部', 'mobile_bottom', '移动设备底部横幅', 'home', 'bottom', 320, 50, 'banner,text', 0.5000, 0.0050);

-- ==========================================
-- 补充广告模板测试数据
-- ==========================================

-- 更多广告类型示例
INSERT INTO `t_ad_template` (`ad_name`, `ad_title`, `ad_description`, `ad_type`, `ad_category`, `image_url`, `video_url`, `icon_url`, `click_url`, `advertiser_name`, `brand_name`, `priority`) VALUES

-- 电商类广告
('电商促销', '双11购物狂欢节', '全场商品5折起，限时抢购', 'banner', 'product', 'https://example.com/ads/shopping-sale.jpg', NULL, 'https://example.com/icons/shop.png', 'https://shop.example.com/sale', '电商平台', 'ShopMall', 95),
('服装推广', '春季新款上市', '时尚潮流，品质保证', 'feed', 'product', 'https://example.com/ads/fashion.jpg', NULL, 'https://example.com/icons/fashion.png', 'https://fashion.example.com/spring', '服装品牌', 'FashionBrand', 85),

-- 游戏类广告
('手游推广', '史诗级RPG手游', '3D魔幻世界，万人同服', 'popup', 'app', 'https://example.com/ads/rpg-game.jpg', 'https://example.com/videos/game-trailer.mp4', 'https://example.com/icons/game.png', 'https://game.example.com/download', '游戏公司', 'GameStudio', 90),
('休闲游戏', '轻松益智小游戏', '随时随地，轻松娱乐', 'banner', 'app', 'https://example.com/ads/casual-game.jpg', NULL, 'https://example.com/icons/puzzle.png', 'https://casual.example.com', '独立游戏工作室', 'CasualGames', 75),

-- 金融类广告
('理财产品', '稳健投资理财', '年化收益8%，银行级安全', 'text', 'service', NULL, NULL, 'https://example.com/icons/finance.png', 'https://finance.example.com/invest', '金融机构', 'FinanceGroup', 88),
('信用卡推广', '免年费信用卡', '新户礼品丰厚，快速审批', 'banner', 'service', 'https://example.com/ads/credit-card.jpg', NULL, 'https://example.com/icons/card.png', 'https://bank.example.com/card', '银行', 'BankCorp', 82),

-- 健康类广告
('健身APP', '在线健身教练', '专业指导，在家练出好身材', 'feed', 'app', 'https://example.com/ads/fitness.jpg', 'https://example.com/videos/fitness-demo.mp4', 'https://example.com/icons/fitness.png', 'https://fitness.example.com/app', '健康科技', 'FitnessPro', 80),
('医疗服务', '在线问诊服务', '专业医生，24小时在线咨询', 'banner', 'service', 'https://example.com/ads/medical.jpg', NULL, 'https://example.com/icons/medical.png', 'https://medical.example.com', '医疗平台', 'HealthCare', 78),

-- 旅游类广告
('旅游推广', '春季踏青特惠', '国内外精品路线，超值价格', 'video', 'service', 'https://example.com/ads/travel.jpg', 'https://example.com/videos/travel-promo.mp4', 'https://example.com/icons/travel.png', 'https://travel.example.com/spring', '旅游公司', 'TravelCorp', 77),

-- 文字广告示例
('简洁文字广告', '高效办公软件', '提升工作效率的神器', 'text', 'product', NULL, NULL, NULL, 'https://office.example.com', '软件公司', 'OfficePro', 70);

-- ==========================================
-- 创建更多投放活动
-- ==========================================

-- 补充投放活动
INSERT INTO `t_ad_campaign` (`campaign_name`, `ad_id`, `placement_id`, `weight`, `daily_budget`, `start_date`, `end_date`) VALUES
-- 电商促销投放
('首页电商促销', 5, 1, 95, 800.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY)),
('信息流服装推广', 6, 5, 85, 400.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 25 DAY)),

-- 游戏推广投放
('弹窗游戏推广', 7, 2, 90, 600.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 20 DAY)),
('内容页休闲游戏', 8, 3, 75, 250.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY)),

-- 金融产品投放
('搜索页理财产品', 9, 4, 88, 350.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 40 DAY)),
('分类页信用卡', 10, 6, 82, 300.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 35 DAY)),

-- 健康服务投放
('信息流健身APP', 11, 5, 80, 200.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 50 DAY)),
('内容页医疗服务', 12, 3, 78, 180.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 45 DAY)),

-- 旅游推广投放
('分类页旅游推广', 13, 6, 77, 220.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY)),

-- 移动端投放
('移动端电商促销', 5, 9, 85, 300.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 12 DAY)),
('移动端游戏推广', 7, 10, 80, 250.00, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 18 DAY));

-- ==========================================
-- 生成模拟展示记录
-- ==========================================

-- 模拟近7天的广告展示数据
INSERT INTO `t_ad_record` (`campaign_id`, `user_id`, `session_id`, `ip_address`, `user_agent`, `page_url`, `view_time`, `is_clicked`, `click_time`, `view_revenue`, `click_revenue`) VALUES

-- 今日数据
(1, 2, 'sess_001', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/home', NOW() - INTERVAL 3 HOUR, 1, NOW() - INTERVAL 3 HOUR + INTERVAL 5 SECOND, 0.0100, 1.0000),
(1, 3, 'sess_002', '192.168.1.101', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'https://collide.com/home', NOW() - INTERVAL 2 HOUR, 0, NULL, 0.0100, 0.0000),
(2, 4, 'sess_003', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/home', NOW() - INTERVAL 1 HOUR, 1, NOW() - INTERVAL 1 HOUR + INTERVAL 10 SECOND, 0.0080, 0.8000),
(3, 2, 'sess_004', '192.168.1.103', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://collide.com/content/123', NOW() - INTERVAL 30 MINUTE, 1, NOW() - INTERVAL 25 MINUTE, 0.0120, 1.2000),
(5, 5, 'sess_005', '192.168.1.104', 'Mozilla/5.0 (Android 11; SM-G991B)', 'https://collide.com/home', NOW() - INTERVAL 15 MINUTE, 0, NULL, 0.0100, 0.0000),

-- 昨日数据
(1, 6, 'sess_101', '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/home', NOW() - INTERVAL 1 DAY + INTERVAL 10 HOUR, 0, NULL, 0.0100, 0.0000),
(2, 7, 'sess_102', '192.168.1.106', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'https://collide.com/home', NOW() - INTERVAL 1 DAY + INTERVAL 8 HOUR, 1, NOW() - INTERVAL 1 DAY + INTERVAL 8 HOUR + INTERVAL 3 SECOND, 0.0080, 0.8000),
(3, 3, 'sess_103', '192.168.1.107', 'Mozilla/5.0 (iPad; CPU OS 15_0)', 'https://collide.com/content/456', NOW() - INTERVAL 1 DAY + INTERVAL 6 HOUR, 1, NOW() - INTERVAL 1 DAY + INTERVAL 6 HOUR + INTERVAL 8 SECOND, 0.0120, 1.2000),
(4, 8, 'sess_104', '192.168.1.108', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/search?q=tech', NOW() - INTERVAL 1 DAY + INTERVAL 4 HOUR, 0, NULL, 0.0150, 0.0000),
(5, 4, 'sess_105', '192.168.1.109', 'Mozilla/5.0 (Android 11; Pixel 5)', 'https://collide.com/home', NOW() - INTERVAL 1 DAY + INTERVAL 2 HOUR, 1, NOW() - INTERVAL 1 DAY + INTERVAL 2 HOUR + INTERVAL 6 SECOND, 0.0100, 1.0000),

-- 前天数据
(1, 9, 'sess_201', '192.168.1.110', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/home', NOW() - INTERVAL 2 DAY + INTERVAL 14 HOUR, 1, NOW() - INTERVAL 2 DAY + INTERVAL 14 HOUR + INTERVAL 12 SECOND, 0.0100, 1.0000),
(6, 5, 'sess_202', '192.168.1.111', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'https://collide.com/feed', NOW() - INTERVAL 2 DAY + INTERVAL 12 HOUR, 0, NULL, 0.0200, 0.0000),
(7, 6, 'sess_203', '192.168.1.112', 'Mozilla/5.0 (Android 11; SM-G991B)', 'https://collide.com/home', NOW() - INTERVAL 2 DAY + INTERVAL 10 HOUR, 1, NOW() - INTERVAL 2 DAY + INTERVAL 10 HOUR + INTERVAL 4 SECOND, 0.0080, 0.8000),
(8, 7, 'sess_204', '192.168.1.113', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://collide.com/content/789', NOW() - INTERVAL 2 DAY + INTERVAL 8 HOUR, 0, NULL, 0.0120, 0.0000),

-- 3天前数据
(9, 8, 'sess_301', '192.168.1.114', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/search?q=finance', NOW() - INTERVAL 3 DAY + INTERVAL 16 HOUR, 1, NOW() - INTERVAL 3 DAY + INTERVAL 16 HOUR + INTERVAL 7 SECOND, 0.0120, 1.2000),
(10, 2, 'sess_302', '192.168.1.115', 'Mozilla/5.0 (iPad; CPU OS 15_0)', 'https://collide.com/category/finance', NOW() - INTERVAL 3 DAY + INTERVAL 13 HOUR, 0, NULL, 0.0100, 0.0000),
(11, 9, 'sess_303', '192.168.1.116', 'Mozilla/5.0 (Android 11; Pixel 5)', 'https://collide.com/feed', NOW() - INTERVAL 3 DAY + INTERVAL 11 HOUR, 1, NOW() - INTERVAL 3 DAY + INTERVAL 11 HOUR + INTERVAL 9 SECOND, 0.0200, 2.0000),

-- 更多历史数据
(1, 10, 'sess_401', '192.168.1.117', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 'https://collide.com/home', NOW() - INTERVAL 4 DAY + INTERVAL 15 HOUR, 0, NULL, 0.0100, 0.0000),
(2, 11, 'sess_402', '192.168.1.118', 'Mozilla/5.0 (iPhone; CPU iPhone OS 15_0)', 'https://collide.com/home', NOW() - INTERVAL 4 DAY + INTERVAL 12 HOUR, 1, NOW() - INTERVAL 4 DAY + INTERVAL 12 HOUR + INTERVAL 11 SECOND, 0.0080, 0.8000),
(3, 3, 'sess_403', '192.168.1.119', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)', 'https://collide.com/content/101', NOW() - INTERVAL 4 DAY + INTERVAL 9 HOUR, 0, NULL, 0.0120, 0.0000),
(12, 4, 'sess_404', '192.168.1.120', 'Mozilla/5.0 (Android 11; SM-G991B)', 'https://collide.com/content/202', NOW() - INTERVAL 4 DAY + INTERVAL 7 HOUR, 1, NOW() - INTERVAL 4 DAY + INTERVAL 7 HOUR + INTERVAL 13 SECOND, 0.0120, 1.2000);

-- ==========================================
-- 更新投放活动统计
-- ==========================================

-- 更新各投放活动的统计数据
UPDATE t_ad_campaign SET 
    total_views = (SELECT COUNT(*) FROM t_ad_record WHERE campaign_id = t_ad_campaign.id),
    total_clicks = (SELECT SUM(is_clicked) FROM t_ad_record WHERE campaign_id = t_ad_campaign.id),
    total_revenue = (SELECT SUM(view_revenue + click_revenue) FROM t_ad_record WHERE campaign_id = t_ad_campaign.id),
    click_rate = CASE 
        WHEN (SELECT COUNT(*) FROM t_ad_record WHERE campaign_id = t_ad_campaign.id) > 0 
        THEN (SELECT SUM(is_clicked) FROM t_ad_record WHERE campaign_id = t_ad_campaign.id) / (SELECT COUNT(*) FROM t_ad_record WHERE campaign_id = t_ad_campaign.id)
        ELSE 0 
    END
WHERE id IN (1,2,3,4,5,6,7,8,9,10,11,12);

-- ==========================================
-- 生成按日统计数据
-- ==========================================

-- 生成最近7天的统计数据
INSERT INTO `t_ad_statistics` (`stat_date`, `campaign_id`, `total_views`, `total_clicks`, `unique_users`, `click_rate`, `total_revenue`, `avg_revenue_per_click`) 
SELECT 
    DATE(view_time) as stat_date,
    campaign_id,
    COUNT(*) as total_views,
    SUM(is_clicked) as total_clicks,
    COUNT(DISTINCT user_id) as unique_users,
    CASE WHEN COUNT(*) > 0 THEN SUM(is_clicked) / COUNT(*) ELSE 0 END as click_rate,
    SUM(view_revenue + click_revenue) as total_revenue,
    CASE WHEN SUM(is_clicked) > 0 THEN SUM(click_revenue) / SUM(is_clicked) ELSE 0 END as avg_revenue_per_click
FROM t_ad_record 
WHERE view_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY DATE(view_time), campaign_id
ORDER BY stat_date DESC, campaign_id;

-- ==========================================
-- 测试查询示例
-- ==========================================

-- 1. 获取首页顶部横幅广告
SELECT '=== 首页顶部横幅广告 ===' as demo;
CALL get_ads_for_placement('home_top_banner', 3);

-- 2. 查看广告投放效果排行
SELECT '=== 广告投放效果排行 ===' as demo;
SELECT 
    c.campaign_name,
    a.ad_name,
    a.ad_type,
    c.total_views,
    c.total_clicks,
    ROUND(c.click_rate * 100, 2) as click_rate_percent,
    c.total_revenue,
    ROUND(c.total_revenue / CASE WHEN c.total_views > 0 THEN c.total_views ELSE 1 END * 1000, 4) as cpm
FROM t_ad_campaign c
JOIN t_ad_template a ON c.ad_id = a.id
WHERE c.status = 'active'
ORDER BY c.total_revenue DESC
LIMIT 10;

-- 3. 查看今日广告统计
SELECT '=== 今日广告统计 ===' as demo;
SELECT 
    COUNT(*) as total_views,
    SUM(is_clicked) as total_clicks,
    ROUND(SUM(is_clicked) * 100.0 / COUNT(*), 2) as click_rate_percent,
    SUM(view_revenue + click_revenue) as total_revenue,
    COUNT(DISTINCT user_id) as unique_users,
    COUNT(DISTINCT campaign_id) as active_campaigns
FROM t_ad_record
WHERE DATE(view_time) = CURDATE();

-- 4. 查看广告类型效果对比
SELECT '=== 广告类型效果对比 ===' as demo;
SELECT 
    a.ad_type,
    COUNT(r.id) as total_views,
    SUM(r.is_clicked) as total_clicks,
    ROUND(SUM(r.is_clicked) * 100.0 / COUNT(r.id), 2) as click_rate_percent,
    SUM(r.view_revenue + r.click_revenue) as total_revenue,
    ROUND(SUM(r.view_revenue + r.click_revenue) / COUNT(r.id) * 1000, 4) as cpm
FROM t_ad_template a
JOIN t_ad_campaign c ON a.id = c.ad_id
JOIN t_ad_record r ON c.id = r.campaign_id
WHERE r.view_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY a.ad_type
ORDER BY total_revenue DESC;

-- 5. 查看广告位效果排行
SELECT '=== 广告位效果排行 ===' as demo;
SELECT 
    p.placement_name,
    p.placement_code,
    p.page_type,
    p.position,
    COUNT(r.id) as total_views,
    SUM(r.is_clicked) as total_clicks,
    ROUND(SUM(r.is_clicked) * 100.0 / COUNT(r.id), 2) as click_rate_percent,
    SUM(r.view_revenue + r.click_revenue) as total_revenue
FROM t_ad_placement p
JOIN t_ad_campaign c ON p.id = c.placement_id
JOIN t_ad_record r ON c.id = r.campaign_id
WHERE r.view_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
  AND p.is_active = 1
GROUP BY p.id
ORDER BY total_revenue DESC;

-- 6. 查看最近7天每日趋势
SELECT '=== 最近7天每日趋势 ===' as demo;
SELECT 
    DATE(view_time) as date,
    COUNT(*) as total_views,
    SUM(is_clicked) as total_clicks,
    ROUND(SUM(is_clicked) * 100.0 / COUNT(*), 2) as click_rate_percent,
    SUM(view_revenue + click_revenue) as total_revenue,
    COUNT(DISTINCT user_id) as unique_users
FROM t_ad_record
WHERE view_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(view_time)
ORDER BY date DESC;

-- 7. 查看用户点击行为分析
SELECT '=== 用户点击行为分析 ===' as demo;
SELECT 
    user_id,
    COUNT(*) as total_views,
    SUM(is_clicked) as total_clicks,
    ROUND(SUM(is_clicked) * 100.0 / COUNT(*), 2) as personal_click_rate,
    SUM(view_revenue + click_revenue) as generated_revenue
FROM t_ad_record
WHERE user_id IS NOT NULL
  AND view_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY user_id
HAVING total_views >= 2
ORDER BY total_clicks DESC, personal_click_rate DESC
LIMIT 10;

-- ==========================================
-- 性能测试
-- ==========================================

-- 测试广告选择性能
SELECT '=== 广告选择性能测试 ===' as demo;
EXPLAIN SELECT 
    c.id as campaign_id,
    a.ad_name,
    a.ad_title,
    a.image_url,
    c.weight
FROM t_ad_campaign c
JOIN t_ad_template a ON c.ad_id = a.id
JOIN t_ad_placement p ON c.placement_id = p.id
WHERE p.placement_code = 'home_top_banner'
  AND c.status = 'active'
  AND a.status = 'active'
ORDER BY c.weight DESC
LIMIT 3;

-- ==========================================
-- 数据清理示例
-- ==========================================

-- 清理过期展示记录（保留最近30天）
-- DELETE FROM t_ad_record 
-- WHERE view_time < DATE_SUB(NOW(), INTERVAL 30 DAY);

-- 清理过期统计数据（保留最近90天）
-- DELETE FROM t_ad_statistics 
-- WHERE stat_date < DATE_SUB(CURDATE(), INTERVAL 90 DAY);

-- ==========================================
-- 广告系统使用示例
-- ==========================================

-- 模拟记录广告展示
-- CALL record_ad_view(1, 123, 'test_session', '192.168.1.200', 'Mozilla/5.0 Test', 'https://collide.com/test');

-- 模拟记录广告点击（需要先获取record_id）
-- CALL record_ad_click(LAST_INSERT_ID());

-- 查看某个投放的详细数据
-- SELECT 
--     campaign_name,
--     total_views,
--     total_clicks,
--     ROUND(click_rate * 100, 2) as click_rate_percent,
--     total_revenue,
--     daily_budget,
--     start_date,
--     end_date
-- FROM t_ad_campaign 
-- WHERE id = 1;