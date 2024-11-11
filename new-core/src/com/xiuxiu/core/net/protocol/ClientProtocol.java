package com.xiuxiu.core.net.protocol;

import com.xiuxiu.core.net.codec.ByteBufferWrapper;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.codec.Encoder;
import com.xiuxiu.core.net.message.MessageType;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;

import io.netty.buffer.ByteBuf;

public class ClientProtocol implements Protocol {
    @Override
    public byte getVersion() {
        return (byte) Version.CLIENT.ordinal();
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
        byteBufferWrapper.writeIntLE(null == body ? 0 : body.length);
        if (null != body) {
            byteBufferWrapper.writeBytes(body);
        }
    }


    @Override
    public Object decode(ByteBufferWrapper byteBufferWrapper) throws Exception {
        if (byteBufferWrapper.readableBytes() < 18) {
            return null;
        }
        byte requestType = byteBufferWrapper.readByte();
        byte codecType = byteBufferWrapper.readByte();
        int commandId = byteBufferWrapper.readIntLE();
        int requestId = byteBufferWrapper.readIntLE();
        int timeout = byteBufferWrapper.readIntLE();
        int bodyLen = byteBufferWrapper.readIntLE();

        if (bodyLen > 0 && byteBufferWrapper.readableBytes() < bodyLen) {
            return null;
        }
        byte[] body = new byte[bodyLen];
        if (bodyLen > 0) {
            byteBufferWrapper.readBytes(body);
        }
        MessageType messageType = MessageType.parse(requestType);
        if (MessageType.RESPONSE == messageType) {
            ResponseWrapper responseWrapper = new ResponseWrapper(requestId, Version.CLIENT, Codecs.Type.parse(codecType));
            responseWrapper.setCommandId(commandId);
            responseWrapper.setBody(body);
            return responseWrapper;
        } else {
            RequestWrapper requestWrapper = new RequestWrapper(MessageType.parse(requestType), Version.CLIENT, Codecs.Type.parse(codecType), commandId, body, timeout);
            requestWrapper.setRequestId(requestId);
            return requestWrapper;
        }
    }

    @Override
    public ByteBuf encode(Object message) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
