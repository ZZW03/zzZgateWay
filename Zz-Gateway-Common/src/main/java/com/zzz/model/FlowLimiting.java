package com.zzz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowLimiting {
    /**
     * 限流方式 1是对用户信息进行限流 2是对访问路径进行限流 放入redis中限流
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

}