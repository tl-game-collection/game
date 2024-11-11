package com.xiuxiu.core.net.websocket;

import com.xiuxiu.core.net.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public interface WebSocketConnection extends Connection {
    ChannelFuture send(WebSocketFrame msg);
    Object decode(ByteBuf byteBuf) throws Exception;
}
