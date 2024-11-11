package com.xiuxiu.app.server.services.gateway.handler.chat;

import java.util.Iterator;
import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.chat.PCLIChatNtfGetMsgOkInfo;
import com.xiuxiu.app.protocol.client.chat.PCLIChatReqGetMsgInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.LoadMailBoxParam;
import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.chat.MailBoxUid;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ChatGetMessageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIChatReqGetMsgInfo info = (PCLIChatReqGetMsgInfo) request;
        if (!MailBoxManager.I.lock(player.getUid())) {
            Logs.CHAT.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_CHAT_GET_MSG_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            MailBoxUid mailBoxUid = MailBoxManager.I.getLastMsgUid(player.getUid());
            LoadMailBoxParam param = mailBoxUid.getMailBoxOperator(info.lastMsgUid);
            List<MailBox> list = MailBoxManager.I.loadMailBox(player, param);
            PCLIChatNtfGetMsgOkInfo msgOkInfo = new PCLIChatNtfGetMsgOkInfo();
            msgOkInfo.lastMsgUid = mailBoxUid.getLastMsgUid();
            msgOkInfo.opaque = param.getOpaque();
            Iterator<MailBox> it = list.iterator();
            while (it.hasNext()) {
                msgOkInfo.list.add(it.next().to());
            }
            player.send(CommandId.CLI_NTF_CHAT_GET_MSG_OK, msgOkInfo);
        } finally {
            MailBoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
