package com.gig.collide.tag.controller;

import com.gig.collide.api.tag.request.TagCreateRequest;
import com.gig.collide.api.tag.request.TagQueryRequest;
import com.gig.collide.api.tag.request.TagUpdateRequest;
import com.gig.collide.api.tag.response.TagOperatorResponse;
import com.gig.collide.api.tag.response.TagQueryResponse;
import com.gig.collide.api.tag.response.data.TagInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.tag.facade.TagFacadeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 标签管理控制器
 * 提供管理员级别的标签管理功能
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-12-19
 */
@RestController
@RequestMapping("/api/admin/tags")
public class TagManageController {

    @Autowired
    private TagFacadeServiceImpl tagFacadeService;

    /**
     * 管理员创建标签
     * 
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping("/create")
    public TagOperatorResponse createTagByAdmin(@Valid @RequestBody TagCreateRequest request) {
        return tagFacadeService.createTag(request);
    }

    /**
     * 管理员更新标签
     * 
     * @param request 更新请求
     * @return 更新结果
     */
    @PutMapping("/update")
    public TagOperatorResponse updateTagByAdmin(@Valid @RequestBody TagUpdateRequest request) {
        return tagFacadeService.updateTag(request);
    }

    /**
     * 管理员删除标签（软删除）
     * 
     * @param tagId 标签ID
     * @return 删除结果
     */
    @DeleteMapping("/{tagId}")
    public TagOperatorResponse deleteTagByAdmin(@PathVariable Long tagId) {
        return tagFacadeService.deleteTag(tagId);
    }

    /**
     * 管理员批量删除标签
     * 
     * @param tagIds 标签ID列表
     * @return 批量删除结果
     */
    @DeleteMapping("/batch")
    public BaseResponse batchDeleteTags(@RequestParam Long[] tagIds) {
        BaseResponse response = new BaseResponse();
        int successCount = 0;
        int failCount = 0;
        
                 for (Long tagId : tagIds) {
             try {
                 TagOperatorResponse result = tagFacadeService.deleteTag(tagId);
                 if (result.getSuccess() != null && result.getSuccess()) {
                     successCount++;
                 } else {
                     failCount++;
                 }
             } catch (Exception e) {
                 failCount++;
             }
         }
        
        response.setSuccess(failCount == 0);
        response.setMessage(String.format("批量删除完成，成功：%d，失败：%d", successCount, failCount));
        return response;
    }

    /**
     * 管理员激活标签
     * 
     * @param tagId 标签ID
     * @return 操作结果
     */
    @PostMapping("/{tagId}/activate")
    public BaseResponse activateTag(@PathVariable Long tagId) {
                 try {
             TagUpdateRequest request = TagUpdateRequest.builder()
                     .tagId(tagId)
                     .status("active")
                     .build();
             TagOperatorResponse result = tagFacadeService.updateTag(request);
             
             BaseResponse response = new BaseResponse();
             response.setSuccess(result.getSuccess() != null && result.getSuccess());
             response.setMessage(result.getSuccess() != null && result.getSuccess() ? "激活标签成功" : "激活标签失败");
             return response;
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setMessage("激活标签失败：" + e.getMessage());
            return response;
        }
    }

    /**
     * 管理员禁用标签
     * 
     * @param tagId 标签ID
     * @return 操作结果
     */
    @PostMapping("/{tagId}/deactivate")
    public BaseResponse deactivateTag(@PathVariable Long tagId) {
                 try {
             TagUpdateRequest request = TagUpdateRequest.builder()
                     .tagId(tagId)
                     .status("inactive")
                     .build();
             TagOperatorResponse result = tagFacadeService.updateTag(request);
             
             BaseResponse response = new BaseResponse();
             response.setSuccess(result.getSuccess() != null && result.getSuccess());
             response.setMessage(result.getSuccess() != null && result.getSuccess() ? "禁用标签成功" : "禁用标签失败");
             return response;
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setMessage("禁用标签失败：" + e.getMessage());
            return response;
        }
    }

    /**
     * 管理员批量激活标签
     * 
     * @param tagIds 标签ID列表
     * @return 批量操作结果
     */
    @PostMapping("/batch/activate")
    public BaseResponse batchActivateTags(@RequestParam Long[] tagIds) {
        return batchUpdateTagStatus(tagIds, 1, "激活");
    }

    /**
     * 管理员批量禁用标签
     * 
     * @param tagIds 标签ID列表
     * @return 批量操作结果
     */
    @PostMapping("/batch/deactivate")
    public BaseResponse batchDeactivateTags(@RequestParam Long[] tagIds) {
        return batchUpdateTagStatus(tagIds, 0, "禁用");
    }

    /**
     * 管理员查询所有标签（包括禁用的）
     * 
     * @param request 查询请求
     * @return 查询结果
     */
    @PostMapping("/query/all")
    public TagQueryResponse<PageResponse<TagInfo>> queryAllTags(@Valid @RequestBody TagQueryRequest request) {
        // 管理员可以查询所有状态的标签
        return tagFacadeService.queryTags(request);
    }

    /**
     * 获取标签统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public BaseResponse getTagStatistics() {
        BaseResponse response = new BaseResponse();
        try {
            // 这里可以实现具体的统计逻辑
            // 比如总标签数、活跃标签数、各类型标签数量等
            response.setSuccess(true);
            response.setMessage("统计信息获取成功");
            // TODO: 实现具体的统计逻辑
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("统计信息获取失败：" + e.getMessage());
        }
        return response;
    }

    /**
     * 批量更新标签状态的私有方法
     * 
     * @param tagIds 标签ID列表
     * @param status 状态（1：激活，0：禁用）
     * @param operation 操作名称
     * @return 操作结果
     */
    private BaseResponse batchUpdateTagStatus(Long[] tagIds, Integer status, String operation) {
        BaseResponse response = new BaseResponse();
        int successCount = 0;
        int failCount = 0;
        
                 for (Long tagId : tagIds) {
             try {
                 String statusStr = status == 1 ? "active" : "inactive";
                 TagUpdateRequest request = TagUpdateRequest.builder()
                         .tagId(tagId)
                         .status(statusStr)
                         .build();
                 TagOperatorResponse result = tagFacadeService.updateTag(request);
                 if (result.getSuccess() != null && result.getSuccess()) {
                     successCount++;
                 } else {
                     failCount++;
                 }
             } catch (Exception e) {
                 failCount++;
             }
         }
        
        response.setSuccess(failCount == 0);
        response.setMessage(String.format("批量%s完成，成功：%d，失败：%d", operation, successCount, failCount));
        return response;
    }
} 