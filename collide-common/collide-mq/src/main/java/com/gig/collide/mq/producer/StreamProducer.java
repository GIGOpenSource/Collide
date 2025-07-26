package com.gig.collide.mq.producer;

import com.gig.collide.mq.param.MessageBody;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.common.message.MessageConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

/**
 * 统一消息生产者
 * 提供标准化的消息发送功能
 *
 * @author Collide Team
 */
public class StreamProducer {

    private static final Logger logger = LoggerFactory.getLogger(StreamProducer.class);

    public static final int DELAY_LEVEL_1_M = 5;
    public static final String ROCKET_MQ_MESSAGE_ID = "ROCKET_MQ_MESSAGE_ID";
    public static final String ROCKET_TAGS = "ROCKET_TAGS";
    public static final String ROCKET_MQ_TOPIC = "ROCKET_MQ_TOPIC";

    @Autowired
    private StreamBridge streamBridge;

    /**
     * 发送普通消息
     *
     * @param bindingName 绑定名称
     * @param tag         消息标签
     * @param msg         消息内容
     * @return 发送结果
     */
    public boolean send(String bindingName, String tag, String msg) {
        // 构建消息对象
        MessageBody message = new MessageBody()
                .setIdentifier(UUID.randomUUID().toString())
                .setBody(msg);
        
        logger.info("send message: {} , {} , {}", bindingName, tag, JSON.toJSONString(message));
        
        boolean result = streamBridge.send(bindingName, 
            MessageBuilder.withPayload(message)
                .setHeader("TAGS", tag)
                .build());
        
        logger.info("send result: {} , {} , {}", bindingName, tag, result);
        return result;
    }

    /**
     * 发送延迟消息
     *
     * @param bindingName 绑定名称
     * @param tag         消息标签
     * @param msg         消息内容
     * @param delayLevel  延迟级别 (RocketMQ支持18个级别的延迟时间)
     * @return 发送结果
     */
    public boolean send(String bindingName, String tag, String msg, int delayLevel) {
        // 构建消息对象
        MessageBody message = new MessageBody()
                .setIdentifier(UUID.randomUUID().toString())
                .setBody(msg);
        
        logger.info("send delay message: {} , {} , {}", bindingName, tag, JSON.toJSONString(message));
        
        boolean result = streamBridge.send(bindingName, 
            MessageBuilder.withPayload(message)
                .setHeader("TAGS", tag)
                .setHeader(MessageConst.PROPERTY_DELAY_TIME_LEVEL, delayLevel)
                .build());
        
        logger.info("send delay result: {} , {} , {}", bindingName, tag, result);
        return result;
    }

    /**
     * 发送带自定义Header的消息
     *
     * @param bindingName 绑定名称
     * @param tag         消息标签
     * @param msg         消息内容
     * @param headerKey   自定义Header键
     * @param headerValue 自定义Header值
     * @return 发送结果
     */
    public boolean send(String bindingName, String tag, String msg, String headerKey, String headerValue) {
        // 构建消息对象
        MessageBody message = new MessageBody()
                .setIdentifier(UUID.randomUUID().toString())
                .setBody(msg);
        
        logger.info("send message with header: {} , {}", bindingName, JSON.toJSONString(message));
        
        boolean result = streamBridge.send(bindingName, 
            MessageBuilder.withPayload(message)
                .setHeader("TAGS", tag)
                .setHeader(headerKey, headerValue)
                .build());
        
        logger.info("send result: {} , {}", bindingName, result);
        return result;
    }
} 