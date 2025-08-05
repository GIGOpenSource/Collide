-- ==========================================
-- Content 模块性能监控脚本
-- 用于分析查询性能和索引使用情况
-- ==========================================

USE collide;

-- ==========================================
-- 1. 基础表信息
-- ==========================================

SELECT '==================== 表基础信息 ====================' as info;

-- 查看表结构和索引
SHOW CREATE TABLE t_content;

-- 查看所有索引
SHOW INDEX FROM t_content;

-- 表统计信息
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '行数',
    ROUND(DATA_LENGTH/1024/1024, 2) as '数据大小(MB)',
    ROUND(INDEX_LENGTH/1024/1024, 2) as '索引大小(MB)',
    ROUND((DATA_LENGTH + INDEX_LENGTH)/1024/1024, 2) as '总大小(MB)'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'collide' AND TABLE_NAME = 't_content';

-- ==========================================
-- 2. 索引使用情况分析
-- ==========================================

SELECT '==================== 索引使用情况 ====================' as info;

-- 检查索引基数（Cardinality）
SELECT 
    INDEX_NAME as '索引名',
    COLUMN_NAME as '列名', 
    CARDINALITY as '基数',
    SUB_PART as '子部分',
    INDEX_TYPE as '索引类型'
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' AND TABLE_NAME = 't_content'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- 查看索引使用统计（需要开启 performance_schema）
SELECT 
    object_name AS '表名',
    index_name AS '索引名',
    count_fetch AS '读取次数',
    count_insert AS '插入次数',
    count_update AS '更新次数',
    count_delete AS '删除次数'
FROM performance_schema.table_io_waits_summary_by_index_usage 
WHERE object_schema = 'collide' AND object_name = 't_content'
ORDER BY count_fetch DESC;

-- ==========================================
-- 3. 慢查询分析
-- ==========================================

SELECT '==================== 慢查询分析 ====================' as info;

-- 显示慢查询日志状态
SHOW VARIABLES LIKE 'slow_query_log%';
SHOW VARIABLES LIKE 'long_query_time';

-- 查看慢查询统计
SHOW GLOBAL STATUS LIKE 'Slow_queries';

-- ==========================================
-- 4. 常用查询的执行计划测试
-- ==========================================

SELECT '==================== 执行计划测试 ====================' as info;

-- 测试1：按作者查询
EXPLAIN SELECT * FROM t_content 
WHERE author_id = 1 AND status = 'PUBLISHED' AND review_status = 'APPROVED'
ORDER BY publish_time DESC LIMIT 10;

-- 测试2：热门内容查询
EXPLAIN SELECT * FROM t_content 
WHERE status = 'PUBLISHED' AND review_status = 'APPROVED'
ORDER BY view_count DESC LIMIT 10;

-- 测试3：分类查询
EXPLAIN SELECT * FROM t_content 
WHERE category_id = 1 AND status = 'PUBLISHED' AND review_status = 'APPROVED'
ORDER BY publish_time DESC LIMIT 10;

-- 测试4：全文搜索查询
EXPLAIN SELECT * FROM t_content 
WHERE MATCH(title, description) AGAINST('测试' IN NATURAL LANGUAGE MODE)
AND status = 'PUBLISHED' AND review_status = 'APPROVED'
ORDER BY MATCH(title, description) AGAINST('测试' IN NATURAL LANGUAGE MODE) DESC LIMIT 10;

-- 测试5：时间范围查询
EXPLAIN SELECT * FROM t_content 
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
AND status = 'PUBLISHED' AND review_status = 'APPROVED'
ORDER BY create_time DESC LIMIT 10;

-- ==========================================
-- 5. 性能基准测试
-- ==========================================

SELECT '==================== 性能基准测试 ====================' as info;

-- 设置性能测试参数
SET @start_time = NOW(6);

-- 测试1：简单主键查询
SELECT SQL_NO_CACHE * FROM t_content WHERE id = 1;
SET @test1_time = NOW(6);

-- 测试2：索引查询
SELECT SQL_NO_CACHE * FROM t_content 
WHERE author_id = 1 AND status = 'PUBLISHED' 
ORDER BY publish_time DESC LIMIT 10;
SET @test2_time = NOW(6);

