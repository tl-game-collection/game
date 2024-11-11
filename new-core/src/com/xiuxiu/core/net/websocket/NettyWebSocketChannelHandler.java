package com.xiuxiu.core.net.websocket;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.*;
import com.xiuxiu.core.net.filter.FloodRecode;
import com.xiuxiu.core.net.message.CommandId;
import com.xiuxiu.core.net.message.MessageReceive;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;

public class NettyWebSocketChannelHandler extends NettyServerChannelHandler {
    private final WebSocketServerHandshakerFactory factory;
    private WebSocketServerHandshaker handshaker;
    private final String path;

    public NettyWebSocketChannelHandler(ConnectionManager connectionManager, MessageReceive messageReceive, SessionContextFactory sessionContextFactory, WebSocketServerHandshakerFactory factory, String path) {
        super(connectionManager, messageReceive, sessionContextFactory);
        this.factory = factory;
        this.path = path;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logs.CONN.info("Netty connected conn=%s", ctx.channel());
        Connection conn = new NettyWebSocketConnection();
        conn.init(ctx.channel());
        conn.setSessionContext(this.sessionContextFactory.create());
        this.connectionManager.add(conn);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            WebSocketConnection conn = (WebSocketConnection) this.connectionManager.get(ctx.channel());
            conn.updateLastReadTime();
            if (msg instanceof FullHttpRequest) {
                this.handleHttpRequest(ctx, (FullHttpRequest) msg);
            } else if (msg instanceof WebSocketFrame) {
                this.handleWebSocketFrame(ctx, conn, (WebSocketFrame) msg);
            } else {
                ctx.close();
            }
        } finally {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Logs.CMD.error("[Net Read Exception channel=%s]", cause, ctx.channel());
        ctx.close();
    }

    private void handleHttpRequest(final ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        if (!req.decoderResult().isSuccess() || !HttpHeaderValues.WEBSOCKET.toString().equals(req.headers().get(HttpHeaderNames.UPGRADE))) {
            this.send(ctx, HttpResponseStatus.BAD_REQUEST, "");
            return;
        }
        if (!this.path.equals(req.uri())) {
            this.send(ctx, HttpResponseStatus.BAD_REQUEST, "");
            return;
        }
        this.handshaker = this.factory.newHandshaker(req);
        if (null == handshaker) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            this.handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketConnection conn, WebSocketFrame frame) throws Exception {
        conn.updateLastReadTime();
        if (frame instanceof CloseWebSocketFrame) {
            this.handshaker.close(ctx.channel(), ((CloseWebSocketFrame) frame).retain());
            frame.release();
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            conn.send(new PongWebSocketFrame(frame.content().retain()));
            frame.release();
            return;
        }
        if (!(frame instanceof BinaryWebSocketFrame)) {
            frame.release();
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        Object command = null;
        try {
            command = conn.decode(frame.content());
        } catch (Exception e) {
            Logs.CMD.error("[Net Read Not Command conn=%s parse data package]", e, conn);
        } finally {
            frame.release();
        }
        if (null == command) {
            Logs.CMD.error("[Net Read Not Command conn=%s data package is null]", conn);
            ctx.close();
            return;
        }
        if (command instanceof RequestWrapper) {
            if (CommandId.HEARTBEAT == ((RequestWrapper) command).getCommandId()) {
                conn.send(CommandId.HEARTBEAT, null);
                return;
            }
            if (CommandId.HEARTBEAT_CLIENT == ((RequestWrapper) command).getCommandId()) {
                conn.send(CommandId.HEARTBEAT_CLIENT, null);
                return;
            }
        }
        Logs.CMD.debug("[Net Read conn=%s command=%s]", conn, command);
        if (command instanceof ResponseWrapper) {
            InvokeFuture future = conn.removeInvokeFuture(((ResponseWrapper) command).getRequestId());
            if (null != future) {
                future.cancelTimeout();
                future.putResponse((ResponseWrapper) command);
                if (CommandId.ERROR == ((ResponseWrapper) command).getCommandId()) {
                    future.exception();
                } else {
                    future.exec();
                }
            }
        } else if (command instanceof RequestWrapper) {
            if (canReceive(ctx, conn, command)) {
                this.messageReceive.onReceive(conn, (RequestWrapper) command);
            }
        } else {
            Logs.CMD.error("[Net Read conn=%s command=%s 无效数据包]", conn, command);
            ctx.close();
        }
    }
    
    private boolean canReceive(ChannelHandlerContext ctx, WebSocketConnection conn, Object command) {
        long ms = System.currentTimeMillis();   // 当前毫秒
        long curSec = ms / 1000;                // 当前秒数
        long curMin = curSec / 60;              // 当前分钟
        
        FloodRecode floodCheck = ((NettyWebSocketConnection)conn).getFloodRecode();
        
        int flag = -1;  // 数据包记录状态
        long lastSec = floodCheck.lastPackTime / 1000;
        long lastMin = lastSec / 60;
        int lastMinPack = floodCheck.lastMinutePacks;
        int lastSecPack = floodCheck.lastSecendPacks;
        // 同一分钟时间, 判断是否到达临界
        if(lastMin == curMin) { 
            if(lastMinPack < FloodRecode.MAX_MINUTE_PACKS) { // 未到达分钟临界
                if(lastSec != curSec) {
                    flag = 1; // 重置秒
                } else {
                    if(lastSecPack < FloodRecode.MAX_SECEND_PACKS){
                        flag = 2; // +++
                    }
                }
            }
        } else {
            flag = 0;  // 重置
        }
        
        switch(flag) {
        case 0:
            floodCheck.lastMinutePacks = 0;
        case 1:
            floodCheck.lastSecendPacks = 0;
        case 2:
            floodCheck.lastSecendPacks ++;
            floodCheck.lastMinutePacks ++;
            floodCheck.lastPackTime = System.currentTimeMillis();
            break;
        default:
            Logs.NET.error("接受到数据包 - 秒: %d, 分: %d", floodCheck.lastSecendPacks, floodCheck.lastMinutePacks);
            Logs.NET.error("非法访问 - 客户端数据包发送频率过高, 关闭链接!!, IP[%s:command=%s]", conn, command);
            ctx.close();
            // 加入黑名单
            return false;
        }
        return true;
    }

    private void send(ChannelHandlerContext ctx, HttpResponseStatus status, String content) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.wrappedBuffer(content.getBytes()));
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
