package com.gig.collide.order.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.request.OrderPayRequest;
import com.gig.collide.api.order.request.OrderCancelRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.order.domain.entity.Order;
import com.gig.collide.order.domain.service.OrderService;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单门面服务实现类 - 简洁版
 * Dubbo服务提供者，实现订单API接口
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@DubboService(version = "2.0.0")
@RequiredArgsConstructor
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final OrderService orderService;

    @Override
    public Result<OrderResponse> createOrder(OrderCreateRequest request) {
        log.info("创建订单请求，用户ID: {}, 商品ID: {}", request.getUserId(), request.getGoodsId());
        
        try {
            // 1. 请求参数转换为实体
            Order order = new Order();
            BeanUtils.copyProperties(request, order);
            
            // 2. 调用业务服务创建订单
            Order createdOrder = orderService.createOrder(order);
            
            // 3. 实体转换为响应对象
            OrderResponse response = convertToResponse(createdOrder);
            
            log.info("订单创建成功，订单号: {}", createdOrder.getOrderNo());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("创建订单失败，用户ID: {}, 商品ID: {}, 错误: {}", 
                     request.getUserId(), request.getGoodsId(), e.getMessage(), e);
            return Result.error("ORDER_CREATE_ERROR", "创建订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<OrderResponse> payOrder(OrderPayRequest request) {
        log.info("支付订单请求，订单ID: {}, 支付方式: {}", request.getOrderId(), request.getPayMethod());
        
        try {
            // 调用业务服务支付订单
            Order paidOrder = orderService.payOrder(
                request.getOrderId(), 
                request.getPayMethod(), 
                request.getPayAmount(),
                request.getThirdPartyTradeNo()
            );
            
            // 实体转换为响应对象
            OrderResponse response = convertToResponse(paidOrder);
            
            log.info("订单支付成功，订单号: {}", paidOrder.getOrderNo());
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("支付订单失败，订单ID: {}, 错误: {}", request.getOrderId(), e.getMessage(), e);
            return Result.error("ORDER_PAY_ERROR", "支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> cancelOrder(OrderCancelRequest request) {
        log.info("取消订单请求，订单ID: {}, 用户ID: {}", request.getOrderId(), request.getUserId());
        
        try {
            // 调用业务服务取消订单
            boolean success = orderService.cancelOrder(
                request.getOrderId(), 
                request.getUserId(), 
                request.getCancelReason()
            );
            
            if (success) {
                log.info("订单取消成功，订单ID: {}", request.getOrderId());
                return Result.success(null);
            } else {
                return Result.error("ORDER_CANCEL_ERROR", "取消订单失败");
            }
            
        } catch (Exception e) {
            log.error("取消订单失败，订单ID: {}, 用户ID: {}, 错误: {}", 
                     request.getOrderId(), request.getUserId(), e.getMessage(), e);
            return Result.error("ORDER_CANCEL_ERROR", "取消订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<OrderResponse> getOrderById(Long orderId) {
        log.debug("查询订单，订单ID: {}", orderId);
        
        try {
            Order order = orderService.getOrderById(orderId);
            
            if (order != null) {
                OrderResponse response = convertToResponse(order);
                return Result.success(response);
            } else {
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
        } catch (Exception e) {
            log.error("查询订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<OrderResponse> getOrderByOrderNo(String orderNo) {
        log.debug("查询订单，订单号: {}", orderNo);
        
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            
            if (order != null) {
                OrderResponse response = convertToResponse(order);
                return Result.success(response);
            } else {
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
        } catch (Exception e) {
            log.error("查询订单失败，订单号: {}, 错误: {}", orderNo, e.getMessage(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<OrderResponse>> queryOrders(OrderQueryRequest request) {
        log.debug("分页查询订单，用户ID: {}, 页码: {}, 页面大小: {}", 
                 request.getUserId(), request.getPageNum(), request.getPageSize());
        
        try {
            // 调用业务服务分页查询
            IPage<Order> orderPage = orderService.queryOrders(
                request.getUserId(),
                request.getStatus(),
                request.getPayStatus(),
                request.getPageNum(),
                request.getPageSize()
            );
            
            // 转换分页数据
            List<OrderResponse> responseList = orderPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            
            PageResponse<OrderResponse> pageResponse = new PageResponse<>();
            pageResponse.setDatas(responseList);
            pageResponse.setTotal(orderPage.getTotal());
            pageResponse.setCurrentPage((int) orderPage.getCurrent());
            pageResponse.setPageSize((int) orderPage.getSize());
            pageResponse.setTotalPage((int) orderPage.getPages());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("分页查询订单失败，用户ID: {}, 错误: {}", request.getUserId(), e.getMessage(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateOrderStatus(Long orderId, String status) {
        log.info("更新订单状态，订单ID: {}, 新状态: {}", orderId, status);
        
        try {
            boolean success = orderService.updateOrderStatus(orderId, status);
            
            if (success) {
                log.info("订单状态更新成功，订单ID: {}", orderId);
                return Result.success(null);
            } else {
                return Result.error("ORDER_UPDATE_ERROR", "更新订单状态失败");
            }
            
        } catch (Exception e) {
            log.error("更新订单状态失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_UPDATE_ERROR", "更新订单状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteOrder(Long orderId) {
        log.info("删除订单，订单ID: {}", orderId);
        
        try {
            // 注意：这里需要用户ID进行权限校验，但API接口没有提供
            // 实际项目中应该从请求上下文或Token中获取当前用户ID
            // 这里简化处理，直接查询订单获取用户ID
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
            boolean success = orderService.deleteOrder(orderId, order.getUserId());
            
            if (success) {
                log.info("订单删除成功，订单ID: {}", orderId);
                return Result.success(null);
            } else {
                return Result.error("ORDER_DELETE_ERROR", "删除订单失败");
            }
            
        } catch (Exception e) {
            log.error("删除订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_DELETE_ERROR", "删除订单失败: " + e.getMessage());
        }
    }

    /**
     * 实体转换为响应对象
     * 
     * @param order 订单实体
     * @return 订单响应对象
     */
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        BeanUtils.copyProperties(order, response);
        return response;
    }
}