-- ==========================================
-- 点赞模块 MySQL 8.0/8.4 优化索引
-- 基于实际查询模式设计的高效索引方案
-- ==========================================

USE collide;

-- 删除like-simple.sql中的基础索引（保留主键和唯一约束）
-- 注意：在生产环境中应该谨慎操作，建议先添加新索引再删除旧索引

-- 保留的基础索引：
-- PRIMARY KEY (`id`) - 保留
-- UNIQUE KEY `uk_user_target` (`user_id`, `like_type`, `target_id`) - 保留

-- 删除将被优化的单列索引
ALTER TABLE `t_like` DROP INDEX IF EXISTS `idx_target_id`;
ALTER TABLE `t_like` DROP INDEX IF EXISTS `idx_user_id`;
ALTER TABLE `t_like` DROP INDEX IF EXISTS `idx_like_type`;
ALTER TABLE `t_like` DROP INDEX IF EXISTS `idx_status`;

-- ==========================================
-- MySQL 8.0 高效复合索引
-- ==========================================

-- 1. 作者作品点赞查询优化索引
-- 优化：findAuthorLikes, countAuthorLikes
-- 查询模式：WHERE target_author_id = ? [AND like_type = ?] [AND status = ?] ORDER BY create_time DESC
CREATE INDEX `idx_author_type_status_time` ON `t_like` (
    `target_author_id`, 
    `like_type`, 
    `status`, 
    `create_time` DESC
) COMMENT '作者作品点赞查询优化索引 - 支持作者ID、类型、状态过滤和时间排序';

-- 2. 用户点赞记录查询优化索引
-- 优化：findUserLikes, countUserLikes
-- 查询模式：WHERE user_id = ? [AND like_type = ?] [AND status = ?] ORDER BY create_time DESC
CREATE INDEX `idx_user_type_status_time` ON `t_like` (
    `user_id`, 
    `like_type`, 
    `status`, 
    `create_time` DESC
) COMMENT '用户点赞记录查询优化索引 - 支持用户ID、类型、状态过滤和时间排序';

-- 3. 目标对象点赞查询优化索引
-- 优化：findTargetLikes, countTargetLikes
-- 查询模式：WHERE target_id = ? AND like_type = ? [AND status = ?] ORDER BY create_time DESC
CREATE INDEX `idx_target_type_status_time` ON `t_like` (
    `target_id`, 
    `like_type`, 
    `status`, 
    `create_time` DESC
) COMMENT '目标对象点赞查询优化索引 - 支持目标ID、类型、状态过滤和时间排序';

-- 4. 时间范围查询优化索引
-- 优化：findByTimeRange
-- 查询模式：WHERE create_time BETWEEN ? AND ? [AND like_type = ?] [AND status = ?] ORDER BY create_time DESC
CREATE INDEX `idx_time_type_status` ON `t_like` (
    `create_time` DESC, 
    `like_type`, 
    `status`
) COMMENT '时间范围查询优化索引 - 支持时间范围过滤、类型、状态和时间排序';

-- 5. 点赞状态检查优化索引
-- 优化：checkLikeExists
-- 查询模式：WHERE user_id = ? AND like_type = ? AND target_id = ? AND status = 'active'
CREATE INDEX `idx_user_type_target_status` ON `t_like` (
    `user_id`, 
    `like_type`, 
    `target_id`, 
    `status`
) COMMENT '点赞状态检查优化索引 - 支持用户、类型、目标和状态的精确匹配';

-- 6. 批量点赞状态检查优化索引
-- 优化：batchCheckLikeStatus
-- 查询模式：WHERE user_id = ? AND like_type = ? AND status = 'active' AND target_id IN (...)
CREATE INDEX `idx_user_type_status_target` ON `t_like` (
    `user_id`, 
    `like_type`, 
    `status`, 
    `target_id`
) COMMENT '批量点赞状态检查优化索引 - 支持用户、类型、状态过滤和目标ID批量查询';

-- 7. 目标对象类型状态统计索引
-- 优化：countTargetLikes 的特殊情况
-- 查询模式：WHERE target_id = ? AND like_type = ? AND status = ?
CREATE INDEX `idx_target_type_status` ON `t_like` (
    `target_id`, 
    `like_type`, 
    `status`
) COMMENT '目标对象类型状态统计索引 - 支持目标ID、类型、状态的统计查询';

