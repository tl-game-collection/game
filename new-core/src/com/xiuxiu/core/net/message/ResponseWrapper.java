package com.xiuxiu.core.net.message;

import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.protocol.Protocol;

public class ResponseWrapper implements RemoteCommand {
    private int requestId;
    private MessageType messageType = MessageType.RESPONSE;
    private Protocol.Version protocolVersion;
    private Codecs.Type codecType = Codecs.Type.JSON;
    private int commandId;
    private Object body;
    private boolean error = false;
    private Throwable cause;

    public ResponseWrapper(int requestId, Protocol.Version protocolVersion, Codecs.Type codecType) {
        this.requestId = requestId;
        this.protocolVersion = protocolVersion;
        this.codecType = codecType;
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    public Protocol.Version getProtocolVersion() {
        return protocolVersion;
    }

    public Codecs.Type getCodecType() {
        return codecType;
    }

    public boolean isError() {
        return error;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
        this.error = true;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{" +
                "requestId=" + requestId +
                ", messageType=" + messageType +
                ", protocolVersion=" + protocolVersion +
                ", codecType=" + codecType +
                ", commandId=" + Integer.toString(commandId, 16) +
                ", body=" + body +
                ", error=" + error +
                ", cause=" + cause +
                '}';
    }
}
