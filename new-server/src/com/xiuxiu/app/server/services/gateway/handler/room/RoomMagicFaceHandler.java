package com.xiuxiu.app.server.services.gateway.handler.room;

import com.xiuxiu.app.protocol.CommandId;
import com.xiuxiu.app.protocol.ErrorCode;
import com.xiuxiu.app.protocol.client.room.PCLIRoomNtfMagicFaceInfo;
import com.xiuxiu.app.protocol.client.room.PCLIRoomReqMagicFaceInfo;
import com.xiuxiu.app.server.Logs;
import com.xiuxiu.app.server.player.Player;
import com.xiuxiu.app.server.room.RoomManager;
import com.xiuxiu.app.server.room.normal.Room;
import com.xiuxiu.core.net.message.Handler;

public class RoomMagicFaceHandler implements Handler {
    @Override
    public Object handler(Object owner, Object request) {
        Player player = (Player) owner;
        PCLIRoomReqMagicFaceInfo info = (PCLIRoomReqMagicFaceInfo) request;
        if (!RoomManager.I.lock(player.getUid())) {
            Logs.ROOM.warn("%s 正在忙", player);
            player.send(CommandId.CLI_NTF_ROOM_MAGIC_FACE_FAIL, ErrorCode.PLAYER_ROOM_NOT_IN);
            return null;
        }
        try {
            if (0 == info.type) {
                // 房间
                Room room = RoomManager.I.getRoom(player.getRoomId());
                if (null == room) {
                    Logs.ROOM.warn("%s 不在房间里", player);
                    player.send(CommandId.CLI_NTF_ROOM_MAGIC_FACE_FAIL, ErrorCode.ROOM_NOT_EXISTS);
                    return null;
                }
                if (null == room.getRoomPlayer(info.toPlayerUid)) {
                    Logs.ROOM.warn("玩家Uid:%d 不在房间中", info.toPlayerUid);
                    player.send(CommandId.CLI_NTF_ROOM_MAGIC_FACE_FAIL, ErrorCode.REQUEST_INVALID_DATA);
                    return null;
                }
                PCLIRoomNtfMagicFaceInfo magicFaceInfo = new PCLIRoomNtfMagicFaceInfo();
                magicFaceInfo.fromPlayerUid = player.getUid();
                magicFaceInfo.toPlayerUid = info.toPlayerUid;
                magicFaceInfo.magicFaceId = info.magicFaceId;
                room.broadcast2Client(CommandId.CLI_NTF_ROOM_MAGIC_FACE, magicFaceInfo);
            }
            player.send(CommandId.CLI_NTF_ROOM_MAGIC_FACE_OK, null);
            return null;
        } finally {
            RoomManager.I.unlock(player.getUid());
        }
    }
}
