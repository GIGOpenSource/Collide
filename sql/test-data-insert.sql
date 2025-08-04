-- ==========================================
-- Collide 项目统一测试数据插入脚本
-- 包含所有模块的假数据，保证外键关联正确
-- 执行前请确保数据库已创建完成
-- ==========================================

USE collide;

-- 禁用外键检查，加快插入速度
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================
-- 1. 基础数据：分类、标签、任务、广告
-- ==========================================

-- 分类数据（已在category-simple.sql中定义，这里扩展）
INSERT INTO `t_category` (`name`, `description`, `parent_id`, `sort`, `content_count`, `status`) VALUES
('科技', '科技相关内容', 0, 6, 25, 'active'),
('生活', '生活方式内容', 0, 7, 18, 'active'),
('教育', '教育培训内容', 0, 8, 32, 'active'),
('娱乐', '娱乐八卦内容', 0, 9, 15, 'active'),
('体育', '体育运动内容', 0, 10, 12, 'active'),
-- 二级分类
('Web开发', 'Web前后端开发', 6, 1, 8, 'active'),
('移动开发', '移动端应用开发', 6, 2, 6, 'active'),
('数据科学', '数据分析和机器学习', 6, 3, 4, 'active'),
('美食', '美食制作和分享', 7, 1, 10, 'active'),
('旅行', '旅行攻略和游记', 7, 2, 8, 'active');

-- 标签数据（已在tag-simple.sql中定义，这里扩展）
INSERT INTO `t_tag` (`name`, `description`, `tag_type`, `usage_count`, `status`) VALUES
('Java', 'Java编程语言', 'content', 120, 'active'),
('Python', 'Python编程语言', 'content', 95, 'active'),
('React', 'React前端框架', 'content', 85, 'active'),
('Vue', 'Vue前端框架', 'content', 78, 'active'),
('Spring Boot', 'Spring Boot框架', 'content', 65, 'active'),
('Docker', '容器化技术', 'content', 55, 'active'),
('美食制作', '美食制作技巧', 'interest', 45, 'active'),
('旅行拍照', '旅行摄影技巧', 'interest', 38, 'active'),
('健身', '健身运动', 'interest', 42, 'active'),
('音乐', '音乐创作和欣赏', 'interest', 35, 'active');

-- 任务模板数据（已在task-simple.sql中定义）
-- 任务奖励数据（已在task-simple.sql中定义）

-- 广告数据（已在ads-simple.sql中定义，这里扩展）
INSERT INTO `t_ad` (`ad_name`, `ad_title`, `ad_description`, `ad_type`, `image_url`, `click_url`, `alt_text`, `sort_order`) VALUES
('技术课程推广', '全栈开发训练营', '6个月成为全栈工程师', 'banner', 'https://example.com/images/fullstack-banner.jpg', 'https://example.com/fullstack-course', '全栈课程广告', 85),
('生活用品广告', '智能家居产品', '让生活更智能更便捷', 'sidebar', 'https://example.com/images/smart-home.jpg', 'https://example.com/smart-home', '智能家居广告', 75),
('旅游推广', '春季旅游特惠', '多个热门目的地低价出行', 'feed', 'https://example.com/images/travel-spring.jpg', 'https://example.com/travel-deals', '旅游特惠广告', 65);

-- ==========================================
-- 2. 用户数据（统一表设计）
-- ==========================================

-- 用户完整数据（users-simple.sql使用统一表设计）
INSERT INTO `t_user` (
    `username`, `nickname`, `avatar`, `email`, `phone`, `password_hash`, `role`, `status`,
    `bio`, `birthday`, `gender`, `location`, 
    `follower_count`, `following_count`, `content_count`, `like_count`,
    `last_login_time`, `login_count`, `invite_code`, `inviter_id`, `invited_count`
) VALUES
('alice_wang', '爱丽丝', 'https://example.com/avatars/alice.jpg', 'alice.wang@example.com', '13812345678', '$2a$10$encrypted_password_hash', 'blogger', 'active', '热爱编程的全栈开发者', '1995-03-15', 'female', '北京', 1250, 180, 25, 3420, '2024-01-30 14:30:00', 145, 'INV001', NULL, 2),
('bob_chen', '小鲍', 'https://example.com/avatars/bob.jpg', 'bob.chen@example.com', '13887654321', '$2a$10$encrypted_password_hash', 'blogger', 'active', '资深Java工程师，开源爱好者', '1992-07-22', 'male', '上海', 890, 120, 18, 2150, '2024-01-30 09:15:00', 98, 'INV002', 3, 2),
('carol_li', '卡罗尔', 'https://example.com/avatars/carol.jpg', 'carol.li@example.com', '13866778899', '$2a$10$encrypted_password_hash', 'vip', 'active', '前端工程师，UI/UX设计师', '1996-11-08', 'female', '深圳', 2340, 85, 32, 4680, '2024-01-30 16:45:00', 201, 'INV003', 3, 1),
('david_zhang', '大卫', 'https://example.com/avatars/david.jpg', 'david.zhang@example.com', '13855443322', '$2a$10$encrypted_password_hash', 'user', 'active', 'Python数据科学家', '1990-01-30', 'male', '杭州', 756, 200, 15, 1890, '2024-01-29 20:22:00', 87, 'INV004', 4, 1),
('eve_liu', '小伊', 'https://example.com/avatars/eve.jpg', 'eve.liu@example.com', '13844556677', '$2a$10$encrypted_password_hash', 'blogger', 'active', '移动端开发工程师', '1998-09-12', 'female', '广州', 1680, 95, 22, 3210, '2024-01-30 11:30:00', 156, 'INV005', 5, 1),
('frank_wu', '弗兰克', 'https://example.com/avatars/frank.jpg', 'frank.wu@example.com', '13833445566', '$2a$10$encrypted_password_hash', 'user', 'active', 'DevOps工程师，云计算专家', '1988-05-18', 'male', '成都', 445, 150, 12, 980, '2024-01-30 08:45:00', 65, 'INV006', 4, 0),
('grace_huang', '格蕾丝', 'https://example.com/avatars/grace.jpg', 'grace.huang@example.com', '13822334455', '$2a$10$encrypted_password_hash', 'vip', 'active', '产品经理，用户体验专家', '1994-12-03', 'female', '西安', 3250, 65, 28, 5680, '2024-01-30 17:20:00', 234, 'INV007', 6, 1),
('henry_xu', '亨利', 'https://example.com/avatars/henry.jpg', 'henry.xu@example.com', '13811223344', '$2a$10$encrypted_password_hash', 'blogger', 'active', '架构师，技术博主', '1985-08-25', 'male', '南京', 2100, 110, 35, 6420, '2024-01-30 13:10:00', 189, 'INV008', 7, 1),
('iris_lin', '艾瑞丝', 'https://example.com/avatars/iris.jpg', 'iris.lin@example.com', '13800112233', '$2a$10$encrypted_password_hash', 'user', 'active', '设计师，摄影爱好者', '1997-04-14', 'female', '武汉', 1520, 75, 40, 2890, '2024-01-30 15:55:00', 167, 'INV009', 8, 0),
('jack_zhou', '杰克', 'https://example.com/avatars/jack.jpg', 'jack.zhou@example.com', '13899887766', '$2a$10$encrypted_password_hash', 'blogger', 'active', '创业者，技术顾问', '1987-10-07', 'male', '重庆', 980, 220, 20, 1560, '2024-01-30 10:30:00', 112, 'INV010', 9, 0);

