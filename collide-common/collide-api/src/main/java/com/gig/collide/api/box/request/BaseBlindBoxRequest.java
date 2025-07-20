package com.gig.collide.api.box.request;

import com.gig.collide.api.goods.constant.GoodsEvent;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 盲盒通用请求参数
 *
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseBlindBoxRequest extends BaseRequest {

    /**
     * 幂等号
     */
    @NotNull(message = "identifier is not null")
    private String identifier;

    /**
     * '盲盒id'
     */
    private Long blindBoxId;

    /**
     * 获取事件类型
     *
     * @return
     */
    public abstract GoodsEvent getEventType();
}
