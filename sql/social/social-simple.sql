-- =======================
-- Social模块核心数据库设计
-- =======================

-- 1. 用户关注关系表
CREATE TABLE t_user_follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    following_id BIGINT NOT NULL COMMENT '被关注者ID',
    follow_status TINYINT DEFAULT 1 COMMENT '关注状态:1-正常,2-单向屏蔽,0-取消关注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    INDEX idx_follower (follower_id),
    INDEX idx_following (following_id),
    INDEX idx_follow_time (create_time),
    
    CONSTRAINT chk_not_self_follow CHECK (follower_id != following_id)
);

-- 2. 内容分类表（三级结构）
CREATE TABLE t_social_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID(0为顶级)',
    level TINYINT NOT NULL COMMENT '分类级别:1-一级,2-二级,3-三级',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    category_path VARCHAR(200) COMMENT '分类路径: /1/2/3',
    category_icon VARCHAR(200) COMMENT '分类图标',
    category_description TEXT COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    content_count INT DEFAULT 0 COMMENT '内容数量',
    child_count INT DEFAULT 0 COMMENT '子分类数量',
    status TINYINT DEFAULT 1 COMMENT '状态:1-启用,0-禁用',
    own_tag_ids JSON COMMENT '分类自有标签ID列表',
    
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_category_code (category_code),
    INDEX idx_parent_level (parent_id, level),
    INDEX idx_path (category_path),
    INDEX idx_sort (sort_order),
    
    CONSTRAINT chk_level CHECK (level IN (1, 2, 3))
);

-- 3. 分类标签继承关系表
CREATE TABLE t_category_tag_inheritance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT NOT NULL COMMENT '分类ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    tag_source TINYINT NOT NULL COMMENT '标签来源:1-自有,2-从父级继承,3-从祖父级继承',
    source_category_id BIGINT COMMENT '标签来源分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_category_tag (category_id, tag_id),
    INDEX idx_category (category_id),
    INDEX idx_tag_source (tag_source)
);

-- 4. 社交内容表（整合content模块）
CREATE TABLE t_social_content (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    category_id BIGINT NOT NULL COMMENT '分类ID（三级分类）',
    category_path VARCHAR(200) COMMENT '分类路径',
    content_type TINYINT NOT NULL COMMENT '内容类型:1-短视频,2-长视频,3-图片,4-文字',
    title VARCHAR(200) COMMENT '标题',
    description TEXT COMMENT '描述/正文',
    media_urls JSON COMMENT '媒体地址',
    cover_url VARCHAR(500) COMMENT '封面图',
    duration INT DEFAULT 0 COMMENT '视频时长(秒)',
    media_info JSON COMMENT '媒体信息',
    
    -- 付费相关
    is_paid TINYINT DEFAULT 0 COMMENT '是否付费:0-免费,1-付费',
    price INT DEFAULT 0 COMMENT '价格(金币)',
    free_duration INT DEFAULT 0 COMMENT '免费试看时长(秒)',
    purchase_count INT DEFAULT 0 COMMENT '购买次数',
    
    -- 统计字段
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    share_count INT DEFAULT 0 COMMENT '分享数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    view_count INT DEFAULT 0 COMMENT '播放数',
    
    recommend_score DECIMAL(10,4) DEFAULT 0 COMMENT '推荐分数',
    quality_score DECIMAL(10,4) DEFAULT 0 COMMENT '质量分数',
    
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,2-审核中,0-已删除',
    privacy TINYINT DEFAULT 1 COMMENT '隐私:1-公开,2-仅关注者,3-私密',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_time (user_id, create_time),
    INDEX idx_category_path (category_path),
    INDEX idx_content_type (content_type),
    INDEX idx_paid (is_paid, price),
    INDEX idx_hot (like_count, view_count, create_time)
);

-- 5. 内容点赞表（整合like模块）
CREATE TABLE t_social_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    content_owner_id BIGINT NOT NULL COMMENT '内容作者ID',
    like_type TINYINT DEFAULT 1 COMMENT '点赞类型:1-普通点赞,2-超级点赞',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_content (user_id, content_id),
    INDEX idx_user (user_id),
    INDEX idx_content (content_id),
    INDEX idx_content_owner (content_owner_id)
);

