package com.gig.collide.es.config;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * ES配置
 *
 * @author GIG
 */
@Configuration
@EsMapperScan("com.gig.collide.*.infrastructure.es.mapper")
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
public class EsConfiguration {

}
