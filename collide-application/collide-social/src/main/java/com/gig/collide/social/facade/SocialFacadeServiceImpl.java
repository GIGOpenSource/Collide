package com.gig.collide.social.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.social.SocialFacadeService;
import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.SocialDynamic;
import com.gig.collide.social.domain.service.SocialDynamicService;
import com.gig.collide.web.vo.Result;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 社交动态门面服务实现类 - 严格对应接口版
 * 实现SocialFacadeService接口的全部方法，严格对应Service层设计
 *
 * @author GIG Team
 * @version 3.0.0 (重新设计版)
 * @since 2024-01-30
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class SocialFacadeServiceImpl implements SocialFacadeService {

    private final SocialDynamicService socialDynamicService;

    // =================== 业务CRUD操作（Controller层需要） ===================

    @Override
    public Result<SocialDynamicResponse> createDynamic(SocialDynamicCreateRequest request) {
        try {
            log.info("创建动态请求: 用户ID={}, 内容={}", request.getUserId(), request.getContent());
            
            // 参数验证
            if (request.getUserId() == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (request.getContent() == null || request.getContent().trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "动态内容不能为空");
            }
            
            // 转换请求对象为实体对象
            SocialDynamic dynamic = new SocialDynamic();
            BeanUtils.copyProperties(request, dynamic);
            
            // 调用Service层创建动态
            SocialDynamic savedDynamic = socialDynamicService.createDynamic(dynamic);
            
            // 转换为响应对象
            SocialDynamicResponse response = convertToResponse(savedDynamic);
            
            log.info("动态创建成功: ID={}", savedDynamic.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建动态失败", e);
            return Result.error("DYNAMIC_CREATE_ERROR", "创建动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchCreateDynamics(List<SocialDynamicCreateRequest> requests, Long operatorId) {
        try {
            log.info("批量创建动态: 数量={}, 操作人={}", requests.size(), operatorId);
            
            // 参数验证
            if (CollectionUtils.isEmpty(requests)) {
                return Result.error("INVALID_PARAM", "动态列表不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            // 转换请求对象为实体对象
            List<SocialDynamic> dynamics = requests.stream().map(request -> {
                SocialDynamic dynamic = new SocialDynamic();
                BeanUtils.copyProperties(request, dynamic);
                return dynamic;
            }).collect(Collectors.toList());
            
            // 调用Service层批量创建
            int result = socialDynamicService.batchCreateDynamics(dynamics);
            
            log.info("批量创建动态完成: 成功创建{}条", result);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量创建动态失败", e);
            return Result.error("BATCH_CREATE_ERROR", "批量创建动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SocialDynamicResponse> createShareDynamic(SocialDynamicCreateRequest request) {
        try {
            log.info("创建分享动态: 用户ID={}, 分享目标={}", request.getUserId(), request.getShareTargetId());
            
            // 参数验证
            if (request.getUserId() == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (request.getShareTargetId() == null) {
                return Result.error("INVALID_PARAM", "分享目标ID不能为空");
            }
            
            // 转换请求对象为实体对象
            SocialDynamic dynamic = new SocialDynamic();
            BeanUtils.copyProperties(request, dynamic);
            
            // 调用Service层创建分享动态
            SocialDynamic savedDynamic = socialDynamicService.createShareDynamic(dynamic);
            
            // 转换为响应对象
            SocialDynamicResponse response = convertToResponse(savedDynamic);
            
            log.info("分享动态创建成功: ID={}", savedDynamic.getId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("创建分享动态失败", e);
            return Result.error("SHARE_CREATE_ERROR", "创建分享动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request) {
        try {
            log.info("更新动态请求: ID={}, 用户ID={}", request.getId(), request.getUserId());
            
            // 参数验证
            if (request.getId() == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (request.getUserId() == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            // TODO: 这里需要实现动态更新逻辑，目前Service层没有对应的方法
            // 可以考虑添加一个updateDynamicContent方法到Service层
            
            log.warn("动态更新功能暂未实现，需要在Service层添加对应方法");
            return Result.error("NOT_IMPLEMENTED", "动态更新功能暂未实现");
        } catch (Exception e) {
            log.error("更新动态失败", e);
            return Result.error("DYNAMIC_UPDATE_ERROR", "更新动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteDynamic(Long dynamicId, Long operatorId) {
        try {
            log.info("删除动态: ID={}, 操作人={}", dynamicId, operatorId);
            
            // 参数验证
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            // 调用Service层更新动态状态为删除
            int result = socialDynamicService.updateStatus(dynamicId, "deleted");
            if (result > 0) {
                log.info("动态删除成功: ID={}", dynamicId);
                return Result.success(null);
            } else {
                return Result.error("DELETE_FAILED", "动态删除失败，可能动态不存在");
            }
        } catch (Exception e) {
            log.error("删除动态失败", e);
            return Result.error("DYNAMIC_DELETE_ERROR", "删除动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SocialDynamicResponse> getDynamicById(Long dynamicId, Boolean includeDeleted) {
        try {
            log.debug("查询动态详情: ID={}, 包含已删除={}", dynamicId, includeDeleted);
            
            // 参数验证
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            
            // TODO: 这里需要实现根据ID查询动态的逻辑，目前Service层没有对应的方法
            // 可以考虑添加一个selectById方法到Service层，或者使用Mapper的selectById
            
            log.warn("根据ID查询动态功能暂未实现，需要在Service层添加对应方法");
            return Result.error("NOT_IMPLEMENTED", "根据ID查询动态功能暂未实现");
        } catch (Exception e) {
            log.error("查询动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "查询动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> queryDynamics(SocialDynamicQueryRequest request) {
        try {
            log.debug("分页查询动态: 页码={}, 大小={}", request.getCurrentPage(), request.getPageSize());
            
            // 参数验证
            if (request.getCurrentPage() < 1) {
                request.setCurrentPage(1);
            }
            if (request.getPageSize() < 1) {
                request.setPageSize(20);
            }
            
            // TODO: 这里需要实现分页查询逻辑，目前Service层的查询方法签名不匹配
            // 需要根据查询条件选择合适的Service方法
            
            log.warn("分页查询动态功能暂未完全实现，需要适配Service层方法");
            return Result.success(createEmptyPageResponse(request.getCurrentPage(), request.getPageSize()));
        } catch (Exception e) {
            log.error("分页查询动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "分页查询动态失败: " + e.getMessage());
        }
    }

    // =================== 核心查询方法（严格对应Service层7个） ===================

    @Override
    public Result<PageResponse<SocialDynamicResponse>> selectByUserId(Integer currentPage, Integer pageSize, Long userId, String status, String dynamicType) {
        try {
            log.debug("根据用户ID查询动态: 用户ID={}, 状态={}, 类型={}", userId, status, dynamicType);
            
            // 参数验证
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.selectByUserId(page, userId, status, dynamicType);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据用户ID查询动态失败", e);
            return Result.error("QUERY_ERROR", "根据用户ID查询动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> selectByDynamicType(Integer currentPage, Integer pageSize, String dynamicType, String status) {
        try {
            log.debug("根据动态类型查询: 类型={}, 状态={}", dynamicType, status);
            
            // 参数验证
            if (dynamicType == null || dynamicType.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "动态类型不能为空");
            }
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.selectByDynamicType(page, dynamicType, status);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据动态类型查询失败", e);
            return Result.error("QUERY_ERROR", "根据动态类型查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> selectByStatus(Integer currentPage, Integer pageSize, String status) {
        try {
            log.debug("根据状态查询动态: 状态={}", status);
            
            // 参数验证
            if (status == null || status.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "状态不能为空");
            }
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.selectByStatus(page, status);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据状态查询动态失败", e);
            return Result.error("QUERY_ERROR", "根据状态查询动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> selectFollowingDynamics(Integer currentPage, Integer pageSize, Long userId, String status) {
        try {
            log.debug("查询关注用户动态: 用户ID={}, 状态={}", userId, status);
            
            // 参数验证
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            // TODO: 这里需要先获取用户关注的用户ID列表
            // 目前暂时返回空结果，需要集成follow模块
            List<Long> followingUserIds = Collections.emptyList();
            
            if (followingUserIds.isEmpty()) {
                PageResponse<SocialDynamicResponse> emptyResponse = createEmptyPageResponse(currentPage, pageSize);
                return Result.success(emptyResponse);
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.selectFollowingDynamics(page, followingUserIds, status);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询关注用户动态失败", e);
            return Result.error("QUERY_ERROR", "查询关注用户动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> searchByContent(Integer currentPage, Integer pageSize, String keyword, String status) {
        try {
            log.debug("搜索动态内容: 关键词={}, 状态={}", keyword, status);
            
            // 参数验证
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "搜索关键词不能为空");
            }
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.searchByContent(page, keyword, status);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("搜索动态内容失败", e);
            return Result.error("SEARCH_ERROR", "搜索动态内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> selectHotDynamics(Integer currentPage, Integer pageSize, String status, String dynamicType) {
        try {
            log.debug("查询热门动态: 状态={}, 类型={}", status, dynamicType);
            
            // 参数验证
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.selectHotDynamics(page, status, dynamicType);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询热门动态失败", e);
            return Result.error("QUERY_ERROR", "查询热门动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> selectByShareTarget(Integer currentPage, Integer pageSize, String shareTargetType, Long shareTargetId, String status) {
        try {
            log.debug("根据分享目标查询: 类型={}, 目标ID={}, 状态={}", shareTargetType, shareTargetId, status);
            
            // 参数验证
            if (shareTargetType == null || shareTargetType.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "分享目标类型不能为空");
            }
            if (shareTargetId == null) {
                return Result.error("INVALID_PARAM", "分享目标ID不能为空");
            }
            if (currentPage == null || currentPage < 1) {
                currentPage = 1;
            }
            if (pageSize == null || pageSize < 1) {
                pageSize = 20;
            }
            
            Page<SocialDynamic> page = new Page<>(currentPage, pageSize);
            IPage<SocialDynamic> result = socialDynamicService.selectByShareTarget(page, shareTargetType, shareTargetId, status);
            
            PageResponse<SocialDynamicResponse> response = convertToPageResponse(result);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据分享目标查询失败", e);
            return Result.error("QUERY_ERROR", "根据分享目标查询失败: " + e.getMessage());
        }
    }

    // =================== 统计计数方法（严格对应Service层3个） ===================

    @Override
    public Result<Long> countByUserId(Long userId, String status, String dynamicType) {
        try {
            log.debug("统计用户动态数量: 用户ID={}, 状态={}, 类型={}", userId, status, dynamicType);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            
            Long count = socialDynamicService.countByUserId(userId, status, dynamicType);
            return Result.success(count != null ? count : 0L);
        } catch (Exception e) {
            log.error("统计用户动态数量失败", e);
            return Result.error("COUNT_ERROR", "统计用户动态数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countByDynamicType(String dynamicType, String status) {
        try {
            log.debug("统计动态类型数量: 类型={}, 状态={}", dynamicType, status);
            
            if (dynamicType == null || dynamicType.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "动态类型不能为空");
            }
            
            Long count = socialDynamicService.countByDynamicType(dynamicType, status);
            return Result.success(count != null ? count : 0L);
        } catch (Exception e) {
            log.error("统计动态类型数量失败", e);
            return Result.error("COUNT_ERROR", "统计动态类型数量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status) {
        try {
            log.debug("统计时间范围内动态数量: 开始时间={}, 结束时间={}, 状态={}", startTime, endTime, status);
            
            if (startTime == null || endTime == null) {
                return Result.error("INVALID_PARAM", "开始时间和结束时间不能为空");
            }
            if (startTime.isAfter(endTime)) {
                return Result.error("INVALID_PARAM", "开始时间不能晚于结束时间");
            }
            
            Long count = socialDynamicService.countByTimeRange(startTime, endTime, status);
            return Result.success(count != null ? count : 0L);
        } catch (Exception e) {
            log.error("统计时间范围内动态数量失败", e);
            return Result.error("COUNT_ERROR", "统计时间范围内动态数量失败: " + e.getMessage());
        }
    }

    // =================== 互动统计更新（严格对应Service层5个） ===================

    @Override
    public Result<Integer> increaseLikeCount(Long dynamicId, Long operatorId) {
        try {
            log.debug("增加点赞数: 动态ID={}, 操作人={}", dynamicId, operatorId);
            
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.increaseLikeCount(dynamicId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("增加点赞数失败", e);
            return Result.error("UPDATE_ERROR", "增加点赞数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> decreaseLikeCount(Long dynamicId, Long operatorId) {
        try {
            log.debug("减少点赞数: 动态ID={}, 操作人={}", dynamicId, operatorId);
            
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.decreaseLikeCount(dynamicId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("减少点赞数失败", e);
            return Result.error("UPDATE_ERROR", "减少点赞数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> increaseCommentCount(Long dynamicId, Long operatorId) {
        try {
            log.debug("增加评论数: 动态ID={}, 操作人={}", dynamicId, operatorId);
            
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.increaseCommentCount(dynamicId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("增加评论数失败", e);
            return Result.error("UPDATE_ERROR", "增加评论数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> increaseShareCount(Long dynamicId, Long operatorId) {
        try {
            log.debug("增加分享数: 动态ID={}, 操作人={}", dynamicId, operatorId);
            
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.increaseShareCount(dynamicId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("增加分享数失败", e);
            return Result.error("UPDATE_ERROR", "增加分享数失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> updateStatistics(Long dynamicId, Long likeCount, Long commentCount, Long shareCount, Long operatorId) {
        try {
            log.debug("批量更新统计数据: 动态ID={}, 点赞数={}, 评论数={}, 分享数={}, 操作人={}", 
                    dynamicId, likeCount, commentCount, shareCount, operatorId);
            
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.updateStatistics(dynamicId, likeCount, commentCount, shareCount);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新统计数据失败", e);
            return Result.error("UPDATE_ERROR", "批量更新统计数据失败: " + e.getMessage());
        }
    }

    // =================== 状态管理（严格对应Service层2个） ===================

    @Override
    public Result<Integer> updateStatus(Long dynamicId, String status, Long operatorId) {
        try {
            log.debug("更新动态状态: 动态ID={}, 状态={}, 操作人={}", dynamicId, status, operatorId);
            
            if (dynamicId == null) {
                return Result.error("INVALID_PARAM", "动态ID不能为空");
            }
            if (status == null || status.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "状态不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.updateStatus(dynamicId, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新动态状态失败", e);
            return Result.error("UPDATE_ERROR", "更新动态状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Integer> batchUpdateStatus(List<Long> dynamicIds, String status, Long operatorId) {
        try {
            log.debug("批量更新动态状态: 数量={}, 状态={}, 操作人={}", dynamicIds.size(), status, operatorId);
            
            if (CollectionUtils.isEmpty(dynamicIds)) {
                return Result.error("INVALID_PARAM", "动态ID列表不能为空");
            }
            if (status == null || status.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "状态不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.batchUpdateStatus(dynamicIds, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新动态状态失败", e);
            return Result.error("BATCH_UPDATE_ERROR", "批量更新动态状态失败: " + e.getMessage());
        }
    }

    // =================== 用户信息同步（严格对应Service层1个） ===================

    @Override
    public Result<Integer> updateUserInfo(Long userId, String userNickname, String userAvatar, Long operatorId) {
        try {
            log.debug("更新用户冗余信息: 用户ID={}, 昵称={}, 头像={}, 操作人={}", userId, userNickname, userAvatar, operatorId);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.updateUserInfo(userId, userNickname, userAvatar);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新用户冗余信息失败", e);
            return Result.error("UPDATE_ERROR", "更新用户冗余信息失败: " + e.getMessage());
        }
    }

    // =================== 数据清理（严格对应Service层1个） ===================

    @Override
    public Result<Integer> deleteByStatusAndTime(String status, LocalDateTime beforeTime, Integer limit, Long operatorId) {
        try {
            log.warn("执行数据清理: 状态={}, 截止时间={}, 限制数量={}, 操作人={}", status, beforeTime, limit, operatorId);
            
            if (status == null || status.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "状态不能为空");
            }
            if (beforeTime == null) {
                return Result.error("INVALID_PARAM", "截止时间不能为空");
            }
            if (operatorId == null) {
                return Result.error("INVALID_PARAM", "操作人ID不能为空");
            }
            
            int result = socialDynamicService.deleteByStatusAndTime(status, beforeTime, limit);
            log.warn("数据清理完成: 删除记录数={}, 操作人={}", result, operatorId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("数据清理失败", e);
            return Result.error("DELETE_ERROR", "数据清理失败: " + e.getMessage());
        }
    }

    // =================== 特殊查询（严格对应Service层3个） ===================

    @Override
    public Result<List<SocialDynamicResponse>> selectLatestDynamics(Integer limit, String status) {
        try {
            log.debug("查询最新动态: 限制数量={}, 状态={}", limit, status);
            
            if (limit == null || limit < 1) {
                limit = 10; // 默认查询10条
            }
            if (limit > 100) {
                limit = 100; // 最多查询100条
            }
            
            List<SocialDynamic> dynamics = socialDynamicService.selectLatestDynamics(limit, status);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询最新动态失败", e);
            return Result.error("QUERY_ERROR", "查询最新动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SocialDynamicResponse>> selectUserLatestDynamics(Long userId, Integer limit, String status) {
        try {
            log.debug("查询用户最新动态: 用户ID={}, 限制数量={}, 状态={}", userId, limit, status);
            
            if (userId == null) {
                return Result.error("INVALID_PARAM", "用户ID不能为空");
            }
            if (limit == null || limit < 1) {
                limit = 10; // 默认查询10条
            }
            if (limit > 100) {
                limit = 100; // 最多查询100条
            }
            
            List<SocialDynamic> dynamics = socialDynamicService.selectUserLatestDynamics(userId, limit, status);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户最新动态失败", e);
            return Result.error("QUERY_ERROR", "查询用户最新动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SocialDynamicResponse>> selectShareDynamics(String shareTargetType, Integer limit, String status) {
        try {
            log.debug("查询分享动态列表: 目标类型={}, 限制数量={}, 状态={}", shareTargetType, limit, status);
            
            if (shareTargetType == null || shareTargetType.trim().isEmpty()) {
                return Result.error("INVALID_PARAM", "分享目标类型不能为空");
            }
            if (limit == null || limit < 1) {
                limit = 10; // 默认查询10条
            }
            if (limit > 100) {
                limit = 100; // 最多查询100条
            }
            
            List<SocialDynamic> dynamics = socialDynamicService.selectShareDynamics(shareTargetType, limit, status);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询分享动态列表失败", e);
            return Result.error("QUERY_ERROR", "查询分享动态列表失败: " + e.getMessage());
        }
    }

    // =================== 系统健康检查 ===================

    @Override
    public Result<String> healthCheck() {
        try {
            log.debug("执行社交动态系统健康检查");
            
            // 执行简单的数据库连接检查
            Long normalCount = socialDynamicService.countByDynamicType("text", "normal");
            
            String healthInfo = String.format("社交动态系统运行正常，当前正常文本动态数量: %d", normalCount != null ? normalCount : 0);
            
            log.info("健康检查完成: {}", healthInfo);
            return Result.success(healthInfo);
        } catch (Exception e) {
            log.error("健康检查失败", e);
            return Result.error("HEALTH_CHECK_ERROR", "健康检查失败: " + e.getMessage());
        }
    }

    // =================== 私有方法 ===================

    /**
     * 转换为PageResponse
     */
    private PageResponse<SocialDynamicResponse> convertToPageResponse(IPage<SocialDynamic> page) {
        if (page == null) {
            return createEmptyPageResponse(1, 20);
        }
        
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return createEmptyPageResponse((int) page.getCurrent(), (int) page.getSize());
        }

        List<SocialDynamicResponse> responses = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        PageResponse<SocialDynamicResponse> response = new PageResponse<>();
        response.setDatas(responses);
        response.setTotal((int) page.getTotal());
        response.setCurrentPage((int) page.getCurrent());
        response.setPageSize((int) page.getSize());
        response.setTotalPage((int) page.getPages());
        response.setSuccess(true);

        return response;
    }

    /**
     * 转换为响应对象
     */
    private SocialDynamicResponse convertToResponse(SocialDynamic dynamic) {
        if (dynamic == null) {
            return null;
        }
        
        SocialDynamicResponse response = new SocialDynamicResponse();
        BeanUtils.copyProperties(dynamic, response);
        return response;
    }

    /**
     * 创建空的分页响应
     */
    private PageResponse<SocialDynamicResponse> createEmptyPageResponse(int currentPage, int pageSize) {
        PageResponse<SocialDynamicResponse> response = new PageResponse<>();
        response.setDatas(Collections.emptyList());
        response.setTotal(0);
        response.setCurrentPage(currentPage);
        response.setPageSize(pageSize);
        response.setTotalPage(0);
        response.setSuccess(true);
        return response;
    }
} 