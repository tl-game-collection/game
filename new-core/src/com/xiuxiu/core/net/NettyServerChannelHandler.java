package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.message.CommandId;
import com.xiuxiu.core.net.message.MessageReceive;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyServerChannelHandler extends ChannelInboundHandlerAdapter {
    protected final ConnectionManager connectionManager;
    protected final MessageReceive messageReceive;
    protected final SessionContextFactory sessionContextFactory;

    public NettyServerChannelHandler(ConnectionManager connectionManager, MessageReceive messageReceive, SessionContextFactory sessionContextFactory) {
        this.connectionManager = connectionManager;
        this.messageReceive = messageReceive;
        this.sessionContextFactory = sessionContextFactory;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logs.CONN.info("Netty connected conn=%s", ctx.channel());
        Connection conn = new NettyConnection();
        conn.init(ctx.channel());
        conn.setSessionContext(this.sessionContextFactory.create());
        this.connectionManager.add(conn);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            Connection conn = this.connectionManager.get(ctx.channel());
            conn.updateLastReadTime();
            if (msg instanceof RequestWrapper) {
                if (CommandId.HEARTBEAT == ((RequestWrapper) msg).getCommandId()) {
                    conn.send(CommandId.HEARTBEAT, null);
                    return;
                }
                if (CommandId.HEARTBEAT_CLIENT == ((RequestWrapper) msg).getCommandId()) {
                    conn.send(CommandId.HEARTBEAT_CLIENT, null);
                    return;
                }
            }
            Logs.CMD.debug("Netty channelRead conn=%s, message=%s", conn, msg);
            if (msg instanceof ResponseWrapper) {
                InvokeFuture future = conn.removeInvokeFuture(((ResponseWrapper) msg).getRequestId());
                if (null != future) {
                    future.cancelTimeout();
                    future.putResponse((ResponseWrapper) msg);
                    if (CommandId.ERROR == ((ResponseWrapper) msg).getCommandId()) {
                        future.exception();
                    } else {
                        future.exec();
                    }
                }
            } else {
                this.messageReceive.onReceive(conn, (RequestWrapper) msg);
            }
        } finally {

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection conn = this.connectionManager.get(ctx.channel());
        Logs.CONN.error("Netty caught, conn=%s", conn);
        Logs.NET.error("Netty caught, channel=%s, conn=%s", cause, ctx.channel(), conn);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection conn = this.connectionManager.removeAndClose(ctx.channel());
        Logs.CONN.info("Netty disconnected conn=%s", conn);
    }
}
