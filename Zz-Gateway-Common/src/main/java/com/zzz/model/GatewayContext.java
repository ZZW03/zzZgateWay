package com.zzz.model;

import io.micrometer.core.instrument.Timer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * 网关信息上下文
 */
public class GatewayContext extends BasicContext{

    private GatewayRequest request;

    private GatewayResponse response;

    private Rule rule;

    /**
     * 重试次数
     */
    private int currentRetryTimes;

    /**
     * 灰度
     */
    private boolean gray;

    private Timer.Sample timerSample;


    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive,
                          GatewayRequest request, Rule rule, int currentRetryTimes){
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.rule = rule;
        this.currentRetryTimes = currentRetryTimes;
    }


    @Override
    public GatewayRequest getRequest() {
        return request;
    }

    public void setRequest(GatewayRequest request) {
        this.request = request;
    }

    @Override
    public GatewayResponse getResponse() {
        return response;
    }

    public void setResponse(GatewayResponse response) {
        this.response = response;
    }

    @Override
    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public int getCurrentRetryTimes() {
        return currentRetryTimes;
    }

    public void setCurrentRetryTimes(int currentRetryTimes) {
        this.currentRetryTimes = currentRetryTimes;
    }

    public boolean isGray() {
        return gray;
    }

    public void setGray(boolean gray) {
        this.gray = gray;
    }

    public Timer.Sample getTimerSample() {
        return timerSample;
    }

    public void setTimerSample(Timer.Sample timerSample) {
        this.timerSample = timerSample;
    }

    public Filter getFilterConfigById(Long filterId){
        return  rule.getFilterConfigById(filterId);
    }

    public String getUniqueId(){
        return request.getUniqueId();
    }


    /**
     * 重写父类释放资源方法，用于正在释放资源
     * release() 方法通常减少对象的引用计数。当计数达到零时，资源被释放。
     */
    public void releaseRequest(){
        if(requestReleased.compareAndSet(false,true)){
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
    }

    /**
     * 获取原始的请求对象
     * @return
     */
    public GatewayRequest getOriginRequest(){
        return  request;
    }

}
