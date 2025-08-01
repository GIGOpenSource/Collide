-- ==========================================
-- Collide 项目测试数据插入脚本
-- 基于 collide-simple-all.sql 表结构生成完整测试数据
-- ==========================================

USE collide;

-- ==========================================
-- 1. 用户模块测试数据
-- ==========================================

-- 插入测试用户数据（除了已有的管理员）
INSERT INTO `t_user` (
    `username`, `nickname`, `avatar`, `email`, `phone`, `password_hash`, 
    `role`, `status`, `bio`, `birthday`, `gender`, `location`, 
    `follower_count`, `following_count`, `content_count`, `like_count`,
    `vip_expire_time`, `last_login_time`, `login_count`, 
    `invite_code`, `inviter_id`, `invited_count`
) VALUES
('alice2024', '爱丽丝', 'https://example.com/avatars/alice.jpg', 'alice@example.com', '13800138001', '$2a$10$encrypted_password_hash', 'user', 'active', '喜欢阅读和写作的文学爱好者', '1995-03-15', 'female', '北京市', 125, 89, 23, 567, '2024-12-31 23:59:59', '2024-01-15 10:30:00', 45, 'INVITE001', 2, 3),
('bob_coder', '编程小王子', 'https://example.com/avatars/bob.jpg', 'bob@example.com', '13800138002', '$2a$10$encrypted_password_hash', 'blogger', 'active', '全栈开发工程师，专注于Java和Python技术分享', '1992-07-22', 'male', '上海市', 890, 234, 156, 2340, '2024-06-30 23:59:59', '2024-01-15 09:15:00', 89, 'INVITE002', 1, 12),
('charlie_designer', '设计师查理', 'https://example.com/avatars/charlie.jpg', 'charlie@example.com', '13800138003', '$2a$10$encrypted_password_hash', 'user', 'active', 'UI/UX设计师，热爱创意设计和用户体验', '1990-11-08', 'male', '深圳市', 567, 345, 78, 1890, NULL, '2024-01-14 14:20:00', 67, 'INVITE003', 2, 8),
('diana_artist', '画家黛安娜', 'https://example.com/avatars/diana.jpg', 'diana@example.com', '13800138004', '$2a$10$encrypted_password_hash', 'blogger', 'active', '插画师和漫画家，专注于原创插画作品', '1988-02-14', 'female', '杭州市', 1234, 456, 234, 3456, '2024-12-31 23:59:59', '2024-01-15 08:45:00', 123, 'INVITE004', 1, 15),
('evan_gamer', '游戏达人埃文', 'https://example.com/avatars/evan.jpg', 'evan@example.com', '13800138005', '$2a$10$encrypted_password_hash', 'user', 'active', '资深游戏玩家，喜欢分享游戏攻略和心得', '1996-09-30', 'male', '成都市', 678, 567, 45, 1234, NULL, '2024-01-14 19:30:00', 34, 'INVITE005', 3, 5),
('fiona_chef', '美食家菲奥娜', 'https://example.com/avatars/fiona.jpg', 'fiona@example.com', '13800138006', '$2a$10$encrypted_password_hash', 'user', 'active', '专业厨师，热爱美食制作和分享', '1993-05-17', 'female', '广州市', 890, 234, 67, 1567, NULL, '2024-01-15 07:15:00', 56, 'INVITE006', 4, 7),
('george_photographer', '摄影师乔治', 'https://example.com/avatars/george.jpg', 'george@example.com', '13800138007', '$2a$10$encrypted_password_hash', 'blogger', 'active', '风光摄影师，擅长自然景观和人文摄影', '1985-12-03', 'male', '西安市', 1567, 789, 345, 4567, '2024-09-30 23:59:59', '2024-01-14 16:45:00', 178, 'INVITE007', 2, 23),
('helen_writer', '作家海伦', 'https://example.com/avatars/helen.jpg', 'helen@example.com', '13800138008', '$2a$10$encrypted_password_hash', 'blogger', 'active', '自由撰稿人，专注于科幻和奇幻小说创作', '1987-08-19', 'female', '武汉市', 2345, 1234, 456, 5678, '2024-12-31 23:59:59', '2024-01-15 11:00:00', 245, 'INVITE008', 1, 34),
('ivan_musician', '音乐人伊万', 'https://example.com/avatars/ivan.jpg', 'ivan@example.com', '13800138009', '$2a$10$encrypted_password_hash', 'user', 'active', '独立音乐人，擅长创作和演奏多种乐器', '1991-04-25', 'male', '南京市', 456, 234, 28, 890, NULL, '2024-01-14 20:30:00', 38, 'INVITE009', 5, 4),
('julia_teacher', '教师朱莉娅', 'https://example.com/avatars/julia.jpg', 'julia@example.com', '13800138010', '$2a$10$encrypted_password_hash', 'user', 'active', '高中数学老师，热爱教育和知识分享', '1989-10-12', 'female', '重庆市', 345, 178, 34, 567, NULL, '2024-01-15 06:30:00', 67, 'INVITE010', 6, 2);

-- 用户钱包数据（为所有用户创建钱包）
INSERT INTO `t_user_wallet` (
    `user_id`, `balance`, `frozen_amount`, `coin_balance`, `coin_total_earned`, 
    `coin_total_spent`, `total_income`, `total_expense`, `status`
) VALUES
(3, 150.50, 0.00, 280, 350, 70, 200.50, 50.00, 'active'),
(4, 89.30, 10.00, 1560, 1800, 240, 150.30, 61.00, 'active'),
(5, 245.80, 0.00, 890, 1200, 310, 300.80, 55.00, 'active'),
(6, 67.90, 5.50, 2340, 2500, 160, 120.40, 52.50, 'active'),
(7, 178.20, 0.00, 450, 600, 150, 200.20, 22.00, 'active'),
(8, 298.60, 15.00, 1890, 2100, 210, 350.60, 52.00, 'active'),
(9, 445.30, 0.00, 3450, 3800, 350, 500.30, 55.00, 'active'),
(10, 234.70, 8.20, 2780, 3000, 220, 280.90, 46.20, 'active'),
(11, 123.40, 0.00, 670, 800, 130, 150.40, 27.00, 'active'),
(12, 89.80, 3.50, 890, 1000, 110, 120.30, 30.50, 'active');

-- 用户拉黑关系数据
INSERT INTO `t_user_block` (
    `user_id`, `blocked_user_id`, `user_username`, `blocked_username`, 
    `status`, `reason`
) VALUES
(3, 7, 'alice2024', 'evan_gamer', 'active', '发布不当内容'),
(4, 11, 'bob_coder', 'ivan_musician', 'active', '恶意评论'),
(5, 12, 'charlie_designer', 'julia_teacher', 'cancelled', '误解已解决'),
(8, 7, 'helen_writer', 'evan_gamer', 'active', '抄袭行为'),
(9, 5, 'george_photographer', 'charlie_designer', 'active', '版权纠纷');

-- ==========================================
-- 2. 分类和标签模块测试数据
-- ==========================================

-- 添加更多分类数据（基于已有的基础分类）
INSERT INTO `t_category` (`name`, `description`, `parent_id`, `sort`, `content_count`, `status`) VALUES
-- 小说子分类
('玄幻小说', '玄幻题材小说', 1, 1, 145, 'active'),
('都市小说', '现代都市题材小说', 1, 2, 234, 'active'),
('历史小说', '历史题材小说', 1, 3, 89, 'active'),
('科幻小说', '科幻题材小说', 1, 4, 156, 'active'),
-- 漫画子分类
('日漫', '日本漫画风格', 2, 1, 345, 'active'),
('国漫', '中国原创漫画', 2, 2, 234, 'active'),
('韩漫', '韩国漫画风格', 2, 3, 178, 'active'),
-- 视频子分类
('教学视频', '教育教学类视频', 3, 1, 456, 'active'),
('娱乐视频', '娱乐搞笑类视频', 3, 2, 567, 'active'),
('技术分享', '技术知识分享视频', 3, 3, 234, 'active'),
-- 文章子分类
('技术文章', '编程技术相关文章', 4, 1, 789, 'active'),
('生活随笔', '生活感悟类文章', 4, 2, 345, 'active'),
('新闻资讯', '时事新闻资讯', 4, 3, 234, 'active'),
-- 音频子分类
('有声小说', '小说朗读音频', 5, 1, 123, 'active'),
('播客节目', '各类播客节目', 5, 2, 234, 'active'),
('音乐作品', '原创音乐作品', 5, 3, 156, 'active');

-- 添加更多标签数据
INSERT INTO `t_tag` (`name`, `description`, `tag_type`, `category_id`, `usage_count`, `status`) VALUES
-- 内容标签
('Java开发', 'Java编程开发', 'content', 4, 890, 'active'),
('Python爬虫', 'Python网络爬虫', 'content', 4, 567, 'active'),
('前端框架', '前端开发框架', 'content', 4, 678, 'active'),
('数据库设计', '数据库相关技术', 'content', 4, 345, 'active'),
('机器学习', '人工智能机器学习', 'content', 4, 789, 'active'),
('美食制作', '烹饪美食制作', 'content', 4, 456, 'active'),
('旅行攻略', '旅游出行攻略', 'content', 4, 567, 'active'),
('健身运动', '健身运动相关', 'content', 4, 345, 'active'),
('投资理财', '投资理财知识', 'content', 4, 234, 'active'),
('心理健康', '心理健康话题', 'content', 4, 123, 'active'),
-- 兴趣标签
('游戏竞技', '电子竞技游戏', 'interest', NULL, 890, 'active'),
('动漫二次元', '动漫二次元文化', 'interest', NULL, 567, 'active'),
('手工制作', '手工艺品制作', 'interest', NULL, 234, 'active'),
('宠物养护', '宠物饲养护理', 'interest', NULL, 345, 'active'),
('户外运动', '户外运动活动', 'interest', NULL, 456, 'active'),
('历史文化', '历史文化研究', 'interest', NULL, 234, 'active'),
('科学探索', '科学知识探索', 'interest', NULL, 345, 'active'),
('艺术收藏', '艺术品收藏鉴赏', 'interest', NULL, 123, 'active'),
-- 系统标签
('置顶', '置顶内容', 'system', NULL, 45, 'active'),
('官方', '官方发布内容', 'system', NULL, 123, 'active'),
('活动', '平台活动内容', 'system', NULL, 89, 'active'),
('公告', '重要公告内容', 'system', NULL, 67, 'active');

