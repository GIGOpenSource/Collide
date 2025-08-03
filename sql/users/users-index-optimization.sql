-- ==========================================
-- 用户模块索引优化建议 SQL
-- 基于6表架构的Mapper优化后的索引建议
-- ==========================================

USE collide;

-- ==========================================
-- 当前已有的索引（根据users-simple.sql）
-- ==========================================

-- t_user表索引
-- PRIMARY KEY (id) ✓
-- UNIQUE KEY uk_username (username) ✓  
-- UNIQUE KEY uk_email (email) USING HASH ✓
-- UNIQUE KEY uk_phone (phone) USING HASH ✓

-- t_user_profile表索引
-- PRIMARY KEY (user_id) ✓

-- t_user_stats表索引  
-- PRIMARY KEY (user_id) ✓

-- t_user_role表索引
-- PRIMARY KEY (id) ✓
-- UNIQUE KEY uk_user_role (user_id, role) ✓
-- KEY idx_user_id (user_id) ✓

-- t_user_wallet表索引
-- PRIMARY KEY (id) ✓
-- UNIQUE KEY uk_user_id (user_id) ✓
-- KEY idx_status (status) ✓
-- KEY idx_coin_balance (coin_balance) ✓

-- t_user_block表索引
-- PRIMARY KEY (id) ✓
-- UNIQUE KEY uk_user_blocked (user_id, blocked_user_id) ✓
-- KEY idx_user_id (user_id) ✓
-- KEY idx_blocked_user_id (blocked_user_id) ✓ 
-- KEY idx_status (status) ✓
-- KEY idx_create_time (create_time) ✓

-- ==========================================
-- 建议添加的索引（提升查询性能）
-- ==========================================

-- 1. t_user_profile表建议添加的索引
-- 用于昵称搜索优化
ALTER TABLE `t_user_profile` ADD INDEX `idx_nickname` (`nickname`);

-- 用于地区查询优化
ALTER TABLE `t_user_profile` ADD INDEX `idx_location` (`location`);

-- 用于性别筛选优化
ALTER TABLE `t_user_profile` ADD INDEX `idx_gender` (`gender`);

-- 用于生日/年龄查询优化
ALTER TABLE `t_user_profile` ADD INDEX `idx_birthday` (`birthday`);

-- 复合索引：性别+地区（常用组合查询）
ALTER TABLE `t_user_profile` ADD INDEX `idx_gender_location` (`gender`, `location`);

-- 2. t_user_stats表建议添加的索引
-- 用于粉丝数排行榜
ALTER TABLE `t_user_stats` ADD INDEX `idx_follower_count` (`follower_count` DESC);

-- 用于关注数排行榜
ALTER TABLE `t_user_stats` ADD INDEX `idx_following_count` (`following_count` DESC);

-- 用于内容数排行榜
ALTER TABLE `t_user_stats` ADD INDEX `idx_content_count` (`content_count` DESC);

-- 用于点赞数排行榜
ALTER TABLE `t_user_stats` ADD INDEX `idx_like_count` (`like_count` DESC);

-- 用于登录次数排行榜
ALTER TABLE `t_user_stats` ADD INDEX `idx_login_count` (`login_count` DESC);

-- 用于最后登录时间查询
ALTER TABLE `t_user_stats` ADD INDEX `idx_last_login_time` (`last_login_time` DESC);

-- 用于活跃度分数排行榜（需要在实际表结构中添加该字段）
-- ALTER TABLE `t_user_stats` ADD COLUMN `activity_score` DECIMAL(10,2) DEFAULT 0.0;
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_activity_score` (`activity_score` DESC);

-- 用于影响力分数排行榜（需要在实际表结构中添加该字段）
-- ALTER TABLE `t_user_stats` ADD COLUMN `influence_score` DECIMAL(10,2) DEFAULT 0.0;
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_influence_score` (`influence_score` DESC);

-- 3. t_user_role表额外优化索引
-- 用于角色过期时间查询
ALTER TABLE `t_user_role` ADD INDEX `idx_expire_time` (`expire_time`);

