package com.zzz.gateway.netty.process.impl;

import com.zzz.gateway.context.GatewayContext;
import com.zzz.gateway.context.HttpRequestWrapper;
import com.zzz.gateway.filter.FilterChainFactory;
import com.zzz.gateway.filter.GatewayFilterChainChainFactory;
import com.zzz.gateway.helper.RequestHelper;
import com.zzz.gateway.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    // FilterChainFactory 负责创建和管理过滤器的执行。
    private FilterChainFactory filterChainFactory = GatewayFilterChainChainFactory.getInstance();


    @Override
    public void process(HttpRequestWrapper wrapper) {

        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();

        try {
            // 创建并填充 GatewayContext 以保存有关传入请求的信息。 请求的上下文
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);

            // 在GatewayContext 上执行过滤器链逻辑。 将这个走过滤器链
            filterChainFactory.buildFilterChain(gatewayContext).doFilter(gatewayContext);

        }catch (Exception e){
            // todo 捕获异常后 回消息 . 清除request
            log.error(e.getMessage(), e);
        }
    }

}
