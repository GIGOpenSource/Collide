-- ==========================================
-- 分类模块索引优化 - MySQL 8.4.1 版本
-- 基于 CategoryMapper.xml 查询模式分析的索引优化策略
-- ==========================================

USE collide;

-- =================== 删除旧索引（如果存在） ===================
-- 删除原有的简单索引，替换为优化的复合索引

-- 保留主键和唯一索引
-- PRIMARY KEY (`id`) -- 保留
-- UNIQUE KEY `uk_name_parent` (`name`, `parent_id`) -- 保留

-- 删除单列索引，用复合索引替换
DROP INDEX IF EXISTS `idx_parent_id` ON `t_category`;
DROP INDEX IF EXISTS `idx_status` ON `t_category`;
DROP INDEX IF EXISTS `idx_sort` ON `t_category`;

-- =================== MySQL 8.4.1 优化索引设计 ===================

-- 1. 【高频查询】父分类查询 + 状态过滤 + 排序优化
-- 覆盖: selectChildCategories, selectCategoriesPage, selectCategoryTree
CREATE INDEX `idx_parent_status_sort` ON `t_category` (`parent_id`, `status`, `sort`);

-- 2. 【热门分类】状态 + 内容数量降序 + 排序
-- 覆盖: selectPopularCategories (MySQL 8.4.1 支持降序索引优化)
CREATE INDEX `idx_status_content_desc_sort` ON `t_category` (`status`, `content_count` DESC, `sort`);

-- 3. 【搜索优化】名称前缀索引 + 状态 + 内容数量
-- 覆盖: searchCategories, selectCategorySuggestions
-- MySQL 8.4.1 前缀索引优化，减少索引大小
CREATE INDEX `idx_name_prefix_status_content` ON `t_category` (`name`(20), `status`, `content_count` DESC);

-- 4. 【时间查询】状态 + 创建时间降序
-- 覆盖: 按时间排序的查询
CREATE INDEX `idx_status_create_time_desc` ON `t_category` (`status`, `create_time` DESC);

-- 5. 【时间查询】状态 + 更新时间降序  
-- 覆盖: 按更新时间排序的查询
CREATE INDEX `idx_status_update_time_desc` ON `t_category` (`status`, `update_time` DESC);

-- 6. 【范围查询】内容数量范围 + 状态 + 排序
-- 覆盖: selectCategoriesByContentCountRange
CREATE INDEX `idx_content_count_status_sort` ON `t_category` (`content_count`, `status`, `sort`);

-- 7. 【范围查询】排序值范围 + 父分类 + 状态
-- 覆盖: selectCategoriesBySortRange  
CREATE INDEX `idx_sort_parent_status` ON `t_category` (`sort`, `parent_id`, `status`);

-- 8. 【叶子分类查询】优化 LEFT JOIN 性能
-- 专用于 selectLeafCategories 的优化索引
CREATE INDEX `idx_parent_for_leaf_query` ON `t_category` (`parent_id`, `status`, `sort`);

-- =================== MySQL 8.4.1 新特性索引 ===================

-- 9. 【不可见索引】用于性能测试的备用索引
-- MySQL 8.4.1 支持不可见索引，可以用来测试索引效果而不影响现有查询
CREATE INDEX `idx_invisible_test_parent_name` ON `t_category` (`parent_id`, `name`) INVISIBLE;

-- 10. 【函数索引】优化大小写不敏感搜索 (MySQL 8.4.1 新特性)
-- 用于不区分大小写的名称搜索
CREATE INDEX `idx_name_lower_status` ON `t_category` ((LOWER(`name`)), `status`);

-- 11. 【多值索引】如果将来扩展支持标签数组字段 (MySQL 8.4.1 支持)
-- 为将来可能的JSON标签字段预留
-- CREATE INDEX `idx_tags_multivalue` ON `t_category` ((CAST(`tags` AS JSON ARRAY)));

