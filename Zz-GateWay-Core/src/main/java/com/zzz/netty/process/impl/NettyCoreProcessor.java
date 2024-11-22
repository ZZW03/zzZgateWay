package com.zzz.netty.process.impl;

import com.zzz.filter.GatewayFilterChainChainFactory;
import com.zzz.model.GatewayContext;
import com.zzz.model.HttpRequestWrapper;
import com.zzz.netty.factory.RequestFactory;
import com.zzz.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();
        try{
            log.info("进行处理");
            GatewayContext gatewayContext = RequestFactory.doContext(request, ctx);
            GatewayFilterChainChainFactory.getInstance().buildFilterChain(gatewayContext).doFilter(gatewayContext);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
