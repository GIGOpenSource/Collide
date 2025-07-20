package com.gig.collide.api.artist.constant;

/**
 * 博主审核结果枚举
 * @author GIG
 */
public enum ArtistReviewResult {

    /**
     * 审核通过
     */
    APPROVED("审核通过"),

    /**
     * 审核拒绝
     */
    REJECTED("审核拒绝"),

    /**
     * 需要补充材料
     */
    NEED_SUPPLEMENT("需要补充材料"),

    /**
     * 暂停审核
     */
    SUSPENDED("暂停审核"),

    /**
     * 重新审核
     */
    RE_REVIEW("重新审核");

    private final String desc;

    ArtistReviewResult(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 是否为通过结果
     */
    public boolean isApproved() {
        return this == APPROVED;
    }

    /**
     * 是否为拒绝结果
     */
    public boolean isRejected() {
        return this == REJECTED;
    }

    /**
     * 是否需要申请人进一步操作
     */
    public boolean needUserAction() {
        return this == NEED_SUPPLEMENT;
    }

    /**
     * 是否为最终结果
     */
    public boolean isFinalResult() {
        return this == APPROVED || this == REJECTED;
    }
} 