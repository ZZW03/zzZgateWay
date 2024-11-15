package com.zzz.netty.process.impl;

import com.zzz.model.HttpRequestWrapper;
import com.zzz.netty.process.NettyProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();
        try{


        }catch (Exception e){

        }

    }

}
