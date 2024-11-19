package com.zzz.netty.client;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.concurrent.CompletableFuture;

public class AsyncHttpHelper {

    public void initialized(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }

    private static class SingletonHolder {
        private static final AsyncHttpHelper INSTANCE = new AsyncHttpHelper();
    }

    public static AsyncHttpHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    AsyncHttpClient asyncHttpClient = null;

    public void init(AsyncHttpClient asyncHttpClient){
        this.asyncHttpClient = asyncHttpClient;
    }

    public CompletableFuture<Response> execute(Request request){
        ListenableFuture<Response> responseListenableFuture = asyncHttpClient.executeRequest(request);
        return responseListenableFuture.toCompletableFuture();
    }

}
