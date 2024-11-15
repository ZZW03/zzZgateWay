package com.zzz.netty.service;

import com.zzz.model.HttpRequestWrapper;
import com.zzz.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;


public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    private NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor){
        this.nettyProcessor = nettyProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将接收到的消息转换为 FullHttpRequest 对象
        FullHttpRequest request = (FullHttpRequest) msg;
        // 创建 HttpRequestWrapper 对象，并设置上下文和请求
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
        httpRequestWrapper.setCtx(ctx);
        httpRequestWrapper.setRequest(request);

        // 调用业务逻辑处理器的 process 方法处理请求
        nettyProcessor.process(httpRequestWrapper);
    }
}