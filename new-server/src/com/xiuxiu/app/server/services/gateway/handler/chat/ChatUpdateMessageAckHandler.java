package com.xiuxiu.app.server.services.gateway.handler.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.chat.PCLIChatReqUpdateMsgAckInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ChatUpdateMessageAckHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIChatReqUpdateMsgAckInfo info = (PCLIChatReqUpdateMsgAckInfo) request;
        if (!MailBoxManager.I.lock(player.getUid())) {
            Logs.CHAT.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_CHAT_UPDATE_MSG_ACK_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            MailBoxManager.I.updateMessageAck(player.getUid(), info.messageUid);
            player.send(CommandId.CLI_NTF_CHAT_UPDATE_MSG_ACK_OK, null);
        } finally {
            MailBoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
