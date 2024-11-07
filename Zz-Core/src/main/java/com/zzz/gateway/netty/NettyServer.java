package com.zzz.gateway.netty;

import com.zzz.gateway.config.Config;
import com.zzz.gateway.lifecycle.NettyLifeCycle;
import com.zzz.gateway.netty.handle.NettyHttpServerHandler;
import com.zzz.gateway.netty.handle.NettyServerConnectManagerHandler;
import com.zzz.gateway.netty.process.NettyProcessor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
@Getter
public class NettyServer implements NettyLifeCycle {

    private final Config config;

    // boss线程组，用于处理新的客户端连接
    private EventLoopGroup eventLoopGroupBoss;

    // worker线程组，用于处理已经建立的连接的后续操作
    private EventLoopGroup eventLoopGroupWoker;

    // 服务器引导类，用于配置和启动Netty服务
    private ServerBootstrap serverBootstrap;

    // 自定义的处理器
    private final NettyProcessor nettyProcessor;

    public NettyServer(Config config, NettyProcessor nettyProcessor, ServerBootstrap serverBootstrap, EventLoopGroup eventLoopGroupWoker, EventLoopGroup eventLoopGroupBoss) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        this.serverBootstrap = serverBootstrap;
        this.eventLoopGroupWoker = eventLoopGroupWoker;
        this.eventLoopGroupBoss = eventLoopGroupBoss;
    }

    @Override
    public void init() {

        this.serverBootstrap = new ServerBootstrap();

        if(Epoll.isAvailable()){
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(), new DefaultThreadFactory("epoll-netty-boss"));
            this.eventLoopGroupWoker = new EpollEventLoopGroup(config.getEventLoopGroupWokerNum(),new DefaultThreadFactory("epoll-netty-woker"));
        }else{
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(),new DefaultThreadFactory("nio-netty-boss"));
            this.eventLoopGroupWoker = new NioEventLoopGroup(config.getEventLoopGroupWokerNum(),new DefaultThreadFactory("nio-netty-woker"));
        }
    }

    @Override
    public void start() {
        // 配置服务器参数，如端口、TCP参数等
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
                                new HttpObjectAggregator(config.getMaxContentLength()), // 聚合HTTP请求
                                new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                new NettyHttpServerHandler(nettyProcessor), // 自定义的处理器
                                new NettyServerConnectManagerHandler() // 连接管理处理器
                        );
                    }
                });

        // 绑定端口并启动服务，等待服务端关闭
        try {
            this.serverBootstrap.bind().sync();
            //也可以用这种方法进行netty端口监听的绑定
            //this.serverBootstrap.bind(config.getPort()).sync();
            log.info("server startup on port {}", this.config.getPort());
        } catch (Exception e) {
            throw new RuntimeException("启动服务器时发生异常", e);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void reStart() {

    }
}
