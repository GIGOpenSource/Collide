-- 标签系统初始化数据

-- 一级系统标签
INSERT INTO `tag` 
(`gmt_create`, `gmt_modified`, `tag_name`, `description`, `tag_type`, `status`, `level`, `parent_tag_id`, `color`, `sort_order`, `creator_id`) 
VALUES 
(NOW(), NOW(), '内容', '内容相关标签', 'SYSTEM', 'ACTIVE', 1, NULL, '#1890FF', 1, 0),
(NOW(), NOW(), '技术', '技术相关标签', 'SYSTEM', 'ACTIVE', 1, NULL, '#52C41A', 2, 0),
(NOW(), NOW(), '兴趣', '兴趣爱好标签', 'SYSTEM', 'ACTIVE', 1, NULL, '#FA8C16', 3, 0),
(NOW(), NOW(), '生活', '生活方式标签', 'SYSTEM', 'ACTIVE', 1, NULL, '#F5222D', 4, 0),
(NOW(), NOW(), '学习', '学习教育标签', 'SYSTEM', 'ACTIVE', 1, NULL, '#722ED1', 5, 0),
(NOW(), NOW(), '商业', '商业相关标签', 'SYSTEM', 'ACTIVE', 1, NULL, '#13C2C2', 6, 0);

-- 二级系统标签（内容分类）
INSERT INTO `tag` 
(`gmt_create`, `gmt_modified`, `tag_name`, `description`, `tag_type`, `status`, `level`, `parent_tag_id`, `parent_tag_path`, `color`, `sort_order`, `creator_id`) 
VALUES 
(NOW(), NOW(), '文章', '文章内容', 'SYSTEM', 'ACTIVE', 2, 1, '/1', '#1890FF', 1, 0),
(NOW(), NOW(), '视频', '视频内容', 'SYSTEM', 'ACTIVE', 2, 1, '/1', '#1890FF', 2, 0),
(NOW(), NOW(), '图片', '图片内容', 'SYSTEM', 'ACTIVE', 2, 1, '/1', '#1890FF', 3, 0),
(NOW(), NOW(), '音频', '音频内容', 'SYSTEM', 'ACTIVE', 2, 1, '/1', '#1890FF', 4, 0),

-- 二级系统标签（技术分类）
(NOW(), NOW(), '前端', '前端技术', 'SYSTEM', 'ACTIVE', 2, 2, '/2', '#52C41A', 1, 0),
(NOW(), NOW(), '后端', '后端技术', 'SYSTEM', 'ACTIVE', 2, 2, '/2', '#52C41A', 2, 0),
(NOW(), NOW(), '移动端', '移动端技术', 'SYSTEM', 'ACTIVE', 2, 2, '/2', '#52C41A', 3, 0),
(NOW(), NOW(), '数据库', '数据库技术', 'SYSTEM', 'ACTIVE', 2, 2, '/2', '#52C41A', 4, 0),
(NOW(), NOW(), '人工智能', 'AI人工智能', 'SYSTEM', 'ACTIVE', 2, 2, '/2', '#52C41A', 5, 0),

-- 二级系统标签（兴趣分类）
(NOW(), NOW(), '音乐', '音乐爱好', 'SYSTEM', 'ACTIVE', 2, 3, '/3', '#FA8C16', 1, 0),
(NOW(), NOW(), '电影', '电影爱好', 'SYSTEM', 'ACTIVE', 2, 3, '/3', '#FA8C16', 2, 0),
(NOW(), NOW(), '运动', '体育运动', 'SYSTEM', 'ACTIVE', 2, 3, '/3', '#FA8C16', 3, 0),
(NOW(), NOW(), '旅行', '旅行爱好', 'SYSTEM', 'ACTIVE', 2, 3, '/3', '#FA8C16', 4, 0),
(NOW(), NOW(), '美食', '美食爱好', 'SYSTEM', 'ACTIVE', 2, 3, '/3', '#FA8C16', 5, 0);

-- 三级系统标签（技术细分）
INSERT INTO `tag` 
(`gmt_create`, `gmt_modified`, `tag_name`, `description`, `tag_type`, `status`, `level`, `parent_tag_id`, `parent_tag_path`, `color`, `sort_order`, `creator_id`) 
VALUES 
-- 前端技术细分
(NOW(), NOW(), 'JavaScript', 'JavaScript技术', 'SYSTEM', 'ACTIVE', 3, 5, '/2/5', '#52C41A', 1, 0),
(NOW(), NOW(), 'React', 'React框架', 'SYSTEM', 'ACTIVE', 3, 5, '/2/5', '#52C41A', 2, 0),
(NOW(), NOW(), 'Vue', 'Vue框架', 'SYSTEM', 'ACTIVE', 3, 5, '/2/5', '#52C41A', 3, 0),
(NOW(), NOW(), 'TypeScript', 'TypeScript技术', 'SYSTEM', 'ACTIVE', 3, 5, '/2/5', '#52C41A', 4, 0),

-- 后端技术细分
(NOW(), NOW(), 'Java', 'Java技术', 'SYSTEM', 'ACTIVE', 3, 6, '/2/6', '#52C41A', 1, 0),
(NOW(), NOW(), 'Python', 'Python技术', 'SYSTEM', 'ACTIVE', 3, 6, '/2/6', '#52C41A', 2, 0),
(NOW(), NOW(), 'Go', 'Go语言', 'SYSTEM', 'ACTIVE', 3, 6, '/2/6', '#52C41A', 3, 0),
(NOW(), NOW(), 'Node.js', 'Node.js技术', 'SYSTEM', 'ACTIVE', 3, 6, '/2/6', '#52C41A', 4, 0),

-- 数据库细分
(NOW(), NOW(), 'MySQL', 'MySQL数据库', 'SYSTEM', 'ACTIVE', 3, 8, '/2/8', '#52C41A', 1, 0),
(NOW(), NOW(), 'Redis', 'Redis缓存', 'SYSTEM', 'ACTIVE', 3, 8, '/2/8', '#52C41A', 2, 0),
(NOW(), NOW(), 'MongoDB', 'MongoDB数据库', 'SYSTEM', 'ACTIVE', 3, 8, '/2/8', '#52C41A', 3, 0),
(NOW(), NOW(), 'PostgreSQL', 'PostgreSQL数据库', 'SYSTEM', 'ACTIVE', 3, 8, '/2/8', '#52C41A', 4, 0); 