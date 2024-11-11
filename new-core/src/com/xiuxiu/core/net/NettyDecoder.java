package com.xiuxiu.core.net;

import com.xiuxiu.core.net.codec.NettyByteBufferWrapper;
import com.xiuxiu.core.net.protocol.Protocol;
import com.xiuxiu.core.net.protocol.ProtocolException;
import com.xiuxiu.core.net.protocol.ProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
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
            list.add(msg);
        }
    }
}
