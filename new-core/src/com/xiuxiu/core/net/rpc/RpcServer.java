package com.xiuxiu.core.net.rpc;

import com.xiuxiu.core.net.*;
import com.xiuxiu.core.net.message.MessageDispatch;
import io.netty.channel.ChannelHandler;

public class RpcServer extends NettyTcpServer {
    protected ServerConnectionManager connectionManager;
    protected MessageDispatch messageReceive;
    protected SessionContextFactory sessionContextFactory;

    public RpcServer(int port) {
        this("0.0.0.0", port);
    }

    public RpcServer(String host, int port) {
        this(host, port, new ServerConnectionManager());
    }

    public RpcServer(String host, int port, ServerConnectionManager connectionManager) {
        this(host, port, connectionManager, new MessageDispatch());
    }

    public RpcServer(String host, int port, MessageDispatch messageReceive) {
        this(host, port, new ServerConnectionManager(), messageReceive);
    }

    public RpcServer(String host, int port, SessionContextFactory sessionContextFactory) {
        this(host, port, new ServerConnectionManager(), new MessageDispatch(), sessionContextFactory);
    }

    public RpcServer(String host, int port, ServerConnectionManager connectionManager, SessionContextFactory sessionContextFactory) {
        this(host, port, connectionManager, new MessageDispatch(), sessionContextFactory);
    }

    public RpcServer(String host, int port, ServerConnectionManager connectionManager, MessageDispatch messageReceive) {
        this(host, port, connectionManager, messageReceive, new DefaultSessionContextFactory());
    }

    public RpcServer(String host, int port, MessageDispatch messageReceive, SessionContextFactory sessionContextFactory) {
        this(host, port, new ServerConnectionManager(), messageReceive, sessionContextFactory);
    }

    public RpcServer(String host, int port, ServerConnectionManager connectionManager, MessageDispatch messageReceive, SessionContextFactory sessionContextFactory) {
        super(host, port);
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
    protected ChannelHandler getDecoder() {
        return new NettyDecoder();
    }

    @Override
    protected ChannelHandler getEncoder() {
        return new NettyEncoder();
    }

    @Override
    protected ChannelHandler getChannelHandler() {
        return new NettyServerChannelHandler(this.connectionManager, this.messageReceive, this.sessionContextFactory);
    }

    public ServerConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public MessageDispatch getMessageReceive() {
        return messageReceive;
    }
}
