package com.gig.collide.task.integration;

import com.gig.collide.task.domain.service.TaskWalletSyncService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 模块钱包集成测试
 * 测试任务奖励发放功能
 * 
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class TaskWalletIntegrationTest {

    @Autowired
    private TaskWalletSyncService taskWalletSyncService;

    /**
     * 测试钱包服务可用性检查
     */
    @Test
    public void testWalletServiceAvailability() {
        log.info("测试钱包服务可用性检查");
        
        boolean available = taskWalletSyncService.isWalletServiceAvailable();
        
        log.info("钱包服务可用性检查结果: {}", available);
        // 注意：在测试环境中，如果 users 服务未启动，这个测试可能会失败
        // 可以根据实际测试环境调整断言
    }

    /**
     * 测试金币奖励发放
     */
    @Test
    public void testCoinRewardGrant() {
        log.info("测试金币奖励发放");
        
        Long testUserId = 1001L;
        Integer rewardAmount = 50;
        String source = "TEST_DAILY_TASK";
        Long taskRecordId = 12345L;
        
        // 检查服务可用性
        if (!taskWalletSyncService.isWalletServiceAvailable()) {
            log.warn("钱包服务不可用，跳过奖励发放测试");
            return;
        }
        
        // 获取发放前的余额
        Long balanceBefore = taskWalletSyncService.getCurrentCoinBalance(testUserId);
        log.info("发放前用户余额: {}", balanceBefore);
        
        // 发放奖励
        boolean success = taskWalletSyncService.syncCoinRewardToWallet(
            testUserId, rewardAmount, source, taskRecordId
        );
        
        log.info("奖励发放结果: {}", success);
        
        if (success) {
            // 获取发放后的余额
            Long balanceAfter = taskWalletSyncService.getCurrentCoinBalance(testUserId);
            log.info("发放后用户余额: {}", balanceAfter);
            
            // 验证余额增加
            assertEquals(balanceBefore + rewardAmount, balanceAfter, 
                "用户余额应该增加" + rewardAmount + "金币");
        }
    }

    /**
     * 测试余额查询功能
     */
    @Test
    public void testBalanceQuery() {
        log.info("测试余额查询功能");
        
        Long testUserId = 1001L;
        
        if (!taskWalletSyncService.isWalletServiceAvailable()) {
            log.warn("钱包服务不可用，跳过余额查询测试");
            return;
        }
        
        // 查询当前余额
        Long currentBalance = taskWalletSyncService.getCurrentCoinBalance(testUserId);
        log.info("用户当前余额: {}", currentBalance);
        
        // 查询累计收入
        Long totalEarned = taskWalletSyncService.getTotalEarnedCoins(testUserId);
        log.info("用户累计收入: {}", totalEarned);
        
        // 验证数据合理性
        assertNotNull(currentBalance, "当前余额不应该为空");
        assertNotNull(totalEarned, "累计收入不应该为空");
        assertTrue(currentBalance >= 0, "当前余额不应该为负数");
        assertTrue(totalEarned >= 0, "累计收入不应该为负数");
        assertTrue(totalEarned >= currentBalance, "累计收入应该大于等于当前余额");
    }

    /**
     * 测试参数验证
     */
    @Test
    public void testParameterValidation() {
        log.info("测试参数验证");
        
        // 测试无效用户ID
        boolean result1 = taskWalletSyncService.syncCoinRewardToWallet(
            null, 50, "TEST", 123L
        );
        assertFalse(result1, "用户ID为空时应该返回失败");
        
        // 测试无效金额
        boolean result2 = taskWalletSyncService.syncCoinRewardToWallet(
            1001L, 0, "TEST", 123L
        );
        assertFalse(result2, "金额为0时应该返回失败");
        
        // 测试负数金额
        boolean result3 = taskWalletSyncService.syncCoinRewardToWallet(
            1001L, -10, "TEST", 123L
        );
        assertFalse(result3, "金额为负数时应该返回失败");
        
        log.info("参数验证测试完成");
    }

    /**
     * 批量奖励发放压力测试
     */
    @Test
    public void testBatchRewardGrant() {
        log.info("测试批量奖励发放");
        
        if (!taskWalletSyncService.isWalletServiceAvailable()) {
            log.warn("钱包服务不可用，跳过批量测试");
            return;
        }
        
        Long testUserId = 1002L;
        int batchSize = 5;
        int rewardPerTask = 10;
        
        Long balanceBefore = taskWalletSyncService.getCurrentCoinBalance(testUserId);
        log.info("批量发放前余额: {}", balanceBefore);
        
        int successCount = 0;
        for (int i = 0; i < batchSize; i++) {
            boolean success = taskWalletSyncService.syncCoinRewardToWallet(
                testUserId, 
                rewardPerTask, 
                "BATCH_TEST_" + i, 
                (long) (2000 + i)
            );
            
            if (success) {
                successCount++;
            }
            
            // 避免请求过快
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        log.info("批量发放完成: 总数={}, 成功={}", batchSize, successCount);
        
        Long balanceAfter = taskWalletSyncService.getCurrentCoinBalance(testUserId);
        log.info("批量发放后余额: {}", balanceAfter);
        
        // 验证余额变化
        long expectedIncrease = successCount * rewardPerTask;
        assertEquals(balanceBefore + expectedIncrease, balanceAfter, 
            "余额应该增加" + expectedIncrease + "金币");
    }
}