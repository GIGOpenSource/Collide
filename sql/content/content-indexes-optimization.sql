-- ==========================================
-- Content 表性能优化索引脚本
-- ==========================================

USE collide;

-- 删除可能存在的旧索引
DROP INDEX IF EXISTS idx_author_status_publish ON t_content;
DROP INDEX IF EXISTS idx_category_status_publish ON t_content;
DROP INDEX IF EXISTS idx_type_status_publish ON t_content;
DROP INDEX IF EXISTS idx_title_search ON t_content;
DROP INDEX IF EXISTS idx_tags_search ON t_content;
DROP INDEX IF EXISTS idx_hot_content ON t_content;
DROP INDEX IF EXISTS idx_create_time_desc ON t_content;
DROP INDEX IF EXISTS idx_update_time_desc ON t_content;

-- 1. 核心业务查询优化索引
-- 作者相关查询（按作者ID + 状态 + 发布时间）
CREATE INDEX idx_author_status_publish ON t_content (author_id, status, review_status, publish_time DESC);

-- 分类相关查询（按分类ID + 状态 + 发布时间）
CREATE INDEX idx_category_status_publish ON t_content (category_id, status, review_status, publish_time DESC);

-- 内容类型查询（按内容类型 + 状态 + 发布时间）
CREATE INDEX idx_type_status_publish ON t_content (content_type, status, review_status, publish_time DESC);

-- 2. 热门内容排序优化
-- 观看数排序索引
CREATE INDEX idx_view_count_desc ON t_content (status, review_status, view_count DESC, publish_time DESC);

-- 点赞数排序索引  
CREATE INDEX idx_like_count_desc ON t_content (status, review_status, like_count DESC, publish_time DESC);

-- 收藏数排序索引
CREATE INDEX idx_favorite_count_desc ON t_content (status, review_status, favorite_count DESC, publish_time DESC);

-- 评论数排序索引
CREATE INDEX idx_comment_count_desc ON t_content (status, review_status, comment_count DESC, publish_time DESC);

-- 分享数排序索引
CREATE INDEX idx_share_count_desc ON t_content (status, review_status, share_count DESC, publish_time DESC);

-- 3. 时间范围查询优化
-- 创建时间索引（用于时间范围筛选）
CREATE INDEX idx_create_time_desc ON t_content (create_time DESC, status, review_status);

-- 更新时间索引
CREATE INDEX idx_update_time_desc ON t_content (update_time DESC, status, review_status);

-- 4. 搜索优化
-- 标题全文搜索索引
CREATE FULLTEXT INDEX ft_title_search ON t_content (title);

-- 描述全文搜索索引  
CREATE FULLTEXT INDEX ft_description_search ON t_content (description);

-- 标题和描述联合全文搜索
CREATE FULLTEXT INDEX ft_title_desc_search ON t_content (title, description);

-- 5. 复合业务场景索引
-- 推荐内容查询（状态 + 热度指标）
CREATE INDEX idx_recommend_content ON t_content (status, review_status, view_count DESC, like_count DESC, favorite_count DESC);

-- 相似内容查询（分类 + 类型 + 状态）
CREATE INDEX idx_similar_content ON t_content (category_id, content_type, status, review_status, view_count DESC);

-- 6. 统计查询优化
-- 评分相关索引（score_count > 0 的记录）
CREATE INDEX idx_score_stats ON t_content (score_count, score_total, status, review_status);

-- 7. 管理后台查询优化
-- 审核状态查询
CREATE INDEX idx_review_status_time ON t_content (review_status, create_time DESC);

-- 状态管理查询
CREATE INDEX idx_status_time ON t_content (status, update_time DESC);

-- 查看索引使用情况
SHOW INDEX FROM t_content;