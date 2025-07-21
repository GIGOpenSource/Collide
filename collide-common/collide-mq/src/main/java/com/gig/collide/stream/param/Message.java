package com.gig.collide.stream.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息
 *
 * @author GIG
 */
@Data
@Accessors(chain = true)
public class Message {
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 消息体
     */
    private String body;
}