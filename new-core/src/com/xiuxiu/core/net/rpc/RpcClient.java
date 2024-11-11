package com.xiuxiu.core.net.rpc;

import com.xiuxiu.core.net.*;
import com.xiuxiu.core.net.message.MessageDispatch;
import io.netty.channel.ChannelHandler;

public class RpcClient extends NettyTcpClient {
    protected ClientConnectionManager connectionManager;
    protected MessageDispatch messageReceive;

    public RpcClient() {
        this(new MessageDispatch());
    }

    public RpcClient(MessageDispatch messageReceive) {
        this.connectionManager = new ClientConnectionManager();
        this.messageReceive = messageReceive;
    }

    @Override
    public Connection getConnection() {
        if (null == this.channelFuture) {
            return null;
        }
        return this.connectionManager.get(this.channelFuture.channel());
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
        return new NettyClientHandler(this.connectionManager, this.messageReceive, this.countDownLatch);
    }
}
