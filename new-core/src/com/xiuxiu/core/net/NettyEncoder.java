package com.xiuxiu.core.net;

import com.xiuxiu.core.net.codec.NettyByteBufferWrapper;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.net.protocol.Protocol;
import com.xiuxiu.core.net.protocol.ProtocolException;
import com.xiuxiu.core.net.protocol.ProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class NettyEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
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
        NettyByteBufferWrapper nettyByteBufferWrapper = new NettyByteBufferWrapper(out);
        protocol.encode(msg, nettyByteBufferWrapper);
    }
}
