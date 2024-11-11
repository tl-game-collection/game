package com.xiuxiu.app.server.services.gateway.handler.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.chat.PCLIChatReqDelRecallMsgInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubPrivilege;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ChatDelRecallMessageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIChatReqDelRecallMsgInfo info = (PCLIChatReqDelRecallMsgInfo) request;
        if (!MailBoxManager.I.lock(player.getUid())) {
            Logs.CHAT.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            MailBox mailBox = MailBoxManager.I.getMailBoxByPlayerAndMessageUid(player.getUid(), info.delRecallMsgUid);
            if (null == mailBox) {
                Logs.CHAT.warn("%s 邮件:%d 不存在, 无法删除撤回", player, info.delRecallMsgUid);
                player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorCode.CHAT_NOT_EXISTS);
                return null;
            }
            if (2 != mailBox.getMessageType()) {
                Logs.CHAT.warn("%s 邮件:%d 暂时只支持群聊天内容删除, 不是群消息无法删除撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorCode.CHAT_NOT_GROUP);
                return null;
            }
            if (4 != mailBox.getState()) {
                Logs.CHAT.warn("%s 邮件:%d 未被删除无法删除撤回, 不是群消息无法删除撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorCode.CHAT_NOT_GROUP);
                return null;
            }
            IClub club = ClubManager.I.getClubByUid(mailBox.getFromGroupUid());
            if (null == club) {
                Logs.CHAT.warn("%s 邮件:%d 群不存在, 不是群消息无法删除撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorCode.CHAT_NOT_GROUP);
                return null;
            }
            if (!club.hasPrivilege(player.getUid(), EClubPrivilege.DEL_CHAT)) {
                Logs.CHAT.warn("%s 邮件:%d 没有删除群消息权限, 不是群消息无法删除撤回", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
                return null;
            }
            MailBoxManager.I.delRecall(player.getUid(), mailBox.getMessageUid());
            player.send(CommandId.CLI_NTF_CHAT_DEL_RECALL_OK, null);
        } finally {
            MailBoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
