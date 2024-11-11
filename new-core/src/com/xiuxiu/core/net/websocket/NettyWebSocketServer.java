package com.xiuxiu.core.net.websocket;

import com.xiuxiu.core.net.*;
import com.xiuxiu.core.net.message.MessageDispatch;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

public class NettyWebSocketServer extends NettyTcpServer implements Server {
    protected final String PATH;
    protected ServerConnectionManager connectionManager;
    protected MessageDispatch messageReceive;
    protected SessionContextFactory sessionContextFactory;
    protected WebSocketServerHandshakerFactory factory;
    protected boolean enableSSL;

    public NettyWebSocketServer(String path, int port) {
        this(path, "0.0.0.0", port);
    }

    public NettyWebSocketServer(String path, String host, int port) {
        this(path, host, port, new ServerConnectionManager());
    }

    public NettyWebSocketServer(String path, String host, int port, ServerConnectionManager connectionManager) {
        this(path, host, port, connectionManager, new MessageDispatch());
    }

    public NettyWebSocketServer(String path, String host, int port, MessageDispatch messageReceive) {
        this(path, host, port, new ServerConnectionManager(), messageReceive);
    }

    public NettyWebSocketServer(String path, String host, int port, SessionContextFactory sessionContextFactory) {
        this(path, host, port, new ServerConnectionManager(), new MessageDispatch(), sessionContextFactory);
    }

    public NettyWebSocketServer(String path, String host, int port, ServerConnectionManager connectionManager, SessionContextFactory sessionContextFactory) {
        this(path, host, port, connectionManager, new MessageDispatch(), sessionContextFactory);
    }

    public NettyWebSocketServer(String path, String host, int port, ServerConnectionManager connectionManager, MessageDispatch messageReceive) {
        this(path, host, port, connectionManager, messageReceive, new DefaultSessionContextFactory());
    }

    public NettyWebSocketServer(String path, String host, int port, MessageDispatch messageReceive, SessionContextFactory sessionContextFactory) {
        this(path, host, port, new ServerConnectionManager(), messageReceive, sessionContextFactory);
    }

    public NettyWebSocketServer(String path, String host, int port, ServerConnectionManager connectionManager, MessageDispatch messageReceive, SessionContextFactory sessionContextFactory) {
        super(host, port);
        this.PATH = path;
        this.connectionManager = connectionManager;
        this.messageReceive = messageReceive;
        this.sessionContextFactory = sessionContextFactory;
    }

    @Override
    public void init() {
        super.init();
        this.connectionManager.init();
    }

    @Override
    protected void initPipeline(ChannelPipeline pipeline) {
        if (this.enableSSL) {
        }
        pipeline.addLast("decoder",new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(10 * 1024 * 1024));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
        pipeline.addLast("handler", this.getChannelHandler());
    }

    @Override
    protected ChannelHandler getDecoder() {
        return null;
    }

    @Override
    protected ChannelHandler getEncoder() {
        return null;
    }

    @Override
    protected ChannelHandler getChannelHandler() {
        if (null == this.factory) {
            this.factory = new WebSocketServerHandshakerFactory("wss://" + this.host + ":" + this.port + "/" + this.PATH, null, false);
        }
        return new NettyWebSocketChannelHandler(this.connectionManager, this.messageReceive, this.sessionContextFactory, this.factory, "/" + this.PATH);
    }

    public ServerConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public MessageDispatch getMessageReceive() {
        return messageReceive;
    }

    public static SSLContext createSSLContext(String type ,String path ,String password) throws Exception {
        KeyStore ks = KeyStore.getInstance(type); /// "JKS"
        InputStream ksInputStream = new FileInputStream(path); /// 证书存放地址
        ks.load(ksInputStream, password.toCharArray());
        //KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());//getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
        kmf.init(ks, password.toCharArray());
        //SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }
}
