package com.xiuxiu.app.server.services.api.game;

import com.sun.net.httpserver.HttpExchange;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.api.SetHundredWinInfo;
import com.xiuxiu.app.protocol.api.SetHundredWinInfoResp;
import com.xiuxiu.app.server.Config;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.api.ApiManager;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.handle.impl.hundred.IHundredHandle;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.app.server.services.api.old.BaseHttpHandler;
import com.xiuxiu.core.net.HttpServer;
import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.JsonUtil;
import com.xiuxiu.core.utils.MD5Util;

import java.io.IOException;

public class SetHundredWinHandler extends BaseHttpHandler {
    @Override
    protected void doHandler(HttpExchange httpExchange) throws IOException {
        String body = new String(HttpServer.readBody(httpExchange), Charsetutil.UTF8);
        SetHundredWinInfo info = JsonUtil.fromJson(body, SetHundredWinInfo.class);
        String sign = MD5Util.getMD5(info.clubUid, info.roomId, info.win, Config.APP_KEY);
        Logs.API.debug("set hundred win:%s", info);
        SetHundredWinInfoResp resp = new SetHundredWinInfoResp();
        do {
//            if (!ApiManager.I.isVerifyToken(info.token)) {
//                resp.setRet(ErrorCode.REQUEST_INVALID_TOKEN);
//                break;
//            }
            if (!sign.equalsIgnoreCase(info.token)) {
                resp.ret = ErrorCode.REQUEST_INVALID_DATA.getRet();
                resp.msg = ErrorCode.REQUEST_INVALID_DATA.getMsg();
                break;
            }
            Room room = RoomManager.I.getRoom(info.roomId);
            if (null == room) {
                resp.setRet(ErrorCode.ROOM_NOT_EXISTS);
                break;
            }
            IHundredHandle hundredHandle = (IHundredHandle) room.getRoomHandle();
            hundredHandle.setWinIndex(info.win);
            //ApiManager.I.updateOpTime();
        } while (false);

        HttpServer.sendOk(httpExchange, JsonUtil.toJson(resp).getBytes(Charsetutil.UTF8));
        httpExchange.close();
    }
}
