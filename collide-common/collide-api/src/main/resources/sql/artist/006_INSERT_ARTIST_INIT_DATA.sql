-- 博主系统初始化数据

-- 注意：这里只是示例数据结构，实际部署时可根据需要调整

-- 初始化博主分类配置（可以存储在配置表中）
-- INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `gmt_create`, `gmt_modified`) VALUES
-- ('artist.categories', '["TECHNOLOGY","LIFESTYLE","FOOD","FASHION","TRAVEL","FITNESS","EDUCATION","ENTERTAINMENT","ART","BUSINESS","HEALTH","AUTOMOTIVE","HOME","PARENTING","PETS","GAMING","OTHER"]', '博主分类配置', NOW(), NOW());

-- 初始化博主等级配置
-- INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `gmt_create`, `gmt_modified`) VALUES
-- ('artist.levels', '[{"level":"NEWCOMER","name":"新手博主","minFollowers":0,"maxFollowers":1000},{"level":"GROWING","name":"成长博主","minFollowers":1000,"maxFollowers":10000},{"level":"EXCELLENT","name":"优秀博主","minFollowers":10000,"maxFollowers":50000},{"level":"FAMOUS","name":"知名博主","minFollowers":50000,"maxFollowers":200000},{"level":"TOP","name":"顶级博主","minFollowers":200000,"maxFollowers":999999999}]', '博主等级配置', NOW(), NOW());

-- 初始化认证类型配置
-- INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `gmt_create`, `gmt_modified`) VALUES
-- ('artist.verification_types', '[{"type":"PERSONAL","name":"个人认证","badge":"personal_verified","level":1},{"type":"ORGANIZATION","name":"机构认证","badge":"org_verified","level":2},{"type":"PROFESSIONAL","name":"专业认证","badge":"pro_verified","level":3},{"type":"BLUE_V","name":"蓝V认证","badge":"blue_v","level":4},{"type":"GOLD_V","name":"金V认证","badge":"gold_v","level":5}]', '认证类型配置', NOW(), NOW());

-- 初始化审核配置
-- INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `gmt_create`, `gmt_modified`) VALUES
-- ('artist.review_config', '{"auto_review_enabled":false,"review_timeout_days":7,"max_application_rounds":3,"required_materials":["identity_proof","portfolio_url"],"review_criteria":{"min_followers":100,"min_content_quality":6}}', '博主审核配置', NOW(), NOW());

-- 初始化等级权限配置
-- INSERT INTO `system_config` (`config_key`, `config_value`, `config_desc`, `gmt_create`, `gmt_modified`) VALUES
-- ('artist.level_permissions', '{"NEWCOMER":{"max_daily_posts":5,"can_create_topic":false,"can_pin_content":false},"GROWING":{"max_daily_posts":10,"can_create_topic":true,"can_pin_content":false},"EXCELLENT":{"max_daily_posts":20,"can_create_topic":true,"can_pin_content":true},"FAMOUS":{"max_daily_posts":50,"can_create_topic":true,"can_pin_content":true,"can_host_live":true},"TOP":{"max_daily_posts":100,"can_create_topic":true,"can_pin_content":true,"can_host_live":true,"priority_support":true}}', '博主等级权限配置', NOW(), NOW());

-- 插入示例数据（开发/测试环境使用）
-- 注意：生产环境请删除或注释以下示例数据

-- 示例：创建一个测试博主申请
/*
INSERT INTO `artist_application` 
(`gmt_create`, `gmt_modified`, `user_id`, `application_type`, `status`, `artist_name`, `bio`, `categories`, `apply_reason`, `contact_email`, `contact_phone`, `submit_time`, `application_round`) 
VALUES 
(NOW(), NOW(), 1001, 'PERSONAL', 'APPLYING', '科技达人小李', '专注于前端技术分享，React、Vue等框架的深度解析', '["TECHNOLOGY"]', '我在前端开发领域有5年经验，希望通过平台分享技术心得，帮助更多开发者成长。', 'test@example.com', '13800138000', NOW(), 1);
*/

-- 示例：创建测试博主记录
/*
INSERT INTO `artist` 
(`gmt_create`, `gmt_modified`, `user_id`, `artist_name`, `bio`, `status`, `application_type`, `categories`, `level`, `contact_email`, `contact_phone`) 
VALUES 
(NOW(), NOW(), 1002, '美食探索家', '探索世界各地美食，分享烹饪技巧和美食文化', 'ACTIVE', 'PERSONAL', '["FOOD"]', 'GROWING', 'foodie@example.com', '13900139000');
*/

-- 示例：插入博主统计数据
/*
INSERT INTO `artist_statistics` 
(`gmt_create`, `gmt_modified`, `artist_id`, `period_start`, `period_end`, `last_updated`) 
VALUES 
(NOW(), NOW(), 1, '2024-01-01', '2024-12-31', NOW());
*/

-- 创建索引优化查询性能（如果需要额外索引）
-- CREATE INDEX `idx_artist_hot_score_status` ON `artist` (`hot_score`, `status`);
-- CREATE INDEX `idx_artist_influence_level` ON `artist` (`influence_index`, `level`);
-- CREATE INDEX `idx_application_status_type` ON `artist_application` (`status`, `application_type`, `submit_time`); 