package com.zzz.filter;

import com.zzz.model.GatewayContext;

public interface FilterChainFactory {

    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

}
