package com.xiuxiu.core.net.message;

import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.codec.Codecs;
import com.xiuxiu.core.thread.NameThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MessageDispatchExecutor extends MessageDispatch {
    protected ExecutorService executorService;

    public MessageDispatchExecutor() {
        this.executorService = new ThreadPoolExecutor(10, 5000, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), new NameThreadFactory("MessageDispatchExecutor"));
    }

    public MessageDispatchExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void onReceive(Connection conn, RequestWrapper message) {
        this.executorService.submit(new Task(conn, message));
    }

    private class Task implements Runnable {
        private Connection conn;
        private RequestWrapper request;

        public Task(Connection conn, RequestWrapper request) {
            this.conn = conn;
            this.request = request;
        }

        @Override
        public void run() {
            Handler handler = allHandler.get(this.request.getCommandId());
            ResponseWrapper response = new ResponseWrapper(this.request.getRequestId(), this.request.getProtocolVersion(), this.request.getCodecType());
            if (null != handler) {
                try {
                    Object body = Codecs.getDecoder(this.request.getCodecType()).decoder(this.request.getCommandId(), (byte[]) this.request.getBody());
                    response.setBody(handler.handler(conn, body));
                    if (null != response.getBody()) {
                        response.setCommandId(Codecs.getCommandId(response.getCodecType(), response.getBody().getClass().getSimpleName()));
                    }
                } catch (Exception e) {
                    response.setCause(e);
                }
            }
            if (MessageType.REQUEST == this.request.getMessageType()) {
                if (null != response) {
                    conn.send(response);
                }
            }
        }
    }
}