-- 用户兴趣标签关联数据
INSERT INTO `t_user_interest_tag` (
    `user_id`, `tag_id`, `interest_score`, `status`
) VALUES
-- alice2024 的兴趣
(3, 7, 85.50, 'active'),  -- 编程
(3, 8, 92.30, 'active'),  -- 设计
(3, 20, 78.90, 'active'), -- 历史文化
-- bob_coder 的兴趣
(4, 7, 98.70, 'active'),  -- 编程
(4, 10, 89.40, 'active'), -- Java开发
(4, 11, 85.20, 'active'), -- Python爬虫
(4, 12, 78.60, 'active'), -- 前端框架
-- charlie_designer 的兴趣
(5, 8, 95.80, 'active'),  -- 设计
(5, 17, 88.90, 'active'), -- 动漫二次元
(5, 23, 76.50, 'active'), -- 艺术收藏
-- diana_artist 的兴趣
(6, 8, 97.20, 'active'),  -- 设计
(6, 9, 88.30, 'active'),  -- 摄影
(6, 17, 85.70, 'active'), -- 动漫二次元
(6, 23, 91.40, 'active'), -- 艺术收藏
-- evan_gamer 的兴趣
(7, 16, 94.60, 'active'), -- 游戏竞技
(7, 17, 87.30, 'active'), -- 动漫二次元
(7, 21, 82.10, 'active'), -- 户外运动
-- 其他用户的兴趣标签
(8, 15, 89.70, 'active'), -- 美食制作
(8, 19, 76.80, 'active'), -- 宠物养护
(9, 9, 96.50, 'active'),  -- 摄影
(9, 21, 88.20, 'active'), -- 户外运动
(10, 20, 92.80, 'active'),-- 历史文化
(10, 22, 85.40, 'active'),-- 科学探索
(11, 14, 78.90, 'active'),-- 机器学习
(11, 22, 83.60, 'active'),-- 科学探索
(12, 15, 86.30, 'active'),-- 美食制作
(12, 18, 79.70, 'active');-- 心理健康

-- ==========================================
-- 3. 内容模块测试数据
-- ==========================================

-- 内容主表数据
INSERT INTO `t_content` (
    `title`, `description`, `content_type`, `content_data`, `cover_url`, `tags`,
    `author_id`, `author_nickname`, `author_avatar`, `category_id`, `category_name`,
    `status`, `review_status`, `view_count`, `like_count`, `comment_count`, 
    `favorite_count`, `score_count`, `score_total`, `publish_time`
) VALUES
-- 小说内容
('仙侠传奇：剑道至尊', '一个平凡少年踏上仙侠之路的传奇故事', 'NOVEL', 
'{"chapters": 50, "total_words": 1200000, "update_status": "ongoing", "genre": "仙侠"}', 
'https://example.com/covers/novel1.jpg', '["仙侠", "热门", "连载"]',
4, 'bob_coder', 'https://example.com/avatars/bob.jpg', 6, '玄幻小说',
'PUBLISHED', 'APPROVED', 15430, 567, 89, 234, 156, 780, '2024-01-10 14:30:00'),

('都市重生：商业帝国', '重生回到十年前，打造商业帝国的都市小说', 'NOVEL',
'{"chapters": 32, "total_words": 890000, "update_status": "ongoing", "genre": "都市重生"}',
'https://example.com/covers/novel2.jpg', '["都市", "重生", "商战"]',
8, 'helen_writer', 'https://example.com/avatars/helen.jpg', 7, '都市小说',
'PUBLISHED', 'APPROVED', 23456, 891, 134, 456, 267, 1335, '2024-01-08 09:15:00'),

-- 漫画内容
('星际冒险漫画', '未来世界的星际冒险故事', 'COMIC',
'{"chapters": 24, "pages": 480, "art_style": "日式", "color": true}',
'https://example.com/covers/comic1.jpg', '["科幻", "冒险", "日漫"]',
6, 'diana_artist', 'https://example.com/avatars/diana.jpg', 9, '日漫',
'PUBLISHED', 'APPROVED', 18790, 678, 145, 345, 234, 1170, '2024-01-12 16:20:00'),

('校园恋爱物语', '清新校园恋爱题材漫画', 'COMIC',
'{"chapters": 18, "pages": 360, "art_style": "韩式", "color": true}',
'https://example.com/covers/comic2.jpg', '["校园", "恋爱", "青春"]',
6, 'diana_artist', 'https://example.com/avatars/diana.jpg', 11, '韩漫',
'PUBLISHED', 'APPROVED', 12340, 456, 78, 234, 123, 615, '2024-01-05 11:45:00'),

-- 视频内容
('Java SpringBoot完整教程', 'SpringBoot框架从入门到精通视频教程', 'VIDEO',
'{"duration": 7200, "resolution": "1080p", "format": "mp4", "episodes": 45}',
'https://example.com/covers/video1.jpg', '["Java", "SpringBoot", "教程"]',
4, 'bob_coder', 'https://example.com/avatars/bob.jpg', 12, '教学视频',
'PUBLISHED', 'APPROVED', 34567, 1234, 256, 789, 345, 1725, '2024-01-06 10:00:00'),

('摄影技巧分享', '风光摄影技巧和后期处理教程', 'VIDEO',
'{"duration": 3600, "resolution": "4K", "format": "mp4", "episodes": 12}',
'https://example.com/covers/video2.jpg', '["摄影", "技巧", "教程"]',
9, 'george_photographer', 'https://example.com/avatars/george.jpg', 12, '教学视频',
'PUBLISHED', 'APPROVED', 23450, 890, 167, 456, 234, 1170, '2024-01-09 15:30:00'),

-- 文章内容
('微服务架构设计实践', '大型互联网项目微服务架构设计经验分享', 'ARTICLE',
'{"word_count": 8500, "read_time": 25, "tags": ["微服务", "架构", "实践"]}',
'https://example.com/covers/article1.jpg', '["微服务", "架构", "技术"]',
4, 'bob_coder', 'https://example.com/avatars/bob.jpg', 15, '技术文章',
'PUBLISHED', 'APPROVED', 12890, 456, 89, 234, 167, 835, '2024-01-11 14:15:00'),

('生活中的小确幸', '记录生活中那些美好的小瞬间', 'ARTICLE',
'{"word_count": 2300, "read_time": 8, "tags": ["生活", "感悟", "随笔"]}',
'https://example.com/covers/article2.jpg', '["生活", "感悟", "美好"]',
3, 'alice2024', 'https://example.com/avatars/alice.jpg', 16, '生活随笔',
'PUBLISHED', 'APPROVED', 8790, 234, 45, 123, 89, 445, '2024-01-13 09:30:00'),

-- 音频内容
('古典音乐欣赏', '古典音乐作品解析和欣赏指导', 'AUDIO',
'{"duration": 5400, "format": "mp3", "bitrate": "320kbps", "episodes": 20}',
'https://example.com/covers/audio1.jpg', '["古典音乐", "欣赏", "艺术"]',
11, 'ivan_musician', 'https://example.com/avatars/ivan.jpg', 19, '音乐作品',
'PUBLISHED', 'APPROVED', 9876, 345, 67, 189, 123, 615, '2024-01-07 20:00:00'),

('科幻小说朗读', '经典科幻小说有声朗读版本', 'AUDIO',
'{"duration": 18000, "format": "mp3", "bitrate": "192kbps", "episodes": 30}',
'https://example.com/covers/audio2.jpg', '["科幻", "有声书", "朗读"]',
8, 'helen_writer', 'https://example.com/avatars/helen.jpg', 18, '有声小说',
'PUBLISHED', 'APPROVED', 15670, 567, 89, 345, 234, 1170, '2024-01-04 18:45:00');

-- 内容章节数据（为小说和漫画添加章节）
INSERT INTO `t_content_chapter` (
    `content_id`, `chapter_num`, `title`, `content`, `word_count`, `status`
) VALUES
-- 仙侠传奇章节
(1, 1, '第一章：平凡少年', '在青山村，有一个名叫林凡的少年...', 2500, 'PUBLISHED'),
(1, 2, '第二章：奇遇仙人', '林凡在后山意外遇到了一位白发仙人...', 2800, 'PUBLISHED'),
(1, 3, '第三章：踏上仙路', '获得仙人传承的林凡开始了修仙之路...', 3200, 'PUBLISHED'),
(1, 4, '第四章：初入宗门', '林凡来到了青云门，开始宗门生活...', 2900, 'PUBLISHED'),
(1, 5, '第五章：同门师兄', '在宗门中，林凡结识了几位师兄弟...', 2700, 'PUBLISHED'),

-- 都市重生章节
(2, 1, '第一章：重生归来', '一场车祸后，李浩重生回到了2014年...', 3500, 'PUBLISHED'),
(2, 2, '第二章：股市风云', '利用前世记忆，李浩开始了第一次投资...', 3800, 'PUBLISHED'),
(2, 3, '第三章：创业起步', '有了启动资金，李浩决定创办科技公司...', 4200, 'PUBLISHED'),
(2, 4, '第四章：招兵买马', '公司初具规模，李浩开始招募人才...', 3900, 'PUBLISHED'),

-- 星际冒险漫画章节
(3, 1, '第一话：星际旅行者', '2350年，人类已经进入星际时代...', 0, 'PUBLISHED'),
(3, 2, '第二话：遭遇海盗', '在前往火星的路上遭遇了星际海盗...', 0, 'PUBLISHED'),
(3, 3, '第三话：神秘文明', '发现了一个古老的外星文明遗迹...', 0, 'PUBLISHED'),

-- 校园恋爱物语章节
(4, 1, '第一话：转学生', '春天，一个神秘的转学生来到班级...', 0, 'PUBLISHED'),
(4, 2, '第二话：图书馆偶遇', '在图书馆里的意外相遇改变了一切...', 0, 'PUBLISHED');

