-- 内容模块初始化数据

-- 插入示例内容分类数据
INSERT INTO `content` (`id`, `content_type`, `title`, `description`, `cover_image`, `content_url`, 
                      `status`, `creator_user_id`, `creator_username`, `tags`, `category`, 
                      `is_original`, `language`, `create_time`) VALUES
(1, 'novel', '示例小说：时空穿越者', '一个关于时空穿越的精彩故事，主人公意外获得穿越能力...', 
 'https://example.com/covers/novel1.jpg', 'https://example.com/novels/1',
 3, 1001, 'author001', '["科幻", "穿越", "冒险"]', '科幻小说', 1, 'zh', NOW()),

(2, 'comic', '示例漫画：超级英雄传说', '一部关于超级英雄的热血漫画，讲述了普通人成为英雄的故事...', 
 'https://example.com/covers/comic1.jpg', 'https://example.com/comics/1',
 3, 1002, 'artist001', '["超级英雄", "热血", "冒险"]', '动作漫画', 1, 'zh', NOW()),

(3, 'short_video', '搞笑短视频：日常趣事', '记录生活中的搞笑瞬间，让你开怀大笑...', 
 'https://example.com/covers/video1.jpg', 'https://example.com/videos/short/1',
 3, 1003, 'vlogger001', '["搞笑", "日常", "生活"]', '搞笑视频', 1, 'zh', NOW()),

(4, 'long_video', '纪录片：自然之美', '一部关于自然风光的纪录片，展现地球的壮美景色...', 
 'https://example.com/covers/doc1.jpg', 'https://example.com/videos/long/1',
 3, 1004, 'director001', '["纪录片", "自然", "风光"]', '纪录片', 1, 'zh', NOW());

-- 更新内容统计数据
UPDATE `content` SET 
    `view_count` = FLOOR(RAND() * 10000) + 100,
    `like_count` = FLOOR(RAND() * 1000) + 10,
    `collect_count` = FLOOR(RAND() * 500) + 5,
    `share_count` = FLOOR(RAND() * 200) + 2,
    `comment_count` = FLOOR(RAND() * 300) + 3,
    `rating` = ROUND(RAND() * 2 + 3, 2),
    `rating_count` = FLOOR(RAND() * 100) + 10,
    `hot_score` = ROUND(RAND() * 100, 2),
    `quality_score` = ROUND(RAND() * 50 + 50, 2),
    `recommend_index` = ROUND(RAND() * 100, 2),
    `publish_time` = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY)
WHERE `id` IN (1, 2, 3, 4);

-- 插入内容统计数据
INSERT INTO `content_statistics` (`content_id`, `statistics_date`, `view_count`, `like_count`, 
                                 `collect_count`, `share_count`, `comment_count`, `hot_score`, 
                                 `quality_score`, `recommend_index`) 
SELECT 
    `id`,
    DATE_FORMAT(NOW(), '%Y-%m-%d'),
    `view_count`,
    `like_count`,
    `collect_count`,
    `share_count`,
    `comment_count`,
    `hot_score`,
    `quality_score`,
    `recommend_index`
FROM `content` 
WHERE `id` IN (1, 2, 3, 4);

-- 插入审核记录
INSERT INTO `content_review` (`content_id`, `review_status`, `reviewer_user_id`, `reviewer_username`,
                             `review_comment`, `review_round`, `submit_time`, `start_time`, `finish_time`) VALUES
(1, 3, 2001, 'reviewer001', '内容质量良好，符合平台规范，审核通过', 1, 
 DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 3, 2001, 'reviewer001', '漫画内容积极向上，画面质量优秀，审核通过', 1,
 DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 3, 2002, 'reviewer002', '视频内容健康有趣，符合社区标准，审核通过', 1,
 DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(4, 3, 2002, 'reviewer002', '纪录片内容教育价值高，制作精良，审核通过', 1,
 DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入用户交互数据示例
INSERT INTO `user_content_interaction` (`user_id`, `content_id`, `interaction_type`, `device_type`) VALUES
(3001, 1, 'view', 'mobile'),
(3001, 1, 'like', 'mobile'),
(3002, 1, 'view', 'pc'),
(3002, 2, 'view', 'pc'),
(3002, 2, 'like', 'pc'),
(3002, 2, 'collect', 'pc'),
(3003, 3, 'view', 'mobile'),
(3003, 3, 'like', 'mobile'),
(3003, 3, 'share', 'mobile'),
(3004, 4, 'view', 'tablet'),
(3004, 4, 'collect', 'tablet');

-- 插入操作日志示例
INSERT INTO `content_operation_log` (`content_id`, `operator_user_id`, `operator_username`,
                                    `operation_type`, `operation_desc`) VALUES
(1, 1001, 'author001', 'create', '创建小说内容'),
(1, 1001, 'author001', 'submit_review', '提交审核'),
(1, 2001, 'reviewer001', 'approve', '审核通过'),
(1, 1001, 'author001', 'publish', '发布内容'),
(2, 1002, 'artist001', 'create', '创建漫画内容'),
(2, 1002, 'artist001', 'submit_review', '提交审核'),
(2, 2001, 'reviewer001', 'approve', '审核通过'),
(2, 1002, 'artist001', 'publish', '发布内容'); 