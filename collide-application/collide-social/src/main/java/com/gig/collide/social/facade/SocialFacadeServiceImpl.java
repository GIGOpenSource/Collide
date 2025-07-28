package com.gig.collide.social.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.social.SocialFacadeService;
import com.gig.collide.api.social.request.SocialDynamicCreateRequest;
import com.gig.collide.api.social.request.SocialDynamicQueryRequest;
import com.gig.collide.api.social.request.SocialDynamicUpdateRequest;
import com.gig.collide.api.social.response.SocialDynamicResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.social.domain.entity.SocialDynamic;
import com.gig.collide.social.domain.service.SocialDynamicService;
import com.gig.collide.web.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 社交动态门面服务实现类 - 简洁版
 *
 * @author GIG Team
 * @version 2.0.0
 */
@Slf4j
@DubboService(version = "2.0.0")
public class SocialFacadeServiceImpl implements SocialFacadeService {

    @Autowired
    private SocialDynamicService socialDynamicService;

    @Override
    public Result<SocialDynamicResponse> createDynamic(SocialDynamicCreateRequest request) {
        try {
            SocialDynamic dynamic = new SocialDynamic();
            BeanUtils.copyProperties(request, dynamic);
            
            SocialDynamic savedDynamic = socialDynamicService.createDynamic(dynamic);
            SocialDynamicResponse response = convertToResponse(savedDynamic);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("发布动态失败", e);
            return Result.error("DYNAMIC_CREATE_ERROR", "发布动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SocialDynamicResponse> updateDynamic(SocialDynamicUpdateRequest request) {
        try {
            SocialDynamic dynamic = new SocialDynamic();
            BeanUtils.copyProperties(request, dynamic);
            
            SocialDynamic updatedDynamic = socialDynamicService.updateDynamic(dynamic);
            SocialDynamicResponse response = convertToResponse(updatedDynamic);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("更新动态失败", e);
            return Result.error("DYNAMIC_UPDATE_ERROR", "更新动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteDynamic(Long dynamicId) {
        try {
            socialDynamicService.deleteDynamic(dynamicId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("删除动态失败", e);
            return Result.error("DYNAMIC_DELETE_ERROR", "删除动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SocialDynamicResponse> getDynamicById(Long dynamicId) {
        try {
            SocialDynamic dynamic = socialDynamicService.getDynamicById(dynamicId);
            if (dynamic == null) {
                return Result.error("DYNAMIC_NOT_FOUND", "动态不存在");
            }
            
            SocialDynamicResponse response = convertToResponse(dynamic);
            return Result.success(response);
        } catch (Exception e) {
            log.error("查询动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "查询动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> queryDynamics(SocialDynamicQueryRequest request) {
        try {
            IPage<SocialDynamic> page = socialDynamicService.queryDynamics(
                request.getCurrentPage(),
                request.getPageSize(),
                request.getUserId(),
                request.getDynamicType(),
                request.getStatus(),
                request.getKeyword(),
                request.getMinLikeCount(),
                request.getSortBy(),
                request.getSortDirection()
            );
            
            List<SocialDynamicResponse> responses = page.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            PageResponse<SocialDynamicResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setTotal((int) page.getTotal());
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "分页查询动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SocialDynamicResponse>> getUserDynamics(Long userId, Integer limit) {
        try {
            List<SocialDynamic> dynamics = socialDynamicService.getUserDynamics(userId, limit);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取用户动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "获取用户动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<SocialDynamicResponse>> getFollowingDynamics(Long userId, SocialDynamicQueryRequest request) {
        try {
            IPage<SocialDynamic> page = socialDynamicService.getFollowingDynamics(
                userId,
                request.getCurrentPage(),
                request.getPageSize()
            );
            
            List<SocialDynamicResponse> responses = page.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            PageResponse<SocialDynamicResponse> result = new PageResponse<>();
            result.setDatas(responses);
            result.setTotal((int) page.getTotal());
            result.setCurrentPage(request.getCurrentPage());
            result.setPageSize(request.getPageSize());
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取关注动态流失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "获取关注动态流失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SocialDynamicResponse>> getDynamicsByType(String dynamicType, Integer limit) {
        try {
            List<SocialDynamic> dynamics = socialDynamicService.getDynamicsByType(dynamicType, limit);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据类型查询动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "根据类型查询动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> likeDynamic(Long dynamicId, Long userId) {
        try {
            socialDynamicService.likeDynamic(dynamicId, userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("点赞动态失败", e);
            return Result.error("DYNAMIC_LIKE_ERROR", "点赞动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> unlikeDynamic(Long dynamicId, Long userId) {
        try {
            socialDynamicService.unlikeDynamic(dynamicId, userId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("取消点赞失败", e);
            return Result.error("DYNAMIC_UNLIKE_ERROR", "取消点赞失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> commentDynamic(Long dynamicId, Long userId, String content) {
        try {
            socialDynamicService.commentDynamic(dynamicId, userId, content);
            return Result.success(null);
        } catch (Exception e) {
            log.error("评论动态失败", e);
            return Result.error("DYNAMIC_COMMENT_ERROR", "评论动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<SocialDynamicResponse> shareDynamic(Long dynamicId, Long userId, String shareContent) {
        try {
            SocialDynamic shareDynamic = socialDynamicService.shareDynamic(dynamicId, userId, shareContent);
            SocialDynamicResponse response = convertToResponse(shareDynamic);
            
            return Result.success(response);
        } catch (Exception e) {
            log.error("分享动态失败", e);
            return Result.error("DYNAMIC_SHARE_ERROR", "分享动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SocialDynamicResponse>> searchDynamics(String keyword, Integer limit) {
        try {
            List<SocialDynamic> dynamics = socialDynamicService.searchDynamics(keyword, limit);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("搜索动态失败", e);
            return Result.error("DYNAMIC_SEARCH_ERROR", "搜索动态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<SocialDynamicResponse>> getHotDynamics(Integer limit) {
        try {
            List<SocialDynamic> dynamics = socialDynamicService.getHotDynamics(limit);
            List<SocialDynamicResponse> responses = dynamics.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取热门动态失败", e);
            return Result.error("DYNAMIC_QUERY_ERROR", "获取热门动态失败: " + e.getMessage());
        }
    }

    /**
     * 转换为响应对象
     */
    private SocialDynamicResponse convertToResponse(SocialDynamic dynamic) {
        SocialDynamicResponse response = new SocialDynamicResponse();
        BeanUtils.copyProperties(dynamic, response);
        return response;
    }
} 