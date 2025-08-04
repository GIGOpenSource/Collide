-- ==========================================
-- 分类模块索引优化 - 生产环境应用脚本
-- 基于 CategoryMapper.xml 优化后的查询模式
-- 兼容 MySQL 8.0+ 版本
-- ==========================================

USE collide;

-- =================== 安全删除旧索引 ===================
-- 删除原有的单列索引，避免索引冗余

-- 检查并删除旧索引（如果存在）
SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_category' AND INDEX_NAME = 'idx_parent_id') > 0,
    'DROP INDEX `idx_parent_id` ON `t_category`',
    'SELECT "Index idx_parent_id does not exist" as Info'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_category' AND INDEX_NAME = 'idx_status') > 0,
    'DROP INDEX `idx_status` ON `t_category`',
    'SELECT "Index idx_status does not exist" as Info'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (SELECT IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS 
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 't_category' AND INDEX_NAME = 'idx_sort') > 0,
    'DROP INDEX `idx_sort` ON `t_category`',
    'SELECT "Index idx_sort does not exist" as Info'
));
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =================== 创建优化复合索引 ===================

-- 1. 【高频查询】父分类查询 + 状态过滤 + 排序优化
-- 覆盖: selectChildCategories, selectCategoriesPage, selectCategoryTree
CREATE INDEX IF NOT EXISTS `idx_parent_status_sort` ON `t_category` (`parent_id`, `status`, `sort`);

-- 2. 【热门分类】状态 + 内容数量降序 + 排序
-- 覆盖: selectPopularCategories
CREATE INDEX IF NOT EXISTS `idx_status_content_desc_sort` ON `t_category` (`status`, `content_count` DESC, `sort`);

-- 3. 【搜索优化】名称前缀索引 + 状态 + 内容数量
-- 覆盖: searchCategories, selectCategorySuggestions (前缀索引优化)
CREATE INDEX IF NOT EXISTS `idx_name_prefix_status_content` ON `t_category` (`name`(20), `status`, `content_count` DESC);

-- 4. 【时间查询】状态 + 创建时间降序
-- 覆盖: 按创建时间排序的查询
CREATE INDEX IF NOT EXISTS `idx_status_create_time_desc` ON `t_category` (`status`, `create_time` DESC);

-- 5. 【时间查询】状态 + 更新时间降序  
-- 覆盖: 按更新时间排序的查询
CREATE INDEX IF NOT EXISTS `idx_status_update_time_desc` ON `t_category` (`status`, `update_time` DESC);

-- 6. 【范围查询】内容数量范围 + 状态 + 排序
-- 覆盖: selectCategoriesByContentCountRange
CREATE INDEX IF NOT EXISTS `idx_content_count_status_sort` ON `t_category` (`content_count`, `status`, `sort`);

-- 7. 【范围查询】排序值范围 + 父分类 + 状态
-- 覆盖: selectCategoriesBySortRange  
CREATE INDEX IF NOT EXISTS `idx_sort_parent_status` ON `t_category` (`sort`, `parent_id`, `status`);

-- 8. 【叶子分类查询】优化 LEFT JOIN 性能
-- 专用于 selectLeafCategories 的优化索引
CREATE INDEX IF NOT EXISTS `idx_parent_for_leaf_query` ON `t_category` (`parent_id`, `status`, `sort`);

-- =================== MySQL 8.0+ 新特性索引（可选） ===================

-- 9. 【函数索引】优化大小写不敏感搜索 (MySQL 8.0.13+)
-- 用于不区分大小写的名称搜索 (仅在MySQL 8.0.13+版本执行)
SET @version = (SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(VERSION(), '-', 1), '.', 2));
SET @major = CAST(SUBSTRING_INDEX(@version, '.', 1) AS UNSIGNED);
SET @minor = CAST(SUBSTRING_INDEX(@version, '.', -1) AS UNSIGNED);

SET @sql = IF(@major > 8 OR (@major = 8 AND @minor >= 1),
    'CREATE INDEX IF NOT EXISTS `idx_name_lower_status` ON `t_category` ((LOWER(`name`)), `status`)',
    'SELECT "MySQL version does not support functional indexes" as Info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =================== 索引创建验证 ===================

-- 显示所有创建的索引
SELECT 
    INDEX_NAME as 'Index Name',
    COLUMN_NAME as 'Column',
    SEQ_IN_INDEX as 'Position',
    COLLATION as 'Order',
    CARDINALITY as 'Cardinality',
    SUB_PART as 'Prefix Length',
    INDEX_TYPE as 'Type'
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_category'
AND INDEX_NAME != 'PRIMARY'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- 索引大小统计
SELECT 
    INDEX_NAME,
    ROUND(((INDEX_LENGTH) / 1024 / 1024), 2) AS 'Index Size (MB)'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 't_category';

-- 验证完成消息
SELECT 'Category table indexes optimization completed successfully!' as 'Status';