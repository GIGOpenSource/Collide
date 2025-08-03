package com.gig.collide.users.infrastructure.mapper;

import com.gig.collide.users.domain.entity.UserBlock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户拉黑Mapper接口 - 简洁版
 * 基于简洁版t_user_block表设计，无连表操作
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Mapper
public interface UserBlockMapper {

    /**
     * 根据ID查询拉黑记录
     */
    UserBlock findById(@Param("id") Long id);

    /**
     * 查询拉黑关系
     */
    UserBlock findByUserAndBlocked(@Param("userId") Long userId, 
                                  @Param("blockedUserId") Long blockedUserId);

    /**
     * 查询用户的拉黑列表
     */
    List<UserBlock> findByUserId(@Param("userId") Long userId,
                                @Param("status") String status,
                                @Param("offset") Integer offset,
                                @Param("size") Integer size);

    /**
     * 查询被拉黑情况
     */
    List<UserBlock> findByBlockedUserId(@Param("blockedUserId") Long blockedUserId,
                                       @Param("status") String status,
                                       @Param("offset") Integer offset,
                                       @Param("size") Integer size);

    /**
     * 统计用户拉黑数量
     */
    Long countByUserId(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 统计被拉黑数量
     */
    Long countByBlockedUserId(@Param("blockedUserId") Long blockedUserId, @Param("status") String status);

    /**
     * 检查拉黑状态
     */
    Integer checkBlockStatus(@Param("userId") Long userId, 
                            @Param("blockedUserId") Long blockedUserId,
                            @Param("status") String status);

    /**
     * 分页查询拉黑记录（支持条件查询）
     */
    List<UserBlock> findBlocksByCondition(@Param("userId") Long userId,
                                         @Param("blockedUserId") Long blockedUserId,
                                         @Param("userUsername") String userUsername,
                                         @Param("blockedUsername") String blockedUsername,
                                         @Param("status") String status,
                                         @Param("offset") Integer offset,
                                         @Param("size") Integer size);

    /**
     * 统计拉黑记录数量（支持条件查询）
     */
    Long countBlocksByCondition(@Param("userId") Long userId,
                               @Param("blockedUserId") Long blockedUserId,
                               @Param("userUsername") String userUsername,
                               @Param("blockedUsername") String blockedUsername,
                               @Param("status") String status);

    /**
     * 插入拉黑记录
     */
    int insert(UserBlock userBlock);

    /**
     * 更新拉黑记录
     */
    int updateById(UserBlock userBlock);

    /**
     * 更新拉黑状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 取消拉黑（更新状态为cancelled）
     */
    int cancelBlock(@Param("userId") Long userId, 
                   @Param("blockedUserId") Long blockedUserId);

    /**
     * 删除拉黑记录（物理删除，慎用）
     */
    int deleteById(@Param("id") Long id);
}