package com.xiuxiu.app.server.services.gateway.handler.player;

import java.util.regex.Pattern;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.player.PCLIPlayerReqChangePayPassWord;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;
import com.xiuxiu.core.utils.StringUtil;

public class PlayerChangePayPassWordHandler implements Handler {

    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIPlayerReqChangePayPassWord info = (PCLIPlayerReqChangePayPassWord) request;

        if (StringUtil.isEmptyOrNull(info.newPayPasswd)) {
            Logs.LOGIN.warn("%s 设置支付密码失败, 密码为空 info:%s", player, info);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_FAIL, ErrorCode.ACCOUNT_PASSWD_NULL);
            return null;
        }
        if(!Pattern.matches("^[0-9]{6}$", info.newPayPasswd)){
            Logs.LOGIN.warn("%s 设置支付密码失败, 密码必须是6位数字 info:%s", player, info);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_FAIL, ErrorCode.ACCOUNT_WALLET_PAY_PASSWORD_NUMBER);
            return null;
        }
        Account account = AccountManager.I.getAccountByUid(player.getUid());

        if (!AccountManager.I.updatePayPasswdByUid(account.getUid(), info.newPayPasswd)) {
            Logs.LOGIN.warn("%s 重置密码失败, 保存数据库失败info:%s", player, info);
            player.send(CommandId.CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_FAIL, ErrorCode.SERVER_DB_ERROR);
            return null;
        }

        player.send(CommandId.CLI_NTF_PLAYER_CHANGE_PAY_PASSWORD_OK, null);
        return null;
    }
}