-- 用户钱包数据
INSERT INTO `t_user_wallet` (`user_id`, `balance`, `frozen_amount`, `coin_balance`, `coin_total_earned`, `coin_total_spent`, `total_income`, `total_expense`, `status`) VALUES
(3, 256.80, 0.00, 1250, 1450, 200, 500.00, 243.20, 'active'),
(4, 89.50, 0.00, 890, 1200, 310, 200.00, 110.50, 'active'),
(5, 1245.30, 50.00, 2340, 2800, 460, 1500.00, 254.70, 'active'),
(6, 45.60, 0.00, 756, 900, 144, 100.00, 54.40, 'active'),
(7, 678.90, 0.00, 1680, 2100, 420, 800.00, 121.10, 'active'),
(8, 23.40, 0.00, 445, 600, 155, 50.00, 26.60, 'active'),
(9, 2340.50, 100.00, 3250, 4000, 750, 3000.00, 659.50, 'active'),
(10, 1890.80, 0.00, 2100, 2650, 550, 2200.00, 309.20, 'active'),
(11, 567.20, 0.00, 1520, 1800, 280, 700.00, 132.80, 'active'),
(12, 890.70, 0.00, 980, 1300, 320, 1000.00, 109.30, 'active');

-- ==========================================
-- 3. 内容数据
-- ==========================================

