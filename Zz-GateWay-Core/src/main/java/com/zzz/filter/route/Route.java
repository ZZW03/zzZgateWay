package com.zzz.filter.route;

import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import com.zzz.constant.ResponseCode;
import com.zzz.exception.ConnectException;
import com.zzz.exception.ResponseException;
import com.zzz.filter.Filter;
import com.zzz.filter.FilterAspect;
import com.zzz.model.GatewayContext;
import com.zzz.model.GatewayResponse;
import com.zzz.model.HystrixConfig;
import com.zzz.netty.client.AsyncHttpHelper;
import com.zzz.netty.factory.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

/**
 * 路由过滤器
 */
@Slf4j
@FilterAspect(name = "route",id = "1",order = Integer.MAX_VALUE)
public class Route implements Filter {


    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        HystrixConfig hystrixConfig = ctx.getRule().getHystrixConfig();
        if (Objects.nonNull(hystrixConfig)) {
            log.info("走熔断请求");
            routeByHystrix(ctx,hystrixConfig);
        }else {
            route(ctx);
        }
    }



    private  CompletableFuture<Response> route(GatewayContext ctx){

        Request request = ctx.getRequest().build();

        //执行具体的请求 并得到一个CompleatableFuture对象用于帮助我们执行后续的处理
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().execute(request);

        future.whenComplete((response, throwable) -> {
            complete(request, response, throwable, ctx);
        });

        return null;

    }

    private void complete(Request request,
                          Response response,
                          Throwable throwable,
                          GatewayContext gatewayContext) {

        //请求已经处理完毕 释放请求资源
        gatewayContext.releaseRequest();

        try {
            //之前出现了异常 执行异常返回逻辑
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable, gatewayContext.getUniqueId(), url,
                            ResponseCode.HTTP_RESPONSE_ERROR));
                    gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                //没有出现异常直接正常返回
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable t) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", t);
        } finally {
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);
        }
    }

    private void routeByHystrix(GatewayContext ctx, HystrixConfig hystrixConfig) {

        HystrixCommand.Setter setter =
                HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(ctx.getUniqueId()))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(ctx.getRequest().getPath()))
                        .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                                .withCoreSize(hystrixConfig.getThreadCoreSize()))
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                                .withExecutionTimeoutInMilliseconds(hystrixConfig.getTimeoutInMilliseconds())
                                .withExecutionIsolationThreadInterruptOnTimeout(true)
                                .withExecutionTimeoutEnabled(true));


        new HystrixCommand<Object>(setter) {
            @Override
            protected Object run() throws Exception {
                // 在Hystrix命令中执行路由操作，这是实际的业务逻辑。
                route(ctx);
                return null;
            }

            @Override
            protected Object getFallback() {
                //超时的做法
                if (getExecutionException() instanceof HystrixTimeoutException) {
                    ctx.written();
                    log.info("请求超时");
                    ctx.setResponse(GatewayResponse.buildGatewayResponse(ResponseCode.GATEWAY_FALLBACK));
                    ResponseHelper.writeResponse(ctx);
                }else{
                    log.info("重新路由");
                    //根据规则在重新路由
                    String fallbackResponse = ctx.getRule().getHystrixConfig().getFallbackResponse();
                    ctx.getRequest().setModifyPath(fallbackResponse);
                    route(ctx);
                }
                return null;
            }
        }.execute(); // 执行Hystrix命令。

    }


}
