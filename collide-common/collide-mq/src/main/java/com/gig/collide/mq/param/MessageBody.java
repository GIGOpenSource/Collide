package com.gig.collide.mq.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息体
 * 统一的消息结构，包含幂等号和消息内容
 *
 * @author Collide Team
 */
@Data
@Accessors(chain = true)
public class MessageBody {
    /**
     * 幂等号
     */
    private String identifier;
    
    /**
     * 消息体
     */
    private String body;
} 