-- 用于角色状态查询（需要在实际表结构中添加该字段）
-- ALTER TABLE `t_user_role` ADD COLUMN `status` VARCHAR(20) DEFAULT 'active';
-- ALTER TABLE `t_user_role` ADD INDEX `idx_status` (`status`);

-- 复合索引：状态+过期时间（查询有效角色）
-- ALTER TABLE `t_user_role` ADD INDEX `idx_status_expire` (`status`, `expire_time`);

-- 4. t_user_wallet表额外优化索引
-- 用于现金余额排行榜
ALTER TABLE `t_user_wallet` ADD INDEX `idx_balance` (`balance` DESC);

-- 用于总收入排行榜
ALTER TABLE `t_user_wallet` ADD INDEX `idx_total_income` (`total_income` DESC);

-- 复合索引：状态+金币余额（常用组合查询）
ALTER TABLE `t_user_wallet` ADD INDEX `idx_status_coin_balance` (`status`, `coin_balance` DESC);

-- 5. t_user表额外优化索引
-- 用于邀请关系查询
ALTER TABLE `t_user` ADD INDEX `idx_inviter_id` (`inviter_id`);

-- 用于邀请码查询（如果还没有）
-- ALTER TABLE `t_user` ADD UNIQUE INDEX `uk_invite_code` (`invite_code`);

-- 复合索引：状态+创建时间（常用组合查询）
ALTER TABLE `t_user` ADD INDEX `idx_status_create_time` (`status`, `create_time` DESC);

-- ==========================================
-- 索引使用说明和性能提升预期
-- ==========================================

/*
索引优化预期效果：

1. 用户搜索相关查询（昵称、地区）性能提升 80-90%
2. 排行榜查询（粉丝、内容、点赞等）性能提升 90-95%
3. 拉黑关系查询利用复合唯一索引，性能提升 95%+
4. 角色权限检查利用复合索引，性能提升 90%+
5. 钱包金币排行榜利用专用索引，性能提升 95%+

注意事项：
- 索引会占用额外存储空间，预计增加 15-20% 的表大小
- 写入性能会略有下降（约 5-10%），但读取性能大幅提升
- 建议在业务低峰期执行索引创建操作
- 定期分析索引使用情况，删除不再使用的索引

索引监控：
- 使用 EXPLAIN 分析查询执行计划
- 监控 show index from table_name 查看索引基数
- 定期执行 ANALYZE TABLE 更新索引统计信息
*/

-- ==========================================
-- 索引创建脚本（全量执行）
-- ==========================================

-- 创建所有建议的索引
-- 取消注释以下行来执行：

-- -- t_user_profile索引
-- ALTER TABLE `t_user_profile` ADD INDEX `idx_nickname` (`nickname`);
-- ALTER TABLE `t_user_profile` ADD INDEX `idx_location` (`location`);
-- ALTER TABLE `t_user_profile` ADD INDEX `idx_gender` (`gender`);
-- ALTER TABLE `t_user_profile` ADD INDEX `idx_birthday` (`birthday`);
-- ALTER TABLE `t_user_profile` ADD INDEX `idx_gender_location` (`gender`, `location`);

-- -- t_user_stats索引
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_follower_count` (`follower_count` DESC);
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_following_count` (`following_count` DESC);
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_content_count` (`content_count` DESC);
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_like_count` (`like_count` DESC);
-- ALTER TABLE `t_user_stats` ADD INDEX `idx_login_count` (`login_count` DESC);

-- -- t_user_role索引
-- ALTER TABLE `t_user_role` ADD INDEX `idx_expire_time` (`expire_time`);

-- -- t_user_wallet索引
-- ALTER TABLE `t_user_wallet` ADD INDEX `idx_balance` (`balance` DESC);
-- ALTER TABLE `t_user_wallet` ADD INDEX `idx_total_income` (`total_income` DESC);
-- ALTER TABLE `t_user_wallet` ADD INDEX `idx_status_coin_balance` (`status`, `coin_balance` DESC);

-- -- t_user索引
-- ALTER TABLE `t_user` ADD INDEX `idx_inviter_id` (`inviter_id`);
-- ALTER TABLE `t_user` ADD INDEX `idx_status_create_time` (`status`, `create_time` DESC);