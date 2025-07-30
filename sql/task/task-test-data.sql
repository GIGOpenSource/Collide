-- ==========================================
-- Collide 每日任务系统 - 测试数据
-- 用于快速测试任务功能的示例数据
-- ==========================================

USE collide;

-- ==========================================
-- 清空现有数据（测试用）
-- ==========================================

-- 注意：生产环境请勿执行清空操作
-- DELETE FROM t_user_reward_record;
-- DELETE FROM t_user_task_record;
-- DELETE FROM t_user_coin_account;
-- DELETE FROM t_task_reward;
-- DELETE FROM t_task_template;

-- ==========================================
-- 补充任务模板数据
-- ==========================================

-- 更多每日任务
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('阅读文章', '阅读10篇文章', 'daily', 'content', 'read_content', 10, 6),
('收藏内容', '收藏3篇优质内容', 'daily', 'content', 'favorite', 3, 7),
('完善资料', '完善个人资料信息', 'daily', 'profile', 'update_profile', 1, 8);

-- 成就任务
INSERT INTO `t_task_template` (`task_name`, `task_desc`, `task_type`, `task_category`, `task_action`, `target_count`, `sort_order`) VALUES
('新手上路', '完成首次发布内容', 'achievement', 'content', 'first_publish', 1, 100),
('社交新星', '获得100个点赞', 'achievement', 'social', 'like_received', 100, 101),
('忠实用户', '连续登录30天', 'achievement', 'login', 'login_streak', 30, 102),
('内容达人', '发布50篇内容', 'achievement', 'content', 'total_publish', 50, 103);

-- ==========================================
-- 补充任务奖励配置
-- ==========================================

-- 新增每日任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
-- 阅读文章奖励
(9, 'coin', '金币', '阅读奖励', 8, NULL, 1),
(9, 'experience', '经验值', '知识获取经验', 3, NULL, 0),
-- 收藏内容奖励
(10, 'coin', '金币', '收藏奖励', 6, NULL, 1),
-- 完善资料奖励
(11, 'coin', '金币', '资料完善奖励', 25, NULL, 1),
(11, 'experience', '经验值', '资料完善经验', 10, NULL, 0);

-- 成就任务奖励
INSERT INTO `t_task_reward` (`task_id`, `reward_type`, `reward_name`, `reward_desc`, `reward_amount`, `reward_data`, `is_main_reward`) VALUES
-- 新手上路
(12, 'coin', '金币', '新手礼包', 50, NULL, 1),
(12, 'item', '新手大礼包', '包含多种道具', 1, JSON_OBJECT('item_id', 2001, 'item_type', 'gift_pack', 'contents', JSON_ARRAY('coin:100', 'experience:20', 'avatar_frame:1')), 0),
-- 社交新星
(13, 'coin', '金币', '社交新星奖励', 200, NULL, 1),
(13, 'item', '社交徽章', '社交达人专属徽章', 1, JSON_OBJECT('item_id', 3001, 'item_type', 'badge', 'rarity', 'rare'), 0),
-- 忠实用户
(14, 'coin', '金币', '忠实用户奖励', 500, NULL, 1),
(14, 'vip', 'VIP会员', 'VIP会员7天体验', 7, JSON_OBJECT('vip_type', 'premium', 'duration_days', 7), 1),
-- 内容达人
(15, 'coin', '金币', '内容达人奖励', 300, NULL, 1),
(15, 'item', '创作者工具包', '内容创作辅助工具', 1, JSON_OBJECT('item_id', 4001, 'item_type', 'tool_pack', 'tools', JSON_ARRAY('editor_theme', 'template_pack', 'image_filter')), 0);

-- ==========================================
-- 用户金币账户测试数据
-- ==========================================

-- 为测试用户创建金币账户
INSERT INTO `t_user_coin_account` (`user_id`, `total_coin`, `available_coin`, `total_earned`) VALUES
(1, 150, 150, 150),  -- admin用户
(2, 280, 250, 300),  -- user1用户（消费过50金币）
(3, 95, 95, 95),     -- user2用户
(4, 420, 380, 420);  -- blogger1用户

-- ==========================================
-- 用户任务记录测试数据
-- ==========================================

