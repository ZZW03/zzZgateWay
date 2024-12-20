package com.zzz.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zzz.constant.ResponseCode;
import com.zzz.util.JSONUtil;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.asynchttpclient.Response;



@Data
public class GatewayResponse {

    /**
     * 响应头
     */
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    /**
     * 额外的响应结果
     */
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    /**
     * 响应内容
     */
    private String content;

    /**
     * 异步返回对象
     */
    private Response futureResponse;

    /**
     * 响应返回码
     */
    private HttpResponseStatus httpResponseStatus;


    public GatewayResponse() {

    }

    /**
     * 设置响应头信息
     *
     * @param key
     * @param val
     */
    public void putHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

    /**
     * 构建异步响应对象
     *
     * @param futureResponse
     * @return
     */
    public static GatewayResponse buildGatewayResponse(Response futureResponse) {
        GatewayResponse response = new GatewayResponse();
        response.setFutureResponse(futureResponse);
        response.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return response;
    }


    /**
     * 处理返回json对象，成功时调用
     *
     * @param data
     * @return
     */
    public static GatewayResponse buildGatewayResponse(Object data) {
        ObjectNode objectNode = JSONUtil.createObjectNode();

        if (data instanceof ResponseCode) {
            ResponseCode code = (ResponseCode) data;
            objectNode.put(JSONUtil.STATUS, code.getStatus().code());
            objectNode.put(JSONUtil.CODE, code.getCode());
            objectNode.putPOJO(JSONUtil.DATA, code.getMessage());
        } else {
            objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
            objectNode.put(JSONUtil.CODE, ResponseCode.SUCCESS.getCode());
            objectNode.putPOJO(JSONUtil.DATA, data);
        }

        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));
        return response;
    }

}
