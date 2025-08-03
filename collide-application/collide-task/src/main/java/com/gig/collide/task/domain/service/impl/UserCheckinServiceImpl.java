package com.gig.collide.task.domain.service.impl;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.task.domain.entity.TaskTemplate;
import com.gig.collide.task.domain.entity.UserCheckinRecord;
import com.gig.collide.task.domain.service.TaskTemplateService;
import com.gig.collide.task.domain.service.UserCheckinService;
import com.gig.collide.task.infrastructure.mapper.UserCheckinRecordMapper;
import com.gig.collide.api.task.request.UserTaskQueryRequest;
import com.gig.collide.api.user.UserWalletFacadeService;
import com.gig.collide.api.user.request.wallet.WalletTransactionRequest;
import com.gig.collide.api.user.request.wallet.UserWalletCreateRequest;
import com.gig.collide.api.user.response.wallet.UserWalletResponse;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户签到领域服务实现 - 对应 t_user_checkin_record 表
 * 负责用户签到行为和奖励记录管理
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCheckinServiceImpl implements UserCheckinService {



    private final UserCheckinRecordMapper userCheckinRecordMapper;
    private final TaskTemplateService taskTemplateService;
    
    @DubboReference(version = "1.0.0")
    private UserWalletFacadeService userWalletFacadeService;

    // =================== 签到核心功能 ===================

    @Override
    @Transactional
    public UserCheckinRecord checkin(Long userId, Long taskTemplateId, String checkinIp) {
        try {
            log.info("用户签到开始: userId={}, taskTemplateId={}", userId, taskTemplateId);
            
            // 1. 参数验证
            if (userId == null || userId <= 0) {
                throw new IllegalArgumentException("用户ID无效");
            }
            
            LocalDate today = LocalDate.now();
            
            // 2. 检查今日是否已签到
            if (isTodayChecked(userId)) {
                log.warn("用户今日已签到: userId={}, date={}", userId, today);
                throw new RuntimeException("今日已签到，请明天再来");
            }
            
            // 3. 验证任务模板
            TaskTemplate taskTemplate = taskTemplateService.getTaskTemplateById(taskTemplateId);
            if (taskTemplate == null || !taskTemplate.isEnabled()) {
                log.error("任务模板无效或已禁用: taskTemplateId={}", taskTemplateId);
                throw new RuntimeException("签到任务不可用");
            }
            
            // 4. 计算连续签到天数
            Integer continuousDays = calculateNextContinuousDays(userId);
            
            // 5. 计算奖励金币
            Integer rewardCoins = calculateCheckinReward(userId, taskTemplate.getRewardCoins());
            
            // 6. 创建签到记录
            UserCheckinRecord checkinRecord = UserCheckinRecord.buildCheckinRecord(
                    userId, taskTemplateId, taskTemplate.getRewardCoins(), continuousDays, checkinIp);
            checkinRecord.setRewardCoins(rewardCoins);
            
            // 7. 保存签到记录
            userCheckinRecordMapper.insert(checkinRecord);
            
            // 8. 发放金币到用户钱包
            boolean coinGranted = grantCheckinRewardCoins(userId, rewardCoins);
            
            log.info("用户签到成功: userId={}, continuousDays={}, rewardCoins={}", 
                    userId, continuousDays, rewardCoins);
            
            return checkinRecord;
        } catch (Exception e) {
            log.error("用户签到失败: userId={}, taskTemplateId={}", userId, taskTemplateId, e);
            throw new RuntimeException("签到失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isTodayChecked(Long userId) {
        try {
            LocalDate today = LocalDate.now();
            boolean checked = userCheckinRecordMapper.checkTodayCheckin(userId, today);
            log.debug("检查今日签到状态: userId={}, today={}, checked={}", userId, today, checked);
            return checked;
        } catch (Exception e) {
            log.error("检查今日签到状态失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public Integer getContinuousCheckinDays(Long userId) {
        try {
            Integer days = userCheckinRecordMapper.calculateContinuousCheckinDays(userId);
            if (days == null) {
                days = 0;
            }
            log.debug("获取连续签到天数: userId={}, days={}", userId, days);
            return days;
        } catch (Exception e) {
            log.error("获取连续签到天数失败: userId={}", userId, e);
            return 0;
        }
    }

    // =================== 签到记录查询 ===================

    @Override
    public UserCheckinRecord getCheckinRecord(Long userId, LocalDate date) {
        try {
            UserCheckinRecord record = userCheckinRecordMapper.findByUserIdAndDate(userId, date);
            log.debug("查询签到记录: userId={}, date={}, found={}", userId, date, record != null);
            return record;
        } catch (Exception e) {
            log.error("查询签到记录失败: userId={}, date={}", userId, date, e);
            return null;
        }
    }

    @Override
    public PageResponse<UserCheckinRecord> queryUserCheckinRecords(UserTaskQueryRequest request) {
        try {
            log.debug("分页查询用户签到记录: userId={}, page={}, size={}", 
                    request.getUserId(), request.getCurrentPage(), request.getPageSize());
            
            // 计算分页参数
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            // 查询记录列表
            List<UserCheckinRecord> records = userCheckinRecordMapper.findUserCheckinRecords(
                    request.getUserId(),
                    request.getTaskTemplateId(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getMinContinuousDays(),
                    request.getMaxContinuousDays(),
                    offset,
                    request.getPageSize()
            );
            
            // 查询总数
            Long total = userCheckinRecordMapper.countUserCheckinRecords(
                    request.getUserId(),
                    request.getTaskTemplateId(),
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getMinContinuousDays(),
                    request.getMaxContinuousDays()
            );
            
            // 构建分页结果
            PageResponse<UserCheckinRecord> result = new PageResponse<>();
            result.setDatas(records);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            log.debug("签到记录查询结果: count={}, total={}", records.size(), total);
            return result;
        } catch (Exception e) {
            log.error("分页查询用户签到记录失败: userId={}", request.getUserId(), e);
            throw new RuntimeException("查询签到记录失败", e);
        }
    }

    @Override
    public List<UserCheckinRecord> getMonthlyCheckinRecords(Long userId, LocalDate month) {
        try {
            List<UserCheckinRecord> records = userCheckinRecordMapper.findMonthlyCheckinRecords(
                    userId, month.getYear(), month.getMonthValue());
            log.debug("查询月度签到记录: userId={}, month={}, count={}", userId, month, records.size());
            return records;
        } catch (Exception e) {
            log.error("查询月度签到记录失败: userId={}, month={}", userId, month, e);
            throw new RuntimeException("查询月度签到记录失败", e);
        }
    }

    @Override
    public UserCheckinRecord getLatestCheckinRecord(Long userId) {
        try {
            UserCheckinRecord record = userCheckinRecordMapper.getLatestCheckinRecord(userId);
            log.debug("获取最近签到记录: userId={}, found={}", userId, record != null);
            return record;
        } catch (Exception e) {
            log.error("获取最近签到记录失败: userId={}", userId, e);
            return null;
        }
    }

    // =================== 签到统计功能 ===================

    @Override
    public Integer getUserTotalCheckins(Long userId) {
        try {
            Integer count = userCheckinRecordMapper.countUserTotalCheckins(userId);
            if (count == null) {
                count = 0;
            }
            log.debug("统计用户总签到次数: userId={}, count={}", userId, count);
            return count;
        } catch (Exception e) {
            log.error("统计用户总签到次数失败: userId={}", userId, e);
            return 0;
        }
    }

    @Override
    public Integer getUserMonthlyCheckins(Long userId, LocalDate month) {
        try {
            Integer count = userCheckinRecordMapper.countUserMonthlyCheckins(
                    userId, month.getYear(), month.getMonthValue());
            if (count == null) {
                count = 0;
            }
            log.debug("统计用户月度签到次数: userId={}, month={}, count={}", userId, month, count);
            return count;
        } catch (Exception e) {
            log.error("统计用户月度签到次数失败: userId={}, month={}", userId, month, e);
            return 0;
        }
    }

    @Override
    public Integer getUserMaxContinuousCheckinDays(Long userId) {
        try {
            Integer maxDays = userCheckinRecordMapper.getMaxContinuousCheckinDays(userId);
            if (maxDays == null) {
                maxDays = 0;
            }
            log.debug("查询用户最长连续签到天数: userId={}, maxDays={}", userId, maxDays);
            return maxDays;
        } catch (Exception e) {
            log.error("查询用户最长连续签到天数失败: userId={}", userId, e);
            return 0;
        }
    }

    @Override
    public Long getUserTotalRewardCoins(Long userId) {
        try {
            Long totalCoins = userCheckinRecordMapper.getTotalRewardCoins(userId);
            if (totalCoins == null) {
                totalCoins = 0L;
            }
            log.debug("统计用户累计获得金币: userId={}, totalCoins={}", userId, totalCoins);
            return totalCoins;
        } catch (Exception e) {
            log.error("统计用户累计获得金币失败: userId={}", userId, e);
            return 0L;
        }
    }

    @Override
    public UserCheckinRecord buildUserCheckinStats(Long userId) {
        try {
            log.debug("构建用户签到统计信息: userId={}", userId);
            
            UserCheckinRecord stats = new UserCheckinRecord();
            stats.setUserId(userId);
            
            // 设置统计数据 - 这里使用扩展字段存储统计信息
            // 注意：这是一个特殊用法，实际项目中可能需要单独的统计DTO
            LocalDate now = LocalDate.now();
            
            Integer totalCount = getUserTotalCheckins(userId);
            Integer monthlyCount = getUserMonthlyCheckins(userId, now);
            Integer maxContinuous = getUserMaxContinuousCheckinDays(userId);
            Long totalCoins = getUserTotalRewardCoins(userId);
            Integer currentContinuous = getContinuousCheckinDays(userId);
            
            // 使用注释字段存储统计信息（临时方案）
            stats.setRewardCoins(totalCount);  // 复用字段存储总次数
            stats.setContinuousDays(currentContinuous);
            
            log.debug("用户签到统计信息: userId={}, total={}, monthly={}, maxContinuous={}, totalCoins={}", 
                    userId, totalCount, monthlyCount, maxContinuous, totalCoins);
            
            return stats;
        } catch (Exception e) {
            log.error("构建用户签到统计信息失败: userId={}", userId, e);
            throw new RuntimeException("获取签到统计信息失败", e);
        }
    }

    // =================== 管理员功能 ===================

    @Override
    public PageResponse<UserCheckinRecord> getCheckinRecordsByDate(LocalDate date, Integer page, Integer size) {
        try {
            log.debug("查询指定日期签到记录: date={}, page={}, size={}", date, page, size);
            
            int offset = (page - 1) * size;
            List<UserCheckinRecord> records = userCheckinRecordMapper.findByCheckinDate(date, offset, size);
            Long total = userCheckinRecordMapper.countCheckinUsersByDate(date);
            
            log.debug("指定日期签到记录查询结果: date={}, count={}, total={}", date, records.size(), total);
            PageResponse<UserCheckinRecord> result = new PageResponse<>();
            result.setDatas(records);
            result.setCurrentPage(page);
            result.setPageSize(size);
            result.setTotal(total);
            return result;
        } catch (Exception e) {
            log.error("查询指定日期签到记录失败: date={}", date, e);
            throw new RuntimeException("查询签到记录失败", e);
        }
    }

    @Override
    public Long countCheckinUsersByDate(LocalDate date) {
        try {
            Long count = userCheckinRecordMapper.countCheckinUsersByDate(date);
            log.debug("统计指定日期签到用户数: date={}, count={}", date, count);
            return count;
        } catch (Exception e) {
            log.error("统计指定日期签到用户数失败: date={}", date, e);
            return 0L;
        }
    }

    @Override
    public List<UserCheckinRecord> batchGetCheckinRecords(List<Long> userIds, LocalDate date) {
        try {
            List<UserCheckinRecord> records = userCheckinRecordMapper.findByUserIds(userIds, date);
            log.debug("批量查询用户签到记录: userIds.size={}, date={}, found={}", 
                    userIds.size(), date, records.size());
            return records;
        } catch (Exception e) {
            log.error("批量查询用户签到记录失败: userIds.size={}, date={}", userIds.size(), date, e);
            throw new RuntimeException("批量查询签到记录失败", e);
        }
    }

    // =================== 业务验证功能 ===================

    @Override
    public boolean canCheckin(Long userId) {
        try {
            // 1. 检查今日是否已签到
            boolean todayChecked = isTodayChecked(userId);
            if (todayChecked) {
                log.debug("用户今日已签到: userId={}", userId);
                return false;
            }
            
            // 2. 检查用户钱包状态，不存在则自动创建
            try {
                Result<Boolean> walletExistsResult = userWalletFacadeService.checkWalletExists(userId);
                if (walletExistsResult == null || !walletExistsResult.getSuccess() || !walletExistsResult.getData()) {
                    log.info("用户钱包不存在，自动创建钱包: userId={}", userId);
                    // 自动创建用户钱包
                    boolean createResult = createUserWalletIfNotExists(userId);
                    if (!createResult) {
                        log.error("创建用户钱包失败，不允许签到: userId={}", userId);
                        return false;
                    }
                } else {
                    // 钱包存在，检查状态
                    Result<UserWalletResponse> walletResult = userWalletFacadeService.getWalletByUserId(userId);
                    if (walletResult != null && walletResult.getSuccess() && walletResult.getData() != null) {
                        UserWalletResponse wallet = walletResult.getData();
                        if (!"active".equals(wallet.getStatus())) {
                            log.warn("用户钱包状态异常，不允许签到: userId={}, status={}", userId, wallet.getStatus());
                            return false;
                        }
                    }
                }
            } catch (Exception walletEx) {
                log.warn("检查用户钱包状态异常，允许签到但钱包发放可能失败: userId={}", userId, walletEx);
                // 钱包异常不阻止签到，但金币发放时会处理异常
            }
            
            log.debug("验证用户签到权限: userId={}, canCheckin=true", userId);
            return true;
        } catch (Exception e) {
            log.error("验证用户签到权限失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public Integer calculateCheckinReward(Long userId, Integer baseReward) {
        try {
            if (baseReward == null || baseReward <= 0) {
                baseReward = 10; // 默认基础奖励
            }
            
            // 获取即将达到的连续签到天数
            Integer nextContinuousDays = calculateNextContinuousDays(userId);
            
            // 计算奖励：连续7天翻倍
            Integer rewardCoins = baseReward;
            if (nextContinuousDays >= 7 && nextContinuousDays % 7 == 0) {
                rewardCoins = baseReward * 2; // 翻倍奖励
                log.debug("连续签到达到奖励条件: userId={}, days={}, baseReward={}, finalReward={}", 
                        userId, nextContinuousDays, baseReward, rewardCoins);
            }
            
            log.debug("计算签到奖励: userId={}, baseReward={}, continuousDays={}, finalReward={}", 
                    userId, baseReward, nextContinuousDays, rewardCoins);
            
            return rewardCoins;
        } catch (Exception e) {
            log.error("计算签到奖励失败: userId={}, baseReward={}", userId, baseReward, e);
            return baseReward != null ? baseReward : 10;
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 计算下次签到的连续天数
     */
    private Integer calculateNextContinuousDays(Long userId) {
        try {
            UserCheckinRecord latestRecord = getLatestCheckinRecord(userId);
            
            if (latestRecord == null) {
                // 第一次签到
                return 1;
            }
            
            LocalDate today = LocalDate.now();
            LocalDate latestDate = latestRecord.getCheckinDate();
            
            if (latestDate.equals(today.minusDays(1))) {
                // 昨天签到了，连续天数+1
                return latestRecord.getContinuousDays() + 1;
            } else {
                // 中断了，重新开始
                return 1;
            }
        } catch (Exception e) {
            log.error("计算下次签到连续天数失败: userId={}", userId, e);
            return 1;
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 发放签到奖励金币
     * 
     * @param userId 用户ID
     * @param rewardCoins 奖励金币数量
     * @return 发放是否成功
     */
    private boolean grantCheckinRewardCoins(Long userId, Integer rewardCoins) {
        try {
            log.info("开始发放签到金币奖励: userId={}, coins={}", userId, rewardCoins);
            
            // 构建钱包交易请求
            WalletTransactionRequest walletRequest = new WalletTransactionRequest();
            walletRequest.setUserId(userId);
            walletRequest.setTransactionType("coin_grant");
            walletRequest.setCoinAmount((long) rewardCoins);
            walletRequest.setBusinessId("checkin_" + userId + "_" + LocalDate.now());
            walletRequest.setDescription("每日签到奖励");
            walletRequest.setBusinessType("DAILY_CHECKIN");
            
            // 尝试发放金币
            Result<UserWalletResponse> walletResult = userWalletFacadeService.grantCoinReward(walletRequest);
            
            if (walletResult != null && walletResult.getSuccess()) {
                log.info("签到金币发放成功: userId={}, coins={}, newBalance={}", 
                        userId, rewardCoins, walletResult.getData().getCoinBalance());
                return true;
            } else {
                String errorMsg = walletResult != null ? walletResult.getMessage() : "钱包服务异常";
                log.warn("签到金币发放失败: userId={}, coins={}, error={}", userId, rewardCoins, errorMsg);
                
                // 如果是钱包不存在的错误，尝试创建钱包后重试
                if (errorMsg != null && (errorMsg.contains("钱包不存在") || errorMsg.contains("USER_WALLET_NOT_FOUND"))) {
                    log.info("检测到钱包不存在错误，尝试创建钱包后重试: userId={}", userId);
                    
                    // 创建钱包
                    if (createUserWalletIfNotExists(userId)) {
                        log.info("钱包创建成功，重试发放金币: userId={}", userId);
                        
                        // 重试发放金币
                        Result<UserWalletResponse> retryResult = userWalletFacadeService.grantCoinReward(walletRequest);
                        if (retryResult != null && retryResult.getSuccess()) {
                            log.info("重试签到金币发放成功: userId={}, coins={}, newBalance={}", 
                                    userId, rewardCoins, retryResult.getData().getCoinBalance());
                            return true;
                        } else {
                            log.error("重试签到金币发放仍然失败: userId={}, coins={}, error={}", 
                                    userId, rewardCoins, retryResult != null ? retryResult.getMessage() : "钱包服务异常");
                        }
                    }
                }
                return false;
            }
        } catch (Exception e) {
            log.error("发放签到金币奖励异常: userId={}, coins={}", userId, rewardCoins, e);
            return false;
        }
    }

    /**
     * 创建用户钱包（如果不存在）
     * 
     * @param userId 用户ID
     * @return 创建是否成功
     */
    private boolean createUserWalletIfNotExists(Long userId) {
        try {
            log.info("开始创建用户钱包: userId={}", userId);
            
            // 构建钱包创建请求
            UserWalletCreateRequest createRequest = new UserWalletCreateRequest();
            createRequest.setUserId(userId);
            createRequest.setBalance(java.math.BigDecimal.ZERO);  // 初始现金余额为0
            createRequest.setCoinBalance(0L);                     // 初始金币余额为0
            createRequest.setStatus("active");                    // 钱包状态为活跃
            
            // 调用钱包服务创建钱包
            Result<UserWalletResponse> createResult = userWalletFacadeService.createWallet(createRequest);
            
            if (createResult != null && createResult.getSuccess()) {
                UserWalletResponse wallet = createResult.getData();
                log.info("用户钱包创建成功: userId={}, walletId={}, balance={}, coinBalance={}", 
                        userId, wallet.getId(), wallet.getBalance(), wallet.getCoinBalance());
                return true;
            } else {
                log.error("用户钱包创建失败: userId={}, error={}", 
                        userId, createResult != null ? createResult.getMessage() : "钱包服务异常");
                return false;
            }
        } catch (Exception e) {
            log.error("创建用户钱包异常: userId={}", userId, e);
            return false;
        }
    }
}