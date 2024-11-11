package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.service.BaseService;
import com.xiuxiu.core.service.FutureListener;
import com.xiuxiu.core.service.ServiceException;
import com.xiuxiu.core.utils.OSUtil;
import com.xiuxiu.core.utils.StringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;

public abstract class NettyTcpServer extends BaseService implements Server {
    protected final AtomicReference<State> state = new AtomicReference<>(State.CREATED);
    protected final String host;
    protected final int port;
    protected EventLoopGroup acceptGroup;
    protected EventLoopGroup workerGroup;
    public NettyTcpServer(int port) {
        this("0.0.0.0", port);
    }

    public NettyTcpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void init() {
        if (!this.state.compareAndSet(State.CREATED, State.INITIALIZED)) {
            throw new ServiceException("Server already init");
        }
    }

    @Override
    public boolean running() {
        return State.STARTED == this.state.get();
    }

    @Override
    protected void doStart(FutureListener serviceListener) {
        if (!this.state.compareAndSet(State.INITIALIZED, State.STARTING)) {
            serviceListener.onFail(new ServiceException("Server already started or have not init"));
            Logs.NET.error("Server: %s already started or have not init", this.getClass().getSimpleName());
            return;
        }
        if (this.useEpoll()) {
            this.createEpollServer(serviceListener);
        } else {
            this.createNioServer(serviceListener);
        }
    }

    @Override
    protected void doStop(FutureListener serviceListener) {
        if (!this.state.compareAndSet(State.STARTED, State.SHUTDOWN)) {
            serviceListener.onFail(new ServiceException("Server already shutdown."));
            Logs.NET.error("Server: %s already shutdown", this.getClass().getSimpleName());
            return;
        }
        Logs.NET.info("Server:%s try shutdown ...", this.getClass().getSimpleName());
        if (null != this.acceptGroup) {
            this.acceptGroup.shutdownGracefully().syncUninterruptibly();
        }
        if (null != this.workerGroup) {
            this.workerGroup.shutdownGracefully().syncUninterruptibly();
        }
        Logs.NET.info("Server:%s shutdown success.", this.getClass().getSimpleName());
        serviceListener.onSucc(this.host, this.port);
    }

    protected void createEpollServer(FutureListener listener) {
        EventLoopGroup acceptGroup = this.getAcceptGroup();
        EventLoopGroup workerGroup = this.getWorkerGroup();

        if (null == acceptGroup) {
            EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(this.getAcceptThreadNum(), this.getAcceptThreadFactory());
            epollEventLoopGroup.setIoRatio(100);
            acceptGroup = epollEventLoopGroup;
        }

        if (null == workerGroup) {
            EpollEventLoopGroup epollEventLoopGroup = new EpollEventLoopGroup(this.getWorkThreadNum(), this.getWorkThreadFactory());
            epollEventLoopGroup.setIoRatio(this.getIoRate());
            workerGroup = epollEventLoopGroup;
        }

        this.createServer(listener, acceptGroup, workerGroup, new ChannelFactory<ServerChannel>() {
            @Override
            public ServerChannel newChannel() {
                return new EpollServerSocketChannel();
            }
        });
    }

    protected void createNioServer(FutureListener listener) {
        EventLoopGroup acceptGroup = this.getAcceptGroup();
        EventLoopGroup workerGroup = this.getWorkerGroup();

        if (null == acceptGroup) {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(this.getAcceptThreadNum(), this.getAcceptThreadFactory());
            nioEventLoopGroup.setIoRatio(100);
            acceptGroup = nioEventLoopGroup;
        }

        if (null == workerGroup) {
            NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup(this.getWorkThreadNum(), this.getWorkThreadFactory());
            nioEventLoopGroup.setIoRatio(this.getIoRate());
            workerGroup = nioEventLoopGroup;
        }

        this.createServer(listener, acceptGroup, workerGroup, new ChannelFactory<ServerChannel>() {
            @Override
            public ServerChannel newChannel() {
                return new NioServerSocketChannel();
            }
        });
    }

    protected void createServer(FutureListener listener, EventLoopGroup accept, EventLoopGroup work, ChannelFactory<? extends ServerChannel> channelFactory) {
        this.acceptGroup = accept;
        this.workerGroup = work;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(this.acceptGroup, this.workerGroup);
            b.channelFactory(channelFactory);
            b.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    initPipeline(ch.pipeline());
                }
            });

            this.initOptions(b);

            InetSocketAddress addr = StringUtil.isEmptyOrNull(this.host) ? new InetSocketAddress(this.port) : new InetSocketAddress(this.host, this.port);

            NettyTcpServer self = this;
            b.bind(addr).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        state.set(State.STARTED);
                        listener.onSucc(host, port);
                        Logs.NET.info("Server: %s start success on: %s:%d", self.getClass().getSimpleName(), host, port);
                    } else {
                        listener.onFail(future.cause());
                        Logs.NET.error("Server: %s start failure on: %s:%d", future.cause(), self.getClass().getSimpleName(), host, port);
                    }
                }
            });
        } catch (Exception e) {
            listener.onFail(e);
            Logs.NET.error("Server: %s start failure on: %s:%d", e, this.getClass().getSimpleName(), this.host, this.port);
            throw new ServiceException("Server: %s start exception, host=" + this.host + " port=" + this.port, e);
        }
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

    protected void initOptions(ServerBootstrap b) {
        b.childOption(ChannelOption.TCP_NODELAY, true);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);
        b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(32 * 1024, 64 * 1024));
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.option(ChannelOption.SO_REUSEADDR, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("decoder", this.getDecoder());
        pipeline.addLast("encoder", this.getEncoder());
        pipeline.addLast("handler", this.getChannelHandler());
    }

    protected abstract ChannelHandler getDecoder();

    protected abstract ChannelHandler getEncoder();

    protected abstract ChannelHandler getChannelHandler();

    protected int getIoRate() {
        return 70;
    }

    protected ThreadFactory getAcceptThreadFactory() {
        return new DefaultThreadFactory(this.getAcceptThreadName());
    }

    protected ThreadFactory getWorkThreadFactory() {
        return new DefaultThreadFactory(this.getWorkThreadName());
    }

    protected int getAcceptThreadNum() {
        return 1;
    }

    protected int getWorkThreadNum() {
        return 0;
    }

    protected String getAcceptThreadName() {
        return "NettyAccept";
    }

    protected String getWorkThreadName() {
        return "NettyWorker";
    }

    public EventLoopGroup getAcceptGroup() {
        return this.acceptGroup;
    }

    public EventLoopGroup getWorkerGroup() {
        return this.workerGroup;
    }

    public enum State {
        CREATED,
        INITIALIZED,
        STARTING,
        STARTED,
        SHUTDOWN;
    }
}
