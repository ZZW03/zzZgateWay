package com.zzz.filter.loadbalance;


import com.zzz.model.GatewayContext;
import com.zzz.model.ServiceInstance;

/**
 * 负载均衡顶级接口
 */
public interface LoadBalanceGatewayRule {

    ServiceInstance choose(GatewayContext ctx);

}
