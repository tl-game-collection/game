package com.xiuxiu.app.server.services.api.old;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.WeChatInfoResp;
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
import com.xiuxiu.core.utils.StringUtil;

/**
 * 根据微信账号id获取游戏用户名、playerId、现有房卡数量 如果不存在，则创建游戏账号；否则直接返回相应数据
 * 
 * @author Administrator
 *
 */
public class WeChatInfoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            WeChatInfoResp resp = new WeChatInfoResp();
            String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
            Map<String, Object> userInfo = JsonUtil.fromJson(body, Map.class);
            if (null == userInfo) {
                Logs.API.warn("WeChatInfoHandler微信验证, 获取微信用户失败1");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
                return;
            }
            Object tempUid = userInfo.get("uid");
            if (null == tempUid) {
                Logs.API.warn("WeChatInfoHandler微信验证, 获取微信用户失败11");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
                return;
            }
            String uid = tempUid.toString();
            Object tempNick = userInfo.get("nick");
            if (null == tempNick) {
                Logs.API.warn("WeChatInfoHandler微信验证, 获取微信用户失败111");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
                return;
            }
            String nick = tempNick.toString();
            if (StringUtil.isEmptyOrNull(uid) || StringUtil.isEmptyOrNull(nick)) {
                Logs.API.warn("WeChatInfoHandler微信验证, 获取微信用户失败2");
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
                return;
            }
            // MD5( uid + nick + sex + icon + country + city + apiKey )
            String sign = MD5Util.getMD5(uid + nick + userInfo.get("sex") + userInfo.get("icon")
                    + userInfo.get("country") + userInfo.get("city"), Config.APP_KEY);
            if (!sign.equalsIgnoreCase(userInfo.get("sign").toString())) {
                Logs.API.error("WeChatInfoHandler数据被串改");
                resp.ret = ErrorCode.REQUEST_INVALID_TOKEN.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_TOKEN.getMsg();
                HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
                return;
            }
            Account account = AccountManager.I.getAccountByOtherPlatformToken(uid);
            if (null == account) {
                account = AccountManager.I.create((byte) 3, "", "", "", "", "", uid, nick,
                        userInfo.get("icon").toString(), Byte.valueOf(userInfo.get("sex").toString()),
                        userInfo.get("city").toString(),0);
            }
            if (null == account) {
                Logs.API.error("WeChatInfoHandler微信验证, 获取微信用户失败4");
                resp.ret = ErrorCode.REQUEST_INVALID.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID.getMsg();
                HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
                return;
            }
            Player player = PlayerManager.I.getPlayer(account.getUid());
            if (null == player) {
                player = PlayerManager.I.createPlayer(account, -1);
                player.init();
            }
            resp.playerId = player.getUid();
            resp.name = player.getName();
            resp.count = player.getMoneyByType(EMoneyType.DIAMOND);
            HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        } catch (Exception e) {
            Logs.API.error("WeChatInfoHandler接口异常", e);
        } finally {
            if (httpExchange != null) {
                httpExchange.close();
            }
        }
    }

}
