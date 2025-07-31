package com.gig.collide.users.controller;

import com.gig.collide.api.user.request.UserBlockCreateRequest;
import com.gig.collide.api.user.request.UserBlockQueryRequest;
import com.gig.collide.api.user.response.UserBlockResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.users.domain.entity.UserBlock;
import com.gig.collide.users.domain.service.UserBlockService;
import com.gig.collide.users.domain.service.UserService;
import com.gig.collide.users.domain.entity.User;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 用户拉黑控制器 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0 (简洁版)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users/blocks")
public class UserBlockController {

    @Autowired
    private UserBlockService userBlockService;

    @Autowired
    private UserService userService;

    /**
     * 拉黑用户
     */
    @PostMapping("/{userId}")
    public Result<UserBlockResponse> blockUser(@PathVariable Long userId, 
                                             @Valid @RequestBody UserBlockCreateRequest request) {
        try {
            log.info("拉黑用户请求: userId={}, request={}", userId, request);
            
            // 获取用户信息
            User user = userService.getUserById(userId);
            User blockedUser = userService.getUserById(request.getBlockedUserId());
            
            if (user == null || blockedUser == null) {
                return Result.error("USER_NOT_FOUND", "用户不存在");
            }
            
            // 执行拉黑操作
            UserBlock userBlock = userBlockService.blockUser(
                userId, 
                request.getBlockedUserId(), 
                user.getUsername(), 
                blockedUser.getUsername(), 
                request.getReason()
            );
            
            // 转换为响应对象
            UserBlockResponse response = new UserBlockResponse();
            BeanUtils.copyProperties(userBlock, response);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("拉黑用户失败", e);
            return Result.error("USER_BLOCK_ERROR", "拉黑用户失败: " + e.getMessage());
        }
    }

    /**
     * 取消拉黑
     */
    @DeleteMapping("/{userId}/{blockedUserId}")
    public Result<Void> unblockUser(@PathVariable Long userId, 
                                   @PathVariable Long blockedUserId) {
        try {
            log.info("取消拉黑请求: userId={}, blockedUserId={}", userId, blockedUserId);
            userBlockService.unblockUser(userId, blockedUserId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消拉黑失败", e);
            return Result.error("USER_UNBLOCK_ERROR", "取消拉黑失败: " + e.getMessage());
        }
    }

    /**
     * 检查拉黑状态
     */
    @GetMapping("/{userId}/{blockedUserId}/status")
    public Result<Boolean> checkBlockStatus(@PathVariable Long userId, 
                                           @PathVariable Long blockedUserId) {
        try {
            log.info("检查拉黑状态: userId={}, blockedUserId={}", userId, blockedUserId);
            boolean isBlocked = userBlockService.isBlocked(userId, blockedUserId);
            return Result.success(isBlocked);
        } catch (Exception e) {
            log.error("检查拉黑状态失败", e);
            return Result.error("CHECK_BLOCK_STATUS_ERROR", "检查拉黑状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取拉黑关系详情
     */
    @GetMapping("/{userId}/{blockedUserId}")
    public Result<UserBlockResponse> getBlockRelation(@PathVariable Long userId, 
                                                     @PathVariable Long blockedUserId) {
        try {
            log.info("获取拉黑关系: userId={}, blockedUserId={}", userId, blockedUserId);
            UserBlock userBlock = userBlockService.getBlockRelation(userId, blockedUserId);
            
            if (userBlock == null) {
                return Result.error("BLOCK_RELATION_NOT_FOUND", "拉黑关系不存在");
            }
            
            UserBlockResponse response = new UserBlockResponse();
            BeanUtils.copyProperties(userBlock, response);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取拉黑关系失败", e);
            return Result.error("GET_BLOCK_RELATION_ERROR", "获取拉黑关系失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户拉黑列表
     */
    @GetMapping("/{userId}")
    public Result<PageResponse<UserBlockResponse>> getUserBlockList(@PathVariable Long userId,
                                                                   @RequestParam(defaultValue = "1") Integer currentPage,
                                                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            log.info("获取用户拉黑列表: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
            PageResponse<UserBlock> pageResponse = userBlockService.getUserBlockList(userId, currentPage, pageSize);
            
            // 转换为响应对象
            PageResponse<UserBlockResponse> result = new PageResponse<>();
            result.setTotal(pageResponse.getTotal());
            result.setCurrentPage(pageResponse.getCurrentPage());
            result.setPageSize(pageResponse.getPageSize());
            
            if (pageResponse.getDatas() != null) {
                result.setDatas(pageResponse.getDatas().stream()
                    .map(userBlock -> {
                        UserBlockResponse response = new UserBlockResponse();
                        BeanUtils.copyProperties(userBlock, response);
                        return response;
                    })
                    .toList());
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户拉黑列表失败", e);
            return Result.error("GET_USER_BLOCK_LIST_ERROR", "获取用户拉黑列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询拉黑记录
     */
    @GetMapping
    public Result<PageResponse<UserBlockResponse>> queryBlocks(UserBlockQueryRequest request) {
        try {
            log.info("分页查询拉黑记录: {}", request);
            PageResponse<UserBlock> pageResponse = userBlockService.queryBlocks(request);
            
            // 转换为响应对象
            PageResponse<UserBlockResponse> result = new PageResponse<>();
            result.setTotal(pageResponse.getTotal());
            result.setCurrentPage(pageResponse.getCurrentPage());
            result.setPageSize(pageResponse.getPageSize());
            
            if (pageResponse.getDatas() != null) {
                result.setDatas(pageResponse.getDatas().stream()
                    .map(userBlock -> {
                        UserBlockResponse response = new UserBlockResponse();
                        BeanUtils.copyProperties(userBlock, response);
                        return response;
                    })
                    .toList());
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询拉黑记录失败", e);
            return Result.error("QUERY_BLOCKS_ERROR", "分页查询拉黑记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询拉黑记录
     */
    @GetMapping("/detail/{id}")
    public Result<UserBlockResponse> getBlockById(@PathVariable Long id) {
        try {
            log.info("根据ID查询拉黑记录: id={}", id);
            UserBlock userBlock = userBlockService.getBlockById(id);
            
            if (userBlock == null) {
                return Result.error("BLOCK_NOT_FOUND", "拉黑记录不存在");
            }
            
            UserBlockResponse response = new UserBlockResponse();
            BeanUtils.copyProperties(userBlock, response);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据ID查询拉黑记录失败", e);
            return Result.error("GET_BLOCK_BY_ID_ERROR", "查询拉黑记录失败: " + e.getMessage());
        }
    }

    /**
     * 统计用户拉黑数量
     */
    @GetMapping("/{userId}/count")
    public Result<Long> countUserBlocks(@PathVariable Long userId) {
        try {
            log.info("统计用户拉黑数量: userId={}", userId);
            Long count = userBlockService.countUserBlocks(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户拉黑数量失败", e);
            return Result.error("COUNT_USER_BLOCKS_ERROR", "统计失败: " + e.getMessage());
        }
    }
}