package com.zzz.netty.service;

import com.zzz.config.Config;
import com.zzz.netty.NettyApi;
import com.zzz.netty.process.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NettyServer implements NettyApi {

    private Config config;

    // 服务器引导类，用于配置和启动Netty服务
    private ServerBootstrap serverBootstrap;

    // boss线程组，用于处理新的客户端连接
    private EventLoopGroup eventLoopGroupBoss;

    // worker线程组，用于处理已经建立的连接的后续操作
    private EventLoopGroup eventLoopGroupWoker;

    NettyProcessor nettyProcessor;

    @Override
    public void init(Config config) {
        this.config = config;
        BuildEvenLoop();
    }


    @Override
    public void start() {
        this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWoker)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
                .localAddress(new InetSocketAddress(config.getPort())) // 绑定监听端口
                .childHandler(new ChannelInitializer<Channel>() {   // 定义处理新连接的管道初始化逻辑
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // 配置管道中的处理器，如编解码器和自定义处理器
                        ch.pipeline().addLast(
                                new HttpServerCodec(), // 处理HTTP请求的编解码器
                                new HttpObjectAggregator(config.getMaxLength()), // 聚合HTTP请求
                                new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                new NettyHttpServerHandler(nettyProcessor) // 自定义的处理器// 连接管理处理器
                        );
                    }
                });

        try {
            ChannelFuture sync = this.serverBootstrap.bind(config.getPort()).sync();
            if (sync.isSuccess()) {
                log.info("netty sever start by port {} successfully",config.getPort());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {

    }


    private void BuildEvenLoop() {
        this.serverBootstrap = new ServerBootstrap();
        if (Epoll.isAvailable()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("epoll-netty-boss-nio"));
            this.eventLoopGroupWoker = new EpollEventLoopGroup(config.getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("epoll-netty-woker-nio"));
        } else {
            // 否则使用默认的NIO模型
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),
                    new DefaultThreadFactory("default-netty-boss-nio"));
            this.eventLoopGroupWoker = new NioEventLoopGroup(config.getEventLoopGroupWorkerNum(),
                    new DefaultThreadFactory("default-netty-woker-nio"));
        }
    }


}
