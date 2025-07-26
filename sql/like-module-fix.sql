-- ================================================
-- 点赞模块数据库修复脚本
-- ================================================

-- 1. 创建点赞表（如果不存在）
CREATE TABLE IF NOT EXISTS `t_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `target_id` bigint(20) NOT NULL COMMENT '目标对象ID（内容ID、评论ID等）',
  `target_type` varchar(50) NOT NULL COMMENT '目标类型：CONTENT、COMMENT、SOCIAL_POST、USER',
  `action_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '操作类型：1-点赞，0-取消，-1-点踩',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `device_info` varchar(500) DEFAULT NULL COMMENT '设备信息',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标记：0-正常，1-删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_id_type` (`target_id`, `target_type`),
  KEY `idx_created_time` (`created_time`),
  KEY `idx_action_type` (`action_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一点赞表';

-- 2. 添加唯一约束（防重复点赞）
ALTER TABLE `t_like` ADD UNIQUE INDEX `uk_user_target` (`user_id`, `target_id`, `target_type`) COMMENT '用户-目标对象唯一约束';

-- 3. 为内容表添加点赞统计字段（如果不存在）
ALTER TABLE `t_content` 
ADD COLUMN IF NOT EXISTS `like_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '点赞数',
ADD COLUMN IF NOT EXISTS `dislike_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '点踩数';

-- 4. 为评论表添加点赞统计字段（如果不存在）
ALTER TABLE `t_comment` 
ADD COLUMN IF NOT EXISTS `like_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '点赞数',
ADD COLUMN IF NOT EXISTS `dislike_count` bigint(20) NOT NULL DEFAULT '0' COMMENT '点踩数';

-- 5. 创建点赞统计索引
ALTER TABLE `t_content` ADD INDEX IF NOT EXISTS `idx_like_count` (`like_count` DESC);
ALTER TABLE `t_content` ADD INDEX IF NOT EXISTS `idx_dislike_count` (`dislike_count` DESC);
ALTER TABLE `t_comment` ADD INDEX IF NOT EXISTS `idx_like_count` (`like_count` DESC);
ALTER TABLE `t_comment` ADD INDEX IF NOT EXISTS `idx_dislike_count` (`dislike_count` DESC);

-- 6. 数据修复：统计现有数据并更新冗余字段
-- 更新内容表的点赞统计
UPDATE t_content c 
SET like_count = (
    SELECT COUNT(*) 
    FROM t_like l 
    WHERE l.target_id = c.id 
      AND l.target_type = 'CONTENT' 
      AND l.action_type = 1 
      AND l.deleted = 0
),
dislike_count = (
    SELECT COUNT(*) 
    FROM t_like l 
    WHERE l.target_id = c.id 
      AND l.target_type = 'CONTENT' 
      AND l.action_type = -1 
      AND l.deleted = 0
);

-- 更新评论表的点赞统计
UPDATE t_comment c 
SET like_count = (
    SELECT COUNT(*) 
    FROM t_like l 
    WHERE l.target_id = c.id 
      AND l.target_type = 'COMMENT' 
      AND l.action_type = 1 
      AND l.deleted = 0
),
dislike_count = (
    SELECT COUNT(*) 
    FROM t_like l 
    WHERE l.target_id = c.id 
      AND l.target_type = 'COMMENT' 
      AND l.action_type = -1 
      AND l.deleted = 0
);

-- 7. 创建点赞数据一致性检查视图
CREATE OR REPLACE VIEW `v_like_consistency_check` AS
SELECT 
    'CONTENT' as target_type,
    c.id as target_id,
    c.like_count as cached_like_count,
    c.dislike_count as cached_dislike_count,
    COALESCE(l.actual_like_count, 0) as actual_like_count,
    COALESCE(l.actual_dislike_count, 0) as actual_dislike_count,
    (c.like_count != COALESCE(l.actual_like_count, 0)) as like_inconsistent,
    (c.dislike_count != COALESCE(l.actual_dislike_count, 0)) as dislike_inconsistent
FROM t_content c
LEFT JOIN (
    SELECT 
        target_id,
        SUM(CASE WHEN action_type = 1 THEN 1 ELSE 0 END) as actual_like_count,
        SUM(CASE WHEN action_type = -1 THEN 1 ELSE 0 END) as actual_dislike_count
    FROM t_like 
    WHERE target_type = 'CONTENT' AND deleted = 0
    GROUP BY target_id
) l ON c.id = l.target_id

UNION ALL

SELECT 
    'COMMENT' as target_type,
    c.id as target_id,
    c.like_count as cached_like_count,
    c.dislike_count as cached_dislike_count,
    COALESCE(l.actual_like_count, 0) as actual_like_count,
    COALESCE(l.actual_dislike_count, 0) as actual_dislike_count,
    (c.like_count != COALESCE(l.actual_like_count, 0)) as like_inconsistent,
    (c.dislike_count != COALESCE(l.actual_dislike_count, 0)) as dislike_inconsistent
FROM t_comment c
LEFT JOIN (
    SELECT 
        target_id,
        SUM(CASE WHEN action_type = 1 THEN 1 ELSE 0 END) as actual_like_count,
        SUM(CASE WHEN action_type = -1 THEN 1 ELSE 0 END) as actual_dislike_count
    FROM t_like 
    WHERE target_type = 'COMMENT' AND deleted = 0
    GROUP BY target_id
) l ON c.id = l.target_id;

-- 8. 查询数据不一致的记录
-- SELECT * FROM v_like_consistency_check WHERE like_inconsistent = 1 OR dislike_inconsistent = 1;

COMMIT; 