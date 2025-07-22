package com.gig.collide.file;

import io.minio.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * oss 服务
 * @author GIGOpenTeam
 */
@Slf4j
@Setter
public class OssServiceImpl implements FileService {

    private String bucket;

    private String endPoint;

    private String accessKey;

    private String accessSecret;

    /**
     * 获取MinIO客户端
     */
    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(endPoint)
                .credentials(accessKey, accessSecret)
                .build();
    }

    /**
     * 确保bucket存在，如果不存在则创建
     */
    private void ensureBucketExists(MinioClient minioClient) throws Exception {
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            log.info("创建bucket成功: {}", bucket);
        }
    }

    @Override
    public boolean upload(String path, InputStream fileStream) {
        return upload(path, fileStream, null);
    }

    @Override
    public boolean upload(String path, InputStream fileStream, String contentType) {
        if (StringUtils.isBlank(path) || fileStream == null) {
            log.error("文件路径或文件流为空");
            return false;
        }

        try {
            MinioClient minioClient = getMinioClient();
            
            // 确保bucket存在
            ensureBucketExists(minioClient);
            
            // 构建上传参数
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .stream(fileStream, -1, 10485760); // 10MB缓冲区
            
            // 设置Content-Type
            if (StringUtils.isNotBlank(contentType)) {
                builder.contentType(contentType);
                log.info("设置Content-Type: {}", contentType);
            } else {
                // 根据文件扩展名推断Content-Type
                String inferredContentType = inferContentType(path);
                if (StringUtils.isNotBlank(inferredContentType)) {
                    builder.contentType(inferredContentType);
                    log.info("推断Content-Type: {}", inferredContentType);
                }
            }
            
            // 上传文件
            minioClient.putObject(builder.build());
            
            log.info("文件上传成功: bucket={}, path={}", bucket, path);
            return true;
            
        } catch (Exception e) {
            log.error("文件上传失败: bucket={}, path={}, error={}", bucket, path, e.getMessage(), e);
            return false;
        } finally {
            // 关闭文件流
            try {
                if (fileStream != null) {
                    fileStream.close();
                }
            } catch (IOException e) {
                log.warn("关闭文件流失败", e);
            }
        }
    }

    /**
     * 根据文件扩展名推断Content-Type
     */
    private String inferContentType(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        } else if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerPath.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerPath.endsWith(".bmp")) {
            return "image/bmp";
        } else if (lowerPath.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (lowerPath.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerPath.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerPath.endsWith(".mp4")) {
            return "video/mp4";
        } else if (lowerPath.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        
        return null;
    }
}