-- 6. 内容评论表（整合comment模块）
CREATE TABLE t_social_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    content_owner_id BIGINT NOT NULL COMMENT '内容作者ID',
    parent_comment_id BIGINT DEFAULT 0 COMMENT '父评论ID(0为顶级评论)',
    reply_to_user_id BIGINT COMMENT '回复的用户ID',
    comment_text TEXT NOT NULL COMMENT '评论内容',
    comment_images JSON COMMENT '评论图片',
    like_count INT DEFAULT 0 COMMENT '评论点赞数',
    reply_count INT DEFAULT 0 COMMENT '回复数量',
    status TINYINT DEFAULT 1 COMMENT '状态:1-正常,0-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_content (content_id),
    INDEX idx_parent (parent_comment_id)
);

-- 7. 内容收藏表（整合favorite模块）
CREATE TABLE t_social_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    content_owner_id BIGINT NOT NULL COMMENT '内容作者ID',
    folder_id BIGINT DEFAULT 0 COMMENT '收藏夹ID(0为默认收藏夹)',
    folder_name VARCHAR(50) DEFAULT '默认收藏夹' COMMENT '收藏夹名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_content_folder (user_id, content_id, folder_id),
    INDEX idx_user (user_id),
    INDEX idx_content (content_id),
    INDEX idx_folder (user_id, folder_id)
);

-- 8. 内容分享表（新增share功能）
CREATE TABLE t_social_share (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '分享用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    content_owner_id BIGINT NOT NULL COMMENT '内容作者ID',
    share_type TINYINT NOT NULL COMMENT '分享类型:1-微信,2-QQ,3-微博,4-复制链接,5-系统内分享',
    share_platform VARCHAR(50) COMMENT '分享平台',
    share_comment VARCHAR(500) COMMENT '分享时的评论',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user (user_id),
    INDEX idx_content (content_id),
    INDEX idx_share_type (share_type)
);

-- 9. 内容标签关联表
CREATE TABLE t_social_content_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL COMMENT '标签ID（来自tag模块）',
    tag_source TINYINT DEFAULT 1 COMMENT '标签来源:1-分类自动,2-用户手动',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_content_tag (content_id, tag_id),
    INDEX idx_content (content_id),
    INDEX idx_tag (tag_id)
);

-- 10. 内容购买记录表
CREATE TABLE t_content_purchase (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '购买用户ID',
    content_id BIGINT NOT NULL COMMENT '内容ID',
    content_owner_id BIGINT NOT NULL COMMENT '内容作者ID',
    price INT NOT NULL COMMENT '购买价格(金币)',
    purchase_type TINYINT DEFAULT 1 COMMENT '购买类型:1-单次购买',
    expire_time DATETIME COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '状态:1-有效,0-已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_user_content (user_id, content_id),
    INDEX idx_user (user_id),
    INDEX idx_content (content_id)
);

-- 11. 用户社交统计表
CREATE TABLE t_user_social_stats (
    user_id BIGINT PRIMARY KEY,
    follower_count INT DEFAULT 0 COMMENT '粉丝数',
    following_count INT DEFAULT 0 COMMENT '关注数',
    content_count INT DEFAULT 0 COMMENT '内容数',
    total_likes_received INT DEFAULT 0 COMMENT '获得总点赞数',
    total_comments_received INT DEFAULT 0 COMMENT '获得总评论数',
    total_shares_received INT DEFAULT 0 COMMENT '获得总分享数',
    total_favorites_received INT DEFAULT 0 COMMENT '获得总收藏数',
    total_views INT DEFAULT 0 COMMENT '总播放数',
    social_score DECIMAL(10,4) DEFAULT 0 COMMENT '社交分数',
    
    video_count INT DEFAULT 0 COMMENT '视频数量',
    image_count INT DEFAULT 0 COMMENT '图片数量',
    text_count INT DEFAULT 0 COMMENT '文字内容数量',
    
    total_earnings INT DEFAULT 0 COMMENT '总收益(金币)',
    monthly_earnings INT DEFAULT 0 COMMENT '本月收益',
    content_sales INT DEFAULT 0 COMMENT '内容销售次数',
    
    last_active_time DATETIME COMMENT '最后活跃时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_follower_count (follower_count),
    INDEX idx_social_score (social_score)
);