package com.gig.collide.users.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.gig.collide.api.file.request.FileUploadRequest;
import com.gig.collide.api.file.response.FileUploadResponse;
import com.gig.collide.users.domain.service.FileUploadService;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "文件上传服务", description = "文件上传相关接口")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    /**
     * 上传单个文件
     *
     * @param file 上传的文件
     * @param fileType 文件类型 (avatar/content/attachment)
     * @param fileName 文件名（可选，默认使用原文件名）
     * @param businessId 业务ID（可选）
     * @param description 文件描述（可选）
     * @return 文件上传结果
     */
    @PostMapping("/upload")
    @SaCheckLogin
    @Operation(summary = "上传文件", description = "支持头像、内容图片/视频、附件等文件上传")
    public Result<FileUploadResponse> uploadFile(
        @Parameter(description = "上传的文件", required = true)
        @RequestParam("file") MultipartFile file,
        
        @Parameter(description = "文件类型", example = "avatar", required = true)
        @RequestParam("fileType") String fileType,
        
        @Parameter(description = "文件名", example = "profile.jpg")
        @RequestParam(value = "fileName", required = false) String fileName,
        
        @Parameter(description = "业务ID", example = "12345")
        @RequestParam(value = "businessId", required = false) Long businessId,
        
        @Parameter(description = "文件描述", example = "用户头像")
        @RequestParam(value = "description", required = false) String description) {
        
        try {
            // 获取当前登录用户ID
            Long uploadUserId = StpUtil.getLoginIdAsLong();
            
            // 构建上传请求
            FileUploadRequest request = new FileUploadRequest();
            request.setFileType(fileType);
            request.setFileName(fileName != null ? fileName : file.getOriginalFilename());
            request.setBusinessId(businessId);
            request.setDescription(description);
            
            // 手动验证请求参数
            validateUploadRequest(request);
            
            log.info("用户{}开始上传文件：{}，类型：{}", uploadUserId, request.getFileName(), fileType);
            
            // 执行文件上传
            FileUploadResponse response = fileUploadService.uploadFile(file, request, uploadUserId);
            
            log.info("用户{}文件上传成功，文件ID：{}", uploadUserId, response.getFileId());
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("文件上传参数错误：{}", e.getMessage());
            return Result.error("PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("UPLOAD_ERROR", "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 批量上传文件（内容发布场景）
     *
     * @param files 上传的文件数组
     * @param fileType 文件类型
     * @param businessId 业务ID（如内容ID）
     * @return 批量上传结果
     */
    @PostMapping("/batch-upload")
    @SaCheckLogin
    @Operation(summary = "批量上传文件", description = "支持一次上传多个文件，常用于内容发布")
    public Result<java.util.List<FileUploadResponse>> batchUploadFiles(
        @Parameter(description = "上传的文件数组", required = true)
        @RequestParam("files") MultipartFile[] files,
        
        @Parameter(description = "文件类型", example = "content", required = true)
        @RequestParam("fileType") String fileType,
        
        @Parameter(description = "业务ID", example = "12345")
        @RequestParam(value = "businessId", required = false) Long businessId) {
        
        try {
            // 检查文件数量限制
            if (files.length > 9) {
                return Result.error("PARAM_ERROR", "一次最多只能上传9个文件");
            }
            
            Long uploadUserId = StpUtil.getLoginIdAsLong();
            java.util.List<FileUploadResponse> responses = new java.util.ArrayList<>();
            
            log.info("用户{}开始批量上传{}个文件，类型：{}", uploadUserId, files.length, fileType);
            
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                
                // 构建上传请求
                FileUploadRequest request = new FileUploadRequest();
                request.setFileType(fileType);
                request.setFileName(file.getOriginalFilename());
                request.setBusinessId(businessId);
                request.setDescription("批量上传文件 " + (i + 1));
                
                // 验证请求参数
                validateUploadRequest(request);
                
                // 执行单个文件上传
                FileUploadResponse response = fileUploadService.uploadFile(file, request, uploadUserId);
                responses.add(response);
            }
            
            log.info("用户{}批量文件上传成功，共{}个文件", uploadUserId, files.length);
            return Result.success(responses);
            
        } catch (IllegalArgumentException e) {
            log.warn("批量文件上传参数错误：{}", e.getMessage());
            return Result.error("PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return Result.error("UPLOAD_ERROR", "批量文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件上传配置信息
     *
     * @return 上传配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取文件上传配置", description = "获取文件类型限制、大小限制等配置信息")
    public Result<java.util.Map<String, Object>> getUploadConfig() {
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        
        // 文件类型配置
        java.util.Map<String, Object> fileTypes = new java.util.HashMap<>();
        fileTypes.put("avatar", java.util.Map.of(
            "allowedTypes", java.util.List.of("image/jpeg", "image/png", "image/gif", "image/webp"),
            "maxSize", 5 * 1024 * 1024,  // 5MB
            "description", "用户头像"
        ));
        fileTypes.put("content", java.util.Map.of(
            "allowedTypes", java.util.List.of("image/jpeg", "image/png", "image/gif", "image/webp", 
                "video/mp4", "video/avi", "video/mov"),
            "maxSize", 100 * 1024 * 1024,  // 100MB
            "description", "内容图片或视频"
        ));
        fileTypes.put("attachment", java.util.Map.of(
            "allowedTypes", java.util.List.of("*/*"),
            "maxSize", 20 * 1024 * 1024,  // 20MB
            "description", "通用附件"
        ));
        
        config.put("fileTypes", fileTypes);
        config.put("maxBatchSize", 9);
        config.put("uploadPath", "/api/v1/files/upload");
        config.put("batchUploadPath", "/api/v1/files/batch-upload");
        
        return Result.success(config);
    }

    /**
     * 验证上传请求参数
     */
    private void validateUploadRequest(FileUploadRequest request) {
        if (request.getFileType() == null || request.getFileType().trim().isEmpty()) {
            throw new IllegalArgumentException("文件类型不能为空");
        }
        
        if (!request.getFileType().matches("^(avatar|content|attachment)$")) {
            throw new IllegalArgumentException("文件类型只能是 avatar、content 或 attachment");
        }
        
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }
    }
} 