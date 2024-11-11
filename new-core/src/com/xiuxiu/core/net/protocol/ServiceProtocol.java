package com.xiuxiu.core.net.protocol;

import com.xiuxiu.core.net.codec.ByteBufferWrapper;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.codec.Encoder;
import com.xiuxiu.core.net.message.CommandId;
import com.xiuxiu.core.net.message.MessageType;
import com.xiuxiu.core.net.message.RequestWrapper;
import com.xiuxiu.core.net.message.ResponseWrapper;

import io.netty.buffer.ByteBuf;

public class ServiceProtocol implements Protocol {
    @Override
    public byte getVersion() {
        return (byte) Version.SERVICE.ordinal();
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
        byteBufferWrapper.writeInt(request.getCommandId());
        byteBufferWrapper.writeInt(request.getRequestId());
        byteBufferWrapper.writeInt(request.getTimeout());
        //byteBufferWrapper.writeLong(request.getGatewaySessionId());
        //byteBufferWrapper.writeByte(request.getGatewayNodeId());
        //byteBufferWrapper.writeLong(request.getUserId());
        Encoder encoder = Codecs.getEncoder(request.getCodecType());
        byte[] body = encoder.encode(request.getBody());
        byteBufferWrapper.writeInt(null == body ? 0 : body.length);
        if (null != body) {
            byteBufferWrapper.writeBytes(body);
        }
    }

    private void encodeResponseWrapper(ResponseWrapper response, ByteBufferWrapper byteBufferWrapper) throws Exception {
        byteBufferWrapper.writeByte(Version.value(response.getProtocolVersion()));
        byteBufferWrapper.writeByte((MessageType.value(response.getMessageType())));
        byteBufferWrapper.writeByte(Codecs.Type.value(response.getCodecType()));
        byteBufferWrapper.writeInt(response.isError() ? CommandId.ERROR : response.getCommandId());
        byteBufferWrapper.writeInt(response.getRequestId());
        byteBufferWrapper.writeInt(0);
        //byteBufferWrapper.writeLong(0);
        //byteBufferWrapper.writeByte((byte) 0);
        //byteBufferWrapper.writeLong(0);
        Encoder encoder = Codecs.getEncoder(response.getCodecType());
        byte[] body = encoder.encode(response.isError() ? response.getCause() : response.getBody());
        byteBufferWrapper.writeInt(null == body ? 0 : body.length);
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
        int commandId = byteBufferWrapper.readInt();
        int requestId = byteBufferWrapper.readInt();
        int timeout = byteBufferWrapper.readInt();
        //long gatewaySessionId = byteBufferWrapper.readLong();
        //byte gatewayNodeId = byteBufferWrapper.readByte();
        //long userId = byteBufferWrapper.readLong();
        int bodyLen = byteBufferWrapper.readInt();

        if (bodyLen > 0 && byteBufferWrapper.readableBytes() < bodyLen) {
            return null;
        }
        byte[] body = new byte[bodyLen];
        if (bodyLen > 0) {
            byteBufferWrapper.readBytes(body);
        }
        MessageType messageType = MessageType.parse(requestType);
        if (MessageType.RESPONSE == messageType) {
            ResponseWrapper responseWrapper = new ResponseWrapper(requestId, Version.SERVICE, Codecs.Type.parse(codecType));
            responseWrapper.setBody(body);
            responseWrapper.setCommandId(commandId);
            return responseWrapper;
        } else {
            RequestWrapper requestWrapper = new RequestWrapper(MessageType.parse(requestType), Version.SERVICE, Codecs.Type.parse(codecType), commandId, body, timeout);
            requestWrapper.setRequestId(requestId);
            //requestWrapper.setGatewaySessionId(gatewaySessionId);
            //requestWrapper.setGatewayNodeId(gatewayNodeId);
            //requestWrapper.setGatewaySessionId(userId);
            return requestWrapper;
        }
    }

    @Override
    public ByteBuf encode(Object message) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }
}
