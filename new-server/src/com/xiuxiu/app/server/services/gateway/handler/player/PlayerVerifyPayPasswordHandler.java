package com.xiuxiu.app.server.services.gateway.handler.player;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqVerifyPayPassword;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerVerifyPayPasswordHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqVerifyPayPassword info = (PCLIPlayerReqVerifyPayPassword) request;
        if (StringUtil.isEmptyOrNull(info.payPassword)) {
            Logs.PLAYER.warn("%s payPassword:%s 参数不合法", player, info.payPassword);
            player.send(CommandId.CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_FAIL, ErrorCode.REQUEST_INVALID_DATA);
            return null;
        }
        Account account = AccountManager.I.getAccountByUid(player.getUid());
        if (!info.payPassword.equals(account.getPayPassword())) {
            Logs.PLAYER.warn("%s payPassword:%s 支付密码验证失败", player, info.payPassword);
            player.send(CommandId.CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_FAIL, ErrorCode.ACCOUNT_PAY_PASSWORD_VERIFY_FAIL);
            return null;
        }
        player.send(CommandId.CLI_NTF_PLAYER_VERIFY_PAY_PASSWORD_OK, null);
        return null;
    }
}
