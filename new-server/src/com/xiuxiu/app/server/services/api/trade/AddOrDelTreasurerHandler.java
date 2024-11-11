package com.xiuxiu.app.server.services.api.trade;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.trade.AddOrDelTreasurerInfo;
import com.xiuxiu.app.protocol.api.temp.trade.AddOrDelTreasurerResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.order.UpDownGoldTreasurerManager;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;
import com.xiuxiu.core.utils.StringUtil;

import java.io.IOException;


public class AddOrDelTreasurerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.warn("收到修改营商描述请求");

        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        AddOrDelTreasurerInfo info = JsonUtil.fromJson(body, AddOrDelTreasurerInfo.class);

        //check something
        String sign = MD5Util.getMD5(info.clubUid, info.playerUid, Config.APP_KEY);

        AddOrDelTreasurerResp resp = new AddOrDelTreasurerResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }

            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (club == null) {
                resp.ret = ErrorCode.CLUB_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_NOT_EXISTS.getMsg();
                break;
            }

            if (!club.hasMember(info.playerUid)) {
                resp.ret = ErrorCode.CLUB_NOT_HAVE_PLAYER.getRet();
                resp.msg = ErrorCode.CLUB_NOT_HAVE_PLAYER.getMsg();
                break;
            }

            Player treasurerPlayer = PlayerManager.I.getPlayer(info.playerUid);
            if (treasurerPlayer == null) {
                resp.ret = ErrorCode.PLAYER_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.PLAYER_NOT_EXISTS.getMsg();
                break;
            }

            //如果合过圈
            if (club.checkIsJoinInMainClub()) {
                if (!club.checkIsMainClub()) {
                    resp.ret = ErrorCode.CLUB_NOT_MAIN_CLUB.getRet();
                    resp.msg = ErrorCode.CLUB_NOT_MAIN_CLUB.getMsg();
                    break;
                }
            }

            //设置营商
            if (info.isSet) {
                club.getClubInfo().getUpGoldTreasurer().add(info.playerUid);
                club.getClubInfo().getDownGoldTreasurer().add(info.playerUid);
                ClubMember clubMember = club.getMember(info.playerUid);
                if (null != clubMember){
                    String desc = StringUtil.isEmptyOrNull(info.descOne) ? "" : info.descOne;
                    desc += ":";
                    desc += StringUtil.isEmptyOrNull(info.descTwo) ? "" : info.descTwo;
                    clubMember.setTreasurerDesc(desc);
                    clubMember.setDirty(true);
                }
            } else {
                if (club.getClubInfo().getDownGoldTreasurer().contains(info.playerUid)) {
                    UpDownGoldTreasurerManager.I.clearTreasurerAllOrder(info.playerUid, info.clubUid);
                    club.getClubInfo().getDownGoldTreasurer().remove(info.playerUid);
                    club.getClubInfo().getUpGoldTreasurer().remove(info.playerUid);
                }
            }
            club.getClubInfo().setDirty(true);
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
