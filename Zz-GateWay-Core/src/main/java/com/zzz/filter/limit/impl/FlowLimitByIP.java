package com.zzz.filter.limit.impl;

import com.zzz.filter.limit.FlowLimit;
import com.zzz.model.FlowLimiting;
import com.zzz.model.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;


@Slf4j
public class FlowLimitByIP implements FlowLimit {

    private static class Singleton{
        private static final FlowLimitByIP INSTANCE = new FlowLimitByIP();
    }

    public static FlowLimitByIP getInstance(){
        return Singleton.INSTANCE;
    }

    static Jedis jedis = new Jedis("localhost",6379);

    @Override
    public void doLimit(GatewayContext ctx)  {
        String clientIp = ctx.getRequest().getClientIp();
        FlowLimiting flowLimiting = ctx.getRule().getFlowLimiting();
        Integer time = flowLimiting.getTime();
        Integer frequency = flowLimiting.getFrequency();

        String s = jedis.get(clientIp);
        if (s == null) {
            jedis.set(clientIp,  "1");
            jedis.expire(clientIp, time);
        } else {
            long currentCount = Long.parseLong(s);
            if (currentCount + 1 >= frequency) {
                log.warn("频率太高");
                throw new RuntimeException("频率太高");
            } else {
                jedis.set(clientIp, String.valueOf(currentCount + 1));
            }
        }

    }





}
