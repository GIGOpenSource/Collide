package com.gig.collide.order.validator;

import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.order.OrderException;

/**
 * 订单校验
 *
 * @author GIG
 */
public interface OrderCreateValidator {
    /**
     * 设置下一个校验器
     *
     * @param nextValidator
     */
    public void setNext(OrderCreateValidator nextValidator);

    /**
     * 返回下一个校验器
     *
     * @return
     */
    public OrderCreateValidator getNext();

    /**
     * 校验
     *
     * @param request
     * @throws OrderException 订单异常
     */
    public void validate(OrderCreateRequest request) throws OrderException;
}
