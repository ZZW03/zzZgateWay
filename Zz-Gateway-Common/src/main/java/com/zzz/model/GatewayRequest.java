package com.zzz.model;

import com.jayway.jsonpath.JsonPath;
import com.zzz.constant.BasicConst;
import io.micrometer.common.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;

import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class GatewayRequest implements IGatewayRequest{

    /**
     * 服务ID
     */
    @Getter
    private final String uniqueId;


    /**
     * 请求进入网关时间
     */
    @Getter
    private final long beginTime;

    /**
     * 字符集不会变的
     */
    @Getter
    private final Charset charset;

    /**
     * 客户端的IP，主要用于做流控、黑白名单
     */
    @Getter
    private final String clientIp;

    /**
     * 请求的地址：IP：port
     */
    @Getter
    private final String host;


    /**
     *  请求的路径   /XXX/XXX/XX
     */
    @Getter
    private final String path;


    /**
     * URI：统一资源标识符，/XXX/XXX/XXX?attr1=value&attr2=value2
     * URL：统一资源定位符，它只是URI的子集一个实现
     */
    @Getter
    private final String uri;

    /**
     * 请求方法 post/put/GET
     */
    @Getter
    private final HttpMethod method;

    /**
     * 请求的格式
     */
    @Getter
    private final String contentType;

    /**
     * 请求头信息
     */
    @Getter
    private final HttpHeaders headers;

    /**
     * 参数解析器
     */
    @Getter
    private final QueryStringDecoder queryStringDecoder;

    /**
     * FullHttpRequest
     */
    @Getter
    private final FullHttpRequest fullHttpRequest;

    /**
     * 请求体
     */
    @Getter
    private String body;


    @Setter
    @Getter
    private long userId;

    /**
     * 请求Cookie
     */
    @Getter
    private Map<String, Cookie> cookieMap;

    /**
     * post请求定义的参数结合
     */
    @Getter
    private Map<String, List<String>> postParameters;


    /******可修改的请求变量***************************************/
    /**
     * 可修改的Scheme，默认是http://
     */
    private String modifyScheme;

    private String modifyHost;

    private String modifyPath;

    /**
     * 构建下游请求是的http请求构建器
     */
    private final RequestBuilder requestBuilder;

    public GatewayRequest(String uniqueId, Charset charset, String clientIp, String host, String uri, HttpMethod method, String contentType, HttpHeaders headers, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;
        this.beginTime = System.currentTimeMillis();
        this.charset = charset;
        this.clientIp = clientIp;
        this.host = host;
        this.uri = uri;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.fullHttpRequest = fullHttpRequest;
        this.queryStringDecoder = new QueryStringDecoder(uri,charset);
        this.path  = queryStringDecoder.path();
        this.modifyHost = host;
        this.modifyPath = path;
        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder = new RequestBuilder();
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());
        ByteBuf contentBuffer = fullHttpRequest.content();
        if(Objects.nonNull(contentBuffer)){
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }

    /**
     * 获取请求体
     * @return
     */
    public String getBody(){
        if(StringUtils.isEmpty(body)){
            body = fullHttpRequest.content().toString(charset);
        }
        return body;
    }

    /**
     * 获取Cookie
     * @param name
     * @return
     */
    public  io.netty.handler.codec.http.cookie.Cookie getCookie(String name){
        if(cookieMap == null){
            cookieMap = new HashMap<String, Cookie>();
            String cookieStr = getHeaders().get(HttpHeaderNames.COOKIE);
            if (StringUtils.isBlank(cookieStr)){
                return null;
            }
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for(io.netty.handler.codec.http.cookie.Cookie cookie: cookies){
                cookieMap.put(name,cookie);
            }
        }
        return cookieMap.get(name);
    }

    /**
     * 获取指定名词参数值
     * @param name
     * @return
     */
    public List<String> getQueryParametersMultiple(String name){
        return  queryStringDecoder.parameters().get(name);
    }

    /**
     * post请求获取指定名词参数值
     * @param name
     * @return
     */
    public List<String> getPostParametersMultiples(String name){
        String body = getBody();
        if(isFormPost()){
            if(postParameters == null){
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body,false);
                postParameters = paramDecoder.parameters();
            }
            if(postParameters == null || postParameters.isEmpty()){
                return null;
            }else{
                return   postParameters.get(name);
            }
        } else if (isJsonPost()){
            try {
                return readJsonPath(body,name);
            }catch (Exception e){
                log.error("JsonPath解析失败，JsonPath:{},Body:{},",name,body,e);
            }
        }
        return null;
    }

    public  boolean isFormPost(){
        return HttpMethod.POST.equals(method) &&
                (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) ||
                        contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    public  boolean isJsonPost(){
        return HttpMethod.POST.equals(method) && contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }


    public  List<String> readJsonPath(String body, String name) {
        // 使用 JsonPath 读取 JSON 路径
        Object jsonPathResult = JsonPath.read(body, name);

        // 创建一个新的 ArrayList
        List<String> resultList = new ArrayList<>();

        // 将 JsonPath 结果转换为字符串并添加到列表中
        resultList.add(jsonPathResult.toString());

        return resultList;
    }



    @Override
    public void setModifyHost(String modifyHost) {
        this.modifyHost = modifyHost;
    }

    @Override
    public String getModifyHost() {
        return modifyHost;
    }

    @Override
    public void setModifyPath(String modifyPath) {
        this.modifyPath = modifyPath;
    }

    @Override
    public String getModifyPath() {
        return modifyPath;
    }

    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name,value);
    }

    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name,value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name,value);
    }

    @Override
    public void addFormParam(String name, String value) {
        if(isFormPost()){
            requestBuilder.addFormParam(name,value);
        }
    }

    @Override
    public void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    @Override
    public String getFinalUrl() {
        return modifyScheme+modifyHost+modifyPath;
    }


    @Override
    public Request build() {
        requestBuilder.setUrl(getFinalUrl());
        //设置用户id 用于下游的服务使用
        requestBuilder.addHeader("userId", String.valueOf(userId));
        return requestBuilder.build();
    }

    @Override
    public String toString() {
        return "GatewayRequest{" +
                "uniqueId='" + uniqueId + '\'' +
                ", beginTime=" + beginTime +
                ", charset=" + charset +
                ", clientIp='" + clientIp + '\'' +
                ", host='" + host + '\'' +
                ", path='" + path + '\'' +
                ", uri='" + uri + '\'' +
                ", method=" + method +
                ", contentType='" + contentType + '\'' +
                ", headers=" + headers +
                ", queryStringDecoder=" + queryStringDecoder +
                ", fullHttpRequest=" + fullHttpRequest +
                ", body='" + body + '\'' +
                ", userId=" + userId +
                ", cookieMap=" + cookieMap +
                ", postParameters=" + postParameters +
                ", modifyScheme='" + modifyScheme + '\'' +
                ", modifyHost='" + modifyHost + '\'' +
                ", modifyPath='" + modifyPath + '\'' +
                ", requestBuilder=" + requestBuilder +
                '}';
    }
}
