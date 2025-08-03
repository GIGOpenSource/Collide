-- ==========================================
-- Collide 广告系统测试数据
-- 基于 ads-simple.sql 表结构生成的测试数据
-- ==========================================

USE collide;

-- ==========================================
-- 清空现有数据（可选）
-- ==========================================
-- TRUNCATE TABLE `t_ad`;

-- ==========================================
-- 插入测试数据
-- ==========================================

INSERT INTO `t_ad` (`ad_name`, `ad_title`, `ad_description`, `ad_type`, `image_url`, `click_url`, `alt_text`, `target_type`, `is_active`, `sort_order`) VALUES

-- Banner 广告数据
('首页主横幅-科技', '2024最新智能手机发布', '搭载最新AI芯片，拍照更清晰，续航更持久', 'banner', 'https://cdn.collide.com/ads/banner/smartphone-2024.jpg', 'https://shop.collide.com/smartphone', '智能手机广告图', '_blank', 1, 100),
('首页主横幅-教育', '在线编程训练营', '0基础到高级，6个月成为全栈工程师', 'banner', 'https://cdn.collide.com/ads/banner/coding-bootcamp.jpg', 'https://edu.collide.com/bootcamp', '编程训练营广告', '_blank', 1, 95),
('首页主横幅-购物', '年中大促销活动', '全场商品最高立减500元，满减优惠叠加使用', 'banner', 'https://cdn.collide.com/ads/banner/mid-year-sale.jpg', 'https://mall.collide.com/sale', '年中促销广告', '_blank', 1, 90),
('首页主横幅-旅游', '暑期旅游优惠套餐', '三亚海南双人游，机票+酒店只需2999元', 'banner', 'https://cdn.collide.com/ads/banner/summer-travel.jpg', 'https://travel.collide.com/sanya', '暑期旅游广告', '_blank', 1, 85),
('首页主横幅-金融', '投资理财新选择', '年化收益高达8%，风险可控，专业团队管理', 'banner', 'https://cdn.collide.com/ads/banner/investment.jpg', 'https://finance.collide.com/investment', '投资理财广告', '_blank', 0, 80),

-- Sidebar 侧边栏广告数据
('侧边栏-工具推荐', '效率神器工具包', '提升工作效率200%，集成50+实用工具', 'sidebar', 'https://cdn.collide.com/ads/sidebar/productivity-tools.jpg', 'https://tools.collide.com/productivity', '效率工具广告', '_blank', 1, 75),
('侧边栏-课程推荐', 'Python数据分析课程', '从入门到精通，实战项目驱动学习', 'sidebar', 'https://cdn.collide.com/ads/sidebar/python-course.jpg', 'https://learn.collide.com/python', 'Python课程广告', '_self', 1, 70),
('侧边栏-软件下载', '专业设计软件', '支持矢量图形，界面简洁，功能强大', 'sidebar', 'https://cdn.collide.com/ads/sidebar/design-software.jpg', 'https://download.collide.com/design', '设计软件广告', '_blank', 1, 65),
('侧边栏-健康', '健身APP推荐', '个性化训练计划，专业教练在线指导', 'sidebar', 'https://cdn.collide.com/ads/sidebar/fitness-app.jpg', 'https://health.collide.com/fitness', '健身APP广告', '_blank', 1, 60),
('侧边栏-美食', '厨艺学习平台', '跟着大厨学做菜，1000+精品菜谱免费学', 'sidebar', 'https://cdn.collide.com/ads/sidebar/cooking-platform.jpg', 'https://food.collide.com/cooking', '厨艺平台广告', '_blank', 0, 55),

-- Feed 信息流广告数据
('信息流-电商', '潮流运动鞋特惠', '知名品牌运动鞋，限时8折优惠，包邮到家', 'feed', 'https://cdn.collide.com/ads/feed/sports-shoes.jpg', 'https://shop.collide.com/shoes', '运动鞋广告', '_blank', 1, 50),
('信息流-游戏', '新游戏震撼上线', '3D大型RPG游戏，画质逼真，剧情丰富', 'feed', 'https://cdn.collide.com/ads/feed/new-game.jpg', 'https://game.collide.com/rpg', '新游戏广告', '_blank', 1, 45),
('信息流-房产', '市中心精装公寓', '地铁口300米，精装修，拎包入住', 'feed', 'https://cdn.collide.com/ads/feed/apartment.jpg', 'https://house.collide.com/apartment', '房产广告', '_blank', 1, 40),
('信息流-汽车', '新能源汽车试驾', '零排放，超长续航，智能驾驶辅助', 'feed', 'https://cdn.collide.com/ads/feed/electric-car.jpg', 'https://auto.collide.com/electric', '新能源汽车广告', '_blank', 1, 35),
('信息流-教育', '儿童编程启蒙', '6-12岁儿童编程课，趣味游戏化学习', 'feed', 'https://cdn.collide.com/ads/feed/kids-coding.jpg', 'https://kids.collide.com/coding', '儿童编程广告', '_self', 1, 30),