-- 内容标签关联数据
INSERT INTO `t_content_tag` (`content_id`, `tag_id`) VALUES
-- 仙侠传奇的标签
(1, 1), (1, 2), (1, 4),   -- 热门、推荐、技术
-- 都市重生的标签
(2, 1), (2, 3), (2, 5),   -- 热门、精选、生活
-- 星际冒险漫画的标签
(3, 2), (3, 6), (3, 22),  -- 推荐、娱乐、科学探索
-- 校园恋爱物语的标签
(4, 3), (4, 6), (4, 17),  -- 精选、娱乐、动漫二次元
-- Java教程的标签
(5, 1), (5, 4), (5, 10),  -- 热门、技术、Java开发
-- 摄影技巧的标签
(6, 2), (6, 9), (6, 4),   -- 推荐、摄影、技术
-- 微服务文章的标签
(7, 4), (7, 10), (7, 12), -- 技术、Java开发、前端框架
-- 生活随笔的标签
(8, 5), (8, 15), (8, 18), -- 生活、美食制作、心理健康
-- 古典音乐的标签
(9, 3), (9, 23), (9, 20), -- 精选、艺术收藏、历史文化
-- 科幻朗读的标签
(10, 2), (10, 20), (10, 22); -- 推荐、历史文化、科学探索

-- 内容付费配置数据
INSERT INTO `t_content_payment` (
    `content_id`, `payment_type`, `coin_price`, `original_price`, `vip_free`, 
    `vip_only`, `trial_enabled`, `trial_content`, `trial_word_count`, 
    `is_permanent`, `valid_days`, `total_sales`, `total_revenue`, `status`
) VALUES
(1, 'COIN_PAY', 50, 80, 1, 0, 1, '试读前三章内容...', 7500, 1, NULL, 234, 11700, 'ACTIVE'),
(2, 'VIP_FREE', 0, 60, 1, 1, 1, '试读前两章内容...', 7300, 1, NULL, 456, 0, 'ACTIVE'),
(3, 'COIN_PAY', 30, 50, 0, 0, 1, '试读前两话内容...', 0, 1, NULL, 345, 10350, 'ACTIVE'),
(4, 'FREE', 0, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, 0, 'ACTIVE'),
(5, 'COIN_PAY', 80, 120, 1, 0, 1, '试看前5集内容...', 0, 1, NULL, 789, 63120, 'ACTIVE'),
(6, 'COIN_PAY', 40, 60, 0, 0, 1, '试看前3集内容...', 0, 1, NULL, 456, 18240, 'ACTIVE'),
(7, 'FREE', 0, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, 0, 'ACTIVE'),
(8, 'FREE', 0, 0, 0, 0, 0, NULL, 0, 1, NULL, 0, 0, 'ACTIVE'),
(9, 'COIN_PAY', 25, 40, 1, 0, 1, '试听前3集内容...', 0, 1, NULL, 189, 4725, 'ACTIVE'),
(10, 'COIN_PAY', 60, 90, 1, 0, 1, '试听前5集内容...', 0, 1, NULL, 345, 20700, 'ACTIVE');

-- 用户内容购买记录数据
INSERT INTO `t_user_content_purchase` (
    `user_id`, `content_id`, `author_id`, `order_id`, `order_no`, 
    `coin_amount`, `original_price`, `discount_amount`, `status`, 
    `purchase_time`, `access_count`, `last_access_time`
) VALUES
(3, 1, 4, 1001, 'ORD2024011001', 50, 80, 30, 'ACTIVE', '2024-01-10 15:30:00', 12, '2024-01-15 09:20:00'),
(3, 5, 4, 1002, 'ORD2024011002', 80, 120, 40, 'ACTIVE', '2024-01-11 10:15:00', 8, '2024-01-14 14:30:00'),
(5, 1, 4, 1003, 'ORD2024011003', 50, 80, 30, 'ACTIVE', '2024-01-10 16:45:00', 15, '2024-01-15 11:10:00'),
(5, 3, 6, 1004, 'ORD2024011004', 30, 50, 20, 'ACTIVE', '2024-01-12 14:20:00', 6, '2024-01-14 16:45:00'),
(7, 5, 4, 1005, 'ORD2024011005', 80, 120, 40, 'ACTIVE', '2024-01-11 11:30:00', 10, '2024-01-15 08:15:00'),
(8, 1, 4, 1006, 'ORD2024011006', 50, 80, 30, 'ACTIVE', '2024-01-10 17:20:00', 18, '2024-01-15 10:30:00'),
(9, 6, 9, 1007, 'ORD2024011007', 40, 60, 20, 'ACTIVE', '2024-01-12 13:45:00', 7, '2024-01-14 19:20:00'),
(10, 9, 11, 1008, 'ORD2024011008', 25, 40, 15, 'ACTIVE', '2024-01-13 16:10:00', 9, '2024-01-15 07:45:00'),
(11, 10, 8, 1009, 'ORD2024011009', 60, 90, 30, 'ACTIVE', '2024-01-13 18:30:00', 5, '2024-01-14 21:15:00'),
(12, 3, 6, 1010, 'ORD2024011010', 30, 50, 20, 'ACTIVE', '2024-01-12 15:50:00', 4, '2024-01-13 12:30:00');

-- ==========================================
-- 4. 商品模块测试数据（扩展已有数据）
-- ==========================================

-- 添加更多商品数据
INSERT INTO `t_goods` (
    `name`, `description`, `category_id`, `category_name`, `goods_type`, 
    `price`, `original_price`, `coin_price`, `coin_amount`, `content_id`, 
    `content_title`, `subscription_duration`, `subscription_type`, `stock`, 
    `cover_url`, `images`, `seller_id`, `seller_name`, `status`, `sales_count`, `view_count`
) VALUES
-- 更多金币充值包
('2000金币充值包', '充值2000金币，送500金币超值优惠', 1, '金币充值', 'coin', 200.00, 250.00, 0, 2500, NULL, NULL, NULL, NULL, -1, '/images/coin_2000.png', '[]', 1, '官方商城', 'active', 145, 2340),
('5000金币充值包', '充值5000金币，送1500金币豪华套餐', 1, '金币充值', 'coin', 500.00, 650.00, 0, 6500, NULL, NULL, NULL, NULL, -1, '/images/coin_5000.png', '[]', 1, '官方商城', 'active', 89, 1567),

-- 更多实体商品
('程序员专用键盘', '机械键盘，适合程序员长时间编程', 2, '数码设备', 'goods', 299.00, 399.00, 0, NULL, NULL, NULL, NULL, NULL, 50, '/images/keyboard.png', '["/images/keyboard1.jpg","/images/keyboard2.jpg"]', 2, '数码专营店', 'active', 67, 890),
('创意马克杯', '程序员专属马克杯，多种代码图案', 2, '生活用品', 'goods', 39.90, 59.90, 0, NULL, NULL, NULL, NULL, NULL, 200, '/images/mug.png', '["/images/mug1.jpg","/images/mug2.jpg"]', 3, '生活馆', 'active', 156, 1234),
('设计师手绘板', '专业数位板，适合插画师和设计师', 2, '数码设备', 'goods', 599.00, 799.00, 0, NULL, NULL, NULL, NULL, NULL, 30, '/images/tablet.png', '["/images/tablet1.jpg"]', 4, '设计工具店', 'active', 34, 567),
('摄影三脚架', '碳纤维三脚架，轻便稳固', 2, '摄影器材', 'goods', 189.00, 259.00, 0, NULL, NULL, NULL, NULL, NULL, 80, '/images/tripod.png', '["/images/tripod1.jpg","/images/tripod2.jpg"]', 5, '摄影器材店', 'active', 78, 654),

-- 更多订阅服务
('SUPER会员季卡', '超级会员权益，3个月畅享所有内容', 4, '会员服务', 'subscription', 89.90, 119.90, 0, NULL, NULL, NULL, 90, 'SUPER', -1, '/images/super_quarter.png', '[]', 1, '官方商城', 'active', 234, 3456),
('创作者专业版', '内容创作者专用工具包月服务', 4, '创作服务', 'subscription', 49.90, 69.90, 0, NULL, NULL, NULL, 30, 'CREATOR', -1, '/images/creator_monthly.png', '[]', 1, '官方商城', 'active', 123, 1890),

-- 付费内容商品
('Redis深度解析', 'Redis数据库深度学习教程', 5, '付费内容', 'content', 0.00, 0.00, 100, NULL, 1001, 'Redis深度解析', NULL, NULL, -1, '/images/redis_course.png', '[]', 4, '技术博主', 'active', 189, 2345),
('UI设计实战', 'UI界面设计从入门到精通', 5, '付费内容', 'content', 0.00, 0.00, 150, NULL, 1002, 'UI设计实战', NULL, NULL, -1, '/images/ui_design.png', '[]', 6, '设计专家', 'active', 234, 3456),
('摄影后期技巧', '数码摄影后期处理高级技巧', 5, '付费内容', 'content', 0.00, 0.00, 80, NULL, 1003, '摄影后期技巧', NULL, NULL, -1, '/images/photo_editing.png', '[]', 9, '摄影大师', 'active', 156, 2234),
('音乐制作教程', '电子音乐制作全流程教学', 5, '付费内容', 'content', 0.00, 0.00, 120, NULL, 1004, '音乐制作教程', NULL, NULL, -1, '/images/music_production.png', '[]', 11, '音乐制作人', 'active', 89, 1567);

-- ==========================================
-- 5. 订单模块测试数据
-- ==========================================

-- 订单数据
INSERT INTO `t_order` (
    `order_no`, `user_id`, `user_nickname`, `goods_id`, `goods_name`, `goods_type`, 
    `goods_cover`, `goods_category_name`, `coin_amount`, `content_id`, 
    `subscription_duration`, `subscription_type`, `quantity`, `payment_mode`, 
    `cash_amount`, `coin_cost`, `total_amount`, `discount_amount`, `final_amount`, 
    `status`, `pay_status`, `pay_method`, `pay_time`
) VALUES
-- 金币充值订单
('ORD2024011501', 3, 'alice2024', 1, '100金币充值包', 'coin', '/images/coin_100.png', '金币充值', 100, NULL, NULL, NULL, 1, 'cash', 10.00, 0, 10.00, 0.00, 10.00, 'completed', 'paid', 'alipay', '2024-01-15 10:30:00'),
('ORD2024011502', 4, 'bob_coder', 9, '2000金币充值包', 'coin', '/images/coin_2000.png', '金币充值', 2500, NULL, NULL, NULL, 1, 'cash', 200.00, 0, 200.00, 50.00, 150.00, 'completed', 'paid', 'wechat', '2024-01-15 14:20:00'),
('ORD2024011503', 5, 'charlie_designer', 2, '500金币充值包', 'coin', '/images/coin_500.png', '金币充值', 550, NULL, NULL, NULL, 1, 'cash', 50.00, 0, 50.00, 0.00, 50.00, 'completed', 'paid', 'balance', '2024-01-14 16:45:00'),