-- 用户1的今日任务记录
INSERT INTO `t_user_task_record` (`user_id`, `task_id`, `task_date`, `task_name`, `task_type`, `task_category`, `target_count`, `current_count`, `is_completed`, `is_rewarded`, `complete_time`, `reward_time`) VALUES
-- 今日已完成的任务
(2, 1, CURDATE(), '每日登录', 'daily', 'login', 1, 1, 1, 1, '2024-01-16 08:30:00', '2024-01-16 08:30:05'),
(2, 2, CURDATE(), '发布内容', 'daily', 'content', 1, 1, 1, 1, '2024-01-16 10:15:00', '2024-01-16 10:15:08'),
-- 进行中的任务
(2, 3, CURDATE(), '点赞互动', 'daily', 'social', 5, 3, 0, 0, NULL, NULL),
(2, 4, CURDATE(), '评论互动', 'daily', 'social', 3, 1, 0, 0, NULL, NULL),
-- 未开始的任务
(2, 5, CURDATE(), '分享内容', 'daily', 'social', 1, 0, 0, 0, NULL, NULL);

-- 用户2的任务记录
INSERT INTO `t_user_task_record` (`user_id`, `task_id`, `task_date`, `task_name`, `task_type`, `task_category`, `target_count`, `current_count`, `is_completed`, `is_rewarded`, `complete_time`) VALUES
-- 今日已完成
(3, 1, CURDATE(), '每日登录', 'daily', 'login', 1, 1, 1, 0, '2024-01-16 09:00:00'),
-- 进行中
(3, 3, CURDATE(), '点赞互动', 'daily', 'social', 5, 2, 0, 0, NULL),
(3, 9, CURDATE(), '阅读文章', 'daily', 'content', 10, 7, 0, 0, NULL);

-- 用户昨日任务记录（用于测试历史数据）
INSERT INTO `t_user_task_record` (`user_id`, `task_id`, `task_date`, `task_name`, `task_type`, `task_category`, `target_count`, `current_count`, `is_completed`, `is_rewarded`, `complete_time`, `reward_time`) VALUES
(2, 1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '每日登录', 'daily', 'login', 1, 1, 1, 1, '2024-01-15 09:00:00', '2024-01-15 09:00:03'),
(2, 2, DATE_SUB(CURDATE(), INTERVAL 1 DAY), '发布内容', 'daily', 'content', 1, 1, 1, 1, '2024-01-15 14:30:00', '2024-01-15 14:30:05');

-- ==========================================
-- 用户奖励记录测试数据
-- ==========================================

-- 用户1的奖励记录
INSERT INTO `t_user_reward_record` (`user_id`, `task_record_id`, `reward_source`, `reward_type`, `reward_name`, `reward_amount`, `status`, `grant_time`) VALUES
-- 今日已发放的奖励
(2, 1, 'task', 'coin', '金币', 10, 'success', '2024-01-16 08:30:05'),
(2, 2, 'task', 'coin', '金币', 20, 'success', '2024-01-16 10:15:08'),
(2, 2, 'task', 'experience', '经验值', 5, 'success', '2024-01-16 10:15:08'),
-- 昨日奖励
(2, 7, 'task', 'coin', '金币', 10, 'success', '2024-01-15 09:00:03'),
(2, 8, 'task', 'coin', '金币', 20, 'success', '2024-01-15 14:30:05'),
-- 待发放的奖励
(3, 3, 'task', 'coin', '金币', 10, 'pending', NULL);

-- 成就奖励记录（假设某用户完成了新手任务）
INSERT INTO `t_user_reward_record` (`user_id`, `task_record_id`, `reward_source`, `reward_type`, `reward_name`, `reward_amount`, `reward_data`, `status`, `grant_time`) VALUES
(2, 0, 'task', 'coin', '金币', 50, NULL, 'success', '2024-01-15 10:00:00'),
(2, 0, 'task', 'item', '新手大礼包', 1, JSON_OBJECT('item_id', 2001, 'item_type', 'gift_pack'), 'success', '2024-01-15 10:00:00');

-- ==========================================
-- 测试查询示例
-- ==========================================

-- 1. 查看用户今日任务完成情况
SELECT 
    t.task_name,
    t.task_desc,
    t.target_count,
    COALESCE(r.current_count, 0) as current_count,
    COALESCE(r.is_completed, 0) as is_completed,
    COALESCE(r.is_rewarded, 0) as is_rewarded,
    CASE 
        WHEN COALESCE(r.is_completed, 0) = 1 THEN '已完成'
        WHEN COALESCE(r.current_count, 0) > 0 THEN '进行中'
        ELSE '未开始'
    END as status,
    ROUND(COALESCE(r.current_count, 0) * 100.0 / t.target_count, 1) as progress_percent
FROM t_task_template t
LEFT JOIN t_user_task_record r ON t.id = r.task_id 
    AND r.user_id = 2 
    AND r.task_date = CURDATE()
WHERE t.task_type = 'daily' 
  AND t.is_active = 1
ORDER BY t.sort_order;

-- 2. 查看用户今日可领取的奖励
SELECT 
    r.task_name,
    rw.reward_type,
    rw.reward_name,
    rw.reward_amount
