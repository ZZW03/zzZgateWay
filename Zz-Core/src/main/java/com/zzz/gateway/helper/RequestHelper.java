package com.zzz.gateway.helper;


import com.zzz.common.config.HttpServiceInvoker;
import com.zzz.common.config.Rule;
import com.zzz.common.config.ServiceDefinition;
import com.zzz.common.config.ServiceInvoker;
import com.zzz.common.constant.BasicConst;
import com.zzz.common.constant.GatewayConst;
import com.zzz.common.exception.ResponseException;
import com.zzz.gateway.config.DynamicConfigManager;
import com.zzz.gateway.context.GatewayContext;
import com.zzz.gateway.request.GatewayRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.zzz.common.enums.ResponseCode.PATH_NO_MATCHED;


/**

 * RequestHelper
 */
public class RequestHelper {

    public static GatewayContext doContext(FullHttpRequest request,
                                           ChannelHandlerContext ctx) {

        //	构建请求对象GatewayRequest
        GatewayRequest gateWayRequest = doRequest(request, ctx);

        // 在项目开始的时候会将注册中心的所有实例全部注册在本地缓中
        // 根据请求对象里的uniqueId，获取资源服务信息(也就是服务定义信息)
        ServiceDefinition serviceDefinition =
                DynamicConfigManager.getInstance().getServiceDefinition(gateWayRequest.getUniqueId());


        //	根据请求对象获取服务定义对应的方法调用，然后获取对应的规则
        ServiceInvoker serviceInvoker = new HttpServiceInvoker();
        serviceInvoker.setInvokerPath(gateWayRequest.getPath());
        serviceInvoker.setTimeout(500);

        //根据请求对象获取规则
        Rule rule = getRule(gateWayRequest, serviceDefinition.getServiceId());

        //	构建我们而定GateWayContext对象
        GatewayContext gatewayContext = new GatewayContext(serviceDefinition.getProtocol(), ctx,
                HttpUtil.isKeepAlive(request), gateWayRequest, rule,0);


        //后续服务发现做完，这里都要改成动态的--以及在负载均衡算法实现
        //gatewayContext.getRequest().setModifyHost("127.0.0.1:8080");

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
        // 判断是get 还是post
        HttpMethod method = fullHttpRequest.method();

        //返回的是请求地址
        String uri = fullHttpRequest.uri();

        //获取访问的ip地址
        String clientIp = getClientIp(ctx, fullHttpRequest);

        // 用于获得Content-Type类型 application/json 或者text/plain
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null :
                HttpUtil.getMimeType(fullHttpRequest).toString();

        //获取编码类型
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);

        //简历上下文
        GatewayRequest gatewayRequest = new GatewayRequest(uniqueId, charset, clientIp,
                host, uri, method,
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
            //获得ip地址
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
        Rule rule = DynamicConfigManager.getInstance().getRuleByPath(key);

        if (rule != null) {
            return rule;
        }
        return DynamicConfigManager.getInstance().getRuleByServiceId(serviceId).stream()
                .filter(r -> gateWayRequest.getPath().startsWith(r.getPrefix())).findAny()
                .orElseThrow(() -> new ResponseException(PATH_NO_MATCHED));
    }

}
