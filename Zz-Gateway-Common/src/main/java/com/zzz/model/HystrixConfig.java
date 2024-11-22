package com.zzz.model;

public class HystrixConfig {

    /**
     * 超时时间
     */
    private int timeoutInMilliseconds;

    /**
     * 核心线程数量
     */
    private int threadCoreSize;

    /**
     * 熔断降级响应
     */
    private String fallbackResponse;


    public HystrixConfig(int timeoutInMilliseconds, int threadCoreSize, String fallbackResponse) {
        this.timeoutInMilliseconds = timeoutInMilliseconds;
        this.threadCoreSize = threadCoreSize;
        this.fallbackResponse = fallbackResponse;
    }

    public HystrixConfig() {
    }

    public int getTimeoutInMilliseconds() {
        return timeoutInMilliseconds;
    }

    public void setTimeoutInMilliseconds(int timeoutInMilliseconds) {
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    public int getThreadCoreSize() {
        return threadCoreSize;
    }

    public void setThreadCoreSize(int threadCoreSize) {
        this.threadCoreSize = threadCoreSize;
    }

    public String getFallbackResponse() {
        return fallbackResponse;
    }

    public void setFallbackResponse(String fallbackResponse) {
        this.fallbackResponse = fallbackResponse;
    }
}
