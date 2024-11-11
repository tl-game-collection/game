package com.xiuxiu.app.server.services.gateway.handler.chat;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.chat.PCLIChatReqDelMsgInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.chat.MailBox;
import com.xiuxiu.app.server.chat.MailBoxManager;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubPrivilege;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class ChatDelMessageHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIChatReqDelMsgInfo info = (PCLIChatReqDelMsgInfo) request;
        if (!MailBoxManager.I.lock(player.getUid())) {
            Logs.CHAT.warn("%s 正在操作", player);
            player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            MailBox mailBox = MailBoxManager.I.getMailBoxByPlayerAndMessageUid(player.getUid(), info.delMsgUid);
            if (null == mailBox) {
                Logs.CHAT.warn("%s 邮件:%d 不存在, 无法删除", player, info.delMsgUid);
                player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.CHAT_NOT_EXISTS);
                return null;
            }
            if (2 != mailBox.getMessageType()) {
                Logs.CHAT.warn("%s 邮件:%d 暂时只支持群聊天内容删除, 不是群消息无法删除", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.CHAT_NOT_GROUP);
                return null;
            }
            if (0 != mailBox.getState()) {
                Logs.CHAT.warn("%s 邮件:%d 不是正常无法删除, 不是群消息无法删除", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.CHAT_NOT_GROUP);
                return null;
            }
            IClub club = ClubManager.I.getClubByUid(mailBox.getFromGroupUid());
            if (null == club) {
                Logs.CHAT.warn("%s 邮件:%d 群不存在, 不是群消息无法删除", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.CHAT_NOT_GROUP);
                return null;
            }
            if (!club.hasPrivilege(player.getUid(), EClubPrivilege.DEL_CHAT)) {
                Logs.CHAT.warn("%s 邮件:%d 没有删除群消息权限, 不是群消息无法删除", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.CLUB_NOT_CHIEF_NO_PRIVILEGE);
                return null;
            }
            if (0 != mailBox.getState()) {
                Logs.CHAT.warn("%s 邮件:%s 状态不对, 无法删除", player, mailBox);
                player.send(CommandId.CLI_NTF_CHAT_DEL_FAIL, ErrorCode.CHAT_STATE_ERROR);
                return null;
            }
            MailBoxManager.I.del(player.getUid(), mailBox.getMessageUid());
            player.send(CommandId.CLI_NTF_CHAT_DEL_OK, null);
        } finally {
            MailBoxManager.I.unlock(player.getUid());
        }
        return null;
    }
}
