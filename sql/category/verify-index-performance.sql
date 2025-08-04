-- ==========================================
-- 分类模块索引性能验证脚本
-- 验证 CategoryMapper.xml 中所有查询的执行计划
-- ==========================================

USE collide;

-- =================== 执行计划验证查询 ===================

-- 设置查询分析模式
SET SESSION optimizer_trace='enabled=on';

-- 1. 验证 selectChildCategories 查询性能
EXPLAIN FORMAT=JSON 
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM t_category
WHERE parent_id = 1 AND status = 'active'
ORDER BY sort ASC;

-- 2. 验证 selectPopularCategories 查询性能
EXPLAIN FORMAT=JSON 
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM t_category
WHERE status = 'active'
ORDER BY content_count DESC, sort ASC;

-- 3. 验证 searchCategories 优化后的查询性能
EXPLAIN FORMAT=JSON 
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM t_category
WHERE (name LIKE '小说%' OR LOWER(name) LIKE LOWER('小说%')) AND status = 'active'
ORDER BY CASE WHEN name = '小说' THEN 1 ELSE 2 END, content_count DESC, sort ASC;

-- 4. 验证 existsCategoryName 查询性能
EXPLAIN FORMAT=JSON 
SELECT COUNT(*) > 0
FROM t_category
WHERE name = '测试分类' AND parent_id = 1;

-- 5. 验证 selectLeafCategories 查询性能
EXPLAIN FORMAT=JSON 
SELECT c.id, c.name, c.description, c.parent_id, c.icon_url, c.sort, c.content_count, c.status, c.create_time, c.update_time
FROM t_category c
LEFT JOIN t_category child ON c.id = child.parent_id
WHERE child.id IS NULL AND c.status = 'active'
ORDER BY c.sort ASC;

-- 6. 验证 selectCategoryAncestors 递归CTE查询性能
EXPLAIN FORMAT=JSON 
WITH RECURSIVE category_ancestors AS (
    SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time, 0 as level
    FROM t_category 
    WHERE id = 1
    
    UNION ALL
    
    SELECT c.id, c.name, c.description, c.parent_id, c.icon_url, c.sort, c.content_count, c.status, c.create_time, c.update_time, ca.level + 1
    FROM t_category c
    INNER JOIN category_ancestors ca ON c.id = ca.parent_id
    WHERE c.parent_id != 0
)
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM category_ancestors
WHERE level > 0 AND status = 'active'
ORDER BY level DESC;

-- 7. 验证 selectCategoryDescendants 递归CTE查询性能
EXPLAIN FORMAT=JSON 
WITH RECURSIVE category_descendants AS (
    SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time, 0 as level
    FROM t_category 
    WHERE id = 1
    
    UNION ALL
    
    SELECT c.id, c.name, c.description, c.parent_id, c.icon_url, c.sort, c.content_count, c.status, c.create_time, c.update_time, cd.level + 1
    FROM t_category c
    INNER JOIN category_descendants cd ON c.parent_id = cd.id
    WHERE cd.level < 3
)
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM category_descendants
WHERE level > 0 AND status = 'active'
ORDER BY level ASC, sort ASC;

-- 8. 验证范围查询性能 - selectCategoriesByContentCountRange
EXPLAIN FORMAT=JSON 
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM t_category
WHERE content_count >= 10 AND content_count <= 100 AND status = 'active'
ORDER BY content_count DESC;

-- 9. 验证范围查询性能 - selectCategoriesBySortRange
EXPLAIN FORMAT=JSON 
SELECT id, name, description, parent_id, icon_url, sort, content_count, status, create_time, update_time
FROM t_category
WHERE sort >= 1 AND sort <= 10 AND parent_id = 0 AND status = 'active'
ORDER BY sort ASC;

-- =================== 索引使用统计查询 ===================

-- 查看当前索引统计信息
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    CARDINALITY,
    SUB_PART as 'Prefix_Length',
    INDEX_TYPE,
    COMMENT
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_category'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- =================== 性能基准测试 ===================

-- 创建性能测试用的临时数据（如果需要）
-- 注意：生产环境请谨慎执行

-- 测试1：父分类查询性能测试
SELECT 'Testing selectChildCategories performance...' as Test;
SELECT BENCHMARK(1000, (
    SELECT COUNT(*) FROM t_category 
    WHERE parent_id = 0 AND status = 'active' 
    ORDER BY sort
)) as 'Performance_Test_1';

-- 测试2：搜索查询性能测试
SELECT 'Testing searchCategories performance...' as Test;
SELECT BENCHMARK(1000, (
    SELECT COUNT(*) FROM t_category 
    WHERE name LIKE '小%' AND status = 'active'
)) as 'Performance_Test_2';

-- 测试3：热门分类查询性能测试
SELECT 'Testing selectPopularCategories performance...' as Test;
SELECT BENCHMARK(1000, (
    SELECT COUNT(*) FROM t_category 
    WHERE status = 'active' 
    ORDER BY content_count DESC 
    LIMIT 10
)) as 'Performance_Test_3';

-- =================== 查询优化建议 ===================

-- 检查可能需要优化的查询
SELECT 
    'Query Optimization Analysis' as 'Analysis Type',
    'Check if all frequently used queries are using indexes properly' as 'Recommendation';

-- 显示索引大小和数量
SELECT 
    COUNT(*) as 'Total_Indexes',
    SUM(CASE WHEN INDEX_NAME != 'PRIMARY' THEN 1 ELSE 0 END) as 'Secondary_Indexes',
    AVG(CARDINALITY) as 'Average_Cardinality'
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_category';

-- 验证完成
SELECT 'Index performance verification completed!' as 'Status',
       NOW() as 'Completion_Time';