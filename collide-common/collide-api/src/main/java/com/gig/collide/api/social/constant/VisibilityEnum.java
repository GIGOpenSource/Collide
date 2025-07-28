package com.gig.collide.api.social.constant;

/**
 * 社交动态可见性枚举
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum VisibilityEnum {
    
    PUBLIC(0, "公开", "所有人可见"),
    FOLLOWERS_ONLY(1, "仅关注者", "仅关注者可见"),
    PRIVATE(2, "仅自己", "仅自己可见");
    
    private final int level;
    private final String code;
    private final String description;
    
    VisibilityEnum(int level, String code, String description) {
        this.level = level;
        this.code = code;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 是否为公开可见
     */
    public boolean isPublic() {
        return this == PUBLIC;
    }
    
    /**
     * 是否为私密可见
     */
    public boolean isPrivate() {
        return this == PRIVATE;
    }
    
    /**
     * 是否限制可见
     */
    public boolean isRestricted() {
        return this != PUBLIC;
    }
    
    /**
     * 是否允许搜索引擎收录
     */
    public boolean allowSearchEngine() {
        return this == PUBLIC;
    }
    
    /**
     * 是否可以在时间线显示
     */
    public boolean canShowInTimeline() {
        return this == PUBLIC || this == FOLLOWERS_ONLY;
    }
    
    /**
     * 是否可以被转发
     */
    public boolean canBeShared() {
        return this == PUBLIC;
    }
    
    /**
     * 获取可见性优先级（数值越大越私密）
     */
    public int getPrivacyPriority() {
        return level;
    }
    
    /**
     * 根据级别获取枚举
     */
    public static VisibilityEnum fromLevel(int level) {
        for (VisibilityEnum visibility : values()) {
            if (visibility.level == level) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("Unknown visibility level: " + level);
    }
    
    /**
     * 根据代码获取枚举
     */
    public static VisibilityEnum fromCode(String code) {
        for (VisibilityEnum visibility : values()) {
            if (visibility.code.equals(code)) {
                return visibility;
            }
        }
        throw new IllegalArgumentException("Unknown visibility code: " + code);
    }
} 