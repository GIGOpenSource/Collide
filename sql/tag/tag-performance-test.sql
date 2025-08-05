-- ==========================================
-- 标签模块性能测试和监控 SQL
-- 用于验证 MySQL 8.4 索引优化效果
-- ==========================================

USE collide;

-- ==========================================
-- 1. 执行计划分析 (EXPLAIN)
-- ==========================================

-- 测试1：标签类型和状态复合查询
EXPLAIN FORMAT=JSON
SELECT * FROM t_tag 
WHERE tag_type = 'content' AND status = 'active' 
ORDER BY usage_count DESC, create_time DESC 
LIMIT 20;

-- 测试2：全文搜索性能
EXPLAIN FORMAT=JSON
SELECT * FROM t_tag 
WHERE MATCH(name) AGAINST('编程 技术' IN NATURAL LANGUAGE MODE) 
AND status = 'active'
ORDER BY usage_count DESC 
LIMIT 10;

-- 测试3：用户兴趣标签查询
EXPLAIN FORMAT=JSON
SELECT * FROM t_user_interest_tag 
WHERE user_id = 1001 AND status = 'active' 
ORDER BY interest_score DESC, create_time DESC;

-- 测试4：热门标签查询
EXPLAIN FORMAT=JSON
SELECT * FROM t_tag 
WHERE status = 'active' 
ORDER BY usage_count DESC, create_time DESC 
LIMIT 10;

-- 测试5：内容标签关联查询
EXPLAIN FORMAT=JSON
SELECT * FROM t_content_tag 
WHERE content_id = 2001 
ORDER BY create_time DESC;

-- ==========================================
-- 2. 索引使用情况监控
-- ==========================================

-- 查看索引统计信息
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    CARDINALITY,
    SEQ_IN_INDEX
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
AND TABLE_NAME LIKE 't_tag%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 查看索引大小统计
SELECT 
    table_name,
    index_name,
    ROUND(stat_value * @@innodb_page_size / 1024 / 1024, 2) AS size_mb
FROM mysql.innodb_index_stats 
WHERE database_name = 'collide' 
AND table_name LIKE 't_tag%' 
AND stat_name = 'size'
ORDER BY size_mb DESC;

-- ==========================================
-- 3. 查询性能基准测试
-- ==========================================

-- 性能测试：标签类型查询
SET @start_time = NOW(6);
SELECT COUNT(*) FROM t_tag WHERE tag_type = 'content' AND status = 'active';
SET @end_time = NOW(6);
SELECT TIMESTAMPDIFF(MICROSECOND, @start_time, @end_time) AS query_time_microseconds;

-- 性能测试：用户兴趣查询
SET @start_time = NOW(6);
SELECT COUNT(*) FROM t_user_interest_tag WHERE user_id = 1001 AND status = 'active';
SET @end_time = NOW(6);
SELECT TIMESTAMPDIFF(MICROSECOND, @start_time, @end_time) AS query_time_microseconds;

-- 性能测试：全文搜索
SET @start_time = NOW(6);
SELECT COUNT(*) FROM t_tag WHERE MATCH(name) AGAINST('技术' IN NATURAL LANGUAGE MODE);
SET @end_time = NOW(6);
SELECT TIMESTAMPDIFF(MICROSECOND, @start_time, @end_time) AS query_time_microseconds;

-- ==========================================
-- 4. 索引效果验证查询
-- ==========================================

-- 验证复合索引效果：tag_type + status + usage_count
SELECT /*+ USE_INDEX(idx_tag_type_status_usage) */ 
    id, name, usage_count 
FROM t_tag 
WHERE tag_type = 'content' AND status = 'active' 
ORDER BY usage_count DESC 
LIMIT 10;

-- 验证覆盖索引效果：只选择索引包含的字段
SELECT tag_type, status, usage_count, create_time 
FROM t_tag 
WHERE tag_type = 'interest' AND status = 'active' 
ORDER BY usage_count DESC;

-- 验证函数索引效果：大小写不敏感搜索
SELECT * FROM t_tag 
WHERE LOWER(name) = LOWER('JavaScript') 
AND status = 'active';

-- ==========================================
-- 5. 慢查询分析
-- ==========================================

-- 开启慢查询日志（如果需要）
-- SET GLOBAL slow_query_log = 'ON';
-- SET GLOBAL long_query_time = 0.1;

