package com.gig.collide.api.artist.constant;

/**
 * 博主申请类型枚举
 * @author GIG
 */
public enum ArtistApplicationType {

    /**
     * 个人博主申请
     */
    PERSONAL("个人博主"),

    /**
     * 机构博主申请
     */
    ORGANIZATION("机构博主"),

    /**
     * 品牌博主申请
     */
    BRAND("品牌博主"),

    /**
     * 媒体博主申请
     */
    MEDIA("媒体博主"),

    /**
     * KOL申请
     */
    KOL("KOL"),

    /**
     * 专业领域博主
     */
    PROFESSIONAL("专业博主");

    private final String desc;

    ArtistApplicationType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为个人类型
     */
    public boolean isPersonal() {
        return this == PERSONAL || this == KOL || this == PROFESSIONAL;
    }

    /**
     * 是否为机构类型
     */
    public boolean isOrganization() {
        return this == ORGANIZATION || this == BRAND || this == MEDIA;
    }

    /**
     * 获取审核要求等级（1-5，5为最严格）
     */
    public int getReviewLevel() {
        switch (this) {
            case PERSONAL:
                return 2;
            case PROFESSIONAL:
                return 3;
            case KOL:
                return 3;
            case ORGANIZATION:
                return 4;
            case BRAND:
                return 4;
            case MEDIA:
                return 5;
            default:
                return 2;
        }
    }
} 