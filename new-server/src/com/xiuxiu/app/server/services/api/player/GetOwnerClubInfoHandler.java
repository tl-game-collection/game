package com.xiuxiu.app.server.services.api.player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.player.GetOwnerClubInfo;
import com.xiuxiu.app.protocol.api.temp.player.GetOwnerClubInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubInfo;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.db.DBManager;
import com.xiuxiu.app.server.floor.Floor;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 获取自己是圈主的所有圈信息
 */
public class GetOwnerClubInfoHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Logs.API.debug("收到获取自己是圈主的所有圈信息请求");
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        GetOwnerClubInfo info = JsonUtil.fromJson(body, GetOwnerClubInfo.class);
        String sign = MD5Util.getMD5(info.playerUid, Config.APP_KEY);
        GetOwnerClubInfoResp resp = new GetOwnerClubInfoResp();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Player player = PlayerManager.I.getPlayer(info.playerUid);
            if (player == null) {

            }
            for (Long clubUid : player.getAllClubUids()) {
                IClub tempClub = ClubManager.I.getClubByUid(clubUid);
                if (tempClub == null) {
                    continue;
                }
                if (tempClub.getOwnerId() != info.playerUid) {
                    continue;
                }
                ClubInfo clubInfo = tempClub.getClubInfo();
                GetOwnerClubInfoResp.ClubInfo temp = new GetOwnerClubInfoResp.ClubInfo();
                temp.uid = clubInfo.getUid();
                temp.name = clubInfo.getName();
                temp.ownerUid = clubInfo.getOwnerId();
                for (Map.Entry<Long,Floor> entry : tempClub.getFloor().entrySet()) {
                    GetOwnerClubInfoResp.FloorInfo tempFloorInfo = new GetOwnerClubInfoResp.FloorInfo();
                    tempFloorInfo.floorUid = entry.getKey();
                    tempFloorInfo.gameDesk2Min = entry.getValue().getSetRobotDesk2Min();
                    tempFloorInfo.gameDesk2Max = entry.getValue().getSetRobotDesk2Max();
                    tempFloorInfo.randomTime2 = entry.getValue().getRandomTime2();
                    tempFloorInfo.gameDesk3Min = entry.getValue().getSetRobotDesk3Min();
                    tempFloorInfo.gameDesk3Max = entry.getValue().getSetRobotDesk3Max();
                    tempFloorInfo.randomTime3 = entry.getValue().getRandomTime3();

                    temp.floorInfos.add(tempFloorInfo);
                }
                resp.list.add(temp);
            }
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
