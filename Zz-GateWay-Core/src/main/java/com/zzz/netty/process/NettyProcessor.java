package com.zzz.netty.process;

import com.zzz.model.HttpRequestWrapper;

public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);
}
