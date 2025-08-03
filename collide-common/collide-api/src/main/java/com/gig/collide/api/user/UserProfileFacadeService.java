package com.gig.collide.api.user;

import com.gig.collide.api.user.request.profile.UserProfileCreateRequest;
import com.gig.collide.api.user.request.profile.UserProfileUpdateRequest;
import com.gig.collide.api.user.request.profile.UserProfileQueryRequest;
import com.gig.collide.api.user.response.profile.UserProfileResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

/**
 * 用户资料服务接口 - 对应 t_user_profile 表
 * 负责用户个人资料信息管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserProfileFacadeService {

    /**
     * 创建用户资料
     * 
     * @param request 用户资料创建请求
     * @return 创建结果
     */
    Result<UserProfileResponse> createProfile(UserProfileCreateRequest request);

    /**
     * 更新用户资料
     * 
     * @param request 用户资料更新请求
     * @return 更新结果
     */
    Result<UserProfileResponse> updateProfile(UserProfileUpdateRequest request);

    /**
     * 根据用户ID查询用户资料
     * 
     * @param userId 用户ID
     * @return 用户资料信息
     */
    Result<UserProfileResponse> getProfileByUserId(Long userId);

    /**
     * 批量查询用户资料
     * 
     * @param userIds 用户ID列表
     * @return 用户资料列表
     */
    Result<java.util.List<UserProfileResponse>> batchGetProfiles(java.util.List<Long> userIds);

    /**
     * 更新用户头像
     * 
     * @param userId 用户ID
     * @param avatarUrl 头像URL
     * @return 更新结果
     */
    Result<Void> updateAvatar(Long userId, String avatarUrl);

    /**
     * 更新用户昵称
     * 
     * @param userId 用户ID
     * @param nickname 昵称
     * @return 更新结果
     */
    Result<Void> updateNickname(Long userId, String nickname);

    /**
     * 更新用户生日
     * 
     * @param userId 用户ID
     * @param birthday 生日
     * @return 更新结果
     */
    Result<Void> updateBirthday(Long userId, java.time.LocalDate birthday);

    /**
     * 更新用户性别
     * 
     * @param userId 用户ID
     * @param gender 性别：0-unknown, 1-male, 2-female
     * @return 更新结果
     */
    Result<Void> updateGender(Long userId, Integer gender);

    /**
     * 更新用户所在地
     * 
     * @param userId 用户ID
     * @param location 所在地
     * @return 更新结果
     */
    Result<Void> updateLocation(Long userId, String location);

    /**
     * 更新用户个人简介
     * 
     * @param userId 用户ID
     * @param bio 个人简介
     * @return 更新结果
     */
    Result<Void> updateBio(Long userId, String bio);

    /**
     * 检查昵称是否可用
     * 
     * @param nickname 昵称
     * @param excludeUserId 排除的用户ID（用于更新时检查）
     * @return 是否可用
     */
    Result<Boolean> checkNicknameAvailable(String nickname, Long excludeUserId);

    /**
     * 分页查询用户资料
     * 
     * @param request 查询请求
     * @return 分页用户资料列表
     */
    Result<PageResponse<UserProfileResponse>> queryProfiles(UserProfileQueryRequest request);

    /**
     * 根据昵称模糊查询用户资料
     * 
     * @param nickname 昵称关键词
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 用户资料列表
     */
    Result<PageResponse<UserProfileResponse>> searchByNickname(String nickname, Integer currentPage, Integer pageSize);

    /**
     * 根据所在地查询用户资料
     * 
     * @param location 所在地
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 用户资料列表
     */
    Result<PageResponse<UserProfileResponse>> searchByLocation(String location, Integer currentPage, Integer pageSize);

    /**
     * 删除用户资料
     * 
     * @param userId 用户ID
     * @return 删除结果
     */
    Result<Void> deleteProfile(Long userId);

    /**
     * 检查用户资料是否存在
     * 
     * @param userId 用户ID
     * @return 是否存在
     */
    Result<Boolean> checkProfileExists(Long userId);
}