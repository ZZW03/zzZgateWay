package com.zzz.netty.process.impl;

import com.zzz.config.Config;
import com.zzz.filter.GatewayFilterChainChainFactory;
import com.zzz.filter.encryption.KeyPairHelper;
import com.zzz.model.GatewayContext;
import com.zzz.model.HttpRequestWrapper;
import com.zzz.netty.factory.RequestFactory;
import com.zzz.netty.factory.ResponseHelper;
import com.zzz.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.Base64;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();

        try{
            log.info("进行处理");
            if (request.uri().equals("/public-key")){
                String s = Base64.getEncoder().encodeToString(KeyPairHelper.getInstance().getKeyPair().getPublic().getEncoded());
                FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(s);
                ctx.writeAndFlush(httpResponse);
                return;
            } else if (request.uri().equals("/favicon.ico")) {
                return;
            }

            GatewayContext gatewayContext = RequestFactory.doContext(request, ctx);
            GatewayFilterChainChainFactory.getInstance().buildFilterChain(gatewayContext).doFilter(gatewayContext);

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
