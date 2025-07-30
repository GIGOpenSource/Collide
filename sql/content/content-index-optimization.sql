-- ==========================================
-- 内容模块索引优化 SQL
-- 基于实际查询场景的性能优化建议
-- ==========================================

USE collide;

-- =================== 现有索引分析 ===================
-- ✅ 已有基础索引：
-- PRIMARY KEY (`id`)
-- KEY `idx_author_id` (`author_id`)
-- KEY `idx_category_id` (`category_id`)
-- KEY `idx_content_type` (`content_type`)
-- KEY `idx_status` (`status`)
-- KEY `idx_publish_time` (`publish_time`)

-- =================== 新增索引优化 ===================

-- 🚀 【复合索引】用于常见的组合查询
-- 1. 状态+类型+作者组合查询索引
ALTER TABLE `t_content` ADD INDEX `idx_status_type_author` (`status`, `content_type`, `author_id`);

-- 2. 状态+审核状态+发布时间组合索引（用于内容管理）
ALTER TABLE `t_content` ADD INDEX `idx_status_review_publish` (`status`, `review_status`, `publish_time`);

-- 3. 分类+类型+状态组合索引（用于分类浏览）
ALTER TABLE `t_content` ADD INDEX `idx_category_type_status` (`category_id`, `content_type`, `status`);

-- ⚡ 【统计字段索引】用于热门内容排序
-- 4. 浏览量索引（用于按浏览量排序）
ALTER TABLE `t_content` ADD INDEX `idx_view_count_desc` (`view_count` DESC);

-- 5. 点赞数索引（用于按点赞数排序）  
ALTER TABLE `t_content` ADD INDEX `idx_like_count_desc` (`like_count` DESC);

-- 6. 收藏数索引（用于按收藏数排序）
ALTER TABLE `t_content` ADD INDEX `idx_favorite_count_desc` (`favorite_count` DESC);

-- 7. 综合热度索引（状态+多个统计字段）
ALTER TABLE `t_content` ADD INDEX `idx_hot_content` (`status`, `view_count` DESC, `like_count` DESC, `favorite_count` DESC);

-- 📅 【时间范围索引】用于时间筛选和排序
-- 8. 创建时间+状态组合索引
ALTER TABLE `t_content` ADD INDEX `idx_create_time_status` (`create_time`, `status`);

-- 9. 发布时间降序索引（用于最新内容查询）
ALTER TABLE `t_content` ADD INDEX `idx_publish_time_desc` (`publish_time` DESC);

-- 💯 【评分相关索引】用于评分排序
-- 10. 评分统计组合索引（用于评分计算）
ALTER TABLE `t_content` ADD INDEX `idx_score_stats` (`score_count`, `score_total`);

-- 🔍 【搜索优化索引】用于标题搜索
-- 11. 标题前缀索引（用于LIKE搜索优化）
ALTER TABLE `t_content` ADD INDEX `idx_title_prefix` (`title`(50));

-- 📊 【审核管理索引】用于内容审核
-- 12. 审核状态+创建时间索引
ALTER TABLE `t_content` ADD INDEX `idx_review_create` (`review_status`, `create_time`);

-- =================== 章节表索引优化 ===================

-- 13. 章节内容查询优化（内容ID+章节号+状态）
ALTER TABLE `t_content_chapter` ADD INDEX `idx_content_chapter_status` (`content_id`, `chapter_num`, `status`);

-- 14. 章节统计索引（内容ID+状态+字数）
ALTER TABLE `t_content_chapter` ADD INDEX `idx_content_stats` (`content_id`, `status`, `word_count`);

-- =================== 索引使用建议 ===================

/*
🎯 索引使用场景说明：

1. 【热门内容查询】
   使用: idx_hot_content, idx_view_count_desc, idx_like_count_desc
   查询: SELECT * FROM t_content WHERE status='PUBLISHED' ORDER BY view_count DESC;

2. 【分类浏览】
   使用: idx_category_type_status
   查询: SELECT * FROM t_content WHERE category_id=1 AND content_type='NOVEL' AND status='PUBLISHED';

3. 【作者内容管理】
   使用: idx_status_type_author
   查询: SELECT * FROM t_content WHERE author_id=123 AND status='DRAFT' AND content_type='ARTICLE';

4. 【时间范围筛选】
   使用: idx_create_time_status, idx_publish_time_desc
   查询: SELECT * FROM t_content WHERE create_time >= '2024-01-01' AND status='PUBLISHED';

5. 【内容审核】
   使用: idx_review_create, idx_status_review_publish
   查询: SELECT * FROM t_content WHERE review_status='PENDING' ORDER BY create_time;

6. 【标题搜索】
   使用: idx_title_prefix
   查询: SELECT * FROM t_content WHERE title LIKE '设计模式%' AND status='PUBLISHED';

7. 【评分排序】
   使用: idx_score_stats
   查询: SELECT * FROM t_content WHERE score_count > 0 ORDER BY (score_total/score_count) DESC;
*/

-- =================== 性能监控建议 ===================

/*
📊 监控重点：

1. 【慢查询监控】
   - 启用 slow_query_log
   - 设置 long_query_time = 2
   - 定期分析慢查询日志

2. 【索引使用率】
   - 定期检查: SHOW INDEX FROM t_content;
   - 分析查询计划: EXPLAIN SELECT ...;
   - 监控索引碎片率

3. 【热点数据识别】
   - 监控高频查询的author_id、category_id
   - 识别热门内容的访问模式
   - 考虑缓存策略配合

4. 【索引维护】
   - 定期优化表: OPTIMIZE TABLE t_content;
   - 监控索引大小和碎片率
   - 根据查询模式调整索引策略
*/