-- 内容主表数据（注意：用户ID从3开始，因为1、2是admin和blogger）
INSERT INTO `t_content` (`title`, `description`, `content_type`, `content_data`, `cover_url`, `tags`, `author_id`, `author_nickname`, `author_avatar`, `category_id`, `category_name`, `status`, `review_status`, `view_count`, `like_count`, `comment_count`, `favorite_count`, `score_count`, `score_total`, `publish_time`) VALUES
('Spring Boot微服务实战指南', '从零开始构建企业级微服务架构', 'ARTICLE', '{"sections": ["环境搭建", "服务拆分", "服务通信", "配置管理", "监控告警"]}', 'https://example.com/covers/springboot-guide.jpg', '["Java", "Spring Boot", "微服务"]', 4, '小鲍', 'https://example.com/avatars/bob.jpg', 11, 'Web开发', 'PUBLISHED', 'APPROVED', 3420, 284, 45, 156, 89, 423, '2024-01-25 10:30:00'),
('React Hooks最佳实践', '深入理解React Hooks的使用场景和优化技巧', 'ARTICLE', '{"sections": ["useState", "useEffect", "useContext", "自定义Hooks", "性能优化"]}', 'https://example.com/covers/react-hooks.jpg', '["React", "前端", "JavaScript"]', 5, '卡罗尔', 'https://example.com/avatars/carol.jpg', 11, 'Web开发', 'PUBLISHED', 'APPROVED', 2890, 321, 67, 198, 76, 364, '2024-01-26 14:15:00'),
('Python数据分析入门', '使用Pandas和NumPy进行数据处理和分析', 'ARTICLE', '{"sections": ["数据导入", "数据清洗", "统计分析", "数据可视化", "案例实战"]}', 'https://example.com/covers/python-data.jpg', '["Python", "数据分析", "Pandas"]', 6, '大卫', 'https://example.com/avatars/david.jpg', 13, '数据科学', 'PUBLISHED', 'APPROVED', 2156, 198, 34, 89, 45, 207, '2024-01-27 09:45:00'),
('移动端性能优化秘籍', 'iOS和Android应用性能优化的最佳实践', 'ARTICLE', '{"sections": ["内存管理", "网络优化", "UI渲染", "电池优化", "监控工具"]}', 'https://example.com/covers/mobile-performance.jpg', '["移动开发", "性能优化", "iOS", "Android"]', 7, '小伊', 'https://example.com/avatars/eve.jpg', 12, '移动开发', 'PUBLISHED', 'APPROVED', 1876, 165, 28, 67, 32, 148, '2024-01-28 16:20:00'),
('Docker容器化部署实战', '从开发到生产的容器化最佳实践', 'ARTICLE', '{"sections": ["Docker基础", "镜像构建", "容器编排", "监控日志", "安全配置"]}', 'https://example.com/covers/docker-deploy.jpg', '["Docker", "DevOps", "容器化"]', 8, '弗兰克', 'https://example.com/avatars/frank.jpg', 11, 'Web开发', 'PUBLISHED', 'APPROVED', 1564, 142, 22, 45, 28, 129, '2024-01-29 11:10:00'),
('美食摄影技巧分享', '如何拍出诱人的美食照片', 'VIDEO', '{"duration": "15:30", "quality": "1080p", "subtitle": true}', 'https://example.com/covers/food-photo.jpg', '["摄影", "美食", "技巧"]', 11, '艾瑞丝', 'https://example.com/avatars/iris.jpg', 14, '美食', 'PUBLISHED', 'APPROVED', 2340, 289, 56, 178, 92, 441, '2024-01-25 20:30:00'),
('产品经理进阶之路', '从执行到战略的产品思维升级', 'ARTICLE', '{"sections": ["需求分析", "产品规划", "数据驱动", "团队协作", "行业洞察"]}', 'https://example.com/covers/pm-guide.jpg', '["产品", "管理", "职场"]', 9, '格蕾丝', 'https://example.com/avatars/grace.jpg', 8, '教育', 'PUBLISHED', 'APPROVED', 3654, 412, 89, 234, 156, 748, '2024-01-24 13:45:00'),
('系统架构设计模式', '大型互联网系统的架构设计和演进', 'ARTICLE', '{"sections": ["架构原则", "设计模式", "分布式系统", "高可用设计", "案例分析"]}', 'https://example.com/covers/system-arch.jpg', '["架构", "系统设计", "分布式"]', 10, '亨利', 'https://example.com/avatars/henry.jpg', 11, 'Web开发', 'PUBLISHED', 'APPROVED', 4123, 567, 134, 345, 234, 1124, '2024-01-23 08:20:00'),
('创业公司技术选型', '初创团队的技术栈选择和实践经验', 'ARTICLE', '{"sections": ["技术评估", "团队能力", "成本考量", "扩展性", "成功案例"]}', 'https://example.com/covers/startup-tech.jpg', '["创业", "技术选型", "团队管理"]', 12, '杰克', 'https://example.com/avatars/jack.jpg', 8, '教育', 'PUBLISHED', 'APPROVED', 2789, 234, 67, 123, 89, 423, '2024-01-22 15:30:00'),
('旅行摄影构图技巧', '用手机也能拍出大片感的旅行照片', 'VIDEO', '{"duration": "22:15", "quality": "4K", "subtitle": true}', 'https://example.com/covers/travel-photo.jpg', '["旅行", "摄影", "构图"]', 11, '艾瑞丝', 'https://example.com/avatars/iris.jpg', 15, '旅行', 'PUBLISHED', 'APPROVED', 1967, 178, 43, 89, 67, 312, '2024-01-21 19:45:00');

-- 内容章节数据（为文章类内容添加章节）
INSERT INTO `t_content_chapter` (`content_id`, `chapter_num`, `title`, `content`, `word_count`, `status`) VALUES
(1, 1, '微服务架构概述', '本章介绍微服务架构的基本概念、优势和挑战...', 2500, 'PUBLISHED'),
(1, 2, '环境搭建和工具准备', '搭建开发环境，介绍必要的工具和框架...', 2200, 'PUBLISHED'),
(1, 3, '服务拆分策略', '如何合理地拆分单体应用为微服务...', 2800, 'PUBLISHED'),
(2, 1, 'Hooks基础概念', 'React Hooks的诞生背景和基本使用方法...', 1800, 'PUBLISHED'),
(2, 2, 'useState深度解析', '状态管理Hook的高级用法和最佳实践...', 2100, 'PUBLISHED'),
(2, 3, 'useEffect完全指南', '副作用处理和生命周期替代方案...', 2400, 'PUBLISHED'),
(3, 1, 'Python数据分析环境', '安装配置Anaconda、Jupyter等工具...', 1500, 'PUBLISHED'),
(3, 2, 'Pandas基础操作', '数据读取、筛选、处理的基本方法...', 2300, 'PUBLISHED'),
(8, 1, '架构设计原则', '系统架构设计的基本原则和方法论...', 3200, 'PUBLISHED'),
(8, 2, '分布式系统设计', '分布式系统的核心问题和解决方案...', 3600, 'PUBLISHED');

