-- ==========================================
-- Collide 私信留言板模块 - 测试数据
-- 用于快速测试私信功能的示例数据
-- ==========================================

USE collide;

-- ==========================================
-- 清空现有数据（测试用）
-- ==========================================

-- 注意：生产环境请勿执行清空操作
-- DELETE FROM t_message_setting;
-- DELETE FROM t_message_session;
-- DELETE FROM t_message;

-- ==========================================
-- 测试用户数据（假设用户表已有数据）
-- ==========================================

-- 假设存在以下测试用户：
-- ID=1: admin (管理员)
-- ID=2: user1 (普通用户1)
-- ID=3: user2 (普通用户2)
-- ID=4: blogger1 (博主1)

-- ==========================================
-- 私信消息测试数据
-- ==========================================

-- 用户间私信对话
INSERT INTO `t_message` (`sender_id`, `receiver_id`, `content`, `message_type`, `status`, `read_time`) VALUES
-- admin 与 user1 的对话
(1, 2, '你好！欢迎来到Collide平台！', 'text', 'read', '2024-01-15 10:30:00'),
(2, 1, '谢谢！这个平台很棒！', 'text', 'read', '2024-01-15 10:32:00'),
(1, 2, '有任何问题都可以联系我哦~', 'text', 'read', '2024-01-15 10:35:00'),

-- user1 与 user2 的对话
(2, 3, '嗨，你也在用这个平台吗？', 'text', 'delivered', NULL),
(3, 2, '是的！感觉很不错呢', 'text', 'read', '2024-01-15 14:20:00'),
(2, 3, '我们可以互相关注一下', 'text', 'sent', NULL),

-- blogger1 收到的粉丝留言（留言板功能）
(2, 4, '你的文章写得真好！学到了很多东西', 'text', 'read', '2024-01-15 16:10:00'),
(3, 4, '请问什么时候更新下一篇文章呢？', 'text', 'read', '2024-01-15 17:30:00'),
(1, 4, '【置顶消息】欢迎大家在我的留言板留言交流！', 'text', 'read', '2024-01-15 09:00:00'),

-- 包含图片的消息
(2, 3, '分享一张照片给你看！', 'image', 'delivered', NULL),

-- 系统消息
(0, 2, '您的账户已通过实名认证', 'system', 'read', '2024-01-15 08:00:00'),
(0, 3, '欢迎新用户！完善个人资料可获得积分奖励', 'system', 'sent', NULL);

-- 更新图片消息的扩展数据
UPDATE `t_message` 
SET `extra_data` = JSON_OBJECT('image_url', 'https://example.com/images/photo1.jpg', 'thumbnail', 'https://example.com/images/photo1_thumb.jpg')
WHERE `content` = '分享一张照片给你看！';

-- 设置置顶留言
UPDATE `t_message` 
SET `is_pinned` = 1 
WHERE `content` = '【置顶消息】欢迎大家在我的留言板留言交流！';

-- ==========================================
-- 会话统计测试数据
-- ==========================================

INSERT INTO `t_message_session` (`user_id`, `other_user_id`, `last_message_id`, `last_message_time`, `unread_count`) VALUES
-- admin 的会话
(1, 2, 3, '2024-01-15 10:35:00', 0),
(1, 4, 6, '2024-01-15 09:00:00', 0),

-- user1 的会话
(2, 1, 3, '2024-01-15 10:35:00', 0),
(2, 3, 6, '2024-01-15 14:20:00', 1),
(2, 4, 4, '2024-01-15 16:10:00', 0),

-- user2 的会话
(3, 2, 6, '2024-01-15 14:20:00', 0),
(3, 4, 5, '2024-01-15 17:30:00', 0),

-- blogger1 的会话
(4, 1, 6, '2024-01-15 09:00:00', 0),
(4, 2, 4, '2024-01-15 16:10:00', 0),
(4, 3, 5, '2024-01-15 17:30:00', 0);

-- ==========================================
-- 消息设置测试数据
-- ==========================================

INSERT INTO `t_message_setting` (`user_id`, `allow_stranger_msg`, `auto_read_receipt`, `message_notification`) VALUES
(1, 1, 1, 1),  -- admin: 全部开启
(2, 1, 1, 0),  -- user1: 不接收通知
(3, 0, 1, 1),  -- user2: 不允许陌生人发消息
(4, 1, 0, 1);  -- blogger1: 不自动已读回执

-- ==========================================
-- 测试查询示例
-- ==========================================

-- 1. 查看 user1 (ID=2) 的会话列表
SELECT 
    ms.other_user_id,
    ms.last_message_time,
    ms.unread_count,
    m.content as last_message
FROM t_message_session ms
LEFT JOIN t_message m ON ms.last_message_id = m.id
WHERE ms.user_id = 2
ORDER BY ms.last_message_time DESC;

-- 2. 查看 user1 和 user2 之间的对话
SELECT 
    sender_id,
    receiver_id,
    content,
    message_type,
    status,
    create_time
FROM t_message 
WHERE (sender_id = 2 AND receiver_id = 3) 
   OR (sender_id = 3 AND receiver_id = 2)
ORDER BY create_time ASC;

-- 3. 查看 blogger1 (ID=4) 的留言板（置顶+最新）
SELECT 
    sender_id,
    content,
    is_pinned,
    create_time
FROM t_message 
WHERE receiver_id = 4
ORDER BY is_pinned DESC, create_time DESC;

-- 4. 查看 user2 (ID=3) 的未读消息
SELECT 
    sender_id,
    content,
    message_type,
    create_time
FROM t_message 
WHERE receiver_id = 3 AND status != 'read'
ORDER BY create_time DESC;

-- 5. 查看 user1 (ID=2) 的消息设置
SELECT 
    allow_stranger_msg,
    auto_read_receipt,
    message_notification
FROM t_message_setting 
WHERE user_id = 2;

-- ==========================================
-- 性能测试查询
-- ==========================================

-- 测试会话列表查询性能
EXPLAIN SELECT DISTINCT 
    CASE 
        WHEN sender_id = 2 THEN receiver_id 
        ELSE sender_id 
    END AS other_user_id,
    MAX(create_time) AS last_time,
    COUNT(CASE WHEN receiver_id = 2 AND status != 'read' THEN 1 END) AS unread_count
FROM t_message 
WHERE sender_id = 2 OR receiver_id = 2
GROUP BY other_user_id
ORDER BY last_time DESC;

-- ==========================================
-- 数据统计查询
-- ==========================================

-- 查看数据库中的消息统计
SELECT 
    message_type,
    status,
    COUNT(*) as count
FROM t_message 
GROUP BY message_type, status;

-- 查看用户消息数量统计
SELECT 
    CASE 
        WHEN sender_id = 0 THEN '系统消息'
        ELSE CONCAT('用户', sender_id)
    END as sender,
    COUNT(*) as message_count
FROM t_message 
GROUP BY sender_id
ORDER BY message_count DESC;