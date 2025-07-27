package com.gig.collide.users.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.users.domain.entity.UserUnified;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * UserUnified数据访问映射器（去连表设计）
 * 所有查询都基于单表，无需JOIN查询
 *
 * @author GIG Team
 * @version 2.0 (重构版本)
 * @since 2024-01-01
 */
@Mapper
public interface UserUnifiedMapper extends BaseMapper<UserUnified> {

    /**
     * 根据用户名查询用户
     * 使用索引优化查询性能
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user_unified WHERE username = #{username} AND deleted = 0 LIMIT 1")
    UserUnified selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user_unified WHERE email = #{email} AND deleted = 0 LIMIT 1")
    UserUnified selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user_unified WHERE phone = #{phone} AND deleted = 0 LIMIT 1")
    UserUnified selectByPhone(@Param("phone") String phone);

    /**
     * 根据邀请码查询用户
     *
     * @param inviteCode 邀请码
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user_unified WHERE invite_code = #{inviteCode} AND deleted = 0 LIMIT 1")
    UserUnified selectByInviteCode(@Param("inviteCode") String inviteCode);

    /**
     * 搜索用户（单表搜索，无连表）
     * 支持用户名、昵称、简介的模糊搜索
     *
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 用户列表
     */
    @Select("""
        <script>
        SELECT *, 
               (CASE 
                 WHEN username LIKE CONCAT('%', #{keyword}, '%') THEN 10
                 WHEN nickname LIKE CONCAT('%', #{keyword}, '%') THEN 8
                 WHEN bio LIKE CONCAT('%', #{keyword}, '%') THEN 5
                 ELSE 1 END) AS relevance_score
        FROM t_user_unified 
        WHERE deleted = 0 AND status = 'active'
        <if test="keyword != null and keyword != ''">
          AND (username LIKE CONCAT('%', #{keyword}, '%') 
               OR nickname LIKE CONCAT('%', #{keyword}, '%') 
               OR bio LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY relevance_score DESC, create_time DESC
        LIMIT #{offset}, #{limit}
        </script>
        """)
    List<UserUnified> searchUsers(@Param("keyword") String keyword, 
                                 @Param("offset") int offset, 
                                 @Param("limit") int limit);

