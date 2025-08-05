-- ==========================================
-- 内容模块优化索引文件 
-- 专门针对内容模块的高性能索引设计
-- 兼容MySQL 5.7+版本
-- ==========================================

USE collide;

-- =================== t_content 表索引优化 ===================

-- 删除旧索引 (如果存在)
ALTER TABLE t_content 
DROP INDEX IF EXISTS `idx_author_id`,
DROP INDEX IF EXISTS `idx_category_id`, 
DROP INDEX IF EXISTS `idx_content_type`,
DROP INDEX IF EXISTS `idx_status`,
DROP INDEX IF EXISTS `idx_publish_time`;

-- 添加通用MySQL优化索引

-- 1. 作者相关复合索引 (最左前缀匹配，支持多维度查询)
ALTER TABLE t_content ADD INDEX `idx_author_status_publish` (`author_id`, `status`, `publish_time` DESC);

-- 2. 分类相关复合索引
ALTER TABLE t_content ADD INDEX `idx_category_status_publish` (`category_id`, `status`, `publish_time` DESC);

-- 3. 内容类型复合索引  
ALTER TABLE t_content ADD INDEX `idx_type_status_publish` (`content_type`, `status`, `publish_time` DESC);

-- 4. 内容状态复合索引 (支持状态查询、审核状态、发布时间排序)
ALTER TABLE t_content ADD INDEX `idx_content_status_publish` (`status`, `review_status`, `publish_time` DESC);

-- 5. 审核状态索引
ALTER TABLE t_content ADD INDEX `idx_review_status` (`review_status`, `status`);

-- 6. 热门内容复合索引 (按热度排序查询)
ALTER TABLE t_content ADD INDEX `idx_hot_content` (`view_count` DESC, `like_count` DESC, `publish_time` DESC);

-- 7. 评分统计索引
ALTER TABLE t_content ADD INDEX `idx_score_stats` (`score_count` DESC, `score_total` DESC);

-- 8. 标题搜索索引 (通用MySQL版本兼容)
ALTER TABLE t_content ADD INDEX `idx_title` (`title`);

-- =================== t_content_chapter 表索引优化 ===================

-- 删除旧索引 (如果存在)
ALTER TABLE t_content_chapter 
DROP INDEX IF EXISTS `idx_content_id`,
DROP INDEX IF EXISTS `idx_status`;

-- 添加通用MySQL优化索引

-- 1. 内容状态复合索引 (支持内容查询、状态筛选、章节号排序)
ALTER TABLE t_content_chapter ADD INDEX `idx_content_status` (`content_id`, `status`, `chapter_num` ASC);

-- 2. 章节范围查询索引 (支持上一章、下一章查询)
ALTER TABLE t_content_chapter ADD INDEX `idx_chapter_range` (`content_id`, `chapter_num` ASC, `status`);

-- 3. 状态时间索引 (支持状态查询、时间排序)
ALTER TABLE t_content_chapter ADD INDEX `idx_status_time` (`status`, `create_time` DESC);

-- 4. 字数统计索引 (支持字数查询和排序)
ALTER TABLE t_content_chapter ADD INDEX `idx_word_count` (`content_id`, `word_count` DESC);

-- 5. 章节标题索引 (通用MySQL版本兼容)
ALTER TABLE t_content_chapter ADD INDEX `idx_chapter_title` (`title`);

-- =================== t_user_content_purchase 表索引优化 ===================

-- 删除旧索引 (如果存在)
ALTER TABLE t_user_content_purchase 
DROP INDEX IF EXISTS `idx_user_id`,
DROP INDEX IF EXISTS `idx_content_id`,
DROP INDEX IF EXISTS `idx_order_id`,
DROP INDEX IF EXISTS `idx_order_no`,
DROP INDEX IF EXISTS `idx_status`,
DROP INDEX IF EXISTS `idx_purchase_time`;

-- 添加通用MySQL优化索引

