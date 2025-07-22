package com.gig.collide.file;

import java.io.InputStream;

/**
 * 文件 服务
 */
public interface FileService {

    /**
     * 文件上传
     * @param path 文件路径
     * @param fileStream 文件流
     * @return 上传结果
     */
    boolean upload(String path, InputStream fileStream);

    /**
     * 文件上传（带Content-Type）
     * @param path 文件路径
     * @param fileStream 文件流
     * @param contentType 文件类型
     * @return 上传结果
     */
    default boolean upload(String path, InputStream fileStream, String contentType) {
        return upload(path, fileStream);
    }
}
