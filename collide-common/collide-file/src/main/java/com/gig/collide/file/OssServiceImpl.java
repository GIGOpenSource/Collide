package com.gig.collide.file;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

    @Override
    public boolean upload(String path, InputStream fileStream) {
        return true;
    }

}