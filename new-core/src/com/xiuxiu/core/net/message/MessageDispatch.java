package com.xiuxiu.core.net.message;

import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.Process;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.net.protocol.ProtocolException;
import com.xiuxiu.core.proxy.ServerNodeType;

import java.util.HashMap;

public class MessageDispatch implements MessageReceive {
    protected HashMap<Integer, Handler> allHandler = new HashMap<>();
    protected HashMap<Integer, ServerNodeType> forward = new HashMap<>();
    protected HashMap<Integer, Process> allHandlerProcess = new HashMap<>();

    public MessageDispatch() {

    }

    public void register(int commandId, Handler handler, Process process) {
        this.allHandler.putIfAbsent(commandId, handler);
        this.allHandlerProcess.putIfAbsent(commandId, process);
    }

    public void registerForward(int commandId, ServerNodeType type) {
        this.forward.putIfAbsent(commandId, type);
    }

    public Handler getHandler(int commandId) {
        return this.allHandler.get(commandId);
    }

    public Process getProcess(int commandId) {
        return this.allHandlerProcess.get(commandId);
    }

    @Override
    public void onReceive(Connection conn, RequestWrapper message) {
        Handler handler = this.allHandler.get(message.getCommandId());
        ResponseWrapper response = new ResponseWrapper(message.getRequestId(), message.getProtocolVersion(), message.getCodecType());
        if (null != handler) {
            try {
                Object body = Codecs.getDecoder(message.getCodecType()).decoder(message.getCommandId(), (byte[]) message.getBody());
                response.setBody(handler.handler(conn, body));
                if (null != response.getBody()) {
                    response.setCommandId(Codecs.getCommandId(response.getCodecType(), response.getBody().getClass().getSimpleName()));
                }
            } catch (Exception e) {
                response.setCause(e);
            }
        } else {
            response.setCause(new ProtocolException("CommandId:" + message.getCommandId() + " not have handler"));
        }
        if (MessageType.REQUEST == message.getMessageType()) {
            if (null != response) {
                conn.send(response);
            }
        }
    }
}
