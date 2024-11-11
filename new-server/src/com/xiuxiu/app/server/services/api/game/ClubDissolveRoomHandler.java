package com.xiuxiu.app.server.services.api.game;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.game.ClubDissolveRoom;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.club.ClubManager;
import com.xiuxiu.app.server.club.IClub;
import com.xiuxiu.app.server.room.ERoomDestroyType;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.net.protocol.ErrorMsg;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

/**
 * @auther: yuyunfei
 * @date: 2019/12/30 10:51
 * @comment: 后台解散房间
 */
public class ClubDissolveRoomHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        ClubDissolveRoom info = JsonUtil.fromJson(body, ClubDissolveRoom.class);
        Logs.API.debug("后台解散房间 %s",info);
        String sign = MD5Util.getMD5(info.roomUid, Config.APP_KEY);
        ErrorMsg resp = new ErrorMsg();
        do {
            if (!sign.equalsIgnoreCase(info.sign)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Room room = RoomManager.I.getRoom(info.roomUid);
            if (null == room){
                resp.ret = ErrorCode.ROOM_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.ROOM_NOT_EXISTS.getMsg();
                break;
            }
            IClub club = ClubManager.I.getClubByUid(room.getGroupUid());
            if (null == club){
                resp.ret = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getRet();
                resp.msg = ErrorCode.CLUB_GOLD_OR_CARD_NOT_EXISTS.getMsg();
                break;
            }
            room.setDestroyType(ERoomDestroyType.MANAGER_DESTROY);
            room.setIsDestroyUid(club.getOwnerId());
            room.destroy();
            resp.ret = ErrorCode.OK.getRet();
            resp.msg = ErrorCode.OK.getMsg();
        } while (false);
        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