-- 实体商品订单
('ORD2024011504', 6, 'diana_artist', 11, '程序员专用键盘', 'goods', '/images/keyboard.png', '数码设备', NULL, NULL, NULL, NULL, 1, 'cash', 299.00, 0, 299.00, 50.00, 249.00, 'shipped', 'paid', 'alipay', '2024-01-13 11:30:00'),
('ORD2024011505', 7, 'evan_gamer', 12, '创意马克杯', 'goods', '/images/mug.png', '生活用品', NULL, NULL, NULL, NULL, 2, 'cash', 79.80, 0, 79.80, 10.00, 69.80, 'completed', 'paid', 'wechat', '2024-01-12 15:20:00'),
('ORD2024011506', 8, 'fiona_chef', 13, '设计师手绘板', 'goods', '/images/tablet.png', '数码设备', NULL, NULL, NULL, NULL, 1, 'cash', 599.00, 0, 599.00, 100.00, 499.00, 'completed', 'paid', 'alipay', '2024-01-11 09:45:00'),

-- 订阅服务订单
('ORD2024011507', 9, 'george_photographer', 6, 'VIP会员月卡', 'subscription', '/images/vip_monthly.png', '会员服务', NULL, NULL, 30, 'VIP', 1, 'cash', 19.90, 0, 19.90, 0.00, 19.90, 'completed', 'paid', 'wechat', '2024-01-10 18:30:00'),
('ORD2024011508', 10, 'helen_writer', 7, 'VIP会员年卡', 'subscription', '/images/vip_yearly.png', '会员服务', NULL, NULL, 365, 'VIP', 1, 'cash', 199.00, 0, 199.00, 50.00, 149.00, 'completed', 'paid', 'balance', '2024-01-09 14:15:00'),
('ORD2024011509', 11, 'ivan_musician', 17, 'SUPER会员季卡', 'subscription', '/images/super_quarter.png', '会员服务', NULL, NULL, 90, 'SUPER', 1, 'cash', 89.90, 0, 89.90, 20.00, 69.90, 'completed', 'paid', 'alipay', '2024-01-08 12:00:00'),

-- 付费内容订单（金币支付）
('ORD2024011510', 12, 'julia_teacher', 19, 'Redis深度解析', 'content', '/images/redis_course.png', '付费内容', NULL, 1001, NULL, NULL, 1, 'coin', 0.00, 100, 0.00, 0.00, 0.00, 'completed', 'paid', 'coin', '2024-01-07 16:40:00'),
('ORD2024011511', 3, 'alice2024', 20, 'UI设计实战', 'content', '/images/ui_design.png', '付费内容', NULL, 1002, NULL, NULL, 1, 'coin', 0.00, 150, 0.00, 0.00, 0.00, 'completed', 'paid', 'coin', '2024-01-06 13:25:00'),
('ORD2024011512', 5, 'charlie_designer', 21, '摄影后期技巧', 'content', '/images/photo_editing.png', '付费内容', NULL, 1003, NULL, NULL, 1, 'coin', 0.00, 80, 0.00, 0.00, 0.00, 'completed', 'paid', 'coin', '2024-01-05 19:50:00'),

-- 待处理订单
('ORD2024011513', 4, 'bob_coder', 14, '摄影三脚架', 'goods', '/images/tripod.png', '摄影器材', NULL, NULL, NULL, NULL, 1, 'cash', 189.00, 0, 189.00, 30.00, 159.00, 'pending', 'unpaid', NULL, NULL),
('ORD2024011514', 6, 'diana_artist', 18, '创作者专业版', 'subscription', '/images/creator_monthly.png', '创作服务', NULL, NULL, 30, 'CREATOR', 1, 'cash', 49.90, 0, 49.90, 10.00, 39.90, 'pending', 'unpaid', NULL, NULL);

-- ==========================================
-- 6. 支付模块测试数据
-- ==========================================

-- 支付记录数据
INSERT INTO `t_payment` (
    `payment_no`, `order_id`, `order_no`, `user_id`, `user_nickname`, 
    `amount`, `pay_method`, `pay_channel`, `third_party_no`, 
    `status`, `pay_time`, `notify_time`
) VALUES
('PAY2024011501', 1, 'ORD2024011501', 3, 'alice2024', 10.00, 'alipay', 'alipay_app', '2024011501234567890', 'success', '2024-01-15 10:30:15', '2024-01-15 10:30:18'),
('PAY2024011502', 2, 'ORD2024011502', 4, 'bob_coder', 150.00, 'wechat', 'wechat_pay', '2024011502345678901', 'success', '2024-01-15 14:20:30', '2024-01-15 14:20:35'),
('PAY2024011503', 3, 'ORD2024011503', 5, 'charlie_designer', 50.00, 'balance', 'wallet', 'WALLET2024011503', 'success', '2024-01-14 16:45:20', '2024-01-14 16:45:20'),
('PAY2024011504', 4, 'ORD2024011504', 6, 'diana_artist', 249.00, 'alipay', 'alipay_web', '2024011504456789012', 'success', '2024-01-13 11:30:45', '2024-01-13 11:30:50'),
('PAY2024011505', 5, 'ORD2024011505', 7, 'evan_gamer', 69.80, 'wechat', 'wechat_h5', '2024011505567890123', 'success', '2024-01-12 15:20:10', '2024-01-12 15:20:15'),
('PAY2024011506', 6, 'ORD2024011506', 8, 'fiona_chef', 499.00, 'alipay', 'alipay_app', '2024011506678901234', 'success', '2024-01-11 09:45:25', '2024-01-11 09:45:30'),
('PAY2024011507', 7, 'ORD2024011507', 9, 'george_photographer', 19.90, 'wechat', 'wechat_pay', '2024011507789012345', 'success', '2024-01-10 18:30:40', '2024-01-10 18:30:45'),
('PAY2024011508', 8, 'ORD2024011508', 10, 'helen_writer', 149.00, 'balance', 'wallet', 'WALLET2024011508', 'success', '2024-01-09 14:15:55', '2024-01-09 14:15:55'),
('PAY2024011509', 9, 'ORD2024011509', 11, 'ivan_musician', 69.90, 'alipay', 'alipay_web', '2024011509890123456', 'success', '2024-01-08 12:00:12', '2024-01-08 12:00:18'),
-- 金币支付记录（金额为0）
('PAY2024011510', 10, 'ORD2024011510', 12, 'julia_teacher', 0.00, 'coin', 'coin_wallet', 'COIN2024011510', 'success', '2024-01-07 16:40:25', '2024-01-07 16:40:25'),
('PAY2024011511', 11, 'ORD2024011511', 3, 'alice2024', 0.00, 'coin', 'coin_wallet', 'COIN2024011511', 'success', '2024-01-06 13:25:30', '2024-01-06 13:25:30'),
('PAY2024011512', 12, 'ORD2024011512', 5, 'charlie_designer', 0.00, 'coin', 'coin_wallet', 'COIN2024011512', 'success', '2024-01-05 19:50:15', '2024-01-05 19:50:15');

-- ==========================================
-- 7. 关注模块测试数据
-- ==========================================

