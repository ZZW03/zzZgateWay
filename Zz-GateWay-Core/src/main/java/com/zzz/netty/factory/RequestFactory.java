package com.zzz.netty.factory;


import com.zzz.constant.BasicConst;
import com.zzz.constant.GatewayConst;
import com.zzz.holder.RulesHolder;
import com.zzz.holder.ServiceHolder;
import com.zzz.model.GatewayContext;
import com.zzz.model.GatewayRequest;
import com.zzz.model.Rule;
import com.zzz.model.ServiceDefinition;
import io.micrometer.common.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RequestFactory {

    public static GatewayContext doContext(FullHttpRequest request, ChannelHandlerContext ctx) {
        GatewayRequest gateWayRequest = doRequest(request, ctx);

        //	根据请求对象里的uniqueId，获取资源服务信息(也就是服务定义信息)
        ServiceDefinition serviceDefinition =
                ServiceHolder.getInstance().getServiceDefinitionByName(gateWayRequest.getUniqueId());

        //根据请求对象获取规则
        Rule rule = getRule(gateWayRequest, serviceDefinition.getServiceName());

        GatewayContext gatewayContext = new GatewayContext("http", ctx,
                HttpUtil.isKeepAlive(request), gateWayRequest, rule,0);

        return gatewayContext;
    }


    /**
     * 构建Request请求对象
     */
    private static GatewayRequest doRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {

        HttpHeaders headers = fullHttpRequest.headers();
        //	从header头获取必须要传入的关键属性 uniqueId
        String uniqueId = headers.get(GatewayConst.UNIQUE_ID);

        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.getMethod();
        String uri = fullHttpRequest.getUri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null :
                HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);

        GatewayRequest gatewayRequest = new GatewayRequest(uniqueId, charset, clientIp, host, uri, method,
                contentType, headers, fullHttpRequest);

        return gatewayRequest;
    }

    /**
     * 获取客户端ip
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {

        String xForwardedValue = request.headers().get(BasicConst.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if (StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if (values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }

        if (clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }


    /**
     * 根据请求对象获取Rule对象
     *
     * @param gateWayRequest 请求对象
     * @return
     */
    private static Rule getRule(GatewayRequest gateWayRequest, String serviceId) {
        String key = serviceId + "." + gateWayRequest.getPath();

        Rule rule = RulesHolder.getInstance().getRuleByName(key);
        if (rule != null) {
            return rule;
        }

        Optional<Rule> first = RulesHolder.getInstance().getRulesByPath(gateWayRequest.getPath()).stream().findFirst();
        if(first.isPresent()){
            return first.get();
        }

        return RulesHolder.getInstance().getPreRulesByPath(gateWayRequest.getPath()).stream().findFirst().get();
    }


}
