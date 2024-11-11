package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.service.BaseService;
import com.xiuxiu.core.service.FutureListener;
import com.xiuxiu.core.utils.OSUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;

public abstract class NettyTcpClient extends BaseService implements Client {
    protected EventLoopGroup workerGroup;
    protected Bootstrap bootstrap;
    protected ChannelFuture channelFuture;
    protected CountDownLatch countDownLatch;
    protected String ip;
    protected int port;

    public ChannelFuture connect(String host, int port) {
        this.countDownLatch = new CountDownLatch(1);
        this.ip = host;
        this.port = port;
        this.channelFuture = this.bootstrap.connect(new InetSocketAddress(host, port));
        return channelFuture;
    }

    public abstract Connection getConnection();

    public void await() throws InterruptedException {
        if (null != this.countDownLatch) {
            this.countDownLatch.await();
        }
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    protected void doStart(FutureListener serviceListener) {
        if (this.useEpoll()) {
            this.createEpollClient(serviceListener);
        } else {
            this.createNioClient(serviceListener);
        }
    }

    @Override
    protected void doStop(FutureListener serviceListener) {
        if (null != this.workerGroup) {
            this.workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        serviceListener.onSucc();
        Logs.NET.info("NettyClient: %s stopped.", this.getClass().getSimpleName());
    }

    protected void createEpollClient(FutureListener listener) {
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(this.getThreadNum(), this.getThreadFactory());
        workerGroup.setIoRatio(this.getIoRate());
        this.createClient(listener, workerGroup, new ChannelFactory<Channel>() {
            @Override
            public Channel newChannel() {
                return new EpollSocketChannel();
            }
        });
    }

    protected void createNioClient(FutureListener listener) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(this.getThreadNum(), this.getThreadFactory());
        workerGroup.setIoRatio(this.getIoRate());
        this.createClient(listener, workerGroup, new ChannelFactory<Channel>() {
            @Override
            public Channel newChannel() {
                return new NioSocketChannel();
            }
        });
    }

    protected void createClient(FutureListener listener, EventLoopGroup worker, ChannelFactory<? extends Channel> channelFactory) {
        this.workerGroup = worker;
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.workerGroup);
        this.bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        this.bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 50000);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.channelFactory(channelFactory);
        this.bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                initPipeline(ch.pipeline());
            }
        });
        this.initOptions(this.bootstrap);
        listener.onSucc();
    }

    protected boolean useEpoll() {
        if (OSUtil.isLinux()) {
            try {
                Native.offsetofEpollData();
                return true;
            } catch (UnsatisfiedLinkError e) {
                Logs.NET.warn("can't load netty epoll, switch nio model.");
            }
        }
        return false;
    }

    protected void initOptions(Bootstrap bootstrap) {
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", this.getDecoder());
        pipeline.addLast("encoder", this.getEncoder());
        pipeline.addLast("handler", this.getChannelHandler());
    }

    protected abstract ChannelHandler getDecoder();

    protected abstract ChannelHandler getEncoder();

    protected abstract ChannelHandler getChannelHandler();

    protected String getThreadName() {
        return "NettyClient";
    }

    protected int getIoRate() {
        return 50;
    }

    protected int getThreadNum() {
        return 1;
    }

    private ThreadFactory getThreadFactory() {
        return new DefaultThreadFactory(this.getThreadName());
    }
}