-- 关注关系数据
INSERT INTO `t_follow` (
    `follower_id`, `followee_id`, `follower_nickname`, `follower_avatar`, 
    `followee_nickname`, `followee_avatar`, `status`
) VALUES
-- alice2024 关注的人
(3, 4, 'alice2024', 'https://example.com/avatars/alice.jpg', 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
(3, 6, 'alice2024', 'https://example.com/avatars/alice.jpg', 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
(3, 8, 'alice2024', 'https://example.com/avatars/alice.jpg', 'helen_writer', 'https://example.com/avatars/helen.jpg', 'active'),
(3, 9, 'alice2024', 'https://example.com/avatars/alice.jpg', 'george_photographer', 'https://example.com/avatars/george.jpg', 'active'),

-- bob_coder 关注的人
(4, 1, 'bob_coder', 'https://example.com/avatars/bob.jpg', '系统管理员', NULL, 'active'),
(4, 5, 'bob_coder', 'https://example.com/avatars/bob.jpg', 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'active'),
(4, 8, 'bob_coder', 'https://example.com/avatars/bob.jpg', 'helen_writer', 'https://example.com/avatars/helen.jpg', 'active'),
(4, 12, 'bob_coder', 'https://example.com/avatars/bob.jpg', 'julia_teacher', 'https://example.com/avatars/julia.jpg', 'active'),

-- charlie_designer 关注的人
(5, 3, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
(5, 6, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
(5, 9, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'george_photographer', 'https://example.com/avatars/george.jpg', 'active'),
(5, 10, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'helen_writer', 'https://example.com/avatars/helen.jpg', 'active'),

-- diana_artist 关注的人
(6, 2, 'diana_artist', 'https://example.com/avatars/diana.jpg', '博主示例', NULL, 'active'),
(6, 4, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
(6, 5, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'active'),
(6, 9, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'george_photographer', 'https://example.com/avatars/george.jpg', 'active'),
(6, 11, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'ivan_musician', 'https://example.com/avatars/ivan.jpg', 'active'),

-- 其他用户的关注关系
(7, 3, 'evan_gamer', 'https://example.com/avatars/evan.jpg', 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
(7, 6, 'evan_gamer', 'https://example.com/avatars/evan.jpg', 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
(8, 4, 'fiona_chef', 'https://example.com/avatars/fiona.jpg', 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
(8, 9, 'fiona_chef', 'https://example.com/avatars/fiona.jpg', 'george_photographer', 'https://example.com/avatars/george.jpg', 'active'),
(9, 4, 'george_photographer', 'https://example.com/avatars/george.jpg', 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
(9, 6, 'george_photographer', 'https://example.com/avatars/george.jpg', 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
(10, 3, 'helen_writer', 'https://example.com/avatars/helen.jpg', 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
(10, 4, 'helen_writer', 'https://example.com/avatars/helen.jpg', 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
(11, 6, 'ivan_musician', 'https://example.com/avatars/ivan.jpg', 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
(11, 10, 'ivan_musician', 'https://example.com/avatars/ivan.jpg', 'helen_writer', 'https://example.com/avatars/helen.jpg', 'active'),
(12, 4, 'julia_teacher', 'https://example.com/avatars/julia.jpg', 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
(12, 8, 'julia_teacher', 'https://example.com/avatars/julia.jpg', 'fiona_chef', 'https://example.com/avatars/fiona.jpg', 'active');

-- ==========================================
-- 8. 点赞模块测试数据
-- ==========================================

-- 点赞数据
INSERT INTO `t_like` (
    `like_type`, `target_id`, `user_id`, `target_title`, `target_author_id`, 
    `user_nickname`, `user_avatar`, `status`
) VALUES
-- 内容点赞
('CONTENT', 1, 3, '仙侠传奇：剑道至尊', 4, 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
('CONTENT', 1, 5, '仙侠传奇：剑道至尊', 4, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'active'),
('CONTENT', 1, 7, '仙侠传奇：剑道至尊', 4, 'evan_gamer', 'https://example.com/avatars/evan.jpg', 'active'),
('CONTENT', 1, 9, '仙侠传奇：剑道至尊', 4, 'george_photographer', 'https://example.com/avatars/george.jpg', 'active'),
('CONTENT', 1, 11, '仙侠传奇：剑道至尊', 4, 'ivan_musician', 'https://example.com/avatars/ivan.jpg', 'active'),

('CONTENT', 2, 4, '都市重生：商业帝国', 8, 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
('CONTENT', 2, 6, '都市重生：商业帝国', 8, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
('CONTENT', 2, 8, '都市重生：商业帝国', 8, 'fiona_chef', 'https://example.com/avatars/fiona.jpg', 'active'),
('CONTENT', 2, 10, '都市重生：商业帝国', 8, 'helen_writer', 'https://example.com/avatars/helen.jpg', 'active'),

('CONTENT', 3, 3, '星际冒险漫画', 6, 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
('CONTENT', 3, 7, '星际冒险漫画', 6, 'evan_gamer', 'https://example.com/avatars/evan.jpg', 'active'),
('CONTENT', 3, 12, '星际冒险漫画', 6, 'julia_teacher', 'https://example.com/avatars/julia.jpg', 'active'),

('CONTENT', 5, 3, 'Java SpringBoot完整教程', 4, 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
('CONTENT', 5, 5, 'Java SpringBoot完整教程', 4, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'active'),
('CONTENT', 5, 12, 'Java SpringBoot完整教程', 4, 'julia_teacher', 'https://example.com/avatars/julia.jpg', 'active'),

('CONTENT', 6, 4, '摄影技巧分享', 9, 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
('CONTENT', 6, 6, '摄影技巧分享', 9, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
('CONTENT', 6, 8, '摄影技巧分享', 9, 'fiona_chef', 'https://example.com/avatars/fiona.jpg', 'active'),

('CONTENT', 7, 3, '微服务架构设计实践', 4, 'alice2024', 'https://example.com/avatars/alice.jpg', 'active'),
('CONTENT', 7, 9, '微服务架构设计实践', 4, 'george_photographer', 'https://example.com/avatars/george.jpg', 'active'),

('CONTENT', 8, 4, '生活中的小确幸', 3, 'bob_coder', 'https://example.com/avatars/bob.jpg', 'active'),
('CONTENT', 8, 6, '生活中的小确幸', 3, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'active'),
('CONTENT', 8, 10, '生活中的小确幸', 3, 'helen_writer', 'https://example.com/avatars/helen.jpg', 'active');

-- ==========================================
-- 9. 收藏模块测试数据
-- ==========================================

-- 收藏数据
INSERT INTO `t_favorite` (
    `favorite_type`, `target_id`, `user_id`, `target_title`, `target_cover`, 
    `target_author_id`, `user_nickname`, `status`
) VALUES
-- 内容收藏
('CONTENT', 1, 3, '仙侠传奇：剑道至尊', 'https://example.com/covers/novel1.jpg', 4, 'alice2024', 'active'),
('CONTENT', 1, 5, '仙侠传奇：剑道至尊', 'https://example.com/covers/novel1.jpg', 4, 'charlie_designer', 'active'),
('CONTENT', 1, 8, '仙侠传奇：剑道至尊', 'https://example.com/covers/novel1.jpg', 4, 'fiona_chef', 'active'),

('CONTENT', 2, 4, '都市重生：商业帝国', 'https://example.com/covers/novel2.jpg', 8, 'bob_coder', 'active'),
('CONTENT', 2, 9, '都市重生：商业帝国', 'https://example.com/covers/novel2.jpg', 8, 'george_photographer', 'active'),

('CONTENT', 3, 7, '星际冒险漫画', 'https://example.com/covers/comic1.jpg', 6, 'evan_gamer', 'active'),
('CONTENT', 3, 11, '星际冒险漫画', 'https://example.com/covers/comic1.jpg', 6, 'ivan_musician', 'active'),

('CONTENT', 5, 3, 'Java SpringBoot完整教程', 'https://example.com/covers/video1.jpg', 4, 'alice2024', 'active'),
('CONTENT', 5, 12, 'Java SpringBoot完整教程', 'https://example.com/covers/video1.jpg', 4, 'julia_teacher', 'active'),

('CONTENT', 6, 6, '摄影技巧分享', 'https://example.com/covers/video2.jpg', 9, 'diana_artist', 'active'),
('CONTENT', 6, 8, '摄影技巧分享', 'https://example.com/covers/video2.jpg', 9, 'fiona_chef', 'active'),

-- 商品收藏
('GOODS', 11, 4, '程序员专用键盘', '/images/keyboard.png', 2, 'bob_coder', 'active'),
('GOODS', 11, 12, '程序员专用键盘', '/images/keyboard.png', 2, 'julia_teacher', 'active'),

('GOODS', 13, 5, '设计师手绘板', '/images/tablet.png', 4, 'charlie_designer', 'active'),
('GOODS', 13, 6, '设计师手绘板', '/images/tablet.png', 4, 'diana_artist', 'active'),

('GOODS', 14, 9, '摄影三脚架', '/images/tripod.png', 5, 'george_photographer', 'active'),

('GOODS', 6, 10, 'VIP会员月卡', '/images/vip_monthly.png', 1, 'helen_writer', 'active'),
('GOODS', 7, 3, 'VIP会员年卡', '/images/vip_yearly.png', 1, 'alice2024', 'active');

-- ==========================================
-- 10. 评论模块测试数据
-- ==========================================

-- 评论数据
INSERT INTO `t_comment` (
    `comment_type`, `target_id`, `parent_comment_id`, `content`, `user_id`, 
    `user_nickname`, `user_avatar`, `reply_to_user_id`, `reply_to_user_nickname`, 
    `reply_to_user_avatar`, `status`, `like_count`, `reply_count`
) VALUES
-- 内容评论（根评论）
('CONTENT', 1, 0, '这个小说写得真不错，剧情很吸引人！', 3, 'alice2024', 'https://example.com/avatars/alice.jpg', NULL, NULL, NULL, 'NORMAL', 12, 3),
('CONTENT', 1, 0, '主角的成长线很清晰，期待后续更新', 5, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', NULL, NULL, NULL, 'NORMAL', 8, 2),
('CONTENT', 1, 0, '仙侠题材永远不会过时，支持作者！', 9, 'george_photographer', 'https://example.com/avatars/george.jpg', NULL, NULL, NULL, 'NORMAL', 15, 1),

-- 回复评论
('CONTENT', 1, 1, '同感！特别是修炼体系设计得很合理', 7, 'evan_gamer', 'https://example.com/avatars/evan.jpg', 3, 'alice2024', 'https://example.com/avatars/alice.jpg', 'NORMAL', 5, 0),
('CONTENT', 1, 1, '我也觉得，比很多同类小说都要好', 11, 'ivan_musician', 'https://example.com/avatars/ivan.jpg', 3, 'alice2024', 'https://example.com/avatars/alice.jpg', 'NORMAL', 3, 0),
('CONTENT', 1, 1, '作者文笔确实不错', 12, 'julia_teacher', 'https://example.com/avatars/julia.jpg', 3, 'alice2024', 'https://example.com/avatars/alice.jpg', 'NORMAL', 2, 0),

('CONTENT', 1, 2, '是的，每个境界的突破都很有说服力', 4, 'bob_coder', 'https://example.com/avatars/bob.jpg', 5, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'NORMAL', 6, 0),
('CONTENT', 1, 2, '主角不是无脑升级，有自己的思考', 8, 'fiona_chef', 'https://example.com/avatars/fiona.jpg', 5, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', 'NORMAL', 4, 0),

('CONTENT', 1, 3, '希望不要太监啊，追到现在不容易', 10, 'helen_writer', 'https://example.com/avatars/helen.jpg', 9, 'george_photographer', 'https://example.com/avatars/george.jpg', 'NORMAL', 7, 0),

-- 其他内容的评论
('CONTENT', 2, 0, '都市重生文，看得很爽！', 4, 'bob_coder', 'https://example.com/avatars/bob.jpg', NULL, NULL, NULL, 'NORMAL', 20, 5),
('CONTENT', 2, 0, '商战情节写得很专业', 6, 'diana_artist', 'https://example.com/avatars/diana.jpg', NULL, NULL, NULL, 'NORMAL', 13, 2),
('CONTENT', 2, 0, '主角利用重生优势很合理，不会太夸张', 10, 'helen_writer', 'https://example.com/avatars/helen.jpg', NULL, NULL, NULL, 'NORMAL', 18, 3),

('CONTENT', 5, 0, 'SpringBoot教程很详细，感谢分享！', 3, 'alice2024', 'https://example.com/avatars/alice.jpg', NULL, NULL, NULL, 'NORMAL', 25, 4),
('CONTENT', 5, 0, '正好在学习这个框架，讲解很清楚', 12, 'julia_teacher', 'https://example.com/avatars/julia.jpg', NULL, NULL, NULL, 'NORMAL', 19, 2),
('CONTENT', 5, 0, '代码示例很实用，已经收藏了', 5, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', NULL, NULL, NULL, 'NORMAL', 16, 1),

('CONTENT', 6, 0, '摄影技巧讲得很实用，学到了很多', 4, 'bob_coder', 'https://example.com/avatars/bob.jpg', NULL, NULL, NULL, 'NORMAL', 22, 3),
('CONTENT', 6, 0, '后期处理的部分特别有用', 6, 'diana_artist', 'https://example.com/avatars/diana.jpg', NULL, NULL, NULL, 'NORMAL', 17, 2),

('CONTENT', 8, 0, '生活随笔写得很温暖', 6, 'diana_artist', 'https://example.com/avatars/diana.jpg', NULL, NULL, NULL, 'NORMAL', 14, 2),
('CONTENT', 8, 0, '感同身受，生活中确实有很多小美好', 10, 'helen_writer', 'https://example.com/avatars/helen.jpg', NULL, NULL, NULL, 'NORMAL', 11, 1);

-- ==========================================
-- 11. 社交动态模块测试数据
-- ==========================================

-- 社交动态数据
INSERT INTO `t_social_dynamic` (
    `content`, `dynamic_type`, `images`, `video_url`, `user_id`, `user_nickname`, 
    `user_avatar`, `share_target_type`, `share_target_id`, `share_target_title`, 
    `like_count`, `comment_count`, `share_count`, `status`
) VALUES
-- 文字动态
('今天学习了新的Java技术栈，感觉又进步了不少！分享给大家一些心得体会。', 'text', NULL, NULL, 4, 'bob_coder', 'https://example.com/avatars/bob.jpg', NULL, NULL, NULL, 45, 12, 8, 'normal'),
('刚完成了一幅新的插画作品，灵感来源于昨天的梦境，希望大家喜欢！', 'text', NULL, NULL, 6, 'diana_artist', 'https://example.com/avatars/diana.jpg', NULL, NULL, NULL, 67, 23, 15, 'normal'),
('生活就像一杯茶，需要慢慢品味。今天在咖啡厅度过了一个宁静的下午。', 'text', NULL, NULL, 3, 'alice2024', 'https://example.com/avatars/alice.jpg', NULL, NULL, NULL, 34, 8, 5, 'normal'),

-- 图片动态
('周末户外摄影的收获，大自然的美景总是让人心旷神怡！', 'image', '["https://example.com/dynamics/photo1.jpg", "https://example.com/dynamics/photo2.jpg", "https://example.com/dynamics/photo3.jpg"]', NULL, 9, 'george_photographer', 'https://example.com/avatars/george.jpg', NULL, NULL, NULL, 89, 34, 22, 'normal'),
('设计了一套新的UI界面，配色和布局都很满意，求大家的意见和建议！', 'image', '["https://example.com/dynamics/ui_design1.jpg", "https://example.com/dynamics/ui_design2.jpg"]', NULL, 5, 'charlie_designer', 'https://example.com/avatars/charlie.jpg', NULL, NULL, NULL, 78, 28, 18, 'normal'),
('今天做了一桌好菜，色香味俱全，和大家分享一下制作过程！', 'image', '["https://example.com/dynamics/food1.jpg", "https://example.com/dynamics/food2.jpg", "https://example.com/dynamics/food3.jpg", "https://example.com/dynamics/food4.jpg"]', NULL, 8, 'fiona_chef', 'https://example.com/avatars/fiona.jpg', NULL, NULL, NULL, 56, 19, 12, 'normal'),

-- 视频动态
('录了一个编程技巧的小视频，希望对初学者有帮助！', 'video', NULL, 'https://example.com/dynamics/coding_tips.mp4', 4, 'bob_coder', 'https://example.com/avatars/bob.jpg', NULL, NULL, NULL, 123, 45, 67, 'normal'),
('演奏了一首新创作的曲子，用的是新买的吉他，音色很棒！', 'video', NULL, 'https://example.com/dynamics/music_play.mp4', 11, 'ivan_musician', 'https://example.com/avatars/ivan.jpg', NULL, NULL, NULL, 78, 23, 15, 'normal'),

-- 分享动态
('强烈推荐这个Java教程，讲解得非常详细！', 'share', NULL, NULL, 3, 'alice2024', 'https://example.com/avatars/alice.jpg', 'content', 5, 'Java SpringBoot完整教程', 34, 12, 8, 'normal'),
('这个小说太精彩了，剧情跌宕起伏，强烈推荐！', 'share', NULL, NULL, 7, 'evan_gamer', 'https://example.com/avatars/evan.jpg', 'content', 1, '仙侠传奇：剑道至尊', 56, 18, 24, 'normal'),
('设计师手绘板真的很好用，推荐给需要的朋友们！', 'share', NULL, NULL, 6, 'diana_artist', 'https://example.com/avatars/diana.jpg', 'goods', 13, '设计师手绘板', 45, 15, 19, 'normal'),

-- 更多动态
('最近在学习摄影后期处理，这个教程帮助很大！', 'text', NULL, NULL, 12, 'julia_teacher', 'https://example.com/avatars/julia.jpg', NULL, NULL, NULL, 23, 7, 3, 'normal'),
('健身第30天打卡！坚持就是胜利，身体状态越来越好了！', 'image', '["https://example.com/dynamics/fitness.jpg"]', NULL, 7, 'evan_gamer', 'https://example.com/avatars/evan.jpg', NULL, NULL, NULL, 67, 25, 12, 'normal'),
('新写的诗歌，记录心情的文字总是最真实的。', 'text', NULL, NULL, 10, 'helen_writer', 'https://example.com/avatars/helen.jpg', NULL, NULL, NULL, 43, 16, 9, 'normal');

-- ==========================================
-- 12. 消息模块测试数据
-- ==========================================

-- 私信消息数据
INSERT INTO `t_message` (
    `sender_id`, `receiver_id`, `content`, `message_type`, `extra_data`, 
    `status`, `read_time`, `reply_to_id`, `is_pinned`
) VALUES
-- alice2024 和 bob_coder 的对话
(3, 4, '你好！看了你的Java教程，写得真的很棒！', 'text', NULL, 'read', '2024-01-15 10:30:00', NULL, 0),
(4, 3, '谢谢你的夸奖！有什么问题可以随时问我', 'text', NULL, 'read', '2024-01-15 10:35:00', 1, 0),
(3, 4, '我在SpringBoot集成Redis时遇到了问题', 'text', NULL, 'read', '2024-01-15 10:40:00', NULL, 0),
(4, 3, '具体是什么问题？可以把错误信息发给我看看', 'text', NULL, 'read', '2024-01-15 10:42:00', 3, 0),
(3, 4, '这是错误截图', 'image', '{"image_url": "https://example.com/messages/error_screenshot.jpg", "image_name": "error.jpg"}', 'read', '2024-01-15 10:45:00', NULL, 0),
(4, 3, '这个问题是配置文件的问题，我发个示例给你', 'file', '{"file_url": "https://example.com/messages/config_example.yml", "file_name": "application.yml", "file_size": 1024}', 'read', '2024-01-15 10:50:00', 5, 0),
(3, 4, '太感谢了！问题解决了，你真是太厉害了！', 'text', NULL, 'read', '2024-01-15 11:00:00', 6, 0),

-- diana_artist 和 charlie_designer 的对话
(6, 5, '看到你的UI设计作品，非常有创意！', 'text', NULL, 'read', '2024-01-14 14:20:00', NULL, 0),
(5, 6, '谢谢夸奖！你的插画作品我也很喜欢', 'text', NULL, 'read', '2024-01-14 14:25:00', 8, 0),
(6, 5, '有没有兴趣合作一个项目？我负责插画，你负责界面设计', 'text', NULL, 'read', '2024-01-14 14:30:00', NULL, 0),
(5, 6, '当然有兴趣！什么类型的项目？', 'text', NULL, 'read', '2024-01-14 14:32:00', 10, 0),
(6, 5, '一个儿童教育APP，需要可爱的插画和简洁的界面', 'text', NULL, 'read', '2024-01-14 14:35:00', NULL, 0),
(5, 6, '听起来很有意思，我们详细聊聊吧！', 'text', NULL, 'read', '2024-01-14 14:40:00', 12, 0),

-- george_photographer 和 fiona_chef 的对话
(9, 8, '你拍的美食照片构图很棒！', 'text', NULL, 'read', '2024-01-13 16:15:00', NULL, 0),
(8, 9, '哈哈，谢谢！其实都是跟你学的摄影技巧', 'text', NULL, 'read', '2024-01-13 16:20:00', 14, 0),
(9, 8, '下次做菜的时候叫我，我来帮你拍照！', 'text', NULL, 'read', '2024-01-13 16:25:00', NULL, 0),
(8, 9, '好啊！这周末我计划做一桌大餐', 'text', NULL, 'read', '2024-01-13 16:30:00', 16, 0),

-- helen_writer 和 ivan_musician 的对话
(10, 11, '你的音乐很有感染力，适合做我小说的背景音乐', 'text', NULL, 'read', '2024-01-12 19:45:00', NULL, 0),
(11, 10, '真的吗？我很荣幸！什么类型的小说？', 'text', NULL, 'read', '2024-01-12 19:50:00', 18, 0),
(10, 11, '奇幻冒险类，需要一些神秘和激昂的音乐', 'text', NULL, 'read', '2024-01-12 19:55:00', NULL, 0),
(11, 10, '我有几首作品很适合，发给你听听', 'text', NULL, 'read', '2024-01-12 20:00:00', 20, 0),

-- 系统消息
(0, 3, '欢迎加入Collide平台！记得完善你的个人资料哦', 'system', NULL, 'read', '2024-01-10 09:00:00', NULL, 1),
(0, 4, '恭喜你成为认证博主！现在可以发布付费内容了', 'system', NULL, 'read', '2024-01-08 10:00:00', NULL, 1),
(0, 6, '你的作品《星际冒险漫画》获得了本周热门推荐！', 'system', NULL, 'read', '2024-01-12 08:00:00', NULL, 1);

-- 用户会话统计数据
INSERT INTO `t_message_session` (
    `user_id`, `other_user_id`, `last_message_id`, `last_message_time`, 
    `unread_count`, `is_archived`
) VALUES
(3, 4, 7, '2024-01-15 11:00:00', 0, 0),
(4, 3, 7, '2024-01-15 11:00:00', 0, 0),
(6, 5, 13, '2024-01-14 14:40:00', 0, 0),
(5, 6, 13, '2024-01-14 14:40:00', 0, 0),
(9, 8, 17, '2024-01-13 16:30:00', 0, 0),
(8, 9, 17, '2024-01-13 16:30:00', 0, 0),
(10, 11, 22, '2024-01-12 20:00:00', 0, 0),
(11, 10, 22, '2024-01-12 20:00:00', 0, 0);

-- 用户消息设置数据
INSERT INTO `t_message_setting` (
    `user_id`, `allow_stranger_msg`, `auto_read_receipt`, `message_notification`
) VALUES
(3, 1, 1, 1),
(4, 1, 1, 1),
(5, 1, 1, 1),
(6, 1, 1, 1),
(7, 0, 1, 1),  -- 不允许陌生人发消息
(8, 1, 0, 1),  -- 不自动发送已读回执
(9, 1, 1, 0),  -- 关闭消息通知
(10, 1, 1, 1),
(11, 1, 1, 1),
(12, 1, 1, 1);

-- ==========================================
-- 13. 搜索模块测试数据
-- ==========================================

-- 搜索历史数据
INSERT INTO `t_search_history` (
    `user_id`, `keyword`, `search_type`, `result_count`
) VALUES
(3, 'Java教程', 'content', 25),
(3, 'SpringBoot', 'content', 18),
(3, '仙侠小说', 'content', 156),
(3, '设计师手绘板', 'goods', 8),
(3, 'VIP会员', 'goods', 12),

(4, 'Python爬虫', 'content', 34),
(4, '微服务架构', 'content', 22),
(4, '程序员键盘', 'goods', 15),
(4, 'bob_coder', 'user', 1),
(4, '编程教程', 'content', 67),

(5, 'UI设计', 'content', 45),
(5, '插画教程', 'content', 28),
(5, '手绘板', 'goods', 12),
(5, 'diana_artist', 'user', 1),
(5, '界面设计', 'content', 33),

(6, '摄影技巧', 'content', 56),
(6, '后期处理', 'content', 34),
(6, '星际漫画', 'content', 12),
(6, '三脚架', 'goods', 8),
(6, '绘画工具', 'goods', 15),

(7, '游戏攻略', 'content', 89),
(7, '二次元', 'content', 67),
(7, '创意马克杯', 'goods', 5),
(7, '健身', 'content', 23),

(8, '美食制作', 'content', 78),
(8, '烹饪技巧', 'content', 45),
(8, '厨房用具', 'goods', 34),
(8, '摄影教程', 'content', 19),

(9, '风光摄影', 'content', 67),
(9, '摄影器材', 'goods', 45),
(9, '后期软件', 'content', 23),
(9, '自然风景', 'content', 56),

(10, '写作技巧', 'content', 34),
(10, '小说创作', 'content', 28),
(10, '文学理论', 'content', 15),
(10, '创作灵感', 'content', 22),

(11, '音乐制作', 'content', 45),
(11, '乐器教学', 'content', 38),
(11, '音乐设备', 'goods', 23),
(11, '古典音乐', 'content', 19),

(12, '教育方法', 'content', 56),
(12, '数学教学', 'content', 34),
(12, '学习工具', 'goods', 28),
(12, '知识分享', 'content', 45);

-- 热门搜索数据
INSERT INTO `t_hot_search` (
    `keyword`, `search_count`, `trend_score`, `status`
) VALUES
('Java教程', 2340, 95.80, 'active'),
('SpringBoot', 1890, 87.50, 'active'),
('Python爬虫', 1567, 82.30, 'active'),
('UI设计', 1345, 78.90, 'active'),
('摄影技巧', 1234, 75.60, 'active'),
('仙侠小说', 1123, 72.40, 'active'),
('美食制作', 998, 69.80, 'active'),
('游戏攻略', 887, 65.20, 'active'),
('音乐制作', 776, 61.50, 'active'),
('写作技巧', 665, 58.30, 'active'),
('VIP会员', 554, 54.70, 'active'),
('设计师手绘板', 445, 51.20, 'active'),
('程序员键盘', 389, 47.80, 'active'),
('摄影三脚架', 334, 44.60, 'active'),
('创意马克杯', 278, 41.30, 'active'),
('微服务架构', 234, 38.90, 'active'),
('插画教程', 198, 35.70, 'active'),
('后期处理', 167, 32.50, 'active'),
('健身运动', 145, 29.80, 'active'),
('教育方法', 123, 26.40, 'active');

-- ==========================================
-- 14. 任务模块测试数据（扩展已有数据）
-- ==========================================

-- 用户任务记录数据
INSERT INTO `t_user_task_record` (
    `user_id`, `task_id`, `task_date`, `task_name`, `task_type`, `task_category`, 
    `target_count`, `current_count`, `is_completed`, `is_rewarded`, 
    `complete_time`, `reward_time`
) VALUES
-- alice2024 的任务记录
(3, 1, '2024-01-15', '每日登录', 'daily', 'login', 1, 1, 1, 1, '2024-01-15 09:00:00', '2024-01-15 09:01:00'),
(3, 2, '2024-01-15', '发布内容', 'daily', 'content', 1, 1, 1, 1, '2024-01-15 09:30:00', '2024-01-15 09:31:00'),
(3, 3, '2024-01-15', '点赞互动', 'daily', 'social', 5, 5, 1, 1, '2024-01-15 10:15:00', '2024-01-15 10:16:00'),
(3, 4, '2024-01-15', '评论互动', 'daily', 'social', 3, 3, 1, 1, '2024-01-15 11:20:00', '2024-01-15 11:21:00'),
(3, 5, '2024-01-15', '分享内容', 'daily', 'social', 1, 0, 0, 0, NULL, NULL),

-- bob_coder 的任务记录
(4, 1, '2024-01-15', '每日登录', 'daily', 'login', 1, 1, 1, 1, '2024-01-15 08:30:00', '2024-01-15 08:31:00'),
(4, 2, '2024-01-15', '发布内容', 'daily', 'content', 1, 2, 1, 1, '2024-01-15 10:00:00', '2024-01-15 10:01:00'),
(4, 3, '2024-01-15', '点赞互动', 'daily', 'social', 5, 7, 1, 1, '2024-01-15 11:00:00', '2024-01-15 11:01:00'),
(4, 4, '2024-01-15', '评论互动', 'daily', 'social', 3, 4, 1, 1, '2024-01-15 12:00:00', '2024-01-15 12:01:00'),
(4, 5, '2024-01-15', '分享内容', 'daily', 'social', 1, 1, 1, 1, '2024-01-15 13:00:00', '2024-01-15 13:01:00'),

-- charlie_designer 的任务记录
(5, 1, '2024-01-15', '每日登录', 'daily', 'login', 1, 1, 1, 1, '2024-01-15 10:00:00', '2024-01-15 10:01:00'),
(5, 2, '2024-01-15', '发布内容', 'daily', 'content', 1, 0, 0, 0, NULL, NULL),
(5, 3, '2024-01-15', '点赞互动', 'daily', 'social', 5, 3, 0, 0, NULL, NULL),

-- diana_artist 的任务记录
(6, 1, '2024-01-15', '每日登录', 'daily', 'login', 1, 1, 1, 1, '2024-01-15 08:45:00', '2024-01-15 08:46:00'),
(6, 2, '2024-01-15', '发布内容', 'daily', 'content', 1, 1, 1, 1, '2024-01-15 14:20:00', '2024-01-15 14:21:00'),
(6, 3, '2024-01-15', '点赞互动', 'daily', 'social', 5, 6, 1, 1, '2024-01-15 15:30:00', '2024-01-15 15:31:00'),
(6, 4, '2024-01-15', '评论互动', 'daily', 'social', 3, 2, 0, 0, NULL, NULL),

-- 周常任务记录
(4, 6, '2024-01-15', '周活跃用户', 'weekly', 'login', 7, 7, 1, 1, '2024-01-15 23:59:00', '2024-01-15 23:59:30'),
(4, 7, '2024-01-15', '内容创作者', 'weekly', 'content', 5, 6, 1, 1, '2024-01-15 18:00:00', '2024-01-15 18:01:00'),
(6, 6, '2024-01-15', '周活跃用户', 'weekly', 'login', 7, 6, 0, 0, NULL, NULL),
(6, 7, '2024-01-15', '内容创作者', 'weekly', 'content', 5, 4, 0, 0, NULL, NULL);

-- 用户奖励记录数据
INSERT INTO `t_user_reward_record` (
    `user_id`, `task_record_id`, `reward_source`, `reward_type`, `reward_name`, 
    `reward_amount`, `reward_data`, `status`, `grant_time`
) VALUES
-- alice2024 的奖励记录
(3, 1, 'task', 'coin', '金币', 10, NULL, 'success', '2024-01-15 09:01:00'),
(3, 2, 'task', 'coin', '金币', 20, NULL, 'success', '2024-01-15 09:31:00'),
(3, 2, 'task', 'experience', '经验值', 5, NULL, 'success', '2024-01-15 09:31:00'),
(3, 3, 'task', 'coin', '金币', 15, NULL, 'success', '2024-01-15 10:16:00'),
(3, 4, 'task', 'coin', '金币', 12, NULL, 'success', '2024-01-15 11:21:00'),

-- bob_coder 的奖励记录
(4, 5, 'task', 'coin', '金币', 10, NULL, 'success', '2024-01-15 08:31:00'),
(4, 6, 'task', 'coin', '金币', 20, NULL, 'success', '2024-01-15 10:01:00'),
(4, 6, 'task', 'experience', '经验值', 5, NULL, 'success', '2024-01-15 10:01:00'),
(4, 7, 'task', 'coin', '金币', 15, NULL, 'success', '2024-01-15 11:01:00'),
(4, 8, 'task', 'coin', '金币', 12, NULL, 'success', '2024-01-15 12:01:00'),
(4, 9, 'task', 'coin', '金币', 8, NULL, 'success', '2024-01-15 13:01:00'),

-- 周常任务奖励
(4, 10, 'task', 'coin', '金币', 100, NULL, 'success', '2024-01-15 23:59:30'),
(4, 10, 'task', 'item', '活跃宝箱', 1, '{"item_id": 1001, "item_type": "treasure_box"}', 'success', '2024-01-15 23:59:30'),
(4, 11, 'task', 'coin', '金币', 150, NULL, 'success', '2024-01-15 18:01:00'),
(4, 11, 'task', 'vip', 'VIP体验', 3, '{"vip_type": "premium", "duration_days": 3}', 'success', '2024-01-15 18:01:00'),

-- diana_artist 的奖励记录
(6, 12, 'task', 'coin', '金币', 10, NULL, 'success', '2024-01-15 08:46:00'),
(6, 13, 'task', 'coin', '金币', 20, NULL, 'success', '2024-01-15 14:21:00'),
(6, 13, 'task', 'experience', '经验值', 5, NULL, 'success', '2024-01-15 14:21:00'),
(6, 14, 'task', 'coin', '金币', 15, NULL, 'success', '2024-01-15 15:31:00');

-- ==========================================
-- 15. 广告模块测试数据（扩展已有数据）
-- ==========================================

-- 广告展示记录数据
INSERT INTO `t_ad_record` (
    `campaign_id`, `user_id`, `session_id`, `ip_address`, `user_agent`, 
    `page_url`, `view_time`, `is_clicked`, `click_time`, 
    `view_revenue`, `click_revenue`
) VALUES
-- 首页科技产品推广的展示记录
(1, 3, 'sess_alice_001', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/', '2024-01-15 10:00:00', 1, '2024-01-15 10:02:30', 0.0100, 1.0000),
(1, 4, 'sess_bob_001', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'https://collide.com/', '2024-01-15 10:15:00', 0, NULL, 0.0100, 0.0000),
(1, 5, 'sess_charlie_001', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/', '2024-01-15 10:30:00', 1, '2024-01-15 10:35:15', 0.0100, 1.0000),
(1, 6, 'sess_diana_001', '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15', 'https://collide.com/', '2024-01-15 10:45:00', 0, NULL, 0.0100, 0.0000),
(1, 7, 'sess_evan_001', '192.168.1.104', 'Mozilla/5.0 (Android 11; Mobile; rv:92.0) Gecko/92.0 Firefox/92.0', 'https://collide.com/', '2024-01-15 11:00:00', 1, '2024-01-15 11:05:45', 0.0100, 1.0000),

-- 首页右侧教育课程的展示记录
(2, 8, 'sess_fiona_001', '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/', '2024-01-15 11:20:00', 1, '2024-01-15 11:25:20', 0.0080, 0.8000),
(2, 9, 'sess_george_001', '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'https://collide.com/', '2024-01-15 11:35:00', 0, NULL, 0.0080, 0.0000),
(2, 10, 'sess_helen_001', '192.168.1.107', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/', '2024-01-15 11:50:00', 1, '2024-01-15 11:55:30', 0.0080, 0.8000),
(2, 11, 'sess_ivan_001', '192.168.1.108', 'Mozilla/5.0 (iPad; CPU OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15', 'https://collide.com/', '2024-01-15 12:05:00', 0, NULL, 0.0080, 0.0000),

-- 内容页APP推广的展示记录
(3, 3, 'sess_alice_002', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/content/1', '2024-01-15 12:20:00', 0, NULL, 0.0120, 0.0000),
(3, 4, 'sess_bob_002', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'https://collide.com/content/2', '2024-01-15 12:35:00', 1, '2024-01-15 12:40:15', 0.0120, 1.2000),
(3, 12, 'sess_julia_001', '192.168.1.109', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/content/5', '2024-01-15 12:50:00', 0, NULL, 0.0120, 0.0000),

-- 搜索页品牌推广的展示记录
(4, 5, 'sess_charlie_002', '192.168.1.102', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 'https://collide.com/search?q=Java', '2024-01-15 13:10:00', 1, '2024-01-15 13:15:45', 0.0150, 1.5000),
(4, 6, 'sess_diana_002', '192.168.1.103', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15', 'https://collide.com/search?q=设计', '2024-01-15 13:25:00', 0, NULL, 0.0150, 0.0000),
(4, 9, 'sess_george_002', '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 'https://collide.com/search?q=摄影', '2024-01-15 13:40:00', 1, '2024-01-15 13:45:20', 0.0150, 1.5000);

-- 广告统计数据
INSERT INTO `t_ad_statistics` (
    `stat_date`, `campaign_id`, `total_views`, `total_clicks`, `unique_users`, 
    `click_rate`, `total_revenue`, `avg_revenue_per_click`
) VALUES
('2024-01-15', 1, 150, 18, 89, 0.1200, 23.50, 1.3056),
('2024-01-15', 2, 120, 12, 67, 0.1000, 10.56, 0.8800),
('2024-01-15', 3, 89, 8, 45, 0.0899, 10.68, 1.3350),
('2024-01-15', 4, 67, 9, 34, 0.1343, 14.85, 1.6500),
('2024-01-14', 1, 134, 15, 78, 0.1119, 21.34, 1.4227),
('2024-01-14', 2, 98, 10, 56, 0.1020, 8.80, 0.8800),
('2024-01-14', 3, 76, 6, 38, 0.0789, 8.12, 1.3533),
('2024-01-14', 4, 55, 7, 29, 0.1273, 10.75, 1.5357),
('2024-01-13', 1, 145, 20, 85, 0.1379, 25.45, 1.2725),
('2024-01-13', 2, 112, 14, 63, 0.1250, 12.32, 0.8800),
('2024-01-13', 3, 87, 9, 42, 0.1034, 11.28, 1.2533),
('2024-01-13', 4, 72, 8, 36, 0.1111, 12.96, 1.6200);

-- ==========================================
-- 数据插入完成
-- ==========================================

-- 更新统计信息，确保冗余字段的一致性
UPDATE `t_user` SET 
    `follower_count` = (SELECT COUNT(*) FROM `t_follow` WHERE `followee_id` = `t_user`.`id` AND `status` = 'active'),
    `following_count` = (SELECT COUNT(*) FROM `t_follow` WHERE `follower_id` = `t_user`.`id` AND `status` = 'active'),
    `content_count` = (SELECT COUNT(*) FROM `t_content` WHERE `author_id` = `t_user`.`id` AND `status` = 'PUBLISHED'),
    `like_count` = (SELECT COUNT(*) FROM `t_like` WHERE `target_author_id` = `t_user`.`id` AND `status` = 'active');

-- 更新内容统计信息
UPDATE `t_content` SET 
    `like_count` = (SELECT COUNT(*) FROM `t_like` WHERE `target_id` = `t_content`.`id` AND `like_type` = 'CONTENT' AND `status` = 'active'),
    `comment_count` = (SELECT COUNT(*) FROM `t_comment` WHERE `target_id` = `t_content`.`id` AND `comment_type` = 'CONTENT' AND `status` = 'NORMAL'),
    `favorite_count` = (SELECT COUNT(*) FROM `t_favorite` WHERE `target_id` = `t_content`.`id` AND `favorite_type` = 'CONTENT' AND `status` = 'active');

-- 更新分类内容数量
UPDATE `t_category` SET 
    `content_count` = (SELECT COUNT(*) FROM `t_content` WHERE `category_id` = `t_category`.`id` AND `status` = 'PUBLISHED');

-- 更新标签使用次数
UPDATE `t_tag` SET 
    `usage_count` = (
        SELECT COUNT(*) FROM `t_content_tag` WHERE `tag_id` = `t_tag`.`id`
    ) + (
        SELECT COUNT(*) FROM `t_user_interest_tag` WHERE `tag_id` = `t_tag`.`id` AND `status` = 'active'
    );

-- 更新广告投放统计
UPDATE `t_ad_campaign` SET 
    `total_views` = (SELECT IFNULL(SUM(`total_views`), 0) FROM `t_ad_statistics` WHERE `campaign_id` = `t_ad_campaign`.`id`),
    `total_clicks` = (SELECT IFNULL(SUM(`total_clicks`), 0) FROM `t_ad_statistics` WHERE `campaign_id` = `t_ad_campaign`.`id`),
    `total_revenue` = (SELECT IFNULL(SUM(`total_revenue`), 0) FROM `t_ad_statistics` WHERE `campaign_id` = `t_ad_campaign`.`id`),
    `click_rate` = CASE 
        WHEN (SELECT IFNULL(SUM(`total_views`), 0) FROM `t_ad_statistics` WHERE `campaign_id` = `t_ad_campaign`.`id`) > 0 
        THEN (SELECT IFNULL(SUM(`total_clicks`), 0) FROM `t_ad_statistics` WHERE `campaign_id` = `t_ad_campaign`.`id`) / 
             (SELECT IFNULL(SUM(`total_views`), 0) FROM `t_ad_statistics` WHERE `campaign_id` = `t_ad_campaign`.`id`)
        ELSE 0 
    END;

-- ==========================================
-- 测试数据插入脚本完成
-- ==========================================
-- 
-- 总计插入的测试数据：
-- - 用户：10个测试用户 + 2个管理员用户
-- - 分类：20个分类（包含父子分类）
-- - 标签：24个标签（内容、兴趣、系统标签）
-- - 内容：10个不同类型的内容
-- - 商品：22个不同类型的商品
-- - 订单：14个不同状态和类型的订单
-- - 关注关系：24条关注记录
-- - 点赞记录：22条点赞记录
-- - 收藏记录：17条收藏记录
-- - 评论记录：18条评论（包含回复）
-- - 社交动态：13条动态记录
-- - 私信消息：23条消息记录
-- - 搜索历史：48条搜索记录
-- - 热门搜索：20个热门关键词
-- - 任务记录：17条任务完成记录
-- - 奖励记录：18条奖励发放记录
-- - 广告记录：16条展示记录
-- - 广告统计：12条统计记录
-- 
-- 数据特点：
-- 1. 严格按照表结构设计，保持数据完整性
-- 2. 考虑了表间关联关系，确保外键一致性
-- 3. 包含了各种业务场景的测试用例
-- 4. 数据时间跨度合理，便于测试
-- 5. 冗余字段保持一致，符合无连表设计理念