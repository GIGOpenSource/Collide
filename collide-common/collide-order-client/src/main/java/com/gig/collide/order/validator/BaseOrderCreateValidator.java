package com.gig.collide.order.validator;

import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.order.OrderException;

/**
 * 订单校验
 *
 * @author GIG
 */
public abstract class BaseOrderCreateValidator implements OrderCreateValidator {

    protected OrderCreateValidator nextValidator;

    @Override
    public void setNext(OrderCreateValidator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public OrderCreateValidator getNext() {
        return nextValidator;
    }

    /**
     * 校验
     *
     * @param request
     * @throws Exception
     */
    @Override
    public void validate(OrderCreateRequest request) throws OrderException {
        doValidate(request);

        if (nextValidator != null) {
            nextValidator.validate(request);
        }
    }

    /**
     * 校验方法的具体实现
     *
     * @param request
     * @throws OrderException
     */
    protected abstract void doValidate(OrderCreateRequest request) throws OrderException;
}
