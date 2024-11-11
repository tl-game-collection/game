package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.GetPlayerInfo;
import com.xiuxiu.app.protocol.api.temp.player.GetPlayerInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.account.Account;
import com.xiuxiu.app.server.account.AccountManager;
import com.xiuxiu.app.server.constant.EMoneyType;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class GetPlayerListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetPlayerInfo info = JsonUtil.fromJson(body, GetPlayerInfo.class);
        Logs.API.debug("获取玩家列表:%s", info);
        String sign = MD5Util.getMD5(info.playerUid, info.referrerUid, info.page, info.pageSize, Config.APP_KEY);
        GetPlayerInfoResp resp = new GetPlayerInfoResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            List<Player> list = new ArrayList<>();
            if (info.playerUid > 0 && info.referrerUid <= 0){
                Player player = PlayerManager.I.getPlayer(info.playerUid);
                if (null != player){
                    list.add(player);
                }
            }else if (info.playerUid <= 0 && info.referrerUid <= 0){
                resp.count = DBManager.I.getPlayerDao().loadAllUid().size();
                list = DBManager.I.getPlayerDao().loadAllPlayer(info.page * info.pageSize, info.pageSize);
            }else if (info.playerUid <= 0){
                List<Long> rUid = DBManager.I.getRecommendDao().loadByReferrerUid(info.referrerUid, info.page * info.pageSize, info.pageSize);
                for (Long uid : rUid){
                    Player player = PlayerManager.I.getPlayer(uid);
                    if (null != player){
                        list.add(player);
                    }
                }
            }
            for (Player player : list){
                GetPlayerInfoResp.PlayerInfo pInfo = new GetPlayerInfoResp.PlayerInfo();
                Account account = AccountManager.I.getAccountByUid(player.getUid());
                if (null == account){
                    continue;
                }
                pInfo.uid = player.getUid();
                pInfo.name = player.getName();
                pInfo.diamond = player.getMoneyByType(EMoneyType.DIAMOND);
                pInfo.referrerUid = player.getRecommendInfo().getRecommendPlayerUid();
                pInfo.lastLoginTime = player.getLastLoginTime();
                pInfo.lastLoginIp = player.getLastLoginIp();
                pInfo.type = account.getType();
                pInfo.enrollTime = account.getCreateTime();
                pInfo.phone = account.getPhone();
                pInfo.state = account.getState();
                pInfo.sign = MD5Util.getMD5(pInfo.uid, pInfo.name, pInfo.diamond, pInfo.referrerUid, pInfo.lastLoginTime, pInfo.lastLoginIp, pInfo.type, pInfo.enrollTime, pInfo.phone, pInfo.state, Config.APP_KEY);
                resp.list.add(pInfo);
            }
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}

