package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.users.stats.UserStatsQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserStats;
import com.gig.collide.users.domain.service.UserStatsService;
import com.gig.collide.users.infrastructure.mapper.UserStatsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户统计领域服务实现 - 对应 t_user_stats 表
 * 负责用户统计数据管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final UserStatsMapper statsMapper;

    @Override
    @Transactional
    public UserStats createStats(UserStats userStats) {
        try {
            // 检查是否已存在
            UserStats existStats = getStatsByUserId(userStats.getUserId());
            if (existStats != null) {
                log.info("用户统计数据已存在: userId={}", userStats.getUserId());
                return existStats;
            }

            userStats.initDefaults();
            statsMapper.insert(userStats);
            
            log.info("用户统计数据创建成功: userId={}", userStats.getUserId());
            return userStats;
        } catch (Exception e) {
            log.error("创建用户统计数据失败: userId={}", userStats.getUserId(), e);
            throw new RuntimeException("创建用户统计数据失败", e);
        }
    }

    @Override
    @Transactional
    public UserStats updateStats(UserStats userStats) {
        try {
            userStats.updateModifyTime();
            statsMapper.updateByUserId(userStats);
            
            log.info("用户统计数据更新成功: userId={}", userStats.getUserId());
            return userStats;
        } catch (Exception e) {
            log.error("更新用户统计数据失败: userId={}", userStats.getUserId(), e);
            throw new RuntimeException("更新用户统计数据失败", e);
        }
    }

    @Override
    public UserStats getStatsByUserId(Long userId) {
        return statsMapper.findByUserId(userId);
    }

    @Override
    public List<UserStats> batchGetStats(List<Long> userIds) {
        return statsMapper.findByUserIds(userIds);
    }

    @Override
    @Transactional
    public boolean incrementFollowerCount(Long userId, Integer increment) {
        try {
            int result = statsMapper.incrementFollowerCount(userId, increment);
            if (result > 0) {
                log.info("粉丝数更新成功: userId={}, increment={}", userId, increment);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新粉丝数失败: userId={}, increment={}", userId, increment, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementFollowingCount(Long userId, Integer increment) {
        try {
            int result = statsMapper.incrementFollowingCount(userId, increment);
            if (result > 0) {
                log.info("关注数更新成功: userId={}, increment={}", userId, increment);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新关注数失败: userId={}, increment={}", userId, increment, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementContentCount(Long userId, Integer increment) {
        try {
            int result = statsMapper.incrementContentCount(userId, increment);
            if (result > 0) {
                log.info("内容数更新成功: userId={}, increment={}", userId, increment);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新内容数失败: userId={}, increment={}", userId, increment, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementLikeCount(Long userId, Integer increment) {
        try {
            int result = statsMapper.incrementLikeCount(userId, increment);
            if (result > 0) {
                log.info("点赞数更新成功: userId={}, increment={}", userId, increment);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新点赞数失败: userId={}, increment={}", userId, increment, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean incrementLoginCount(Long userId) {
        try {
            int result = statsMapper.incrementLoginCount(userId);
            if (result > 0) {
                log.info("登录次数更新成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新登录次数失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchUpdateStats(List<UserStats> statsList) {
        try {
            int result = statsMapper.batchUpdateStats(statsList);
            log.info("批量更新统计数据成功: count={}", result);
            return result > 0;
        } catch (Exception e) {
            log.error("批量更新统计数据失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resetUserStats(Long userId) {
        try {
            int result = statsMapper.resetUserStats(userId);
            if (result > 0) {
                log.info("用户统计数据重置成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("重置用户统计数据失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public List<UserStats> getTopFollowerUsers(Integer limit) {
        try {
            return statsMapper.findTopFollowerUsers(limit);
        } catch (Exception e) {
            log.error("查询粉丝数排行榜失败: limit={}", limit, e);
            throw new RuntimeException("查询排行榜失败", e);
        }
    }

    @Override
    public List<UserStats> getMostActiveUsers(Integer limit) {
        try {
            return statsMapper.findMostActiveUsers(limit);
        } catch (Exception e) {
            log.error("查询最活跃用户失败: limit={}", limit, e);
            throw new RuntimeException("查询活跃用户失败", e);
        }
    }

    @Override
    public List<UserStats> getTopContentUsers(Integer limit) {
        try {
            return statsMapper.findTopContentUsers(limit);
        } catch (Exception e) {
            log.error("查询内容数排行榜失败: limit={}", limit, e);
            throw new RuntimeException("查询排行榜失败", e);
        }
    }

    @Override
    public PageResponse<UserStats> queryStats(UserStatsQueryRequest request) {
        try {
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            List<UserStats> statsList = statsMapper.findStatsByCondition(
                    request.getFollowerCountMin(),
                    request.getFollowerCountMax(),
                    request.getFollowingCountMin(),
                    request.getFollowingCountMax(),
                    request.getContentCountMin(),
                    request.getContentCountMax(),
                    request.getLikeCountMin(),
                    request.getLikeCountMax(),
                    request.getLoginCountMin(),
                    request.getLoginCountMax(),
                    request.getSortField(),
                    request.getSortDirection(),
                    offset,
                    request.getPageSize()
            );
            
            Long total = statsMapper.countStatsByCondition(
                    request.getFollowerCountMin(),
                    request.getFollowerCountMax(),
                    request.getFollowingCountMin(),
                    request.getFollowingCountMax(),
                    request.getContentCountMin(),
                    request.getContentCountMax(),
                    request.getLikeCountMin(),
                    request.getLikeCountMax(),
                    request.getLoginCountMin(),
                    request.getLoginCountMax()
            );
            
            PageResponse<UserStats> result = new PageResponse<>();
            result.setDatas(statsList);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            return result;
        } catch (Exception e) {
            log.error("查询用户统计数据列表失败", e);
            throw new RuntimeException("查询统计数据失败", e);
        }
    }

    @Override
    public double calculateActivityScore(Long userId) {
        try {
            UserStats stats = getStatsByUserId(userId);
            if (stats != null) {
                stats.calculateAndUpdateActivityScore();
                updateStats(stats); // 保存到数据库
                return stats.getActivityScore() != null ? stats.getActivityScore().doubleValue() : 0.0;
            }
            return 0.0;
        } catch (Exception e) {
            log.error("计算用户活跃度分数失败: userId={}", userId, e);
            return 0.0;
        }
    }

    @Override
    public double calculateInfluenceScore(Long userId) {
        try {
            UserStats stats = getStatsByUserId(userId);
            if (stats != null) {
                stats.calculateAndUpdateInfluenceScore();
                updateStats(stats); // 保存到数据库
                return stats.getInfluenceScore() != null ? stats.getInfluenceScore().doubleValue() : 0.0;
            }
            return 0.0;
        } catch (Exception e) {
            log.error("计算用户影响力分数失败: userId={}", userId, e);
            return 0.0;
        }
    }

    @Override
    public boolean isActiveUser(Long userId) {
        try {
            UserStats stats = getStatsByUserId(userId);
            return stats != null && stats.isActiveUser();
        } catch (Exception e) {
            log.error("检查用户是否活跃失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public boolean isInfluentialUser(Long userId) {
        try {
            UserStats stats = getStatsByUserId(userId);
            return stats != null && stats.isInfluentialUser();
        } catch (Exception e) {
            log.error("检查用户是否知名失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public List<UserStats> batchInitializeStats(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> {
                    UserStats stats = new UserStats();
                    stats.setUserId(userId);
                    return createStats(stats);
                })
                .toList();
    }

    @Override
    @Transactional
    public boolean deleteStats(Long userId) {
        try {
            int result = statsMapper.deleteByUserId(userId);
            if (result > 0) {
                log.warn("用户统计数据被删除: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户统计数据失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean syncUserStats(Long userId) {
        try {
            // 这里可以实现同步统计数据的逻辑
            // 比如从其他表重新计算统计数据
            log.info("同步用户统计数据: userId={}", userId);
            return true;
        } catch (Exception e) {
            log.error("同步用户统计数据失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public boolean batchSyncStats(List<Long> userIds) {
        return userIds.stream().allMatch(this::syncUserStats);
    }
}