/*
 * Collide 项目数据库性能优化
 * 包含索引优化、查询优化、缓存策略等
 * 
 * @author GIG Team
 * @version 1.0.0
 * @date 2024-01-01
 */

USE `collide`;

-- ====================================
-- 1. 核心业务表复合索引优化
-- ====================================

-- 内容表性能优化索引
-- 优化内容列表查询（按类型、状态、发布时间）
ALTER TABLE `t_content` ADD INDEX `idx_type_status_published` (`content_type`, `status`, `published_time` DESC);

-- 优化用户内容查询（按作者、状态、创建时间）  
ALTER TABLE `t_content` ADD INDEX `idx_author_status_create` (`author_id`, `status`, `create_time` DESC);

-- 优化推荐内容查询（推荐标记、权重分数）
ALTER TABLE `t_content` ADD INDEX `idx_recommended_weight` (`is_recommended`, `weight_score` DESC);

-- 优化热门内容查询（按点赞数、查看数）
ALTER TABLE `t_content` ADD INDEX `idx_popular_content` (`status`, `like_count` DESC, `view_count` DESC);

-- 优化分类内容查询
ALTER TABLE `t_content` ADD INDEX `idx_category_status_published` (`category_id`, `status`, `published_time` DESC);

-- 关注表性能优化索引
-- 优化粉丝列表查询（被关注者、状态、时间）
ALTER TABLE `t_user_follow` ADD INDEX `idx_following_status_time` (`following_id`, `status`, `create_time` DESC);

-- 优化关注列表查询（关注者、状态、时间）
ALTER TABLE `t_user_follow` ADD INDEX `idx_follower_status_time` (`follower_id`, `status`, `create_time` DESC);

-- 内容交互表性能优化索引
-- 优化用户交互历史查询
ALTER TABLE `t_content_interaction` ADD INDEX `idx_user_type_time` (`user_id`, `interaction_type`, `create_time` DESC);

-- 优化内容交互统计查询
ALTER TABLE `t_content_interaction` ADD INDEX `idx_content_type_time` (`content_id`, `interaction_type`, `create_time` DESC);

-- 评论表性能优化索引  
-- 优化评论列表查询（内容、状态、时间）
ALTER TABLE `t_content_comment` ADD INDEX `idx_content_status_time` (`content_id`, `status`, `create_time` DESC);

-- 优化用户评论查询
ALTER TABLE `t_content_comment` ADD INDEX `idx_user_status_time` (`user_id`, `status`, `create_time` DESC);

-- 优化回复查询
ALTER TABLE `t_content_comment` ADD INDEX `idx_parent_status_time` (`parent_id`, `status`, `create_time` ASC);

-- 用户表性能优化索引
-- 优化用户搜索（状态、角色、创建时间）
ALTER TABLE `t_user` ADD INDEX `idx_status_role_create` (`status`, `role`, `create_time` DESC);

-- 优化最近活跃用户查询
ALTER TABLE `t_user` ADD INDEX `idx_status_last_login` (`status`, `last_login_time` DESC);

-- 用户档案表优化索引
-- 优化VIP用户查询
ALTER TABLE `t_user_profile` ADD INDEX `idx_vip_expire_status` (`vip_expire_time`, `blogger_status`);

-- 优化热门用户查询（按粉丝数）
ALTER TABLE `t_user_profile` ADD INDEX `idx_follower_content_count` (`follower_count` DESC, `content_count` DESC);

-- ====================================
-- 2. 统计表索引优化
-- ====================================

-- OAuth统计表索引
ALTER TABLE `t_app_user_statistics` ADD INDEX `idx_app_date_active` (`app_id`, `stat_date` DESC, `active_user_count` DESC);

-- 金币交易记录索引
ALTER TABLE `t_coin_transaction` ADD INDEX `idx_user_type_time` (`user_id`, `transaction_type`, `create_time` DESC);
ALTER TABLE `t_coin_transaction` ADD INDEX `idx_business_status_time` (`business_type`, `status`, `create_time` DESC);

-- 社交动态索引
ALTER TABLE `t_social_post` ADD INDEX `idx_user_status_time` (`user_id`, `status`, `create_time` DESC);
ALTER TABLE `t_social_post` ADD INDEX `idx_visibility_status_time` (`visibility`, `status`, `create_time` DESC);

-- ====================================
-- 3. 分区表优化（大数据量表）
-- ====================================

