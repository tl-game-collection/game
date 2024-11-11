package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ChangeAccountPhone;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.*;

import java.io.IOException;

/**
 * @auther: luocheng
 * @date: 2020/1/5 14:04
 */
public class ChangeAccountPhoneHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("修改用户绑定手机号");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ChangeAccountPhone info = JsonUtil.fromJson(body, ChangeAccountPhone.class);
        String sign = MD5Util.getMD5(info.playerUid, info.phoneNumber, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.playerUid);
            if (null == player) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }
            if (info.phoneNumber <= 0) {
                Logs.API.warn("更改绑定手机号失败, 手机号不正确 info:%s", info);
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                break;
            }
            String phoneNum = String.valueOf(info.phoneNumber);
            if (phoneNum == null || phoneNum.length() != 11) {
                Logs.API.warn("更改绑定手机号失败, 手机号不正确 info:%s", info);
                resp.ret = ErrorCode.PHONE_INVALID.getRet();
                resp.msg = ErrorCode.PHONE_INVALID.getMsg();
                break;
            }
            if (null != AccountManager.I.getAccountByPhone(phoneNum)) {
                Logs.API.warn("更改绑定手机号失败, 该手机号已被绑定 info:%s", info);
                resp.ret = ErrorCode.PHONE_ALREADY_USED.getRet();
                resp.msg = ErrorCode.PHONE_ALREADY_USED.getMsg();
                break;
            }
            if (!AccountManager.I.bindPhone(info.playerUid, phoneNum)) {
                Logs.API.warn("更改绑定手机号失败, 保存数据库失败 info:%s", info);
                resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                break;
            }
            if (!AccountManager.I.updatePasswdByUid(info.playerUid,  MD5Util.getMD5(String.valueOf(RandomUtil.random(100000, 999999))))) {
                Logs.LOGIN.warn("重置密码失败, 保存数据库失败info:%s", info);
                resp.ret = ErrorCode.SERVER_DB_ERROR.getRet();
                resp.msg = ErrorCode.SERVER_DB_ERROR.getMsg();
                break;
            }
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