    /**
     * 统计搜索结果数量
     *
     * @param keyword 搜索关键词
     * @return 结果数量
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM t_user_unified 
        WHERE deleted = 0 AND status = 'active'
        <if test="keyword != null and keyword != ''">
          AND (username LIKE CONCAT('%', #{keyword}, '%') 
               OR nickname LIKE CONCAT('%', #{keyword}, '%') 
               OR bio LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        </script>
        """)
    long countSearchUsers(@Param("keyword") String keyword);

    /**
     * 根据博主状态查询用户列表
     *
     * @param bloggerStatus 博主状态
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 用户列表
     */
    @Select("""
        SELECT * FROM t_user_unified 
        WHERE deleted = 0 AND blogger_status = #{bloggerStatus}
        ORDER BY blogger_apply_time DESC
        LIMIT #{offset}, #{limit}
        """)
    List<UserUnified> selectByBloggerStatus(@Param("bloggerStatus") String bloggerStatus,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    /**
     * 统计各种博主状态的用户数量
     *
     * @param bloggerStatus 博主状态
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_user_unified WHERE deleted = 0 AND blogger_status = #{bloggerStatus}")
    long countByBloggerStatus(@Param("bloggerStatus") String bloggerStatus);

    /**
     * 根据邀请人ID查询被邀请用户列表
     *
     * @param inviterId 邀请人ID
     * @param offset 偏移量
     * @param limit 限制条数
     * @return 被邀请用户列表
     */
    @Select("""
        SELECT * FROM t_user_unified 
        WHERE deleted = 0 AND inviter_id = #{inviterId}
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
        """)
    List<UserUnified> selectByInviterId(@Param("inviterId") Long inviterId,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    /**
     * 获取邀请排行榜（根据邀请人数排序）
     *
     * @param limit 限制条数
     * @return 邀请排行榜
     */
    @Select("""
        SELECT * FROM t_user_unified 
        WHERE deleted = 0 AND invited_count > 0
        ORDER BY invited_count DESC, create_time ASC
        LIMIT #{limit}
        """)
    List<UserUnified> getInviteRanking(@Param("limit") int limit);

    /**
     * 更新用户统计字段
     * 避免并发问题，使用原子操作
     *
     * @param userId 用户ID
     * @param field 字段名
     * @param increment 增量
     * @return 影响行数
     */
    @Update("""
        <script>
        UPDATE t_user_unified 
        SET ${field} = ${field} + #{increment}, 
            update_time = NOW()
        WHERE id = #{userId} AND deleted = 0
        </script>
        """)
    int updateStatisticsField(@Param("userId") Long userId,
                             @Param("field") String field,
                             @Param("increment") long increment);

    /**
     * 批量更新用户统计字段（性能优化）
     *
     * @param userIds 用户ID列表
     * @param field 字段名
     * @param increment 增量
     * @return 影响行数
     */
    @Update("""
        <script>
        UPDATE t_user_unified 
        SET ${field} = ${field} + #{increment}, 
            update_time = NOW()
        WHERE deleted = 0 AND id IN
        <foreach item="userId" collection="userIds" open="(" separator="," close=")">
            #{userId}
        </foreach>
        </script>
        """)
    int batchUpdateStatisticsField(@Param("userIds") List<Long> userIds,
                                  @Param("field") String field,
                                  @Param("increment") long increment);

    /**
     * 获取用户关键信息（用于缓存优化）
     * 只返回必要字段，减少网络传输
     *
     * @param userId 用户ID
     * @return 用户关键信息
     */
    @Select("""
        SELECT id, username, nickname, avatar, role, status, 
               follower_count, following_count, content_count, like_count,
               blogger_status, vip_expire_time, last_login_time
        FROM t_user_unified 
        WHERE id = #{userId} AND deleted = 0
        """)
    UserUnified selectKeyInfoById(@Param("userId") Long userId);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM t_user_unified WHERE username = #{username} AND deleted = 0")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM t_user_unified WHERE email = #{email} AND deleted = 0")
    boolean existsByEmail(@Param("email") String email);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM t_user_unified WHERE phone = #{phone} AND deleted = 0")
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 检查邀请码是否存在
     *
     * @param inviteCode 邀请码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM t_user_unified WHERE invite_code = #{inviteCode} AND deleted = 0")
    boolean existsByInviteCode(@Param("inviteCode") String inviteCode);

    /**
     * 获取活跃用户统计（最近登录的用户）
     *
     * @param days 天数
     * @return 活跃用户数
     */
    @Select("""
        SELECT COUNT(*) FROM t_user_unified 
        WHERE deleted = 0 AND status = 'active'
        AND last_login_time >= DATE_SUB(NOW(), INTERVAL #{days} DAY)
        """)
    long countActiveUsers(@Param("days") int days);

    /**
     * 获取用户统计信息（全局统计）
     *
     * @return 统计信息Map
     */
    @Select("""
        SELECT 
            COUNT(*) as total_users,
            COUNT(CASE WHEN status = 'active' THEN 1 END) as active_users,
            COUNT(CASE WHEN role = 'blogger' THEN 1 END) as blogger_users,
            COUNT(CASE WHEN role = 'vip' THEN 1 END) as vip_users,
            COUNT(CASE WHEN blogger_status = 'applying' THEN 1 END) as applying_bloggers
        FROM t_user_unified 
        WHERE deleted = 0
        """)
    java.util.Map<String, Long> getUserStatistics();

    // ================================ 幂等性操作方法 ================================

    /**
     * 幂等性更新用户状态
     * 使用乐观锁机制，确保状态更新的幂等性
     *
     * @param userId 用户ID
     * @param expectedStatus 期望的当前状态
     * @param newStatus 新状态
     * @param version 当前版本号
     * @return 影响行数（1表示成功，0表示失败）
     */
    @Update("UPDATE t_user_unified SET status = #{newStatus}, version = version + 1, update_time = NOW() " +
            "WHERE id = #{userId} AND status = #{expectedStatus} AND version = #{version} AND deleted = 0")
    int updateStatusIdempotent(@Param("userId") Long userId,
                              @Param("expectedStatus") String expectedStatus,
                              @Param("newStatus") String newStatus,
                              @Param("version") Integer version);

    /**
     * 幂等性更新用户角色
     * 使用乐观锁机制，确保角色更新的幂等性
     *
     * @param userId 用户ID
     * @param expectedRole 期望的当前角色
     * @param newRole 新角色
     * @param version 当前版本号
     * @return 影响行数（1表示成功，0表示失败）
     */
    @Update("UPDATE t_user_unified SET role = #{newRole}, version = version + 1, update_time = NOW() " +
            "WHERE id = #{userId} AND role = #{expectedRole} AND version = #{version} AND deleted = 0")
    int updateRoleIdempotent(@Param("userId") Long userId,
                            @Param("expectedRole") String expectedRole,
                            @Param("newRole") String newRole,
                            @Param("version") Integer version);

    /**
     * 幂等性更新博主认证状态
     * 使用乐观锁机制，确保博主状态更新的幂等性
     *
     * @param userId 用户ID
     * @param expectedBloggerStatus 期望的当前博主状态
     * @param newBloggerStatus 新博主状态
     * @param version 当前版本号
     * @return 影响行数（1表示成功，0表示失败）
     */
    @Update("UPDATE t_user_unified SET blogger_status = #{newBloggerStatus}, version = version + 1, update_time = NOW() " +
            "WHERE id = #{userId} AND blogger_status = #{expectedBloggerStatus} AND version = #{version} AND deleted = 0")
    int updateBloggerStatusIdempotent(@Param("userId") Long userId,
                                     @Param("expectedBloggerStatus") String expectedBloggerStatus,
                                     @Param("newBloggerStatus") String newBloggerStatus,
                                     @Param("version") Integer version);

    /**
     * 根据用户ID查询（加悲观锁）
     * 用于需要排他性访问的场景
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user_unified WHERE id = #{userId} AND deleted = 0 FOR UPDATE")
    UserUnified selectByIdForUpdate(@Param("userId") Long userId);
} 