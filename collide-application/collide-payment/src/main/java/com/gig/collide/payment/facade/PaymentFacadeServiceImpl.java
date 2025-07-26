package com.gig.collide.payment.facade;

import com.gig.collide.api.payment.service.PaymentFacadeService;
import com.gig.collide.base.response.SingleResponse;
import com.gig.collide.payment.domain.service.TestPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 支付服务 Facade 实现
 * 
 * 提供支付相关的 RPC 服务接口实现
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@DubboService(version = "1.0.0", timeout = 5000)
@RequiredArgsConstructor
public class PaymentFacadeServiceImpl implements PaymentFacadeService {

    private final TestPaymentService testPaymentService;

    @Override
    public SingleResponse<Map<String, Object>> mockPaymentSuccess(String orderNo, String payAmount, String payType) {
        try {
            log.info("RPC调用：模拟支付成功，订单号：{}，支付金额：{}，支付方式：{}", orderNo, payAmount, payType);
            
            if (orderNo == null || orderNo.trim().isEmpty()) {
                return SingleResponse.buildFailure("PARAM_ERROR", "订单号不能为空");
            }

            Map<String, Object> result = testPaymentService.mockPaymentSuccess(orderNo, payAmount, payType);
            
            log.info("RPC调用：模拟支付成功完成，订单号：{}", orderNo);
            return SingleResponse.buildSuccess(result);
            
        } catch (Exception e) {
            log.error("RPC调用：模拟支付成功失败，订单号：{}", orderNo, e);
            return SingleResponse.buildFailure("MOCK_PAYMENT_SUCCESS_ERROR", "模拟支付成功失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> mockPaymentFailure(String orderNo, String failureReason) {
        try {
            log.info("RPC调用：模拟支付失败，订单号：{}，失败原因：{}", orderNo, failureReason);
            
            if (orderNo == null || orderNo.trim().isEmpty()) {
                return SingleResponse.buildFailure("PARAM_ERROR", "订单号不能为空");
            }

            Map<String, Object> result = testPaymentService.mockPaymentFailure(orderNo, failureReason);
            
            log.info("RPC调用：模拟支付失败完成，订单号：{}", orderNo);
            return SingleResponse.buildSuccess(result);
            
        } catch (Exception e) {
            log.error("RPC调用：模拟支付失败失败，订单号：{}", orderNo, e);
            return SingleResponse.buildFailure("MOCK_PAYMENT_FAILURE_ERROR", "模拟支付失败失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> batchMockPaymentSuccess(List<String> orderNos) {
        try {
            log.info("RPC调用：批量模拟支付成功，订单数量：{}", orderNos != null ? orderNos.size() : 0);
            
            if (orderNos == null || orderNos.isEmpty()) {
                return SingleResponse.buildFailure("PARAM_ERROR", "订单号列表不能为空");
            }

            Map<String, Object> result = testPaymentService.batchMockPaymentSuccess(orderNos);
            
            log.info("RPC调用：批量模拟支付成功完成，成功数量：{}", result.get("successCount"));
            return SingleResponse.buildSuccess(result);
            
        } catch (Exception e) {
            log.error("RPC调用：批量模拟支付成功失败", e);
            return SingleResponse.buildFailure("BATCH_MOCK_PAYMENT_SUCCESS_ERROR", "批量模拟支付成功失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> getPaymentStatus(String orderNo) {
        try {
            log.info("RPC调用：获取支付状态，订单号：{}", orderNo);
            
            if (orderNo == null || orderNo.trim().isEmpty()) {
                return SingleResponse.buildFailure("PARAM_ERROR", "订单号不能为空");
            }

            Map<String, Object> status = testPaymentService.getPaymentStatus(orderNo);
            
            log.info("RPC调用：获取支付状态成功，订单号：{}", orderNo);
            return SingleResponse.buildSuccess(status);
            
        } catch (Exception e) {
            log.error("RPC调用：获取支付状态失败，订单号：{}", orderNo, e);
            return SingleResponse.buildFailure("GET_PAYMENT_STATUS_ERROR", "获取支付状态失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Void> resetPaymentStatus(String orderNo) {
        try {
            log.info("RPC调用：重置支付状态，订单号：{}", orderNo);
            
            if (orderNo == null || orderNo.trim().isEmpty()) {
                return SingleResponse.buildFailure("PARAM_ERROR", "订单号不能为空");
            }

            testPaymentService.resetPaymentStatus(orderNo);
            
            log.info("RPC调用：重置支付状态成功，订单号：{}", orderNo);
            return SingleResponse.buildSuccess();
            
        } catch (Exception e) {
            log.error("RPC调用：重置支付状态失败，订单号：{}", orderNo, e);
            return SingleResponse.buildFailure("RESET_PAYMENT_STATUS_ERROR", "重置支付状态失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Map<String, Object>> getTestPaymentConfig() {
        try {
            log.info("RPC调用：获取测试支付配置");
            
            Map<String, Object> config = testPaymentService.getTestPaymentConfig();
            
            log.info("RPC调用：获取测试支付配置成功");
            return SingleResponse.buildSuccess(config);
            
        } catch (Exception e) {
            log.error("RPC调用：获取测试支付配置失败", e);
            return SingleResponse.buildFailure("GET_TEST_PAYMENT_CONFIG_ERROR", "获取测试支付配置失败：" + e.getMessage());
        }
    }

    @Override
    public SingleResponse<Boolean> processPaymentCallback(String orderNo, Map<String, String> callbackParams) {
        try {
            log.info("RPC调用：处理支付回调，订单号：{}，回调参数：{}", orderNo, callbackParams);
            
            if (orderNo == null || orderNo.trim().isEmpty()) {
                return SingleResponse.buildFailure("PARAM_ERROR", "订单号不能为空");
            }

            boolean success = testPaymentService.processPaymentCallback(orderNo, callbackParams);
            
            log.info("RPC调用：处理支付回调完成，订单号：{}，结果：{}", orderNo, success);
            return SingleResponse.buildSuccess(success);
            
        } catch (Exception e) {
            log.error("RPC调用：处理支付回调失败，订单号：{}", orderNo, e);
            return SingleResponse.buildFailure("PROCESS_PAYMENT_CALLBACK_ERROR", "处理支付回调失败：" + e.getMessage());
        }
    }
}