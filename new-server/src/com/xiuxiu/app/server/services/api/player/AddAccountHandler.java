package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.AddAccount;
import com.xiuxiu.app.protocol.api.temp.player.AddAccountResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Constant;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.RandomUtil;

import java.io.IOException;
import java.util.regex.Matcher;

/**
 * 新增账号
 */
public class AddAccountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        AddAccount info = JsonUtil.fromJson(body, AddAccount.class);
        Logs.API.debug("新增账号:%s", info);
        String sign = MD5Util.getMD5(info.uid, info.phone, Config.APP_KEY);
        AddAccountResp resp = new AddAccountResp();
        Account account = null;
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            if (info.uid > 9999999 || info.uid < 1000000) {
                resp.ret = ErrorCode.API_UID_INVALID.getRet();
                resp.msg = ErrorCode.API_UID_INVALID.getMsg();
                break;
            }
            account = AccountManager.I.getAccountByUid(info.uid);
            if (null != account) {
                resp.ret = ErrorCode.ACCOUNT_ALREADY_EXISTS.getRet();
                resp.msg = ErrorCode.ACCOUNT_ALREADY_EXISTS.getMsg();
                break;
            }
            account = AccountManager.I.getAccountByPhone(info.phone);
            if (null != account) {
                resp.ret = ErrorCode.PHONE_ALREADY_USED.getRet();
                resp.msg = ErrorCode.PHONE_ALREADY_USED.getMsg();
                break;
            }
            if (info.phone.length() != 11) {
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                break;
            }
            Matcher matcher = Constant.PATTERN_PHONE.matcher(info.phone);
            if (!matcher.matches()) {
                resp.ret = ErrorCode.OLD_PHONE_INVALID.getRet();
                resp.msg = ErrorCode.OLD_PHONE_INVALID.getMsg();
                break;
            }
            int passwd = RandomUtil.random(100000, 999999);
            int name = RandomUtil.random(0, 1000);
            account = AccountManager.I.create((byte) 2, info.phone, MD5Util.getMD5(String.valueOf(passwd)), "", "", "", "", String.valueOf(name), "", (byte) 1, "", info.uid);
            if (null == account) {
                resp.ret = ErrorCode.PHONE_ALREADY_USED.getRet();
                resp.msg = ErrorCode.PHONE_ALREADY_USED.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.uid);
            if (null == player) {
                player = PlayerManager.I.createPlayer(account, -1);
                player.init();
                player.save();
            }
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
            Logs.LOGIN.debug("帐号验证成功:%s resp:%s", info, resp);
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
