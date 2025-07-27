package com.gig.collide.tag.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gig.collide.tag.domain.repository.UserInterestTagRepository;
import com.gig.collide.tag.infrastructure.entity.UserInterestTagEntity;
import com.gig.collide.tag.infrastructure.mapper.UserInterestTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签仓储实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserInterestTagRepositoryImpl implements UserInterestTagRepository {

    private final UserInterestTagMapper userInterestTagMapper;

    @Override
    public UserInterestTagEntity save(UserInterestTagEntity entity) {
        if (entity.getId() == null) {
            userInterestTagMapper.insert(entity);
        } else {
            userInterestTagMapper.updateById(entity);
        }
        return entity;
    }

    @Override
    public List<UserInterestTagEntity> findByUserId(Long userId) {
        return userInterestTagMapper.selectByUserId(userId, "active");
    }

    @Override
    public List<UserInterestTagEntity> findByTagId(Long tagId, Integer limit) {
        return userInterestTagMapper.selectByTagId(tagId, "active", limit);
    }

    @Override
    public UserInterestTagEntity findByUserIdAndTagId(Long userId, Long tagId) {
        return userInterestTagMapper.selectByUserIdAndTagId(userId, tagId);
    }

    @Override
    public boolean batchSave(List<UserInterestTagEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return true;
        }
        
        try {
            int result = userInterestTagMapper.batchInsert(entities);
            return result > 0;
        } catch (Exception e) {
            log.error("批量保存用户兴趣标签失败", e);
            return false;
        }
    }

    @Override
    public boolean deleteByUserIdAndTagId(Long userId, Long tagId) {
        LambdaQueryWrapper<UserInterestTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTagEntity::getUserId, userId)
               .eq(UserInterestTagEntity::getTagId, tagId);
        
        int result = userInterestTagMapper.delete(wrapper);
        return result > 0;
    }

    @Override
    public boolean batchDeleteByUserIdAndTagIds(Long userId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return true;
        }
        
        int result = userInterestTagMapper.batchDeleteByUserIdAndTagIds(userId, tagIds);
        return result > 0;
    }

    @Override
    public boolean updateInterestScore(Long userId, Long tagId, BigDecimal interestScore) {
        int result = userInterestTagMapper.updateInterestScore(userId, tagId, interestScore);
        return result > 0;
    }

    @Override
    public List<UserInterestTagEntity> findHighInterestTags(Long userId, BigDecimal minScore, Integer limit) {
        return userInterestTagMapper.selectHighInterestTags(userId, minScore, limit);
    }

    @Override
    public boolean deleteAllByUserId(Long userId) {
        int result = userInterestTagMapper.deleteAllByUserId(userId);
        return result >= 0; // 即使没有记录删除也算成功
    }

    @Override
    public boolean existsByUserIdAndTagId(Long userId, Long tagId) {
        LambdaQueryWrapper<UserInterestTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInterestTagEntity::getUserId, userId)
               .eq(UserInterestTagEntity::getTagId, tagId)
               .eq(UserInterestTagEntity::getStatus, "active");
        
        Long count = userInterestTagMapper.selectCount(wrapper);
        return count > 0;
    }
} 