package com.gig.collide.users.domain.service.impl;

import com.gig.collide.api.user.request.users.profile.UserProfileQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.service.UserProfileService;
import com.gig.collide.users.infrastructure.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户资料领域服务实现 - 对应 t_user_profile 表
 * 负责用户个人资料信息管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileMapper profileMapper;

    @Override
    @Transactional
    public UserProfile createProfile(UserProfile userProfile) {
        try {
            // 检查是否已存在
            UserProfile existProfile = getProfileByUserId(userProfile.getUserId());
            if (existProfile != null) {
                log.info("用户资料已存在: userId={}", userProfile.getUserId());
                return existProfile;
            }

            userProfile.initDefaults();
            profileMapper.insert(userProfile);
            
            log.info("用户资料创建成功: userId={}", userProfile.getUserId());
            return userProfile;
        } catch (Exception e) {
            log.error("创建用户资料失败: userId={}", userProfile.getUserId(), e);
            throw new RuntimeException("创建用户资料失败", e);
        }
    }

    @Override
    @Transactional
    public UserProfile updateProfile(UserProfile userProfile) {
        try {
            userProfile.updateModifyTime();
            profileMapper.updateByUserId(userProfile);
            
            log.info("用户资料更新成功: userId={}", userProfile.getUserId());
            return userProfile;
        } catch (Exception e) {
            log.error("更新用户资料失败: userId={}", userProfile.getUserId(), e);
            throw new RuntimeException("更新用户资料失败", e);
        }
    }

    @Override
    public UserProfile getProfileByUserId(Long userId) {
        return profileMapper.findByUserId(userId);
    }

    @Override
    public List<UserProfile> batchGetProfiles(List<Long> userIds) {
        return profileMapper.findByUserIds(userIds);
    }

    @Override
    public boolean checkNicknameAvailable(String nickname, Long excludeUserId) {
        try {
            int count = profileMapper.checkNicknameAvailable(nickname, excludeUserId);
            return count == 0;
        } catch (Exception e) {
            log.error("检查昵称可用性失败: nickname={}", nickname, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateAvatar(Long userId, String avatar) {
        try {
            int result = profileMapper.updateAvatar(userId, avatar);
            if (result > 0) {
                log.info("用户头像更新成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户头像失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateNickname(Long userId, String nickname) {
        try {
            // 检查昵称是否可用
            if (!checkNicknameAvailable(nickname, userId)) {
                log.warn("昵称已被使用: nickname={}", nickname);
                return false;
            }

            int result = profileMapper.updateNickname(userId, nickname);
            if (result > 0) {
                log.info("用户昵称更新成功: userId={}, nickname={}", userId, nickname);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户昵称失败: userId={}, nickname={}", userId, nickname, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateBirthday(Long userId, LocalDate birthday) {
        try {
            int result = profileMapper.updateBirthday(userId, birthday);
            if (result > 0) {
                log.info("用户生日更新成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户生日失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateGender(Long userId, Integer gender) {
        try {
            int result = profileMapper.updateGender(userId, gender);
            if (result > 0) {
                log.info("用户性别更新成功: userId={}, gender={}", userId, gender);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户性别失败: userId={}, gender={}", userId, gender, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateLocation(Long userId, String location) {
        try {
            int result = profileMapper.updateLocation(userId, location);
            if (result > 0) {
                log.info("用户所在地更新成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户所在地失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateBio(Long userId, String bio) {
        try {
            int result = profileMapper.updateBio(userId, bio);
            if (result > 0) {
                log.info("用户个人简介更新成功: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("更新用户个人简介失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public List<UserProfile> searchByNickname(String nickname, int page, int size) {
        try {
            int offset = (page - 1) * size;
            return profileMapper.findByNicknameLike(nickname, offset, size);
        } catch (Exception e) {
            log.error("根据昵称搜索用户失败: nickname={}", nickname, e);
            throw new RuntimeException("搜索用户失败", e);
        }
    }

    @Override
    public List<UserProfile> getProfilesByLocation(String location, int page, int size) {
        try {
            int offset = (page - 1) * size;
            return profileMapper.findByLocation(location, offset, size);
        } catch (Exception e) {
            log.error("根据所在地查询用户失败: location={}", location, e);
            throw new RuntimeException("查询用户失败", e);
        }
    }

    @Override
    public PageResponse<UserProfile> queryProfiles(UserProfileQueryRequest request) {
        try {
            int offset = (request.getCurrentPage() - 1) * request.getPageSize();
            
            List<UserProfile> profiles = profileMapper.findProfilesByCondition(
                    request.getNickname(),
                    request.getGender(),
                    request.getLocation(),
                    offset,
                    request.getPageSize()
            );
            
            Long total = profileMapper.countProfilesByCondition(
                    request.getNickname(),
                    request.getGender(),
                    request.getLocation()
            );
            
            PageResponse<UserProfile> result = new PageResponse<>();
            result.setDatas(profiles);
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            result.setTotal(total);
            
            return result;
        } catch (Exception e) {
            log.error("查询用户资料列表失败", e);
            throw new RuntimeException("查询用户资料失败", e);
        }
    }

    @Override
    public boolean hasCompleteProfile(Long userId) {
        try {
            UserProfile profile = getProfileByUserId(userId);
            return profile != null && profile.hasCompleteProfile();
        } catch (Exception e) {
            log.error("检查用户资料完整性失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    public int getProfileCompleteness(Long userId) {
        try {
            UserProfile profile = getProfileByUserId(userId);
            if (profile == null) {
                return 0;
            }
            
            int totalFields = 6; // 昵称、头像、简介、生日、性别、所在地
            int filledFields = 0;
            
            if (profile.getNickname() != null && !profile.getNickname().trim().isEmpty()) filledFields++;
            if (profile.getAvatar() != null && !profile.getAvatar().trim().isEmpty()) filledFields++;
            if (profile.getBio() != null && !profile.getBio().trim().isEmpty()) filledFields++;
            if (profile.getBirthday() != null) filledFields++;
            if (profile.getGender() != null && profile.getGender() > 0) filledFields++;
            if (profile.getLocation() != null && !profile.getLocation().trim().isEmpty()) filledFields++;
            
            return (filledFields * 100) / totalFields;
        } catch (Exception e) {
            log.error("计算用户资料完整度失败: userId={}", userId, e);
            return 0;
        }
    }

    @Override
    @Transactional
    public boolean deleteProfile(Long userId) {
        try {
            int result = profileMapper.deleteByUserId(userId);
            if (result > 0) {
                log.warn("用户资料被删除: userId={}", userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("删除用户资料失败: userId={}", userId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public UserProfile initializeProfile(Long userId, String defaultNickname) {
        try {
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setNickname(defaultNickname != null ? defaultNickname : "用户" + userId);
            
            return createProfile(profile);
        } catch (Exception e) {
            log.error("初始化用户资料失败: userId={}", userId, e);
            throw new RuntimeException("初始化用户资料失败", e);
        }
    }

    @Override
    public List<UserProfile> batchInitializeProfiles(List<Long> userIds) {
        return userIds.stream()
                .map(userId -> initializeProfile(userId, null))
                .toList();
    }
}