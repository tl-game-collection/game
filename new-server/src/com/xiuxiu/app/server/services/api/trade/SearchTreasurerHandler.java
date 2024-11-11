package com.xiuxiu.app.server.services.api.trade;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.trade.SearchTreasurerInfo;
import com.xiuxiu.app.protocol.api.temp.trade.SearchTreasurerResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchTreasurerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.warn("收到查询营商请求");

        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        SearchTreasurerInfo info = JsonUtil.fromJson(body, SearchTreasurerInfo.class);

        //check something
        String sign = MD5Util.getMD5(info.clubUid, info.playerUid, Config.APP_KEY);

        SearchTreasurerResp resp = new SearchTreasurerResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }

            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            Player treasurerPlayer = PlayerManager.I.getPlayer(info.playerUid);
            if (treasurerPlayer == null && club == null) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }

            List<SearchTreasurerResp.TreasurerPlayerInfo> upGoldTreasurerPlayers = new ArrayList<>(); //上分财务
            if (club != null) {
                if (club.checkIsJoinInMainClub() && !club.checkIsMainClub()) {
                    resp.ret = ErrorCode.CLUB_NOT_MAIN_CLUB.getRet();
                    resp.msg = ErrorCode.CLUB_NOT_MAIN_CLUB.getMsg();
                    break;
                }

                for (Long tempPlayerUid : club.getClubInfo().getUpGoldTreasurer()) {
                    Player tempPlayer = PlayerManager.I.getPlayer(tempPlayerUid);
                    if (tempPlayer == null) {
                        continue;
                    }
                    if (treasurerPlayer != null && tempPlayer.getUid() != treasurerPlayer.getUid()){
                        continue;
                    }
                    upGoldTreasurerPlayers.add(newTreasurerPlayer(tempPlayer,club));
                }
            }else {
                if (treasurerPlayer != null){
                    for (Long clubUid : treasurerPlayer.getAllClubUids()){
                        IClub tempClub = ClubManager.I.getClubByUid(clubUid);
                        if (tempClub == null){
                            continue;
                        }

                        if (!tempClub.checkIsUpTreasurer(treasurerPlayer.getUid())){
                            continue;
                        }
                        upGoldTreasurerPlayers.add(newTreasurerPlayer(treasurerPlayer,tempClub));
                    }
                }
            }

            resp.players = upGoldTreasurerPlayers;
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private SearchTreasurerResp.TreasurerPlayerInfo newTreasurerPlayer(Player treasurerPlayer,IClub club){
        SearchTreasurerResp.TreasurerPlayerInfo tempInfo =new SearchTreasurerResp.TreasurerPlayerInfo(treasurerPlayer.getUid(),treasurerPlayer.getName(),club.getGold(treasurerPlayer.getUid()));
        ClubMember clubMember = club.getMember(tempInfo.playerUid);
        tempInfo.desc = clubMember == null ? ":" : clubMember.getTreasurerDesc();
        tempInfo.registerTime = clubMember == null ? System.currentTimeMillis() : clubMember.getJoinTime();
        tempInfo.clubUid = club.getClubUid();
        tempInfo.clubName = club.getName();
        return tempInfo;
    }
}
