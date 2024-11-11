package com.xiuxiu.app.server.services.gateway.handler.mahjong;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongNtfSelectInfo;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongReqSelectInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.core.net.message.Handler;

public class MahjongSelectHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIMahjongReqSelectInfo info = (PCLIMahjongReqSelectInfo) request;
        if (null == info) {
            Logs.ROOM.warn("%s 无效请求", player);
            return null;
        }
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            return null;
        }
        PCLIMahjongNtfSelectInfo selectInfo = new PCLIMahjongNtfSelectInfo();
        selectInfo.index = info.index;
        selectInfo.uid = player.getUid();
        room.broadcast2Client(CommandId.CLI_NTF_MAHJONG_SELECT, selectInfo);
        return null;
    }
}
