package com.gig.collide.users.facade;

import com.gig.collide.api.user.UserProfileFacadeService;
import com.gig.collide.api.user.request.users.profile.*;
import com.gig.collide.api.user.response.users.profile.UserProfileResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.users.domain.service.UserProfileService;
import com.gig.collide.users.infrastructure.cache.UserCacheConstant;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.dubbo.config.annotation.DubboService;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.CacheType;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户资料门面服务实现 - 对应 t_user_profile 表
 * Dubbo独立微服务提供者 - 负责用户个人资料信息管理
 * 
 * @author GIG Team
 * @version 2.0.0 (Dubbo微服务版 - 6表架构)
 */
@Slf4j
@DubboService(version = "1.0.0", interfaceClass = UserProfileFacadeService.class)
@RequiredArgsConstructor
public class UserProfileFacadeServiceImpl implements UserProfileFacadeService {

    private final UserProfileService userProfileService;

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_LIST_CACHE)
    public Result<UserProfileResponse> createProfile(UserProfileCreateRequest request) {
        try {
            log.info("创建用户资料请求: userId={}", request.getUserId());
            
            UserProfile userProfile = new UserProfile();
            BeanUtils.copyProperties(request, userProfile);
            
            UserProfile savedProfile = userProfileService.createProfile(userProfile);
            UserProfileResponse response = convertToResponse(savedProfile);
            
            log.info("用户资料创建成功: userId={}", savedProfile.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建用户资料失败", e);
            return Result.error("PROFILE_CREATE_ERROR", "创建用户资料失败: " + e.getMessage());
        }
    }

    @Override
    @CacheUpdate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE,
                 key = UserCacheConstant.USER_PROFILE_DETAIL_KEY,
                 value = "#result.data")
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_LIST_CACHE)
    public Result<UserProfileResponse> updateProfile(UserProfileUpdateRequest request) {
        try {
            log.info("更新用户资料请求: userId={}", request.getUserId());
            
            UserProfile userProfile = userProfileService.getProfileByUserId(request.getUserId());
            if (userProfile == null) {
                return Result.error("PROFILE_NOT_FOUND", "用户资料不存在");
            }
            
            BeanUtils.copyProperties(request, userProfile);
            UserProfile updatedProfile = userProfileService.updateProfile(userProfile);
            UserProfileResponse response = convertToResponse(updatedProfile);
            
            log.info("用户资料更新成功: userId={}", updatedProfile.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新用户资料失败", e);
            return Result.error("PROFILE_UPDATE_ERROR", "更新用户资料失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE,
            key = UserCacheConstant.USER_PROFILE_DETAIL_KEY,
            expire = UserCacheConstant.USER_PROFILE_DETAIL_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<UserProfileResponse> getProfileByUserId(Long userId) {
        try {
            log.debug("获取用户资料: userId={}", userId);
            
            UserProfile profile = userProfileService.getProfileByUserId(userId);
            if (profile == null) {
                return Result.error("PROFILE_NOT_FOUND", "用户资料不存在");
            }
            
            UserProfileResponse response = convertToResponse(profile);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询用户资料失败", e);
            return Result.error("PROFILE_NOT_FOUND","查询用户资料失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<UserProfileResponse>> batchGetProfiles(List<Long> userIds) {
        try {
            List<UserProfile> profiles = userProfileService.batchGetProfiles(userIds);
            List<UserProfileResponse> responses = profiles.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("批量查询用户资料失败", e);
            return Result.error("BATCH_GET_PROFILES_ERROR", "批量查询用户资料失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    public Result<Void> updateAvatar(Long userId, String avatarUrl) {
        try {
            boolean success = userProfileService.updateAvatar(userId, avatarUrl);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("AVATAR_UPDATE_ERROR", "更新头像失败");
            }
        } catch (Exception e) {
            log.error("更新头像失败", e);
            return Result.error("AVATAR_UPDATE_ERROR", "更新头像失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    public Result<Void> updateNickname(Long userId, String nickname) {
        try {
            boolean success = userProfileService.updateNickname(userId, nickname);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("NICKNAME_UPDATE_ERROR", "更新昵称失败：昵称已被使用");
            }
        } catch (Exception e) {
            log.error("更新昵称失败", e);
            return Result.error("NICKNAME_UPDATE_ERROR", "更新昵称失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    public Result<Void> updateBirthday(Long userId, LocalDate birthday) {
        try {
            boolean success = userProfileService.updateBirthday(userId, birthday);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("BIRTHDAY_UPDATE_ERROR", "更新生日失败");
            }
        } catch (Exception e) {
            log.error("更新生日失败", e);
            return Result.error("BIRTHDAY_UPDATE_ERROR", "更新生日失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    public Result<Void> updateGender(Long userId, Integer gender) {
        try {
            boolean success = userProfileService.updateGender(userId, gender);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("GENDER_UPDATE_ERROR", "更新性别失败");
            }
        } catch (Exception e) {
            log.error("更新性别失败", e);
            return Result.error("GENDER_UPDATE_ERROR", "更新性别失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    public Result<Void> updateLocation(Long userId, String location) {
        try {
            boolean success = userProfileService.updateLocation(userId, location);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("LOCATION_UPDATE_ERROR", "更新所在地失败");
            }
        } catch (Exception e) {
            log.error("更新所在地失败", e);
            return Result.error("LOCATION_UPDATE_ERROR", "更新所在地失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    public Result<Void> updateBio(Long userId, String bio) {
        try {
            boolean success = userProfileService.updateBio(userId, bio);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("BIO_UPDATE_ERROR", "更新个人简介失败");
            }
        } catch (Exception e) {
            log.error("更新个人简介失败", e);
            return Result.error("BIO_UPDATE_ERROR", "更新个人简介失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkNicknameAvailable(String nickname, Long excludeUserId) {
        try {
            boolean available = userProfileService.checkNicknameAvailable(nickname, excludeUserId);
            return Result.success(available);
        } catch (Exception e) {
            log.error("检查昵称可用性失败", e);
            return Result.error("CHECK_NICKNAME_ERROR", "检查昵称可用性失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = UserCacheConstant.USER_PROFILE_LIST_CACHE,
            key = UserCacheConstant.USER_PROFILE_LIST_KEY,
            expire = UserCacheConstant.USER_PROFILE_LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES,
            cacheType = CacheType.BOTH)
    public Result<PageResponse<UserProfileResponse>> queryProfiles(UserProfileQueryRequest request) {
        try {
            log.debug("分页查询用户资料: currentPage={}, pageSize={}", request.getCurrentPage(), request.getPageSize());
            
            PageResponse<UserProfile> pageResult = userProfileService.queryProfiles(request);
            
            List<UserProfileResponse> responses = pageResult.getDatas().stream()
                    .map(this::convertToResponse)
                    .toList();
            
            PageResponse<UserProfileResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(pageResult.getCurrentPage());
            result.setPageSize(pageResult.getPageSize());
            result.setTotal(pageResult.getTotal());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("查询用户资料列表失败", e);
            return Result.error("PROFILE_LIST_QUERY_ERROR", "查询用户资料列表失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<UserProfileResponse>> searchByNickname(String nickname, Integer currentPage, Integer pageSize) {
        try {
            List<UserProfile> profiles = userProfileService.searchByNickname(nickname, currentPage, pageSize);
            List<UserProfileResponse> responses = profiles.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            // 构造分页结果
            PageResponse<UserProfileResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(currentPage);
            result.setPageSize(pageSize);
            result.setTotal((long) responses.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据昵称搜索用户失败", e);
            return Result.error("SEARCH_BY_NICKNAME_ERROR", "搜索用户失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<UserProfileResponse>> searchByLocation(String location, Integer currentPage, Integer pageSize) {
        try {
            List<UserProfile> profiles = userProfileService.getProfilesByLocation(location, currentPage, pageSize);
            List<UserProfileResponse> responses = profiles.stream()
                    .map(this::convertToResponse)
                    .toList();
            
            // 构造分页结果
            PageResponse<UserProfileResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setCurrentPage(currentPage);
            result.setPageSize(pageSize);
            result.setTotal((long) responses.size());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("根据所在地查询用户失败", e);
            return Result.error("SEARCH_BY_LOCATION_ERROR", "查询用户失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_DETAIL_CACHE)
    @CacheInvalidate(name = UserCacheConstant.USER_PROFILE_LIST_CACHE)
    public Result<Void> deleteProfile(Long userId) {
        try {
            log.info("删除用户资料请求: userId={}", userId);
            
            boolean success = userProfileService.deleteProfile(userId);
            if (success) {
                log.info("用户资料删除成功: userId={}", userId);
                return Result.success(null);
            } else {
                return Result.error("PROFILE_DELETE_ERROR", "删除用户资料失败");
            }
        } catch (Exception e) {
            log.error("删除用户资料失败", e);
            return Result.error("PROFILE_DELETE_ERROR", "删除用户资料失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkProfileExists(Long userId) {
        try {
            UserProfile profile = userProfileService.getProfileByUserId(userId);
            return Result.success(profile != null);
        } catch (Exception e) {
            log.error("检查用户资料是否存在失败", e);
            return Result.error("CHECK_PROFILE_EXISTS_ERROR", "检查资料存在性失败: " + e.getMessage());
        }
    }

    /**
     * 转换为用户资料响应对象
     */
    private UserProfileResponse convertToResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        BeanUtils.copyProperties(profile, response);
        return response;
    }
}