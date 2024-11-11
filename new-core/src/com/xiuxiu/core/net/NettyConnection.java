package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.message.MessageType;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.net.protocol.Protocol;
import com.xiuxiu.core.utils.TimerHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NettyConnection implements Connection, ChannelFutureListener {
    protected static final AtomicLong ID = new AtomicLong(1);
    protected Channel channel;
    protected volatile byte status = STATUS_NEW;
    protected long lastReadTime;
    protected long lastWriteTime;
    protected long id;
    protected ConcurrentHashMap<Integer, InvokeFuture> allInvokeFuture = new ConcurrentHashMap<>();
    protected AtomicInteger timeoutTimes = new AtomicInteger(0);
    protected SessionContext context;
    protected Protocol.Version protocolVersion;

    public NettyConnection() {
        this.protocolVersion = Protocol.Version.SERVICE;
    }

    public NettyConnection(Protocol.Version version) {
        this.protocolVersion = version;
    }

    @Override
    public void init(Channel channel) {
        this.id = ID.getAndIncrement();
        this.channel = channel;
        this.lastReadTime = System.currentTimeMillis();
        this.status = STATUS_CONNECTED;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ChannelFuture send(int commandId, Object message) {
        return this.send(commandId, message, this.protocolVersion);
    }

    @Override
    public ChannelFuture send(int commandId, Object message, Protocol.Version version) {
        if (this.channel.isActive()) {
            RequestWrapper request = new RequestWrapper(MessageType.ONEWAY, version, commandId, message);
            final NettyConnection self = this;
            ChannelFuture future = this.channel.writeAndFlush(request).addListener(this);
            if (channel.isWritable()) {
                return future;
            }
            //if (!future.channel().eventLoop().inEventLoop()) {
            //    future.awaitUninterruptibly(100);
            //}
            return future;
        } else {
            Logs.CONN.warn("%s this connection is unActive", this);
            return this.close();
        }
    }

    @Override
    public ChannelFuture send(ResponseWrapper message) {
        if (this.channel.isActive()) {
            ChannelFuture future = this.channel.writeAndFlush(message).addListener(this);
            if (channel.isWritable()) {
                return future;
            }
            if (!future.channel().eventLoop().inEventLoop()) {
                future.awaitUninterruptibly(100);
            }
            return future;
        } else {
            return this.close();
        }
    }

    @Override
    public void sendWithCallback(int commandId, Object message, InvokeCallback callback) {
        this.sendWithCallback(commandId, message, callback, 3000);
    }

    @Override
    public void sendWithCallback(int commandId, Object message, InvokeCallback callback, int timeoutMillis) {
        this.sendWithCallback(commandId, message, this.protocolVersion, callback, timeoutMillis);
    }

    @Override
    public void sendWithCallback(int commandId, Object message, Protocol.Version version, InvokeCallback callback) {
        this.sendWithCallback(commandId, message, version, callback, 3000);
    }

    @Override
    public void sendWithCallback(int commandId, Object message, Protocol.Version version, InvokeCallback callback, int timeoutMillis) {
        RequestWrapper request = new RequestWrapper(version, commandId, message, timeoutMillis);
        InvokeFuture future = new DefaultInvokeFuture(request.getRequestId(), callback);
        this.addInvokeFuture(future);
        try {
            Timeout timeout = TimerHolder.getTimer().newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    InvokeFuture future = removeInvokeFuture(request.getRequestId());
                    if (null != future) {
                        Logs.CONN.warn("NettClient send timeout request:%s conn:%s", request, channel);
                        future.timeout();
                    }
                }
            }, timeoutMillis, TimeUnit.MILLISECONDS);
            future.addTimeout(timeout);
            this.channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        Logs.CONN.error("NettClient send failure request:%s conn:%s", request, channel);
                        InvokeFuture f = removeInvokeFuture(request.getRequestId());
                        if (null != f) {
                            f.cancelTimeout();
                            f.sendFail();
                        }
                    } else {
                        lastWriteTime = System.currentTimeMillis();
                    }
                }
            });
        } catch (Exception e) {
            Logs.CONN.error("NettClient send caught request:%s conn:%s", e, request, channel);
            InvokeFuture f = this.removeInvokeFuture(request.getRequestId());
            if (null != f) {
                f.cancelTimeout();
                f.sendFail();
            }
        }
    }

    @Override
    public void addInvokeFuture(InvokeFuture future) {
        this.allInvokeFuture.putIfAbsent(future.invokeId(), future);
    }

    @Override
    public InvokeFuture removeInvokeFuture(int requestId) {
        return this.allInvokeFuture.remove(requestId);
    }

    @Override
    public ChannelFuture close() {
        if (STATUS_DISCONNECTED == this.status) {
            return null;
        }
        ChannelFuture channelFuture = null;
        if (STATUS_CONNECTED == this.status) {
            channelFuture = this.channel.close();
        }
        this.status = STATUS_DISCONNECTED;
        return channelFuture;
    }

    @Override
    public boolean isConnected() {
        return STATUS_CONNECTED == this.status;
    }

    @Override
    public boolean isReadTimeout() {
        return System.currentTimeMillis() - this.lastReadTime > this.heartbeat() + 1000;
    }

    @Override
    public boolean isWriteTimeout() {
        return System.currentTimeMillis() - this.lastWriteTime > this.heartbeat() - 1000;
    }

    @Override
    public void updateLastReadTime() {
        this.lastReadTime = System.currentTimeMillis();
    }

    @Override
    public void updateLastWriteTime() {
        this.lastWriteTime = System.currentTimeMillis();
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (channelFuture.isSuccess()) {
            this.lastWriteTime = System.currentTimeMillis();
        } else {
            Logs.NET.error("channel:%s send fail:%s", channelFuture.channel(), channelFuture.cause());
        }
    }

    @Override
    public int heartbeat() {
        return 10000;
    }

    @Override
    public int timeout() {
        return this.timeoutTimes.incrementAndGet();
    }

    @Override
    public int getTimeoutTimes() {
        return this.timeoutTimes.get();
    }

    @Override
    public void clearTimeout() {
        this.timeoutTimes.set(0);
    }

    @Override
    public String getRemoteAddr() {
        return this.channel.remoteAddress().toString();
    }

    @Override
    public String getRemoteIp() {
        InetSocketAddress remoteAddr = (InetSocketAddress) this.channel.remoteAddress();
        if (null == remoteAddr) {
            return "127.0.0.1";
        }
        return remoteAddr.getAddress().getHostAddress();
    }

    @Override
    public SessionContext getSessionContext() {
        return this.context;
    }

    @Override
    public void setSessionContext(SessionContext context) {
        this.context = context;
    }

    @Override
    public Protocol.Version getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public String toString() {
        return "[id: " + this.id + ", status: " + this.status + ", conn:" + this.channel + ", lastReadTime: " + this.lastReadTime + ", lastWriteTime: " + this.lastWriteTime + ", Session:" + this.getSessionContext() + "]";
    }
}
