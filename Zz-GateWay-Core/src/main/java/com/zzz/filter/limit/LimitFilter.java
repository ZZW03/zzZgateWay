package com.zzz.filter.limit;

import com.zzz.filter.Filter;
import com.zzz.filter.FilterAspect;
import com.zzz.filter.limit.impl.FlowLimitByIP;
import com.zzz.model.FlowLimiting;
import com.zzz.model.GatewayContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FilterAspect(name = "limit",id = "3",order = Integer.MAX_VALUE)
public class LimitFilter implements Filter {

    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        log.info("进入限流处理");
        FlowLimit flowLimit = choose(ctx);
        flowLimit.doLimit(ctx);
    }

    private FlowLimit choose(GatewayContext ctx) {
        FlowLimit flowLimit =null;
        Integer type = ctx.getRule().getFlowLimiting().getType();
        if (type == 1){
            flowLimit =  FlowLimitByIP.getInstance();
        }
        return flowLimit;
    }


}