-- 1. 用户状态复合索引 (支持用户购买记录查询、状态筛选、购买时间排序)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_user_status` (`user_id`, `status`, `purchase_time` DESC);

-- 2. 内容销售复合索引 (支持内容销售查询、状态筛选、购买时间排序)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_content_sales` (`content_id`, `status`, `purchase_time` DESC);

-- 3. 订单信息索引 (支持订单查询)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_order_info` (`order_id`, `order_no`);

-- 4. 内容类型索引 (支持按内容类型查询购买记录)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_content_type` (`user_id`, `content_type`, `purchase_time` DESC);

-- 5. 作者购买索引 (支持用户查询特定作者的购买记录)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_author_purchase` (`user_id`, `author_id`, `purchase_time` DESC);

-- 6. 过期时间索引 (支持过期记录查询和批量处理)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_expire_time` (`status`, `expire_time`);

-- 7. 金币金额索引 (支持高价值购买查询和统计)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_coin_amount` (`coin_amount` DESC, `purchase_time` DESC);

-- 8. 访问统计索引 (支持访问次数查询和统计)
ALTER TABLE t_user_content_purchase ADD INDEX `idx_access_stats` (`access_count` DESC, `last_access_time` DESC);

-- =================== t_content_payment 表索引优化 ===================

-- 删除旧索引 (如果存在)
ALTER TABLE t_content_payment 
DROP INDEX IF EXISTS `idx_payment_type`,
DROP INDEX IF EXISTS `idx_coin_price`,
DROP INDEX IF EXISTS `idx_status`;

-- 添加通用MySQL优化索引

-- 1. 付费类型状态复合索引 (支持付费类型查询、状态筛选、创建时间排序)
ALTER TABLE t_content_payment ADD INDEX `idx_payment_status` (`payment_type`, `status`, `create_time` DESC);

-- 2. VIP相关复合索引 (支持VIP免费和VIP专享查询)
ALTER TABLE t_content_payment ADD INDEX `idx_vip_status` (`vip_free`, `vip_only`, `status`);

-- 3. 价格范围索引 (支持价格范围查询和排序)
ALTER TABLE t_content_payment ADD INDEX `idx_price_range` (`payment_type`, `coin_price` ASC, `status`);

-- 4. 试读功能索引 (支持试读内容查询)
ALTER TABLE t_content_payment ADD INDEX `idx_trial_status` (`trial_enabled`, `status`, `create_time` DESC);

-- 5. 永久/限时索引 (支持永久和限时内容查询)
ALTER TABLE t_content_payment ADD INDEX `idx_permanent_status` (`is_permanent`, `valid_days`, `status`);

-- 6. 销售统计索引 (支持销售排行和收入统计)
ALTER TABLE t_content_payment ADD INDEX `idx_sales_stats` (`total_sales` DESC, `total_revenue` DESC, `status`);

-- 7. 价格相关索引 (通用MySQL版本兼容)
ALTER TABLE t_content_payment ADD INDEX `idx_price_stats` (`coin_price`, `original_price`);

-- =================== 索引使用建议和查询优化 ===================

/*
1. 复合索引最左前缀原则：
   - idx_author_status_publish 可以支持：
     * WHERE author_id = ?
     * WHERE author_id = ? AND status = ?  
     * WHERE author_id = ? AND status = ? ORDER BY publish_time DESC

2. 查询优化建议：
   - 标题搜索：WHERE title LIKE '%keyword%'
   - 利用复合索引进行多条件查询
   - 使用ORDER BY时注意索引字段顺序

3. 索引优化：
   - 利用复合索引覆盖多维度查询
   - 特别是时间字段和统计字段的排序查询

4. 索引统计信息更新：
   - 定期执行 ANALYZE TABLE 更新索引统计信息
   - 使用 OPTIMIZER_TRACE 分析查询执行计划

5. 查询优化建议：
   - 避免SELECT * ，明确指定需要的字段
   - 合理使用LIMIT，特别是大数据量分页
   - 利用覆盖索引，减少回表查询
   - 对于大文本字段（如content_data URL），建议分离存储
*/