-- 8. 用户类型状态统计索引
-- 优化：countUserLikes 的特殊情况
-- 查询模式：WHERE user_id = ? AND like_type = ? AND status = ?
CREATE INDEX `idx_user_type_status` ON `t_like` (
    `user_id`, 
    `like_type`, 
    `status`
) COMMENT '用户类型状态统计索引 - 支持用户ID、类型、状态的统计查询';

-- 9. 作者类型状态统计索引
-- 优化：countAuthorLikes 的特殊情况
-- 查询模式：WHERE target_author_id = ? AND like_type = ? AND status = ?
CREATE INDEX `idx_author_type_status` ON `t_like` (
    `target_author_id`, 
    `like_type`, 
    `status`
) COMMENT '作者类型状态统计索引 - 支持作者ID、类型、状态的统计查询';

-- ==========================================
-- MySQL 8.0 高级特性索引
-- ==========================================

-- 10. 降序时间索引（MySQL 8.0特性）
-- 优化所有按时间倒序的查询
CREATE INDEX `idx_create_time_desc` ON `t_like` (
    `create_time` DESC
) COMMENT 'MySQL 8.0降序时间索引 - 优化所有时间倒序查询，避免filesort';

-- 11. 状态活跃记录索引（部分索引概念）
-- 优化大部分查询中的 status = 'active' 条件
CREATE INDEX `idx_status_active_time` ON `t_like` (
    `status`, 
    `create_time` DESC
) COMMENT '活跃状态记录索引 - 优化状态过滤和时间排序';

-- ==========================================
-- 覆盖索引（Covering Index）
-- ==========================================

-- 12. 用户点赞统计覆盖索引
-- 覆盖 countUserLikes 查询的所有字段，避免回表
CREATE INDEX `idx_user_count_covering` ON `t_like` (
    `user_id`, 
    `like_type`, 
    `status`
) COMMENT '用户点赞统计覆盖索引 - 避免回表查询，提升COUNT性能';

-- 13. 目标对象点赞统计覆盖索引
-- 覆盖 countTargetLikes 查询的所有字段，避免回表
CREATE INDEX `idx_target_count_covering` ON `t_like` (
    `target_id`, 
    `like_type`, 
    `status`
) COMMENT '目标对象点赞统计覆盖索引 - 避免回表查询，提升COUNT性能';

-- ==========================================
-- 索引使用分析和监控
-- ==========================================

-- 查看索引使用情况的SQL
/*
-- 1. 检查索引使用情况
SELECT 
    INDEX_NAME,
    CARDINALITY,
    SUB_PART,
    PACKED,
    NULLABLE,
    INDEX_TYPE,
    COMMENT
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' AND TABLE_NAME = 't_like'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- 2. 分析查询执行计划
EXPLAIN SELECT * FROM t_like WHERE target_author_id = 1 ORDER BY create_time DESC LIMIT 10;
EXPLAIN SELECT * FROM t_like WHERE user_id = 1 AND like_type = 'CONTENT' ORDER BY create_time DESC LIMIT 10;
EXPLAIN SELECT COUNT(*) FROM t_like WHERE target_id = 1 AND like_type = 'CONTENT' AND status = 'active';

-- 3. 查看索引统计信息
SHOW INDEX FROM t_like;

-- 4. 查看表状态
SHOW TABLE STATUS LIKE 't_like';
*/

-- ==========================================
-- 索引维护建议
-- ==========================================

/*
性能优化建议：

1. 索引选择原则：
   - 高选择性字段优先（user_id, target_id, target_author_id）
   - 等值条件优先于范围条件
   - 排序字段放在索引末尾

2. MySQL 8.0 特性利用：
   - 降序索引优化 ORDER BY ... DESC
   - 复合索引覆盖更多查询场景
   - 更好的索引统计信息

3. 监控指标：
   - 索引命中率
   - 查询执行时间
   - 索引空间使用情况

4. 维护策略：
   - 定期分析索引使用情况
   - 删除未使用的索引
   - 根据查询模式调整索引

5. 注意事项：
   - 写入性能会因索引增加而下降
   - 监控索引空间占用
   - 根据实际查询QPS调整索引策略
*/

-- ==========================================
-- 索引创建完成提示
-- ==========================================

-- 显示优化后的索引列表
SELECT 
    CONCAT('✅ 索引: ', INDEX_NAME, ' | 列: ', GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX), ' | 说明: ', COALESCE(MAX(COMMENT), '无说明')) as 优化结果
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' AND TABLE_NAME = 't_like'
GROUP BY INDEX_NAME
ORDER BY INDEX_NAME;