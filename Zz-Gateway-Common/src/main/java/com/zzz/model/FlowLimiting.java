package com.zzz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FlowLimiting {
    /**
     * 限流方式 0是对ip进行限流 1是对用户信息进行限流 2是对访问路径进行限流 放入redis中限流
     */
    Integer type;

    /**
     * 一定时间内限流
     */
    Integer Time;

    /**
     * 次数
     */
    Integer frequency;

    public FlowLimiting(Integer type, Integer time, Integer frequency) {
        this.type = type;
        Time = time;
        this.frequency = frequency;
    }

    public FlowLimiting() {
    }
}