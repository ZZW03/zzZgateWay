package com.zzz.filter.loadbalance.impl;

import com.zzz.filter.loadbalance.LoadBalanceGatewayRule;
import com.zzz.holder.ServiceHolder;
import com.zzz.model.GatewayContext;
import com.zzz.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class RoundRobin implements LoadBalanceGatewayRule {

    AtomicInteger count = new AtomicInteger(0);

    private static Map<String,RoundRobin> INSTANCES = new HashMap<String,RoundRobin>();

    public static RoundRobin getInstance(String uniqueId){
        RoundRobin orDefault = INSTANCES.getOrDefault(uniqueId, new RoundRobin());
        INSTANCES.put(uniqueId, orDefault);
        return orDefault;
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx) {
        String uniqueId = ctx.getRequest().getUniqueId();
        List<ServiceInstance> serviceInstanceList =
                ServiceHolder.getInstance().getServiceInstancesByServiceName(uniqueId);
        int size = serviceInstanceList.size();
        if(size == 0){
            log.warn("No instance available for:{}", uniqueId);
            throw new RuntimeException("服务未找到");
        }
        ServiceInstance instances = serviceInstanceList.get(count.get() % size);
        count.getAndIncrement();
        return instances;
    }


}
