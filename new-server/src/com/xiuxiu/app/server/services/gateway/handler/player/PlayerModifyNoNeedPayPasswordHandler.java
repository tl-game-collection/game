package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqModifyNoNeedPayPassword;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.message.Handler;

public class PlayerModifyNoNeedPayPasswordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqModifyNoNeedPayPassword info = (PCLIPlayerReqModifyNoNeedPayPassword) request;
        if (!PlayerManager.I.lock(player.getUid())) {
            Logs.PLAYER.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.PLAYER_BUSY);
            return null;
        }
        try {
            Account account = AccountManager.I.getAccountByUid(player.getUid());
            if (null == account) {
                Logs.PLAYER.warn("accountUid:%d 不存在", player.getUid());
                player.send(CommandId.CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.ACCOUNT_NOT_EXISTS);
                return null;
            }
            if (info.noNeedPayPassword < 0 || info.noNeedPayPassword > 1) {
                Logs.PLAYER.warn("noNeedPayPassword:%d 不在范围0-1", info.noNeedPayPassword);
                player.send(CommandId.CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                return null;
            }
            PCLIPlayerReqModifyNoNeedPayPassword resp = new PCLIPlayerReqModifyNoNeedPayPassword();
            if (info.noNeedPayPassword != account.getNoNeedPayPassword()) {
                boolean res = AccountManager.I.updateNoNeedPayPasswdByUid(account.getUid(), info.noNeedPayPassword);
                if (!res) {
                    player.send(CommandId.CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_FAIL, ErrorCode.ACCOUNT_MODIFY_NO_NEED_PAY_PASSWORD_FAIL);
                    return null;
                }
            }
            resp.noNeedPayPassword = info.noNeedPayPassword;
            player.send(CommandId.CLI_NTF_PLAYER_MODIFY_NO_NEED_PAY_PASSWORD_STATE_OK, resp);
            return null;
        } finally {
            PlayerManager.I.unlock(player.getUid());
        }
    }
}
