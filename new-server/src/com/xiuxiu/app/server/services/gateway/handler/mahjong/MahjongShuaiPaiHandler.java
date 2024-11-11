package com.xiuxiu.app.server.services.gateway.handler.mahjong;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.mahjong.PCLIMahjongReqShuaiPai;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.IRoom;
import com.xiuxiu.app.server.room.normal.mahjong.IMahjongRoom;
import com.xiuxiu.core.net.message.Handler;

public class MahjongShuaiPaiHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIMahjongReqShuaiPai info = (PCLIMahjongReqShuaiPai) request;
        IRoom room = RoomManager.I.getRoom(player.getRoomId());
        if (null == room) {
            Logs.ROOM.warn("%s 不在房间里", player);
            player.send(CommandId.CLI_NTF_MAHJONG_SHUAI_PAI_FAIL, ErrorCode.ROOM_NOT_EXISTS);
            return null;
        }
        ErrorCode err = ErrorCode.DISCARD_PROTOCOL;
        if (room instanceof IMahjongRoom) {
            err = ((IMahjongRoom) room).shuaiPai(player, info.card);
        }
        if (ErrorCode.OK != err) {
            player.send(CommandId.CLI_NTF_MAHJONG_SHUAI_PAI_FAIL, err);
        } else {
            player.send(CommandId.CLI_NTF_MAHJONG_SHUAI_PAI_OK, err);
        }
        return null;
    }
}
