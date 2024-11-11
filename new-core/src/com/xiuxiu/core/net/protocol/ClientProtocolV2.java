package com.xiuxiu.core.net.protocol;

import com.xiuxiu.core.net.codec.ByteBufferWrapper;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.codec.Encoder;
import com.xiuxiu.core.net.message.MessageType;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;
import com.xiuxiu.core.utils.CompressUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ClientProtocolV2 implements Protocol {
    @Override
    public byte getVersion() {
        return (byte) Version.CLIENTV2.ordinal();
    }

    @Override
    public ByteBufferWrapper encode(Object message, ByteBufferWrapper byteBufferWrapper) throws Exception {
        if (message instanceof RequestWrapper) {
            this.encodeRequestWrapper((RequestWrapper) message, byteBufferWrapper);
        } else if (message instanceof ResponseWrapper) {
            this.encodeResponseWrapper((ResponseWrapper) message, byteBufferWrapper);
        }
        return byteBufferWrapper;
    }

    private void encodeRequestWrapper(RequestWrapper request, ByteBufferWrapper byteBufferWrapper) throws Exception {
        byteBufferWrapper.writeByte(Version.value(request.getProtocolVersion()));
        byteBufferWrapper.writeByte((MessageType.value(request.getMessageType())));
        byteBufferWrapper.writeByte(Codecs.Type.value(request.getCodecType()));
        byteBufferWrapper.writeIntLE(request.getCommandId());
        byteBufferWrapper.writeIntLE(request.getRequestId());
        byteBufferWrapper.writeIntLE(request.getTimeout());
        Encoder encoder = Codecs.getEncoder(request.getCodecType());
        byte[] body = encoder.encode(request.getBody());
        long flag = 0;//Protocol.FLAG_COMPRESS;
        if (null != body && body.length > 1024) {
            flag |= Protocol.FLAG_COMPRESS;
            body = CompressUtil.gzip(body);
        }
        if (null != body) {
            for (int i = 0, len = body.length; i < len; ++i) {
                body[i] ^= len;
            }
        }
        byteBufferWrapper.writeLongLE(flag);
        byteBufferWrapper.writeIntLE(null == body ? 0 : body.length);
        if (null != body) {
            byteBufferWrapper.writeBytes(body);
        }
    }

    private void encodeResponseWrapper(ResponseWrapper response, ByteBufferWrapper byteBufferWrapper) throws Exception {
        byteBufferWrapper.writeByte(Version.value(response.getProtocolVersion()));
        byteBufferWrapper.writeByte((MessageType.value(response.getMessageType())));
        byteBufferWrapper.writeByte(Codecs.Type.value(response.getCodecType()));
        byteBufferWrapper.writeIntLE(response.getCommandId());
        byteBufferWrapper.writeIntLE(response.getRequestId());
        byteBufferWrapper.writeIntLE(0);
        Encoder encoder = Codecs.getEncoder(response.getCodecType());
        byte[] body = encoder.encode(response.getBody());
        long flag = 0;//Protocol.FLAG_COMPRESS;
        if (null != body && body.length > 1024) {
            flag |= Protocol.FLAG_COMPRESS;
            body = CompressUtil.gzip(body);
        }
        if (null != body) {
            for (int i = 0, len = body.length; i < len; ++i) {
                body[i] ^= len;
            }
        }
        byteBufferWrapper.writeLongLE(flag);
        byteBufferWrapper.writeIntLE(null == body ? 0 : body.length);
        if (null != body) {
            byteBufferWrapper.writeBytes(body);
        }
    }

    @Override
    public Object decode(ByteBufferWrapper byteBufferWrapper) throws Exception {
        if (byteBufferWrapper.readableBytes() < 26) {
            return null;
        }
        byte requestType = byteBufferWrapper.readByte();
        byte codecType = byteBufferWrapper.readByte();
        int commandId = byteBufferWrapper.readIntLE();
        int requestId = byteBufferWrapper.readIntLE();
        int timeout = byteBufferWrapper.readIntLE();
        long flag = byteBufferWrapper.readLongLE();
        int bodyLen = byteBufferWrapper.readIntLE();

        if (bodyLen > 0 && byteBufferWrapper.readableBytes() < bodyLen) {
            return null;
        }
        byte[] body = new byte[bodyLen];
        if (bodyLen > 0) {
            byteBufferWrapper.readBytes(body);
            if (null != body) {
                for (int i = 0, len = body.length; i < len; ++i) {
                    body[i] ^= len;
                }
            }
            if (Protocol.FLAG_COMPRESS == (flag & Protocol.FLAG_COMPRESS)) {
                body = CompressUtil.unGZip(body);
            }
        }
        MessageType messageType = MessageType.parse(requestType);
        if (MessageType.RESPONSE == messageType) {
            ResponseWrapper responseWrapper = new ResponseWrapper(requestId, Version.CLIENTV2, Codecs.Type.parse(codecType));
            responseWrapper.setCommandId(commandId);
            responseWrapper.setBody(body);
            return responseWrapper;
        } else {
            RequestWrapper requestWrapper = new RequestWrapper(MessageType.parse(requestType), Version.CLIENTV2, Codecs.Type.parse(codecType), commandId, body, timeout);
            requestWrapper.setRequestId(requestId);
            return requestWrapper;
        }
    }

    @Override
    public ByteBuf encode(Object message) throws Exception {
        if (message instanceof RequestWrapper) {
            return this.encodeRequestToByteBuf((RequestWrapper) message);
        } else if (message instanceof ResponseWrapper) {
            return this.encodeResponseToByteBuf((ResponseWrapper) message);
        }
        return null;
    }
    
    private ByteBuf encodeRequestToByteBuf(RequestWrapper request) throws Exception {
        Encoder encoder = Codecs.getEncoder(request.getCodecType());
        byte[] body = encoder.encode(request.getBody());
        long flag = 0;//Protocol.FLAG_COMPRESS;
        if (null != body && body.length > 1024) {
            flag |= Protocol.FLAG_COMPRESS;
            body = CompressUtil.gzip(body);
        }
        if (null != body) {
            for (int i = 0, len = body.length; i < len; ++i) {
                body[i] ^= len;
            }
        }
        int size = 27 + (null == body ? 0 : body.length);
        ByteBuf byteBuf = Unpooled.buffer(size);
        
        byteBuf.writeByte(Version.value(request.getProtocolVersion()));
        byteBuf.writeByte((MessageType.value(request.getMessageType())));
        byteBuf.writeByte(Codecs.Type.value(request.getCodecType()));
        byteBuf.writeIntLE(request.getCommandId());
        byteBuf.writeIntLE(request.getRequestId());
        byteBuf.writeIntLE(request.getTimeout());
       
        byteBuf.writeLongLE(flag);
        byteBuf.writeIntLE(null == body ? 0 : body.length);
        if (null != body) {
            byteBuf.writeBytes(body);
        }
        return byteBuf;
    }
    
    private ByteBuf encodeResponseToByteBuf(ResponseWrapper response) throws Exception{
        // 总长度=字节头长度+消息内容长度
        // 字节头长度=协议版本号(1byte)+消息类型(1byte)+编码类型(1byte)+协议号(4byte)+requestId(4byte)+占位(4byte)+压缩标识(8byte)+消息内容长度(4byte)=27bytes
        Encoder encoder = Codecs.getEncoder(response.getCodecType());
        byte[] body = encoder.encode(response.getBody());
        long flag = 0;//Protocol.FLAG_COMPRESS;
        if (null != body && body.length > 1024) {
            flag |= Protocol.FLAG_COMPRESS;
            body = CompressUtil.gzip(body);
        }
        if (null != body) {
            for (int i = 0, len = body.length; i < len; ++i) {
                body[i] ^= len;
            }
        }
        int size = 27 + (null == body ? 0 : body.length);
        ByteBuf byteBuf = Unpooled.buffer(size);
        
        byteBuf.writeByte(Version.value(response.getProtocolVersion()));
        byteBuf.writeByte((MessageType.value(response.getMessageType())));
        byteBuf.writeByte(Codecs.Type.value(response.getCodecType()));
        byteBuf.writeIntLE(response.getCommandId());
        byteBuf.writeIntLE(response.getRequestId());
        byteBuf.writeIntLE(0);
        
        byteBuf.writeLongLE(flag);
        byteBuf.writeIntLE(null == body ? 0 : body.length);
        if (null != body) {
            byteBuf.writeBytes(body);
        }
        return byteBuf;
    }
}
