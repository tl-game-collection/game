package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.GetUserInfo;
import com.xiuxiu.app.protocol.api.temp.player.GetUserInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

public class GetUserInfoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetUserInfo info = JsonUtil.fromJson(body, GetUserInfo.class);
        Logs.API.debug("获取用户信息:%s", info);
        String sign = MD5Util.getMD5(info.uid, Config.APP_KEY);
        GetUserInfoResp resp = new GetUserInfoResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.uid);
            if (null == player) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            Account account = AccountManager.I.getAccountByUid(player.getUid());
            GetUserInfoResp.UserInfo userInfo = new GetUserInfoResp.UserInfo();
            userInfo.uid = player.getUid();
            userInfo.phone = account.getPhone();
            userInfo.avatar = player.getIcon();
            userInfo.bankCard = "";
            userInfo.createAt = player.getCreateTimestamp();
            userInfo.nickName = player.getName();
            userInfo.diamond = player.getMoneyByType(EMoneyType.DIAMOND);
            userInfo.wallet = player.getMoneyByType(EMoneyType.WALLET);
            userInfo.league = player.getMoneyByType(EMoneyType.LEAGUE);
            userInfo.recommendedPlayerUid = player.getRecommendInfo().getRecommendPlayerUid();
            userInfo.sign = MD5Util.getMD5(userInfo.uid, userInfo.nickName, userInfo.avatar, userInfo.phone, userInfo.bankCard, userInfo.diamond, userInfo.wallet, userInfo.league, userInfo.recommendedPlayerUid, userInfo.createAt, Config.APP_KEY);
            resp.data = userInfo;
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
