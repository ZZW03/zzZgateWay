package com.zzz.constant;

import lombok.Getter;

@Getter
public enum LoadBalancingConst {

    ROUND_ROBIN(0, "随机策略"),
    RANDOM_ROBIN(1, "随机策略"),
    CONSISTENT_HASH_ROBIN(2, "一致性哈希策略");

    final Integer strategy;
    final String name;

    LoadBalancingConst(Integer strategy, String name) {
        this.strategy = strategy;
        this.name = name;
    }

    public Integer getStrategy() {
        return strategy;
    }

    public String getName() {
        return name;
    }
}
