package com.gig.collide.users.domain.entity;

import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class UserOperateStream extends BaseEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 操作参数
     */
    private String param;

    /**
     * 扩展字段
     */
    private String extendInfo;

}
