package com.xiuxiu.app.server.services.gateway.handler.mail;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mail.PCLIMailReqReadInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class MailReadHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIMailReqReadInfo info = (PCLIMailReqReadInfo) request;
        if (!MailManager.I.lock(player.getUid())) {
            Logs.MAIL.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_MAIL_READ_FAIL, ErrorCode.PLAYER_BUSY);
        }
        try {
            ErrorCode err = MailManager.I.readMail(player, info.mailUid);
            if (ErrorCode.OK == err) {
                player.send(CommandId.CLI_NTF_MAIL_READ_OK, null);
            } else {
                player.send(CommandId.CLI_NTF_MAIL_READ_FAIL, err);
            }
            return null;
        } finally {
            MailManager.I.unlock(player.getUid());
        }
    }
}
