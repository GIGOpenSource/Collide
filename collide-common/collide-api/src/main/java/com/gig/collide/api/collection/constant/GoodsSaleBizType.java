package com.gig.collide.api.collection.constant;

/**
 * 藏品交易类型
 *
 * @author GIG
 */
public enum GoodsSaleBizType {

    /**
     * 一级市场交易
     */
    PRIMARY_TRADE,

    /**
     * 二级市场交易
     *
     * ！！！ 不做二级市场 ！！！
     */
    @Deprecated
    SECONDARY_TRADE,

    /**
     * 盲盒交易
     */
    BLIND_BOX_TRADE,

    /**
     * 空投
     */
    AIR_DROP,

    /**
     * 转赠
     */
    TRANSFER;
}
