package com.gig.collide.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁接口
 * 参考 nft-turbo-lock 设计
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface DistributedLock {

    /**
     * 尝试获取锁
     *
     * @param key     锁的唯一标识
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String key, long timeout, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param key 锁的唯一标识
     */
    void unlock(String key);
} 