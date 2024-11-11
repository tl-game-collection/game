package com.xiuxiu.app.server.services.gateway.handler.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.chat.PCLIChatReqRecallMsgInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.TimeUtil;

public class ChatRecallMessageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIChatReqRecallMsgInfo info = (PCLIChatReqRecallMsgInfo) request;
        if (!MailBoxManager.I.lock(player.getUid())) {
            Logs.CHAT.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_CHAT_RECALL_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            MailBox mailBox = MailBoxManager.I.getMailBoxByPlayerAndMessageUid(player.getUid(), info.recallMsgUid);
            if (null == mailBox) {
                Logs.CHAT.warn("%s 邮件:%d 不存在, 无法撤回", player, info.recallMsgUid);
                player.send(CommandId.CLI_NTF_CHAT_RECALL_FAIL, ErrorCode.CHAT_NOT_EXISTS);
                return null;
            }
            if (mailBox.getFromPlayerUid() != player.getUid()) {
                Logs.CHAT.warn("%s 邮件:%s 不是你发送的, 无法撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_RECALL_FAIL, ErrorCode.CHAT_NOT_SELF_SAY);
                return null;
            }
            if (0 != mailBox.getState()) {
                Logs.CHAT.warn("%s 邮件:%s 状态不对, 无法撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_RECALL_FAIL, ErrorCode.CHAT_STATE_ERROR);
                return null;
            }
            long now = System.currentTimeMillis();
            if ((now - mailBox.getSayTime()) > TimeUtil.FIVE_MINUTE_MS) {
                Logs.CHAT.warn("%s 邮件:%s 已经发送超过5分钟, 无法撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_RECALL_FAIL, ErrorCode.CHAT_SAY_LONG);
                return null;
            }
            MailBoxManager.I.recall(player.getUid(), mailBox.getMessageUid());
            player.send(CommandId.CLI_NTF_CHAT_RECALL_OK, null);
        } finally {
            MailBoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
