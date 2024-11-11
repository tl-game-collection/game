package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerNtfIsSetPayPassWordInfo;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerIsSetPayPassWordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;

        Account account = AccountManager.I.getAccountByUid(player.getUid());

        PCLIPlayerNtfIsSetPayPassWordInfo resp = new PCLIPlayerNtfIsSetPayPassWordInfo();
        resp.isSet = StringUtil.isEmptyOrNull(account.getPayPassword()) ? 0 : 1;

        player.send(CommandId.CLI_NTF_PLAYER_IS_SET_PAY_PASSWORD_OK, resp);
        return null;
    }
}
