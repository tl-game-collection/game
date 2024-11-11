package com.xiuxiu.core.net.message;


import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.protocol.Protocol;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestWrapper implements RemoteCommand {
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(1);

    private int requestId;
    private MessageType messageType = MessageType.REQUEST;
    private Protocol.Version protocolVersion;
    private Codecs.Type codecType = Codecs.Type.JSON;
    private int commandId;
    private Object body;
    private int timeout = 3000;

    private long gatewaySessionId;
    private byte gatewayNodeId;
    private long userId;

    public RequestWrapper(Protocol.Version protocolVersion, int commandId, Object message) {
        this.requestId = REQUEST_ID.getAndIncrement();
        this.protocolVersion = protocolVersion;
        this.commandId = commandId;
        this.body = message;
    }

    public RequestWrapper(Protocol.Version protocolVersion, int commandId, Object message, int timeout) {
        this.requestId = REQUEST_ID.getAndIncrement();
        this.protocolVersion = protocolVersion;
        this.commandId = commandId;
        this.body = message;
        this.timeout = timeout;
    }

    public RequestWrapper(Protocol.Version protocolVersion, Codecs.Type codecType, int commandId, Object message) {
        this.requestId = REQUEST_ID.getAndIncrement();
        this.protocolVersion = protocolVersion;
        this.codecType = codecType;
        this.commandId = commandId;
        this.body = message;
    }

    public RequestWrapper(Protocol.Version protocolVersion, Codecs.Type codecType, int commandId, Object message, int timeout) {
        this.requestId = REQUEST_ID.getAndIncrement();
        this.protocolVersion = protocolVersion;
        this.codecType = codecType;
        this.commandId = commandId;
        this.body = message;
        this.timeout = timeout;
    }

    public RequestWrapper(MessageType messageType, Protocol.Version protocolVersion, int commandId, Object message) {
        this.messageType = messageType;
        this.protocolVersion = protocolVersion;
        this.commandId = commandId;
        this.body = message;
    }

    public RequestWrapper(MessageType messageType, Protocol.Version protocolVersion, int commandId, Object message, int timeout) {
        this.messageType = messageType;
        this.protocolVersion = protocolVersion;
        this.commandId = commandId;
        this.body = message;
        this.timeout = timeout;
    }

    public RequestWrapper(MessageType messageType, Protocol.Version protocolVersion, Codecs.Type codecType, int commandId, Object message) {
        this.messageType = messageType;
        this.protocolVersion = protocolVersion;
        this.codecType = codecType;
        this.commandId = commandId;
        this.body = message;
    }

    public RequestWrapper(MessageType messageType, Protocol.Version protocolVersion, Codecs.Type codecType, int commandId, Object message, int timeout) {
        this.messageType = messageType;
        this.protocolVersion = protocolVersion;
        this.codecType = codecType;
        this.commandId = commandId;
        this.body = message;
        this.timeout = timeout;
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Protocol.Version getProtocolVersion() {
        return protocolVersion;
    }

    public Codecs.Type getCodecType() {
        return codecType;
    }

    public int getCommandId() {
        return commandId;
    }

    public Object getBody() {
        return body;
    }

    public int getTimeout() {
        return timeout;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public long getGatewaySessionId() {
        return gatewaySessionId;
    }

    public void setGatewaySessionId(long gatewaySessionId) {
        this.gatewaySessionId = gatewaySessionId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public byte getGatewayNodeId() {
        return gatewayNodeId;
    }

    public void setGatewayNodeId(byte gatewayNodeId) {
        this.gatewayNodeId = gatewayNodeId;
    }

    @Override
    public String toString() {
        return "RequestWrapper{" +
                "requestId=" + requestId +
                ", messageType=" + messageType +
                ", protocolVersion=" + protocolVersion +
                ", codecType=" + codecType +
                ", commandId=" + Integer.toString(commandId, 16) +
                ", body=" + body +
                ", timeout=" + timeout +
                ", gatewaySessionId=" + gatewaySessionId +
                ", gatewayNodeId=" + gatewayNodeId +
                ", userId=" + userId +
                '}';
    }
}
