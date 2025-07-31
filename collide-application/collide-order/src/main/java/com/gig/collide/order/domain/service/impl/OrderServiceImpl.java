package com.gig.collide.order.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.order.domain.entity.Order;
import com.gig.collide.order.domain.service.OrderService;
import com.gig.collide.order.infrastructure.mapper.OrderMapper;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 订单业务服务实现类 - 简洁版
 * 基于order-simple.sql的单表设计，实现核心订单业务逻辑
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @DubboReference
    private UserFacadeService userFacadeService;
    private final Random random = new Random();

    @Override
    @Transactional
    public Order createOrder(Order order) {
        log.info("开始创建订单，用户ID: {}, 商品ID: {}", order.getUserId(), order.getGoodsId());
        
        try {
            // 1. 生成唯一订单号
            order.setOrderNo(generateOrderNo());
            
            // 2. 设置初始状态
            order.setStatus("pending");
            order.setPayStatus("unpaid");
            
            // 3. 金额校验
            validateOrderAmount(order);
            
            // 4. 设置默认值
            if (order.getDiscountAmount() == null) {
                order.setDiscountAmount(BigDecimal.ZERO);
            }
            
            // 5. 保存订单
            int result = orderMapper.insert(order);
            
            if (result > 0) {
                log.info("订单创建成功，订单号: {}, 订单ID: {}", order.getOrderNo(), order.getId());
                return order;
            } else {
                throw new RuntimeException("订单创建失败");
            }
            
        } catch (Exception e) {
            log.error("创建订单失败，用户ID: {}, 商品ID: {}, 错误: {}", 
                     order.getUserId(), order.getGoodsId(), e.getMessage(), e);
            throw new RuntimeException("创建订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Order payOrder(Long orderId, String payMethod, BigDecimal payAmount, String thirdPartyTradeNo) {
        log.info("开始支付订单，订单ID: {}, 支付方式: {}, 支付金额: {}", orderId, payMethod, payAmount);
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            // 2. 检查订单状态
            if (!order.canPay()) {
                throw new RuntimeException("订单状态不允许支付，当前状态: " + order.getStatus());
            }
            
            // 3. 校验支付金额
            if (payAmount.compareTo(order.getFinalAmount()) != 0) {
                throw new RuntimeException("支付金额与订单金额不符");
            }
            
            // 4. 更新支付信息
            int result = orderMapper.updatePayInfo(orderId, "paid", payMethod, LocalDateTime.now());
            
            if (result > 0) {
                // 重新查询更新后的订单
                order = orderMapper.selectById(orderId);
                log.info("订单支付成功，订单号: {}, 第三方交易号: {}", order.getOrderNo(), thirdPartyTradeNo);
                
                // 5. 🪙 金币类商品支付成功后自动增加钱包余额
                if (order.isCoinGoods()) {
                    try {
                        BigDecimal coinAmount = order.getTotalCoinAmount();
                        log.info("检测到金币类商品订单支付成功，开始增加用户钱包余额: userId={}, coinAmount={}, payAmount={}, orderNo={}", 
                                order.getUserId(), coinAmount, order.getFinalAmount(), order.getOrderNo());
                        
                        Result<Void> walletResult = userFacadeService.addWalletBalance(
                            order.getUserId(), 
                            coinAmount,  // 🔑 使用金币数量而不是支付金额
                            order.getOrderNo(), 
                            String.format("购买金币：%s (数量×%d=%.2f金币)", 
                                order.getGoodsName(), order.getQuantity(), coinAmount)
                        );
                        
                        if (walletResult.getSuccess()) {
                            log.info("✅ 金币充值成功: userId={}, coinAmount={}, orderNo={}", 
                                    order.getUserId(), coinAmount, order.getOrderNo());
                        } else {
                            log.error("❌ 金币充值失败: userId={}, coinAmount={}, orderNo={}, error={}", 
                                    order.getUserId(), coinAmount, order.getOrderNo(), walletResult.getMessage());
                            // 注意：这里不抛出异常，避免影响订单支付流程，但需要记录错误日志
                        }
                    } catch (Exception e) {
                        log.error("❌ 金币充值异常: userId={}, orderNo={}", 
                                order.getUserId(), order.getOrderNo(), e);
                        // 不影响主流程，但需要后续补偿处理
                    }
                }
                
                return order;
            } else {
                throw new RuntimeException("更新支付信息失败");
            }
            
        } catch (Exception e) {
            log.error("支付订单失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("支付订单失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean cancelOrder(Long orderId, Long userId, String cancelReason) {
        log.info("开始取消订单，订单ID: {}, 用户ID: {}, 取消原因: {}", orderId, userId, cancelReason);
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            // 2. 权限校验
            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("无权限取消此订单");
            }
            
            // 3. 检查订单状态
            if (!order.canCancel()) {
                throw new RuntimeException("订单状态不允许取消，当前状态: " + order.getStatus());
            }
            
            // 4. 更新订单状态
            int result = orderMapper.updateOrderStatus(orderId, "cancelled");
            
            if (result > 0) {
                log.info("订单取消成功，订单号: {}", order.getOrderNo());
                return true;
            } else {
                throw new RuntimeException("取消订单失败");
            }
            
        } catch (Exception e) {
            log.error("取消订单失败，订单ID: {}, 用户ID: {}, 错误: {}", orderId, userId, e.getMessage(), e);
            throw new RuntimeException("取消订单失败: " + e.getMessage());
        }
    }

    @Override
    public Order getOrderById(Long orderId) {
        log.debug("查询订单，订单ID: {}", orderId);
        return orderMapper.selectById(orderId);
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        log.debug("查询订单，订单号: {}", orderNo);
        return orderMapper.findByOrderNo(orderNo);
    }

    @Override
    public IPage<Order> queryOrders(Long userId, String status, String payStatus, 
                                  Integer pageNum, Integer pageSize) {
        log.debug("分页查询订单，用户ID: {}, 状态: {}, 支付状态: {}, 页码: {}, 页面大小: {}", 
                 userId, status, payStatus, pageNum, pageSize);
        
        Page<Order> page = new Page<>(pageNum, pageSize);
        return orderMapper.findUserOrders(page, userId, status, payStatus);
    }

    @Override
    public IPage<Order> getUserOrders(Long userId, String status, String payStatus, 
                                    Integer pageNum, Integer pageSize) {
        return queryOrders(userId, status, payStatus, pageNum, pageSize);
    }

    @Override
    public List<Order> getOrdersByGoodsId(Long goodsId, String status) {
        log.debug("查询商品订单，商品ID: {}, 状态: {}", goodsId, status);
        return orderMapper.findByGoodsId(goodsId, status);
    }

    @Override
    public List<Order> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status) {
        log.debug("查询时间范围订单，开始时间: {}, 结束时间: {}, 状态: {}", startTime, endTime, status);
        return orderMapper.findByTimeRange(startTime, endTime, status);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long orderId, String status) {
        log.info("更新订单状态，订单ID: {}, 新状态: {}", orderId, status);
        
        try {
            // 先获取订单信息，用于后续的钱包操作判断
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                log.error("订单不存在，订单ID: {}", orderId);
                return false;
            }
            
            int result = orderMapper.updateOrderStatus(orderId, status);
            
            if (result > 0) {
                log.info("订单状态更新成功，订单ID: {}", orderId);
                
                // 💰 处理退款时的钱包操作
                if ("refunded".equals(status) && order.isCoinGoods()) {
                    try {
                        BigDecimal coinAmount = order.getTotalCoinAmount();
                        log.info("检测到金币类商品订单退款，开始减少用户钱包余额: userId={}, coinAmount={}, refundAmount={}, orderNo={}", 
                                order.getUserId(), coinAmount, order.getFinalAmount(), order.getOrderNo());
                        
                        Result<Void> walletResult = userFacadeService.deductWalletBalance(
                            order.getUserId(), 
                            coinAmount,  // 🔑 使用金币数量而不是退款金额
                            order.getOrderNo(), 
                            String.format("退款扣减：%s (数量×%d=%.2f金币)", 
                                order.getGoodsName(), order.getQuantity(), coinAmount)
                        );
                        
                        if (walletResult.getSuccess()) {
                            log.info("✅ 金币退款扣减成功: userId={}, coinAmount={}, orderNo={}", 
                                    order.getUserId(), coinAmount, order.getOrderNo());
                        } else {
                            log.error("❌ 金币退款扣减失败: userId={}, coinAmount={}, orderNo={}, error={}", 
                                    order.getUserId(), coinAmount, order.getOrderNo(), walletResult.getMessage());
                            // 注意：这里不抛出异常，避免影响订单状态更新，但需要记录错误日志用于后续补偿
                        }
                    } catch (Exception e) {
                        log.error("❌ 金币退款扣减异常: userId={}, orderNo={}", 
                                order.getUserId(), order.getOrderNo(), e);
                        // 不影响主流程，但需要后续补偿处理
                    }
                }
                
                return true;
            } else {
                log.warn("订单状态更新失败，订单ID: {}", orderId);
                return false;
            }
            
        } catch (Exception e) {
            log.error("更新订单状态失败，订单ID: {}, 错误: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("更新订单状态失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteOrder(Long orderId, Long userId) {
        log.info("删除订单，订单ID: {}, 用户ID: {}", orderId, userId);
        
        try {
            // 1. 查询订单
            Order order = orderMapper.selectById(orderId);
            if (order == null) {
                throw new RuntimeException("订单不存在");
            }
            
            // 2. 权限校验
            if (!order.getUserId().equals(userId)) {
                throw new RuntimeException("无权限删除此订单");
            }
            
            // 3. 逻辑删除（更新状态为deleted）
            int result = orderMapper.updateOrderStatus(orderId, "deleted");
            
            if (result > 0) {
                log.info("订单删除成功，订单号: {}", order.getOrderNo());
                return true;
            } else {
                throw new RuntimeException("删除订单失败");
            }
            
        } catch (Exception e) {
            log.error("删除订单失败，订单ID: {}, 用户ID: {}, 错误: {}", orderId, userId, e.getMessage(), e);
            throw new RuntimeException("删除订单失败: " + e.getMessage());
        }
    }

    @Override
    public Long countUserOrders(Long userId, String status) {
        log.debug("统计用户订单数量，用户ID: {}, 状态: {}", userId, status);
        return orderMapper.countUserOrders(userId, status);
    }

    @Override
    public Long countGoodsSales(Long goodsId) {
        log.debug("统计商品销量，商品ID: {}", goodsId);
        return orderMapper.countGoodsSales(goodsId, "paid");
    }

    @Override
    public String generateOrderNo() {
        // 格式：ORDER + 时间戳(yyyyMMddHHmmss) + 4位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomNum = 1000 + random.nextInt(9000); // 生成4位随机数
        return "ORDER" + timestamp + randomNum;
    }

    /**
     * 校验订单金额
     * 
     * @param order 订单信息
     */
    private void validateOrderAmount(Order order) {
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("订单总金额必须大于0");
        }
        
        if (order.getFinalAmount() == null || order.getFinalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("实付金额必须大于0");
        }
        
        if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("优惠金额不能为负数");
        }
        
        // 校验：实付金额 = 总金额 - 优惠金额
        BigDecimal expectedFinalAmount = order.getTotalAmount().subtract(
            order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO);
        
        if (order.getFinalAmount().compareTo(expectedFinalAmount) != 0) {
            throw new RuntimeException("实付金额计算错误");
        }
    }
}