FROM t_user_task_record r
JOIN t_task_reward rw ON r.task_id = rw.task_id
WHERE r.user_id = 2 
  AND r.task_date = CURDATE()
  AND r.is_completed = 1 
  AND r.is_rewarded = 0
ORDER BY r.complete_time;

-- 3. 查看用户金币变动记录
SELECT 
    reward_name,
    reward_amount,
    status,
    grant_time,
    DATE_FORMAT(grant_time, '%Y-%m-%d %H:%i') as grant_time_formatted
FROM t_user_reward_record 
WHERE user_id = 2 
  AND reward_type = 'coin'
ORDER BY create_time DESC
LIMIT 10;

-- 4. 查看用户任务完成统计
SELECT 
    task_type,
    COUNT(*) as total_tasks,
    SUM(is_completed) as completed_tasks,
    SUM(is_rewarded) as rewarded_tasks,
    ROUND(SUM(is_completed) * 100.0 / COUNT(*), 1) as completion_rate
FROM t_user_task_record 
WHERE user_id = 2 
  AND task_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
GROUP BY task_type;

-- 5. 查看系统任务统计
SELECT 
    tt.task_name,
    tt.task_type,
    COUNT(utr.id) as total_attempts,
    SUM(utr.is_completed) as total_completions,
    ROUND(SUM(utr.is_completed) * 100.0 / COUNT(utr.id), 1) as completion_rate
FROM t_task_template tt
LEFT JOIN t_user_task_record utr ON tt.id = utr.task_id
WHERE tt.is_active = 1
GROUP BY tt.id, tt.task_name, tt.task_type
ORDER BY completion_rate DESC;

-- ==========================================
-- 性能测试查询
-- ==========================================

-- 测试任务列表查询性能
EXPLAIN SELECT 
    t.id as task_id,
    t.task_name,
    t.task_desc,
    t.target_count,
    COALESCE(r.current_count, 0) as current_count,
    COALESCE(r.is_completed, 0) as is_completed,
    COALESCE(r.is_rewarded, 0) as is_rewarded
FROM t_task_template t
LEFT JOIN t_user_task_record r ON t.id = r.task_id 
    AND r.user_id = 2 
    AND r.task_date = CURDATE()
WHERE t.task_type = 'daily' 
  AND t.is_active = 1
ORDER BY t.sort_order;

-- ==========================================
-- 数据清理脚本（可选）
-- ==========================================

-- 清理过期的任务记录（保留最近30天）
-- DELETE FROM t_user_task_record 
-- WHERE task_date < DATE_SUB(CURDATE(), INTERVAL 30 DAY)
--   AND task_type = 'daily';

-- 清理过期的奖励记录（保留最近90天）
-- DELETE FROM t_user_reward_record 
-- WHERE create_time < DATE_SUB(NOW(), INTERVAL 90 DAY)
--   AND status = 'success';

-- ==========================================
-- 模拟任务进度更新
-- ==========================================

-- 模拟用户完成点赞操作
-- CALL update_user_task_progress(2, 'like', 1);

-- 模拟用户完成评论操作
-- CALL update_user_task_progress(2, 'comment', 1);

-- 模拟用户阅读文章
-- CALL update_user_task_progress(3, 'read_content', 3);

-- ==========================================
-- 数据统计分析
-- ==========================================

-- 查看整体任务参与情况
SELECT 
    '每日任务' as task_category,
    COUNT(DISTINCT user_id) as active_users,
    COUNT(*) as total_attempts,
    SUM(is_completed) as total_completions,
    ROUND(AVG(current_count * 100.0 / target_count), 1) as avg_progress
FROM t_user_task_record 
WHERE task_type = 'daily' 
  AND task_date = CURDATE()

UNION ALL

SELECT 
    '周常任务' as task_category,
    COUNT(DISTINCT user_id) as active_users,
    COUNT(*) as total_attempts,
    SUM(is_completed) as total_completions,
    ROUND(AVG(current_count * 100.0 / target_count), 1) as avg_progress
FROM t_user_task_record 
WHERE task_type = 'weekly' 
  AND task_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);

-- 查看奖励发放情况
SELECT 
    reward_type,
    SUM(reward_amount) as total_distributed,
    COUNT(*) as distribution_count,
    COUNT(CASE WHEN status = 'success' THEN 1 END) as success_count,
    ROUND(COUNT(CASE WHEN status = 'success' THEN 1 END) * 100.0 / COUNT(*), 1) as success_rate
FROM t_user_reward_record 
WHERE DATE(create_time) = CURDATE()
GROUP BY reward_type;