-- 内容付费配置
INSERT INTO `t_content_payment` (`content_id`, `payment_type`, `coin_price`, `original_price`, `vip_free`, `trial_enabled`, `trial_word_count`, `total_sales`, `total_revenue`, `status`) VALUES
(1, 'COIN_PAY', 50, 80, 1, 1, 500, 145, 7250, 'ACTIVE'),
(2, 'COIN_PAY', 40, 60, 1, 1, 400, 198, 7920, 'ACTIVE'),
(3, 'FREE', 0, 0, 0, 0, 0, 0, 0, 'ACTIVE'),
(4, 'COIN_PAY', 35, 50, 0, 1, 300, 89, 3115, 'ACTIVE'),
(5, 'COIN_PAY', 45, 70, 1, 1, 450, 67, 3015, 'ACTIVE'),
(6, 'FREE', 0, 0, 0, 0, 0, 0, 0, 'ACTIVE'),
(7, 'VIP_FREE', 0, 100, 1, 1, 600, 234, 0, 'ACTIVE'),
(8, 'COIN_PAY', 80, 120, 1, 1, 800, 345, 27600, 'ACTIVE'),
(9, 'COIN_PAY', 60, 90, 0, 1, 600, 123, 7380, 'ACTIVE'),
(10, 'FREE', 0, 0, 0, 0, 0, 0, 0, 'ACTIVE');

-- ==========================================
-- 4. 商品数据（已在goods-simple.sql中定义，这里扩展）
-- ==========================================

INSERT INTO `t_goods` (`name`, `description`, `category_id`, `category_name`, `goods_type`, `price`, `original_price`, `coin_price`, `seller_id`, `seller_name`, `cover_url`, `sales_count`, `view_count`) VALUES
('程序员专用键盘', '机械键盘，Cherry轴，RGB背光', 2, '数码配件', 'goods', 299.00, 399.00, 0, 2, '数码专营店', '/images/keyboard.png', 67, 1234),
('编程学习套装', '包含多本经典编程书籍', 2, '图书音像', 'goods', 158.00, 200.00, 0, 3, '图书专营店', '/images/book-set.png', 89, 2156),
('云服务器优惠券', '阿里云服务器代金券，有效期1年', 1, '虚拟服务', 'goods', 50.00, 100.00, 0, 1, '官方商城', '/images/cloud-coupon.png', 234, 3456);

-- ==========================================
-- 5. 社交数据
-- ==========================================

-- 关注关系数据
INSERT INTO `t_follow` (`follower_id`, `followee_id`, `follower_nickname`, `follower_avatar`, `followee_nickname`, `followee_avatar`, `status`) VALUES
(3, 4, '爱丽丝', 'https://example.com/avatars/alice.jpg', '小鲍', 'https://example.com/avatars/bob.jpg', 'active'),
(3, 5, '爱丽丝', 'https://example.com/avatars/alice.jpg', '卡罗尔', 'https://example.com/avatars/carol.jpg', 'active'),
(3, 10, '爱丽丝', 'https://example.com/avatars/alice.jpg', '亨利', 'https://example.com/avatars/henry.jpg', 'active'),
(4, 5, '小鲍', 'https://example.com/avatars/bob.jpg', '卡罗尔', 'https://example.com/avatars/carol.jpg', 'active'),
(4, 8, '小鲍', 'https://example.com/avatars/bob.jpg', '弗兰克', 'https://example.com/avatars/frank.jpg', 'active'),
(5, 3, '卡罗尔', 'https://example.com/avatars/carol.jpg', '爱丽丝', 'https://example.com/avatars/alice.jpg', 'active'),
(5, 9, '卡罗尔', 'https://example.com/avatars/carol.jpg', '格蕾丝', 'https://example.com/avatars/grace.jpg', 'active'),
(6, 10, '大卫', 'https://example.com/avatars/david.jpg', '亨利', 'https://example.com/avatars/henry.jpg', 'active'),
(7, 11, '小伊', 'https://example.com/avatars/eve.jpg', '艾瑞丝', 'https://example.com/avatars/iris.jpg', 'active'),
(8, 4, '弗兰克', 'https://example.com/avatars/frank.jpg', '小鲍', 'https://example.com/avatars/bob.jpg', 'active'),
(9, 5, '格蕾丝', 'https://example.com/avatars/grace.jpg', '卡罗尔', 'https://example.com/avatars/carol.jpg', 'active'),
(10, 12, '亨利', 'https://example.com/avatars/henry.jpg', '杰克', 'https://example.com/avatars/jack.jpg', 'active'),
(11, 9, '艾瑞丝', 'https://example.com/avatars/iris.jpg', '格蕾丝', 'https://example.com/avatars/grace.jpg', 'active'),
(12, 10, '杰克', 'https://example.com/avatars/jack.jpg', '亨利', 'https://example.com/avatars/henry.jpg', 'active');

-- 社交动态数据
INSERT INTO `t_social_dynamic` (`content`, `dynamic_type`, `images`, `user_id`, `user_nickname`, `user_avatar`, `like_count`, `comment_count`, `share_count`, `status`) VALUES
('刚完成了一个React项目，用Hooks重构了状态管理，性能提升明显！', 'text', NULL, 5, '卡罗尔', 'https://example.com/avatars/carol.jpg', 56, 12, 8, 'normal'),
('今天学习了Spring Boot的自动配置原理，真的是太方便了', 'text', NULL, 4, '小鲍', 'https://example.com/avatars/bob.jpg', 34, 7, 3, 'normal'),
('分享一张今天拍的日落照片，用新学的构图技巧', 'image', '["https://example.com/images/sunset1.jpg", "https://example.com/images/sunset2.jpg"]', 11, '艾瑞丝', 'https://example.com/avatars/iris.jpg', 89, 23, 15, 'normal'),
('Docker部署踩坑记录，网络配置真的很复杂', 'text', NULL, 8, '弗兰克', 'https://example.com/avatars/frank.jpg', 23, 9, 2, 'normal'),
('产品需求又变了，第五次了...不过这次的想法确实不错', 'text', NULL, 9, '格蕾丝', 'https://example.com/avatars/grace.jpg', 67, 18, 5, 'normal'),
('写了一篇关于系统架构的文章，欢迎大家交流讨论', 'share', NULL, 10, '亨利', 'https://example.com/avatars/henry.jpg', 98, 31, 12, 'normal'),
('创业路上的第100天，感谢团队的努力！', 'text', NULL, 12, '杰克', 'https://example.com/avatars/jack.jpg', 45, 14, 6, 'normal'),
('Python数据处理真的很强大，Pandas太好用了', 'text', NULL, 6, '大卫', 'https://example.com/avatars/david.jpg', 28, 5, 1, 'normal'),
('移动端性能优化小技巧分享，内存占用减少30%', 'text', NULL, 7, '小伊', 'https://example.com/avatars/eve.jpg', 41, 11, 7, 'normal'),
('今天的美食摄影课程很棒，学到了很多光影技巧', 'text', NULL, 3, '爱丽丝', 'https://example.com/avatars/alice.jpg', 19, 4, 2, 'normal');

