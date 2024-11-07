package com.zzz.gateway.lifecycle;

public interface NettyLifeCycle {

    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();

    /**
     * 重启
     */
    void reStart();
}
