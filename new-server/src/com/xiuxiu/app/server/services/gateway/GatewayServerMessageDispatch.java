package com.xiuxiu.app.server.services.gateway;

import com.xiuxiu.app.server.Logs;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.Process;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.net.message.MessageDispatch;
import com.xiuxiu.core.net.message.RequestWrapper;

public class GatewayServerMessageDispatch extends MessageDispatch {

    @Override
    public void onReceive(Connection conn, RequestWrapper message) {
        Handler handler = this.allHandler.get(message.getCommandId());
        Process process = this.allHandlerProcess.get(message.getCommandId());
        if (null == handler) {
            Logs.CMD.warn("消息CommandId:%s 没有对应处理器", Integer.toString(message.getCommandId(), 16));
            return;
        }
        if (null != process) {
            process.exec(new MessageTask(conn, message, handler));
        } else {
            super.onReceive(conn, message);
        }
    }
}