-- 点赞数据
INSERT INTO `t_like` (`like_type`, `target_id`, `user_id`, `target_title`, `target_author_id`, `user_nickname`, `user_avatar`, `status`) VALUES
('CONTENT', 1, 3, 'Spring Boot微服务实战指南', 4, '爱丽丝', 'https://example.com/avatars/alice.jpg', 'active'),
('CONTENT', 1, 5, 'Spring Boot微服务实战指南', 4, '卡罗尔', 'https://example.com/avatars/carol.jpg', 'active'),
('CONTENT', 2, 4, 'React Hooks最佳实践', 5, '小鲍', 'https://example.com/avatars/bob.jpg', 'active'),
('CONTENT', 2, 6, 'React Hooks最佳实践', 5, '大卫', 'https://example.com/avatars/david.jpg', 'active'),
('CONTENT', 8, 3, '系统架构设计模式', 10, '爱丽丝', 'https://example.com/avatars/alice.jpg', 'active'),
('CONTENT', 8, 4, '系统架构设计模式', 10, '小鲍', 'https://example.com/avatars/bob.jpg', 'active'),
('CONTENT', 8, 5, '系统架构设计模式', 10, '卡罗尔', 'https://example.com/avatars/carol.jpg', 'active'),
('DYNAMIC', 1, 4, NULL, 5, '小鲍', 'https://example.com/avatars/bob.jpg', 'active'),
('DYNAMIC', 1, 6, NULL, 5, '大卫', 'https://example.com/avatars/david.jpg', 'active'),
('DYNAMIC', 3, 5, NULL, 11, '卡罗尔', 'https://example.com/avatars/carol.jpg', 'active'),
('DYNAMIC', 3, 9, NULL, 11, '格蕾丝', 'https://example.com/avatars/grace.jpg', 'active'),
('DYNAMIC', 6, 3, NULL, 10, '爱丽丝', 'https://example.com/avatars/alice.jpg', 'active'),
('DYNAMIC', 6, 12, NULL, 10, '杰克', 'https://example.com/avatars/jack.jpg', 'active');

-- 评论数据
INSERT INTO `t_comment` (`comment_type`, `target_id`, `parent_comment_id`, `content`, `user_id`, `user_nickname`, `user_avatar`, `like_count`, `reply_count`, `status`) VALUES
('CONTENT', 1, 0, '这篇文章写得很详细，微服务拆分的策略部分特别有用！', 3, '爱丽丝', 'https://example.com/avatars/alice.jpg', 12, 2, 'NORMAL'),
('CONTENT', 1, 1, '同感，我们公司正在做微服务改造，这些经验很宝贵', 5, '卡罗尔', 'https://example.com/avatars/carol.jpg', 5, 0, 'NORMAL'),
('CONTENT', 1, 1, '作者还有其他相关的文章吗？', 6, '大卫', 'https://example.com/avatars/david.jpg', 3, 0, 'NORMAL'),
('CONTENT', 2, 0, 'React Hooks确实是个好东西，比Class组件简洁多了', 4, '小鲍', 'https://example.com/avatars/bob.jpg', 8, 1, 'NORMAL'),
('CONTENT', 2, 4, '是的，特别是状态管理方面，代码量减少了很多', 7, '小伊', 'https://example.com/avatars/eve.jpg', 4, 0, 'NORMAL'),
('CONTENT', 8, 0, '架构设计真的是一门艺术，需要在性能和复杂度之间找平衡', 12, '杰克', 'https://example.com/avatars/jack.jpg', 15, 3, 'NORMAL'),
('CONTENT', 8, 6, '对的，而且还要考虑团队的技术水平和维护成本', 9, '格蕾丝', 'https://example.com/avatars/grace.jpg', 7, 0, 'NORMAL'),
('DYNAMIC', 1, 0, '期待看到你的项目上线！', 4, '小鲍', 'https://example.com/avatars/bob.jpg', 3, 0, 'NORMAL'),
('DYNAMIC', 3, 0, '照片拍得真棒，请问用的什么相机？', 9, '格蕾丝', 'https://example.com/avatars/grace.jpg', 6, 1, 'NORMAL'),
('DYNAMIC', 3, 9, '其实是用手机拍的，主要是光线和构图比较重要', 11, '艾瑞丝', 'https://example.com/avatars/iris.jpg', 2, 0, 'NORMAL');