-- 查看当前慢查询设置
SHOW VARIABLES LIKE 'slow_query%';
SHOW VARIABLES LIKE 'long_query_time';

-- ==========================================
-- 6. Performance Schema 监控
-- ==========================================

-- 查看表的访问统计
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    COUNT_READ,
    COUNT_WRITE,
    COUNT_FETCH,
    COUNT_INSERT,
    COUNT_UPDATE,
    COUNT_DELETE
FROM performance_schema.table_io_waits_summary_by_table 
WHERE OBJECT_SCHEMA = 'collide' 
AND OBJECT_NAME LIKE 't_tag%'
ORDER BY COUNT_READ + COUNT_WRITE DESC;

-- 查看索引的访问统计
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    INDEX_NAME,
    COUNT_FETCH,
    COUNT_INSERT,
    COUNT_UPDATE,
    COUNT_DELETE
FROM performance_schema.table_io_waits_summary_by_index_usage 
WHERE OBJECT_SCHEMA = 'collide' 
AND OBJECT_NAME LIKE 't_tag%'
ORDER BY COUNT_FETCH DESC;

-- ==========================================
-- 7. 实际业务场景测试
-- ==========================================

-- 场景1：用户浏览标签页面
SELECT 
    t.id,
    t.name,
    t.usage_count,
    t.description
FROM t_tag t
WHERE t.tag_type = 'content' AND t.status = 'active'
ORDER BY t.usage_count DESC, t.create_time DESC
LIMIT 20;

-- 场景2：搜索标签功能
SELECT 
    t.id,
    t.name,
    t.tag_type,
    t.usage_count
FROM t_tag t
WHERE MATCH(t.name) AGAINST('前端 React' IN NATURAL LANGUAGE MODE)
AND t.status = 'active'
ORDER BY t.usage_count DESC
LIMIT 10;

-- 场景3：用户兴趣标签管理
SELECT 
    uit.tag_id,
    uit.interest_score,
    uit.create_time
FROM t_user_interest_tag uit
WHERE uit.user_id = 1001 AND uit.status = 'active'
ORDER BY uit.interest_score DESC, uit.create_time DESC;

-- 场景4：内容标签关联查询
SELECT 
    ct.tag_id,
    ct.create_time
FROM t_content_tag ct
WHERE ct.content_id = 2001
ORDER BY ct.create_time DESC;

-- ==========================================
-- 8. 索引维护和优化建议
-- ==========================================

-- 分析表统计信息
ANALYZE TABLE t_tag, t_user_interest_tag, t_content_tag;

-- 检查表的碎片情况
SELECT 
    TABLE_NAME,
    ROUND(DATA_LENGTH / 1024 / 1024, 2) AS data_size_mb,
    ROUND(INDEX_LENGTH / 1024 / 1024, 2) AS index_size_mb,
    ROUND(DATA_FREE / 1024 / 1024, 2) AS free_size_mb,
    ROUND(DATA_FREE / (DATA_LENGTH + INDEX_LENGTH) * 100, 2) AS fragmentation_percent
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = 'collide' 
AND TABLE_NAME LIKE 't_tag%';

-- ==========================================
-- 9. 预期性能提升
-- ==========================================

/*
优化前后性能对比预期：

1. 标签类型查询（selectByTagType）
   - 优化前：使用 idx_tag_type + 回表查询 status
   - 优化后：使用 idx_tag_type_status_usage 复合索引
   - 预期提升：50-70% 性能提升

2. 模糊搜索（searchByName）
   - 优化前：LIKE '%keyword%' 全表扫描
   - 优化后：MATCH...AGAINST 全文索引
   - 预期提升：80-90% 性能提升

3. 热门标签查询（selectHotTags）
   - 优化前：status 索引 + 文件排序
   - 优化后：idx_status_usage_time 覆盖索引
   - 预期提升：60-80% 性能提升

4. 用户兴趣查询（selectByUserId）
   - 优化前：user_id 索引 + 回表 + 文件排序
   - 优化后：idx_user_status_score 覆盖索引
   - 预期提升：70-85% 性能提升
*/

-- ==========================================
-- 注意事项：
-- 1. 在生产环境执行前请先在测试环境验证
-- 2. 建议在业务低峰期执行索引创建操作
-- 3. 定期监控索引使用情况，清理无用索引
-- 4. 根据实际查询模式调整索引策略
-- ==========================================