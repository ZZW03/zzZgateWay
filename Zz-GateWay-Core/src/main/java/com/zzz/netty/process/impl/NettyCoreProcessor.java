package com.zzz.netty.process.impl;

import com.zzz.model.GatewayContext;
import com.zzz.model.HttpRequestWrapper;
import com.zzz.netty.factory.RequestFactory;
import com.zzz.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();
        try{
            // 创建并填充 GatewayContext 以保存有关传入请求的信息。
            GatewayContext gatewayContext = RequestFactory.doContext(request, ctx);

        }catch (Exception e){

        }

    }

}