-- 收藏数据
INSERT INTO `t_favorite` (`favorite_type`, `target_id`, `user_id`, `target_title`, `target_cover`, `target_author_id`, `user_nickname`, `status`) VALUES
('CONTENT', 1, 3, 'Spring Boot微服务实战指南', 'https://example.com/covers/springboot-guide.jpg', 4, '爱丽丝', 'active'),
('CONTENT', 2, 3, 'React Hooks最佳实践', 'https://example.com/covers/react-hooks.jpg', 5, '爱丽丝', 'active'),
('CONTENT', 8, 3, '系统架构设计模式', 'https://example.com/covers/system-arch.jpg', 10, '爱丽丝', 'active'),
('CONTENT', 1, 5, 'Spring Boot微服务实战指南', 'https://example.com/covers/springboot-guide.jpg', 4, '卡罗尔', 'active'),
('CONTENT', 7, 5, '产品经理进阶之路', 'https://example.com/covers/pm-guide.jpg', 9, '卡罗尔', 'active'),
('CONTENT', 3, 6, 'Python数据分析入门', 'https://example.com/covers/python-data.jpg', 6, '大卫', 'active'),
('CONTENT', 8, 6, '系统架构设计模式', 'https://example.com/covers/system-arch.jpg', 10, '大卫', 'active'),
('GOODS', 13, 4, '程序员专用键盘', '/images/keyboard.png', 2, '小鲍', 'active'),
('GOODS', 14, 5, '编程学习套装', '/images/book-set.png', 3, '卡罗尔', 'active');

-- ==========================================
-- 6. 交互数据
-- ==========================================

-- 用户兴趣标签
INSERT INTO `t_user_interest_tag` (`user_id`, `tag_id`, `interest_score`, `status`) VALUES
(3, 11, 95.5, 'active'), -- 爱丽丝 - Java
(3, 13, 88.3, 'active'), -- 爱丽丝 - React
(4, 11, 98.7, 'active'), -- 小鲍 - Java
(4, 15, 92.1, 'active'), -- 小鲍 - Spring Boot
(5, 13, 96.8, 'active'), -- 卡罗尔 - React
(5, 14, 89.4, 'active'), -- 卡罗尔 - Vue
(6, 12, 94.2, 'active'), -- 大卫 - Python
(7, 12, 78.6, 'active'), -- 小伊 - Python
(8, 16, 91.3, 'active'), -- 弗兰克 - Docker
(9, 7, 85.7, 'active'),  -- 格蕾丝 - 设计
(10, 11, 87.9, 'active'), -- 亨利 - Java
(11, 8, 93.4, 'active'),  -- 艾瑞丝 - 摄影
(11, 17, 76.8, 'active'), -- 艾瑞丝 - 美食制作
(12, 11, 82.5, 'active'); -- 杰克 - Java

-- 内容标签关联
INSERT INTO `t_content_tag` (`content_id`, `tag_id`) VALUES
(1, 11), -- Spring Boot指南 - Java
(1, 15), -- Spring Boot指南 - Spring Boot
(2, 13), -- React Hooks - React
(3, 12), -- Python数据分析 - Python
(4, 12), -- 移动端优化 - Python (移动开发相关)
(5, 16), -- Docker部署 - Docker
(6, 8),  -- 美食摄影 - 摄影
(6, 17), -- 美食摄影 - 美食制作
(7, 7),  -- 产品经理 - 设计
(8, 11), -- 系统架构 - Java
(9, 11), -- 创业技术选型 - Java
(10, 8), -- 旅行摄影 - 摄影
(10, 18); -- 旅行摄影 - 旅行拍照

-- ==========================================
-- 7. 订单和支付数据
-- ==========================================

-- 订单数据（扩展goods-simple.sql中的数据）
INSERT INTO `t_order` (`order_no`, `user_id`, `user_nickname`, `goods_id`, `goods_name`, `goods_type`, `quantity`, `payment_mode`, `cash_amount`, `total_amount`, `final_amount`, `pay_method`, `status`, `pay_status`) VALUES
('ORD202501310010', 3, '爱丽丝', 13, '程序员专用键盘', 'goods', 1, 'cash', 299.00, 299.00, 299.00, 'alipay', 'completed', 'paid'),
('ORD202501310011', 4, '小鲍', 14, '编程学习套装', 'goods', 1, 'cash', 158.00, 158.00, 158.00, 'wechat', 'shipped', 'paid'),
('ORD202501310012', 5, '卡罗尔', 3, '1000金币充值包', 'coin', 1, 'cash', 100.00, 100.00, 100.00, 'alipay', 'completed', 'paid'),
('ORD202501310013', 6, '大卫', 15, '云服务器优惠券', 'goods', 2, 'cash', 100.00, 100.00, 100.00, 'balance', 'completed', 'paid');

-- 支付记录数据
INSERT INTO `t_payment` (`payment_no`, `order_id`, `order_no`, `user_id`, `user_nickname`, `amount`, `pay_method`, `pay_channel`, `third_party_no`, `status`, `pay_time`, `notify_time`) VALUES
('PAY202501310010', 10, 'ORD202501310010', 3, '爱丽丝', 299.00, 'alipay', 'alipay_app', '2024013110001001', 'success', '2024-01-31 10:15:30', '2024-01-31 10:15:35'),
('PAY202501310011', 11, 'ORD202501310011', 4, '小鲍', 158.00, 'wechat', 'wechat_h5', 'wx20240131100110012', 'success', '2024-01-31 11:22:15', '2024-01-31 11:22:18'),
('PAY202501310012', 12, 'ORD202501310012', 5, '卡罗尔', 100.00, 'alipay', 'alipay_pc', '2024013111001002', 'success', '2024-01-31 12:33:45', '2024-01-31 12:33:48'),
('PAY202501310013', 13, 'ORD202501310013', 6, '大卫', 100.00, 'balance', 'balance', 'balance_20240131_001', 'success', '2024-01-31 13:45:20', '2024-01-31 13:45:20');

