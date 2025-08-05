-- ==========================================
-- 标签模块 MySQL 8.4 索引优化方案
-- 基于查询模式分析，提供高性能索引设计
-- ==========================================

USE collide;

-- ==========================================
-- 1. t_tag 表索引优化
-- ==========================================

-- 删除现有的单列索引（保留主键和唯一索引）
ALTER TABLE `t_tag` DROP INDEX `idx_tag_type`;
ALTER TABLE `t_tag` DROP INDEX `idx_status`;

-- 复合索引：按类型和状态查询（selectByTagType）
-- 覆盖索引：包含常用查询字段，避免回表
CREATE INDEX `idx_tag_type_status_usage` ON `t_tag` (`tag_type`, `status`, `usage_count` DESC, `create_time` DESC);

-- 复合索引：按状态和使用次数排序（selectHotTags）
-- 针对热门标签查询优化
CREATE INDEX `idx_status_usage_time` ON `t_tag` (`status`, `usage_count` DESC, `create_time` DESC);

-- 复合索引：按分类和状态查询（selectByCategoryId）
CREATE INDEX `idx_category_status_usage` ON `t_tag` (`category_id`, `status`, `usage_count` DESC, `create_time` DESC);

-- 全文索引：支持标签名称全文搜索（MySQL 8.0+ 支持中文分词）
-- 替代LIKE模糊查询，大幅提升搜索性能
ALTER TABLE `t_tag` ADD FULLTEXT INDEX `ft_idx_name` (`name`) WITH PARSER ngram;

-- 函数索引：支持大小写不敏感的名称查询（MySQL 8.0+）
CREATE INDEX `idx_name_lower` ON `t_tag` ((LOWER(`name`)));

-- 覆盖索引：名称唯一性检查专用
-- 已有 uk_name_type 唯一索引，无需额外优化

-- ==========================================
-- 2. t_user_interest_tag 表索引优化  
-- ==========================================

-- 删除现有的单列索引（保留主键和唯一索引）
ALTER TABLE `t_user_interest_tag` DROP INDEX `idx_user_id`;
ALTER TABLE `t_user_interest_tag` DROP INDEX `idx_tag_id`;

-- 复合索引：按用户查询兴趣标签（selectByUserId）
-- 覆盖索引：包含兴趣分数和创建时间，避免回表
CREATE INDEX `idx_user_status_score` ON `t_user_interest_tag` (`user_id`, `status`, `interest_score` DESC, `create_time` DESC);

-- 复合索引：按标签查询关注用户（selectByTagId）
CREATE INDEX `idx_tag_status_score` ON `t_user_interest_tag` (`tag_id`, `status`, `interest_score` DESC, `create_time` DESC);

-- 复合索引：用户兴趣分数更新专用（updateInterestScore）
-- 已有 uk_user_tag 唯一索引，可用于定位更新记录

-- 复合索引：批量状态更新优化（batchUpdateStatus）
CREATE INDEX `idx_user_tag_status` ON `t_user_interest_tag` (`user_id`, `tag_id`, `status`);

-- ==========================================
-- 3. t_content_tag 表索引优化
-- ==========================================

-- 保留现有索引，已经比较优化
-- 但可以添加一些覆盖索引

-- 覆盖索引：内容标签查询（selectByContentId）
-- 包含创建时间，避免回表排序
CREATE INDEX `idx_content_time` ON `t_content_tag` (`content_id`, `create_time` DESC);

-- 覆盖索引：标签内容查询（selectByTagId）  
CREATE INDEX `idx_tag_time` ON `t_content_tag` (`tag_id`, `create_time` DESC);

-- 保留原有的唯一索引 uk_content_tag，用于重复性检查

-- ==========================================
-- 4. MySQL 8.4 高级特性优化
-- ==========================================

-- 隐藏索引：用于测试索引效果（MySQL 8.0+）
-- 可以先隐藏旧索引，测试新索引效果
-- ALTER TABLE `t_tag` ALTER INDEX `idx_old_index` INVISIBLE;

-- 降序索引：明确指定降序索引（MySQL 8.0+）
-- 对于 ORDER BY ... DESC 查询有显著优化效果
-- 上述索引已使用 DESC 关键字

-- 多值索引：如果将来需要支持JSON字段（MySQL 8.0.17+）
-- CREATE INDEX idx_json_tags ON t_tag ((CAST(json_field->'$.tags' AS CHAR(255) ARRAY)));

-- 克隆索引：快速创建相似索引结构（MySQL 8.0+）
-- CREATE INDEX idx_new LIKE idx_existing ON table_name;

-- ==========================================
-- 5. 索引使用建议和监控
-- ==========================================

-- 查看索引使用情况
-- SELECT * FROM sys.schema_unused_indexes WHERE object_schema = 'collide';

-- 查看重复索引
-- SELECT * FROM sys.schema_redundant_indexes WHERE table_schema = 'collide';

-- 查看索引统计信息
-- SELECT * FROM information_schema.statistics WHERE table_schema = 'collide' AND table_name LIKE 't_tag%';

-- ==========================================
-- 6. 性能测试验证 SQL
-- ==========================================

-- 测试1：标签类型查询
-- EXPLAIN SELECT * FROM t_tag WHERE tag_type = 'content' AND status = 'active' ORDER BY usage_count DESC;

-- 测试2：全文搜索
-- EXPLAIN SELECT * FROM t_tag WHERE MATCH(name) AGAINST('编程' IN NATURAL LANGUAGE MODE) AND status = 'active';

-- 测试3：用户兴趣查询
-- EXPLAIN SELECT * FROM t_user_interest_tag WHERE user_id = 1001 AND status = 'active' ORDER BY interest_score DESC;

-- 测试4：热门标签查询
-- EXPLAIN SELECT * FROM t_tag WHERE status = 'active' ORDER BY usage_count DESC, create_time DESC LIMIT 10;

-- ==========================================
-- 7. 索引维护建议
-- ==========================================

-- 定期分析表和索引统计信息（MySQL 8.0 自动更新统计信息）
-- ANALYZE TABLE t_tag, t_user_interest_tag, t_content_tag;

-- 监控索引碎片和使用情况
-- 建议使用 pt-index-usage 工具或 sys schema 视图

-- 索引大小监控
-- SELECT 
--   table_name,
--   index_name,
--   ROUND(stat_value * @@innodb_page_size / 1024 / 1024, 2) AS size_mb
-- FROM mysql.innodb_index_stats 
-- WHERE database_name = 'collide' 
--   AND table_name LIKE 't_tag%' 
--   AND stat_name = 'size';

-- ==========================================
-- 注意事项：
-- 1. 请在测试环境验证索引效果后再应用到生产环境
-- 2. 建议在业务低峰期执行索引变更操作
-- 3. 大表添加索引可能需要较长时间，建议使用 pt-online-schema-change
-- 4. 定期监控索引使用情况，删除未使用的索引
-- ==========================================