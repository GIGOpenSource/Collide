package com.gig.collide.payment.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.payment.PaymentFacadeService;
import com.gig.collide.api.payment.request.PaymentCreateRequest;
import com.gig.collide.api.payment.request.PaymentQueryRequest;
import com.gig.collide.api.payment.request.PaymentCallbackRequest;
import com.gig.collide.api.payment.response.PaymentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.payment.domain.entity.Payment;
import com.gig.collide.payment.domain.service.PaymentService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付门面服务实现 - 简洁版
 * 基于简洁版SQL设计和API（本地服务，优先调用）
 *
 * @author GIG Team
 * @version 2.0.0 (本地服务版)
 */
@Slf4j
@DubboService(version = "1.0.0")
public class PaymentFacadeServiceImpl implements PaymentFacadeService {

    @Autowired
    private PaymentService paymentService;

    @Override
    public Result<PaymentResponse> createPayment(PaymentCreateRequest request) {
        try {
            Payment payment = new Payment();
            BeanUtils.copyProperties(request, payment);
            
            Payment createdPayment = paymentService.createPayment(payment);
            PaymentResponse response = convertToResponse(createdPayment);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建支付订单失败", e);
            return Result.error("PAYMENT_CREATE_ERROR", "创建支付订单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PaymentResponse> getPaymentById(Long paymentId) {
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse response = convertToResponse(payment);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询支付详情失败", e);
            return Result.error("PAYMENT_QUERY_ERROR", "查询支付详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PaymentResponse> getPaymentByNo(String paymentNo) {
        try {
            Payment payment = paymentService.getPaymentByNo(paymentNo);
            if (payment == null) {
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse response = convertToResponse(payment);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据支付单号查询失败", e);
            return Result.error("PAYMENT_QUERY_ERROR", "根据支付单号查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<PaymentResponse>> queryPayments(PaymentQueryRequest request) {
        try {
            IPage<Payment> page = paymentService.queryPayments(
                request.getUserId(),
                request.getPayMethod(),
                request.getStatus(),
                request.getPaymentNo(),
                request.getOrderNo(),
                request.getOrderId(),
                request.getThirdPartyNo(),
                request.getMinAmount(),
                request.getMaxAmount(),
                request.getStartTime(),
                request.getEndTime(),
                request.getCurrentPage(),
                request.getPageSize(),
                request.getSortBy(),
                request.getSortDirection()
            );
            
            List<PaymentResponse> responses = page.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            PageResponse<PaymentResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setTotal((int) page.getTotal());
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询支付记录失败", e);
            return Result.error("PAYMENT_QUERY_ERROR", "分页查询支付记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> handlePaymentCallback(PaymentCallbackRequest request) {
        try {
            paymentService.handlePaymentCallback(
                request.getPaymentNo(),
                request.getStatus(),
                request.getThirdPartyNo(),
                request.getPayTime(),
                request.getNotifyTime()
            );
            
            return Result.success(null);
        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            return Result.error("PAYMENT_CALLBACK_ERROR", "支付回调处理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> cancelPayment(Long paymentId) {
        try {
            paymentService.cancelPayment(paymentId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消支付失败", e);
            return Result.error("PAYMENT_CANCEL_ERROR", "取消支付失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PaymentResponse> syncPaymentStatus(String paymentNo) {
        try {
            Payment payment = paymentService.syncPaymentStatus(paymentNo);
            if (payment == null) {
                return Result.error("PAYMENT_NOT_FOUND", "支付记录不存在");
            }
            
            PaymentResponse response = convertToResponse(payment);
            return Result.success(response);
        } catch (Exception e) {
            log.error("同步支付状态失败", e);
            return Result.error("PAYMENT_SYNC_ERROR", "同步支付状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<PaymentResponse>> getUserPayments(Long userId, Integer limit) {
        try {
            List<Payment> payments = paymentService.getUserPayments(userId, limit);
            List<PaymentResponse> responses = payments.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户支付记录失败", e);
            return Result.error("PAYMENT_QUERY_ERROR", "获取用户支付记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<PaymentResponse>> getPaymentsByOrderId(Long orderId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByOrderId(orderId);
            List<PaymentResponse> responses = payments.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据订单ID查询支付记录失败", e);
            return Result.error("PAYMENT_QUERY_ERROR", "根据订单ID查询支付记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> verifyPaymentStatus(String paymentNo) {
        try {
            boolean isSuccess = paymentService.verifyPaymentStatus(paymentNo);
            return Result.success(isSuccess);
        } catch (Exception e) {
            log.error("验证支付状态失败", e);
            return Result.error("PAYMENT_VERIFY_ERROR", "验证支付状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PaymentResponse> getPaymentStatistics(Long userId) {
        try {
            Payment statistics = paymentService.getUserPaymentStatistics(userId);
            PaymentResponse response = convertToResponse(statistics);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取支付统计信息失败", e);
            return Result.error("PAYMENT_STATISTICS_ERROR", "获取支付统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 转换为支付响应对象
     */
    private PaymentResponse convertToResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        BeanUtils.copyProperties(payment, response);
        
        // 设置状态描述
        response.setStatusDesc(getStatusDesc(payment.getStatus()));
        
        // 设置是否可以取消
        response.setCancellable(paymentService.canCancelPayment(payment.getId()));
        
        return response;
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(String status) {
        switch (status) {
            case "pending":
                return "待支付";
            case "success":
                return "支付成功";
            case "failed":
                return "支付失败";
            case "cancelled":
                return "已取消";
            default:
                return "未知状态";
        }
    }
} 