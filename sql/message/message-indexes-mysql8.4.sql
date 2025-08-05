-- ==========================================
-- 消息模块 MySQL 8.0/8.4 优化索引
-- 基于message-simple.sql的性能优化方案
-- ==========================================

USE collide;

-- ==========================================
-- t_message 表索引优化
-- ==========================================

-- 删除原有的简单索引（保留主键和唯一索引）
ALTER TABLE t_message DROP INDEX IF EXISTS idx_sender_receiver;
ALTER TABLE t_message DROP INDEX IF EXISTS idx_receiver_status;  
ALTER TABLE t_message DROP INDEX IF EXISTS idx_create_time;
ALTER TABLE t_message DROP INDEX IF EXISTS idx_reply_to;

-- 1. 聊天记录查询优化索引（覆盖索引，包含所有查询字段）
CREATE INDEX idx_chat_history_optimized ON t_message 
(sender_id, receiver_id, create_time, status, id, message_type, content(100), is_pinned)
COMMENT '聊天记录查询优化索引 - 覆盖查询所需字段，避免回表';

-- 2. 接收者状态时间复合索引（降序优化最新消息查询）
CREATE INDEX idx_receiver_status_time_desc ON t_message 
(receiver_id, status, create_time DESC, id)
COMMENT '接收者状态时间降序索引 - 优化最新消息查询和未读统计';

-- 3. 发送者时间统计索引（时间范围查询优化）
CREATE INDEX idx_sender_time_stats ON t_message 
(sender_id, create_time, status, message_type)
COMMENT '发送者时间统计索引 - 优化发送消息统计和时间范围查询';

-- 4. 留言板消息优化索引（置顶+时间排序）
CREATE INDEX idx_wall_messages_optimized ON t_message 
(receiver_id, is_pinned DESC, create_time DESC, status, id)
COMMENT '留言板消息优化索引 - 置顶优先，时间降序，支持状态过滤';

-- 5. 回复消息链索引（回复查询优化）
CREATE INDEX idx_reply_chain_optimized ON t_message 
(reply_to_id, create_time, status, id, sender_id)
COMMENT '回复消息链索引 - 优化回复查询和回复链追踪';

-- 6. 消息内容全文检索索引（MySQL 8.0 ngram支持中文）
CREATE FULLTEXT INDEX idx_content_fulltext ON t_message (content) 
WITH PARSER ngram
COMMENT '消息内容全文索引 - 支持中英文混合检索，替代LIKE查询';

-- 7. 用户消息时间范围索引（用户维度查询优化）
CREATE INDEX idx_user_message_time ON t_message 
(sender_id, receiver_id, create_time DESC, status, message_type)
COMMENT '用户消息时间索引 - 优化用户维度的消息查询和筛选';

-- 8. 消息状态更新索引（状态变更优化）
CREATE INDEX idx_message_status_update ON t_message 
(id, status, read_time, update_time)
COMMENT '消息状态更新索引 - 优化状态变更和已读时间更新';

-- 9. 最新消息查询索引（会话最新消息）
CREATE INDEX idx_latest_message_between ON t_message 
(sender_id, receiver_id, create_time DESC, status, id)
COMMENT '最新消息查询索引 - 优化会话间最新消息获取';

-- 10. 时间范围清理索引（数据清理优化）
CREATE INDEX idx_cleanup_expired ON t_message 
(status, update_time, create_time)
COMMENT '过期数据清理索引 - 优化已删除消息的物理清理';

-- ==========================================
-- t_message_session 表索引优化
-- ==========================================

-- 删除原有索引（保留主键和唯一索引）
ALTER TABLE t_message_session DROP INDEX IF EXISTS idx_user_time;
ALTER TABLE t_message_session DROP INDEX IF EXISTS idx_last_message;

-- 1. 用户会话列表优化索引（时间降序，支持归档筛选）
CREATE INDEX idx_user_sessions_optimized ON t_message_session 
(user_id, is_archived, last_message_time DESC, unread_count, id)
COMMENT '用户会话列表优化索引 - 支持归档筛选，时间降序，包含未读数';

-- 2. 未读会话统计索引（未读数量查询优化）  
CREATE INDEX idx_unread_sessions_count ON t_message_session 
(user_id, unread_count, is_archived, last_message_time DESC)
COMMENT '未读会话统计索引 - 优化未读会话数量统计和查询';

-- 3. 活跃会话查询索引（活跃度筛选优化）
CREATE INDEX idx_active_sessions_time ON t_message_session 
(user_id, last_message_time DESC, unread_count, is_archived)
COMMENT '活跃会话查询索引 - 按活跃时间排序，支持未读和归档筛选';

-- 4. 会话清理优化索引（数据清理）
CREATE INDEX idx_session_cleanup ON t_message_session 
(is_archived, update_time, last_message_id)
COMMENT '会话清理索引 - 优化归档会话清理和空会话删除';

-- 5. 最后消息关联索引（消息关联查询优化）
CREATE INDEX idx_last_message_ref ON t_message_session 
(last_message_id, last_message_time, user_id)
COMMENT '最后消息关联索引 - 优化消息关联查询和一致性检查';

-- ==========================================
-- t_message_setting 表索引优化  
-- ==========================================

-- MessageSetting表较简单，现有uk_user_id唯一索引已足够
-- 添加设置查询优化索引（支持设置值筛选）
CREATE INDEX idx_setting_values ON t_message_setting 
(allow_stranger_msg, auto_read_receipt, message_notification, user_id)
COMMENT '消息设置值索引 - 优化按设置值筛选用户的查询';

-- ==========================================
-- 索引使用说明
-- ==========================================

/*
索引优化策略说明：

1. 覆盖索引设计：
   - 主要查询索引包含所有SELECT字段，避免回表查询
   - 减少I/O操作，提升查询性能

2. 排序优化：
   - 使用DESC降序索引优化ORDER BY DESC查询
   - 避免MySQL的filesort操作

3. 复合索引顺序：
   - 选择性高的字段在前（如user_id, receiver_id）
   - 范围查询字段在后（如create_time）
   - 排序字段最后

4. 全文检索：
   - 使用ngram解析器支持中文分词
   - 替代LIKE '%keyword%'的全表扫描

5. 功能索引分离：
   - 不同查询场景使用专门的索引
   - 避免索引冲突和选择困难

6. MySQL 8.0特性：
   - 降序索引：CREATE INDEX (col DESC)
   - 函数索引：CREATE INDEX ((LOWER(col)))
   - 不可见索引：ALTER INDEX INVISIBLE
   - 多值索引：支持JSON数组索引

建议监控索引使用情况：
- 使用 EXPLAIN 分析查询计划
- 监控 sys.schema_unused_indexes 视图
- 定期检查索引碎片化程度
*/