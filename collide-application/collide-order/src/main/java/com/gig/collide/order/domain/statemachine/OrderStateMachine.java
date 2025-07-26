package com.gig.collide.order.domain.statemachine;

import com.gig.collide.order.domain.constant.OrderStatus;
import com.gig.collide.order.domain.constant.OrderEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态机
 * 管理订单状态的转换逻辑：CREATE -> UNPAID -> PAID
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public class OrderStateMachine {
    
    private static final Map<String, OrderStatus> TRANSITIONS = new HashMap<>();
    
    static {
        // CREATE -> UNPAID (确认订单后转为未付款状态)
        TRANSITIONS.put(buildKey(OrderStatus.CREATE, OrderEvent.CONFIRM), OrderStatus.UNPAID);
        
        // CREATE -> PAID (直接支付)
        TRANSITIONS.put(buildKey(OrderStatus.CREATE, OrderEvent.PAY), OrderStatus.PAID);
        
        // UNPAID -> PAID (未付款状态支付后转为已支付)
        TRANSITIONS.put(buildKey(OrderStatus.UNPAID, OrderEvent.PAY), OrderStatus.PAID);
        
        // 取消订单处理
        TRANSITIONS.put(buildKey(OrderStatus.CREATE, OrderEvent.CANCEL), OrderStatus.CREATE);
        TRANSITIONS.put(buildKey(OrderStatus.UNPAID, OrderEvent.CANCEL), OrderStatus.CREATE);
    }
    
    /**
     * 状态转换
     */
    public static OrderStatus transition(OrderStatus currentStatus, OrderEvent event) {
        String key = buildKey(currentStatus, event);
        OrderStatus nextStatus = TRANSITIONS.get(key);
        
        if (nextStatus == null) {
            throw new IllegalStateException(
                String.format("无效的状态转换: %s -> %s", currentStatus, event));
        }
        
        return nextStatus;
    }
    
    /**
     * 检查状态转换是否有效
     */
    public static boolean canTransition(OrderStatus currentStatus, OrderEvent event) {
        String key = buildKey(currentStatus, event);
        return TRANSITIONS.containsKey(key);
    }
    
    /**
     * 构建状态转换key
     */
    private static String buildKey(OrderStatus status, OrderEvent event) {
        return status.getCode() + "_" + event.getCode();
    }
} 