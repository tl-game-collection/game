package com.xiuxiu.app.server.services.api.club;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.GetClubRelations;
import com.xiuxiu.app.protocol.api.temp.club.GetClubRelationsResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

public class GetClubRelationsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetClubRelations info = JsonUtil.fromJson(body, GetClubRelations.class);
        Logs.API.debug("查询俱乐部列表:%s", info);
        GetClubRelationsResp resp = new GetClubRelationsResp();
        String sign = MD5Util.getMD5(info.clubUid, Config.APP_KEY);
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(info.clubUid);
            if (club == null) {
                resp.ret = ErrorCode.GROUP_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.GROUP_NOT_EXISTS.getMsg();
                break;
            }
            long rootClubUid = club.getFinalClubId();//最上层的club
            if (rootClubUid == 0) {
                resp.ret = ErrorCode.CLUB_NOT_HAVE_MERGE.getRet();
                resp.msg = ErrorCode.CLUB_NOT_HAVE_MERGE.getMsg();
                break;
            }
            IClub rootClub = ClubManager.I.getClubByUid(rootClubUid);
            List<Long> allChildClubs = new ArrayList<>();
            rootClub.fillDepthChildClubUidList(allChildClubs);
            allChildClubs.add(0,rootClubUid);
            for (Long clubUid: allChildClubs) {
                GetClubRelationsResp.clubRelations temp = new GetClubRelationsResp.clubRelations();
                IClub tempClub = ClubManager.I.getClubByUid(clubUid);
                if (null == tempClub) {
                    continue;
                }
                temp.clubUid = tempClub.getClubUid();
                temp.clubName = tempClub.getName();
                temp.parentUid = tempClub.getClubInfo().getParentUid();
                temp.ownerUid = tempClub.getOwnerId();
                temp.ownerName = PlayerManager.I.getPlayer(tempClub.getOwnerId()).getName();
                temp.time = tempClub.getClubInfo().getJoinParentTime();
                temp.totalRewardValue = tempClub.getTotalGoldAndRewardValueNoChild()[0];
                temp.totalGoldValue = tempClub.getTotalGoldAndRewardValueNoChild()[1];
                resp.list.add(temp);
            }
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
