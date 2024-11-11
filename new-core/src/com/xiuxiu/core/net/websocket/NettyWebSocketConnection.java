package com.xiuxiu.core.net.websocket;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.DefaultInvokeFuture;
import com.xiuxiu.core.net.InvokeCallback;
import com.xiuxiu.core.net.InvokeFuture;
import com.xiuxiu.core.net.NettyConnection;
import com.xiuxiu.core.net.codec.NettyByteBufferWrapper;
import com.xiuxiu.core.net.filter.FloodRecode;
import com.xiuxiu.core.net.message.MessageType;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.net.protocol.Protocol;
import com.xiuxiu.core.net.protocol.ProtocolException;
import com.xiuxiu.core.net.protocol.ProtocolFactory;
import com.xiuxiu.core.utils.TimerHolder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.TimeUnit;

public class NettyWebSocketConnection extends NettyConnection implements WebSocketConnection {

    protected FloodRecode floodRecode = new FloodRecode();
    
    public NettyWebSocketConnection () {
        this.protocolVersion = Protocol.Version.SERVICE;
    }

    public NettyWebSocketConnection (Protocol.Version version) {
        this.protocolVersion = version;
    }

    @Override
    public ChannelFuture send(WebSocketFrame msg) {
        if (this.channel.isActive()) {
            ChannelFuture future = this.channel.writeAndFlush(msg).addListener(this);
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
    public ChannelFuture send(int commandId, Object message, Protocol.Version version) {
        if (this.channel.isActive()) {
            RequestWrapper request = new RequestWrapper(MessageType.ONEWAY, version, commandId, message);
            // final NettyConnection self = this;
            try {
                BinaryWebSocketFrame frame = this.encode(request);
//                System.out.println("commandId="+Integer.toHexString(commandId)+",size="+frame.content().array().length);
                ChannelFuture future = this.channel.writeAndFlush(frame).addListener(this);
                if (channel.isWritable()) {
                    return future;
                }
                //if (!future.channel().eventLoop().inEventLoop()) {
                //    future.awaitUninterruptibly(100);
                //}
                return future;
            } catch (Exception e) {
                Logs.CONN.error("%s encode data package err", e, this);
                return this.close();
            }finally {
            }

        } else {
            Logs.CONN.warn("%s this connection is unActive", this);
            return this.close();
        }
    }

    @Override
    public ChannelFuture send(ResponseWrapper message) {
        if (this.channel.isActive()) {
            try {
                BinaryWebSocketFrame frame = this.encode(message);
                ChannelFuture future = this.channel.writeAndFlush(frame).addListener(this);
                if (channel.isWritable()) {
                    return future;
                }
                if (!future.channel().eventLoop().inEventLoop()) {
                    future.awaitUninterruptibly(100);
                }
                return future;
            } catch (Exception e) {
                Logs.CONN.error("%s encode data package err", e, this);
                return this.close();
            }
        } else {
            return this.close();
        }
    }

    @Override
    public void sendWithCallback(int commandId, Object message, Protocol.Version version, InvokeCallback callback, int timeoutMillis) {
        RequestWrapper request = new RequestWrapper(version, commandId, message, timeoutMillis);
        InvokeFuture future = new DefaultInvokeFuture(request.getRequestId(), callback);
        this.addInvokeFuture(future);
        try {
            BinaryWebSocketFrame frame = this.encode(request);
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
            this.channel.writeAndFlush(frame).addListener(new ChannelFutureListener() {
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
    public Object decode(ByteBuf byteBuf) throws Exception {
        NettyByteBufferWrapper nettyByteBufferWrapper = new NettyByteBufferWrapper(byteBuf);
        while (nettyByteBufferWrapper.isReadable()) {
            if (nettyByteBufferWrapper.readableBytes() < 16) {
                break;
            }
            nettyByteBufferWrapper.markReaderIndex();
            Protocol.Version protocolVersion = Protocol.Version.parse(nettyByteBufferWrapper.readByte());
            if (null == protocolVersion) {
                nettyByteBufferWrapper.resetReaderIndex();
                throw new ProtocolException("Protocol version not exists.");
            }
            Protocol protocol = ProtocolFactory.getProtocol(protocolVersion);
            if (null == protocol) {
                nettyByteBufferWrapper.resetReaderIndex();
                throw new ProtocolException("Protocol version not exists.");
            }
            Object msg = protocol.decode(nettyByteBufferWrapper);
            if (null == msg) {
                nettyByteBufferWrapper.resetReaderIndex();
                break;
            }
            return msg;
        }
        return null;
    }

    protected BinaryWebSocketFrame encode(Object msg) throws Exception {
        try {
            Protocol.Version protocolVersion = null;
            if (msg instanceof RequestWrapper) {
                protocolVersion = ((RequestWrapper) msg).getProtocolVersion();
            } else if (msg instanceof ResponseWrapper) {
                protocolVersion = ((ResponseWrapper) msg).getProtocolVersion();
            }
            Protocol protocol = ProtocolFactory.getProtocol(protocolVersion);
            if (null == protocol) {
                throw new ProtocolException("Protocol version not exists.");
            }
//            NettyByteBufferWrapper nettyByteBufferWrapper = new NettyByteBufferWrapper(buf);
            ByteBuf byteBuf = protocol.encode(msg);
            return new BinaryWebSocketFrame(byteBuf);
        } finally {
            
        }
    }
    
    public FloodRecode getFloodRecode() {
        return floodRecode;
    }
}
