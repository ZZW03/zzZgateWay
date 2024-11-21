package com.zzz.filter.loadbalance;

import com.zzz.constant.LoadBalancingConst;
import com.zzz.filter.Filter;
import com.zzz.filter.FilterAspect;
import com.zzz.filter.loadbalance.impl.ConsistentHashRobin;
import com.zzz.filter.loadbalance.impl.RandomRobin;
import com.zzz.filter.loadbalance.impl.RoundRobin;
import com.zzz.model.GatewayContext;
import com.zzz.model.GatewayRequest;
import com.zzz.model.ServiceInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
@FilterAspect(name = "load",id = "2",order = Integer.MAX_VALUE)
public class LoadBalanceFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        Integer loadBalancing = ctx.getRule().getLoadBalancing();
        LoadBalanceGatewayRule robinInstance = getRobinInstance(loadBalancing, ctx);
        ServiceInstance serviceInstance = robinInstance.choose(ctx);
        GatewayRequest request = ctx.getRequest();
        if (serviceInstance != null && request != null) {
            String host = serviceInstance.getIp() + ":" + serviceInstance.getPort();
            request.setModifyHost(host);
        } else {
            log.warn("No instance available for :{}", ctx.getUniqueId());
            throw new RuntimeException("服务未找到");
        }
    }

    private LoadBalanceGatewayRule getRobinInstance(Integer loadBalancing, GatewayContext ctx) {
        LoadBalanceGatewayRule loadBalanceGatewayRule = null;
        if(loadBalancing.equals(LoadBalancingConst.ROUND_ROBIN.getStrategy())){
            loadBalanceGatewayRule = RoundRobin.getInstance(ctx.getRule().getServerName());

        }else if (loadBalancing.equals(LoadBalancingConst.RANDOM_ROBIN.getStrategy())){
            loadBalanceGatewayRule = RandomRobin.getInstance();

        } else if (loadBalancing.equals(LoadBalancingConst.CONSISTENT_HASH_ROBIN.getStrategy())) {
            loadBalanceGatewayRule = ConsistentHashRobin.getInstance();
        }
        return loadBalanceGatewayRule;
    }
}