-- 测试3：全文搜索
SELECT SQL_NO_CACHE * FROM t_content 
WHERE MATCH(title, description) AGAINST('测试' IN NATURAL LANGUAGE MODE)
LIMIT 10;
SET @test3_time = NOW(6);

-- 测试4：聚合查询
SELECT SQL_NO_CACHE COUNT(*), AVG(view_count), MAX(like_count) 
FROM t_content 
WHERE status = 'PUBLISHED';
SET @test4_time = NOW(6);

-- 显示测试结果
SELECT 
    '主键查询' as test_name,
    TIMESTAMPDIFF(MICROSECOND, @start_time, @test1_time) as time_microseconds
UNION ALL
SELECT 
    '索引查询' as test_name,
    TIMESTAMPDIFF(MICROSECOND, @test1_time, @test2_time) as time_microseconds
UNION ALL
SELECT 
    '全文搜索' as test_name,
    TIMESTAMPDIFF(MICROSECOND, @test2_time, @test3_time) as time_microseconds
UNION ALL
SELECT 
    '聚合查询' as test_name,
    TIMESTAMPDIFF(MICROSECOND, @test3_time, @test4_time) as time_microseconds;

-- ==========================================
-- 6. 数据分布分析
-- ==========================================

SELECT '==================== 数据分布分析 ====================' as info;

-- 按状态分布
SELECT status, COUNT(*) as count, ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM t_content), 2) as percentage
FROM t_content 
GROUP BY status;

-- 按内容类型分布
SELECT content_type, COUNT(*) as count, ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM t_content), 2) as percentage
FROM t_content 
GROUP BY content_type;

-- 按创建时间分布（最近30天）
SELECT 
    DATE(create_time) as date,
    COUNT(*) as count
FROM t_content 
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(create_time)
ORDER BY date DESC;

-- 热门内容统计
SELECT 
    '总内容数' as metric, COUNT(*) as value FROM t_content
UNION ALL
SELECT 
    '已发布内容数' as metric, COUNT(*) as value FROM t_content WHERE status = 'PUBLISHED'
UNION ALL
SELECT 
    '平均观看数' as metric, ROUND(AVG(view_count), 0) as value FROM t_content WHERE status = 'PUBLISHED'
UNION ALL
SELECT 
    '平均点赞数' as metric, ROUND(AVG(like_count), 0) as value FROM t_content WHERE status = 'PUBLISHED'
UNION ALL
SELECT 
    '最高观看数' as metric, MAX(view_count) as value FROM t_content
UNION ALL
SELECT 
    '最高点赞数' as metric, MAX(like_count) as value FROM t_content;

-- ==========================================
-- 7. 优化建议
-- ==========================================

SELECT '==================== 优化建议 ====================' as info;

-- 检查重复索引
SELECT 
    CONCAT('表 ', TABLE_NAME, ' 可能存在重复索引') as suggestion
FROM (
    SELECT TABLE_NAME, COLUMN_NAME, COUNT(*) as cnt
    FROM information_schema.STATISTICS 
    WHERE TABLE_SCHEMA = 'collide' AND TABLE_NAME = 't_content'
    GROUP BY TABLE_NAME, COLUMN_NAME
    HAVING cnt > 1
) t;

-- 检查未使用的索引（需要运行一段时间后才有意义）
SELECT 
    CONCAT('索引 ', index_name, ' 可能未被使用') as suggestion
FROM performance_schema.table_io_waits_summary_by_index_usage 
WHERE object_schema = 'collide' 
AND object_name = 't_content'
AND index_name IS NOT NULL
AND index_name != 'PRIMARY'
AND count_fetch = 0;

-- 表碎片检查
SELECT 
    TABLE_NAME as '表名',
    ROUND(DATA_FREE/1024/1024, 2) as '碎片大小(MB)',
    CASE 
        WHEN DATA_FREE > 100*1024*1024 THEN '建议执行 OPTIMIZE TABLE'
        ELSE '表状态良好'
    END as '建议'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'collide' AND TABLE_NAME = 't_content';

SELECT '==================== 性能监控完成 ====================' as info;