-- Popup 弹窗广告数据
('弹窗-新用户', '新用户专享福利', '注册即送100元代金券，首单免运费', 'popup', 'https://cdn.collide.com/ads/popup/new-user-gift.jpg', 'https://collide.com/register', '新用户福利广告', '_blank', 1, 25),
('弹窗-会员', 'VIP会员限时优惠', '原价299元/年，现在只需99元，享受全站特权', 'popup', 'https://cdn.collide.com/ads/popup/vip-discount.jpg', 'https://collide.com/vip', 'VIP优惠广告', '_self', 1, 20),
('弹窗-活动', '双11预热活动', '提前加购物车，双11当天享受额外9折', 'popup', 'https://cdn.collide.com/ads/popup/double11.jpg', 'https://mall.collide.com/double11', '双11活动广告', '_blank', 0, 15),
('弹窗-APP下载', '手机APP下载', '下载APP享受更多专属优惠和便捷服务', 'popup', 'https://cdn.collide.com/ads/popup/app-download.jpg', 'https://app.collide.com/download', 'APP下载广告', '_blank', 1, 10),
('弹窗-调研', '用户体验调研', '参与调研赢取精美礼品，您的意见很重要', 'popup', 'https://cdn.collide.com/ads/popup/survey.jpg', 'https://survey.collide.com/ux', '调研活动广告', '_blank', 1, 5),

-- 更多多样化数据
('横幅-科技新品', '智能家居套装', '全屋智能化解决方案，语音控制，远程管理', 'banner', 'https://cdn.collide.com/ads/banner/smart-home.jpg', 'https://tech.collide.com/smart-home', '智能家居广告', '_blank', 1, 88),
('侧边栏-书籍', '畅销书籍推荐', '年度畅销书Top100，电子版限时免费阅读', 'sidebar', 'https://cdn.collide.com/ads/sidebar/bestseller-books.jpg', 'https://book.collide.com/bestseller', '畅销书广告', '_self', 1, 68),
('信息流-美妆', '护肤品新品上市', '天然有机成分，温和不刺激，适合敏感肌', 'feed', 'https://cdn.collide.com/ads/feed/skincare.jpg', 'https://beauty.collide.com/skincare', '护肤品广告', '_blank', 1, 42),
('弹窗-服务', '在线客服咨询', '7×24小时在线服务，专业客服随时为您解答', 'popup', 'https://cdn.collide.com/ads/popup/customer-service.jpg', 'https://service.collide.com/chat', '客服咨询广告', '_blank', 1, 18),

-- 一些禁用状态的广告（用于测试）
('过期活动-春节', '春节特惠活动', '春节期间全场8.8折，新年新气象', 'banner', 'https://cdn.collide.com/ads/banner/spring-festival.jpg', 'https://festival.collide.com/spring', '春节活动广告', '_blank', 0, 0),
('停用推广-老版本', '旧版APP推广', '功能丰富的移动应用，已停止推广', 'sidebar', 'https://cdn.collide.com/ads/sidebar/old-app.jpg', 'https://old.collide.com/app', '旧版APP广告', '_blank', 0, 0),
('测试广告-内部', '内部测试广告', '仅供内部测试使用，不对外展示', 'feed', 'https://cdn.collide.com/ads/feed/test-internal.jpg', 'https://test.collide.com/internal', '内部测试广告', '_blank', 0, 0),

-- 无描述的简洁广告
('简洁横幅-logo', '品牌形象展示', NULL, 'banner', 'https://cdn.collide.com/ads/banner/brand-logo.jpg', 'https://about.collide.com/brand', '品牌Logo', '_blank', 1, 95),
('简洁侧边-联系', '联系我们', NULL, 'sidebar', 'https://cdn.collide.com/ads/sidebar/contact-us.jpg', 'https://collide.com/contact', '联系我们', '_self', 1, 50),
('简洁信息流-关注', '关注我们', NULL, 'feed', 'https://cdn.collide.com/ads/feed/follow-us.jpg', 'https://social.collide.com/follow', '关注我们', '_blank', 1, 25);

-- ==========================================
-- 数据统计验证
-- ==========================================

-- 查看插入的数据统计
SELECT 
    ad_type AS '广告类型',
    COUNT(*) AS '数量',
    SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) AS '启用数量',
    SUM(CASE WHEN is_active = 0 THEN 1 ELSE 0 END) AS '禁用数量'
FROM t_ad 
GROUP BY ad_type
ORDER BY ad_type;

-- 查看总数据量
SELECT 
    COUNT(*) AS '总广告数',
    SUM(CASE WHEN is_active = 1 THEN 1 ELSE 0 END) AS '启用广告数',
    SUM(CASE WHEN is_active = 0 THEN 1 ELSE 0 END) AS '禁用广告数'
FROM t_ad;

-- ==========================================
-- 测试查询示例
-- ==========================================

-- 测试：获取所有启用的banner广告，按权重排序
-- SELECT * FROM t_ad WHERE ad_type = 'banner' AND is_active = 1 ORDER BY sort_order DESC, id ASC;

-- 测试：随机获取一个启用的sidebar广告
-- SELECT * FROM t_ad WHERE ad_type = 'sidebar' AND is_active = 1 ORDER BY RAND() LIMIT 1;

-- 测试：获取权重最高的5个启用广告
-- SELECT * FROM t_ad WHERE is_active = 1 ORDER BY sort_order DESC LIMIT 5;

-- 测试：获取所有广告类型
-- SELECT DISTINCT ad_type FROM t_ad WHERE is_active = 1;
