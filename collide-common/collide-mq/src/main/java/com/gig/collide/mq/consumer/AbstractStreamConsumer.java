package com.gig.collide.mq.consumer;

import com.gig.collide.mq.param.MessageBody;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;

import static com.gig.collide.mq.producer.StreamProducer.*;

/**
 * MQ消费基类
 * 提供统一的消息解析和日志记录功能
 * 
 * @author Collide Team
 */
@Slf4j
public abstract class AbstractStreamConsumer {

    /**
     * 从消息中解析出消息对象
     *
     * @param msg  消息
     * @param type 目标类型
     * @param <T>  泛型类型
     * @return 解析后的对象
     */
    public static <T> T getMessage(Message<MessageBody> msg, Class<T> type) {
        String messageId = msg.getHeaders().get(ROCKET_MQ_MESSAGE_ID, String.class);
        String tag = msg.getHeaders().get(ROCKET_TAGS, String.class);
        String topic = msg.getHeaders().get(ROCKET_MQ_TOPIC, String.class);
        
        Object object = JSON.parseObject(msg.getPayload().getBody(), type);
        
        log.info("Received Message topic:{} messageId:{}, object:{}, tag:{}", 
                topic, messageId, JSON.toJSONString(object), tag);
        
        return (T) object;
    }
    
    /**
     * 获取消息的幂等号
     *
     * @param msg 消息
     * @return 幂等号
     */
    public static String getIdentifier(Message<MessageBody> msg) {
        return msg.getPayload().getIdentifier();
    }
    
    /**
     * 获取消息的Tag
     *
     * @param msg 消息
     * @return Tag
     */
    public static String getTag(Message<MessageBody> msg) {
        return msg.getHeaders().get(ROCKET_TAGS, String.class);
    }
    
    /**
     * 获取消息的Topic
     *
     * @param msg 消息
     * @return Topic
     */
    public static String getTopic(Message<MessageBody> msg) {
        return msg.getHeaders().get(ROCKET_MQ_TOPIC, String.class);
    }
} 