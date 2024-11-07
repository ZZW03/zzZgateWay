package com.zzz.gateway.netty.process;

import com.zzz.gateway.context.HttpRequestWrapper;
import io.netty.handler.codec.http.FullHttpRequest;

public interface NettyProcessor {

    void process(HttpRequestWrapper request);

}
