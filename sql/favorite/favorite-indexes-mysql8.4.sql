-- ==========================================
-- 收藏模块 MySQL 8.0 索引优化 SQL
-- 基于查询模式分析的高性能索引设计
-- ==========================================

USE collide;

-- =================== 删除可能存在的旧索引 ===================
ALTER TABLE `t_favorite` DROP INDEX IF EXISTS `idx_target_id`;
ALTER TABLE `t_favorite` DROP INDEX IF EXISTS `idx_user_id`;
ALTER TABLE `t_favorite` DROP INDEX IF EXISTS `idx_favorite_type`;
ALTER TABLE `t_favorite` DROP INDEX IF EXISTS `idx_status`;

-- =================== 基础复合索引（MySQL 8.0优化） ===================

-- 目标对象收藏查询索引（支持按类型和状态过滤）
ALTER TABLE `t_favorite` ADD INDEX `idx_target_type_status` (`favorite_type`, `target_id`, `status`, `create_time` DESC);

-- 用户收藏查询索引（支持按类型和状态过滤，按时间排序）
ALTER TABLE `t_favorite` ADD INDEX `idx_user_type_status_time` (`user_id`, `favorite_type`, `status`, `create_time` DESC);

-- 作者作品收藏索引（新增，支持按作者查询收藏作品）
ALTER TABLE `t_favorite` ADD INDEX `idx_author_type_status_time` (`target_author_id`, `favorite_type`, `status`, `create_time` DESC);

-- 热门收藏聚合查询索引（优化GROUP BY性能）
ALTER TABLE `t_favorite` ADD INDEX `idx_type_status_target_time` (`favorite_type`, `status`, `target_id`, `create_time` DESC);

-- =================== 状态管理索引 ===================

-- 数据清理索引（按状态和更新时间清理已取消记录）
ALTER TABLE `t_favorite` ADD INDEX `idx_status_update_time` (`status`, `update_time` DESC);

-- 统计查询索引（用户收藏统计）
ALTER TABLE `t_favorite` ADD INDEX `idx_user_status_type` (`user_id`, `status`, `favorite_type`);

-- =================== MySQL 8.0 高级索引优化 ===================

-- 全文搜索索引（用于标题搜索，使用ngram解析器支持中文）
ALTER TABLE `t_favorite` ADD FULLTEXT INDEX `idx_target_title_fulltext` (`target_title`) WITH PARSER ngram;

-- 函数索引（MySQL 8.0新特性，支持大小写不敏感查询）
ALTER TABLE `t_favorite` ADD INDEX `idx_favorite_type_lower` ((LOWER(`favorite_type`)));
ALTER TABLE `t_favorite` ADD INDEX `idx_status_lower` ((LOWER(`status`)));

-- 前缀索引（优化用户昵称查询，节省存储空间）
ALTER TABLE `t_favorite` ADD INDEX `idx_user_nickname_prefix` (`user_nickname`(20));

-- =================== 统计信息更新 ===================

-- 更新表统计信息以优化查询计划
ANALYZE TABLE `t_favorite`;

-- =================== 索引使用说明 ===================

/*
1. 复合索引设计原则：
   - 最常用的查询条件放在最前面
   - 等值查询条件在前，范围查询在后
   - 排序字段放在最后

2. 主要查询模式优化：
   - 用户收藏列表：idx_user_type_status_time
   - 目标收藏统计：idx_target_type_status
   - 作者作品收藏：idx_author_type_status_time
   - 热门收藏排行：idx_type_status_target_time
   - 全文标题搜索：idx_target_title_fulltext
   - 数据清理操作：idx_status_update_time

3. MySQL 8.0 新特性利用：
   - 全文索引ngram解析器：支持中文搜索
   - 函数索引：支持大小写不敏感查询
   - 前缀索引：节省存储空间提升性能
   - 降序索引：优化时间排序查询

4. 索引覆盖优化：
   - 查询尽量使用索引覆盖，避免回表
   - 统计查询利用复合索引提升性能
   - 分页查询使用时间字段索引优化
*/