-- =================== 索引使用建议和统计 ===================

-- 查看索引大小和使用情况
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    INDEX_TYPE,
    IS_VISIBLE,
    COMMENT
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
AND TABLE_NAME = 't_category'
ORDER BY INDEX_NAME;

-- =================== 查询优化建议 ===================

-- 针对 CategoryMapper.xml 中的主要查询模式：

-- 1. selectChildCategories - 使用 idx_parent_status_sort
-- WHERE parent_id = ? AND status = ? ORDER BY sort

-- 2. searchCategories - 使用 idx_name_prefix_status_content  
-- WHERE name LIKE 'prefix%' AND status = ? ORDER BY content_count DESC

-- 3. selectPopularCategories - 使用 idx_status_content_desc_sort
-- WHERE status = ? ORDER BY content_count DESC, sort ASC

-- 4. selectLeafCategories - 使用 idx_parent_for_leaf_query
-- LEFT JOIN 优化，减少扫描范围

-- 5. existsCategoryName - 继续使用原有唯一索引 uk_name_parent
-- WHERE name = ? AND parent_id = ? (完美匹配唯一索引)

-- =================== 性能监控查询 ===================

-- 监控索引使用情况 (MySQL 8.4.1 增强的性能模式)
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    INDEX_NAME,
    COUNT_FETCH,
    COUNT_INSERT,
    COUNT_UPDATE,
    COUNT_DELETE,
    SUM_TIMER_FETCH,
    SUM_TIMER_INSERT,
    SUM_TIMER_UPDATE,
    SUM_TIMER_DELETE
FROM performance_schema.table_io_waits_summary_by_index_usage 
WHERE OBJECT_SCHEMA = 'collide' 
AND OBJECT_NAME = 't_category'
ORDER BY COUNT_FETCH DESC;

-- 查看查询执行计划建议
-- 使用 EXPLAIN FORMAT=JSON SELECT ... 来验证索引使用效果

-- =================== 索引维护建议 ===================

-- 1. 定期更新表统计信息 (MySQL 8.4.1 增强)
-- ANALYZE TABLE t_category UPDATE HISTOGRAM ON parent_id, status, content_count;

-- 2. 监控索引碎片
-- SELECT * FROM INFORMATION_SCHEMA.INNODB_SYS_INDEXES WHERE NAME LIKE '%category%';

-- 3. 根据实际查询模式调整不可视索引
-- ALTER INDEX idx_invisible_test_parent_name ON t_category VISIBLE;
-- ALTER INDEX idx_invisible_test_parent_name ON t_category INVISIBLE;

-- =================== 特殊场景索引 ===================

-- 如果有大量的全文搜索需求，考虑全文索引
-- ALTER TABLE t_category ADD FULLTEXT INDEX `ft_name_description` (`name`, `description`);

-- 如果有地理位置相关需求，可以添加空间索引
-- 但当前 category 表没有地理字段，此处仅作说明

-- =================== 索引维护脚本 ===================

-- 检查重复索引的脚本
SELECT 
    table_schema,
    table_name,
    index_name,
    GROUP_CONCAT(column_name ORDER BY seq_in_index) AS columns
FROM information_schema.statistics 
WHERE table_schema = 'collide' 
AND table_name = 't_category'
GROUP BY table_schema, table_name, index_name
ORDER BY table_name, index_name;

-- 检查未使用的索引 (需要运行一段时间后查看)
SELECT 
    OBJECT_SCHEMA,
    OBJECT_NAME,
    INDEX_NAME
FROM performance_schema.table_io_waits_summary_by_index_usage 
WHERE OBJECT_SCHEMA = 'collide' 
AND OBJECT_NAME = 't_category'
AND INDEX_NAME IS NOT NULL 
AND INDEX_NAME != 'PRIMARY'
AND COUNT_FETCH = 0
AND COUNT_INSERT = 0
AND COUNT_UPDATE = 0
AND COUNT_DELETE = 0;