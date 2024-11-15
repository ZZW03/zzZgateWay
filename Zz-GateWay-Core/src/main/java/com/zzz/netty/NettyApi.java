package com.zzz.netty;

import com.zzz.config.Config;

public interface NettyApi {

    void  init(Config config);

    void start();

    void shutdown();

}
