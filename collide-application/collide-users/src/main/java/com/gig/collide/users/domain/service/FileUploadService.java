package com.gig.collide.users.domain.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.gig.collide.api.file.request.FileUploadRequest;
import com.gig.collide.api.file.response.FileUploadResponse;
import com.gig.collide.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件上传业务服务
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final FileService fileService;

    /**
     * 允许的图片文件类型
     */
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    /**
     * 允许的视频文件类型
     */
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv"
    );

    /**
     * 文件大小限制（字节）
     */
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_VIDEO_SIZE = 100 * 1024 * 1024; // 100MB
    private static final long MAX_ATTACHMENT_SIZE = 20 * 1024 * 1024; // 20MB

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @param request 上传请求
     * @param uploadUserId 上传用户ID
     * @return 文件上传响应
     */
    public FileUploadResponse uploadFile(MultipartFile file, FileUploadRequest request, Long uploadUserId) {
        try {
            log.info("开始上传文件，文件名：{}，文件类型：{}，用户ID：{}", 
                file.getOriginalFilename(), request.getFileType(), uploadUserId);

            // 1. 文件验证
            validateFile(file, request.getFileType());

            // 2. 生成文件路径
            String filePath = generateFilePath(file, request.getFileType());

            // 3. 上传文件到存储服务
            boolean uploadSuccess = fileService.upload(filePath, file.getInputStream());
            
            if (!uploadSuccess) {
                throw new RuntimeException("文件上传失败");
            }

            // 4. 构建响应
            FileUploadResponse response = FileUploadResponse.builder()
                .fileId(generateFileId())
                .fileUrl(generateFileUrl(filePath))
                .filePath(filePath)
                .originalFileName(file.getOriginalFilename())
                .fileType(request.getFileType())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .uploadTime(LocalDateTime.now())
                .uploadUserId(uploadUserId)
                .build();

            log.info("文件上传成功，文件路径：{}，文件大小：{}字节", filePath, file.getSize());
            return response;

        } catch (IOException e) {
            log.error("文件上传IO异常，文件名：{}", file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传异常，文件名：{}", file.getOriginalFilename(), e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, String fileType) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String contentType = file.getContentType();
        long fileSize = file.getSize();

        // 根据文件类型进行不同的验证
        switch (fileType) {
            case "avatar":
                validateImageFile(contentType, fileSize, "头像");
                break;
            case "content":
                if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
                    validateImageFile(contentType, fileSize, "内容图片");
                } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
                    validateVideoFile(contentType, fileSize);
                } else {
                    throw new IllegalArgumentException("不支持的内容文件类型：" + contentType);
                }
                break;
            case "attachment":
                if (fileSize > MAX_ATTACHMENT_SIZE) {
                    throw new IllegalArgumentException("附件文件大小不能超过20MB");
                }
                break;
            default:
                throw new IllegalArgumentException("不支持的文件类型：" + fileType);
        }
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(String contentType, long fileSize, String fileCategory) {
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(fileCategory + "只支持JPG、PNG、GIF、WebP格式");
        }
        if (fileSize > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException(fileCategory + "大小不能超过5MB");
        }
    }

    /**
     * 验证视频文件
     */
    private void validateVideoFile(String contentType, long fileSize) {
        if (!ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("视频只支持MP4、AVI、MOV、WMV、FLV格式");
        }
        if (fileSize > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException("视频文件大小不能超过100MB");
        }
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(MultipartFile file, String fileType) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        String fileName = IdUtil.fastSimpleUUID() + fileExtension;
        
        return String.format("%s/%s/%s", fileType, dateStr, fileName);
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (StrUtil.isBlank(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    /**
     * 生成文件ID
     */
    private String generateFileId() {
        return IdUtil.getSnowflakeNextIdStr();
    }

    /**
     * 生成文件访问URL
     */
    private String generateFileUrl(String filePath) {
        // TODO: 这里应该根据实际的CDN或文件服务器配置生成完整的访问URL
        // 目前使用相对路径，在实际部署时需要配置文件服务器的域名
        return "/files/" + filePath;
    }
} 