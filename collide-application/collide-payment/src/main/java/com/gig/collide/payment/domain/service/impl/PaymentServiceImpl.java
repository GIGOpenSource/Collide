package com.gig.collide.payment.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.payment.domain.entity.Payment;
import com.gig.collide.payment.domain.service.PaymentService;
import com.gig.collide.payment.infrastructure.mapper.PaymentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * 支付服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    private final Random random = new Random();

    @Override
    @Transactional
    public Payment createPayment(Payment payment) {
        // 生成支付单号
        if (!StringUtils.hasText(payment.getPaymentNo())) {
            payment.setPaymentNo(generatePaymentNo());
        }
        
        // 设置默认状态
        if (!StringUtils.hasText(payment.getStatus())) {
            payment.setStatus("pending");
        }
        
        paymentMapper.insert(payment);
        return payment;
    }

    @Override
    public Payment getPaymentById(Long paymentId) {
        return paymentMapper.selectById(paymentId);
    }

    @Override
    public Payment getPaymentByNo(String paymentNo) {
        return paymentMapper.selectByPaymentNo(paymentNo);
    }

    @Override
    public IPage<Payment> queryPayments(Long userId, String payMethod, String status,
                                       String paymentNo, String orderNo, Long orderId,
                                       String thirdPartyNo, BigDecimal minAmount, BigDecimal maxAmount,
                                       LocalDateTime startTime, LocalDateTime endTime,
                                       int pageNum, int pageSize, String sortBy, String sortDirection) {
        Page<Payment> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Payment> queryWrapper = new LambdaQueryWrapper<>();
        
        // 动态查询条件
        if (userId != null) {
            queryWrapper.eq(Payment::getUserId, userId);
        }
        if (StringUtils.hasText(payMethod)) {
            queryWrapper.eq(Payment::getPayMethod, payMethod);
        }
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Payment::getStatus, status);
        }
        if (StringUtils.hasText(paymentNo)) {
            queryWrapper.like(Payment::getPaymentNo, paymentNo);
        }
        if (StringUtils.hasText(orderNo)) {
            queryWrapper.like(Payment::getOrderNo, orderNo);
        }
        if (orderId != null) {
            queryWrapper.eq(Payment::getOrderId, orderId);
        }
        if (StringUtils.hasText(thirdPartyNo)) {
            queryWrapper.eq(Payment::getThirdPartyNo, thirdPartyNo);
        }
        if (minAmount != null) {
            queryWrapper.ge(Payment::getAmount, minAmount);
        }
        if (maxAmount != null) {
            queryWrapper.le(Payment::getAmount, maxAmount);
        }
        if (startTime != null) {
            queryWrapper.ge(Payment::getCreateTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(Payment::getCreateTime, endTime);
        }
        
        // 排序
        if ("amount".equals(sortBy)) {
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(Payment::getAmount);
            } else {
                queryWrapper.orderByDesc(Payment::getAmount);
            }
        } else if ("pay_time".equals(sortBy)) {
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(Payment::getPayTime);
            } else {
                queryWrapper.orderByDesc(Payment::getPayTime);
            }
        } else {
            // 默认按创建时间排序
            if ("asc".equals(sortDirection)) {
                queryWrapper.orderByAsc(Payment::getCreateTime);
            } else {
                queryWrapper.orderByDesc(Payment::getCreateTime);
            }
        }
        
        return paymentMapper.selectPage(page, queryWrapper);
    }

    @Override
    @Transactional
    public void handlePaymentCallback(String paymentNo, String status, String thirdPartyNo,
                                     LocalDateTime payTime, LocalDateTime notifyTime) {
        Payment payment = getPaymentByNo(paymentNo);
        if (payment == null) {
            log.warn("支付回调失败：未找到支付记录，支付单号: {}", paymentNo);
            return;
        }
        
        // 更新支付状态
        paymentMapper.updatePaymentStatus(paymentNo, status, thirdPartyNo, payTime, notifyTime);
        log.info("支付回调处理成功：支付单号={}, 状态={}", paymentNo, status);
    }

    @Override
    @Transactional
    public void cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment == null) {
            throw new RuntimeException("支付记录不存在");
        }
        
        if (!"pending".equals(payment.getStatus())) {
            throw new RuntimeException("只能取消待支付的订单");
        }
        
        payment.setStatus("cancelled");
        paymentMapper.updateById(payment);
    }

    @Override
    public Payment syncPaymentStatus(String paymentNo) {
        // 这里应该调用第三方支付接口查询状态
        // 暂时返回当前状态
        return getPaymentByNo(paymentNo);
    }

    @Override
    public List<Payment> getUserPayments(Long userId, Integer limit) {
        return paymentMapper.selectByUserId(userId, limit);
    }

    @Override
    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentMapper.selectByOrderId(orderId);
    }

    @Override
    public boolean verifyPaymentStatus(String paymentNo) {
        Payment payment = getPaymentByNo(paymentNo);
        return payment != null && "success".equals(payment.getStatus());
    }

    @Override
    public String generatePaymentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomSuffix = String.format("%06d", random.nextInt(1000000));
        return "PAY" + timestamp + randomSuffix;
    }

    @Override
    public boolean canCancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        return payment != null && "pending".equals(payment.getStatus());
    }

    @Override
    public Payment getUserPaymentStatistics(Long userId) {
        BigDecimal totalAmount = paymentMapper.sumAmountByUserId(userId);
        int totalCount = paymentMapper.countByUserId(userId);
        
        Payment statistics = new Payment();
        statistics.setUserId(userId);
        statistics.setAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        // 使用ID字段存储总次数（仅用于统计展示）
        statistics.setId((long) totalCount);
        
        return statistics;
    }

    @Override
    @Transactional
    public void updateExpiredPayments() {
        LocalDateTime expireTime = LocalDateTime.now().minusHours(24); // 24小时过期
        int count = paymentMapper.updateExpiredPayments(expireTime);
        log.info("更新过期支付订单数量: {}", count);
    }
} 