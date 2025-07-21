package com.gig.collide.chain.domain.entity;

import com.gig.collide.api.chain.constant.ChainOperateBizTypeEnum;
import com.gig.collide.api.chain.constant.ChainOperateTypeEnum;
import com.gig.collide.chain.domain.constant.ChainOperateStateEnum;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 链操作
 * </p>
 *
 * @author GIG
 * @since 2025-07-19
 */
@Getter
@Setter
public class ChainOperateInfo extends BaseEntity {

    /**
     * 链类型
     */
    private String chainType;

    /**
     * 业务id
     */
    private String bizId;

    /**
     * 业务类型
     *
     * @see ChainOperateBizTypeEnum
     */
    private String bizType;

    /**
     * 操作类型
     * @see ChainOperateTypeEnum
     */
    private String operateType;

    /**
     * 状态
     */
    private ChainOperateStateEnum state;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 成功时间
     */
    private Date succeedTime;

    /**
     * 入参
     */
    private String param;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 外部业务id
     */
    private String outBizId;
}
