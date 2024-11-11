package com.xiuxiu.app.server.services.gateway.handler.mail;

import java.util.List;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mail.PCLIMailNtfDelInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class MailQuickDelHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (!MailManager.I.lock(player.getUid())) {
            Logs.MAIL.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_MAIL_QUICK_DEL_FAIL, ErrorCode.PLAYER_BUSY);
        }
        try {
            List<Long> delMails = MailManager.I.quickDelMail(player);
            PCLIMailNtfDelInfo delInfo = new PCLIMailNtfDelInfo();
            delInfo.delMailUids = delMails;
            player.send(CommandId.CLI_NTF_MAIL_QUICK_DEL_OK, delInfo);
            return null;
        } finally {
            MailManager.I.unlock(player.getUid());
        }
    }
}
