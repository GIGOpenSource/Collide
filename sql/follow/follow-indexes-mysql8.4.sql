-- ==========================================
-- 关注模块 MySQL 8.0 索引优化
-- 基于查询模式分析的高性能索引设计
-- ==========================================

USE collide;

-- =================== 基础复合索引（MySQL 8.0优化） ===================

-- 关注者查询索引（支持状态过滤和时间排序）
ALTER TABLE `t_follow` ADD INDEX `idx_follower_status_time` (`follower_id`, `status`, `create_time` DESC);

-- 被关注者查询索引（支持状态过滤和时间排序）
ALTER TABLE `t_follow` ADD INDEX `idx_followee_status_time` (`followee_id`, `status`, `create_time` DESC);

-- 双向关注查询索引（用于互关好友查询）
ALTER TABLE `t_follow` ADD INDEX `idx_mutual_follow` (`follower_id`, `followee_id`, `status`);

-- 状态时间复合索引（用于清理和管理）
ALTER TABLE `t_follow` ADD INDEX `idx_status_update_time` (`status`, `update_time` DESC);

-- 统计查询优化索引
ALTER TABLE `t_follow` ADD INDEX `idx_follower_status` (`follower_id`, `status`);
ALTER TABLE `t_follow` ADD INDEX `idx_followee_status` (`followee_id`, `status`);

-- =================== 时间查询索引 ===================

-- 时间范围查询索引（支持按时间查询）
ALTER TABLE `t_follow` ADD INDEX `idx_create_time_status` (`create_time` DESC, `status`);

-- 更新时间索引（用于清理操作）
ALTER TABLE `t_follow` ADD INDEX `idx_update_time_status` (`update_time` DESC, `status`);

-- =================== MySQL 8.0 高级索引优化 ===================

-- 全文搜索索引（用于昵称搜索，使用ngram解析器支持中文）
ALTER TABLE `t_follow` ADD FULLTEXT INDEX `idx_follower_nickname_fulltext` (`follower_nickname`) WITH PARSER ngram;
ALTER TABLE `t_follow` ADD FULLTEXT INDEX `idx_followee_nickname_fulltext` (`followee_nickname`) WITH PARSER ngram;

-- 函数索引（MySQL 8.0新特性，支持大小写不敏感查询）
ALTER TABLE `t_follow` ADD INDEX `idx_status_lower` ((LOWER(`status`)));

-- 前缀索引（优化昵称查询，节省存储空间）
ALTER TABLE `t_follow` ADD INDEX `idx_follower_nickname_prefix` (`follower_nickname`(20));
ALTER TABLE `t_follow` ADD INDEX `idx_followee_nickname_prefix` (`followee_nickname`(20));

-- =================== 复合条件查询索引 ===================

-- 复合条件查询索引（支持多种查询组合）
ALTER TABLE `t_follow` ADD INDEX `idx_follower_followee_status_time` (`follower_id`, `followee_id`, `status`, `create_time` DESC);

-- 批量状态检查索引
ALTER TABLE `t_follow` ADD INDEX `idx_follower_status_followee` (`follower_id`, `status`, `followee_id`);

-- =================== 查询性能说明 ===================

/*
索引使用场景说明：

1. idx_follower_status_time
   - 用于: findFollowing, countFollowing
   - 查询: WHERE follower_id = ? AND status = ? ORDER BY create_time DESC
   - 性能: 直接索引覆盖，无需回表

2. idx_followee_status_time  
   - 用于: findFollowers, countFollowers
   - 查询: WHERE followee_id = ? AND status = ? ORDER BY create_time DESC
   - 性能: 直接索引覆盖，无需回表

3. idx_mutual_follow
   - 用于: findMutualFollows
   - 查询: JOIN条件优化
   - 性能: 减少JOIN开销

4. idx_follower_nickname_fulltext / idx_followee_nickname_fulltext
   - 用于: searchByNickname
   - 查询: MATCH(nickname) AGAINST('keyword' IN NATURAL LANGUAGE MODE)
   - 性能: 全文检索，支持中文分词

5. idx_status_lower
   - 用于: 大小写不敏感的状态查询
   - 查询: WHERE LOWER(status) = 'active'
   - 性能: 函数索引直接命中

6. idx_follower_followee_status_time
   - 用于: findWithConditions复合查询
   - 查询: 多条件组合查询
   - 性能: 覆盖索引，减少回表

索引选择策略：
- 单表查询：优先使用复合索引
- JOIN查询：确保JOIN条件有索引
- 排序查询：索引包含ORDER BY字段
- 统计查询：使用覆盖索引
- 全文搜索：使用FULLTEXT索引
- 函数查询：使用函数索引
*/

-- =================== 索引维护建议 ===================

/*
定期维护命令：

1. 查看索引使用情况：
   SELECT * FROM sys.schema_unused_indexes WHERE object_schema = 'collide' AND object_name = 't_follow';

2. 分析索引效率：
   EXPLAIN FORMAT=JSON SELECT ... FROM t_follow WHERE ...;

3. 重建索引（如需要）：
   ALTER TABLE t_follow ENGINE=InnoDB;

4. 查看索引大小：
   SELECT 
     index_name,
     ROUND(stat_value * @@innodb_page_size / 1024 / 1024, 2) AS size_mb
   FROM mysql.innodb_index_stats 
   WHERE table_name = 't_follow' AND stat_name = 'size';
*/