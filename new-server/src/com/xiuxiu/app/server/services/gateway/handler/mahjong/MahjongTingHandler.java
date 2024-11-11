package com.xiuxiu.app.server.services.gateway.handler.mahjong;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongReqTingInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.MahjongRoom;
import com.xiuxiu.app.server.room.normal.mahjong2.yymj.YYMJMahjongRoom;
import com.xiuxiu.core.net.message.Handler;

/**
 * 麻将听牌处理
 */
public class MahjongTingHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player)owner;
        PCLIMahjongReqTingInfo info = (PCLIMahjongReqTingInfo)request;
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_MAHJONG_TING_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode err = ErrorCode.DISCARD_PROTOCOL;
        if (room instanceof MahjongRoom) {
            err = ((MahjongRoom)room).ting(player, info.cardValue, info.isLast, info.index, info.outputCardIndex,
                info.length, info.ting,info.desktingIndex);
        }
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_MAHJONG_TING_FAIL, err);
        }
        return null;
    }
}