-- 内容交互表按月分区（适用于数据量大的场景）
-- ALTER TABLE `t_content_interaction` PARTITION BY RANGE (YEAR(create_time)*100 + MONTH(create_time)) (
--     PARTITION p202401 VALUES LESS THAN (202402),
--     PARTITION p202402 VALUES LESS THAN (202403),
--     PARTITION p202403 VALUES LESS THAN (202404),
--     PARTITION p202404 VALUES LESS THAN (202405),
--     PARTITION p202405 VALUES LESS THAN (202406),
--     PARTITION p202406 VALUES LESS THAN (202407),
--     PARTITION p202407 VALUES LESS THAN (202408),
--     PARTITION p202408 VALUES LESS THAN (202409),
--     PARTITION p202409 VALUES LESS THAN (202410),
--     PARTITION p202410 VALUES LESS THAN (202411),
--     PARTITION p202411 VALUES LESS THAN (202412),
--     PARTITION p202412 VALUES LESS THAN (202501),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- ====================================
-- 4. 查询优化建议
-- ====================================

-- 内容列表查询优化（避免连表）
-- 优化前：SELECT * FROM t_content c JOIN t_user u ON c.author_id = u.id 
-- 优化后：分步查询
-- 1. SELECT * FROM t_content WHERE status = 'PUBLISHED' ORDER BY published_time DESC LIMIT 20
-- 2. SELECT id, username, nickname, avatar FROM t_user WHERE id IN (...)

-- 关注列表查询优化（避免连表）  
-- 优化前：SELECT * FROM t_user_follow f JOIN t_user u ON f.following_id = u.id
-- 优化后：分步查询
-- 1. SELECT following_id FROM t_user_follow WHERE follower_id = ? AND status = 'active' 
-- 2. SELECT id, username, nickname, avatar FROM t_user WHERE id IN (...)

-- 热门内容查询优化
-- SELECT * FROM t_content 
-- WHERE status = 'PUBLISHED' 
-- AND content_type = ? 
-- ORDER BY like_count DESC, view_count DESC 
-- LIMIT 20;

-- 用户内容查询优化
-- SELECT * FROM t_content 
-- WHERE author_id = ? 
-- AND status IN ('PUBLISHED', 'DRAFT')
-- ORDER BY create_time DESC 
-- LIMIT 20 OFFSET ?;

-- ====================================
-- 5. 数据库参数优化建议
-- ====================================

-- MySQL 配置优化建议（my.cnf）
/*
[mysqld]
# InnoDB 缓冲池大小（建议设置为内存的70-80%）
innodb_buffer_pool_size = 1G

# InnoDB 日志文件大小
innodb_log_file_size = 256M

# InnoDB 日志缓冲区大小
innodb_log_buffer_size = 64M

# 查询缓存大小（MySQL 8.0已移除）
# query_cache_size = 256M

# 最大连接数
max_connections = 300

# 连接超时时间
wait_timeout = 600
interactive_timeout = 600

# 慢查询日志
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# 索引统计信息
innodb_stats_persistent = 1
innodb_stats_auto_recalc = 1
*/

-- ====================================
-- 6. 缓存策略建议
-- ====================================

-- Redis 缓存键设计
/*
用户信息缓存：
- user:info:{userId} (TTL: 30分钟)
- user:profile:{userId} (TTL: 30分钟)

内容缓存：
- content:info:{contentId} (TTL: 1小时)
- content:list:hot:{contentType} (TTL: 10分钟)
- content:list:latest:{contentType} (TTL: 5分钟)

关注关系缓存：
- follow:following:{userId} (TTL: 1小时)
- follow:followers:{userId} (TTL: 1小时)
- follow:count:{userId} (TTL: 1小时)

统计数据缓存：
- stats:content:{contentId} (TTL: 1小时)
- stats:user:{userId} (TTL: 30分钟)
*/

-- ====================================
-- 7. 监控查询
-- ====================================

-- 慢查询监控
SELECT 
    query_time,
    lock_time,
    rows_sent,
    rows_examined,
    sql_text
FROM mysql.slow_log 
WHERE start_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)
ORDER BY query_time DESC 
LIMIT 10;

-- 索引使用情况监控
SELECT 
    table_name,
    index_name,
    cardinality,
    non_unique
FROM information_schema.statistics 
WHERE table_schema = 'collide'
ORDER BY table_name, cardinality DESC;

-- 表大小统计
SELECT 
    table_name,
    table_rows,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb,
    ROUND((data_length / 1024 / 1024), 2) AS data_mb,
    ROUND((index_length / 1024 / 1024), 2) AS index_mb
FROM information_schema.tables 
WHERE table_schema = 'collide'
ORDER BY (data_length + index_length) DESC;

-- 执行完成提示
SELECT '数据库性能优化SQL执行完成！' AS message;
SELECT '建议执行ANALYZE TABLE命令更新表统计信息' AS suggestion;

-- 更新表统计信息
ANALYZE TABLE t_content;
ANALYZE TABLE t_user;
ANALYZE TABLE t_user_follow;
ANALYZE TABLE t_content_interaction;
ANALYZE TABLE t_content_comment;
ANALYZE TABLE t_user_profile; 