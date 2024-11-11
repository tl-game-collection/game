package com.xiuxiu.app.server.services.api.club;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.temp.club.ClubModifyMemberGoldReq;
import com.xiuxiu.app.protocol.api.temp.club.ClubModifyMemberGoldResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.box.IBoxOwner;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.ClubMember;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.club.constant.EClubGoldChangeType;
import com.xiuxiu.app.server.club.constant.EClubType;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.player.PlayerManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

public class ClubModifyMemberGoldHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ClubModifyMemberGoldReq info = JsonUtil.fromJson(body, ClubModifyMemberGoldReq.class);

        Logs.API.warn("修改群玩家竞技分:%s", info);

        //check something
        ClubModifyMemberGoldResp resp = new ClubModifyMemberGoldResp();

        String sign = MD5Util.getMD5(info.clubUid,info.playerUid,info.gold,info.type,Config.APP_KEY);
        if (null == sign || !sign.equalsIgnoreCase(info.sign)) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        if (0 >= info.clubUid || 0 >= info.playerUid || 0 == info.gold || (info.type != 0 && info.type != 1)){
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        IClub club = ClubManager.I.getClubByUid(info.clubUid);
        if (club == null || club.getClubType() != EClubType.GOLD) {
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        if (!club.hasMember(info.playerUid)){
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        ClubMember clubMember = club.getMember(info.playerUid);
        if (null == clubMember){
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        Player player = PlayerManager.I.getPlayer(info.playerUid);
        if (null == player){
            resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
            resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
            sendFailToClient(httpExchange,resp);
            return;
        }

        if (0 > info.gold && player.getRoomId() > 0){
            Room room = RoomManager.I.getRoom(player.getRoomId());

            if (room != null){
                IBoxOwner boxOwner = room.getBoxOwner();
                if (boxOwner != null && boxOwner instanceof IClub
                        && ((IClub) boxOwner).getEnterFromClubUid(player.getUid()) == info.clubUid) {
                    resp.ret = ErrorCode.ARENA_REPORT_NOT_CLEAR_IN_GAME.getRet();
                    resp.msg = "无法下分， 玩家正在房间中";
                    sendFailToClient(httpExchange, resp);
                    return;
                }
            }
        }

        //deal
        EClubGoldChangeType changeType = info.type == 0 ? EClubGoldChangeType.BACK_GROUND_CHANGE : EClubGoldChangeType.BACK_GROUND_RECHARGE;
        int addGold = info.gold;
        while (!club.addMemberClubGold(info.playerUid,addGold,0,changeType)){
            if (addGold > 0){
                addGold = 0;
            }else{
                int tempValue = (int)club.getGold(info.playerUid);
                addGold = tempValue > Math.abs(addGold) ? addGold : -tempValue;
            }
        }
        
        club.getMemberExt(info.playerUid,true).save();

        resp.changGold = addGold;
        resp.ret = ErrorCode.OK.getRet();
        resp.msg = ErrorCode.OK.getMsg();
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }

    private void sendFailToClient(HttpExchange httpExchange, ClubModifyMemberGoldResp resp) throws IOException {
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
