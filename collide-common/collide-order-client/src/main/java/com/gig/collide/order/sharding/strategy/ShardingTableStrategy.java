package com.gig.collide.order.sharding.strategy;

/**
 * @author GIG
 */
public interface ShardingTableStrategy {

    /**
     * 获取分表结果
     *
     * @param externalId 外部id
     * @param tableCount 表数量
     * @return 分表结果
     */
    public int getTable(String externalId, int tableCount);
}