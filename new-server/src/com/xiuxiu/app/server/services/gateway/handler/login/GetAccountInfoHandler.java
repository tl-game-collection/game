package com.xiuxiu.app.server.services.gateway.handler.login;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.login.PLGetAccountInfo;
import com.xiuxiu.app.protocol.login.PLGetAccountRespInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.core.net.message.Handler;

public class GetAccountInfoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PLGetAccountInfo info = (PLGetAccountInfo) request;

        PLGetAccountRespInfo resp = new PLGetAccountRespInfo();
        do {
            if (null == info) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                player.send(CommandId.CLI_NTF_PLAYER_GET_INFO_FAIL, resp);
                break;
            }
            if (0 == info.userUid){
                Logs.LOGIN.warn("%s 账号不能为空 info:%s", player, info);
                resp.ret = ErrorCode.ACCOUNT_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_NOT_EXISTS.getMsg();
                player.send(CommandId.CLI_NTF_PLAYER_GET_INFO_FAIL, resp);
                break;
            }
            Account account = AccountManager.I.getAccountByUid(info.userUid);
            if (null == account) {
                Logs.LOGIN.warn("%s 账号不存在 info:%s", player, info);
                resp.ret = ErrorCode.ACCOUNT_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_NOT_EXISTS.getMsg();
                player.send(CommandId.CLI_NTF_PLAYER_GET_INFO_FAIL, resp);
                break;
            }
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
            resp.userUid = info.userUid;
            resp.phone = account.getPhone();
            Logs.LOGIN.debug("%s 获取账号信息成功:%s resp:%s", player, info, resp);
        } while (false);

        player.send(CommandId.CLI_NTF_PLAYER_GET_INFO_OK, resp);
        return null;
    }
}
