package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 博主申请控制器
 * 处理用户申请成为博主的业务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/blogger")
@RequiredArgsConstructor
@Tag(name = "博主申请", description = "博主申请、审核相关接口")
public class BloggerController {

    /**
     * 申请成为博主
     */
    @PostMapping("/apply")
    @SaCheckLogin
    @Operation(summary = "申请成为博主", description = "提交博主申请，需要提供相关证明材料")
    public Result<String> applyForBlogger(@Valid @RequestBody BloggerApplyRequest request) {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户申请成为博主，用户ID: {}, 申请类型: {}", currentUserId, request.getApplyType());

            // TODO: 实现博主申请逻辑
            // 1. 验证用户资格
            // 2. 保存申请信息
            // 3. 发送审核通知

            return Result.success("申请提交成功，请等待审核");
        } catch (Exception e) {
            log.error("申请成为博主失败", e);
            return Result.error("BLOGGER_APPLY_ERROR", "申请失败，请稍后重试");
        }
    }

    /**
     * 查询申请状态
     */
    @GetMapping("/apply/status")
    @SaCheckLogin
    @Operation(summary = "查询申请状态", description = "查询当前用户的博主申请状态")
    public Result<Object> getApplyStatus() {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            
            // TODO: 实现查询申请状态逻辑
            return Result.success("申请审核中");
        } catch (Exception e) {
            log.error("查询申请状态失败", e);
            return Result.error("APPLY_STATUS_ERROR", "查询失败，请稍后重试");
        }
    }

    /**
     * 取消申请
     */
    @DeleteMapping("/apply")
    @SaCheckLogin
    @Operation(summary = "取消申请", description = "取消当前的博主申请")
    public Result<String> cancelApply() {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            log.info("用户取消博主申请，用户ID: {}", currentUserId);

            // TODO: 实现取消申请逻辑
            return Result.success("申请已取消");
        } catch (Exception e) {
            log.error("取消申请失败", e);
            return Result.error("CANCEL_APPLY_ERROR", "取消失败，请稍后重试");
        }
    }

    /**
     * 检查博主权限
     */
    @GetMapping("/check")
    @SaCheckLogin
    @Operation(summary = "检查博主权限", description = "检查当前用户是否具有博主权限")
    public Result<Boolean> checkBloggerPermission() {
        try {
            Long currentUserId = StpUtil.getLoginIdAsLong();
            
            // TODO: 实现检查博主权限逻辑
            boolean hasPermission = false; // 这里需要根据实际业务逻辑判断
            
            return Result.success(hasPermission);
        } catch (Exception e) {
            log.error("检查博主权限失败", e);
            return Result.error("CHECK_PERMISSION_ERROR", "检查失败，请稍后重试");
        }
    }

    /**
     * 博主申请请求参数
     */
    public static class BloggerApplyRequest {
        @NotNull(message = "申请类型不能为空")
        private String applyType; // 申请类型：PERSONAL-个人博主, ORGANIZATION-机构博主
        
        @NotBlank(message = "申请理由不能为空")
        private String applyReason; // 申请理由
        
        @NotBlank(message = "联系方式不能为空")
        private String contactInfo; // 联系方式
        
        private String portfolio; // 作品集链接
        private String socialMedia; // 社交媒体账号
        private String experience; // 相关经验描述

        // Getters and Setters
        public String getApplyType() { return applyType; }
        public void setApplyType(String applyType) { this.applyType = applyType; }
        
        public String getApplyReason() { return applyReason; }
        public void setApplyReason(String applyReason) { this.applyReason = applyReason; }
        
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        
        public String getPortfolio() { return portfolio; }
        public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
        
        public String getSocialMedia() { return socialMedia; }
        public void setSocialMedia(String socialMedia) { this.socialMedia = socialMedia; }
        
        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }
    }
} 