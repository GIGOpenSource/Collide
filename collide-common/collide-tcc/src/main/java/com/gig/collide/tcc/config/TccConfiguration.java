package com.gig.collide.tcc.config;

import com.gig.collide.tcc.service.TransactionLogService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Hollis
 */
@Configuration
@MapperScan("com.gig.collide.tcc.mapper")
public class TccConfiguration {

    @Bean
    public TransactionLogService transactionLogService() {
        return new TransactionLogService();
    }
}
