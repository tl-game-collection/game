package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfGetNoNeedPayPassword;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class PlayerGetNoNeedPayPasswordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        if (!PlayerManager.I.lock(player.getUid())) {
            Logs.PLAYER.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            Account account = AccountManager.I.getAccountByUid(player.getUid());
            if (null == account) {
                Logs.PLAYER.warn("accountUid:%d 不存在", player.getUid());
                player.send(CommandId.CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.ACCOUNT_NOT_EXISTS);
                return null;
            }
            PCLIPlayerNtfGetNoNeedPayPassword resp = new PCLIPlayerNtfGetNoNeedPayPassword();
            resp.noNeedPayPassword = account.getNoNeedPayPassword();
            player.send(CommandId.CLI_NTF_PLAYER_GET_NO_NEED_PAY_PASSWORD_STATE_OK, resp);
            return null;
        } finally {
            PlayerManager.I.unlock(player.getUid());
        }
    }
}
