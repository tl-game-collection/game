package com.xiuxiu.app.server.services.gateway.handler.mahjong;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongReqSelectPiaoInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong.MahjongRoom;
import com.xiuxiu.app.server.room.normal.poker.IPokerXuanPiao;
import com.xiuxiu.core.net.message.Handler;

public class MahjongSelectPiaoHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIMahjongReqSelectPiaoInfo info = (PCLIMahjongReqSelectPiaoInfo) request;
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode err = ErrorCode.DISCARD_PROTOCOL;
        if (room instanceof IMahjongRoom) {
            err = ((MahjongRoom) room).xuanPiao(player, info.value);
        } else if(room instanceof IPokerXuanPiao) {
            err = ((IPokerXuanPiao) room).xuanPiao(player, info.value);
        }
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_MAHJONG_SELECT_PIAO_FAIL, err);
        }
        return null;
    }
}