-- 用户内容购买记录
INSERT INTO `t_user_content_purchase` (`user_id`, `content_id`, `author_id`, `order_id`, `order_no`, `coin_amount`, `original_price`, `status`, `access_count`, `last_access_time`) VALUES
(3, 1, 4, 7, 'ORD202501310007', 50, 80, 'ACTIVE', 5, '2024-01-31 14:30:00'),
(5, 2, 5, NULL, NULL, 40, 60, 'ACTIVE', 3, '2024-01-31 15:45:00'),
(6, 4, 7, NULL, NULL, 35, 50, 'ACTIVE', 2, '2024-01-31 16:20:00'),
(9, 8, 10, NULL, NULL, 80, 120, 'ACTIVE', 7, '2024-01-31 17:10:00'),
(12, 9, 12, NULL, NULL, 60, 90, 'ACTIVE', 4, '2024-01-31 18:25:00');

-- ==========================================
-- 8. 任务和奖励数据
-- ==========================================

-- 用户任务记录（今日任务）
INSERT INTO `t_user_task_record` (`user_id`, `task_id`, `task_date`, `task_name`, `task_type`, `task_category`, `target_count`, `current_count`, `is_completed`, `is_rewarded`, `complete_time`, `reward_time`) VALUES
(3, 1, '2024-01-31', '每日登录', 1, 1, 1, 1, 1, 1, '2024-01-31 08:30:00', '2024-01-31 08:30:05'),
(3, 2, '2024-01-31', '发布内容', 1, 2, 1, 0, 0, 0, NULL, NULL),
(3, 3, '2024-01-31', '点赞互动', 1, 3, 5, 3, 0, 0, NULL, NULL),
(4, 1, '2024-01-31', '每日登录', 1, 1, 1, 1, 1, 1, '2024-01-31 09:15:00', '2024-01-31 09:15:05'),
(4, 2, '2024-01-31', '发布内容', 1, 2, 1, 1, 1, 1, '2024-01-31 10:45:00', '2024-01-31 10:45:05'),
(4, 3, '2024-01-31', '点赞互动', 1, 3, 5, 2, 0, 0, NULL, NULL),
(5, 1, '2024-01-31', '每日登录', 1, 1, 1, 1, 1, 1, '2024-01-31 07:20:00', '2024-01-31 07:20:05'),
(5, 3, '2024-01-31', '点赞互动', 1, 3, 5, 5, 1, 1, '2024-01-31 14:30:00', '2024-01-31 14:30:05'),
(5, 4, '2024-01-31', '评论互动', 1, 3, 3, 2, 0, 0, NULL, NULL),
(6, 1, '2024-01-31', '每日登录', 1, 1, 1, 1, 1, 1, '2024-01-31 08:45:00', '2024-01-31 08:45:05'),
(6, 3, '2024-01-31', '点赞互动', 1, 3, 5, 1, 0, 0, NULL, NULL);

-- 用户奖励记录
INSERT INTO `t_user_reward_record` (`user_id`, `task_record_id`, `reward_source`, `reward_type`, `reward_name`, `reward_amount`, `status`, `grant_time`) VALUES
(3, 1, 1, 1, '金币', 10, 2, '2024-01-31 08:30:05'),
(4, 4, 1, 1, '金币', 10, 2, '2024-01-31 09:15:05'),
(4, 5, 1, 1, '金币', 20, 2, '2024-01-31 10:45:05'),
(4, 5, 1, 4, '经验值', 5, 2, '2024-01-31 10:45:05'),
(5, 7, 1, 1, '金币', 10, 2, '2024-01-31 07:20:05'),
(5, 8, 1, 1, '金币', 15, 2, '2024-01-31 14:30:05'),
(6, 10, 1, 1, '金币', 10, 2, '2024-01-31 08:45:05');

-- ==========================================
-- 9. 消息数据
-- ==========================================

-- 私信消息
INSERT INTO `t_message` (`sender_id`, `receiver_id`, `content`, `message_type`, `status`, `read_time`) VALUES
(3, 4, '你好，看了你的Spring Boot文章，写得很棒！', 'text', 'read', '2024-01-31 10:30:00'),
(4, 3, '谢谢，有什么问题可以随时交流', 'text', 'read', '2024-01-31 10:35:00'),
(5, 3, '我们可以合作一个React项目吗？', 'text', 'sent', NULL),
(6, 10, '关于系统架构的文章很有启发，想请教一些问题', 'text', 'delivered', NULL),
(9, 5, '产品设计上有个想法想和你讨论', 'text', 'read', '2024-01-31 16:20:00'),
(5, 9, '好的，什么时候方便？', 'text', 'sent', NULL),
(11, 7, '移动端性能优化有什么好的工具推荐吗？', 'text', 'read', '2024-01-31 17:45:00'),
(7, 11, '我推荐用Flipper和Charles，都很好用', 'text', 'sent', NULL);

