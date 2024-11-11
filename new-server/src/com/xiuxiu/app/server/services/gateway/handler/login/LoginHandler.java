package com.xiuxiu.app.server.services.gateway.handler.login;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.PCLILoginInfo;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.Connection;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.net.protocol.ErrorMsg;

public class LoginHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Connection conn = (Connection) owner;
        PCLILoginInfo info = (PCLILoginInfo) request;
        ErrorCode err = PlayerManager.I.login(conn, info.uid);
        if (ErrorCode.OK == err) {
            conn.send(CommandId.CLI_NTF_LOGIN_OK, null);
        } else {
            ErrorMsg errorMsg = new ErrorMsg(err);
            conn.send(CommandId.CLI_NTF_LOGIN_FAIL, errorMsg);
        }
        return null;
    }
}
