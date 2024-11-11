package com.xiuxiu.app.server.services.gateway;

import com.xiuxiu.core.log.Logs;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.ConnectionManager;
import com.xiuxiu.core.net.SessionContextFactory;
import com.xiuxiu.core.net.message.MessageReceive;
import com.xiuxiu.core.net.protocol.Protocol;
import com.xiuxiu.core.net.websocket.NettyWebSocketChannelHandler;
import com.xiuxiu.core.net.websocket.NettyWebSocketConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class GatewayServerChannelHandler extends NettyWebSocketChannelHandler {
    public GatewayServerChannelHandler(ConnectionManager connectionManager, MessageReceive messageReceive, SessionContextFactory sessionContextFactory, WebSocketServerHandshakerFactory factory, String path) {
        super(connectionManager, messageReceive, sessionContextFactory, factory, path);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Logs.CONN.info("Netty connected conn=%s", ctx.channel());
        Connection conn = new NettyWebSocketConnection(Protocol.Version.CLIENTV2);
        conn.init(ctx.channel());
        GatewaySessionContext sessionContext = (GatewaySessionContext) this.sessionContextFactory.create();
        conn.setSessionContext(sessionContext);
        this.connectionManager.add(conn);
    }
}