package com.gig.collide.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.order.domain.entity.Order;
import com.gig.collide.order.domain.service.OrderService;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单控制器 - 简洁版
 * 提供HTTP REST API接口
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理", description = "订单相关操作接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单，包含商品信息冗余存储")
    public Result<OrderResponse> createOrder(@Valid @RequestBody OrderCreateDTO createDTO) {
        log.info("REST API - 创建订单，用户ID: {}, 商品ID: {}", createDTO.getUserId(), createDTO.getGoodsId());
        
        try {
            // DTO转换为实体
            Order order = convertToEntity(createDTO);
            
            // 调用业务服务
            Order createdOrder = orderService.createOrder(order);
            
            // 实体转换为响应DTO
            OrderResponse response = convertToResponse(createdOrder);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("REST API - 创建订单失败，用户ID: {}, 商品ID: {}, 错误: {}", 
                     createDTO.getUserId(), createDTO.getGoodsId(), e.getMessage(), e);
            return Result.error("ORDER_CREATE_ERROR", "创建订单失败: " + e.getMessage());
        }
    }

    @PostMapping("/{orderId}/pay")
    @Operation(summary = "支付订单", description = "支付指定订单")
    public Result<OrderResponse> payOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Valid @RequestBody PayOrderDTO payDTO) {
        log.info("REST API - 支付订单，订单ID: {}, 支付方式: {}", orderId, payDTO.getPayMethod());
        
        try {
            // 调用业务服务
            Order paidOrder = orderService.payOrder(
                orderId, 
                payDTO.getPayMethod(), 
                payDTO.getPayAmount(),
                payDTO.getThirdPartyTradeNo()
            );
            
            // 实体转换为响应DTO
            OrderResponse response = convertToResponse(paidOrder);
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("REST API - 支付订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_PAY_ERROR", "支付订单失败: " + e.getMessage());
        }
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "取消订单", description = "取消指定订单")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Valid @RequestBody CancelOrderDTO cancelDTO) {
        log.info("REST API - 取消订单，订单ID: {}, 用户ID: {}", orderId, cancelDTO.getUserId());
        
        try {
            // 调用业务服务
            boolean success = orderService.cancelOrder(
                orderId, 
                cancelDTO.getUserId(), 
                cancelDTO.getCancelReason()
            );
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("ORDER_CANCEL_ERROR", "取消订单失败");
            }
            
        } catch (Exception e) {
            log.error("REST API - 取消订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_CANCEL_ERROR", "取消订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "查询订单详情", description = "根据订单ID查询订单详情")
    public Result<OrderResponse> getOrderById(
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        log.debug("REST API - 查询订单详情，订单ID: {}", orderId);
        
        try {
            Order order = orderService.getOrderById(orderId);
            
            if (order != null) {
                OrderResponse response = convertToResponse(order);
                return Result.success(response);
            } else {
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
        } catch (Exception e) {
            log.error("REST API - 查询订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/order-no/{orderNo}")
    @Operation(summary = "根据订单号查询", description = "根据订单号查询订单详情")
    public Result<OrderResponse> getOrderByOrderNo(
            @Parameter(description = "订单号") @PathVariable String orderNo) {
        log.debug("REST API - 查询订单详情，订单号: {}", orderNo);
        
        try {
            Order order = orderService.getOrderByOrderNo(orderNo);
            
            if (order != null) {
                OrderResponse response = convertToResponse(order);
                return Result.success(response);
            } else {
                return Result.error("ORDER_NOT_FOUND", "订单不存在");
            }
            
        } catch (Exception e) {
            log.error("REST API - 查询订单失败，订单号: {}, 错误: {}", orderNo, e.getMessage(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "分页查询订单", description = "根据条件分页查询订单列表")
    public Result<PageResponse<OrderResponse>> queryOrders(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status,
            @Parameter(description = "支付状态") @RequestParam(required = false) String payStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        log.debug("REST API - 分页查询订单，用户ID: {}, 状态: {}, 页码: {}", userId, status, pageNum);
        
        try {
            // 调用业务服务分页查询
            IPage<Order> orderPage = orderService.queryOrders(userId, status, payStatus, pageNum, pageSize);
            
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
            log.error("REST API - 分页查询订单失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return Result.error("ORDER_QUERY_ERROR", "查询订单失败: " + e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    @Operation(summary = "更新订单状态", description = "更新指定订单的状态")
    public Result<Void> updateOrderStatus(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "新状态") @RequestParam String status) {
        log.info("REST API - 更新订单状态，订单ID: {}, 新状态: {}", orderId, status);
        
        try {
            boolean success = orderService.updateOrderStatus(orderId, status);
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("ORDER_UPDATE_ERROR", "更新订单状态失败");
            }
            
        } catch (Exception e) {
            log.error("REST API - 更新订单状态失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_UPDATE_ERROR", "更新订单状态失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "删除订单", description = "逻辑删除指定订单")
    public Result<Void> deleteOrder(
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.info("REST API - 删除订单，订单ID: {}, 用户ID: {}", orderId, userId);
        
        try {
            boolean success = orderService.deleteOrder(orderId, userId);
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("ORDER_DELETE_ERROR", "删除订单失败");
            }
            
        } catch (Exception e) {
            log.error("REST API - 删除订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            return Result.error("ORDER_DELETE_ERROR", "删除订单失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/user/{userId}/count")
    @Operation(summary = "统计用户订单数量", description = "统计指定用户的订单数量")
    public Result<Long> countUserOrders(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "订单状态") @RequestParam(required = false) String status) {
        log.debug("REST API - 统计用户订单数量，用户ID: {}, 状态: {}", userId, status);
        
        try {
            Long count = orderService.countUserOrders(userId, status);
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("REST API - 统计用户订单数量失败，用户ID: {}, 错误: {}", userId, e.getMessage(), e);
            return Result.error("ORDER_STATS_ERROR", "统计订单数量失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/goods/{goodsId}/sales")
    @Operation(summary = "统计商品销量", description = "统计指定商品的销量")
    public Result<Long> countGoodsSales(
            @Parameter(description = "商品ID") @PathVariable Long goodsId) {
        log.debug("REST API - 统计商品销量，商品ID: {}", goodsId);
        
        try {
            Long sales = orderService.countGoodsSales(goodsId);
            return Result.success(sales);
            
        } catch (Exception e) {
            log.error("REST API - 统计商品销量失败，商品ID: {}, 错误: {}", goodsId, e.getMessage(), e);
            return Result.error("ORDER_STATS_ERROR", "统计商品销量失败: " + e.getMessage());
        }
    }

    // =================== 私有转换方法 ===================
    
    /**
     * 创建DTO转换为实体
     */
    private Order convertToEntity(OrderCreateDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setUserNickname(dto.getUserNickname());
        order.setGoodsId(dto.getGoodsId());
        order.setGoodsName(dto.getGoodsName());
        order.setGoodsPrice(dto.getGoodsPrice());
        order.setGoodsCover(dto.getGoodsCover());
        order.setQuantity(dto.getQuantity());
        order.setTotalAmount(dto.getTotalAmount());
        order.setDiscountAmount(dto.getDiscountAmount());
        order.setFinalAmount(dto.getFinalAmount());
        order.setPayMethod(dto.getPayMethod());
        return order;
    }
    
    /**
     * 实体转换为响应DTO
     */
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNo(order.getOrderNo());
        response.setUserId(order.getUserId());
        response.setUserNickname(order.getUserNickname());
        response.setGoodsId(order.getGoodsId());
        response.setGoodsName(order.getGoodsName());
        response.setGoodsPrice(order.getGoodsPrice());
        response.setGoodsCover(order.getGoodsCover());
        response.setQuantity(order.getQuantity());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setStatus(order.getStatus());
        response.setPayStatus(order.getPayStatus());
        response.setPayMethod(order.getPayMethod());
        response.setPayTime(order.getPayTime());
        response.setCreateTime(order.getCreateTime());
        response.setUpdateTime(order.getUpdateTime());
        return response;
    }

    // =================== 内部DTO类 ===================
    
    /**
     * 创建订单DTO
     */
    public static class OrderCreateDTO {
        private Long userId;
        private String userNickname;
        private Long goodsId;
        private String goodsName;
        private BigDecimal goodsPrice;
        private String goodsCover;
        private Integer quantity;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String payMethod;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
        public Long getGoodsId() { return goodsId; }
        public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
        public String getGoodsName() { return goodsName; }
        public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
        public BigDecimal getGoodsPrice() { return goodsPrice; }
        public void setGoodsPrice(BigDecimal goodsPrice) { this.goodsPrice = goodsPrice; }
        public String getGoodsCover() { return goodsCover; }
        public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getFinalAmount() { return finalAmount; }
        public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
        public String getPayMethod() { return payMethod; }
        public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
    }
    
    /**
     * 支付订单DTO
     */
    public static class PayOrderDTO {
        private String payMethod;
        private BigDecimal payAmount;
        private String thirdPartyTradeNo;
        
        // Getters and Setters
        public String getPayMethod() { return payMethod; }
        public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
        public BigDecimal getPayAmount() { return payAmount; }
        public void setPayAmount(BigDecimal payAmount) { this.payAmount = payAmount; }
        public String getThirdPartyTradeNo() { return thirdPartyTradeNo; }
        public void setThirdPartyTradeNo(String thirdPartyTradeNo) { this.thirdPartyTradeNo = thirdPartyTradeNo; }
    }
    
    /**
     * 取消订单DTO
     */
    public static class CancelOrderDTO {
        private Long userId;
        private String cancelReason;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getCancelReason() { return cancelReason; }
        public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    }
    
    /**
     * 订单响应DTO
     */
    public static class OrderResponse {
        private Long id;
        private String orderNo;
        private Long userId;
        private String userNickname;
        private Long goodsId;
        private String goodsName;
        private BigDecimal goodsPrice;
        private String goodsCover;
        private Integer quantity;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String status;
        private String payStatus;
        private String payMethod;
        private java.time.LocalDateTime payTime;
        private java.time.LocalDateTime createTime;
        private java.time.LocalDateTime updateTime;
        
        // Getters and Setters (省略具体实现以节省空间)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOrderNo() { return orderNo; }
        public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserNickname() { return userNickname; }
        public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
        public Long getGoodsId() { return goodsId; }
        public void setGoodsId(Long goodsId) { this.goodsId = goodsId; }
        public String getGoodsName() { return goodsName; }
        public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
        public BigDecimal getGoodsPrice() { return goodsPrice; }
        public void setGoodsPrice(BigDecimal goodsPrice) { this.goodsPrice = goodsPrice; }
        public String getGoodsCover() { return goodsCover; }
        public void setGoodsCover(String goodsCover) { this.goodsCover = goodsCover; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getFinalAmount() { return finalAmount; }
        public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getPayStatus() { return payStatus; }
        public void setPayStatus(String payStatus) { this.payStatus = payStatus; }
        public String getPayMethod() { return payMethod; }
        public void setPayMethod(String payMethod) { this.payMethod = payMethod; }
        public java.time.LocalDateTime getPayTime() { return payTime; }
        public void setPayTime(java.time.LocalDateTime payTime) { this.payTime = payTime; }
        public java.time.LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(java.time.LocalDateTime createTime) { this.createTime = createTime; }
        public java.time.LocalDateTime getUpdateTime() { return updateTime; }
        public void setUpdateTime(java.time.LocalDateTime updateTime) { this.updateTime = updateTime; }
    }
}