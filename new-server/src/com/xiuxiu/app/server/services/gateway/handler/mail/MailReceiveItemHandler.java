package com.xiuxiu.app.server.services.gateway.handler.mail;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mail.PCLIMailNtfReceiveItemInfo;
import com.xiuxiu.app.protocol.client.mail.PCLIMailReqReceiveItemInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.mail.Mail;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class MailReceiveItemHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIMailReqReceiveItemInfo info = (PCLIMailReqReceiveItemInfo) request;
        if (!MailManager.I.lock(player.getUid())) {
            Logs.MAIL.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_MAIL_GIVE_ITEM_FAIL, ErrorCode.PLAYER_BUSY);
        }
        try {
            ErrorCode err = MailManager.I.receiveMailItem(player, info.mailUid);
            if (ErrorCode.OK == err) {
                Mail mail = player.getMail(info.mailUid);
                PCLIMailNtfReceiveItemInfo receiveItemInfo = new PCLIMailNtfReceiveItemInfo();
                receiveItemInfo.items.putAll(mail.getItem());
                receiveItemInfo.receiveMailList.add(mail.getUid());
                player.send(CommandId.CLI_NTF_MAIL_GIVE_ITEM_OK, receiveItemInfo);
            } else {
                player.send(CommandId.CLI_NTF_MAIL_GIVE_ITEM_FAIL, err);
            }
            return null;
        } finally {
            MailManager.I.unlock(player.getUid());
        }
    }
}
