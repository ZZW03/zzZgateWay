package com.zzz.filter.loadbalance.impl;

import com.zzz.filter.loadbalance.LoadBalanceGatewayRule;
import com.zzz.holder.ServiceHolder;
import com.zzz.model.GatewayContext;
import com.zzz.model.ServiceInstance;

import java.util.List;

public class RandomRobin implements LoadBalanceGatewayRule {

    private static class SingletonHolder {
        private static final RandomRobin instance = new RandomRobin();
    }

    public static RandomRobin getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public ServiceInstance choose(GatewayContext ctx) {
        List<ServiceInstance> serviceInstances = ServiceHolder.getInstance().getServiceInstancesByServiceName(ctx.getRule().getServerName());
        int length = serviceInstances.size();
        java.util.Random random = new java.util.Random();
        return serviceInstances.get(random.nextInt(0, length));
    }
}
