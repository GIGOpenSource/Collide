package com.gig.collide.api.category.constant;

/**
 * 分类模块常量类
 * 定义分类相关的业务常量
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public final class CategoryConstant {

    private CategoryConstant() {
        // 工具类，不允许实例化
    }

    // ===================== 分类状态相关常量 =====================

    /** 激活状态 */
    public static final String CATEGORY_STATUS_ACTIVE = "ACTIVE";
    
    /** 禁用状态 */
    public static final String CATEGORY_STATUS_INACTIVE = "INACTIVE";

    // ===================== 分类层级相关常量 =====================

    /** 根分类层级 */
    public static final int ROOT_CATEGORY_LEVEL = 1;
    
    /** 二级分类层级 */
    public static final int SECOND_CATEGORY_LEVEL = 2;
    
    /** 三级分类层级 */
    public static final int THIRD_CATEGORY_LEVEL = 3;
    
    /** 最大分类层级 */
    public static final int MAX_CATEGORY_LEVEL = 5;
    
    /** 根分类父ID */
    public static final long ROOT_PARENT_ID = 0L;

    // ===================== 路径相关常量 =====================

    /** 根路径 */
    public static final String ROOT_PATH = "/";
    
    /** 路径分隔符 */
    public static final String PATH_SEPARATOR = "/";
    
    /** 路径最大长度 */
    public static final int MAX_PATH_LENGTH = 500;

    // ===================== 操作类型常量 =====================

    /** 创建操作 */
    public static final String OPERATION_CREATE = "CREATE";
    
    /** 更新操作 */
    public static final String OPERATION_UPDATE = "UPDATE";
    
    /** 删除操作 */
    public static final String OPERATION_DELETE = "DELETE";
    
    /** 移动操作 */
    public static final String OPERATION_MOVE = "MOVE";
    
    /** 排序操作 */
    public static final String OPERATION_SORT = "SORT";
    
    /** 启用操作 */
    public static final String OPERATION_ENABLE = "ENABLE";
    
    /** 禁用操作 */
    public static final String OPERATION_DISABLE = "DISABLE";

    // ===================== 排序相关常量 =====================

    /** 按排序顺序排序 */
    public static final String SORT_BY_SORT_ORDER = "sort_order";
    
    /** 按创建时间排序 */
    public static final String SORT_BY_CREATE_TIME = "create_time";
    
    /** 按更新时间排序 */
    public static final String SORT_BY_UPDATE_TIME = "update_time";
    
    /** 按分类名称排序 */
    public static final String SORT_BY_NAME = "name";
    
    /** 按内容数量排序 */
    public static final String SORT_BY_CONTENT_COUNT = "content_count";

    // ===================== 业务限制常量 =====================

    /** 分类名称最大长度 */
    public static final int MAX_NAME_LENGTH = 50;
    
    /** 分类描述最大长度 */
    public static final int MAX_DESCRIPTION_LENGTH = 500;
    
    /** 子分类最大数量 */
    public static final int MAX_CHILD_CATEGORIES = 50;
    
    /** 分类图标URL最大长度 */
    public static final int MAX_ICON_URL_LENGTH = 255;
    
    /** 分类封面URL最大长度 */
    public static final int MAX_COVER_URL_LENGTH = 255;
    
    /** 操作原因最大长度 */
    public static final int MAX_REASON_LENGTH = 200;

    // ===================== 默认值常量 =====================

    /** 默认页面大小 */
    public static final int DEFAULT_PAGE_SIZE = 20;
    
    /** 最大页面大小 */
    public static final int MAX_PAGE_SIZE = 100;
    
    /** 默认排序顺序 */
    public static final int DEFAULT_SORT_ORDER = 0;
    
    /** 默认内容数量 */
    public static final long DEFAULT_CONTENT_COUNT = 0L;
    
    /** 默认版本号 */
    public static final int DEFAULT_VERSION = 1;

    // ===================== 查询条件常量 =====================

    /** 查询所有状态 */
    public static final String QUERY_ALL_STATUS = "ALL";
    
    /** 仅查询激活状态 */
    public static final String QUERY_ACTIVE_ONLY = "ACTIVE_ONLY";
    
    /** 仅查询根分类 */
    public static final String QUERY_ROOT_ONLY = "ROOT_ONLY";
    
    /** 包含子分类 */
    public static final String QUERY_INCLUDE_CHILDREN = "INCLUDE_CHILDREN";

    // ===================== 缓存相关常量 =====================

    /** 分类缓存前缀 */
    public static final String CACHE_PREFIX_CATEGORY = "category:";
    
    /** 分类树缓存前缀 */
    public static final String CACHE_PREFIX_CATEGORY_TREE = "category:tree:";
    
    /** 分类路径缓存前缀 */
    public static final String CACHE_PREFIX_CATEGORY_PATH = "category:path:";
    
    /** 分类统计缓存前缀 */
    public static final String CACHE_PREFIX_CATEGORY_STATS = "category:stats:";
    
    /** 分类缓存时间（秒） */
    public static final int CACHE_EXPIRE_SECONDS = 3600;

    // ===================== 系统预设分类 =====================

    /** 系统创建者名称 */
    public static final String SYSTEM_CREATOR_NAME = "system";
    
    /** 默认分类图标 */
    public static final String DEFAULT_CATEGORY_ICON = "/images/category/default.png";

    // ===================== 验证相关常量 =====================

    /** 分类名称正则表达式（中文、英文、数字、常用符号） */
    public static final String CATEGORY_NAME_PATTERN = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_\\.\\(\\)]+$";
    
    /** 分类路径正则表达式 */
    public static final String CATEGORY_PATH_PATTERN = "^(/[\\u4e00-\\u9fa5a-zA-Z0-9\\s\\-_\\.\\(\\)]+)+$";

    // ===================== 错误消息常量 =====================

    /** 分类不存在错误消息 */
    public static final String ERROR_CATEGORY_NOT_FOUND = "分类不存在";
    
    /** 分类名称重复错误消息 */
    public static final String ERROR_CATEGORY_NAME_DUPLICATE = "同级分类名称不能重复";
    
    /** 分类层级超限错误消息 */
    public static final String ERROR_CATEGORY_LEVEL_EXCEEDED = "分类层级不能超过%d级";
    
    /** 分类包含子分类错误消息 */
    public static final String ERROR_CATEGORY_HAS_CHILDREN = "分类包含子分类，无法删除";
    
    /** 分类包含内容错误消息 */
    public static final String ERROR_CATEGORY_HAS_CONTENT = "分类包含内容，无法删除";
    
    /** 无权限操作错误消息 */
    public static final String ERROR_NO_PERMISSION = "无权限执行此操作";
} 