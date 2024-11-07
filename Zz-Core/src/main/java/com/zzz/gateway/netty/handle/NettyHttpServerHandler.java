package com.zzz.gateway.netty.handle;


import com.zzz.gateway.context.HttpRequestWrapper;
import com.zzz.gateway.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;


/**
 * NettyHttpServerHandler 用于处理通过 Netty 传入的 HTTP 请求。
 * 它继承自 ChannelInboundHandlerAdapter，这样可以覆盖回调方法来处理入站事件。
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    // 成员变量nettyProcessor，用于处理具体的业务逻辑
    private final NettyProcessor nettyProcessor;

    /**
     * 构造函数需要一个处理器
     */
    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    /**
     * 接受一个的是一个http请求 所以可以直接转换 然后交给process处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将接收到的消息转换为 FullHttpRequest 对象
        FullHttpRequest request = (FullHttpRequest) msg;
        // 创建 HttpRequestWrapper 对象，并设置上下文和请求 自己封装请求
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
        httpRequestWrapper.setCtx(ctx);
        httpRequestWrapper.setRequest(request);

        // 调用业务逻辑处理器的 process 方法处理请求
        nettyProcessor.process(httpRequestWrapper);
    }


}