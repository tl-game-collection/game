package com.xiuxiu.core.net;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.message.CommandId;
import com.xiuxiu.core.net.message.MessageReceive;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.utils.TimerHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private ConnectionManager connectionManager;
    private MessageReceive messageReceive;
    private CountDownLatch countDownLatch;

    public NettyClientHandler(ConnectionManager connectionManager, MessageReceive messageReceive, CountDownLatch countDownLatch) {
        this.connectionManager = connectionManager;
        this.messageReceive = messageReceive;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logs.CONN.info("NettyClient connected conn=%s", ctx.channel());
        Connection conn = new NettyConnection();
        conn.init(ctx.channel());
        this.connectionManager.add(conn);
        this.startHeartbeat(conn);
        this.countDownLatch.countDown();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Connection conn = this.connectionManager.removeAndClose(ctx.channel());
        Logs.CONN.info("NettyClient disconnected conn=%s", conn);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Connection conn = this.connectionManager.get(ctx.channel());
        conn.updateLastReadTime();
        if (msg instanceof RequestWrapper) {
            if (CommandId.HEARTBEAT == ((RequestWrapper) msg).getCommandId() ||
                    CommandId.HEARTBEAT_CLIENT == ((RequestWrapper) msg).getCommandId()) {
                return;
            }
        }
        Logs.CONN.debug("NettyClient receive msg:%s conn:%s", msg, conn);
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
        } else if (msg instanceof RequestWrapper) {
            this.messageReceive.onReceive(conn, (RequestWrapper) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Connection conn = this.connectionManager.get(ctx.channel());
        Logs.CONN.error("NettyClient caught, conn=%s", cause, conn);
        ctx.close();
    }

    private void startHeartbeat(Connection conn) {
        TimerHolder.getTimer().newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                if (conn.isConnected() && checkHealth(conn)) {
                    startHeartbeat(conn);
                }
            }
        }, conn.heartbeat(), TimeUnit.MILLISECONDS);
    }

    private boolean checkHealth(Connection conn) {
        if (conn.isReadTimeout()) {
            conn.timeout();
        } else {
            conn.clearTimeout();
        }

        if (conn.getTimeoutTimes() > Connection.TIMEOUT_TIMES) {
            Logs.CONN.warn("NettyClient timeout conn:%s", conn);
            conn.close();
            return false;
        }

        if (conn.isWriteTimeout()) {
            conn.send(CommandId.HEARTBEAT, null);
        }

        return true;
    }
}
