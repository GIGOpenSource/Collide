package com.gig.collide.users.config;

import com.gig.collide.file.FileService;
import com.gig.collide.file.OssServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 用户模块文件服务配置
 */
@Configuration
public class UserFileServiceConfig {

    @Bean
    @Primary
    public FileService userFileService() {
        OssServiceImpl ossService = new OssServiceImpl();
        ossService.setBucket("mds");
        ossService.setEndPoint("http://192.168.1.107:9000");
        ossService.setAccessKey("minioadmin");
        ossService.setAccessSecret("minioadmin");
        return ossService;
    }
} 