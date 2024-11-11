package com.xiuxiu.app.server.services.gateway.handler.mail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mail.PCLIMailNtfReceiveItemInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.mail.Mail;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class MailQuickReceiveItemHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (!MailManager.I.lock(player.getUid())) {
            Logs.MAIL.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_MAIL_QUICK_GIVE_ITEM_FAIL, ErrorCode.PLAYER_BUSY);
        }
        try {
            List<Mail> receiveList = MailManager.I.quickReceiveMailItem(player);
            PCLIMailNtfReceiveItemInfo receiveItemInfo = new PCLIMailNtfReceiveItemInfo();
            Iterator<Mail> it = receiveList.iterator();
            while (it.hasNext()) {
                Mail mail = it.next();
                for (Map.Entry<Integer, Integer> entry : mail.getItem().entrySet()) {
                    receiveItemInfo.items.put(entry.getKey(), receiveItemInfo.items.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
                receiveItemInfo.receiveMailList.add(mail.getUid());
            }
            player.send(CommandId.CLI_NTF_MAIL_QUICK_GIVE_ITEM_OK, receiveItemInfo);
            return null;
        } finally {
            MailManager.I.unlock(player.getUid());
        }
    }
}
