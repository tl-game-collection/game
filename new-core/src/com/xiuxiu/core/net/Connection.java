package com.xiuxiu.core.net;

import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.net.protocol.Protocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public interface Connection {
    byte STATUS_NEW = 0;
    byte STATUS_CONNECTED = 1;
    byte STATUS_DISCONNECTED = 2;
    int TIMEOUT_TIMES = 30;

    void init(Channel channel);

    long getId();

    Channel getChannel();

    ChannelFuture send(int commandId, Object message);

    ChannelFuture send(int commandId, Object message, Protocol.Version version);

    ChannelFuture send(ResponseWrapper message);

    void sendWithCallback(int commandId, Object message, InvokeCallback callback);

    void sendWithCallback(int commandId, Object message, InvokeCallback callback, int timeoutMillis);

    void sendWithCallback(int commandId, Object message, Protocol.Version version, InvokeCallback callback);

    void sendWithCallback(int commandId, Object message, Protocol.Version version, InvokeCallback callback, int timeoutMillis);

    ChannelFuture close();

    boolean isConnected();

    boolean isReadTimeout();

    boolean isWriteTimeout();

    void updateLastReadTime();

    void updateLastWriteTime();

    int heartbeat();

    void addInvokeFuture(InvokeFuture future);

    InvokeFuture removeInvokeFuture(int requestId);

    int timeout();

    int getTimeoutTimes();

    void clearTimeout();

    String getRemoteAddr();

    String getRemoteIp();

    SessionContext getSessionContext();

    void setSessionContext(SessionContext context);

    Protocol.Version getProtocolVersion();
}
