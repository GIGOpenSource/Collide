package com.gig.collide.users.domain.service;

import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserProfile;
import com.gig.collide.api.user.request.profile.UserProfileQueryRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * 用户资料领域服务接口 - 对应 t_user_profile 表
 * 负责用户个人资料信息管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
public interface UserProfileService {

    /**
     * 创建用户资料
     */
    UserProfile createProfile(UserProfile userProfile);

    /**
     * 更新用户资料
     */
    UserProfile updateProfile(UserProfile userProfile);

    /**
     * 根据用户ID查询用户资料
     */
    UserProfile getProfileByUserId(Long userId);

    /**
     * 批量查询用户资料
     */
    List<UserProfile> batchGetProfiles(List<Long> userIds);

    /**
     * 检查昵称是否可用
     */
    boolean checkNicknameAvailable(String nickname, Long excludeUserId);

    /**
     * 更新用户头像
     */
    boolean updateAvatar(Long userId, String avatar);

    /**
     * 更新用户昵称
     */
    boolean updateNickname(Long userId, String nickname);

    /**
     * 更新用户生日
     */
    boolean updateBirthday(Long userId, LocalDate birthday);

    /**
     * 更新用户性别
     */
    boolean updateGender(Long userId, Integer gender);

    /**
     * 更新用户所在地
     */
    boolean updateLocation(Long userId, String location);

    /**
     * 更新用户个人简介
     */
    boolean updateBio(Long userId, String bio);

    /**
     * 根据昵称模糊查询用户资料
     */
    List<UserProfile> searchByNickname(String nickname, int page, int size);

    /**
     * 根据所在地查询用户资料
     */
    List<UserProfile> getProfilesByLocation(String location, int page, int size);

    /**
     * 分页查询用户资料
     */
    PageResponse<UserProfile> queryProfiles(UserProfileQueryRequest request);

    /**
     * 检查用户是否有完整资料
     */
    boolean hasCompleteProfile(Long userId);

    /**
     * 获取资料完整度百分比
     */
    int getProfileCompleteness(Long userId);

    /**
     * 删除用户资料
     */
    boolean deleteProfile(Long userId);

    /**
     * 初始化用户资料（注册时调用）
     */
    UserProfile initializeProfile(Long userId, String defaultNickname);

    /**
     * 批量初始化用户资料
     */
    List<UserProfile> batchInitializeProfiles(List<Long> userIds);
}