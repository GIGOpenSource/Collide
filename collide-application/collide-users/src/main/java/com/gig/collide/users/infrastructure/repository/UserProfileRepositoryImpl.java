package com.gig.collide.users.infrastructure.repository;

import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.repository.UserProfileRepository;
import com.gig.collide.users.infrastructure.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户扩展信息仓储实现
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfile save(UserProfile userProfile) {
        if (userProfile.getId() == null) {
            userProfileMapper.insert(userProfile);
        } else {
            userProfileMapper.updateById(userProfile);
        }
        return userProfile;
    }

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        UserProfile userProfile = userProfileMapper.selectByUserId(userId);
        return Optional.ofNullable(userProfile);
    }

    @Override
    public void deleteByUserId(Long userId) {
        userProfileMapper.deleteByUserId(userId);
    }

    @Override
    public void increaseFollowerCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "follower_count", count);
    }

    @Override
    public void decreaseFollowerCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "follower_count", -count);
    }

    @Override
    public void increaseFollowingCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "following_count", count);
    }

    @Override
    public void decreaseFollowingCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "following_count", -count);
    }

    @Override
    public void increaseContentCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "content_count", count);
    }

    @Override
    public void decreaseContentCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "content_count", -count);
    }

    @Override
    public void increaseLikeCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "like_count", count);
    }

    @Override
    public void decreaseLikeCount(Long userId, Long count) {
        userProfileMapper.updateCountField(userId, "like_count", -count);
    }
} 