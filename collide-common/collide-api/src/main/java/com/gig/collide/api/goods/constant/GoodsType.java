package com.gig.collide.api.goods.constant;

public enum GoodsType {

    /**
     * 藏品
     **/
    COLLECTION("藏品"),
    /**
     * 盲盒
     */
    BLIND_BOX("盲盒"),
    /**
     * 金币
     */
    COIN("金币"),
    /**
     * 会员
     */
    PRO("会员");


    private String value;

    GoodsType(String value) {
        this.value = value;
    }
}
