package com.xiuxiu.app.server.services.gateway.handler.mail;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mail.PCLIMailReqDelInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.mail.MailManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class MailDelHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIMailReqDelInfo info = (PCLIMailReqDelInfo) request;
        if (!MailManager.I.lock(player.getUid())) {
            Logs.MAIL.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_MAIL_DEL_FAIL, ErrorCode.PLAYER_BUSY);
        }
        try {
            ErrorCode err = MailManager.I.delMail(player, info.mailUid);
            if (ErrorCode.OK == err) {
                player.send(CommandId.CLI_NTF_MAIL_DEL_OK, null);
            } else {
                player.send(CommandId.CLI_NTF_MAIL_DEL_FAIL, err);
            }
            return null;
        } finally {
            MailManager.I.unlock(player.getUid());
        }
    }
}
