-- ==========================================
-- 评论模块索引优化 - MySQL 8.4.1
-- 基于MySQL 8.4.1新特性进行索引优化
-- ==========================================

USE collide;

-- =================== 删除旧索引 ===================

-- 删除原有的简单索引
DROP INDEX IF EXISTS `idx_target_id` ON `t_comment`;
DROP INDEX IF EXISTS `idx_user_id` ON `t_comment`;
DROP INDEX IF EXISTS `idx_parent_comment_id` ON `t_comment`;
DROP INDEX IF EXISTS `idx_status` ON `t_comment`;

-- =================== 核心索引优化 ===================

-- 1. 目标对象评论查询索引（复合索引，支持状态过滤和时间排序）
CREATE INDEX `idx_target_status_time_desc` ON `t_comment` (`target_id`, `status`, `create_time` DESC);

-- 2. 用户评论查询索引（复合索引，支持状态过滤和时间排序）
CREATE INDEX `idx_user_status_time_desc` ON `t_comment` (`user_id`, `status`, `create_time` DESC);

-- 3. 父评论查询索引（复合索引，支持状态过滤和时间排序）
CREATE INDEX `idx_parent_status_time_desc` ON `t_comment` (`parent_comment_id`, `status`, `create_time` DESC);

-- 4. 回复用户查询索引（复合索引，支持状态过滤和时间排序）
CREATE INDEX `idx_reply_user_status_time_desc` ON `t_comment` (`reply_to_user_id`, `status`, `create_time` DESC);

-- =================== 统计查询索引 ===================

-- 5. 热门评论查询索引（复合索引，支持点赞数和回复数排序）
CREATE INDEX `idx_target_status_like_reply_desc` ON `t_comment` (`target_id`, `status`, `like_count` DESC, `reply_count` DESC);

-- 6. 用户活跃度索引（复合索引，支持用户评论统计）
CREATE INDEX `idx_user_type_status_time_desc` ON `t_comment` (`user_id`, `comment_type`, `status`, `create_time` DESC);

-- =================== 时间范围查询索引 ===================

-- 7. 时间范围查询索引（复合索引，支持时间范围查询）
CREATE INDEX `idx_create_time_status_type` ON `t_comment` (`create_time` DESC, `status`, `comment_type`);

-- 8. 更新时间索引（用于清理操作）
CREATE INDEX `idx_update_time_status` ON `t_comment` (`update_time` DESC, `status`);

-- =================== 全文搜索索引 ===================

-- 9. 评论内容全文索引（MySQL 8.4.1全文搜索优化）
CREATE FULLTEXT INDEX `idx_content_fulltext` ON `t_comment` (`content`) 
WITH PARSER ngram;

-- =================== 函数索引（MySQL 8.4.1新特性） ===================

-- 10. 评论类型函数索引（支持大小写不敏感查询）
CREATE INDEX `idx_comment_type_lower` ON `t_comment` ((LOWER(`comment_type`)));

-- 11. 状态函数索引（支持大小写不敏感查询）
CREATE INDEX `idx_status_lower` ON `t_comment` ((LOWER(`status`)));

-- =================== 前缀索引优化 ===================

-- 12. 用户昵称前缀索引（优化用户信息查询）
CREATE INDEX `idx_user_nickname_prefix` ON `t_comment` (`user_nickname`(20));

-- 13. 回复用户昵称前缀索引（优化回复信息查询）
CREATE INDEX `idx_reply_nickname_prefix` ON `t_comment` (`reply_to_user_nickname`(20));

-- =================== 部分索引（MySQL 8.4.1新特性） ===================

-- 14. 正常状态评论索引（只索引正常状态的评论）
CREATE INDEX `idx_target_normal_time_desc` ON `t_comment` (`target_id`, `create_time` DESC) 
WHERE `status` = 'NORMAL';

-- 15. 有回复的评论索引（只索引有回复的评论）
CREATE INDEX `idx_target_reply_count_desc` ON `t_comment` (`target_id`, `reply_count` DESC) 
WHERE `reply_count` > 0;

-- 16. 有点赞的评论索引（只索引有点赞的评论）
CREATE INDEX `idx_target_like_count_desc` ON `t_comment` (`target_id`, `like_count` DESC) 
WHERE `like_count` > 0;

-- =================== 不可见索引（MySQL 8.4.1新特性） ===================

-- 17. 测试索引（不可见，用于性能测试）
CREATE INDEX `idx_test_invisible` ON `t_comment` (`target_id`, `user_id`) INVISIBLE;

-- =================== 复合统计索引 ===================

-- 18. 评论热度综合索引（支持复杂的热度计算）
CREATE INDEX `idx_hot_score_desc` ON `t_comment` (
    `target_id`, 
    `status`, 
    `like_count` DESC, 
    `reply_count` DESC, 
    `create_time` DESC
) WHERE `status` = 'NORMAL' AND (`like_count` > 0 OR `reply_count` > 0);

-- =================== 时间分区索引 ===================

-- 19. 按月份分区的评论索引（适用于大数据量场景）
CREATE INDEX `idx_monthly_partition` ON `t_comment` (`create_time` DESC, `status`, `comment_type`)
PARTITION BY RANGE (YEAR(`create_time`) * 100 + MONTH(`create_time`)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- =================== 索引使用统计 ===================

-- 查看索引使用情况
SELECT 
    INDEX_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    CARDINALITY,
    SUB_PART,
    PACKED,
    NULLABLE,
    INDEX_TYPE,
    COMMENT
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = 'collide' 
AND TABLE_NAME = 't_comment'
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- =================== 索引优化建议 ===================

/*
MySQL 8.4.1 索引优化特性说明：

1. 降序索引 (DESC)
   - 支持 ORDER BY DESC 的优化
   - 减少排序操作，提升查询性能

2. 函数索引
   - 支持 LOWER() 等函数索引
   - 优化大小写不敏感查询

3. 部分索引 (WHERE 条件)
   - 只索引满足条件的行
   - 减少索引大小，提升维护性能

4. 不可见索引 (INVISIBLE)
   - 用于测试索引效果
   - 不影响查询性能，可随时启用

5. 全文索引优化
   - 使用 ngram 解析器
   - 支持中文和英文混合搜索

6. 前缀索引
   - 优化字符串字段查询
   - 减少索引存储空间

7. 复合索引优化
   - 考虑查询条件的顺序
   - 支持多字段排序优化

8. 时间分区索引
   - 适用于大数据量场景
   - 支持按时间范围快速查询
*/ 