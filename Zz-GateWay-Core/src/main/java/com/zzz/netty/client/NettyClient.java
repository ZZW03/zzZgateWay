package com.zzz.netty.client;

import com.zzz.config.Config;
import com.zzz.netty.NettyApi;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

import java.io.IOException;

@Slf4j
public class NettyClient implements NettyApi {

    // 配置信息对象，包含HTTP客户端的配置参数
    private final Config config;

    // Netty的事件循环组，用于处理客户端的网络事件
    private final EventLoopGroup eventLoopGroupWoker;

    // 异步HTTP客户端实例
    private AsyncHttpClient asyncHttpClient;

    public NettyClient(Config config, EventLoopGroup eventLoopGroupWoker) {
        this.config = config;
        this.eventLoopGroupWoker = eventLoopGroupWoker;
        init(config);  // 初始化客户端
    }

    @Override
    public void init(Config config) {
        // 创建异步HTTP客户端配置的构建器
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setEventLoopGroup(eventLoopGroupWoker) // 使用传入的Netty事件循环组
                .setConnectTimeout(config.getHttpConnectTimeout()) // 连接超时设置
                .setRequestTimeout(config.getHttpRequestTimeout()) // 请求超时设置
                .setMaxRedirects(config.getHttpMaxRequestRetry()) // 最大重定向次数
                .setAllocator(PooledByteBufAllocator.DEFAULT) // 使用池化的ByteBuf分配器以提升性能
                .setCompressionEnforced(true) // 强制压缩
                .setMaxConnections(config.getHttpMaxConnections()) // 最大连接数
                .setMaxConnectionsPerHost(config.getHttpConnectionsPerHost()) // 每个主机的最大连接数
                .setPooledConnectionIdleTimeout(config.getHttpPooledConnectionIdleTimeout()); // 连接池中空闲连接的超时时间
        // 根据配置创建异步HTTP客户端
        this.asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
    }

    @Override
    public void start() {
        AsyncHttpHelper.getInstance().initialized(asyncHttpClient);
    }

    @Override
    public void shutdown() {
        // 如果客户端实例不为空，则尝试关闭它
        if (asyncHttpClient != null) {
            try {
                // 关闭客户端，并处理可能的异常
                this.asyncHttpClient.close();
            } catch (IOException e) {
                // 记录关闭时发生的错误
                log.error("NettyHttpClient shutdown error", e);
            }
        }
    }
}