-- 消息会话统计
INSERT INTO `t_message_session` (`user_id`, `other_user_id`, `last_message_id`, `last_message_time`, `unread_count`) VALUES
(3, 4, 2, '2024-01-31 10:35:00', 0),
(4, 3, 2, '2024-01-31 10:35:00', 0),
(3, 5, 3, '2024-01-31 12:15:00', 1),
(5, 3, 3, '2024-01-31 12:15:00', 0),
(6, 10, 4, '2024-01-31 15:20:00', 0),
(10, 6, 4, '2024-01-31 15:20:00', 1),
(5, 9, 6, '2024-01-31 18:30:00', 0),
(9, 5, 6, '2024-01-31 18:30:00', 1),
(7, 11, 8, '2024-01-31 19:15:00', 0),
(11, 7, 8, '2024-01-31 19:15:00', 1);

-- 消息设置
INSERT INTO `t_message_setting` (`user_id`, `allow_stranger_msg`, `auto_read_receipt`, `message_notification`) VALUES
(3, 1, 1, 1),
(4, 1, 1, 1),
(5, 1, 0, 1),
(6, 0, 1, 1),
(7, 1, 1, 0),
(8, 1, 1, 1),
(9, 1, 1, 1),
(10, 0, 0, 1),
(11, 1, 1, 1),
(12, 1, 1, 0);

-- ==========================================
-- 10. 搜索数据
-- ==========================================

-- 搜索历史
INSERT INTO `t_search_history` (`user_id`, `keyword`, `search_type`, `result_count`) VALUES
(3, 'React Hooks', 'content', 15),
(3, 'Spring Boot', 'content', 23),
(3, '微服务', 'content', 8),
(4, 'Java 面试', 'content', 45),
(4, '系统设计', 'content', 12),
(5, 'Vue3', 'content', 18),
(5, '前端优化', 'content', 27),
(6, 'Python pandas', 'content', 31),
(6, '数据分析', 'content', 56),
(7, 'iOS 性能', 'content', 9),
(8, 'Docker 部署', 'content', 19),
(9, '产品设计', 'content', 34),
(10, '架构模式', 'content', 22),
(11, '摄影技巧', 'content', 41),
(12, '创业经验', 'content', 16);

-- 热门搜索
INSERT INTO `t_hot_search` (`keyword`, `search_count`, `trend_score`, `status`) VALUES
('Spring Boot', 1234, 95.8, 'active'),
('React', 987, 88.4, 'active'),
('Python', 856, 82.6, 'active'),
('微服务', 743, 78.9, 'active'),
('Vue', 689, 75.3, 'active'),
('Docker', 567, 69.7, 'active'),
('系统设计', 456, 65.2, 'active'),
('前端优化', 398, 61.8, 'active'),
('数据分析', 345, 58.4, 'active'),
('架构模式', 289, 54.6, 'active'),
('性能优化', 234, 50.9, 'active'),
('移动开发', 198, 47.3, 'active'),
('产品设计', 167, 43.8, 'active'),
('创业经验', 134, 40.2, 'active'),
('摄影技巧', 112, 36.7, 'active');

-- ==========================================
-- 11. 用户拉黑数据
-- ==========================================

-- 用户拉黑关系（示例数据，实际项目中应该很少）
INSERT INTO `t_user_block` (`user_id`, `blocked_user_id`, `user_username`, `blocked_username`, `status`, `reason`) VALUES
(6, 8, 'david_zhang', 'frank_wu', 'active', '频繁发送无关消息'),
(10, 12, 'henry_xu', 'jack_zhou', 'cancelled', '误操作，已解除拉黑');

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================
-- 数据统计信息
-- ==========================================

-- 查看插入的数据统计
SELECT 
  'Users' as table_name, COUNT(*) as record_count FROM t_user
UNION ALL
SELECT 'User Wallets', COUNT(*) FROM t_user_wallet
UNION ALL
SELECT 'User Blocks', COUNT(*) FROM t_user_block
UNION ALL
SELECT 'Categories', COUNT(*) FROM t_category
UNION ALL
SELECT 'Tags', COUNT(*) FROM t_tag
UNION ALL
SELECT 'Contents', COUNT(*) FROM t_content
UNION ALL
SELECT 'Content Chapters', COUNT(*) FROM t_content_chapter
UNION ALL
SELECT 'Goods', COUNT(*) FROM t_goods
UNION ALL
SELECT 'Orders', COUNT(*) FROM t_order
UNION ALL
SELECT 'Payments', COUNT(*) FROM t_payment
UNION ALL
SELECT 'Follows', COUNT(*) FROM t_follow
UNION ALL
SELECT 'Social Dynamics', COUNT(*) FROM t_social_dynamic
UNION ALL
SELECT 'Likes', COUNT(*) FROM t_like
UNION ALL
SELECT 'Comments', COUNT(*) FROM t_comment
UNION ALL
SELECT 'Favorites', COUNT(*) FROM t_favorite
UNION ALL
SELECT 'Messages', COUNT(*) FROM t_message
UNION ALL
SELECT 'Search History', COUNT(*) FROM t_search_history
UNION ALL
SELECT 'Hot Searches', COUNT(*) FROM t_hot_search
UNION ALL
SELECT 'User Tasks', COUNT(*) FROM t_user_task_record
UNION ALL
SELECT 'User Rewards', COUNT(*) FROM t_user_reward_record
UNION ALL
SELECT 'Ads', COUNT(*) FROM t_ad
ORDER BY record_count DESC;

-- ==========================================
-- 完成提示
-- ==========================================

SELECT '测试数据插入完成！' as message, 
       '包含用户、内容、社交、商品、订单等完整业务数据' as description,
       NOW() as insert_time;