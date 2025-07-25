package com.gig.collide.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * oss配置
 *
 * @author GIGOpenTeam
 */
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties {
    public static final String PREFIX = "spring.oss";

    /**
     * 是否启用OSS服务
     */
    private boolean enabled = true;

    private String bucket;

    private String endPoint;

    private String accessKey;

    private String accessSecret;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }
}
