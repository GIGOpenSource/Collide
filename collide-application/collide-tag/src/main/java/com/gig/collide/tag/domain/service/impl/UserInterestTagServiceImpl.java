package com.gig.collide.tag.domain.service.impl;

import com.gig.collide.tag.domain.entity.UserInterestTag;
import com.gig.collide.tag.domain.service.UserInterestTagService;
import com.gig.collide.tag.infrastructure.mapper.UserInterestTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户兴趣标签服务实现类 - 严格对应UserInterestTagMapper
 *
 * @author GIG Team
 * @version 3.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInterestTagServiceImpl implements UserInterestTagService {

    private final UserInterestTagMapper userInterestTagMapper;

    // =================== 基础CRUD操作 ===================

    @Override
    @Transactional
    public UserInterestTag createUserInterestTag(UserInterestTag userInterestTag) {
        log.debug("创建用户兴趣标签: 用户ID={}, 标签ID={}", 
                userInterestTag.getUserId(), userInterestTag.getTagId());
        
        // 设置默认值
        if (!StringUtils.hasText(userInterestTag.getStatus())) {
            userInterestTag.setStatus("active");
        }
        if (userInterestTag.getInterestScore() == null) {
            userInterestTag.setInterestScore(BigDecimal.ZERO);
        }
        
        int result = userInterestTagMapper.insert(userInterestTag);
        if (result > 0) {
            log.info("用户兴趣标签创建成功: ID={}, 用户ID={}, 标签ID={}", 
                    userInterestTag.getId(), userInterestTag.getUserId(), userInterestTag.getTagId());
            return userInterestTag;
        } else {
            throw new RuntimeException("用户兴趣标签创建失败");
        }
    }

    @Override
    @Transactional
    public UserInterestTag updateUserInterestTag(UserInterestTag userInterestTag) {
        log.debug("更新用户兴趣标签: ID={}", userInterestTag.getId());
        
        int result = userInterestTagMapper.updateById(userInterestTag);
        if (result > 0) {
            log.info("用户兴趣标签更新成功: ID={}", userInterestTag.getId());
            return userInterestTag;
        } else {
            throw new RuntimeException("用户兴趣标签更新失败");
        }
    }

    @Override
    @Transactional
    public boolean deleteUserInterestTagById(Long id) {
        log.debug("删除用户兴趣标签: ID={}", id);
        
        int result = userInterestTagMapper.deleteById(id);
        boolean success = result > 0;
        if (success) {
            log.info("用户兴趣标签删除成功: ID={}", id);
        } else {
            log.warn("用户兴趣标签删除失败: ID={}", id);
        }
        return success;
    }

    @Override
    public UserInterestTag getUserInterestTagById(Long id) {
        log.debug("查询用户兴趣标签: ID={}", id);
        return userInterestTagMapper.selectById(id);
    }

    @Override
    public List<UserInterestTag> getAllUserInterestTags() {
        log.debug("查询所有用户兴趣标签");
        return userInterestTagMapper.selectList(null);
    }

    // =================== 核心查询方法 ===================

    @Override
    public List<UserInterestTag> selectByUserId(Long userId) {
        log.debug("获取用户兴趣标签列表: 用户ID={}", userId);
        return userInterestTagMapper.selectByUserId(userId);
    }

    @Override
    public List<UserInterestTag> selectByTagId(Long tagId) {
        log.debug("根据标签ID获取关注用户列表: 标签ID={}", tagId);
        return userInterestTagMapper.selectByTagId(tagId);
    }

    @Override
    public List<UserInterestTag> selectTopInterestsByUserId(Long userId, Integer limit) {
        log.debug("获取用户高分兴趣标签: 用户ID={}, 限制数量={}", userId, limit);
        return userInterestTagMapper.selectTopInterestsByUserId(userId, limit);
    }

    // =================== 统计和计数方法 ===================

    @Override
    public boolean existsByUserIdAndTagId(Long userId, Long tagId) {
        log.debug("检查用户是否已关注标签: 用户ID={}, 标签ID={}", userId, tagId);
        int count = userInterestTagMapper.countByUserIdAndTagId(userId, tagId);
        return count > 0;
    }

    @Override
    public List<Map<String, Object>> getUserInterestStats(Long userId, BigDecimal minScore) {
        log.debug("获取用户兴趣标签统计: 用户ID={}, 最小分数={}", userId, minScore);
        return userInterestTagMapper.getUserInterestStats(userId, minScore);
    }

    @Override
    public List<Map<String, Object>> getTagHotUsers(Long tagId, Integer limit) {
        log.debug("获取标签的热门关注用户: 标签ID={}, 限制数量={}", tagId, limit);
        return userInterestTagMapper.getTagHotUsers(tagId, limit);
    }

    // =================== 更新操作方法 ===================

    @Override
    @Transactional
    public boolean updateInterestScore(Long userId, Long tagId, BigDecimal interestScore) {
        log.debug("更新用户兴趣分数: 用户ID={}, 标签ID={}, 分数={}", userId, tagId, interestScore);
        
        int result = userInterestTagMapper.updateInterestScore(userId, tagId, interestScore);
        boolean success = result > 0;
        if (success) {
            log.info("用户兴趣分数更新成功: 用户ID={}, 标签ID={}", userId, tagId);
        } else {
            log.warn("用户兴趣分数更新失败: 用户ID={}, 标签ID={}", userId, tagId);
        }
        return success;
    }

    @Override
    @Transactional
    public int batchUpdateStatus(Long userId, List<Long> tagIds, String status) {
        log.debug("批量更新用户标签状态: 用户ID={}, 标签数量={}, 状态={}", userId, tagIds.size(), status);
        
        int result = userInterestTagMapper.batchUpdateStatus(userId, tagIds, status);
        log.info("批量更新用户标签状态完成: 更新数量={}", result);
        return result;
    }

    // =================== 业务逻辑方法 ===================

    @Override
    @Transactional
    public UserInterestTag addUserInterestSafely(Long userId, Long tagId, BigDecimal interestScore) {
        log.debug("安全添加用户兴趣标签: 用户ID={}, 标签ID={}, 分数={}", userId, tagId, interestScore);
        
        // 检查是否已存在
        if (existsByUserIdAndTagId(userId, tagId)) {
            throw new RuntimeException("用户已关注此标签: 用户ID=" + userId + ", 标签ID=" + tagId);
        }
        
        UserInterestTag userInterestTag = new UserInterestTag();
        userInterestTag.setUserId(userId);
        userInterestTag.setTagId(tagId);
        userInterestTag.setInterestScore(interestScore);
        
        return createUserInterestTag(userInterestTag);
    }

    @Override
    @Transactional
    public boolean removeUserInterest(Long userId, Long tagId) {
        log.debug("移除用户兴趣标签: 用户ID={}, 标签ID={}", userId, tagId);
        return batchUpdateStatus(userId, List.of(tagId), "inactive") > 0;
    }

    @Override
    @Transactional
    public boolean activateUserInterest(Long userId, Long tagId) {
        log.debug("激活用户兴趣标签: 用户ID={}, 标签ID={}", userId, tagId);
        return batchUpdateStatus(userId, List.of(tagId), "active") > 0;
    }

    @Override
    @Transactional
    public boolean deactivateUserInterest(Long userId, Long tagId) {
        log.debug("停用用户兴趣标签: 用户ID={}, 标签ID={}", userId, tagId);
        return batchUpdateStatus(userId, List.of(tagId), "inactive") > 0;
    }

    @Override
    @Transactional
    public int batchSetUserInterests(Long userId, List<Long> tagIds, BigDecimal defaultScore) {
        log.debug("批量设置用户兴趣标签: 用户ID={}, 标签数量={}, 默认分数={}", 
                userId, tagIds.size(), defaultScore);
        
        int count = 0;
        for (Long tagId : tagIds) {
            try {
                if (!existsByUserIdAndTagId(userId, tagId)) {
                    UserInterestTag userInterestTag = new UserInterestTag();
                    userInterestTag.setUserId(userId);
                    userInterestTag.setTagId(tagId);
                    userInterestTag.setInterestScore(defaultScore);
                    createUserInterestTag(userInterestTag);
                    count++;
                }
            } catch (Exception e) {
                log.warn("批量设置用户兴趣标签失败: 用户ID={}, 标签ID={}", userId, tagId, e);
            }
        }
        
        log.info("批量设置用户兴趣标签完成: 成功数量={}", count);
        return count;
    }

    @Override
    public List<UserInterestTag> getActiveUserInterests(Long userId) {
        log.debug("获取用户活跃兴趣标签: 用户ID={}", userId);
        List<UserInterestTag> allInterests = selectByUserId(userId);
        return allInterests.stream()
                .filter(interest -> "active".equals(interest.getStatus()))
                .toList();
    }

    @Override
    public List<UserInterestTag> getHighInterestTags(Long userId, BigDecimal minScore) {
        log.debug("获取用户高兴趣标签: 用户ID={}, 最小分数={}", userId, minScore);
        List<UserInterestTag> allInterests = selectByUserId(userId);
        return allInterests.stream()
                .filter(interest -> "active".equals(interest.getStatus()) 
                        && interest.getInterestScore().compareTo(minScore) >= 0)
                .toList();
    }

    @Override
    @Transactional
    public boolean increaseInterestScore(Long userId, Long tagId, BigDecimal increment) {
        log.debug("增加用户对标签的兴趣分数: 用户ID={}, 标签ID={}, 增量={}", userId, tagId, increment);
        
        // 获取当前分数
        List<UserInterestTag> interests = selectByUserId(userId);
        UserInterestTag currentInterest = interests.stream()
                .filter(interest -> tagId.equals(interest.getTagId()))
                .findFirst()
                .orElse(null);
        
        if (currentInterest != null) {
            BigDecimal newScore = currentInterest.getInterestScore().add(increment);
            // 限制最大值为100
            if (newScore.compareTo(BigDecimal.valueOf(100)) > 0) {
                newScore = BigDecimal.valueOf(100);
            }
            return updateInterestScore(userId, tagId, newScore);
        }
        
        return false;
    }

    @Override
    @Transactional
    public boolean decreaseInterestScore(Long userId, Long tagId, BigDecimal decrement) {
        log.debug("减少用户对标签的兴趣分数: 用户ID={}, 标签ID={}, 减量={}", userId, tagId, decrement);
        
        // 获取当前分数
        List<UserInterestTag> interests = selectByUserId(userId);
        UserInterestTag currentInterest = interests.stream()
                .filter(interest -> tagId.equals(interest.getTagId()))
                .findFirst()
                .orElse(null);
        
        if (currentInterest != null) {
            BigDecimal newScore = currentInterest.getInterestScore().subtract(decrement);
            // 限制最小值为0
            if (newScore.compareTo(BigDecimal.ZERO) < 0) {
                newScore = BigDecimal.ZERO;
            }
            return updateInterestScore(userId, tagId, newScore);
        }
        
        return false;
    }

    @Override
    @Transactional
    public boolean resetInterestScore(Long userId, Long tagId, BigDecimal newScore) {
        log.debug("重置用户对标签的兴趣分数: 用户ID={}, 标签ID={}, 新分数={}", userId, tagId, newScore);
        
        // 验证分数范围
        if (newScore.compareTo(BigDecimal.ZERO) < 0) {
            newScore = BigDecimal.ZERO;
        } else if (newScore.compareTo(BigDecimal.valueOf(100)) > 0) {
            newScore = BigDecimal.valueOf(100);
        }
        
        return updateInterestScore(userId, tagId, newScore);
    }
}